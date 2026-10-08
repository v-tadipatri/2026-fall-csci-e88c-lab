package org.cscie88c.core.week6lab

/**
 * Mammals are warm blooded, so they maintain their temperature
 */
abstract class Mammal[M](protected val baseBodyTemperature: Int,
             protected val tolerance: Int,
             protected val currBodyTemperature: Int
            ) {

  /**
   * When cloning, we want to return the type of the subclass
   * @param newTemperature
   * @return
   */
    def cloneWithTemperature(newTemperature: Int): M


  /**
   * Return new instance, whether temperature changed or not
   * @return
   */
  def resetTemperature(): M = {
        if (math.abs(baseBodyTemperature - currBodyTemperature)> tolerance){
            cloneWithTemperature(baseBodyTemperature)
        }else{
            this.asInstanceOf[M]
        }
    }

    def changeTemperature(offset: Int): M = {
         cloneWithTemperature(currBodyTemperature+offset)
    }

  /**
   * Just printing, so no new instance
   * @param prefix
   * @return
   */
    def showTemperature(prefix: String): M = {
        println(s"${prefix} : ${getClass.getSimpleName} has ${currBodyTemperature} temperature")
        this.asInstanceOf[M]
    }

    /*
    def takeoff(): Unit = {
        println("This mammal is attempting to fly!!")
    }
     */

}
