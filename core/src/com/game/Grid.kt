package com.game

import com.game.puyo.PuyoBlock

class Grid (val width: Int, val length: Int){
    val array = Array(width) {Array<PuyoBlock?>(length) {null} }
}
