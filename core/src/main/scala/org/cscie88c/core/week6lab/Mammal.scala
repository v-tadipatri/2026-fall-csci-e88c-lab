package org.cscie88c.core.week6lab

/**
 * Mammals are warm blooded, so they maintain their temperature
 */
class Mammal {
    protected val baseBodyTemperature = 98.6

    //just for demo, using a var
    protected var currBodyTemperature = baseBodyTemperature
    protected val tolerance = 2

    def resetTemperature(): Unit = {
        if (math.abs(baseBodyTemperature - currBodyTemperature)> tolerance){
            currBodyTemperature = baseBodyTemperature
        }
    }

    def changeTemperature(offset: Double): Unit = {
         currBodyTemperature+=offset
    }

    def showTemperature(prefix: String): Unit = {
        println(s"${prefix} : ${getClass.getSimpleName} has ${currBodyTemperature} temperature")
    }

    /*
    def takeoff(): Unit = {
        println("This mammal is attempting to fly!!")
    }
     */

}
