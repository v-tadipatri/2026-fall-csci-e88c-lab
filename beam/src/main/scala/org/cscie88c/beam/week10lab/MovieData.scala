package org.cscie88c.beam.week10lab

import scala.util.Try

case class MovieData(
             series_title: String,
             released_year: Int,
             certificate: String,
             runtime: String,
             genre: String,
             imdb_rating: Double,
             meta_score: Int,
             director: String,
             star1: String,
             star2: String,
             star3: String,
             star4: String,
             num_votes: Int,
             gross: String
          )
object MovieData {

  def apply(csvString: String): Option[MovieData] = {
     val opt = Try{
      val fields = csvString.split(",").map(_.trim)
       MovieData(
         fields(0),
         fields(1).toInt, //year
         fields(2),
         fields(3),
         fields(4),
         fields(5).toDouble,
         Try(fields(6).toInt).getOrElse(0),
         fields(7),
         fields(8),
         fields(9),
         fields(10),
         fields(11),
         fields(12).toInt, //num_votes
         Try(fields(13)).getOrElse("")
       )
     }
     if (opt.isFailure){
       println(opt.failed.get.toString +s" failed: ${csvString}")
     }
     opt.toOption
  }

}
