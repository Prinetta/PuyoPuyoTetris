package com.game.Tetris

class TetrisScoring() {

    var score: Int = 0

    val perfectClearBonus = 6
    val clearBonus: HashMap<Int, Int> = createClearBonus()
    val tSpinClearBonus: HashMap<Int, Int> = createTSpinClearBonus()

    private fun createClearBonus(): HashMap<Int, Int> {
        return hashMapOf(1 to 0, 2 to 1, 3 to 2, 4 to 4)
    }

    private fun createTSpinClearBonus(): HashMap<Int, Int> {
        return hashMapOf(1 to 2, 2 to 3, 3 to 4)
    }
}