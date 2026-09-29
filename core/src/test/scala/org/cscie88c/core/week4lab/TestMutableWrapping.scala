package org.cscie88c.core.week4lab

import org.cscie88c.core.testutils.StandardTest

import scala.collection.mutable.ListBuffer

/**
 * 4. Lists are immutable, but ListBuffers can be changed
 */
class TestMutableWrapping extends StandardTest {
    val presentsNum: Seq[String] = (1 to 5).toList.map("0" + _)


    //what happens if we put a space here after string in Intellij?
    "Wrapping facility" when {
        "asked to mutate lists" should {

            "add more presents" in {
                //try different ways of wrapping gifts
                //presentsNum += "07"
                //Avoid this unless you absolutely have to use it!!
                val badMutablePresents = ListBuffer(presentsNum : _*)
                badMutablePresents should not contain("007")
                println(s"before: ${badMutablePresents}")
                badMutablePresents +="007"
                badMutablePresents +="008"
                badMutablePresents +="009"
                println(s"after: ${badMutablePresents}")
                badMutablePresents should contain("007")

            }

        }

    }
  
}
