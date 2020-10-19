package com.game.puyo

class Puyo(val first: PuyoBlock, val second: PuyoBlock){
    var rotateCount = 0
    var isLocked = false
    var isMain = true
    val minSpeed = 600 //600
    val maxSpeed = 50
    var speed = minSpeed
    val chainSpeed = 900
    var gap = 0.5f

    fun canSpawn() : Boolean{
        return isLocked
    }

    fun updateRotationCount(rotation: Int){
        if(rotation > 0){
            rotateCount += if (rotateCount >= 4) -3 else 1 // loop after 4
        } else {
            rotateCount += if (rotateCount <= 0) 3 else -1 // loop after 0
        }
    }

    fun bothDropped(): Boolean{
        return !first.isFalling && !second.isFalling
    }

    fun startedDrop(): Boolean{
        return !first.isFalling || !second.isFalling
    }
}