package com.game

import com.badlogic.gdx.graphics.g2d.TextureRegion

abstract class Block(var x: Int, var y: Int) {
    abstract var currentSprite: TextureRegion
    var isFalling = true
}