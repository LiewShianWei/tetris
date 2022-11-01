package tetris

import scalafx.scene.paint.Color

case class BoardRect(val x: Double, val y: Double, var value: Int, var color: Array[Color] = Array(Color.rgb(50, 50, 50))) {}