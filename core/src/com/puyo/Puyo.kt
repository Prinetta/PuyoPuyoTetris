package com.puyo

import com.badlogic.gdx.graphics.Color

class Puyo(val first: Block, val second: Block, val color: Color){
    var rotateCount = 0
    var dropped = false

    fun addRotationCount(){
        rotateCount += if (rotateCount >= 4) -3 else 1 // loop after 4
    }

    fun removeRotationCount(){
        rotateCount += if (rotateCount <= 0) 3 else -1 // loop after 0
    }

    fun bothDropped(): Boolean{
        if(!first.falling && !second.falling){
            dropped = true
        }
        return dropped
    }

    fun startedDrop(): Boolean{
        return !first.falling || !second.falling
    }
}