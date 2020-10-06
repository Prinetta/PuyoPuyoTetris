package com.game

import com.badlogic.gdx.graphics.g2d.TextureRegion

abstract class Block(var x: Int, var y: Int) {
    abstract var currentSprite: TextureRegion
    var marked = false
    var isFalling = true
    var isBeingRemoved = false
    var flicker = 0

    fun set(x: Int, y: Int){
        this.x = x
        this.y = y
    }

    fun addFlicker(){
        flicker += if(flicker+1 > 10) -9 else 1
    }
}