package com.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.FitViewport
import com.game.puyo.*

const val SCREEN_WIDTH = 1500f
const val SCREEN_HEIGHT = 1040f

class GameScreen(val game: PuyoPuyoTetris) : Screen {
    private val puyoController = Controller(Timer()) // also add Tetris later

    private var camera = OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT)
    private var shapeRenderer = ShapeRenderer()
    private var viewport : FitViewport
    private val titleFont = game.generateTitleFont(55)
    private val scoreFont = game.generateScoreFont(50)
    private val background = Texture(Gdx.files.internal("background.png"))

    init {
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f)
        viewport = FitViewport(camera.viewportWidth, camera.viewportHeight, camera)
        viewport.setScreenPosition(0, 0)
    }

    override fun render(delta: Float) {
        game.batch.projectionMatrix = camera.combined
        shapeRenderer.projectionMatrix = camera.combined
        Gdx.gl.glClearColor(27 / 255f, 18 / 255f, 64 / 255f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.begin()
        game.batch.draw(background, 0f, 0f, SCREEN_WIDTH, SCREEN_HEIGHT)
        game.batch.end()

        puyoController.readInput()
        puyoController.mainLoop()

        drawGridBackground()
        game.batch.begin()
        drawBlocks()
        drawTitle()
        drawScore()
        drawNextPuyos()
        game.batch.end()
    }

    private fun sendTrash(trash: Int){

    }

    private fun drawNextPuyos(){ // (∩｀-´)⊃━☆ﾟ.*･｡ﾟ 　。。数。。
        val firstNextPuyo = puyoController.getNextPuyo(0)
        val secondNextPuyo = puyoController.getNextPuyo(1)
        game.batch.draw(firstNextPuyo.first.currentSprite, PConstants.GRID_START_X * 1.2f + PConstants.GRID_WIDTH * PConstants.CELL_SIZE + PConstants.CELL_SIZE * 0.25f,
                PConstants.GRID_START_Y * 0.8f + PConstants.CELL_SIZE * 1.25f, PConstants.CELL_SIZE, PConstants.CELL_SIZE)
        game.batch.draw(firstNextPuyo.second.currentSprite, PConstants.GRID_START_X * 1.2f + PConstants.GRID_WIDTH * PConstants.CELL_SIZE + PConstants.CELL_SIZE * 0.25f,
                PConstants.GRID_START_Y * 0.8f + PConstants.CELL_SIZE * 1.25f - PConstants.CELL_SIZE, PConstants.CELL_SIZE, PConstants.CELL_SIZE)
        game.batch.draw(secondNextPuyo.first.currentSprite, PConstants.GRID_START_X * 1.3f + PConstants.GRID_WIDTH * PConstants.CELL_SIZE + PConstants.CELL_SIZE * 0.25f,
                PConstants.GRID_START_Y * 0.65f + PConstants.CELL_SIZE, PConstants.CELL_SIZE * 0.75f, PConstants.CELL_SIZE * 0.75f)
        game.batch.draw(secondNextPuyo.second.currentSprite, PConstants.GRID_START_X * 1.3f + PConstants.GRID_WIDTH * PConstants.CELL_SIZE + PConstants.CELL_SIZE * 0.25f,
                PConstants.GRID_START_Y * 0.65f + PConstants.CELL_SIZE * 0.25f, PConstants.CELL_SIZE * 0.75f, PConstants.CELL_SIZE * 0.75f)
    }

    private fun drawGridBackground(){
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(0.05f, 0.05f, 0.05f, 0.65f)
        shapeRenderer.rect(PConstants.GRID_START_X, PConstants.GRID_START_Y - (PConstants.GRID_LENGTH * PConstants.CELL_SIZE - PConstants.CELL_SIZE), PConstants.GRID_WIDTH * PConstants.CELL_SIZE, (PConstants.GRID_LENGTH - 1) * PConstants.CELL_SIZE)
        shapeRenderer.end()

        drawNextPuyoBg()
        Gdx.gl.glDisable(GL20.GL_BLEND)
    }

    private fun drawNextPuyoBg(){
        drawRoundedRect(PConstants.GRID_START_X * 1.2f + PConstants.GRID_WIDTH * PConstants.CELL_SIZE, PConstants.GRID_START_Y * 0.8f, PConstants.CELL_SIZE * 1.5f, PConstants.CELL_SIZE * 2.6f, 10f)
        drawRoundedRect(PConstants.GRID_START_X * 1.3f + PConstants.GRID_WIDTH * PConstants.CELL_SIZE, PConstants.GRID_START_Y * 0.65f, PConstants.CELL_SIZE * 1.25f, PConstants.CELL_SIZE * 2f, 10f)
    }

    private fun drawRoundedRect(x: Float, y: Float, width: Float, height: Float, radius: Float){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(0.05f, 0.05f, 0.05f, 0.65f)
        shapeRenderer.rect(x + radius, y + radius, width - 2 * radius, height - 2 * radius)

        // Four side rectangles, in clockwise order
        shapeRenderer.rect(x + radius, y, width - 2 * radius, radius)
        shapeRenderer.rect(x + width - radius, y + radius, radius, height - 2 * radius)
        shapeRenderer.rect(x + radius, y + height - radius, width - 2 * radius, radius)
        shapeRenderer.rect(x, y + radius, radius, height - 2 * radius)

        // Four arches, clockwise too
        shapeRenderer.arc(x + radius, y + radius, radius, 180f, 90f)
        shapeRenderer.arc(x + width - radius, y + radius, radius, 270f, 90f)
        shapeRenderer.arc(x + width - radius, y + height - radius, radius, 0f, 90f)
        shapeRenderer.arc(x + radius, y + height - radius, radius, 90f, 90f)
        shapeRenderer.end()
    }

    private fun drawBlocks(){
        val c = game.batch.color
        for(i in 0 until PConstants.GRID_WIDTH){
            for(j in 0 until PConstants.GRID_LENGTH){
                val block = puyoController.getBlockAt(i, j)
                if(block == null || j == 0){
                    continue
                }
                if(block.isBeingRemoved || (block is PuyoBlock && block.flicker > 0 )) {
                    if (block.flicker > 5) {
                        game.batch.setColor(c.r, c.g, c.b, 1f)
                    } else {
                        game.batch.setColor(c.r, c.g, c.b, 0.6f)
                    }
                    block.addFlicker()
                } else {
                    game.batch.setColor(c.r, c.g, c.b, 1f)
                }
                game.batch.draw(block.currentSprite,
                        PConstants.GRID_START_X + i * PConstants.CELL_SIZE,
                        PConstants.GRID_START_Y - j * PConstants.CELL_SIZE,
                        PConstants.CELL_SIZE, PConstants.CELL_SIZE)
            }
        }
        game.batch.setColor(c.r, c.g, c.b, 1f)
    }

    private fun drawTitle(){
        titleFont.draw(game.batch, "Puyo Puyo", PConstants.GRID_START_X * 1.27f, SCREEN_HEIGHT * 0.94f)
    }

    private fun drawScore(){
        scoreFont.draw(game.batch, puyoController.getCurrentScore(), PConstants.GRID_START_X * 1.6f, PConstants.GRID_START_Y - PConstants.GRID_START_Y * 0.87f)
    }

    override fun show() {

    }
    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
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