package com.puyo

import com.badlogic.gdx.graphics.Color

class Puyo(val first: Block, val second: Block, val color: Color){
    var degrees = 0
    var dropped = false

    fun bothDropped(): Boolean{
        if(!first.falling && !second.falling){
            dropped = true
        }
        return dropped;
    }

    fun startedDrop(): Boolean{
        return !first.falling || !second.falling
    }
}