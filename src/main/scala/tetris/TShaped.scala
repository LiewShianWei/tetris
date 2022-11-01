package tetris

import scalafx.scene.paint.Color

class TShaped(
    val rotationCoordinates: Array[Array[Array[Int]]]  = Array(
        Array(Array(0, 3), Array(0, 4), Array(1, 4), Array(0, 5)),
        Array(Array(1, 3), Array(0, 4), Array(1, 4), Array(2, 4)),
        Array(Array(1, 3), Array(1, 4), Array(0, 4), Array(1, 5)),
        Array(Array(0, 4), Array(1, 4), Array(2, 4), Array(1, 5))
    ),
    val color: Array[Color] = Array(
        Color.rgb(139, 37, 130),
        Color.rgb(229, 74, 201),
        Color.rgb(196, 71, 161),
        Color.rgb(255, 254, 243)
    )) extends Block() {
}
