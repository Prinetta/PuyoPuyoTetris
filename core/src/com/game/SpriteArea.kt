package com.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.game.puyo.PuyoColor
import java.util.function.Consumer

class SpriteArea {
    companion object Sprite {
        private val atlas = TextureAtlas("sprites.pack")
        private val guiAtlas = TextureAtlas("gui.pack")
        private val tEffectAtlas = TextureAtlas("effects.atlas")

        val tetrisSprites = hashMapOf("blue" to atlas.findRegion("tblue"), "dark-blue" to atlas.findRegion("tdarkblue"),
                "green" to atlas.findRegion("tgreen"), "orange" to atlas.findRegion("torange"), "purple" to atlas.findRegion("tpurple"),
                "red" to atlas.findRegion("tred"), "yellow" to atlas.findRegion("tyellow"), "garbage" to atlas.findRegion("tgarbage"))

        val tEffectSprites = hashMapOf("big-twinkle" to tEffectAtlas.findRegion("big-twinkle"),
                "blue-particle" to tEffectAtlas.findRegion("blue-particle"), "blue-twinkle" to tEffectAtlas.findRegion("blue-twinkle"),
                "erase-big" to tEffectAtlas.findRegion("erase-big"), "erase-small" to tEffectAtlas.findRegion("erase-small"),
                "full-line" to tEffectAtlas.findRegion("full-line"), "green-twinkle" to tEffectAtlas.findRegion("green-twinkle"),
                "hdrop-line1" to tEffectAtlas.findRegion("hdrop-line1"), "hdrop-line2" to tEffectAtlas.findRegion("hdrop-line2"),
                "hdrop-line3" to tEffectAtlas.findRegion("hdrop-line3"), "hdrop-line4" to tEffectAtlas.findRegion("hdrop-line4"),
                "hdrop-long-line" to tEffectAtlas.findRegion("hdrop-long-line"), "hdrop-long-shine" to tEffectAtlas.findRegion("hdrop-long-shine"),
                "hdrop-shine" to tEffectAtlas.findRegion("hdrop-shine"), "hdrop1" to tEffectAtlas.findRegion("hdrop1"),
                "hdrop2" to tEffectAtlas.findRegion("hdrop2"), "hdrop3" to tEffectAtlas.findRegion("hdrop3"),
                "hdrop4" to tEffectAtlas.findRegion("hdrop4"), "light-blue-twinkle" to tEffectAtlas.findRegion("light-blue-twinkle"),
                "pink-twinkle" to tEffectAtlas.findRegion("pink-twinkle"), "sdrop1" to tEffectAtlas.findRegion("sdrop1"),
                "sdrop2" to tEffectAtlas.findRegion("sdrop2"), "sdrop3" to tEffectAtlas.findRegion("sdrop3"),
                "sdrop4" to tEffectAtlas.findRegion("sdrop4"), "twinkle" to tEffectAtlas.findRegion("twinkle"),
                "white-particle-l" to tEffectAtlas.findRegion("white-particle-l"), "white-particle-s" to tEffectAtlas.findRegion("white-particle-s"),
                "x-twinkle" to tEffectAtlas.findRegion("x-twinkle"))

        val bgSprites = hashMapOf("next-field" to guiAtlas.findRegion("next-field"),
                "hold-field" to guiAtlas.findRegion("hold-field"), "next-field-sec" to guiAtlas.findRegion("next-field-sec"),
                "grid-bg" to guiAtlas.findRegion("gridbg"), "next-bg" to guiAtlas.findRegion("nextbg2"), "next2-bg" to guiAtlas.findRegion("nextbg2"),
                "hold-bg" to guiAtlas.findRegion("holdbg"), "puyo-bg" to guiAtlas.findRegion("puyobg"),
                "press-button" to guiAtlas.findRegion("pressbutton"), "title" to guiAtlas.findRegion("title"),
                "loading" to guiAtlas.findRegion("loading"), "loading-dark" to guiAtlas.findRegion("loadingdark"), "loading-darker" to guiAtlas.findRegion("loadingdarker"))


        val gameSprites = hashMapOf<String, TextureRegion>("garbage-queue1" to atlas.findRegion("g1"), "garbage-queue6" to atlas.findRegion("g6"),
                "garbage-queue30" to atlas.findRegion("g30"), "garbage-queue180" to atlas.findRegion("g180"), "garbage-queue360" to atlas.findRegion("g360"),
                "garbage-queue720" to atlas.findRegion("g720"), "garbage-queue1440" to atlas.findRegion("g1440"),
                "tgarbage-queue1" to atlas.findRegion("garbage1"), "tgarbage-queue6" to atlas.findRegion("garbage2"),
                "tgarbage-queue30" to atlas.findRegion("garbage3"),
                "pgarbage" to atlas.findRegion("pg"), "pgarbage-shine1" to atlas.findRegion("pg3"), "pgarbage-shine2" to atlas.findRegion("pg4"),
                "x1" to atlas.findRegion("cross1"), "x2" to atlas.findRegion("cross2"), "x3" to atlas.findRegion("cross3"),
                "x4" to atlas.findRegion("cross4"), "x5" to atlas.findRegion("cross5"),
                "pchain" to guiAtlas.findRegion("pchain"), "p0" to guiAtlas.findRegion("p0"), "p1" to guiAtlas.findRegion("p1"), "p2" to guiAtlas.findRegion("p2"),
                "p3" to guiAtlas.findRegion("p3"), "p4" to guiAtlas.findRegion("p4"), "p5" to guiAtlas.findRegion("p5"),
                "p6" to guiAtlas.findRegion("p6"), "p7" to guiAtlas.findRegion("p7"), "p8" to guiAtlas.findRegion("p8"), "p9" to guiAtlas.findRegion("p9"),
                "tcombo" to guiAtlas.findRegion("tcombo"), "tcombo1" to guiAtlas.findRegion("tcombo1"),
                "tcombo2" to guiAtlas.findRegion("tcombo2"), "tcombo3" to guiAtlas.findRegion("tcombo3"),
                "tcombo4" to guiAtlas.findRegion("tcombo4"), "tcombo5" to guiAtlas.findRegion("tcombo5"),
                "tcombo6" to guiAtlas.findRegion("tcombo6"), "tcombo7" to guiAtlas.findRegion("tcombo7"),
                "tcombo8" to guiAtlas.findRegion("tcombo8"), "tcombo9" to guiAtlas.findRegion("tcombo9"),
                "tcombo0" to guiAtlas.findRegion("tcombo0"), "tetris" to guiAtlas.findRegion("tetris"),
                "b2b" to guiAtlas.findRegion("backtoback"), "tspin" to guiAtlas.findRegion("tspin"),
                "tspinmini" to guiAtlas.findRegion("tspinmini"), "tspinsingle" to guiAtlas.findRegion("tspinsingle"),
                "tspindouble" to guiAtlas.findRegion("tspindouble"), "tspintriple" to guiAtlas.findRegion("tspintriple"),
                "tspinsinglemini" to guiAtlas.findRegion("tspinsinglemini"),
                "cd1" to guiAtlas.findRegion("cd1"), "cd2" to guiAtlas.findRegion("cd2"), "cd3" to guiAtlas.findRegion("cd3"),
                "cd-start" to guiAtlas.findRegion("cdstart"), "winner" to guiAtlas.findRegion("winner"),
                "loser" to guiAtlas.findRegion("loser"), "btt" to guiAtlas.findRegion("btt"),
                "btt-dark" to guiAtlas.findRegion("btt-dark"), "rematch" to guiAtlas.findRegion("rematch"),
                "rematch-dark" to guiAtlas.findRegion("rematch-dark"))


        val puyoSprites = createPuyoSprites()
        val cutPuyoSprites = createCutPuyoSprites()

        private fun createPuyoSprites() : MutableMap<PuyoColor, HashMap<String, TextureRegion>> {
            val hashMap = mutableMapOf<PuyoColor, HashMap<String, TextureRegion>>()
            for(color in PuyoColor.values()){
                hashMap[color] = getColorSprites(color)
            }
            return hashMap
        }

        private fun createCutPuyoSprites(): MutableMap<TextureRegion, TextureRegion>{
            val hashMap = mutableMapOf<TextureRegion, TextureRegion>()
            for(color in PuyoColor.values()){
                for(sprite in puyoSprites[color]!!.values){
                    hashMap[sprite] = TextureRegion(sprite, 0, PC.CELL_SIZE.toInt() / 2, PC.CELL_SIZE.toInt(), PC.CELL_SIZE.toInt() / 2)
                }
            }
            atlas.textures.forEach {it.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)}
            guiAtlas.textures.forEach {it.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)}
            return hashMap
        }

        private fun getColorSprites(color: PuyoColor) : HashMap<String, TextureRegion>{
            val colorLetter = if(color == PuyoColor.PURPLE) "p2" else color.toString().toLowerCase()[0].toString()
            return hashMapOf("main" to atlas.findRegion(colorLetter), "u" to atlas.findRegion("${colorLetter}u"), // oof
                    "d" to atlas.findRegion("${colorLetter}d"), "l" to atlas.findRegion("${colorLetter}l"), "r" to atlas.findRegion("${colorLetter}r"),
                    "rl" to atlas.findRegion("${colorLetter}h"), "ud" to atlas.findRegion("${colorLetter}v"), "urdl" to atlas.findRegion("${colorLetter}a"),
                    "rd" to atlas.findRegion("${colorLetter}rd"), "ur" to atlas.findRegion("${colorLetter}ur"), "dl" to atlas.findRegion("${colorLetter}dl"),
                    "ul" to atlas.findRegion("${colorLetter}ul"), "udl" to atlas.findRegion("${colorLetter}udl"), "rdl" to atlas.findRegion("${colorLetter}rdl"),
                    "url" to atlas.findRegion("${colorLetter}url"), "urd" to atlas.findRegion("${colorLetter}urd"), "s" to atlas.findRegion("${colorLetter}s"),
                    "dot" to atlas.findRegion("${colorLetter}dot"), "p" to atlas.findRegion("${colorLetter}p"), "p2" to atlas.findRegion("${colorLetter}p2"))
        }
    }
}