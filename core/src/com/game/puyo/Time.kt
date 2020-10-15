package com.game.puyo

class Time (var currentTime: Long, var delay: Int){
    
    fun reset(){
        currentTime = System.currentTimeMillis()
    }

    fun hasPassed(): Boolean{
        return System.currentTimeMillis() - currentTime > delay
    }
}