package com.puyo
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion

// u = up, r = right, d = down, l = left, a = all, s = shocked
class Sprite (color: PuyoColors) {
    private val atlas = TextureAtlas("puyos.pack")
    var hashMap : HashMap<String, TextureRegion>

    init{
        val colorLetter = if(color == PuyoColors.PURPLE) "p2" else color.toString().toLowerCase()[0].toString()
        hashMap = hashMapOf("main" to atlas.findRegion(colorLetter), "u" to atlas.findRegion("${colorLetter}u"), // oof
        "d" to atlas.findRegion("${colorLetter}d"), "l" to atlas.findRegion("${colorLetter}l"), "r" to atlas.findRegion("${colorLetter}r"),
        "rl" to atlas.findRegion("${colorLetter}h"), "ud" to atlas.findRegion("${colorLetter}v"), "urdl" to atlas.findRegion("${colorLetter}a"),
        "rd" to atlas.findRegion("${colorLetter}rd"), "ur" to atlas.findRegion("${colorLetter}ur"), "dl" to atlas.findRegion("${colorLetter}dl"),
        "ul" to atlas.findRegion("${colorLetter}ul"), "udl" to atlas.findRegion("${colorLetter}udl"), "rdl" to atlas.findRegion("${colorLetter}rdl"),
        "url" to atlas.findRegion("${colorLetter}url"), "urd" to atlas.findRegion("${colorLetter}urd"), "s" to atlas.findRegion("${colorLetter}s"))
    }

    fun get(key: String) : TextureRegion{
        return if(hashMap[key] == null){
            hashMap["main"]!!
        } else {
            hashMap[key]!!
        }
    }
}
