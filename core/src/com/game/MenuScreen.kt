package com.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.FitViewport

class MenuScreen(val game: PuyoPuyoTetris) : Screen {
    private var camera = OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT)
    private var shapeRenderer = ShapeRenderer()
    private var viewport : FitViewport
    private val bg = Texture(Gdx.files.internal("menubg.png"))
    private val bgShine = Texture(Gdx.files.internal("menubgshine.png"))
    private val bgm = Gdx.audio.newMusic(Gdx.files.internal("sounds/welcome.mp3"));

    init {
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f)
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT)
        viewport = FitViewport(camera.viewportWidth, camera.viewportHeight, camera)
        viewport.setScreenPosition(0, 0)
        bg.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        //bgm.play()
    }

    override fun render(delta: Float) {
        game.batch.projectionMatrix = camera.combined
        shapeRenderer.projectionMatrix = camera.combined
        Gdx.gl.glClearColor(0f, 2/255f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        game.batch.begin()
        game.batch.draw(bg, 0f, 0f)
        if(Gdx.input.x in 660..1360 && Gdx.input.y in 607..660){
            game.batch.draw(bgShine, 0f, 0f)
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                game.screen = GameScreen(game)
            }
        }
        game.batch.end()
    }

    override fun show() {

    }

    override fun resize(width: Int, height: Int) {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun hide() {
    }

    override fun dispose() {
    }

}