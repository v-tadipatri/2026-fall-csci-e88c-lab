package org.cscie88c.core.week6lab

class Bird {

  def buildNest(): Unit = {
    println(s"${getClass.getSimpleName} is building a nest")
  }
}

//different class hierarchy, but has the trait
class Hummingbird extends Bird with Flyable {

}
