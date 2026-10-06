package org.cscie88c.core.week6lab

class Bear extends Mammal {

   def catchSomeFish(fishCaught: Int)(implicit idTag: IdTag)= {
     println(s"Bear with id = ${idTag} caught ${fishCaught} fish")
   }

   def enterCampsite()(implicit idTag: IdTag)= {
      println(s"Bear with id = ${idTag} is now in the campsite")
   }


}
object Bear {
  implicit val idTag: IdTag = new IdTag(111, "InTheWoods")
}
