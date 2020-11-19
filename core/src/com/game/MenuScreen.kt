package com.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.FitViewport

class MenuScreen(val game: PuyoPuyoTetris) : Screen {
    private var camera = OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT)
    private var shapeRenderer = ShapeRenderer()
    private var viewport : FitViewport
    private val bg = Texture("animations/menu/frame (1).png")
    private val bgm = Gdx.audio.newMusic(Gdx.files.internal("sounds/welcome.mp3"))
    private var frame = 0
    private var loadingCount = 0

    private var hasLoaded = false

    init {
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f)
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT)
        viewport = FitViewport(camera.viewportWidth, camera.viewportHeight, camera)
        viewport.setScreenPosition(0, 0)
        //bgm.play()
    }
    // 1200
    override fun render(delta: Float) {
        game.batch.projectionMatrix = camera.combined
        shapeRenderer.projectionMatrix = camera.combined
        Gdx.gl.glClearColor(0f, 2/255f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.begin()
        if(game.manager.update()){
            /*
            if(!hasLoaded){
                game.batch.draw(TextureRegion(SpriteArea.bgSprites["loading"], 0, 0, 568, 45), (SCREEN_WIDTH-568f)/2, SCREEN_HEIGHT*0.05f)
                game.menuGif.load()
                game.bgGif.load()
                hasLoaded = true
            }
            game.batch.draw(game.menuGif.update(delta), 0f, 0f)*/
            game.batch.draw(bg, 0f, 0f)
            if(frame in 0..70){
                game.batch.draw(SpriteArea.bgSprites["press-button"], (SCREEN_WIDTH-350f)/2, SCREEN_HEIGHT*0.05f, 350f, 20f)
            }
            frame = if(frame > 100) 0 else frame+1

            if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY) || Gdx.input.isButtonPressed(Input.Keys.ANY_KEY)) {
                game.screen = GameScreen(game)
            }
        } else {
            loadingCount++
            game.batch.draw(bg, 0f, 0f)
            game.batch.draw(SpriteArea.bgSprites["loading-dark"], (SCREEN_WIDTH-568f)/2, SCREEN_HEIGHT*0.05f, 568f, 45f)
            game.batch.draw(TextureRegion(SpriteArea.bgSprites["loading"], 0, 0, (568*(loadingCount/1200f)).toInt(), 45), (SCREEN_WIDTH-568f)/2, SCREEN_HEIGHT*0.05f)
        }
        game.batch.draw(SpriteArea.bgSprites["title"], (SCREEN_WIDTH-668f)/2, (SCREEN_HEIGHT-35f)/2, 668f, 215f)
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