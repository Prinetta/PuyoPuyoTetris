package com.puyo.desktop

import kotlin.jvm.JvmStatic
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.game.PuyoPuyoTetris
import com.game.SCREEN_HEIGHT
import com.game.SCREEN_WIDTH

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration()
        config.title = "Puyo Puyo Tetris"
        config.width = SCREEN_WIDTH.toInt()
        config.height = SCREEN_HEIGHT.toInt()
        LwjglApplication(PuyoPuyoTetris(), config)
    }
}