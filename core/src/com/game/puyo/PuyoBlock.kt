package com.game.puyo

import com.game.Block

class PuyoBlock(x: Int, y: Int, val color: PuyoColor) : Block(x, y){
    var isFalling = true
    var marked = false
    val sprites = ColorSprite.valueOf(color.toString()).sprite
    var currentSprite = sprites.hashMap["main"]
    var flicker = 0
    var beingRemoved = false

    fun addFlicker(){
        flicker += if(flicker+1 > 10) -9 else 1
    }
}