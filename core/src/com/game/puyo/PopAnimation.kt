package com.game.puyo

import com.game.PC

class PopAnimation {
    var frames = 0
    val coords = Array(4){ arrayOf(0f, 0f)}
    var firstSize = PC.CELL_SIZE*0.5f
    var secondSize = PC.CELL_SIZE*0.65f

    fun update(){
        if(frames in 1..2){
            updateFirst(PC.CELL_SIZE * 0.1f, PC.CELL_SIZE * 0.53f,
                    coords[0][0] + firstSize * 0.72f, PC.CELL_SIZE * 0.52f,firstSize)
        }
        if(frames in 2..3){
            updateSecond(coords[0][0] - firstSize * 0.45f, coords[0][1] + firstSize * 0.4f,
                    coords[1][0] + firstSize * 0.2f, coords[1][1] + firstSize * 0.35f, secondSize)
        }
        if(frames == 3){
            updateFirst(coords[2][0] - secondSize * 0.2f, coords[2][1] + secondSize * 0.26f,
                    coords[3][0] + secondSize * 0.2f, coords[3][1] + secondSize * 0.26f, PC.CELL_SIZE * 0.7f)
        }
        when (frames) {
            4 -> {
                updateSecond(coords[2][0] - secondSize * 0.2f, coords[2][1] + secondSize * 0.26f,
                        coords[3][0] + secondSize * 0.2f, coords[3][1] + secondSize * 0.26f, PC.CELL_SIZE * 0.7f)
                updateFirst(coords[0][0] - secondSize * 0.2f, coords[0][1] + secondSize * 0.13f,
                        coords[1][0] + secondSize * 0.2f , coords[1][1] + secondSize * 0.11f, PC.CELL_SIZE * 0.77f)
            }
            5 -> {
                updateSecond(coords[0][0], coords[0][1], coords[1][0], coords[1][1], firstSize)
                updateFirst(coords[0][0] - secondSize * 0.1f, coords[0][1] + secondSize * 0.1f,
                        coords[1][0] + secondSize * 0.45f , coords[1][1] + secondSize * 0.1f, PC.CELL_SIZE * 0.6f)
            }
            6 -> {
                updateSecond(coords[2][0] - secondSize * 0.1f, coords[2][1] + secondSize * 0.1f,
                        coords[3][0] + secondSize * 0.1f, coords[3][1] + secondSize * 0.1f, PC.CELL_SIZE * 0.5f)
                updateFirst(coords[0][0] - secondSize * 0.25f, coords[0][1],
                        coords[1][0] + secondSize * 0.25f , coords[1][1], PC.CELL_SIZE * 0.4f)
            }
            7 -> {
                updateSecond(coords[2][0] - secondSize * 0.3f, coords[2][1] + secondSize * 0.1f,
                        coords[3][0] + secondSize * 0.2f, coords[3][1] + secondSize * 0.1f, PC.CELL_SIZE * 0.4f)
                updateFirst(coords[0][0] - secondSize * 0.35f, coords[0][1],
                        coords[1][0] + firstSize * 0.1f , coords[1][1], PC.CELL_SIZE * 0.3f)
            }
            8 -> {
                updateSecond(coords[2][0] - secondSize * 0.3f, coords[2][1] + secondSize * 0.1f,
                        coords[3][0] + secondSize * 0.15f, coords[3][1] + secondSize * 0.1f, PC.CELL_SIZE * 0.3f)
                updateFirst(coords[0][0] - secondSize * 0.3f, coords[0][1],
                        coords[1][0] + firstSize * 0.1f , coords[1][1], PC.CELL_SIZE * 0.2f)
            }
        }

        frames++
    }

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