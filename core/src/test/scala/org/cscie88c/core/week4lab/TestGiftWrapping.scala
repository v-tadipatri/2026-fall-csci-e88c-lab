package org.cscie88c.core.week4lab

import org.cscie88c.core.testutils.{StandardTest}

/**
 * 1. We have a gift wrapping facility that can wrap different
 *  kinds of gifts using functional programming principles
 *
 * Let's also look at pure functions and referential transparency
 */
class TestGiftWrapping extends StandardTest {
    val wrappingFacility = new GiftWrappingFacility
    val presentsNum: Seq[String] = (1 to 5).toList.map("0" + _)

    "Wrapping facility" when {
        "asked to wrap presents in a normal way" should {

            "handle numeric Strings with parentheses" in {
                //this function puts parentheses
                val parenthesesWrap :   (String, String)  => String     = (s1: String, s2: String) =>  {
                    s1 + s"(${s2})"
                }
                //try different ways of wrapping gifts
                //note the fold method takes a dot as the initial value
                val result = wrappingFacility.wrapGifts(presentsNum, parenthesesWrap)
                println(s"parentheses result:  ${result}")
                result shouldBe ".(01)(02)(03)(04)(05)"
            }

            "handle numeric Strings with stars" in {
                //this is a different function we will pass
                val starWrap :   (String, String)  => String     = (s1: String, s2: String) =>  {
                    s1 + s" **| ${s2}"
                }
                val result = wrappingFacility.wrapGifts(presentsNum, starWrap)
                println(s"starred result: ${result}")
                result shouldBe ". **| 01 **| 02 **| 03 **| 04 **| 05"
            }


            "handle numeric Strings with stars as a pure function" in {
                //this is a different function we will pass
                val starWrap :   (String, String)  => String     = (s1: String, s2: String) =>  {
                    //println("Run an errand to get more stars")
                    s1 + s" **| ${s2}"
                }

                val pctWrap :   (String, String)  => String     = (s1: String, s2: String) =>  {
                    //println("Run an errand to get more percents")
                    s1 + s" %%| ${s2}"
                }


                println("===== combined result below =====")
                val combined = wrappingFacility.wrapGifts(presentsNum, starWrap) + wrappingFacility.wrapGifts(presentsNum, pctWrap)
                val combined_again = wrappingFacility.wrapGifts(presentsNum, starWrap) + wrappingFacility.wrapGifts(presentsNum, pctWrap)
                println(s"combined: ${combined}")
                val expected_result = ". **| 01 **| 02 **| 03 **| 04 **| 05. %%| 01 %%| 02 %%| 03 %%| 04 %%| 05"
                combined shouldBe expected_result

                println("===== combined result with replaced values below =====")
                val result = wrappingFacility.wrapGifts(presentsNum, starWrap)
                val result2 = wrappingFacility.wrapGifts(presentsNum, pctWrap)

                //let's try replacing the value
                val combined_result = result + result2
                val combined_result_again = result + result2

                println(s"combined result: ${combined_result}")
                combined_result shouldBe expected_result
            }
        }

    }
  
}
