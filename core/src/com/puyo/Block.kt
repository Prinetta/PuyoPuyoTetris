package com.puyo

import com.badlogic.gdx.graphics.Color

class Block(var x: Int, var y: Int, val color: PuyoColors){
    var falling = true
    var marked = false
}