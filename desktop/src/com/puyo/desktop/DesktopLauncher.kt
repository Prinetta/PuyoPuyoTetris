package com.puyo.desktop

import com.badlogic.gdx.Gdx
import kotlin.jvm.JvmStatic
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.puyo.PuyoPuyoTetris

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration()
        config.title = "Puyo Puyo Tetris"
        config.width = 1920
        config.height = 1000
        LwjglApplication(PuyoPuyoTetris(), config)
    }
}