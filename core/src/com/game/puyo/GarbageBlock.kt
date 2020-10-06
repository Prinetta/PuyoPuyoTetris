package com.game.puyo

class GarbageBlock(x: Int, y: Int) : PuyoBlock(x, y, PuyoColor.BLUE) {
    override var currentSprite = ColorSprite.BLUE.sprite.get("g")

    fun set(x: Int, y: Int){
        this.x = x
        this.y = y
    }
}