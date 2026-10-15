package org.cscie88c.core.week7lab

import cats.{FlatMap, Functor, Monad}
import cats.implicits.{toFlatMapOps, toFunctorOps}
import org.cscie88c.core.testutils.StandardTest

import scala.util.{Failure, Success, Try}

/**
 * Start here
 * Let's first look at Option and Try
 * Then we can see Futures
 * Finally, let's discuss Functors and Monads
 */
class TryOptionTest   extends StandardTest {

  //Here's my multiply function
  def multiplier(x: Int) = x * 5

  //region functor_inc
  def incrementAnyFunctor[F[_]: Functor](fa: F[Int]) : F[Int] = {
    fa.map(_ + 1)
  }
  //endregion


  //region flatten_any
  def flattenAnyMonad[M[_], A](ffa: M[M[A]])(implicit F: FlatMap[M]): M[A] = {
    F.flatMap(ffa)(identity)
  }
  //endregion

  "Try option" should {
    "parse values" in {
      val optInt = Option(2)
      //simple example mapping some value
      val outputInt = optInt.map( multiplier(_))
      println(s"Got integer output : ${outputInt}")

    }

    "BAD :: parse empty values with extra code" in {
      val emptyInt = Option.empty[Int]
      //DO NOT do this on homework assignment!!
      val outputEmpty = if (emptyInt.isEmpty){
         Option.empty[Int]
      }else{
        emptyInt.map(multiplier(_))
      }
      println(s"Got empty output with bulky code: ${outputEmpty}")
    }

    "parse empty values nicely" in {
      val emptyInt = Option.empty[Int]
      //we can see map working on empty/'none' options
      val outputEmpty = emptyInt.map(multiplier(_))
      println(s"Got empty output nicely: ${outputEmpty}")
    }

    "BAD :: try dividing by 0 with bulky code" in {
      val test = Try(15 / 0)
      //DO NOT do this on homework assignment!!
      val result = if (test.isFailure == false){
        Option(test.get)
      }else{
        Option.empty
      }
      println(s"Finally - got my result from div by 0 : ${result}")

    }

    "try dividing by 0" in {
      val optlist = Seq(Option(5), Option(2), Option.empty[Int])
      //let's try calling map on some and none options
      optlist(1).map(x => println(s"option = ${x}"))
      optlist(2).map(x => println(s"option = ${x}"))
      //but when calling map on the elements, clearly there is a none element there
      optlist.map(x => println(s"Processing ${x}"))

      //let's make use of the fact that only "valid" values are parsed
      //first : valid division
      val result_div2 = Try( 15 / 2).map( x => println(s"Div by 2: ${x}"))
      Try(15/0) match {
        case Success(value) => println("success" + value)
        case Failure(err) => println("failure "+ err)
      }
      println(s"Got result after div by 2 : ${result_div2}")
      //this works because it's a functor
      //note that println never happens
      val result_div0 = Try( 15 / 0).map( x => println(s"Div by 0: ${x}")).toOption
      //how would this be useful when parsing possible bad data?
      println(s"Got result after div by 0 : ${result_div0}")
      //try is also a monad
      val result_div0_flatten = Try( 15 / 0).flatMap( x => {println(s"Div by 0: ${x}"); Try(x)})
      println(s"Got result after div by 0 : ${result_div0_flatten}")

      //now move on to futures (ParallelTest)
      // then explanation of functors and monads
    }

  }


  "Generic Functor increment" should {
      "handle any functor" in {
        println("try = " + incrementAnyFunctor(   Try(2)  ))
        println("option = " + incrementAnyFunctor(  Option(2) ))
        println("list = " + incrementAnyFunctor(   List(2)  ))
      }

    "handle any monad" in {
      println("try = " + flattenAnyMonad(  Try(Try(2)) ))
      println("option = " + flattenAnyMonad(   Option(Option(2)) ))
      println("list = " + flattenAnyMonad(    List(List(2))  ))
    }

  }


}
