package com.game.puyo

import com.game.PC

class PopAnimation {
    var frames = 0
    val coords = Array(4){ arrayOf(0f, 0f)}
    var firstSize = PC.CELL_SIZE*0.5f
    var secondSize = PC.CELL_SIZE*0.65f

    fun updateFirst(x1: Float, y1:Float, x2: Float, y2: Float, size: Float){
        coords[0][0] = x1
        coords[0][1] = y1
        coords[1][0] = x2
        coords[1][1] = y2

        firstSize = size
    }

    fun updateSecond(x1: Float, y1:Float, x2: Float, y2: Float, size: Float){
        coords[2][0] = x1
        coords[2][1] = y1
        coords[3][0] = x2
        coords[3][1] = y2

        secondSize = size
    }
}