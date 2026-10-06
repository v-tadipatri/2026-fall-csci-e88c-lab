package org.cscie88c.core.week6lab

import org.cscie88c.core.testutils.StandardTest
import org.scalacheck.Gen

class BearTest extends StandardTest {

  "bears" should {


    "maintain temperature" in {
      val bear = new Bear()
      bear.showTemperature("before")
      bear.changeTemperature(-5)
      bear.showTemperature("after mod")
      bear.resetTemperature()
      bear.showTemperature("after reset")
    }

    "wander through the woods" in {
      val bear = new Bear()

      import Bear.idTag
      bear.catchSomeFish(3)
      bear.enterCampsite()
    }



   }
}
