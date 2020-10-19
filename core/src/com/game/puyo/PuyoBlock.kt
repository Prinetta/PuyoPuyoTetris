package com.game.puyo
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.game.Block

class PuyoBlock(x: Int, y: Int, val color : PuyoColor) : Block(x, y){
    val sprites = PuyoSprite.valueOf(color.toString()).sprites!!
    override var currentSprite = sprites["main"]!!
    val pop = PopAnimation()
    var bounceFrame = 0
    val bounceDelay = Time(5)
    var bounceOver = true
    var firstBounce = true
    var width = 1f
    var length = 1f

    fun updateSprite(name: String){
        currentSprite = sprites.getOrDefault(name, sprites["main"]!!)
    }
}