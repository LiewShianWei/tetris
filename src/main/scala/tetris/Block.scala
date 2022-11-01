package tetris

import scala.collection.mutable.ArrayBuffer
import scalafx.scene.paint.Color

abstract class Block {
    // Keep track of how much the block has moved in the x and y direction.
    var xMoved: Int = 0
    var yMoved: Int = 0

    // Keep track of the current rotation index.
    var rotation: Int = 0

    // Consists of all possible rotation coordinates.
    val rotationCoordinates: Array[Array[Array[Int]]]

    // Holds current coordinates of the block based on its rotation.
    var currentCoordinates: Array[Array[Int]] = rotationCoordinates(rotation)
    val color: Array[Color]

    // Move block left.
    def moveLeft(): Unit = {
        xMoved -= 1
    }

    // Move block right.
    def moveRight(): Unit = {
        xMoved += 1
    }

    // Move block down.
    def moveDown(): Unit = {
        yMoved += 1
    }

    // Rotate block.
    def rotate(): Unit = {
        rotation += 1
        rotation = rotation % rotationCoordinates.length
        currentCoordinates = rotationCoordinates(rotation)
    }

    // Unrotate block.
    def unrotate(): Unit = {
        rotation -= 1

        if (rotation < 0) {
            rotation = rotationCoordinates.length + rotation
        }

        currentCoordinates = rotationCoordinates(rotation)
    }

    // Calculate the width of the block.
    def calculateWidthOfBlock(): ArrayBuffer[Int] = {
        var widthOfBlock: ArrayBuffer[Int] = ArrayBuffer[Int]()

        for (i <- currentCoordinates) {
            if (!widthOfBlock.contains(i(1))) {
                widthOfBlock.append(i(1))
            }
        }

        widthOfBlock
    }
}