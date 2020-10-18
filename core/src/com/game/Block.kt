package com.game

import com.badlogic.gdx.graphics.g2d.TextureRegion

abstract class Block(var x: Int, var y: Int) {
    abstract var currentSprite: TextureRegion
    var marked = false
    var isFalling = true
    var isLocked = false
    var isBeingRemoved = false
    var removeFrames = 0
    var flickerCount = 0

    fun set(x: Int, y: Int){
        this.x = x
        this.y = y
    }

    fun addFrameCount(){
        removeFrames++
        flickerCount += if(flickerCount+1 > 10) -9 else 1
    }

}