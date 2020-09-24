package com.puyo

class Puyo(val first: Block, val second: Block, val puyoColor: PuyoColors){
    var rotateCount = 0
    var dropped = false
    val color = puyoColor.color

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