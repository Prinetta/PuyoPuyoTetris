package com.puyo

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture

class Block(var x: Int, var y: Int, val color: PuyoColors){
    var falling = true
    var marked = false
}