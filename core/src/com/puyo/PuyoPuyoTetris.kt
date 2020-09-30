package com.puyo

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch


class PuyoPuyoTetris : Game() {
    lateinit var font: BitmapFont
    lateinit var batch: SpriteBatch

    override fun create() {
        font = BitmapFont()
        batch = SpriteBatch()
        screen = PuyoScreen(this)
    }

}