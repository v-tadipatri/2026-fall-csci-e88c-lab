package org.cscie88c.kafka
import com.typesafe.scalalogging.LazyLogging

import java.util.Properties
import org.apache.kafka.streams.{KafkaStreams, StreamsBuilder, StreamsConfig}
import org.apache.kafka.streams.kstream.{KStream, KTable, Produced}
import org.apache.kafka.common.serialization.Serdes

import scala.jdk.CollectionConverters._

/**
 * This overrides App, so no need to define main
 */
object WordCountJava extends App with LazyLogging{

  val inputTopic = System.getProperty("input.topic", "input_word")
  val outputTopic = System.getProperty("output.topic", "output_word")
  val bootstrapServers = System.getProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  val props = new Properties()
  props.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount")
  props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
  // Configure default serdes
  props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, classOf[Serdes.StringSerde])
  props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, classOf[Serdes.StringSerde])
  KafkaSharedUtils.checkAws(props,logger)


  val builder = new StreamsBuilder  // Java StreamsBuilder
  val textLines = builder.stream[String, String](inputTopic)

  //uses the Java API, so convert to Java list
  val wordCounts = textLines
    .flatMapValues(_.toLowerCase.split("\\W+").toList.asJava)
    .groupBy((_, word) => word)
    .count()

  wordCounts.toStream.to(outputTopic, Produced.`with`(Serdes.String(), Serdes.Long()))

  val streams = new KafkaStreams(builder.build(), props)
  streams.start()
}
