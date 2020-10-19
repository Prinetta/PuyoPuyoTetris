package com.game.puyo
import com.game.Block
import com.game.SpriteArea

class GarbageBlock(x: Int, y: Int) : Block(x, y) {
    val sprites = SpriteArea.gameSprites
    override var currentSprite = sprites["pgarbage"]!!
    var frame = 0
    var shineStart = Time(5000)
    var shineDuration = Time(100)

    fun changeSprite(){
        frame++
        when (frame) {
            1, 3 -> currentSprite = sprites["pgarbage-shine1"]!!
            2 -> currentSprite = sprites["pgarbage-shine2"]!!
            else -> {
                frame = 0
                currentSprite = sprites["pgarbage"]!!
            }
        }
    }
}