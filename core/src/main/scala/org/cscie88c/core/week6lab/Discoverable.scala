package org.cscie88c.core.week6lab

trait Discoverable {

   //is this method name ok?
    def land(): Unit = {
      println(getClass.getSimpleName+" discovered a new land!")
    }

  //what happens without an implementation
    //def foundWater()

}
