package com.puyo

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator


class PuyoPuyoTetris : Game() {
    lateinit var batch: SpriteBatch

    override fun create() {
        batch = SpriteBatch()
        screen = Screen(this)
    }

    fun generateTitleFont(size: Int) : BitmapFont {
        val generator = FreeTypeFontGenerator(Gdx.files.internal("Bubblegum.ttf"))
        val param = FreeTypeFontGenerator.FreeTypeFontParameter()
        param.size = size
        param.borderWidth = 4f
        param.borderColor = Color(233/255f, 33/255f, 153/255f, 1f)


        val font = generator.generateFont(param)
        generator.dispose()

        return font
    }

    fun generateScoreFont(size: Int) : BitmapFont {
        val generator = FreeTypeFontGenerator(Gdx.files.internal("Mont.ttf"))
        val param = FreeTypeFontGenerator.FreeTypeFontParameter()
        param.size = size
        param.borderWidth = 3f
        param.borderColor = Color.BLACK
        param.shadowColor = Color.BLACK;
        param.shadowOffsetX = 3;
        param.shadowOffsetY = 3;


        val font = generator.generateFont(param)
        generator.dispose()

        return font
    }

}