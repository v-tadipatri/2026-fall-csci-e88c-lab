package org.cscie88c.core.week6lab

import org.cscie88c.core.testutils.StandardTest
import org.scalacheck.Gen

/**
 * Let's start with testing out the bat
 */
class BatTest extends StandardTest {

  "bats" should {


    "maintain temperature" in {
      val bat = new Bat()
      //call method on new object that's returned
      bat.showTemperature("before")
        .changeTemperature(15)
        .showTemperature("after mod")
        .resetTemperature()
        .showTemperature("after reset")

    }

    "flap their wings like any Flyable" in {

      val bat = new Bat()
      bat.flapWings(3)

      //what if takeoff is defined in another class (Mammal)?
      bat.takeoff()

      //what if land is defined in another trait (Discoverable)?
      bat.land()
    }




    //let's look at the bird, before coming back to implicits



    "be able to catch insects implicitly" in {
      val bat = new Bat()
      //what if the method needs to be enhanced to support IdTag?
      bat.catchInsectsWithParams(4)

      //how to specify implicit value here?
      //bat.catchInsects(3)

    }

    //import Bat.idTag
    //implicit val idTag = IdTag(2222, "BatCave")


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
        bat.takeoffAndTag()
        bat.landAndTag()
      })

    }

   }
}
