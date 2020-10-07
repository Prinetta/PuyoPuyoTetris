package com.game.puyo

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.game.SpriteArea

enum class PuyoSprite(val sprites: HashMap<String, TextureRegion>?) {
    BLUE(SpriteArea.puyoSprites[PuyoColor.BLUE]!!),
    YELLOW(SpriteArea.puyoSprites[PuyoColor.YELLOW]!!),
    PURPLE(SpriteArea.puyoSprites[PuyoColor.PURPLE]!!),
    PINK(SpriteArea.puyoSprites[PuyoColor.PINK]!!),
}