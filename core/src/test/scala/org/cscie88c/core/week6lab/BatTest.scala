package org.cscie88c.core.week6lab

import org.cscie88c.core.testutils.StandardTest
import org.scalacheck.Gen

class BatTest extends StandardTest {

  "bats" should {


    "maintain temperature" in {
      val bat = new Bat()
      bat.showTemperature("before")
      bat.changeTemperature(-5)
      bat.showTemperature("after mod")
      bat.resetTemperature()
      bat.showTemperature("after reset")
      //import Bat.idTag
      //bat.catchInsects(3)
    }


    "be tracked when flying" in {
      val intGen = Gen.choose(0,1000)
      val stringGen = Gen.oneOf(Seq("Boston", "NYC", "Montreal"))
      val idGen = for {
         intval <- intGen
         strval <- stringGen
      } yield IdTag(intval, strval)

      val foundBats = List.fill(10)(new Bat)
      foundBats.foreach(bat => {
        implicit val id = idGen.sample.get
        bat.takeoff()
        bat.land()
      })

    }

   }
}
