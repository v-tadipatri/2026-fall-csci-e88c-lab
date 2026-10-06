package org.cscie88c.core.week6lab

trait Flyable {

    def takeoff()(implicit idTag: IdTag): Unit = {
      println(getClass.getSimpleName+s" is now taking off, with id ${idTag}")
    }

    def land()(implicit idTag: IdTag): Unit = {
      println(getClass.getSimpleName+s" is now landing, with id ${idTag}")
    }
}

object Flyable {
  implicit val idTag: IdTag = new IdTag(234, "abc")
}
