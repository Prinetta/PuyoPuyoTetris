package com.puyo

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.GridPoint2

class PuyoBlock(coords : GridPoint2, color : Color) : Block(coords, color) {
    val first = GridPoint2(coords.x, coords.y);
    val second = GridPoint2(coords.x, coords.y+1);
}