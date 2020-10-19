package com.game.puyo

import com.game.SpriteArea

class CrossAnimation {
    val start = Time(400)
    val delay = Time(100)
    var frame = 0
    var currentSprite = SpriteArea.gameSprites["x1"]!!
    var flip = false

    fun updateSprite(){
        frame++
        flip = when(frame){
            in 1..3, in 9..12 -> true
            else              -> false
        }
        when(frame){
            1, 3, 5, 12 -> currentSprite = SpriteArea.gameSprites["x2"]!!
            2, 6, 11    -> currentSprite = SpriteArea.gameSprites["x3"]!!
            7, 10       -> currentSprite = SpriteArea.gameSprites["x4"]!!
            8, 9        -> currentSprite = SpriteArea.gameSprites["x5"]!!
            else        -> {
                           currentSprite = SpriteArea.gameSprites["x1"]!!
                           if(frame > 12){
                               frame = 0
                           }
            }
        }
    }
}