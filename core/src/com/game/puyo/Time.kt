package com.game.puyo

class Time (var delay: Int){
    var currentTime = System.currentTimeMillis()

    fun reset(){
        currentTime = System.currentTimeMillis()
    }


    fun copy(): Time{
        val time = Time(delay)
        time.currentTime = currentTime
        return time
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

    fun runtime() = System.currentTimeMillis() - currentTime

    fun cancel() {
        currentTime = Long.MAX_VALUE
    }

    fun isRunning() = currentTime != Long.MAX_VALUE
}