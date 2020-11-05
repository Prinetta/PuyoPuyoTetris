package com.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.utils.Array
import java.io.File

class GifAnimation(val game: PuyoPuyoTetris, source: String, private val frames: Int, private val delay: Float) {
    private lateinit var animation: Animation<Texture>
    private var playTime = 0f
    private var location: String = "animations/$source/frame"
    private var type: String
    private lateinit var images: Array<Texture>

    init {
        type = if(File("core/assets/$location (1).gif").exists()) ".gif" else ".png"

        for (i in 1..frames){
            game.manager.load("$location ($i)$type", Texture::class.java)
        }
    }

    fun load(){
        images = Array()
        for(i in 1..frames){
            images.add(game.manager.get(("$location ($i)$type")))
        }
        animation = Animation(delay, images, Animation.PlayMode.LOOP)
    }

    fun update(delta: Float) : Texture{
        playTime += delta
        return animation.getKeyFrame(playTime, true)
    }
}