package com.puyo
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Skin

class Sprite (color: PuyoColors) {
    val atlas = TextureAtlas("sprites.pack")
    lateinit var main: TextureRegion
    lateinit var up: TextureRegion
    lateinit var down: TextureRegion
    lateinit var left: TextureRegion
    lateinit var right: TextureRegion

    init{
        main = atlas.findRegion(color.toString().toLowerCase())
        up = atlas.findRegion("${color.toString().toLowerCase()}-up")
        down = atlas.findRegion("${color.toString().toLowerCase()}-down")
        left = atlas.findRegion("${color.toString().toLowerCase()}-left")
        right = atlas.findRegion("${color.toString().toLowerCase()}-right")
    }
}
