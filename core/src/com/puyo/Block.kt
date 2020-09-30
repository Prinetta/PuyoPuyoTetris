package com.puyo

class Block(var x: Int, var y: Int, val color: PuyoColors){
    var isFalling = true
    var marked = false
    val sprites = PuyoSprites.valueOf(color.toString()).sprite
    var currentSprite = sprites.hashMap["main"]
    var flicker = 0

    fun addFlicker(){
        flicker += if(flicker+1 > 20) -19 else 1
    }
}