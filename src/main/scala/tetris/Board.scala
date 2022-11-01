package tetris

import scala.util.Random
import scala.util.control.Breaks._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.scene.paint.Color
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.canvas.Canvas

class Board(var gridWidth: Int, var gridHeight: Int, var rectWidth: Double, var rectHeight: Double, var canvas: Canvas) {
    // An array of the 10X20 tetris board.
    val grid: Array[Array[BoardRect]] = Array.ofDim[BoardRect](gridHeight, gridWidth)
    val gc: GraphicsContext = canvas.getGraphicsContext2D()
    var currentBlock: Block = new SShaped()
    var nextBlock: Block = new SShaped()
    var score: Int = 0

    // Initialize the board by filling the grid the board rect.
    def initialize(): Unit = {
        for (y <- 0 until gridHeight) {
            for (x <- 0 until gridWidth) {
                // Board rect holds the x and y coordinates, and a 1 or 0 that indicates if an index in the grid is filled.
                grid(y)(x) = new BoardRect(x * (rectWidth + 0.022), y * (rectHeight + 0.022), 0)
            }
        }

        // Sets the next block.
        setNextBlock()
    }

    // Spawns block.
    def spawnBlock(): Unit = {
        // Sets the current block to the next block.
        currentBlock = nextBlock

        // Sets the next block again.
        setNextBlock()
    }

    // Sets next block by generating a random number to randomly generate a block shape.
    def setNextBlock(): Unit = {
        var rng: Int = Random.nextInt(7)

        if (rng == 0) {nextBlock = new SShaped()}
        if (rng == 1) {nextBlock = new ZShaped()}
        if (rng == 2) {nextBlock = new LShaped()}
        if (rng == 3) {nextBlock = new ReversedLShaped()}
        if (rng == 4) {nextBlock = new TShaped()}
        if (rng == 5) {nextBlock = new SquareShaped()}
        if (rng == 6) {nextBlock = new StraightShaped()}
    }

    // Draws the grid onto the canvas by drawing every rectangle individually according to the grid.
    def draw(): Unit = {
        for (y <- 0 until gridHeight) {
            for (x <- 0 until gridWidth) {
                drawBoardRect(grid(y)(x).x, grid(y)(x).y, rectWidth, rectHeight, grid(y)(x).value, grid(y)(x).color)
            }
        }
    }

    // Draws a rectangle on the canvas.
    def drawBoardRect(x: Double, y: Double, width: Double, height: Double, value: Int, color: Array[Color]): Unit = {
        // JavaFX GUI manipulations must be done in JavaFX main thread.
        val runnable: Runnable = new Runnable {
            override def run(): Unit = {
                // If value is 0, means the index is not filled, so draw default rectangle.
                if (value == 0) {
                    gc.setFill(Color.rgb(50, 50, 50))
                    gc.fillRect(x, y, width, height)
                    gc.lineWidth_=(3)
                    gc.setStroke(Color.rgb(45, 45, 45))
                    gc.strokeRect(x, y, width, height)
                }

                // If value is 1, means the index contains a piece of the block, so draw the block.
                else {
                    // Draws 4 squares of different sizes to style the block.
                    val shapeSize: Array[Array[Double]] = Array(Array(0, 0), Array(2.5, 5), Array(10.5, 21), Array(3, 34))

                    // Draws the 4 squares.
                    for (i <- 0 until color.size) {
                        gc.setFill(color(i))
                        gc.fillRect(x + shapeSize(i)(0), y + shapeSize(i)(0), width - shapeSize(i)(1), height - shapeSize(i)(1))
                    }
                }
            }
        }
        Platform.runLater(runnable)
    }

    // Undraws a block by setting its value to 0.
    def undraw(): Unit = {
        for (i <- currentBlock.currentCoordinates) {
            grid(i(0) + currentBlock.yMoved)(i(1) + currentBlock.xMoved).value = 0
        }

    }

    // Draws the next block in the top right corner of the screen.
    def drawNextBlock(): Unit = {
        // Undraw the previous next block first, then draw the new next block.
        undrawNextBlock()

        for (i <- nextBlock.currentCoordinates) {
            drawBoardRect((i(1) * (20)) + 357, i(0) * (20) + 83, 20, 20, 1, nextBlock.color)
        }
    }

    // Undraws the previous next block by drawing a block square over it.
    def undrawNextBlock(): Unit = {
        drawBoardRect(400, 50, 100, 100, 1, Array(Color.Black))
    }

    // Inserts block into grid by setting the index to 1.
    def insertBlockIntoGrid(): Unit = {
        for (i <- currentBlock.currentCoordinates) {
            grid(i(0) + currentBlock.yMoved)(i(1) + currentBlock.xMoved).value = 1
            grid(i(0) + currentBlock.yMoved)(i(1) + currentBlock.xMoved).color = currentBlock.color
        }
    }

