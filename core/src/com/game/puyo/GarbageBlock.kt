package com.game.puyo
import com.game.Block

class GarbageBlock(x: Int, y: Int) : Block(x, y) {
    override var currentSprite = PuyoSprite.BLUE.sprite.hashMap["pg"]!!
}