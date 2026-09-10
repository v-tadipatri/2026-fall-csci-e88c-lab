package org.cscie88c.core.week2.demo

import org.cscie88c.core.testutils.StandardTest

import scala.collection.mutable

class MapTest extends StandardTest{
  "Map objects" when {
    "reduced" should {
      "show a single value" in {
        val map1 = Map( "Civic" -> "Honda", "Corolla" -> "Toyota", "Sonata" -> "Hyundai")
        val list2map = List( ("Civic",  "Honda"),
          ("Corolla" , "Toyota"),
          ("Sonata" , "Hyundai")).toMap
        println(s"map1 = ${map1}")
        println(s"list2map = ${list2map}")
        val lookupCivic = map1("Civic")
        println(s"Civic is a kind of ${lookupCivic}")

        val lookupSentraUnknown = map1.getOrElse("Sentra", "MaybeNissan")
        println(s"Sentra is a kind of ${lookupSentraUnknown}")
        /*
        val lookupSentra = map1("Sentra")
        println(s"Sentra is a kind of ${lookupSentra}")
         */

      }
    }
  }

  "Map function" when {
    "analyzed" should {
      println(
        List(1,2,3,4)
          .map(i => s"num${i}")
      )
      //println(List(1,2,3,4).map(i => s"num${i}"))
    }
  }

}
