package org.cscie88c.beam.week10lab

import com.spotify.scio._
import com.spotify.scio.values.SCollection
import com.typesafe.scalalogging.LazyLogging

/*
 * From David Troyer 2024
 * run with
 sbt "runMain org.cscie88c.beam.week10.BeamQuartilesApplication \
 --input-file=./docker/beam/data/taxi_tripdata.csv \
 --output-file=/tmp/approx-quartile"
 */
object BeamQuantilesApplication extends LazyLogging {

  def main(cmdLineArgs: Array[String]): Unit = {
    // 1. Create context
    val (sc: ScioContext, args: Args) = ContextAndArgs(cmdLineArgs)
    implicit val scImplicit: ScioContext = sc

    // 2. Read command line arguments
    val inputFile: String = args("input-file")
    val outputFile: String = args("output-file")

    // 3. Run pipeline
    runPipelineDefault(inputFile: String, outputFile: String)
  }

  def runPipelineDefault(inputFile: String, outputFile: String)(implicit sc: ScioContext): Unit = {
    // Read the input file
    val lines: SCollection[String] = sc.textFile(inputFile)

    // Calculate the min, quartiles and max values
    val results =
      lines
        //.filter(_.trim.indexOf("VendorID")!=0)
        .flatMap(MovieData(_))
        .map(_.released_year)
        //.max(Ordering.Int)
        //.min(Ordering.Int)
        //.map(_.toString)
        .quantilesApprox(5)
        .map(_.mkString("\n"))

    // Write the quartiles to an output file
    results.saveAsTextFile(path = outputFile, numShards = 1)

    sc.run().waitUntilFinish()
  }

}
