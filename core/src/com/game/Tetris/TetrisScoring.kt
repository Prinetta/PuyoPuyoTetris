package com.game.Tetris

class TetrisScoring() {

    var puyoGarbage: Int = 0
    var tetrisGarbage: Int = 0
    var tetrisScore: Int = 0

    val perfectClearBonus = 6
    val clearBonus: HashMap<Int, Int> = createClearBonus()
    val tSpinClearBonus: HashMap<Int, Int> = createTSpinClearBonus()

    val scoreTSpinZeroBonus = 400
    val scoreComboBonus = 50
    val scoreClearBonus: HashMap<Int, Int> = createScoreClearBonus()
    val scorePerfectClearBonus: HashMap<Int, Int> = createScorePerfectClearBonus()
    val scoreTSpinClearBonus: HashMap<Int, Int> = createScoreTSpinClearBonus()

    private fun createClearBonus(): HashMap<Int, Int> {
        return hashMapOf(1 to 0, 2 to 1, 3 to 2, 4 to 4)
    }

    private fun createTSpinClearBonus(): HashMap<Int, Int> {
        return hashMapOf(1 to 2, 2 to 3, 3 to 4)
    }

    fun getComboBonus(combos: Int): Int {
        if (combos < 2) return 0
        else if (combos < 7) return (combos - 1)
        else if (combos < 11) return (5 * 1) + ((combos - 6) * 2)
        else if (combos < 15) return (5 * 1) + (4 * 2) + ((combos - 10) * 3)
        else if (combos < 19) return (5 * 1) + (4 * 2) + (4 * 3) + ((combos - 14) * 4)
        else return (5 * 1) + (4 * 2) + (4 * 3) + (4 * 4) + ((combos - 18) * 5)
    }

    private fun createScoreClearBonus(): HashMap<Int, Int> {
        return hashMapOf(1 to 100, 2 to 300, 3 to 500, 4 to 800)
    }

    private fun createScorePerfectClearBonus(): HashMap<Int, Int> {
        return hashMapOf(1 to 800, 2 to 1200, 3 to 1800, 4 to 2000)
    }

    private fun createScoreTSpinClearBonus(): HashMap<Int, Int> {
        return hashMapOf(1 to 800, 2 to 1200, 3 to 1600)
    }

    fun getScoreString(): String {
        return "${"0".repeat(8-tetrisScore.toString().length)}$tetrisScore"
    }
}