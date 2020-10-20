package com.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.FitViewport
import com.game.puyo.*
import com.game.Tetris.*

const val SCREEN_WIDTH = 1700f
const val SCREEN_HEIGHT = 1040f

class GameScreen(val game: PuyoPuyoTetris) : Screen {
    private var tetrisGame: TetrisGame = TetrisGame()
    private val puyoController = Controller()

    private var camera = OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT)
    private var shapeRenderer = ShapeRenderer()
    private var viewport : FitViewport
    private val titleFont = game.generateTitleFont(55)
    private val scoreFont = game.generateScoreFont(50)
    private val background = Texture(Gdx.files.internal("animations/bg/frame (1).gif"))
    private val bgm = Gdx.audio.newMusic(Gdx.files.internal("music/corona.mp3"));
    //private val bgGif = GifAnimation("bg", 121, 0.1f)

    // Tetris
    private val nextFont = game.generateTetrisNextFont(25)

    init {
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f)
        viewport = FitViewport(camera.viewportWidth, camera.viewportHeight, camera)
        viewport.setScreenPosition(0, 0)
        puyoController.setTetris(tetrisGame)
        tetrisGame.setPuyo(puyoController.puyoGame)
        Sounds.start.play()
        //bgm.play()
    }

    override fun render(delta: Float) {
        game.batch.projectionMatrix = camera.combined
        shapeRenderer.projectionMatrix = camera.combined
        Gdx.gl.glClearColor(0f, 2/255f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        /// Background
        game.batch.begin()
        //game.batch.draw(bgGif.update(delta), 0f, 0f, SCREEN_WIDTH, SCREEN_HEIGHT)
        game.batch.draw(background, 0f, 0f, SCREEN_WIDTH, SCREEN_HEIGHT)
        drawPuyoBgTexture()
        drawTetrisGridTexture()
        game.batch.end()

        /// Puyo Controller
        puyoController.readInput()
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
        drawTetrisCombo()
        game.batch.end()
        /// -End Draw-

        //println(Gdx.graphics.framesPerSecond)
    }

    /// Tetris Methods

    private fun drawTetrisCombo() {
        if (tetrisGame.comboTime.isRunning()) {
            var process: Float = tetrisGame.comboTime.runtime() / tetrisGame.comboTime.delay.toFloat()
            if (process > 1) process = 1f
            game.batch.draw(SpriteArea.bgSprites["tcombo"],
                    TC.COMBO_LABEL_LEFT_X, TC.GRID_TOP_Y - TC.CELL_SIZE * (TC.ROWS / 2) - (process * (TC.CELL_SIZE * 3.5f / 2)),
                    TC.COMBO_LABEL_WIDTH, TC.COMBO_LABEL_HEIGHT * process)
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
                    game.batch.draw(SpriteArea.tEffectSprites["erase-big"],
                            TC.GRID_LEFT_X + (time * ((TC.CELL_SIZE * TC.COLUMNS) / 0.27f)) - (1.5f * TC.CELL_SIZE),
                            TC.GRID_TOP_Y - ((fullRows[row] + (count - 1)) * TC.CELL_SIZE) - ((1.5f * TC.CELL_SIZE)),
                            8f * TC.CELL_SIZE, (4f * TC.CELL_SIZE) * count)
                    game.batch.draw(SpriteArea.tEffectSprites["full-line"], TC.GRID_LEFT_X - 3f,
                            TC.GRID_TOP_Y - ((fullRows[row]) * TC.CELL_SIZE) - (3f * count) + (time * 2f * TC.CELL_SIZE * count),
                            TC.COLUMNS * TC.CELL_SIZE + 6f,
                            (TC.CELL_SIZE + 6f) * count - (time * 4f * TC.CELL_SIZE * count))
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
                PC.GRID_START_X + 3 * PC.CELL_SIZE,
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
    private var isDisplayed = false

    private fun drawPuyoLabels(){
        if(!displayChainTime.hasPassed()){
            game.batch.draw(SpriteArea.gameSprites["p$chainCount"],
            PC.GRID_START_X + numX * PC.CELL_SIZE, PC.GRID_START_Y - numY * PC.CELL_SIZE, 40f, 60f)
            isDisplayed = true
        } else {
            chainCount = 0
        }
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
        if(puyoController.chainCount > 0 && puyosToPop.isNotEmpty()){
            val puyo = puyosToPop[puyosToPop.size/2]
            chainCount = puyoController.chainCount
            numX = puyo.x
            numY = puyo.y
            isDisplayed = false
        }
        if(chainCount > 0){
            if(!isDisplayed){
                displayChainTime.reset()
            }
            drawPuyoLabels()
        }
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

            if(pop.frames in 1..2){
                pop.updateFirst(PC.CELL_SIZE * 0.1f, PC.CELL_SIZE * 0.53f,
                                pop.coords[0][0] + pop.firstSize * 0.72f, PC.CELL_SIZE * 0.52f,pop.firstSize)
            }
            if(pop.frames in 2..3){
                pop.updateSecond(pop.coords[0][0] - pop.firstSize * 0.45f, pop.coords[0][1] + pop.firstSize * 0.4f,
                                 pop.coords[1][0] + pop.firstSize * 0.2f, pop.coords[1][1] + pop.firstSize * 0.35f, pop.secondSize)
            }
            if(pop.frames == 3){
                pop.updateFirst(pop.coords[2][0] - pop.secondSize * 0.2f, pop.coords[2][1] + pop.secondSize * 0.26f,
                                pop.coords[3][0] + pop.secondSize * 0.2f, pop.coords[3][1] + pop.secondSize * 0.26f, PC.CELL_SIZE * 0.7f)
            }
            if(pop.frames == 4){
                pop.updateSecond(pop.coords[2][0] - pop.secondSize * 0.2f, pop.coords[2][1] + pop.secondSize * 0.26f,
                                 pop.coords[3][0] + pop.secondSize * 0.2f, pop.coords[3][1] + pop.secondSize * 0.26f, PC.CELL_SIZE * 0.7f)
                pop.updateFirst(pop.coords[0][0] - pop.secondSize * 0.2f, pop.coords[0][1] + pop.secondSize * 0.13f,
                                pop.coords[1][0] + pop.secondSize * 0.2f , pop.coords[1][1] + pop.secondSize * 0.11f, PC.CELL_SIZE * 0.77f)
            }
            if(pop.frames == 5){
                pop.updateSecond(pop.coords[0][0], pop.coords[0][1], pop.coords[1][0], pop.coords[1][1], pop.firstSize)
                pop.updateFirst(pop.coords[0][0] - pop.secondSize * 0.1f, pop.coords[0][1] + pop.secondSize * 0.1f,
                                pop.coords[1][0] + pop.secondSize * 0.45f , pop.coords[1][1] + pop.secondSize * 0.1f, PC.CELL_SIZE * 0.6f)
            }
            if(pop.frames == 6){
                pop.updateSecond(pop.coords[2][0] - pop.secondSize * 0.1f, pop.coords[2][1] + pop.secondSize * 0.1f,
                                 pop.coords[3][0] + pop.secondSize * 0.1f, pop.coords[3][1] + pop.secondSize * 0.1f, PC.CELL_SIZE * 0.5f)
                pop.updateFirst(pop.coords[0][0] - pop.secondSize * 0.25f, pop.coords[0][1],
                                pop.coords[1][0] + pop.secondSize * 0.25f , pop.coords[1][1], PC.CELL_SIZE * 0.4f)
            }
            if(pop.frames == 7){
                pop.updateSecond(pop.coords[2][0] - pop.secondSize * 0.3f, pop.coords[2][1] + pop.secondSize * 0.1f,
                                 pop.coords[3][0] + pop.secondSize * 0.2f, pop.coords[3][1] + pop.secondSize * 0.1f, PC.CELL_SIZE * 0.4f)
                pop.updateFirst(pop.coords[0][0] - pop.secondSize * 0.35f, pop.coords[0][1],
                                pop.coords[1][0] + pop.firstSize * 0.1f , pop.coords[1][1], PC.CELL_SIZE * 0.3f)
            }
            if(pop.frames == 8){
                pop.updateSecond(pop.coords[2][0] - pop.secondSize * 0.3f, pop.coords[2][1] + pop.secondSize * 0.1f,
                                 pop.coords[3][0] + pop.secondSize * 0.15f, pop.coords[3][1] + pop.secondSize * 0.1f, PC.CELL_SIZE * 0.3f)
                pop.updateFirst(pop.coords[0][0] - pop.secondSize * 0.3f, pop.coords[0][1],
                                pop.coords[1][0] + pop.firstSize * 0.1f , pop.coords[1][1], PC.CELL_SIZE * 0.2f)
            }

            pop.frames++
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