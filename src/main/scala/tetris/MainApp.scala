package tetris

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.text.{Text, Font}
import scalafx.scene.effect.DropShadow
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Rectangle, Path, MoveTo, LineTo}
import scalafx.animation. FadeTransition
import scalafx.util.Duration
import scalafx.scene.image.{Image, ImageView}

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.canvas.Canvas
import scalafx.scene.layout.BorderPane

import scala.util.Random
import scala.util.control.Breaks._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.scene.media.{Media, MediaPlayer, AudioClip}
import scalafx.geometry.Pos
import scalafx.scene.layout.BackgroundFill
import scalafxml.core.{NoDependencyResolver, FXMLView, FXMLLoader}
import javafx.{scene => jfxs}

//https://docs.oracle.com/javase/8/javafx/api/toc.htm
//https://www.scalafx.org/api/8.0/index.html#scalafx.scene.image.ImageView
//https://mvnrepository.com/artifact/org.scalafx/scalafx_2.12/8.0.192-R14

object MainApp extends JFXApp {
    // Transform path of RootLayout.fxml to URI for resource location.
    val rootResource = getClass.getResourceAsStream("view/RootLayout.fxml")

    // Initialize the loader object.
    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(rootResource);

    // Retrieve the root component BorderPane from the FXML 
    val roots = loader.getRoot[jfxs.layout.BorderPane]

    // Initialize the media and media player elements for game sounds.
    val backgroundAudio = new Media(getClass().getResource("/Sounds/TetrisBattleMusic.mp3").toExternalForm())
    val backgroundMusic = new MediaPlayer(backgroundAudio)
    val harddropSound = new AudioClip(getClass().getResource("/Sounds/harddrop.mp3").toExternalForm())
    val gameoverSound = new AudioClip(getClass().getResource("/Sounds/gameover.mp3").toExternalForm())
    val lineClearSound = new AudioClip(getClass().getResource("/Sounds/lineClear.mp3").toExternalForm())

    // Set background music on loop so that it runs infinitely.
    backgroundMusic.setOnEndOfMedia(new Runnable() {
        override def run(): Unit = {
            backgroundMusic.seek(Duration.ZERO);
        }
    });

    // Initialize game screen image for game scene.
    val gameScreen = new Image(getClass.getResourceAsStream("/Images/gameScreen.png"))
    val gameScreenIV = new ImageView(gameScreen) {
        fitWidth = 730
        fitHeight = 880
        x = 75
        y = 5
    }

    // Initialize canvas element for tetris classes to draw on.
    val canvas = new Canvas(802, 802);
    canvas.layoutX = 246
    canvas.layoutY = 75
    
    // Initialize board class and pass in canvas for board methods to draw on canvas. (Aggregation relationship)
    var board = new Board(10, 20, 37, 37, canvas)

    // Initialize lines sent or scoreboard in game scene.
    var linesSent = new Text(145, 640, "0") {
        id = "linesSent"
    }

    // Initialize a runnable so that second thread is able to interact with GUI thread.
    val gameOver = new Runnable {
        override def run(): Unit = {
            showResults()
        }
    }

    // Initialize stage.
    stage = new PrimaryStage() {
        title = "Tetris Battle"
        resizable = false
        icons += new Image(getClass.getResourceAsStream("/Images/menuLogo.png"))
    }

    // Initialize menu scene for fxml files.
    var menuScene = new Scene(width = 900, height = 880) {
        root = roots
        stylesheets += getClass.getResource("/Styles/main.css").toString()
    }

    // Initialize game scene for tetris and canvas.
    var gameScene = new Scene(width = 900, height = 880) {
        root = new BorderPane() {
            id = "borderPane"
        }
        
        stylesheets += getClass.getResource("/Styles/main.css").toString()

        content = Array(gameScreenIV, canvas, linesSent)
    }

    // Method for initializing keyboard controls for tetris game.
    def initializeControls(): Unit = {
        gameScene.onKeyPressed = { ke => 
            if (ke.code == KeyCode.LEFT) {
                board.moveBlockLeft()
            }
            else if (ke.code == KeyCode.RIGHT) {
                board.moveBlockRight()
            }
            else if (ke.code == KeyCode.UP) {
                board.rotateBlock()
            }
            else if (ke.code == KeyCode.SPACE) {
                board.placeBlock()
                harddropSound.play()
            }
            else if (ke.code == KeyCode.DOWN) {
                board.moveBlockDown()
            }
        }
    }

    // Method for starting the tetris game.
    def startGame(): Unit = {
        // Change stage scene to game scene, initialize score, controls, and draw board.
        stage.scene = gameScene
        linesSent.x = 145
        linesSent.text = "0"
        board.score = 0
        initializeControls()
        board.initialize()
        board.draw()
        board.spawnBlock()
        stage.show()

        // Have to run background processes in a different thread because the first thread is used by GUI.
        // New thread to run the while loop but platform.runlater to run the GUI manipulation in main thread.
        // Long-running tasks, like Thread.sleep and while loops should never be executed in GUI thread, 
        // since windows will hang, and the GUI will be frozen.
        val thread = new Thread {
            override def run(): Unit = {
                backgroundMusic.play()

                // While blocks are able to move down means the board is not full.
                while (board.canMoveDown()) {
                    // Draw block in board and draw next block at the side.
                    board.draw()
                    board.drawNextBlock()

                    // While current block can move down means it has not reached the bottom, keep going.
                    while (board.canMoveDown()) {
                        board.moveBlockDown()
                        // Thread sleep to make the block falling animations and block fall speed.
                        Thread.sleep(450)
                    }

                    // After block fallen, check if there are finished rows and clear them.
                    if (board.clearFinishedRows()) {
                        lineClearSound.play()

                        // If score is double digits, move x coordinate slightly to the left to align number.
                        if (board.score > 9) {
                            linesSent.x = 126
                        }

                        // Update score.
                        linesSent.text = board.score.toString
                    }

                    // Spawn new block.
                    board.spawnBlock()
                }

                backgroundMusic.stop()
                gameoverSound.play()
                Platform.runLater(gameOver)
            }
        }

        thread.start
    }

    // Displays the menu page.
    def showMenu() = {
        stage.scene = menuScene
        val resource = getClass.getResourceAsStream("view/Menu.fxml")
        val loader = new FXMLLoader(null, NoDependencyResolver)
        loader.load(resource);
        val roots = loader.getRoot[jfxs.layout.AnchorPane]
        this.roots.setCenter(roots)
    }

    // Displays the how to play page.
    def showHowToPlay() = {
        stage.scene = menuScene
        val resource = getClass.getResourceAsStream("view/HowToPlay.fxml")
        val loader = new FXMLLoader(null, NoDependencyResolver)
        loader.load(resource);
        val roots = loader.getRoot[jfxs.layout.AnchorPane]
        this.roots.setCenter(roots)
    }

    // Displays the results page.
    def showResults() = {
        stage.scene = menuScene
        val resource = getClass.getResourceAsStream("view/Results.fxml")
        val loader = new FXMLLoader(null, NoDependencyResolver)
        loader.load(resource);
        val roots = loader.getRoot[jfxs.layout.AnchorPane]
        this.roots.setCenter(roots)
    }

    // Makes stage startup at center of user's screen.
    stage.centerOnScreen()

    // Display menu when user first runs the application.
    showMenu()
}