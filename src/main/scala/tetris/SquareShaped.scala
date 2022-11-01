package tetris

import scalafx.scene.paint.Color

class SquareShaped(
    val rotationCoordinates: Array[Array[Array[Int]]]  = Array(
        Array(Array(0, 4), Array(0, 5), Array(1, 4), Array(1, 5))
    ),
    val color: Array[Color] = Array(
        Color.rgb(156, 119, 36),
        Color.rgb(248, 228, 64),
        Color.rgb(255, 194, 41),
        Color.rgb(255, 254, 243)
    )) extends Block() {
}