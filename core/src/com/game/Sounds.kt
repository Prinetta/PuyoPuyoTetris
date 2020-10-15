package com.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound

class Sounds {
    companion object Sounds {
        val pmove: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/pmove.wav"))
        val pdrop: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/pdrop.wav"))
        val protate: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/protate.wav"))
        val garbage: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/pojama.wav"))
        val garbage2: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/pojama2.wav"))


        val gsend1: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/gsend1.wav"))
        val gsend2: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/gsend2.wav"))
        val gsend3: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/gsend3.wav"))
        val gsend4: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/gsend4.wav"))

        // tetris

        val tmove: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/tmove.wav"))
        val tfall: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/tfall.wav"))
        val tdown: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/tdown.wav"))
        val thdrop: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/thdrop.wav"))
        val trotate: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/trotate.wav"))
        val tkesiline: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/tkesiline.wav"))
        val tkesib2b: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/tkesib2b.wav"))
        val tkesitetris: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/tkesitetris.wav"))
        val tkesispin: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/tkesispin.wav"))
        val tspin: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/tspin.wav"))
        val thold: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/thold.wav"))
        val tover: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/tover.wav"))


        val chainSounds = createChainMap()

        private fun createChainMap(): MutableMap<String, Sound> {
            val hashMap = mutableMapOf<String, Sound>()
            for(i in 1..7){
                hashMap["pchain$i"] = Gdx.audio.newSound(Gdx.files.internal("sounds/pren$i.wav"))
            }
            return hashMap
        }
    }
}