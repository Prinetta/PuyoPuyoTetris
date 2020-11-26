package com.game

class GC {

    companion object Constants {
        val garbageSteps = listOf(1, 6, 30, 180, 360, 720, 1440)

        const val WIN_LABEL_HEIGHT: Float = 3.25f * TC.CELL_SIZE
        const val WIN_LABEL_WIDTH: Float = 3.362f * WIN_LABEL_HEIGHT
        const val LOSE_LABEL_HEIGHT: Float = 2.75f * TC.CELL_SIZE
        const val LOSE_LABEL_WIDTH: Float = 3.1078f * LOSE_LABEL_HEIGHT

        const val GAMEOVER_LABEL_WIDTH: Float = 10f * TC.CELL_SIZE
        const val GAMEOVER_LABEL_HEIGHT: Float = GAMEOVER_LABEL_WIDTH * 0.222f
    }
}