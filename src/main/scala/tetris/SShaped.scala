package tetris

import scalafx.scene.paint.Color

class SShaped(
    val rotationCoordinates: Array[Array[Array[Int]]] = Array(
        Array(Array(1, 3), Array(0, 4), Array(1, 4), Array(0, 5)), 
        Array(Array(0, 4), Array(1, 4), Array(1, 5), Array(2, 5))
    ),
    val color: Array[Color] = Array(
        Color.rgb(22, 135, 25),
        Color.rgb(133, 228, 54),
        Color.rgb(117, 199, 30),
        Color.rgb(255, 254, 243)
    )) extends Block() {
}