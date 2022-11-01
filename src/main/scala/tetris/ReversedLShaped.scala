package tetris

import scalafx.scene.paint.Color

class ReversedLShaped(
    val rotationCoordinates: Array[Array[Array[Int]]]  = Array(
        Array(Array(2, 3), Array(2, 4), Array(1, 4), Array(0, 4)),
        Array(Array(2, 3), Array(3, 3), Array(3, 4), Array(3, 5)),
        Array(Array(4, 3), Array(3, 3), Array(2, 3), Array(2, 4)),
        Array(Array(2, 3), Array(2, 4), Array(2, 5), Array(3, 5))
    ),
    val color: Array[Color] = Array(
        Color.rgb(54, 91, 173),
        Color.rgb(70, 134, 250),
        Color.rgb(73, 122, 222),
        Color.rgb(255, 254, 243)
    )) extends Block() {
}
