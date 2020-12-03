package com.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import com.game.Tetris.*
import com.game.puyo.*

const val SCREEN_WIDTH = 1700f
const val SCREEN_HEIGHT = 1040f

class GameScreen(val game: PuyoPuyoTetris) : Screen {
    private var gameOver = false

    private var gameOverTime = Time(300)

    private var screenshot: Texture? = null
    private var winTexture = SpriteArea.gameSprites["winner"]
    private var loseTexture = SpriteArea.gameSprites["loser"]

    private var tetrisGame: TetrisGame = TetrisGame()
    private val puyoController = Controller()

    private var camera = OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT)
    private var shapeRenderer = ShapeRenderer()
    private var viewport : FitViewport
    private val scoreFont = game.generateScoreFont(50)
    private val background = Texture(Gdx.files.internal("animations/bg/frame (1).gif"))

    // Tetris
    private val nextFont = game.generateTetrisNextFont(25)

    init {
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f)
        viewport = FitViewport(camera.viewportWidth, camera.viewportHeight, camera)
        viewport.setScreenPosition(0, 0)
        puyoController.setTetris(tetrisGame)
        tetrisGame.setPuyo(puyoController.puyoGame)
        Sounds.pmove.play()
    }

    private val countdown = Time(1000)
    private var count = 3

    override fun render(delta: Float) {
        game.batch.projectionMatrix = camera.combined
        shapeRenderer.projectionMatrix = camera.combined
        Gdx.gl.glClearColor(0f, 2/255f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        if (tetrisGame.gameOver || puyoController.puyoGame.gameOver) gameOver = true

        if (screenshot == null) {
            /// Background
            game.batch.begin()
            game.batch.draw(game.bgGif.update(delta), 0f, 0f, SCREEN_WIDTH, SCREEN_HEIGHT)
            //game.batch.draw(background, 0f, 0f, SCREEN_WIDTH, SCREEN_HEIGHT)
            drawPuyoBgTexture()
            drawTetrisGridTexture()
            game.batch.end()

            /// Puyo Controller
            if(count < 0){
                puyoController.readInput()
            }
            puyoController.mainLoop()

            /// Tetris Controller
            tetrisGame.run()

            /// Shape Renderer Begin
            drawPuyoBg()
            drawTetrisGridBackground()
            drawTetrisGrid()
            /// Shape Renderer End

            /// -Begin draw-
            game.batch.begin()
            /// Puyo Draw
            drawCross()
            drawPuyoPreview()
            drawPuyos()
            drawNextPuyos()
            drawGarbageQueue()
            /// Tetris Draw
            drawTetrominos()
            drawHeldTetrominoBg()
            drawHeldTetromino()
            drawTetrisNextBg()
            drawNextTetrominos()
            drawTetrisShadow()
            drawScore()
            drawTetrisGarbageQueue()
            drawTetrisEffects()
            drawTetrisLabels()

            drawCountDown()
            game.batch.end()
            /// -End Draw-
            if (gameOver) {
                screenshot = if (Gdx.graphics.width > SCREEN_WIDTH) Texture(ScreenUtils.getFrameBufferPixmap(100, 0, SCREEN_WIDTH.toInt(), SCREEN_HEIGHT.toInt()))
                else Texture(ScreenUtils.getFrameBufferPixmap(0, 0, SCREEN_WIDTH.toInt(), SCREEN_HEIGHT.toInt()))
                gameOverTime.reset()
                Sounds.lose.play()
            }
        } else {
            handleInputs()
            drawVictoryScreen()
        }

        //println(Gdx.graphics.framesPerSecond)
    }

    private fun handleInputs() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (Gdx.input.x.toFloat() in (SCREEN_WIDTH - GC.GAMEOVER_LABEL_WIDTH) / 2..(SCREEN_WIDTH - GC.GAMEOVER_LABEL_WIDTH) / 2 + GC.GAMEOVER_LABEL_WIDTH) {
                // for some reason coordinates don't fully match labels
                if (SCREEN_HEIGHT - Gdx.input.y.toFloat() in SCREEN_HEIGHT * 0.46f - GC.GAMEOVER_LABEL_HEIGHT..SCREEN_HEIGHT * 0.46f) {
                    game.screen = GameScreen(game)
                    this.dispose()
                } else if (SCREEN_HEIGHT - Gdx.input.y.toFloat() in SCREEN_HEIGHT * 0.365f - GC.GAMEOVER_LABEL_HEIGHT..SCREEN_HEIGHT * 0.365f) {
                    game.screen = MenuScreen(game)
                    game.bgm.stop()
                    this.dispose()
                }
            }
        }
    }

    private fun drawCountDown(){
        if(count >= 0) {
            if(count == 0){
                game.batch.draw(SpriteArea.gameSprites["cd-start"], SCREEN_WIDTH/2-458/2, SCREEN_HEIGHT/2, 458f, 127f)
            } else {
                game.batch.draw(SpriteArea.gameSprites["cd$count"], SCREEN_WIDTH / 2 - 123 / 2, SCREEN_HEIGHT / 2, 123f, 168f)
            }
            if (countdown.hasPassed()) {
                count--
                if (count > 0) {
                    Sounds.pmove.play()
                } else if (count == 0) {
                    Sounds.start.play()
                }
                countdown.reset()
            }
        } else if (count == -1) {
            puyoController.hasStarted = true
            tetrisGame.hasStarted = true
            count--
            if(!game.bgm.isPlaying){
                game.bgm.volume = 0.4f
                game.bgm.isLooping = true
                game.bgm.play()
            }
        }
    }

    // Game methods

    private fun drawVictoryScreen() {
        game.batch.begin()
        var process: Float = if (!gameOverTime.hasPassed()) gameOverTime.runtime() / gameOverTime.delay.toFloat() else 1f
        game.batch.setColor(1f - process / 1.75f, 1 - process / 1.75f, 1 - process / 1.75f, 1f)
        //screenshot is upside down for some reason
        game.batch.draw(screenshot, 0f, SCREEN_HEIGHT, SCREEN_WIDTH, -SCREEN_HEIGHT)
        game.batch.setColor(1f, 1f, 1f, 1f)
        var puyoTexture = winTexture
        var tetrisTexture = loseTexture
        if (puyoController.puyoGame.gameOver) {
            puyoTexture = loseTexture
            tetrisTexture = winTexture
            game.batch.draw(puyoTexture, PC.GRID_START_X + (PC.GRID_WIDTH * PC.CELL_SIZE - GC.LOSE_LABEL_WIDTH) / 2,
                    SCREEN_HEIGHT * 0.675f - GC.LOSE_LABEL_HEIGHT * process / 2, GC.LOSE_LABEL_WIDTH, GC.LOSE_LABEL_HEIGHT * process)
            game.batch.draw(tetrisTexture, TC.GRID_LEFT_X + (TC.GRID_WIDTH - GC.WIN_LABEL_WIDTH) / 2,
                    SCREEN_HEIGHT * 0.675f - GC.WIN_LABEL_HEIGHT * process / 2, GC.WIN_LABEL_WIDTH, GC.WIN_LABEL_HEIGHT * process)
        } else {
            game.batch.draw(puyoTexture, PC.GRID_START_X + (PC.GRID_WIDTH * PC.CELL_SIZE - GC.WIN_LABEL_WIDTH) / 2,
                    SCREEN_HEIGHT * 0.675f - GC.WIN_LABEL_HEIGHT * process / 2, GC.WIN_LABEL_WIDTH, GC.WIN_LABEL_HEIGHT * process)
            game.batch.draw(tetrisTexture, TC.GRID_LEFT_X + (TC.GRID_WIDTH - GC.LOSE_LABEL_WIDTH) / 2,
                    SCREEN_HEIGHT * 0.675f - GC.LOSE_LABEL_HEIGHT * process / 2, GC.LOSE_LABEL_WIDTH, GC.LOSE_LABEL_HEIGHT * process)
        }
        process = (gameOverTime.runtime() - gameOverTime.delay.toFloat()) / gameOverTime.delay.toFloat()
        if (process < 0f) process = 0f else if (process > 1f) process = 1f
        drawGameOverButtons(process)
    }

    private fun drawGameOverButtons(process: Float) {
        drawGameOverButtonBg(process)

        var label: TextureRegion?
        if (Gdx.input.x.toFloat() in (SCREEN_WIDTH - GC.GAMEOVER_LABEL_WIDTH) / 2..(SCREEN_WIDTH - GC.GAMEOVER_LABEL_WIDTH) / 2 + GC.GAMEOVER_LABEL_WIDTH
                && SCREEN_HEIGHT - Gdx.input.y.toFloat() in SCREEN_HEIGHT * 0.46f - GC.GAMEOVER_LABEL_HEIGHT..SCREEN_HEIGHT * 0.46f) {
            label = SpriteArea.gameSprites["rematch"]
        } else label = SpriteArea.gameSprites["rematch-dark"]
        game.batch.draw(label, (SCREEN_WIDTH - GC.GAMEOVER_LABEL_WIDTH) / 2,
                SCREEN_HEIGHT * 0.425f - GC.GAMEOVER_LABEL_HEIGHT * process / 2, GC.GAMEOVER_LABEL_WIDTH, GC.GAMEOVER_LABEL_HEIGHT * process)

        if (Gdx.input.x.toFloat() in (SCREEN_WIDTH - GC.GAMEOVER_LABEL_WIDTH) / 2..(SCREEN_WIDTH - GC.GAMEOVER_LABEL_WIDTH) / 2 + GC.GAMEOVER_LABEL_WIDTH
                && SCREEN_HEIGHT - Gdx.input.y.toFloat() in SCREEN_HEIGHT * 0.365f - GC.GAMEOVER_LABEL_HEIGHT..SCREEN_HEIGHT * 0.365f) {
            label = SpriteArea.gameSprites["btt"]
        } else label = SpriteArea.gameSprites["btt-dark"]
        game.batch.draw(label, (SCREEN_WIDTH - GC.GAMEOVER_LABEL_WIDTH) / 2,
                SCREEN_HEIGHT * 0.33f - GC.GAMEOVER_LABEL_HEIGHT * process / 2, GC.GAMEOVER_LABEL_WIDTH, GC.GAMEOVER_LABEL_HEIGHT * process)

        game.batch.end()
    }

    private fun drawGameOverButtonBg(process: Float) {
        game.batch.draw(SpriteArea.bgSprites["grid-bg"], (SCREEN_WIDTH - GC.GAMEOVER_LABEL_WIDTH) / 2 - 1.5f * TC.CELL_SIZE + 2f,
                SCREEN_HEIGHT * ((0.33f + 0.425f) / 2) - 0.2f * SCREEN_HEIGHT * process / 2, GC.GAMEOVER_LABEL_WIDTH + 3f * TC.CELL_SIZE,
                0.2f * SCREEN_HEIGHT * process)
        game.batch.end()

        Gdx.gl.glEnable(GL20.GL_BLEND)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(1/255f, 3/255f, 7/255f, 0.5f)
        shapeRenderer.rect((SCREEN_WIDTH - GC.GAMEOVER_LABEL_WIDTH) / 2 - TC.CELL_SIZE + 2f,
                SCREEN_HEIGHT * ((0.33f + 0.425f) / 2) - 0.2f * SCREEN_HEIGHT * process / 2, GC.GAMEOVER_LABEL_WIDTH + 2f * TC.CELL_SIZE,
                0.2f * SCREEN_HEIGHT * process)
        shapeRenderer.end()
        Gdx.gl.glDisable(GL20.GL_BLEND)

        game.batch.begin()
    }

    /// Tetris Methods

    private fun drawTetrisLabels() {
        drawTetrisComboLabel()
        drawTetrisB2BLabel()
        drawTetrisTSpinLabel()
        drawTetrisTetrisLabel()
    }

    private fun drawTetrisComboLabel() {
        val c = game.batch.color
        if (tetrisGame.comboTime.isRunning() && tetrisGame.comboTime.runtime() < 4000f) {
            var process: Float = tetrisGame.comboTime.runtime() / tetrisGame.comboTime.delay.toFloat()
            if (process > 1) {
                game.batch.setColor(c.r, c.g, c.b, (3 - (tetrisGame.comboTime.runtime() - tetrisGame.comboTime.delay) / 1000f))
                process = 1f
            }
            if (tetrisGame.comboCount > 10) {
                game.batch.draw(SpriteArea.gameSprites["tcombo${tetrisGame.comboCount.toString().first()}"],
                        TC.COMBO_NUMBER_LABEL_LEFT_X - TC.COMBO_NUMBER_LABEL_WIDTH * 1.01f,
                        TC.GRID_TOP_Y - TC.CELL_SIZE * (TC.ROWS / 2) - (process * (TC.CELL_SIZE * 3.5f / 2)),
                        TC.COMBO_NUMBER_LABEL_WIDTH, TC.COMBO_NUMBER_LABEL_HEIGHT * process)
            }
            game.batch.draw(SpriteArea.gameSprites["tcombo${tetrisGame.comboCount.toString().last() - 1}"],
                    TC.COMBO_NUMBER_LABEL_LEFT_X,
                    TC.GRID_TOP_Y - TC.CELL_SIZE * (TC.ROWS / 2) - (process * (TC.CELL_SIZE * 3.5f / 2)),
                    TC.COMBO_NUMBER_LABEL_WIDTH, TC.COMBO_NUMBER_LABEL_HEIGHT * process)
            game.batch.draw(SpriteArea.gameSprites["tcombo"], TC.COMBO_LABEL_LEFT_X,
                    TC.GRID_TOP_Y - TC.CELL_SIZE * (TC.ROWS / 2) - (process * (TC.CELL_SIZE * 3.5f / 2)),
                    TC.COMBO_LABEL_WIDTH, TC.COMBO_LABEL_HEIGHT * process)
            game.batch.setColor(c.r, c.g, c.b, 1f)
        }
    }

    private fun drawTetrisB2BLabel() {
        val c = game.batch.color
        if (tetrisGame.b2bTime.isRunning() && tetrisGame.b2bTime.runtime() < 4000f) {
            var process: Float = tetrisGame.b2bTime.runtime() / tetrisGame.b2bTime.delay.toFloat()
            if (process > 1) {
                game.batch.setColor(c.r, c.g, c.b, (3 - (tetrisGame.b2bTime.runtime() - tetrisGame.b2bTime.delay) / 1000f))
                process = 1f
            }
            game.batch.draw(SpriteArea.gameSprites["b2b"], TC.B2B_LABEL_LEFT_X,
                    TC.GRID_TOP_Y - TC.CELL_SIZE * (TC.ROWS / 2) - (process * (TC.CELL_SIZE * 3.5f / 2)) - TC.CELL_SIZE * 4f,
                    TC.B2B_LABEL_WIDTH, TC.B2B_LABEL_HEIGHT * process)
            game.batch.setColor(c.r, c.g, c.b, 1f)
        }
    }

    private fun drawTetrisTSpinLabel() {
        val c = game.batch.color
        var sprite: TextureRegion? = null
        var time: Time? = tetrisGame.getCurrentTSpinTimer()
        when (time) {
            tetrisGame.tSpin1Time -> sprite = SpriteArea.gameSprites["tspinsingle"]
            tetrisGame.tSpin2Time -> sprite = SpriteArea.gameSprites["tspindouble"]
            tetrisGame.tSpin3Time -> sprite = SpriteArea.gameSprites["tspintriple"]
            tetrisGame.tSpinMiniTime -> sprite = SpriteArea.gameSprites["tspinsinglemini"]
            tetrisGame.tSpin0Time -> sprite = SpriteArea.gameSprites["tspin"]
            tetrisGame.tSpinMini0Time -> sprite = SpriteArea.gameSprites["tspinmini"]
        }

        if (time != null) {
            if (time.runtime() < 4000f) {
                var process: Float = time.runtime() / time.delay.toFloat()
                if (process > 1) {
                    game.batch.setColor(c.r, c.g, c.b, (3 - (time.runtime() - time.delay) / 1000f))
                    process = 1f
                }
                var width = if (time != tetrisGame.tSpinMini0Time) TC.T_SPIN_LABEL_WIDTH else TC.T_SPIN_ZERO_LABEL_WIDTH // t spin mini zero is the only label with different size
                var height = if (time != tetrisGame.tSpinMini0Time) TC.T_SPIN_LABEL_HEIGHT else TC.T_SPIN_ZERO_LABEL_HEIGHT
                game.batch.draw(sprite, TC.B2B_LABEL_LEFT_X,
                        TC.GRID_TOP_Y - TC.CELL_SIZE * (TC.ROWS / 2) - (process * (TC.CELL_SIZE * 3.5f / 2)) - TC.CELL_SIZE * 7f,
                        width, height * process)
                game.batch.setColor(c.r, c.g, c.b, 1f)
            }
        }
    }

    private fun drawTetrisTetrisLabel() {
        val c = game.batch.color
        if (tetrisGame.tetrisTime.isRunning() && tetrisGame.tetrisTime.runtime() < 2000f) {
            var process: Float = tetrisGame.tetrisTime.runtime() / tetrisGame.tetrisTime.delay.toFloat()
            if (process > 1) {
                game.batch.setColor(c.r, c.g, c.b, (1 - (tetrisGame.tetrisTime.runtime() - tetrisGame.tetrisTime.delay) / 1000f))
                process = 1f
            }
            game.batch.draw(SpriteArea.gameSprites["tetris"], TC.TETRIS_LEFT_X,
                    TC.GRID_TOP_Y - ((TC.ROWS - 3) * TC.CELL_SIZE) - ((TC.TETRIS_HEIGHT * process) / 2),
                    TC.TETRIS_WIDTH, TC.TETRIS_HEIGHT * process)
            game.batch.setColor(c.r, c.g, c.b, 1f)
        }
    }

    private fun drawTetrisGridBackground() {
        Gdx.gl.glEnable(GL20.GL_BLEND)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(1/255f, 3/255f, 7/255f, 0.8f)
        shapeRenderer.rect(TC.GRID_LEFT_X, TC.GRID_TOP_Y - ((TC.ROWS - 1) * TC.CELL_SIZE), (TC.COLUMNS * TC.CELL_SIZE), ((TC.ROWS - 1) * TC.CELL_SIZE))
        shapeRenderer.end()
        Gdx.gl.glDisable(GL20.GL_BLEND)
    }

    private fun drawTetrisGridTexture(){
        game.batch.draw(SpriteArea.bgSprites["grid-bg"],
                        TC.GRID_LEFT_X-20, TC.GRID_TOP_Y - ((TC.ROWS - 1) * TC.CELL_SIZE)-20,
                        (TC.COLUMNS * TC.CELL_SIZE)+40, ((TC.ROWS - 1) * TC.CELL_SIZE)+40)
    }

    private fun drawTetrominos(){
        for (i in tetrisGame.cells.indices) { // invisible rows are nice
            for (j in 1 until tetrisGame.cells[i].size) {
                if (tetrisGame.cells[i][j] != null) {
                    if (!tetrisGame.getFullRows().contains(j) || tetrisGame.currentTetromino.isFalling) {
                        game.batch.draw(tetrisGame.cells[i][j].texture, i.toFloat() * TC.CELL_SIZE + TC.GRID_LEFT_X, TC.GRID_TOP_Y - (j.toFloat() * TC.CELL_SIZE),
                                TC.CELL_SIZE, TC.CELL_SIZE)
                    }
                }
            }
        }
    }

    private fun drawTetrisNextBg(){
        game.batch.draw(SpriteArea.bgSprites["next2-bg"],
                TC.NEXT_BLOCK_FIELD_X, TC.NEXT_BLOCK_FIELD_Y, TC.NEXT_BLOCK_FIELD_SIZE, TC.NEXT_BLOCK_FIELD_SIZE)
        for (i in 0..9 step 3) {
            game.batch.draw(SpriteArea.bgSprites["next2-bg"],
                    TC.NEXT_BLOCK_FIELD_X, TC.NEXT_BLOCK_FIELD2_Y - (i * (TC.CELL_SIZE)),
                    TC.NEXT_BLOCK_FIELD2_WIDTH, TC.NEXT_BLOCK_FIELD2_HEIGHT)
        }
    }

    private fun drawNextTetrominos(){
        nextFont.draw(game.batch, "NEXT", TC.NEXT_BLOCK_FIELD_X + (TC.CELL_SIZE * 0.4f), TC.GRID_TOP_Y - (TC.CELL_SIZE * 0.4f))
        var nextBlock: Tetromino = tetrisGame.nextTetrominos.peek()
        for (i in nextBlock.shape.indices) { // beeg
            for (j in 0 until nextBlock.shape[i].size) {
                if (nextBlock.shape[i][j] != null) {
                    game.batch.draw(nextBlock.shape[i][j].texture,
                            TC.NEXT_BLOCK_FIELD_X + ((TC.NEXT_BLOCK_FIELD_SIZE - (nextBlock.width * 0.9f)) / 2f) + ((i - nextBlock.firstColumn()) * TC.CELL_SIZE * 0.9f) - 2,
                            TC.GRID_TOP_Y - (TC.CELL_SIZE * 0.9f) - ((TC.NEXT_BLOCK_FIELD_SIZE - (nextBlock.height * 0.9f)) / 2) - ((j - nextBlock.firstRow()) * TC.CELL_SIZE * 0.9f),
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
                                TC.NEXT_BLOCK_FIELD_X + ((TC.NEXT_BLOCK_FIELD2_WIDTH - (nextBlock.width * 0.7f)) / 2f) + ((i - nextBlock.firstColumn()) * TC.CELL_SIZE * 0.7f) - 2,
                                TC.NEXT_BLOCK_FIELD2_TOP_Y - (((field * 3) + 0.7f) * TC.CELL_SIZE) - ((TC.NEXT_BLOCK_FIELD2_HEIGHT - (nextBlock.height * 0.7f)) / 2) - ((j - nextBlock.firstRow()) * TC.CELL_SIZE * 0.7f),
                                TC.CELL_SIZE * 0.7f, TC.CELL_SIZE * 0.7f)
                    }
                }
            }
        }
    }

    private fun drawTetrisShadow() {
        val c = game.batch.color
        game.batch.setColor(c.r, c.g, c.b, 0.4f)

        var shadowShape: MutableList<Pair<Int, Int>>? = tetrisGame.getShadowCoordinates()
        if (shadowShape != null) {
            for (pair in shadowShape) {
                game.batch.draw(tetrisGame.currentTetromino.texture, pair.first.toFloat() * TC.CELL_SIZE + TC.GRID_LEFT_X,
                        TC.GRID_TOP_Y - (pair.second.toFloat() * TC.CELL_SIZE), TC.CELL_SIZE, TC.CELL_SIZE)
            }
        }
        game.batch.setColor(c.r, c.g, c.b, 1f)
    }

    private fun drawTetrisGarbageQueue() {
        var garbage = tetrisGame.scoring.tetrisGarbage
        var count = 0
        var factor: Float
        while (garbage > 0 && count < PC.GRID_WIDTH) {
            val closest = GC.garbageSteps.last { it <= garbage }
            factor = if (closest == 1) 0.8f else 1f
            game.batch.draw(SpriteArea.gameSprites["tgarbage-queue$closest"],
                    TC.GRID_LEFT_X + (count * (TC.CELL_SIZE * 1.75f)), SCREEN_HEIGHT * 0.89f,
                    TC.CELL_SIZE * factor, TC.CELL_SIZE * factor)
            garbage -= closest
            count++
        }
    }

    private fun drawScore() {
        scoreFont.draw(game.batch, "${"0".repeat(8-puyoController.getCurrentScore().length)}${puyoController.getCurrentScore()}", PC.GRID_START_X*1.3f, PC.GRID_START_Y - PC.GRID_START_Y * 0.87f)
        scoreFont.draw(game.batch, tetrisGame.scoring.getScoreString(), TC.GRID_LEFT_X + TC.CELL_SIZE * 0.6f,
                TC.GRID_TOP_Y - TC.GRID_TOP_Y * 0.87f)
    }

    private fun drawTetrisGrid(){
        Gdx.gl.glEnable(GL20.GL_BLEND)
        shapeRenderer.setAutoShapeType(true)
        shapeRenderer.setColor(71/255f, 62/255f, 95/255f, 1f)
        shapeRenderer.begin()

        for (i in 0 until tetrisGame.rows) { // invisible rows are nice
            shapeRenderer.rectLine(TC.GRID_LEFT_X, TC.GRID_TOP_Y - (i * TC.CELL_SIZE),
                    TC.GRID_RIGHT_X, TC.GRID_TOP_Y - (i * TC.CELL_SIZE), 1f)
        }

        for (j in 0..tetrisGame.columns) {
            shapeRenderer.rectLine(TC.GRID_LEFT_X + (j * TC.CELL_SIZE), TC.GRID_TOP_Y,
                    TC.GRID_LEFT_X + (j * TC.CELL_SIZE), TC.GRID_BOTTOM_Y, 1f)
        }

        Gdx.gl.glDisable(GL20.GL_BLEND)
        shapeRenderer.end()
    }

    private fun drawHeldTetrominoBg() {
        game.batch.draw(SpriteArea.bgSprites["hold-bg"],
                TC.HOLD_FIELD_X, TC.GRID_TOP_Y - TC.HOLD_FIELD_SIZE, TC.HOLD_FIELD_SIZE, TC.HOLD_FIELD_SIZE)
    }

    private fun drawHeldTetromino() {
        nextFont.draw(game.batch, "HOLD", TC.HOLD_FIELD_X + (TC.CELL_SIZE * 0.2f), TC.GRID_TOP_Y - (TC.CELL_SIZE * 0.15f))
        if (tetrisGame.heldTetromino != null) {
            val heldBlock: Tetromino = tetrisGame.heldTetromino!!
            for (i in heldBlock.shape.indices) {
                for (j in 0 until heldBlock.shape[i].size) {
                    if (heldBlock.shape[i][j] != null) {
                        game.batch.draw(heldBlock.shape[i][j].texture,
                                TC.HOLD_FIELD_X + ((TC.HOLD_FIELD_SIZE - (heldBlock.width * 0.9f)) / 2f) + ((i - heldBlock.firstColumn()) * TC.CELL_SIZE * 0.9f),
                                TC.GRID_TOP_Y - (TC.CELL_SIZE * 0.9f) - ((TC.CELL_SIZE * 4.5f - (heldBlock.height * 0.9f)) / 2) - ((j - heldBlock.firstRow()) * TC.CELL_SIZE * 0.9f),
                                TC.CELL_SIZE * 0.9f, TC.CELL_SIZE * 0.9f)
                    }
                }
            }
        }
    }

    private fun drawTetrisEffects() {
        drawTetrisRemoveLine()
        drawHardDropEffect()
    }

    private fun drawTetrisRemoveLine() {
        if (tetrisGame.removeLineTime.isRunning()) {
            var fullRows = tetrisGame.getFullRows()
            for (row in 0 until fullRows.size) {
                var count = 1
                while (fullRows.contains(fullRows[row] + count)) count++
                if (count == 1) {
                    count = 1
                    while (fullRows.contains(fullRows[row] - count)) count++
                    var time: Float = tetrisGame.removeLineTime.runtime() / 1000f
                    game.batch.draw(SpriteArea.tEffectSprites["full-line"], TC.GRID_LEFT_X - 3f,
                            TC.GRID_TOP_Y - ((fullRows[row]) * TC.CELL_SIZE) - (3f * count) + (time * 2f * TC.CELL_SIZE * count),
                            TC.COLUMNS * TC.CELL_SIZE + 6f,
                            (TC.CELL_SIZE + 6f) * count - (time * 4f * TC.CELL_SIZE * count))
                    game.batch.draw(SpriteArea.tEffectSprites["erase-big"],
                            TC.GRID_LEFT_X + (time * ((TC.CELL_SIZE * TC.COLUMNS) / 0.27f)) - (1.5f * TC.CELL_SIZE),
                            TC.GRID_TOP_Y - ((fullRows[row] + 1) * TC.CELL_SIZE - (count * 0.25f) / 2),
                            8f * TC.CELL_SIZE, TC.CELL_SIZE * (count + 2f + (count * 0.25f)))
                }
            }
        }
    }

    private fun drawHardDropEffect() {
        if(tetrisGame.hardDropTime.isRunning()) {
            var block = tetrisGame.currentTetromino
            var timeProcess: Float = tetrisGame.hardDropTime.runtime().toFloat() / tetrisGame.hardDropTime.delay

            var c = game.batch.color
            game.batch.setColor(c.r, c.g, c.b, 1 - timeProcess)
            game.batch.draw(SpriteArea.tEffectSprites["hdrop-line${block.getColumns()}"],
            TC.GRID_LEFT_X + (block.column + (block.firstColumn() - block.pivotX)) * TC.CELL_SIZE,
            TC.GRID_TOP_Y - ((block.row) + (block.lastRow() - block.pivotY)) * TC.CELL_SIZE + timeProcess * (TC.CELL_SIZE * 2),
            block.getColumns() * TC.CELL_SIZE, TC.CELL_SIZE * (block.getRows() + 1))
            var effects = arrayOf(SpriteArea.tEffectSprites["big-twinkle"],
                    SpriteArea.tEffectSprites["twinkle"],
                    SpriteArea.tEffectSprites["white-particle-s"],
                    SpriteArea.tEffectSprites["x-twinkle"]
            )
            for (i in 0 until block.getColumns()) {
                game.batch.draw(effects[i],
                        TC.GRID_LEFT_X + (block.column + (block.firstColumn() - block.pivotX) + i) * TC.CELL_SIZE,
                        TC.GRID_TOP_Y - ((block.row) + (block.lastRow() - block.pivotY) + i % 2)
                                * TC.CELL_SIZE + timeProcess * (TC.CELL_SIZE * 2) + TC.CELL_SIZE * (block.getRows() + 1),
                        TC.CELL_SIZE, TC.CELL_SIZE)
            }
            game.batch.setColor(c.r, c.g, c.b, 1f)
        }
    }

    /// Puyo Puyo

    private fun drawPuyoPreview(){
        if(puyoController.displayPreview() && puyosToPop.isEmpty()){
            val c = game.batch.color
            game.batch.setColor(c.r, c.g, c.b, 0.8f)

            val puyo = puyoController.getCurrentPuyo()
            val coords = puyoController.getPreviewCoords()
            if(coords[0][1] >= 0) game.batch.draw(puyo.first.sprites["dot"],
                    PC.GRID_START_X+PC.CELL_SIZE*coords[0][0]+PC.CELL_SIZE*0.1f, PC.GRID_START_Y-coords[0][1]*PC.CELL_SIZE, PC.CELL_SIZE*0.75f, PC.CELL_SIZE*0.75f)
            if(coords[1][1] >= 0) game.batch.draw(puyo.second.sprites["dot"],
                    PC.GRID_START_X+PC.CELL_SIZE*coords[1][0]+PC.CELL_SIZE*0.1f, PC.GRID_START_Y-PC.CELL_SIZE*coords[1][1], PC.CELL_SIZE*0.75f, PC.CELL_SIZE*0.75f)

            game.batch.setColor(c.r, c.g, c.b, 1f)
        }
    }

    private fun drawGarbageQueue(){
        if(!puyoController.displayGarbage()) return

        val garbageSprites = SpriteArea.gameSprites
        var garbage = puyoController.getGarbage()
        var count = 0
        do {
            val closest = GC.garbageSteps.last { it <= garbage }
            game.batch.draw(garbageSprites["garbage-queue$closest"],
            PC.GRID_START_X+PC.CELL_SIZE*count*0.75f, SCREEN_HEIGHT * 0.88f, PC.CELL_SIZE*0.75f, PC.CELL_SIZE*0.75f)
            garbage -= closest
            count++
        } while (garbage > 0 && count < PC.GRID_WIDTH)
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

    private fun drawPuyoBgTexture(){
        game.batch.draw(SpriteArea.bgSprites["puyo-bg"]!!,
                        PC.GRID_START_X-20, PC.GRID_START_Y - (PC.GRID_LENGTH * PC.CELL_SIZE - PC.CELL_SIZE)-20,
                        PC.GRID_WIDTH * PC.CELL_SIZE+40, (PC.GRID_LENGTH - 1) * PC.CELL_SIZE+40)
    }

    private fun drawPuyoBg(){
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(0.05f, 0.05f, 0.05f, 0.8f)
        shapeRenderer.rect(PC.GRID_START_X, PC.GRID_START_Y - (PC.GRID_LENGTH * PC.CELL_SIZE - PC.CELL_SIZE), PC.GRID_WIDTH * PC.CELL_SIZE, (PC.GRID_LENGTH - 1) * PC.CELL_SIZE)
        shapeRenderer.end()

        drawNextPuyoBg()
        Gdx.gl.glDisable(GL20.GL_BLEND)
    }

    private fun drawNextPuyoBg(){
        drawRoundedRect(PC.GRID_START_X * 1.2f + PC.GRID_WIDTH * PC.CELL_SIZE, PC.GRID_START_Y * 0.8f, PC.CELL_SIZE * 1.5f, PC.CELL_SIZE * 2.6f, 10f)
        drawRoundedRect(PC.GRID_START_X * 1.3f + PC.GRID_WIDTH * PC.CELL_SIZE, PC.GRID_START_Y * 0.65f, PC.CELL_SIZE * 1.25f, PC.CELL_SIZE * 2f, 10f)
    }

    private fun drawBounce(puyo: PuyoBlock) {
        if(puyo.bounceDelay.hasPassed()){
            puyo.bounceFrame++
            when(puyo.bounceFrame){
                1, 7, 8 -> {
                    puyo.width = 0.8f
                    puyo.length = 1f
                }
                in 3..5, in 10..12 -> {
                    puyo.width = 1f
                    puyo.length = 0.8f
                }
                else -> {
                    puyo.width = 1f
                    puyo.length = 1f
                }
            }
            if(puyo.bounceFrame > 12){
                puyo.bounceOver = true
                puyo.bounceFrame = 0
            }
            puyo.bounceDelay.reset()
        }
        val x = if(puyo.width < 1f) PC.CELL_SIZE * puyo.width * 0.1f else 0f
        game.batch.draw(puyo.currentSprite, PC.GRID_START_X + puyo.x * PC.CELL_SIZE + x,
                PC.GRID_START_Y - puyo.y * PC.CELL_SIZE, PC.CELL_SIZE * puyo.width, PC.CELL_SIZE * puyo.length)
    }

    private fun drawMainPuyos(){
        val puyos = puyoController.puyoGame.puyo
        if(puyos.first.isFalling && puyos.first.bounceOver){
            if(puyos.gap == 0.5f && puyos.first.y == 0){
                game.batch.draw(SpriteArea.cutPuyoSprites[puyos.first.currentSprite], PC.GRID_START_X + puyos.first.x * PC.CELL_SIZE,
                        PC.GRID_START_Y - puyos.first.y * PC.CELL_SIZE - PC.CELL_SIZE*puyos.gap, PC.CELL_SIZE, PC.CELL_SIZE/2)
            } else {
                game.batch.draw(puyos.first.currentSprite, PC.GRID_START_X + puyos.first.x * PC.CELL_SIZE,
                        PC.GRID_START_Y - puyos.first.y * PC.CELL_SIZE - PC.CELL_SIZE*puyos.gap, PC.CELL_SIZE, PC.CELL_SIZE)
            }
        }
        if(puyos.second.isFalling && puyos.second.bounceOver){
            game.batch.draw(puyos.second.currentSprite, PC.GRID_START_X + puyos.second.x * PC.CELL_SIZE,
                    PC.GRID_START_Y - puyos.second.y * PC.CELL_SIZE - PC.CELL_SIZE*puyos.gap, PC.CELL_SIZE, PC.CELL_SIZE)
        }
    }

    private val crossAnim = CrossAnimation()
    private var isFlipped = false

    private fun drawCross(){
        if(crossAnim.start.hasPassed() && crossAnim.frame == 0){
            crossAnim.updateSprite()
            crossAnim.start.reset()
            crossAnim.delay.reset()
        } else if(crossAnim.frame > 0 && crossAnim.delay.hasPassed()){
            crossAnim.updateSprite()
            crossAnim.start.reset()
            crossAnim.delay.reset()
        }

        if(crossAnim.flip && !isFlipped){
            crossAnim.currentSprite.flip(true, false)
            isFlipped = true
        } else if(!crossAnim.flip && isFlipped){
            crossAnim.currentSprite.flip(true, false) // flipped back
            isFlipped = false
        }

        game.batch.draw(crossAnim.currentSprite,
                PC.GRID_START_X + 3 * PC.CELL_SIZE + PC.CELL_SIZE*0.07f,
                PC.GRID_START_Y - PC.CELL_SIZE,
                PC.CELL_SIZE*0.7f, PC.CELL_SIZE*0.7f)
    }

    private var puyosToPop = mutableListOf<PuyoBlock>()
    private var popTime = Time(50)
    private var shineStart = Time(3000)

    private fun drawPuyos(){
        drawMainPuyos()
        val c = game.batch.color
        for(i in 0 until PC.GRID_WIDTH){
            for(j in 0 until PC.GRID_LENGTH){
                val block = puyoController.getBlockAt(i, j)
                if(block == null || j == 0 ||
                  (block == puyoController.puyoGame.puyo.first && block.isFalling  && puyoController.puyoGame.puyo.first.bounceOver) ||
                  (block == puyoController.puyoGame.puyo.second && block.isFalling && puyoController.puyoGame.puyo.second.bounceOver)){
                    continue
                }
                if(block.isBeingRemoved) {
                    if (block.removeFrames in 20 until PC.POP_SPRITE_AT && block.flickerCount > 5) {
                        game.batch.setColor(c.r, c.g, c.b, 0.6f)
                    } else {
                        game.batch.setColor(c.r, c.g, c.b, 1f)
                    }
                    block.addFrameCount()
                } else {
                    game.batch.setColor(c.r, c.g, c.b, 1f)
                }

                if(block is GarbageBlock && block.frame == 0 && shineStart.hasPassed()){
                    block.changeSprite()
                    block.shineDuration.reset()
                } else if (block is GarbageBlock && block.frame > 0 && block.shineDuration.hasPassed()){
                    block.changeSprite()
                    block.shineDuration.reset()
                    shineStart.reset()
                }

                if(!(block is PuyoBlock && block.removeFrames >= PC.POP3_SPRITE_AT)) {
                    if(block is PuyoBlock && !block.bounceOver){
                        drawBounce(block)
                    } else {
                        game.batch.draw(block.currentSprite, PC.GRID_START_X + i * PC.CELL_SIZE, PC.GRID_START_Y - j * PC.CELL_SIZE, PC.CELL_SIZE, PC.CELL_SIZE)
                    }
                    if (block is PuyoBlock && block.removeFrames >= PC.POP_SPRITE_AT && !puyosToPop.contains(block)) {
                        puyosToPop.add(block)
                    }
                }
            }
        }

        game.batch.setColor(c.r, c.g, c.b, 1f)
        drawPop()
        if(popTime.hasPassed()){
            updatePop()
            popTime.reset()
        }
    }

    private var chainCount = 0
    private var numX = 0
    private var numY = 0
    private var displayChainTime = Time(500)
    private var startShowing = false
    private var frames = 0


    private fun drawPuyoLabels(){
        val c = game.batch.color
        if(!displayChainTime.hasPassed()){ // change sprites
            if(chainCount > 0){
                if (frames <= 30) {
                    game.batch.setColor(c.r, c.g, c.b, 1-frames/30f)
                } else {
                    frames = 0
                }
                game.batch.draw(SpriteArea.gameSprites["pchain"],
                        PC.GRID_START_X + numX * PC.CELL_SIZE - 57, PC.GRID_START_Y - numY * PC.CELL_SIZE, 99f, 26f)
                val chainString = chainCount.toString()
                for(i in chainString.indices){
                    game.batch.draw(SpriteArea.gameSprites["p${chainString[i]}"],
                    PC.GRID_START_X + numX * PC.CELL_SIZE + 57 + i*24f, PC.GRID_START_Y - numY * PC.CELL_SIZE, 25f, 29f)
                }
                frames++
            }
        } else if(puyoController.chainCount == 0){
            chainCount = 0
            frames = 0
        }
        game.batch.setColor(c.r, c.g, c.b, 1f)
    }

    private fun drawPop(){
        puyoController.puyoGame.animationDone = puyosToPop.isEmpty()
        for(puyo in puyosToPop){
            val x = PC.GRID_START_X + puyo.x * PC.CELL_SIZE
            val y = PC.GRID_START_Y - puyo.y * PC.CELL_SIZE
            val pop = puyo.pop


            if(pop.coords[0][0] != 0f){
                if(pop.coords[2][0] != 0f){
                    game.batch.draw(puyo.sprites["dot"], x+pop.coords[2][0], y+pop.coords[2][1], pop.secondSize, pop.secondSize)
                    game.batch.draw(puyo.sprites["dot"], x+pop.coords[3][0], y+pop.coords[3][1], pop.secondSize, pop.secondSize)
                }
                game.batch.draw(puyo.sprites["dot"], x+pop.coords[0][0], y+pop.coords[0][1], pop.firstSize, pop.firstSize)
                game.batch.draw(puyo.sprites["dot"], x+pop.coords[1][0], y+pop.coords[1][1], pop.firstSize, pop.firstSize)
            }
        }

        if(puyoController.chainCount > chainCount && puyosToPop.isNotEmpty()){
            val puyo = puyosToPop[puyosToPop.size/2]
            numX = puyo.x
            numY = puyo.y
            chainCount = puyoController.chainCount
            startShowing = true
        }

        if(startShowing){
            displayChainTime.reset()
            startShowing = false
        }
        drawPuyoLabels()
    }

    private fun updatePop(){
        val iterator = puyosToPop.listIterator()
        while(iterator.hasNext()){
            val puyo = iterator.next()
            val pop = puyo.pop

            if(pop.frames > 7){
                iterator.remove()
                continue
            }

            pop.update()
        }
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