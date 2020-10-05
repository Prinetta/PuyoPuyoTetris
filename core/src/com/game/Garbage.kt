package com.game

class Garbage {
    companion object Converters {
        val tetrisToPuyo = hashMapOf(
            1 to 4, 2 to 5, 3 to 6, 4 to 8, 5 to 10, 6 to 13, 7 to 16, 8 to 20, 9 to 24, 10 to 28,
            11 to 33, 12 to 28, 13 to 43, 14 to 49, 15 to 55, 16 to 61, 17 to 68, 18 to 75, 19 to 83, 20 to 92,
            21 to 102, 22 to 113, 23 to 125, 24 to 138, 25 to 152, 26 to 167, 27 to 183, 28 to 200, 29 to 218, 30 to 237)
        val puyoToTetris = tetrisToPuyo.map { (tetris, puyo) -> puyo to tetris }.toMap()
    }
}