package com.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.utils.Array

class GifAnimation(var source: String, val frames: Int, val delay: Float) {
    private var animation: Animation<Texture>
    private var playTime = 0f

    init {
        val images = Array<Texture>()
        for (i in 1..frames){
            images.add(Texture(Gdx.files.internal("animations/$source/frame ($i).gif")))
        }
        animation = Animation(delay, images, Animation.PlayMode.LOOP)
    }

    fun update(delta: Float) : Texture{
        playTime += delta
        return animation.getKeyFrame(playTime, true)
    }
}