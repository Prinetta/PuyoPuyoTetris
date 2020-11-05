package com.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.utils.Array
import java.io.File

class GifAnimation(source: String, frames: Int, delay: Float) {
    private var animation: Animation<Texture>
    private var playTime = 0f

    init {
        val images = Array<Texture>()

        val location = "animations/$source/frame"
        val type = if(File("core/assets/$location (1).gif").exists()) ".gif" else ".png"

        for (i in 1..frames){
            images.add(Texture(Gdx.files.internal("$location ($i)$type")))
        }
        animation = Animation(delay, images, Animation.PlayMode.LOOP)
    }

    fun update(delta: Float) : Texture{
        playTime += delta
        return animation.getKeyFrame(playTime, true)
    }
}