package tetris.controller

import scalafxml.core.macros.sfxml
import tetris.MainApp
import scalafx.application.Platform

@sfxml
class HowToPlayController() {
    // Starts game when user clicks start button.
    def handleStart(): Unit = {
        MainApp.startGame()
    }

    // Closes program when user clicks close button.
    def handleClose(): Unit = {
        MainApp.showMenu()
    }
}