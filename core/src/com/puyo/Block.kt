package com.puyo

class Block(var x: Int, var y: Int, val color: PuyoColors){
    var falling = true
    var marked = false
    val sprites = PuyoSprites.valueOf(color.toString()).sprite
    var currentSprite = sprites.hashMap["main"]
}