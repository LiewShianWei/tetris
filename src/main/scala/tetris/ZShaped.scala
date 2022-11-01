package tetris

import scalafx.scene.paint.Color

class ZShaped(
    val rotationCoordinates: Array[Array[Array[Int]]]  = Array(
        Array(Array(0, 3), Array(0, 4), Array(1, 4), Array(1, 5)),
        Array(Array(2, 4), Array(1, 4), Array(1, 5), Array(0, 5))
    ),
    val color: Array[Color] = Array(
        Color.rgb(166, 31, 63),
        Color.rgb(254, 65, 88),
        Color.rgb(230, 41, 82),
        Color.rgb(255, 254, 243)
    )) extends Block() {
}