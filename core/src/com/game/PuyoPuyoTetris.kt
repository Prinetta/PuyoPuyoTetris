package com.game

import com.badlogic.gdx.Audio
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator


class PuyoPuyoTetris : Game() {
    lateinit var batch: SpriteBatch
    lateinit var menuGif: GifAnimation
    lateinit var bgGif: GifAnimation
    lateinit var manager: AssetManager
    lateinit var titleBgm: Music
    lateinit var bgm: Music

    override fun create() {
        batch = SpriteBatch()
        manager = AssetManager()
        bgm = Gdx.audio.newMusic(Gdx.files.internal("music/wood.mp3"))
        titleBgm = Gdx.audio.newMusic(Gdx.files.internal("music/title.mp3"))
        //menuGif = GifAnimation(this, "menu", 100, 0.075f)
        //bgGif = GifAnimation(this, "bg", 121, 0.1f)
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