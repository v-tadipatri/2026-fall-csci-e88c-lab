package org.cscie88c.core.week6lab

/**
 * We want to tag animals while they exhibit different behaviors
 * But passing this new class in every method could require a lot of refactoring
 * Let's use implicits!
 * @param id
 * @param srcLocation
 */
case class IdTag (id: Long, srcLocation: String) {

}

//companion object
object IdTag {

}
