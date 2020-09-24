package com.puyo.desktop

import kotlin.jvm.JvmStatic
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.puyo.PuyoPuyoTetris

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration()
        config.title = "Puyo Puyo Tetris"
        config.width = 700
        config.height = 800
        LwjglApplication(PuyoPuyoTetris(), config)
    }
}