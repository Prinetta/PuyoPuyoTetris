package com.puyo

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Array
import java.awt.Rectangle

class TetrisBlock(var column: Int, var row: Int, var texture: TextureRegion) {

    fun setPosition(x: Int, y: Int) {
        this.column = x
        this.row = y
    }
}