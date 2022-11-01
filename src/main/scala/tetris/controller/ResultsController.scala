package tetris.controller

import scalafxml.core.macros.sfxml
import tetris.MainApp
import scalafx.application.Platform
import scalafx.scene.text.Text
import scalafx.scene.text.Font

@sfxml
class ResultsController(private val score: Text) {
    // Retrieves and assigns final score to the score text field to be displayed.
    score.text = MainApp.board.score.toString

    // Returns to game menu page when user clicks main menu button.
    def handleMainMenu(): Unit = {
        MainApp.showMenu()
    }

    // Exits program when user clicks exit button.
    def handleQuit(): Unit = {
        Platform.exit()
    }
}