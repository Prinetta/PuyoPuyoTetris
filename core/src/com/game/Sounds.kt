package com.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound

class Sounds {
    companion object Sounds {
        val pmove: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/pmove.wav"))
        val pdrop: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/pdrop.wav"))
        val protate: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/protate.wav"))
        val plost: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/dead.mp3"))

        val garbage: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/pojama.wav"))
        val garbage2: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/pojama2.wav"))
        val start: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/start.wav"))

        val gsend1: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/gsend1.wav"))
        val gsend2: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/gsend2.wav"))
        val gsend3: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/gsend3.wav"))
        val gsend4: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/gsend4.wav"))

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