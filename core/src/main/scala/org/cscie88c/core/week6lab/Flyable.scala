package org.cscie88c.core.week6lab

trait Flyable {
    //traits can have attributes
    val wingCount = 2
    val flapWings = (sec: Int) => {
      println(s"stretching ${wingCount} wings")
      (1 to sec).foreach(i => {
        println(s"flapping for second ${i}")
      })
      println(s"folding ${wingCount} wings")
    }

    //traits can have methods
    def takeoff(): Unit = {
      println(getClass.getSimpleName+s" is now taking off")
    }

    def land(): Unit = {
      println(getClass.getSimpleName+s" is now landing")
    }





  def takeoffAndTag()(implicit idTag: IdTag): Unit = {
    println(getClass.getSimpleName+s" is now taking off, with id = ${idTag}")
  }

  def landAndTag()(implicit idTag: IdTag): Unit = {
    println(getClass.getSimpleName+s" is now landing, with id = ${idTag}")
  }


}