    // Checks if the current block's surroundings is part of itself or not.
    def notPartOfBlock(coordinate: Array[Int]): Boolean = {
        var result: Boolean = true
        breakable {
            for (i <- currentBlock.currentCoordinates) {
                if (i(0) == coordinate(0) && i(1) == coordinate(1)) {
                    result = false
                    break
                }
            }
        }

        result
    }

    // Checks if the current block's x coordinates exceeds the x bounds of the board.
    def exceededWidthOfBoard(x: Int, xMoved: Int): Boolean = {
        if (x + xMoved < 0 || x + xMoved > 9) {
            true
        }
        else {
            false
        }
    }

    // Calculates the direction of where the block should bounce after colliding with a wall or another block.
    def calculateBounceDirection(xIntercept: Int): String = {
        // Gets the width of the block.
        var widthOfBlock: ArrayBuffer[Int] = currentBlock.calculateWidthOfBlock()
        
        // The position where the block is intercepted.
        var indexOfInterception: Double = 0;

        // Calculated the center of the block.
        var centerOfBlock: Double = widthOfBlock.size.toFloat / 2

        // Identifies which index of the block was intercepted.
        breakable {
            for (i <- widthOfBlock) {
                indexOfInterception += 1

                if (i == xIntercept) {
                    break
                }
            }
        }

        // If the interception was less than the center of the block means the block should bounce right.
        // If its more than the center means the block should bounce left.
        if (indexOfInterception < centerOfBlock) {
            "Right"
        }
        else {
            "Left"
        }
    }

    // Checks if the block can move left.
    def canMoveLeft(): Boolean = {
        var result: Boolean = true
        breakable {
            for (i <- currentBlock.currentCoordinates) {
                // If the block is already at the wall then it cant move left anymore.
                if (i(1) + currentBlock.xMoved <= 0) {
                    result = false
                    break
                }

                // If there is another block on the left of the current block which is not part of the current block, then cant move left anymore.
                if (notPartOfBlock(Array(i(0), i(1) - 1)) && grid(i(0) + currentBlock.yMoved)(i(1) + currentBlock.xMoved - 1).value == 1) {
                    result = false
                    break
                }
            }
        }

        result
    }


    // Checks if the block can move right.
    def canMoveRight(): Boolean = {
        var result: Boolean = true
        breakable {
            for (i <- currentBlock.currentCoordinates) {
                // If the block is already at the wall then it cant move right anymore.
                if (i(1) + currentBlock.xMoved >= 9) {
                    result = false
                    break
                }

                // If there is another block on the right of the current block which is not part of the current block, then cant move right anymore.
                if (notPartOfBlock(Array(i(0), i(1) + 1)) && grid(i(0) + currentBlock.yMoved)(i(1) + currentBlock.xMoved + 1).value == 1) {
                    result = false
                    break
                }
            }
        }

        result
    }

    // Checks if the block can move down.
    def canMoveDown(): Boolean = {
        var result: Boolean = true
        breakable {
            for (i <- currentBlock.currentCoordinates) {
                // If the block is already at the bottom then it cant move down anymore.                
                if (i(0) + currentBlock.yMoved >= 19) {
                    result = false
                    break
                }

                // If there is another block underneath of the current block which is not part of the current block, then cant move down anymore.
                if (notPartOfBlock(Array(i(0) + 1, i(1))) && grid(i(0) + currentBlock.yMoved + 1)(i(1) + currentBlock.xMoved).value == 1) {
                    result = false
                    break
                }
            }
        }

        result
    }

