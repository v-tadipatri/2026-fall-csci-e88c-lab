package org.cscie88c.spark

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{col, when}
import org.cscie88c.core.Utils

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.util.Try

/**
 * Example of Spark Job using Movie dataset
 * Intellij Run:
 * In Intellij, set in Build,Exec,Deployment -> BuildTools ->SBT -> VM params : -DLOCAL_MODE=1
 *  see build.sbt
 *  In Run config, set working dir to spark
 *   and set LOCAL_MODE=1 in VMArgs
 *
 * Local Run:
 * export LOCAL_MODE=1
 *  sbt -DLOCAL_MODE=1 spark/run
 *
 * Docker Run:
 *  unset LOCAL_MODE
 *  sbt spark/clean
 *  sbt spark/assembly
 *  cp spark/target/scala-2.13/SparkJob.jar docker/apps/spark
 *  docker exec -it spark-master /bin/bash
 *  /opt/spark-apps/runSparkMovies.sh
 *
 */
case class Basepath(value: String)


object SparkJob {

  def loadSparkCluster: SparkSession = {
    println(s"Loading cluster config...")
    SparkSession.builder()
      .appName("ClusterSparkJob")
      .getOrCreate()
  }

  def loadSparkLocal: SparkSession = {
    println(s"Loading local config...")
    SparkSession.builder()
      .appName("LocalSparkJob")
      .master("local[*]")
      .getOrCreate()
  }

  def getTimestamp(): String = {
    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
    currentDateTime.format(formatter)
  }

  def loadResource(path: String, isLocal: Boolean)(implicit basepath: Basepath) = {
    if (isLocal){
      //working dir must be spark
      new java.io.File(s"../data/movies/${path}").getAbsoluteFile.toURI.toString
    }else {
      s"${basepath.value}/${path}"
    }
  }

  //Get prop for env or vm arg
  def getProp(key: String): Option[String] = {
     sys.props.get(key).orElse(sys.env.get(key))
  }

  /**
   * Example of implicit class, to wrap a DataFrame
   * @param df
   */
  implicit class DebugDF (val df: DataFrame) {
    def showDebug(truncate: Boolean): Unit = showDebug(10000, truncate)
    def showDebug(numRows: Int, truncate: Boolean): Unit = {
      if (checkLocal()) {
        df.show(numRows, truncate)
      }
    }
  }

  def checkLocal(): Boolean = {
    getProp("LOCAL_MODE").getOrElse("0") == "1"
  }


  def main(args: Array[String]): Unit = {

    //Need to pass this as a sys prop in IDE
    val isLocal = checkLocal()
    val spark = if (isLocal) loadSparkLocal else loadSparkCluster
    val dtime = getTimestamp()
    println(s"Current time = ${dtime}")

    import spark.implicits._
    import org.apache.spark.sql.functions._

    val df = Seq("Alice", "Bob", "Carol").toDF("name")
    val greeted = df.map(row => Utils.greet(row.getString(0)))
    greeted.show(false)

    implicit val basepath = Basepath(if (args.length > 0) args(0) else "")
    val outputPath = Try(args(1)).toOption
    //will only work on local paths
    val title_path = loadResource("titles.csv", isLocal)
    //use "file:" and absolute path on spark cluster

    val genre_path = loadResource("genres.csv", isLocal)

    //work with dataframe
    val df_movies = spark.read.option("header", "true").csv(title_path)
    val df_genre_tmp = spark.read.option("header", "true").csv(genre_path)

    println("===Show full movie data, as dataframe")
    df_movies.showDebug(false)

    //example of adding a column
    val isKidFriendly = col("genre_title").equalTo("Family")
    val newcol = when(isKidFriendly, "yes").otherwise("no")
    val df_genre = df_genre_tmp.withColumn("kid_friendly", newcol)
    println("===Show full genre data, as dataframe")
    df_genre.showDebug(false)

    val joined = df_movies.join(df_genre, df_movies("genre")===df_genre("genre_title"))
    println("===Show joined data, as dataframe")
    joined.showDebug(100, false)

    println("===Show distinct genres")
    //df_movies.select("genre").distinct().show()

    df_movies.createOrReplaceTempView("movie_tbl")
    println("===Show counts by genre")
    val query = spark.sql("select genre, count(*) from movie_tbl group by genre")
    query.showDebug(false)

    //or convert to a dataset
    val movie_set = df_movies.as[Movie]
    println("===show full data, as dataset")
    movie_set.show(false)
    println("=== these are superhero movies")
    movie_set.filter(_.isSuperhero)

    println("=== these are avenger movies")
    movie_set
      .filter(_.isYetAnotherAvengersMovie)
      .map(m => {
        val short_desc = m.description.length match {
          case i if i > 50 => m.description.substring(0,50)+"..."
          case _ => m.description
        }
        MovieSnippet("avengers_snippet", m.title, m.year.toInt, short_desc)
      })
      .show(false)

    // ===  new for week 9 ===
    //a movie is a sequel if it contains letters and ends in a number
    val movie_with_sequel = movie_set.withColumn("is_sequel", col("title").rlike("[a-z].*[0-9]$"))
    //movie_with_sequel.select("title", "is_sequel").show(100,false)

    val movie_by_year = movie_with_sequel
      .groupBy("year")
      .agg(
        count("*").as("total_movies"),
        countDistinct("genre").as("genre_count"),
        sum(col("is_sequel").cast("int")).as("sequel_count")
      ).orderBy("year")
    movie_by_year.showDebug(100, false)

    //if output path defined, write movies
    outputPath.foreach( o => {
      println(s"Writing ${movie_set.count()} rows of output to ${o}")
      val outdir = s"${o}/${dtime}"
      println(s"Writing output to ${outdir}")
      new File(outdir).mkdirs()
      movie_by_year.write.mode("overwrite").parquet(outdir)
    })
    spark.stop()
  }
}
