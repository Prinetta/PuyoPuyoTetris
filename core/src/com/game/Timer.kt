package com.game

import com.game.puyo.Puyo
import java.lang.System.currentTimeMillis

class Timer {
    private var lastInputTime = currentTimeMillis()
    private var lastChainTime = currentTimeMillis()
    private var lastBlockDropTime = currentTimeMillis()
    private var lastGarbageDropTime = currentTimeMillis()
    private var lastPuyoDropTime = currentTimeMillis()

    fun resetGarbageDropTime(){
        lastGarbageDropTime = currentTimeMillis()
    }

    fun resetInputTime(){
        lastInputTime = currentTimeMillis();
    }

    fun resetChainTime(){
        lastChainTime = currentTimeMillis();
    }

    fun resetBlockDropTime(){
        lastBlockDropTime = currentTimeMillis();
    }

    fun resetPuyoDropTime(){
        lastPuyoDropTime = currentTimeMillis();
    }

    fun hasGarbageTimePassed(): Boolean {
        return currentTimeMillis() - lastGarbageDropTime > 50
    }

    fun hasInputTimePassed(): Boolean{
        return currentTimeMillis() - lastInputTime > 50
    }

    fun hasChainTimePassed(puyo: Puyo): Boolean{
        return currentTimeMillis() - lastChainTime > puyo.chainSpeed
    }

    fun hasBlockDropTimePassed(puyo: Puyo): Boolean {
        return currentTimeMillis() - lastBlockDropTime > puyo.speed
    }

    fun hasPuyoDropTimePassed(puyo: Puyo): Boolean {
        return currentTimeMillis() - lastPuyoDropTime > puyo.speed
    }
}