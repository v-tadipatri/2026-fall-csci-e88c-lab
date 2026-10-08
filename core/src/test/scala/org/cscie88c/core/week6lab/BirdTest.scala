package org.cscie88c.core.week6lab

import org.cscie88c.core.testutils.StandardTest
import org.scalacheck.Gen

/**
 * Birds belong to a different class hierarchy than mammals
 */
class BirdTest extends StandardTest {

  "birds" should {
      "be capable of flight like any Flyable" in {
          val bird = new Hummingbird()
          bird.buildNest()
          //note how fields and methods from trait are available
          //more than just Java interfaces
          println(s"I have ${bird.wingCount} wings!")
          bird.flapWings(4)
      }


  }

}
