package org.cscie88c.core.week6lab

case class Bat(insectsEaten:Int =0,
               myBodyTemperature:Int=104
              ) extends Mammal[Bat](104, 10, myBodyTemperature)
         with Flyable {

  override def cloneWithTemperature(newTemperature: Int): Bat = {
    new Bat(insectsEaten, newTemperature)
  }

  //how to extend this to support more params?
  def catchInsectsWithParams(numBugs: Int ): Bat = {
    val newBat = new Bat(insectsEaten+numBugs)
    println(s"This bat has eaten bugs = ${newBat.insectsEaten} ") //, with id = ${idTag}")
    newBat
  }









/*
  def catchInsects(numBugs: Int)   (implicit  idTag: IdTag): Bat = {
    val newBat = new Bat(insectsEaten+numBugs)
    println(s"This bat has eaten bugs = ${newBat.insectsEaten}, with id = ${idTag}")
    newBat
  }

 */
}

object Bat {
  implicit val idTag  =  IdTag(999, "GothamCity")
}
