package com.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.FitViewport
import com.game.puyo.*

const val GRID_WIDTH = 6
const val GRID_LENGTH = 13

const val SCREEN_WIDTH = 1500f
const val SCREEN_HEIGHT = 1040f
const val CELL_SIZE = 65f
const val GRID_START_X = SCREEN_WIDTH*0.1f
const val GRID_START_Y = SCREEN_HEIGHT*0.13f + GRID_LENGTH*CELL_SIZE-CELL_SIZE

class GameScreen(val game: PuyoPuyoTetris) : Screen {
    private val controller = Controller(Timer()) // also add Tetris later

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

        controller.readInput()
        controller.mainLoop()

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
        val firstNextPuyo = controller.getNextPuyo(0)
        val secondNextPuyo = controller.getNextPuyo(1)
        game.batch.draw(firstNextPuyo.first.currentSprite, GRID_START_X * 1.2f + GRID_WIDTH * CELL_SIZE + CELL_SIZE * 0.25f,
                GRID_START_Y * 0.8f + CELL_SIZE * 1.25f, CELL_SIZE, CELL_SIZE)
        game.batch.draw(firstNextPuyo.second.currentSprite, GRID_START_X * 1.2f + GRID_WIDTH * CELL_SIZE + CELL_SIZE * 0.25f,
                GRID_START_Y * 0.8f + CELL_SIZE * 1.25f - CELL_SIZE, CELL_SIZE, CELL_SIZE)
        game.batch.draw(secondNextPuyo.first.currentSprite, GRID_START_X * 1.3f + GRID_WIDTH * CELL_SIZE + CELL_SIZE * 0.25f,
                GRID_START_Y * 0.65f + CELL_SIZE, CELL_SIZE * 0.75f, CELL_SIZE * 0.75f)
        game.batch.draw(secondNextPuyo.second.currentSprite, GRID_START_X * 1.3f + GRID_WIDTH * CELL_SIZE + CELL_SIZE * 0.25f,
                GRID_START_Y * 0.65f + CELL_SIZE * 0.25f, CELL_SIZE * 0.75f, CELL_SIZE * 0.75f)
    }

    private fun drawGridBackground(){
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(0.05f, 0.05f, 0.05f, 0.65f)
        shapeRenderer.rect(GRID_START_X, GRID_START_Y - (GRID_LENGTH * CELL_SIZE - CELL_SIZE), GRID_WIDTH * CELL_SIZE, (GRID_LENGTH - 1) * CELL_SIZE)
        shapeRenderer.end()

        drawNextPuyoBg()
        Gdx.gl.glDisable(GL20.GL_BLEND)
    }

    private fun drawNextPuyoBg(){
        drawRoundedRect(GRID_START_X * 1.2f + GRID_WIDTH * CELL_SIZE, GRID_START_Y * 0.8f, CELL_SIZE * 1.5f, CELL_SIZE * 2.6f, 10f)
        drawRoundedRect(GRID_START_X * 1.3f + GRID_WIDTH * CELL_SIZE, GRID_START_Y * 0.65f, CELL_SIZE * 1.25f, CELL_SIZE * 2f, 10f)
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
        for(i in 0 until GRID_WIDTH){
            for(j in 0 until GRID_LENGTH){
                val block = controller.getBlockAt(i, j)
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
                        GRID_START_X + i * CELL_SIZE,
                        GRID_START_Y - j * CELL_SIZE,
                        CELL_SIZE, CELL_SIZE)
            }
        }
        game.batch.setColor(c.r, c.g, c.b, 1f)
    }

    private fun drawTitle(){
        titleFont.draw(game.batch, "Puyo Puyo", GRID_START_X * 1.27f, SCREEN_HEIGHT * 0.94f)
    }

    private fun drawScore(){
        scoreFont.draw(game.batch, controller.getCurrentScore(), GRID_START_X * 1.6f, GRID_START_Y - GRID_START_Y * 0.87f)
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