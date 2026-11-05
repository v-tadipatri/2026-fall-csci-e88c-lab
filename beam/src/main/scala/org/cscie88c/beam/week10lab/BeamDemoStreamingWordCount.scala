package org.cscie88c.beam.week10lab

import com.spotify.scio._
import com.spotify.scio.util.FilenamePolicySupplier
import com.spotify.scio.values.SCollection
import com.typesafe.scalalogging.LazyLogging
import org.joda.time.{DateTimeZone, Duration, Instant}

import java.time.ZoneId
import java.time.ZoneOffset
import org.apache.beam.sdk.transforms.windowing.{BoundedWindow, IntervalWindow, PaneInfo}
import org.apache.beam.sdk.options.StreamingOptions
import org.apache.beam.sdk.transforms.PTransform
import org.apache.beam.sdk.values.{PBegin, PCollection}
import org.apache.beam.sdk.io.TextIO
import org.apache.beam.sdk.transforms.Watch

/*
 * input stream: docker/beam/apps/data-streamer.sh after modifying input and output locations
 * output stream: watch -n1 ls target/streaming-word-count-output
 * run with (note output dir is inside target)
 sbt "runMain org.cscie88c.beam.week10lab.BeamDemoStreamingWordCount \
 --input-file=./target/streaming-word-count-input \
 --num-words=10 \
 --output-dir=./streaming-word-count-output"
 */

object BeamDemoStreamingWordCount extends LazyLogging {

  def main(cmdLineArgs: Array[String]): Unit = {
    // 1. Create context
    val (sc: ScioContext, args: Args) = ContextAndArgs(cmdLineArgs)
    implicit val scImplicit: ScioContext = sc

    // 2. Read command line arguments
    val inputFile: String = args("input-dir")
    val numWords: Int = args("num-words").toInt
    val outputFile: String = args("output-dir")
    val outDirJava = new java.io.File(outputFile)
    //clean up old files
    if (outDirJava.exists()) {
      outDirJava.listFiles().foreach(
        f => f.delete()
      );
    }

    // 3. Run pipeline
    logger.info(s"running top N words program ...")
    runPipeline(inputFile, numWords, outputFile)
    logger.info(s"program complete")

  }

  def runPipeline(inputFile: String, numWords: Int, outputFile: String)(implicit sc: ScioContext): Unit = {
    // 4. Read input data
    val filePattern = s"${inputFile}/*.txt"
    logger.info(s"Reading files with pattern: ${filePattern}")
    val pollInterval = Duration.standardSeconds(5)
    val timeoutWait = Duration.standardSeconds(30)
    val textRead: PTransform[PBegin, PCollection[String]] =
      TextIO
        .read()
        .from(filePattern)
        .watchForNewFiles(pollInterval, Watch.Growth.afterTimeSinceNewOutput(timeoutWait))
    val lines: SCollection[String] = sc.customInput("Read CSV", textRead)

    //For Windows users - if you get errors with colons in paths
    def fmt(i: Instant): String =
      i.toDateTime(DateTimeZone.UTC).toString("yyyyMMdd'T'HH_mm_ss'Z'")

    val policy = FilenamePolicySupplier.filenamePolicySupplierOf(
      windowed = (shard: Int, numShards: Int, window: BoundedWindow, pane: PaneInfo) => {
        val w = window match {
          case iw: IntervalWindow => s"${fmt(iw.start())}-${fmt(iw.end())}"
          case _                  => "global"
        }
        f"$w-pane${pane.getIndex}-$shard%05d-of-$numShards%05d"
      },
      unwindowed = (shard: Int, numShards: Int) =>
        f"-$shard%05d-of-$numShards%05d"
    )

    // 5. Transform and aggregate data
    val result: SCollection[String] = topNWords(numWords, lines)

    // 6. Write results
    // The /ignored indicates that it's a directory and places folder correctly
    result.saveAsTextFile(path = s"${outputFile}/ignored", numShards = 1, filenamePolicySupplier = policy)
    val ec = sc.run()
    ec.waitUntilFinish()
  }

  /*
  def stopWords(input: SCollection[String]): SCollection[String] =
    input.map(identity)
   */

  def topNWords(numWords: Int, lines: SCollection[String]): SCollection[String] = {
    lines
      .withFixedWindows(Duration.standardSeconds(10))
      .flatMap(_.split(" "))
      .filter(_ != "")
      .countByValue             // SCollection[(String, Long)]
      .swap                     // SCollection[(Long, String)]
      .top(numWords)            // SCollection[Iterable[(Long, String)]], iterable of top n
      .map { iter =>
        iter.map {
          //get timestamp in here
          case (count, word) => s"($count, $word)"
        }.mkString("\n")
      }
  }
}
