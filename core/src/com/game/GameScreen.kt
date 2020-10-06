package com.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.FitViewport
import com.game.puyo.*
import com.puyo.TetrisGame
import com.puyo.Tetromino

const val SCREEN_WIDTH = 1600f
const val SCREEN_HEIGHT = 1040f

class GameScreen(val game: PuyoPuyoTetris) : Screen {
    private val puyoController = Controller(Timer())
    private var tetrisGame: TetrisGame = TetrisGame()

    private var camera = OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT)
    private var shapeRenderer = ShapeRenderer()
    private var viewport : FitViewport
    private val titleFont = game.generateTitleFont(55)
    private val scoreFont = game.generateScoreFont(50)
    private val background = Texture(Gdx.files.internal("background.png"))

    // Tetris
    private val nextFont = game.generateTetrisNextFont(25)

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

        /// Puyo Controller
        puyoController.readInput()
        puyoController.mainLoop()

        /// Tetris Controller
        tetrisGame.handleInputs(delta)

        /// Shape Renderer
        drawPuyoBg()
        drawTetrisGridBackground()
        drawNextBorders()
        drawTetrisGrid()

        game.batch.begin()
        /// Puyo Draw
        drawPuyos()
        drawTitle()
        drawScore()
        drawNextPuyos()
        /// Tetris Draw
        drawTetrisBlocks()
        drawNextTetrominos()
      
        game.batch.end()
    }

    /// Tetris Methods

    private fun drawNextTetrominos(){
        nextFont.draw(game.batch, "NEXT", TC.NEXT_BLOCK_FIELD_X + (TC.CELL_SIZE * 0.2f), TC.GRID_TOP_Y - (TC.CELL_SIZE * 0.15f))
        var nextBlock: Tetromino = tetrisGame.nextTetrominos.peek()
        for (i in nextBlock.shape.indices) { // beeg
            for (j in 0 until nextBlock.shape[i].size) {
                if (nextBlock.shape[i][j] != null) {
                    game.batch.draw(nextBlock.shape[i][j].texture,
                            TC.NEXT_BLOCK_FIELD_X + ((TC.CELL_SIZE * 5f - (nextBlock.width * 0.9f)) / 2) + ((i - nextBlock.firstColumn()) * TC.CELL_SIZE * 0.9f),
                            TC.GRID_TOP_Y - (TC.CELL_SIZE * 0.9f) - ((TC.CELL_SIZE * 4.5f - (nextBlock.height * 0.9f)) / 2) - ((j - nextBlock.firstRow()) * TC.CELL_SIZE * 0.9f),
                            TC.CELL_SIZE * 0.9f, TC.CELL_SIZE * 0.9f)
                }
            }
        }
        for (field in 0..3) {
            nextBlock = tetrisGame.nextTetrominos[3 - field]
            for (i in nextBlock.shape.indices) {
                for (j in 0 until nextBlock.shape[i].size) {
                    if (nextBlock.shape[i][j] != null) {
                        game.batch.draw(nextBlock.shape[i][j].texture,
                                TC.NEXT_BLOCK_FIELD_X + ((TC.CELL_SIZE * 3.5f - (nextBlock.width * 0.7f)) / 2) + ((i - nextBlock.firstColumn()) * TC.CELL_SIZE * 0.7f),
                                TC.NEXT_BLOCK_FIELD2_TOP_Y - (((field * 3) + 0.7f) * TC.CELL_SIZE) - ((TC.CELL_SIZE * 2.5f - (nextBlock.height * 0.7f)) / 2) - ((j - nextBlock.firstRow()) * TC.CELL_SIZE * 0.7f),
                                TC.CELL_SIZE * 0.7f, TC.CELL_SIZE * 0.7f)
                    }
                }
            }
        }
    }

    private fun drawTetrisGrid(){
        Gdx.gl.glEnable(GL20.GL_BLEND)
        shapeRenderer.setColor(78f/255, 65f/255, 83f/255, 1f)
        shapeRenderer.begin()

        for (i in 0..tetrisGame.rows - 1) { // invisible rows are nice
            shapeRenderer.rectLine(TC.GRID_LEFT_X, TC.GRID_TOP_Y - (i * TC.CELL_SIZE),
                    TC.GRID_RIGHT_X, TC.GRID_TOP_Y - (i * TC.CELL_SIZE), 1f)
        }

        for (j in 0..tetrisGame.columns) {
            shapeRenderer.rectLine(TC.GRID_LEFT_X + (j * TC.CELL_SIZE), TC.GRID_TOP_Y,
                    TC.GRID_LEFT_X + (j * TC.CELL_SIZE), TC.GRID_TOP_Y - ((tetrisGame.rows - 1) * TC.CELL_SIZE), 1f)
        }

        Gdx.gl.glDisable(GL20.GL_BLEND)
        shapeRenderer.end()
        drawTetrisNextBg()
    }

    private fun drawTetrisNextBg(){
        Gdx.gl.glEnable(GL20.GL_BLEND)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(0.05f, 0.05f, 0.05f, 0.65f)
        shapeRenderer.rect(TC.NEXT_BLOCK_FIELD_X, TC.NEXT_BLOCK_FIELD_Y, TC.CELL_SIZE * 5f, TC.CELL_SIZE * 4.5f)
        for (i in 0..9 step 3) {
            shapeRenderer.rect(TC.NEXT_BLOCK_FIELD_X, TC.NEXT_BLOCK_FIELD2_Y-i*(TC.CELL_SIZE), TC.CELL_SIZE * 3.5f, TC.CELL_SIZE * 2.5f)
        }
        shapeRenderer.end()
        Gdx.gl.glDisable(GL20.GL_BLEND)
    }

    private fun drawNextBorders(){
        shapeRenderer.setAutoShapeType(true)
        shapeRenderer.begin()
        Gdx.gl.glEnable(GL20.GL_BLEND) // allows changing opacity I guess
        shapeRenderer.color = Color.LIGHT_GRAY

        // main next block field
        shapeRenderer.rectLine(TC.NEXT_BLOCK_FIELD_X, TC.NEXT_BLOCK_FIELD_Y + (TC.CELL_SIZE * 4.5f),
                TC.NEXT_BLOCK_FIELD_X + (TC.CELL_SIZE * 5f), TC.NEXT_BLOCK_FIELD_Y + (TC.CELL_SIZE * 4.5f), 1f)
        shapeRenderer.rectLine(TC.NEXT_BLOCK_FIELD_X, TC.NEXT_BLOCK_FIELD_Y, TC.NEXT_BLOCK_FIELD_X + (TC.CELL_SIZE * 5f), TC.NEXT_BLOCK_FIELD_Y, 1f)
        shapeRenderer.rectLine(TC.NEXT_BLOCK_FIELD_X, TC.NEXT_BLOCK_FIELD_Y, TC.NEXT_BLOCK_FIELD_X, TC.NEXT_BLOCK_FIELD_Y + (TC.CELL_SIZE * 4.5f), 1f)
        shapeRenderer.rectLine(TC.NEXT_BLOCK_FIELD_X + (TC.CELL_SIZE * 5f), TC.NEXT_BLOCK_FIELD_Y,
                TC.NEXT_BLOCK_FIELD_X + (TC.CELL_SIZE * 5f), TC.NEXT_BLOCK_FIELD_Y + (TC.CELL_SIZE * 4.5f), 1f)

        // secondary next blocks field
        for (i in 0..9 step 3) {
            shapeRenderer.rectLine(TC.NEXT_BLOCK_FIELD_X, TC.NEXT_BLOCK_FIELD2_Y - (i * TC.CELL_SIZE),
                    TC.NEXT_BLOCK_FIELD_X + (TC.CELL_SIZE * 3.5f), TC.NEXT_BLOCK_FIELD2_Y - (i * TC.CELL_SIZE), 1f)
            shapeRenderer.rectLine(TC.NEXT_BLOCK_FIELD_X, TC.NEXT_BLOCK_FIELD2_TOP_Y - (i * TC.CELL_SIZE),
                    TC.NEXT_BLOCK_FIELD_X + (TC.CELL_SIZE * 3.5f), TC.NEXT_BLOCK_FIELD2_TOP_Y - (i * TC.CELL_SIZE), 1f)
            shapeRenderer.rectLine(TC.NEXT_BLOCK_FIELD_X, TC.NEXT_BLOCK_FIELD2_TOP_Y - (i * TC.CELL_SIZE),
                    TC.NEXT_BLOCK_FIELD_X, TC.NEXT_BLOCK_FIELD2_Y - (i * TC.CELL_SIZE), 1f)
            shapeRenderer.rectLine(TC.NEXT_BLOCK_FIELD_X + (TC.CELL_SIZE * 3.5f), TC.NEXT_BLOCK_FIELD2_TOP_Y - (i * TC.CELL_SIZE),
                    TC.NEXT_BLOCK_FIELD_X + (TC.CELL_SIZE * 3.5f), TC.NEXT_BLOCK_FIELD2_Y - (i * TC.CELL_SIZE), 1f)
        }
        Gdx.gl.glDisable(GL20.GL_BLEND)
        shapeRenderer.end()
    }

    private fun sendTrash(trash: Int){

    }

    private fun drawNextPuyos(){ // (∩｀-´)⊃━☆ﾟ.*･｡ﾟ 　。。数。。
        val firstNextPuyo = puyoController.getNextPuyo(0)
        val secondNextPuyo = puyoController.getNextPuyo(1)
        game.batch.draw(firstNextPuyo.first.currentSprite, PC.GRID_START_X * 1.2f + PC.GRID_WIDTH * PC.CELL_SIZE + PC.CELL_SIZE * 0.25f,
                PC.GRID_START_Y * 0.8f + PC.CELL_SIZE * 1.25f, PC.CELL_SIZE, PC.CELL_SIZE)
        game.batch.draw(firstNextPuyo.second.currentSprite, PC.GRID_START_X * 1.2f + PC.GRID_WIDTH * PC.CELL_SIZE + PC.CELL_SIZE * 0.25f,
                PC.GRID_START_Y * 0.8f + PC.CELL_SIZE * 1.25f - PC.CELL_SIZE, PC.CELL_SIZE, PC.CELL_SIZE)
        game.batch.draw(secondNextPuyo.first.currentSprite, PC.GRID_START_X * 1.3f + PC.GRID_WIDTH * PC.CELL_SIZE + PC.CELL_SIZE * 0.25f,
                PC.GRID_START_Y * 0.65f + PC.CELL_SIZE, PC.CELL_SIZE * 0.75f, PC.CELL_SIZE * 0.75f)
        game.batch.draw(secondNextPuyo.second.currentSprite, PC.GRID_START_X * 1.3f + PC.GRID_WIDTH * PC.CELL_SIZE + PC.CELL_SIZE * 0.25f,
                PC.GRID_START_Y * 0.65f + PC.CELL_SIZE * 0.25f, PC.CELL_SIZE * 0.75f, PC.CELL_SIZE * 0.75f)
    }
  
    private fun drawTetrisGridBackground() {
        Gdx.gl.glEnable(GL20.GL_BLEND)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(0.05f, 0.05f, 0.05f, 0.65f)
        shapeRenderer.rect(TC.GRID_LEFT_X, TC.GRID_TOP_Y - ((TC.ROWS - 1) * TC.CELL_SIZE), (TC.COLUMNS * TC.CELL_SIZE).toFloat(), ((TC.ROWS - 1) * TC.CELL_SIZE).toFloat())
        shapeRenderer.end()
        Gdx.gl.glDisable(GL20.GL_BLEND)
    }

    private fun drawPuyoBg(){
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(0.05f, 0.05f, 0.05f, 0.65f)
        shapeRenderer.rect(PC.GRID_START_X, PC.GRID_START_Y - (PC.GRID_LENGTH * PC.CELL_SIZE - PC.CELL_SIZE), PC.GRID_WIDTH * PC.CELL_SIZE, (PC.GRID_LENGTH - 1) * PC.CELL_SIZE)
        shapeRenderer.end()

        drawNextPuyoBg()
        Gdx.gl.glDisable(GL20.GL_BLEND)
    }

    private fun drawNextPuyoBg(){
        drawRoundedRect(PC.GRID_START_X * 1.2f + PC.GRID_WIDTH * PC.CELL_SIZE, PC.GRID_START_Y * 0.8f, PC.CELL_SIZE * 1.5f, PC.CELL_SIZE * 2.6f, 10f)
        drawRoundedRect(PC.GRID_START_X * 1.3f + PC.GRID_WIDTH * PC.CELL_SIZE, PC.GRID_START_Y * 0.65f, PC.CELL_SIZE * 1.25f, PC.CELL_SIZE * 2f, 10f)
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

    private fun drawTetrisBlocks(){
        for (i in tetrisGame.cells.indices) { // invisible rows are nice
            for (j in 1 until tetrisGame.cells[i].size) {
                if (tetrisGame.cells[i][j] != null) {
                    game.batch.draw(tetrisGame.cells[i][j].texture, i.toFloat() * TC.CELL_SIZE + TC.GRID_LEFT_X, TC.GRID_TOP_Y - (j.toFloat() * TC.CELL_SIZE),
                            TC.CELL_SIZE, TC.CELL_SIZE)
                }
            }
        }
    }

    private fun drawPuyos(){
        val c = game.batch.color
        for(i in 0 until PC.GRID_WIDTH){
            for(j in 0 until PC.GRID_LENGTH){
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
                        PC.GRID_START_X + i * PC.CELL_SIZE,
                        PC.GRID_START_Y - j * PC.CELL_SIZE,
                        PC.CELL_SIZE, PC.CELL_SIZE)
            }
        }
        game.batch.setColor(c.r, c.g, c.b, 1f)
    }

    private fun drawTitle(){
        titleFont.draw(game.batch, "Puyo Puyo", PC.GRID_START_X * 1.27f, SCREEN_HEIGHT * 0.94f)
    }

    private fun drawScore(){
        scoreFont.draw(game.batch, puyoController.getCurrentScore(), PC.GRID_START_X * 1.6f, PC.GRID_START_Y - PC.GRID_START_Y * 0.87f)
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