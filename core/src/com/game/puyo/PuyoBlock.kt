package com.game.puyo

class PuyoBlock(var x: Int, var y: Int, val color: PuyoColor){
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