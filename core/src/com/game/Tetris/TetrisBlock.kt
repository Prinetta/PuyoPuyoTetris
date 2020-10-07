package com.game.Tetris
import com.badlogic.gdx.graphics.g2d.TextureRegion


class TetrisBlock(var column: Int, var row: Int, var texture: TextureRegion) {

    fun setPosition(x: Int, y: Int) {
        this.column = x
        this.row = y
    }
}