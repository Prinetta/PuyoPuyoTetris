package com.game

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion

abstract class Sprite(pack: String) {
    protected val atlas = TextureAtlas(pack)
    abstract var hashMap : HashMap<String, TextureRegion>

    fun get(key: String) : TextureRegion{
        return if(hashMap[key] == null){
            hashMap["main"]!!
        } else {
            hashMap[key]!!
        }
    }
}