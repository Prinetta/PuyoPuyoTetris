package com.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound

class Sounds {
    companion object Sounds {
        val pmove: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/pmove.wav"))
        val protate: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/protate.wav"))
        val garbage: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/pojama.wav"))
        val garbage2: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/pojama2.wav"))

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