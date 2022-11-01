package tetris.controller

import scalafxml.core.macros.sfxml
import tetris.MainApp
import scalafx.application.Platform

@sfxml
class MenuController() {
    // Starts game when user clicks start button.
    def handleStart(): Unit = {
        MainApp.startGame()
    }

    // Displays how to play page when user clicks how to play button.
    def handleHowToPlay(): Unit = {
        MainApp.showHowToPlay()
    }

    // Exits program when user clicks quit button.
    def handleQuit(): Unit = {
        Platform.exit()
    }
}