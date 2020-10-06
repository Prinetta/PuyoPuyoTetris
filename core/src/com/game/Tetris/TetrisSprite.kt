package com.game.Tetris

import com.badlogic.gdx.graphics.g2d.TextureRegion

enum class TetrisSprite(val sprite: TextureRegion) {
    BLUE(TetrisArea.hashMap["blue"]!!),
    DARKBLUE(TetrisArea.hashMap["dark-blue"]!!),
    GREEN(TetrisArea.hashMap["green"]!!),
    ORANGE(TetrisArea.hashMap["orange"]!!),
    PURPLE(TetrisArea.hashMap["purple"]!!),
    RED(TetrisArea.hashMap["red"]!!),
    YELLOW(TetrisArea.hashMap["yellow"]!!)
}