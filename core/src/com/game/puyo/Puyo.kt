package com.game.puyo

class Puyo(val first: PuyoBlock, val second: PuyoBlock){
    var rotateCount = 0
    var dropped = false

    val minSpeed = 600 //600
    val maxSpeed = 50
    var speed = minSpeed
    val chainSpeed = 800
    var gap = 0.5f

    fun canSpawn() : Boolean{
        return bothDropped()
    }

    fun updateRotationCount(rotation: Int){
        if(rotation > 0){
            rotateCount += if (rotateCount >= 4) -3 else 1 // loop after 4
        } else {
            rotateCount += if (rotateCount <= 0) 3 else -1 // loop after 0
        }
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