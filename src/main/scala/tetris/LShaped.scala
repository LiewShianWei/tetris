package tetris

import scalafx.scene.paint.Color

class LShaped(
    val rotationCoordinates: Array[Array[Array[Int]]]  = Array(
        Array(Array(0, 3), Array(1, 3), Array(2, 3), Array(2, 4)),
        Array(Array(2, 3), Array(1, 3), Array(1, 4), Array(1, 5)),
        Array(Array(1, 3), Array(1, 4), Array(2, 4), Array(3, 4)),
        Array(Array(2, 3), Array(2, 4), Array(2, 5), Array(1, 5))
    ),
    val color: Array[Color] = Array(
        Color.rgb(182, 84, 34),
        Color.rgb(250, 160, 34),
        Color.rgb(254, 125, 39),
        Color.rgb(255, 254, 243)
    )) extends Block() {
}