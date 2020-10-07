package com.game.puyo
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.game.Block

open class PuyoBlock(x: Int, y: Int, val color : PuyoColor) : Block(x, y){
    val sprites = PuyoSprite.valueOf(color.toString()).sprites!!
    override var currentSprite = sprites["main"]!!

    fun updateSprite(name: String){
        currentSprite = sprites.getOrDefault(name, sprites["main"]!!)
    }
}