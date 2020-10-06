package com.game.puyo

import com.game.Block

open class PuyoBlock(x: Int, y: Int, val color : PuyoColor) : Block(x, y){
    val sprites = PuyoSprite.valueOf(color.toString()).sprite
    override var currentSprite = sprites.get("main")
}