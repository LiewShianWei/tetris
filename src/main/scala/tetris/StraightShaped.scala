package tetris

import scala.collection.mutable.ArrayBuffer
import scalafx.scene.paint.Color

class StraightShaped(
    val rotationCoordinates: Array[Array[Array[Int]]]  = Array(
        Array(Array(0, 3), Array(0, 4), Array(0, 5), Array(0, 6)),
        Array(Array(0, 4), Array(1, 4), Array(2, 4), Array(3, 4))
    ),
    val color: Array[Color] = Array(
        Color.rgb(26, 133, 171),
        Color.rgb(49, 207, 246),
        Color.rgb(48, 182, 240),
        Color.rgb(255, 254, 243)
    )) extends Block() {

    // This method has to be overidden as the straight block has even number blocks.
    // It has to be overriden so that the center of the block is an odd number in order to calculate the bounce direction.
    override def calculateWidthOfBlock(): ArrayBuffer[Int] = {
        var widthOfBlock: ArrayBuffer[Int] = ArrayBuffer[Int]()

        for (i <- currentCoordinates) {
            if (!widthOfBlock.contains(i(1))) {
                widthOfBlock.append(i(1))
            }
        }

        // To make sure that center of straight block is 2.5, not 2.
        widthOfBlock += 0

        widthOfBlock 
    }
}