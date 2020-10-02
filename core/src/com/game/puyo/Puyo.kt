package com.game.puyo

class Puyo(val first: PuyoBlock, val second: PuyoBlock){
    var rotateCount = 0
    var dropped = false

    val minSpeed = 400
    val maxSpeed = 50
    var speed = minSpeed
    val chainSpeed = 500

    fun canSpawn() : Boolean{
        return bothDropped()
    }

    fun addRotationCount(){
        rotateCount += if (rotateCount >= 4) -3 else 1 // loop after 4
    }

    fun removeRotationCount(){
        rotateCount += if (rotateCount <= 0) 3 else -1 // loop after 0
    }

    fun bothDropped(): Boolean{
        if(!first.isFalling && !second.isFalling){
            dropped = true
        }
        return dropped
    }

    fun startedDrop(): Boolean{
        return !first.isFalling || !second.isFalling
    }
}