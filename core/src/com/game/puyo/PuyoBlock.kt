package com.game.puyo

import com.game.Block

open class PuyoBlock(x: Int, y: Int, val color : PuyoColor) : Block(x, y){
    var marked = false
    val sprites = ColorSprite.valueOf(color.toString()).sprite
    override var currentSprite = sprites.get("main")
    var flicker = 0
    var beingRemoved = false

    fun addFlicker(){
        flicker += if(flicker+1 > 10) -9 else 1
    }
}