package org.cscie88c.kafka

import com.typesafe.scalalogging.LazyLogging
import org.apache.kafka.streams.{KafkaStreams, Topology}

import java.time.Duration
import java.util.Properties
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler
import org.apache.kafka.streams.scala._
import org.apache.kafka.streams.scala.kstream._
import com.goyeau.kafka.streams.circe.CirceSerdes
import io.circe.generic.auto._
import org.cscie88c.kafka.model.Transaction

import scala.util.Try


object StreamingApp extends LazyLogging {


  def createStreamsConfig: Properties = {
    val applicationId = System.getProperty(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-streams-app")
    val config = new Properties()
    val bootstrapServers = System.getProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    val clientId = System.getProperty(StreamsConfig.CLIENT_ID_CONFIG, "kafka-streams-client-0")
    config.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, applicationId)
    config.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    config.setProperty(StreamsConfig.CLIENT_ID_CONFIG, clientId)
    config.setProperty(
      StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG,
      classOf[LogAndContinueExceptionHandler].getName
    )
    config.setProperty(
      StreamsConfig.COMMIT_INTERVAL_MS_CONFIG,
      10000.toString
    )
    KafkaSharedUtils.checkAws(config,logger)
    config
  }

  /**
    * run the streaming application:
      kafka/target/universal/stage/bin/kafka \
        -Dinput.topic=transactions-input-topic \
        -Doutput.topic=transactions-output-topic
    */  
  def main(args: Array[String]): Unit = {
    // read input and output topics
    val inputTopic = System.getProperty("input.topic", "input_txn")
    val outputTopic = System.getProperty("output.topic", "output_txn")
    logger.info(s"now using input topic: $inputTopic")
    logger.info(s"now using output topic: $outputTopic")
    val props = createStreamsConfig
    logger.info(s"now using bootstrap servers: ${props.getProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG)}")
    runStreamingApp(inputTopic, outputTopic, props)
  }

  def runStreamingApp(
    inputTopic: String,
    outputTopic: String,
    props: Properties
  ): Unit = {

    // create serdes for Transaction using circe and implicits
    import Serdes._
    import org.apache.kafka.streams.scala.ImplicitConversions._

    // create builder and stream
    val builder: StreamsBuilder = new StreamsBuilder()
    val transactions: KStream[String, String] = 
      builder.stream[String, String](inputTopic)

    // process the stream (example: increase amount by 10%)
    val processedTransactions: KStream[String, Transaction] = 
      // first, convert each CSV row to Transaction case class
      transactions.mapValues { transaction =>
        Try(Transaction(transaction)).toOption
      }
      // filter out any failed conversions
      .filter {(_, transactionOpt) => transactionOpt.isDefined  && transactionOpt.get.isDefined}
      .mapValues { transactionOpt => transactionOpt.get.get }
      // increase amount by 200% and shorten number
      .mapValues { transaction =>
        transaction.copy(
          amount = transaction.amount * 200,
          number = "T"+transaction.number.substring(0,4)+"..."
        )
      }
      // log each processed transaction
      .peek((key, transaction) => 
        logger.info(s"processed transaction: key=$key, transaction=$transaction")
      )

    // write the processed stream to output topic
    processedTransactions
      .mapValues( transaction => transaction.toString )
      .to(outputTopic)

    // build topology and start streams application
    val topology: Topology = builder.build()
    logger.info(s"topology description: ${topology.describe()}")
    val streams: KafkaStreams = new KafkaStreams(topology, props)

    // add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
    sys.ShutdownHookThread {
      streams.close(Duration.ofSeconds(10))
    }

    // start the streams application
    streams.start()
  } 
  
}
