package com.game


class TC {

    companion object Constants {
        const val COLUMNS: Int = 10
        const val ROWS: Int = 25

        const val CELL_SIZE: Float = 32.5f
        const val GRID_LEFT_X: Float = (SCREEN_WIDTH - (CELL_SIZE * COLUMNS)) / 1.33f
        const val GRID_TOP_Y: Float = SCREEN_HEIGHT - ((SCREEN_HEIGHT - (CELL_SIZE * (ROWS - 1))) / 2)
        const val GRID_RIGHT_X: Float = GRID_LEFT_X + CELL_SIZE * COLUMNS
        const val NEXT_BLOCK_FIELD_X: Float = GRID_RIGHT_X + (CELL_SIZE * 0.7f) // next block field
        const val NEXT_BLOCK_FIELD_Y: Float = GRID_TOP_Y - (CELL_SIZE * 4.5f)
        const val NEXT_BLOCK_FIELD2_Y: Float = NEXT_BLOCK_FIELD_Y - (CELL_SIZE * 3) // 2nd next block field
        const val NEXT_BLOCK_FIELD2_TOP_Y: Float = NEXT_BLOCK_FIELD2_Y + (2.5f * CELL_SIZE)
        const val HOLD_FIELD_WIDTH: Float = 4.5f * CELL_SIZE
        const val HOLD_FIELD_X: Float = GRID_LEFT_X - HOLD_FIELD_WIDTH - (CELL_SIZE * 0.5f)
    }
}