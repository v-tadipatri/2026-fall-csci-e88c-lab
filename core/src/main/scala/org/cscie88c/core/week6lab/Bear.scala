package org.cscie88c.core.week6lab

/**
 * Bears have a lot of implicit methods!
 */
class Bear(myBodyTemp: Int=99)
  extends Mammal[Bear]( 99, 10, myBodyTemp ) {

  val hibernatingTemp = baseBodyTemperature - tolerance

  override def cloneWithTemperature(newTemperature: Int): Bear = {
    if (newTemperature < hibernatingTemp){
      println("I am currently hibernating!!")
    }
    if (newTemperature >= baseBodyTemperature && currBodyTemperature < hibernatingTemp){
      println("I just woke up from hibernation and I'm hungry!!")
    }
    new Bear(newTemperature)
  }

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
