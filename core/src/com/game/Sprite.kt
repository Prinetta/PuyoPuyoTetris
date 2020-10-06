package com.game

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion

abstract class Sprite(pack: String) {
    protected val atlas = TextureAtlas(pack)
    abstract var hashMap : HashMap<String, TextureRegion>
}