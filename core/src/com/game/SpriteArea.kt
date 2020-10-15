package com.game

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.game.puyo.PuyoColor

class SpriteArea {
    companion object Sprite {
        val atlas = TextureAtlas("sprites.pack")
        private val guiAtlas = TextureAtlas("gui.pack")

        val tetrisSprites = hashMapOf("blue" to atlas.findRegion("tblue"), "dark-blue" to atlas.findRegion("tdarkblue"),
            "green" to atlas.findRegion("tgreen"), "orange" to atlas.findRegion("torange"),"purple" to atlas.findRegion("tpurple"),
            "red" to atlas.findRegion("tred"), "yellow" to atlas.findRegion("tyellow"), "garbage" to atlas.findRegion("tgarbage"))

        val bgSprites = hashMapOf("next-field" to guiAtlas.findRegion("next-field"),
             "hold-field" to guiAtlas.findRegion("hold-field"), "next-field-sec" to guiAtlas.findRegion("next-field-sec"),
             "grid-bg" to guiAtlas.findRegion("gridbg"), "next-bg" to guiAtlas.findRegion("nextbg2"), "next2-bg" to guiAtlas.findRegion("nextbg2"),
             "hold-bg" to guiAtlas.findRegion("holdbg"), "puyo-bg" to guiAtlas.findRegion("puyobg"))

        val puyoSprites = createPuyoSprites()
        val cutPuyoSprites = createCutPuyoSprites()

        val gameSprites = hashMapOf("garbage-queue1" to atlas.findRegion("g1"), "garbage-queue6" to atlas.findRegion("g6"),
                "garbage-queue30" to atlas.findRegion("g30"), "garbage-queue180" to atlas.findRegion("g180"),"garbage-queue360" to atlas.findRegion("g360"),
                "garbage-queue720" to atlas.findRegion("g720"), "garbage-queue1440" to atlas.findRegion("g1440"),
                "tgarbage-queue1" to atlas.findRegion("garbage1"), "tgarbage-queue6" to atlas.findRegion("garbage2"),
                "tgarbage-queue30" to atlas.findRegion("garbage3"))

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
                    hashMap[sprite] = TextureRegion(sprite, 0, PC.CELL_SIZE.toInt()/2, PC.CELL_SIZE.toInt(), PC.CELL_SIZE.toInt()/2)
                }
            }
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
                    "dot" to atlas.findRegion("${colorLetter}dot"), "g" to atlas.findRegion("pg"))
        }
    }
}