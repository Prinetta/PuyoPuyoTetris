package com.game

import com.game.puyo.Puyo
import com.game.puyo.Time
import java.lang.System.currentTimeMillis

class Timer {

    fun reset(time: Time){
        time.currentTime = currentTimeMillis()
    }

    fun hasPassed(time: Time): Boolean{
        return currentTimeMillis() - time.currentTime > time.delay
    }
}