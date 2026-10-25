package org.cscie88c.spark

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, when}
import org.cscie88c.core.Utils

import java.io.File
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
 *  sbt spark/assembly
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

  def main(args: Array[String]): Unit = {
    //Need to pass this as a sys prop in IDE
    val isLocal = getProp("LOCAL_MODE").getOrElse("0") == "1"
    val spark = if (isLocal) loadSparkLocal else loadSparkCluster

    import spark.implicits._

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
    df_movies.show(false)

    //example of adding a column
    val isKidFriendly = col("genre_title").equalTo("Family")
    val newcol = when(isKidFriendly, "yes").otherwise("no")
    val df_genre = df_genre_tmp.withColumn("kid_friendly", newcol)
    println("===Show full genre data, as dataframe")
    df_genre.show(false)

    val joined = df_movies.join(df_genre, df_movies("genre")===df_genre("genre_title"))
    println("===Show joined data, as dataframe")
    joined.show(100, false)

    println("===Show distinct genres")
    df_movies.select("genre").distinct().show()

    df_movies.createOrReplaceTempView("movie_tbl")
    println("===Show counts by genre")
    val query = spark.sql("select genre, count(*) from movie_tbl group by genre")
    query.show()

    //or convert to a dataset
    val movie_set = df_movies.as[Movie]
    println("===show full data, as dataset")
    movie_set.show(false)
    println("=== these are superhero movies")
    movie_set.filter(_.isSuperhero).show(false)

    println("=== these are avenger movies")
    movie_set
      .filter(_.isYetAnotherAvengersMovie)
      .map(m => {
        val short_desc = m.description.length match {
          case i if i > 50 => m.description.substring(0,50)+"..."
          case _ => m.description
        }
        MovieSnippet(m.title, m.year.toInt, short_desc)
      })
      .show(false)

    //if output path defined, write movies
    outputPath.foreach( o => {
      println(s"Writing output to ${o}")
      movie_set.write.mode("overwrite").parquet(o)
    })
    spark.stop()
  }
}
