package com.game

class PConstants {
    companion object Constants {
        const val GRID_WIDTH = 6
        const val GRID_LENGTH = 13
        const val CELL_SIZE = 65f

        const val GRID_START_X = SCREEN_WIDTH*0.1f
        const val GRID_START_Y = SCREEN_HEIGHT*0.13f + GRID_LENGTH*CELL_SIZE-CELL_SIZE
    }
}