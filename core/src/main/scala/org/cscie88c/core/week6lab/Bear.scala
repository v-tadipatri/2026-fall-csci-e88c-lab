package org.cscie88c.core.week6lab

/**
 * Bears have a lot of implicit methods!
 */
class Bear extends Mammal {

   def catchSomeFish(fishCaught: Int)  (implicit idTag: IdTag)= {
     println(s"Bear with id = ${idTag} caught ${fishCaught} fish")
   }

   def enterCampsite()    (implicit idTag: IdTag)= {
      println(s"Bear with id = ${idTag} is now in the campsite")
      //call another implicit method
      //the bear has now found pizza inside the campsite!
      findPizza()
   }

  def findPizza()   (implicit idTag: IdTag)= {
    println(s"Bear with id = ${idTag} has found pizza ")
  }

}
object Bear {
  //where can we put this so it can be found more easily?
  implicit val idTag =  IdTag(111, "InTheWoods")
}
