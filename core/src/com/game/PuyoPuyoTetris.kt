package com.game

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
        //screen = GameScreen(this)
        screen = MenuScreen(this)
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

    fun generateTetrisNextFont(size: Int): BitmapFont {
        val generator = FreeTypeFontGenerator(Gdx.files.internal("Vera.ttf"))
        val param = FreeTypeFontGenerator.FreeTypeFontParameter()
        param.size = size
        param.shadowColor = Color.BLACK;
        param.shadowOffsetX = 3;
        param.shadowOffsetY = 3;


        val font = generator.generateFont(param)
        generator.dispose()

        return font
    }

}