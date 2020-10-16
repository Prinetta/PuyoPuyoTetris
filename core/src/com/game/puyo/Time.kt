package com.game.puyo

class Time (var delay: Int){
    var currentTime = System.currentTimeMillis()

    fun reset(){
        currentTime = System.currentTimeMillis()
    }

    fun hasPassed(): Boolean{
        return System.currentTimeMillis() - currentTime > delay
    }
}