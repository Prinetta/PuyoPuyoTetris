package com.puyo
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion

// u = up, r = right, d = down, l = left, a = all, s = shocked
class Sprite (color: PuyoColors) {
    val atlas = TextureAtlas("puyos.pack")
    lateinit var main: TextureRegion
    lateinit var up: TextureRegion
    lateinit var down: TextureRegion
    lateinit var left: TextureRegion
    lateinit var right: TextureRegion
    lateinit var horizontal: TextureRegion
    lateinit var vertical: TextureRegion
    lateinit var all: TextureRegion
    lateinit var shocked: TextureRegion
    lateinit var rd: TextureRegion
    lateinit var ur: TextureRegion
    lateinit var dl: TextureRegion
    lateinit var ul: TextureRegion
    lateinit var udl: TextureRegion
    lateinit var rdl: TextureRegion
    lateinit var url: TextureRegion
    lateinit var urd: TextureRegion

    init{
        val colorLetter = if(color == PuyoColors.PURPLE) "p2" else color.toString().toLowerCase()[0].toString()
        main = atlas.findRegion(colorLetter)
        up = atlas.findRegion("${colorLetter}u")
        down = atlas.findRegion("${colorLetter}d")
        left = atlas.findRegion("${colorLetter}l")
        right = atlas.findRegion("${colorLetter}r")
        horizontal = atlas.findRegion("${colorLetter}h")
        vertical = atlas.findRegion("${colorLetter}v")
        all = atlas.findRegion("${colorLetter}a")
        shocked = atlas.findRegion("${colorLetter}s")
        rd = atlas.findRegion("${colorLetter}rd")
        ur = atlas.findRegion("${colorLetter}ur")
        dl = atlas.findRegion("${colorLetter}dl")
        ul = atlas.findRegion("${colorLetter}ul")
        udl = atlas.findRegion("${colorLetter}udl")
        rdl = atlas.findRegion("${colorLetter}rdl")
        url = atlas.findRegion("${colorLetter}url")
        urd = atlas.findRegion("${colorLetter}urd")
    }
}
