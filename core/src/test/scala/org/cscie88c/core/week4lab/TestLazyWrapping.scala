package org.cscie88c.core.week4lab

import org.cscie88c.core.testutils.StandardTest

import scala.collection.mutable.ListBuffer

/**
 * 3. Lazy evaluation, including infinite lists!
 */
class TestLazyWrapping extends StandardTest {


    "Wrapping facility" when {
        "asked to mutate lists" should {

            "be eager in checking presents" in {
                val presentsNum: Seq[String] = (1 to 5).toList.map(g => {
                    println(s"Opening present ${g} now...")
                    "0" + g
                })
                println("Eager: Now we will see what's in the presents")
                //presentsNum.foreach(println)

            }
            "be lazy in checking presents" in {
                lazy val lazyPresentsNum: Seq[String] = (1 to 5).toList.map(g => {
                    println(s"Wait to open present ${g}...")
                    //maybe open a DB connection here?
                    //or call some API with a long response time?
                    "*0" + g
                })
                println("Lazy: Now we will see what's in the presents")
                lazyPresentsNum.foreach(println)

                //we can make infinite Gifts!!
                val infiniteGifts = LazyList.from(1).map(s => "gift-"+s)
                //but you have to make it in a "real" list by calling toList
                println(infiniteGifts.take(2).toList)
                //println(infiniteGifts.take(200).toList)
            }

        }

    }
  
}
