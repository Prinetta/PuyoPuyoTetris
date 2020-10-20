package com.game


class TC {

    companion object Constants {
        const val COLUMNS: Int = 10
        const val ROWS: Int = 25

        const val CELL_SIZE: Float = 32.5f

        const val GRID_LEFT_X: Float = (SCREEN_WIDTH - (CELL_SIZE * COLUMNS)) / 1.25f
        const val GRID_TOP_Y: Float = SCREEN_HEIGHT - ((SCREEN_HEIGHT - (CELL_SIZE * (ROWS - 1))) / 2)
        const val GRID_BOTTOM_Y: Float = GRID_TOP_Y - ((ROWS - 1) * CELL_SIZE)
        const val GRID_RIGHT_X: Float = GRID_LEFT_X + CELL_SIZE * COLUMNS

        const val NEXT_BLOCK_FIELD_SIZE: Float = CELL_SIZE * 5f
        const val NEXT_BLOCK_FIELD_X: Float = GRID_RIGHT_X + (CELL_SIZE * 0.7f) // next block field
        const val NEXT_BLOCK_FIELD_Y: Float = GRID_TOP_Y - NEXT_BLOCK_FIELD_SIZE

        const val NEXT_BLOCK_FIELD2_HEIGHT: Float = CELL_SIZE * 2.75f
        const val NEXT_BLOCK_FIELD2_WIDTH: Float = 1.3f * (NEXT_BLOCK_FIELD2_HEIGHT)
        const val NEXT_BLOCK_FIELD2_Y: Float = NEXT_BLOCK_FIELD_Y - (CELL_SIZE * 3) // 2nd next block field
        const val NEXT_BLOCK_FIELD2_TOP_Y: Float = NEXT_BLOCK_FIELD2_Y + NEXT_BLOCK_FIELD2_HEIGHT

        const val HOLD_FIELD_SIZE: Float = 4.5f * CELL_SIZE
        const val HOLD_FIELD_X: Float = GRID_LEFT_X - HOLD_FIELD_SIZE - (CELL_SIZE * 0.5f)

        const val COMBO_LABEL_WIDTH: Float = 5f * CELL_SIZE
        const val COMBO_LABEL_HEIGHT: Float = 0.585f * COMBO_LABEL_WIDTH
        const val COMBO_LABEL_LEFT_X: Float = GRID_LEFT_X - COMBO_LABEL_WIDTH - (COMBO_LABEL_WIDTH * 0.1f)
    }
}