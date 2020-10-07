package com.game.Tetris

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.game.SpriteArea

enum class TetrisSprite(val sprite: TextureRegion) {
    BLUE(SpriteArea.tetrisSprites["blue"]!!),
    DARKBLUE(SpriteArea.tetrisSprites["dark-blue"]!!),
    GREEN(SpriteArea.tetrisSprites["green"]!!),
    ORANGE(SpriteArea.tetrisSprites["orange"]!!),
    PURPLE(SpriteArea.tetrisSprites["purple"]!!),
    RED(SpriteArea.tetrisSprites["red"]!!),
    YELLOW(SpriteArea.tetrisSprites["yellow"]!!)
}