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
        screen = GameScreen(this)
        //screen = MenuScreen(this)
    }

    fun generateTitleFont(size: Int) : Array<BitmapFont> {
        val puyoGenerator = FreeTypeFontGenerator(Gdx.files.internal("Bubblegum.ttf"))
        val tetrisGenerator = FreeTypeFontGenerator(Gdx.files.internal("Tetris.ttf"))

        val puyoParam = FreeTypeFontGenerator.FreeTypeFontParameter()
        puyoParam.size = size
        puyoParam.borderWidth = 4f
        puyoParam.borderColor = Color(233/255f, 33/255f, 153/255f, 1f)

        val tetrisParam = FreeTypeFontGenerator.FreeTypeFontParameter()
        tetrisParam.size = (size*1.3).toInt()
        tetrisParam.borderWidth = 2f
        tetrisParam.shadowOffsetY = 3
        tetrisParam.shadowOffsetX = 3

        return arrayOf(puyoGenerator.generateFont(puyoParam), tetrisGenerator.generateFont(tetrisParam))
        // still need to dispose
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