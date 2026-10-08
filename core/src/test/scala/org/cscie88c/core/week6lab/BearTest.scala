package org.cscie88c.core.week6lab

import org.cscie88c.core.testutils.StandardTest
import org.scalacheck.Gen

class BearTest extends StandardTest {

  "bears" should {


    "maintain temperature" in {
      val bear = new Bear()
      //same methods as for bat, but we have extra printlns
      bear.showTemperature("before")
        .changeTemperature(-15)
        .showTemperature("after mod")
        .resetTemperature()
        .showTemperature("after reset")
    }

    "wander through the woods" in {
      val bear = new Bear()

      //is this the right import?
      import Bat.idTag
      //what if we want the IdTag to be available without any import or implicit val?
      bear.catchSomeFish(3)
      bear.enterCampsite()

    }



   }
}
