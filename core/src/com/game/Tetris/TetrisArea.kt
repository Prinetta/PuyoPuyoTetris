package com.game.Tetris

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.game.Sprite
import com.game.puyo.PuyoColor

class TetrisArea {
    companion object Sprite {
        private val atlas = TextureAtlas("sprites.pack")
        var hashMap: HashMap<String, TextureRegion> =
                hashMapOf("blue" to atlas.findRegion("tblue"), "dark-blue" to atlas.findRegion("tdarkblue"),
                        "green" to atlas.findRegion("tgreen"), "orange" to atlas.findRegion("torange"),"purple" to atlas.findRegion("tpurple"),
                        "red" to atlas.findRegion("tred"), "yellow" to atlas.findRegion("tyellow"), "garbage" to atlas.findRegion("tgarbage"))
    }
}

