package org.cscie88c.core.week2.demo

import org.cscie88c.core.testutils.StandardTest

import scala.collection.mutable

class ReduceTest extends StandardTest{
  "Number List" when {
    "reduced" should {
      "show a single value" in {
        def mult2Step(a: Int, b: Int): Int = {
          println(s"$a * $b = ${a * b}")
          a * b
        }
        /*
        val result = List(1,2,3,4,5).reduce(
          (a: Int,b: Int) => mult2Step(a,b)
        )
         */
        val result = List(1,2,3,4,5).reduce(mult2Step)
        //result should be 120
        //result shouldBe 120
        result should be(120)

      }
    }
  }
  "Car list" when {
    "reduced" should {
      "form into a single robot" in {
        val mainpart = "Voltron"
        val part_names = mutable.ListBuffer("head", "arm", "leg")

        def makeVoltron(first_part: String, second_part: String): String = {
          //result of if statement assigned to prefix
          val prefix= if (first_part == mainpart) "This is " else ""
          //remove first element and add it to end
          val curr_part_name = part_names.remove(0)
          part_names += curr_part_name
          //now put everything together and return the string
          val result = s"${prefix}${first_part}, with ${curr_part_name}=${second_part}"
          result
        }




        val available_parts = List(mainpart, "Toyota", "Honda", "Jeep")


        val transformed = available_parts.reduce(
          (part1, part2) => makeVoltron(part1, part2)
        )
        //val transformed = available_parts.reduce(makeVoltron)
        println(transformed)


        //last example - map PLUS reduce
        val available_parts_upper = available_parts.map(s => s.toUpperCase())
        println("UPPER = "+ available_parts_upper.reduce(makeVoltron))

      }
    }
  }
}