    // Checks if the block can rotate.
    def canRotate(): Boolean = {
        var result: Boolean = true

        // Temporary variables to simulate the rotation of the block to check if its possible before actually rotating the block.
        var tempXMoved: Int = currentBlock.xMoved
        var tempYMoved: Int = currentBlock.yMoved

        // To check if the block has gone out of bounds.
        var outOfBoard: Boolean = false

        for (i <- currentBlock.currentCoordinates) {
            // If the block is out of the left wall after rotation, push it back into the grid.
            while (i(1) + tempXMoved < 0) {
                tempXMoved += 1
                outOfBoard = true
            }

            // If the block is out of the right wall after rotation, push it back into the grid.
            while (i(1) + tempXMoved > 9) {
                tempXMoved -= 1
                outOfBoard = true
            }

            // If the block is out of the bottom after rotation, push it back into the grid.
            while (i(0) + tempYMoved > 19) {
                tempYMoved -= 1
            }
        }

        breakable {
            for (i <- currentBlock.currentCoordinates) {
                // Checks whether each block has gone out of bounds or not.
                if (exceededWidthOfBoard(i(1), tempXMoved)) {
                    result = false
                    break
                }

                // Checks if the current block's index intercepts with an already taken index.
                // If this happens it means the block has a collision.
                if (grid(i(0) + tempYMoved)(i(1) + tempXMoved).value == 1) {
                    // Checks if the block has already been out of bounds, if it has then stop.
                    // This is because if the block was out of bounds, and it was pushed back in, and it hits another block.
                    // It means that there is no chance it can rotate as it is sanwiched between the wall and another block.
                    if (outOfBoard) {
                        result = false
                        break
                    }

                    // Checks which direction the block should bounce.
                    var interceptionPoint: Int = tempXMoved
                    var bounceDirection: String = calculateBounceDirection(i(1))

                    // If the block should bounce right, then the block's x coordinate is increased until there is no collision.
                    if (bounceDirection == "Right") {
                        for (i <- currentBlock.currentCoordinates) {
                            while (tempXMoved <= interceptionPoint) {
                                tempXMoved += 1
                            }
                        }
                    }

                    // If the block should bounce left, then the block's x coordinate is decreased until there is no collision.
                    else if (bounceDirection == "Left") {
                        for (i <- currentBlock.currentCoordinates) {
                            while (tempXMoved >= interceptionPoint) {
                                tempXMoved -= 1
                            }
                        }
                    }
                }
            }
        }
        
        // After pushing back in when it has gone out of bounds, and pushing it left or right when it collides with another block,
        // If it passed the previous 2 tests, the final position is checked again to see if the block has any collisions or is out of bounds.
        if (result == true) {
            breakable {
                for (i <- currentBlock.currentCoordinates) {
                    // If the block's final position is still out of bounds, it means the block can't be rotated.
                    if (exceededWidthOfBoard(i(1), tempXMoved)) {
                        result = false
                        break
                    }

                    // If the block's final position is still colliding with another block, it means the block can't be rotated.
                    if (grid(i(0) + tempYMoved)(i(1) + tempXMoved).value == 1) {
                        result = false
                        break
                    }
                }
            }
        }

        // However, if the rotation passes all tests, then the block's x and y coordinates are updated to rotated the block.
        if (result == true) {
            currentBlock.xMoved = tempXMoved
            currentBlock.yMoved = tempYMoved
        }

        result
    }

    // Moves the block left.
    def moveBlockLeft(): Unit = synchronized {
        if (canMoveLeft()) {
            // Undraw the block.
            undraw()

            // Update the block's x coordinates to move left.
            currentBlock.moveLeft()

            // Insert updated coordinates into grid.
            insertBlockIntoGrid()

            // Draw the block.
            draw()
        }
    }
    
    // Moves the block right.
    def moveBlockRight(): Unit = synchronized {
        if (canMoveRight()) {
            // Undraw the block.
            undraw()

            // Update the block's x coordinates to move right.
            currentBlock.moveRight()

            // Insert updated coordinates into grid.
            insertBlockIntoGrid()

            // Draw the block.
            draw()
        }
    }

    // Moves the block down.
    def moveBlockDown(): Unit = synchronized {
        if (canMoveDown()) {
            // Undraw the block.
            undraw()

            // Update the block's y coordinates to move down.
            currentBlock.moveDown()

            // Insert updated coordinates into grid.
            insertBlockIntoGrid()

            // Draw the block.
            draw()
        }
    }

    // Hard drops the block.
    def placeBlock(): Unit = synchronized {
        // Undraw the block.
        undraw()

        // Move the block all the way down.
        while (canMoveDown()) {
            currentBlock.moveDown()
        }

        // Insert updated coordinates into grid.
        insertBlockIntoGrid()

        // Draw the block.
        draw()
    }

    // Rotates block.
    def rotateBlock(): Unit = synchronized {
        // Checks if the block is still able to move down.
        if (canMoveDown()) {
            // Undraw the block.
            undraw()

            // Rotate the block.
            currentBlock.rotate()

            // If the can rotate, insert updates coordinates into grid and draw.
            if (canRotate()) {
                insertBlockIntoGrid()
                draw()
            }

            // If can't rotate, unrotate the block, insert previous coordinates into grid and draw.
            else {
                currentBlock.unrotate()
                insertBlockIntoGrid()
                draw()
            }
        }
    }

    // Clears finished rows.
    def clearFinishedRows(): Boolean = {
        // Keeps track of the y index of the rows that finished.
        var finishedRows: ArrayBuffer[Int] = ArrayBuffer[Int]()
        var cleared: Boolean = false

        for (y <- 0 until gridHeight) {
            var finished: Boolean = true

            // Checks each x index of each y index to ensure that every x index is set to 1.
            for (x <- 0 until gridWidth) {
                if (grid(y)(x).value == 0) {
                    finished = false
                }
            }

            if (finished) {
                finishedRows += y
                score += 1
            }
        }

        // Iterates through all finished rows to clear them.
        for (i <- finishedRows) {
            for (x <- 0 until gridWidth) {
                // Lines are cleared by setting all their index value to 0.
                grid(i)(x).value = 0
            }

            // Move all previous blocks downwards after clearing the line below.
            for (y <- i - 1 to 0 by -1) {
                for (x <- 0 until gridWidth) {
                    if (grid(y)(x).value == 1) {
                        grid(y)(x).value = 0
                        grid(y + 1)(x).value = 1
                        grid(y + 1)(x).color = grid(y)(x).color
                    }
                }
            }

            cleared = true
        }

        cleared
    } 
}