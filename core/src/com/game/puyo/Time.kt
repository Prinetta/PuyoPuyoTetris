package com.game.puyo

class Time (var delay: Int){
    var currentTime = System.currentTimeMillis()

    fun reset(){
        currentTime = System.currentTimeMillis()
    }

    fun hasPassed(): Boolean{
        return System.currentTimeMillis() - currentTime > delay
    }

    fun fastForwardBy(millis: Long) {
        currentTime -= millis
    }

    fun startAt(millis: Long) {
        reset()
        fastForwardBy(millis)
    }
}