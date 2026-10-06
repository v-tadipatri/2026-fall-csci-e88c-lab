package org.cscie88c.core.week6lab

case class Bat() extends Mammal
         with Flyable {
  var insectsEaten = 0

  def catchInsects(numBugs: Int)(implicit  idTag: IdTag): Unit = {
    insectsEaten+=numBugs
    println(s"This bat has eaten bugs = ${insectsEaten}, with id = ${idTag}")
  }
}

object Bat {
  implicit val idTag: IdTag = new IdTag(999, "GothamCity")
}
