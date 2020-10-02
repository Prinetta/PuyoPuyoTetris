package com.puyo

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.FitViewport
import java.lang.System.currentTimeMillis
import kotlin.random.Random

class Screen(val game: PuyoPuyoTetris) : Screen {
    private val grid = Grid(6, 13)
    private var lastInputTime = currentTimeMillis()
    private var lastChainTime = currentTimeMillis()
    private var lastDropTime = currentTimeMillis()
    private val puyoColors = PuyoColors.values()
    private var puyoChain = mutableListOf<List<Block>>()
    private var chainIndex = -1
    private var allBlocksStanding = true
    private var nextPuyos = mutableListOf<Puyo>()
    private var removedPuyos = mutableListOf<List<Block>>()

    val SCREEN_WIDTH = 1500f
    val SCREEN_HEIGHT = 1040f
    val CELL_SIZE = 65f
    val GRID_START_X = SCREEN_WIDTH*0.1f
    val GRID_START_Y = SCREEN_HEIGHT*0.13f + grid.length*CELL_SIZE-CELL_SIZE

    var camera = OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT)
    var shapeRenderer = ShapeRenderer()
    var viewport : FitViewport
    val titleFont = game.generateTitleFont(55)
    val scoreFont = game.generateScoreFont(50)
    val background = Texture(Gdx.files.internal("background.png"))
    val guiBatch = SpriteBatch()
    val scoring = Scoring()

    private lateinit var puyo: Puyo

    init {
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f)
        viewport = FitViewport(camera.viewportWidth, camera.viewportHeight, camera)
        viewport.setScreenPosition(0, 0)
        generatePuyoList()
        spawnPuyo()
    }

    private fun generatePuyoList(){
        nextPuyos.addAll(listOf(
                Puyo(Block(grid.width / 2, 0, puyoColors[Random.nextInt(0, puyoColors.size)]), Block(grid.width / 2, 1, puyoColors[Random.nextInt(0, puyoColors.size)])),
                Puyo(Block(grid.width / 2, 0, puyoColors[Random.nextInt(0, puyoColors.size)]), Block(grid.width / 2, 1, puyoColors[Random.nextInt(0, puyoColors.size)]))
        ))
    }

    override fun render(delta: Float) {
        game.batch.projectionMatrix = camera.combined
        guiBatch.projectionMatrix = camera.combined
        shapeRenderer.projectionMatrix = camera.combined;
        Gdx.gl.glClearColor(27 / 255f, 18 / 255f, 64 / 255f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.begin()
        game.batch.draw(background,
                0f, 0f, SCREEN_WIDTH, SCREEN_HEIGHT)
        game.batch.end()

        if(currentTimeMillis() - lastInputTime > 50 && !puyo.startedDrop()){
            when {
                Gdx.input.isKeyPressed(Input.Keys.LEFT) -> movePuyo(-1)
                Gdx.input.isKeyPressed(Input.Keys.RIGHT) -> movePuyo(1)
                Gdx.input.isKeyPressed(Input.Keys.E) -> rotatePuyo(1)
                Gdx.input.isKeyPressed(Input.Keys.Q) -> rotatePuyo(-1)
            }
            lastInputTime = currentTimeMillis();
        } else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            puyo.speed = puyo.maxSpeed
        } else {
            puyo.speed = puyo.minSpeed
        }

        if(chainIndex != -1) { // chain of four or more has been found
            if (currentTimeMillis() - lastChainTime > puyo.puyoChainSpeed) { // combo waits a bit before disappearing
                removePuyoChain() // current chain gets removed
                while (findBigPuyoChain() != -1){
                    removePuyoChain()  // another combo was found and is therefore simultaneous
                }
                if(chainIndex == -1){
                    lastChainTime = currentTimeMillis();
                }
            }
        } else {
            if (currentTimeMillis() - lastDropTime > puyo.speed) { // floating blocks still need to be dropped
                allBlocksStanding = !dropAllBlocks()
                lastDropTime = currentTimeMillis()
            } else if(allBlocksStanding){
                findBigPuyoChain()
                if(chainIndex == -1){
                    if(removedPuyos.isNotEmpty()){
                        scoring.calculate(removedPuyos)
                        sendTrash(scoring.trash)
                    }
                    if(currentTimeMillis() - puyo.dropTime > puyo.speed){
                        if(puyo.canSpawn() && !isColliding(grid.width / 2, 1)){
                            spawnPuyo()
                        }
                        puyo.dropTime = currentTimeMillis()
                    }
                }
            }
            lastChainTime = currentTimeMillis()
        }

        connectPuyos()
        drawBackground()

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
        game.batch.draw(nextPuyos[0].first.currentSprite, GRID_START_X * 1.2f + grid.width * CELL_SIZE + CELL_SIZE * 0.25f,
                GRID_START_Y * 0.8f + CELL_SIZE * 1.25f, CELL_SIZE, CELL_SIZE)
        game.batch.draw(nextPuyos[0].second.currentSprite, GRID_START_X * 1.2f + grid.width * CELL_SIZE + CELL_SIZE * 0.25f,
                GRID_START_Y * 0.8f + CELL_SIZE * 1.25f - CELL_SIZE, CELL_SIZE, CELL_SIZE)
        game.batch.draw(nextPuyos[1].first.currentSprite, GRID_START_X * 1.3f + grid.width * CELL_SIZE + CELL_SIZE * 0.25f,
                GRID_START_Y * 0.65f + CELL_SIZE, CELL_SIZE * 0.75f, CELL_SIZE * 0.75f)
        game.batch.draw(nextPuyos[1].second.currentSprite, GRID_START_X * 1.3f + grid.width * CELL_SIZE + CELL_SIZE * 0.25f,
                GRID_START_Y * 0.65f + CELL_SIZE * 0.25f, CELL_SIZE * 0.75f, CELL_SIZE * 0.75f)
    }

    private fun drawBackground(){
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(0.05f, 0.05f, 0.05f, 0.65f)
        shapeRenderer.rect(GRID_START_X, GRID_START_Y - (grid.length * CELL_SIZE - CELL_SIZE), grid.width * CELL_SIZE, (grid.length - 1) * CELL_SIZE)
        shapeRenderer.end()

        drawNextPuyoBg()
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private fun drawNextPuyoBg(){
        drawRoundedRect(GRID_START_X * 1.2f + grid.width * CELL_SIZE, GRID_START_Y * 0.8f, CELL_SIZE * 1.5f, CELL_SIZE * 2.6f, 10f)
        drawRoundedRect(GRID_START_X * 1.3f + grid.width * CELL_SIZE, GRID_START_Y * 0.65f, CELL_SIZE * 1.25f, CELL_SIZE * 2f, 10f)
    }

    private fun drawRoundedRect(x: Float, y: Float, width: Float, height: Float, radius: Float){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(0.05f, 0.05f, 0.05f, 0.65f)
        shapeRenderer.rect(x + radius, y + radius, width - 2 * radius, height - 2 * radius);

        // Four side rectangles, in clockwise order
        shapeRenderer.rect(x + radius, y, width - 2 * radius, radius);
        shapeRenderer.rect(x + width - radius, y + radius, radius, height - 2 * radius);
        shapeRenderer.rect(x + radius, y + height - radius, width - 2 * radius, radius);
        shapeRenderer.rect(x, y + radius, radius, height - 2 * radius);

        // Four arches, clockwise too
        shapeRenderer.arc(x + radius, y + radius, radius, 180f, 90f);
        shapeRenderer.arc(x + width - radius, y + radius, radius, 270f, 90f);
        shapeRenderer.arc(x + width - radius, y + height - radius, radius, 0f, 90f);
        shapeRenderer.arc(x + radius, y + height - radius, radius, 90f, 90f);
        shapeRenderer.end()
    }

    private fun connectPuyos(){
        for (chain in puyoChain){
            if(chain.size <= 1){
                continue
            } else if (chain.size >= 4){
                for(block in chain){
                    block.currentSprite = block.sprites.get("s")
                    block.beingRemoved = true
                }
            } else {
                for(block in chain){
                    var s = ""
                    if(!isOutOfBounds(block.x, block.y - 1) && chain.contains(grid.array[block.x][block.y - 1])){
                        s += "u"
                    }
                    if(!isOutOfBounds(block.x + 1, block.y) && chain.contains(grid.array[block.x + 1][block.y])){
                        s += "r"
                    }
                    if(!isOutOfBounds(block.x, block.y + 1) && chain.contains(grid.array[block.x][block.y + 1])){
                        s += "d"
                    }
                    if(!isOutOfBounds(block.x - 1, block.y) && chain.contains(grid.array[block.x - 1][block.y])){
                        s += "l"
                    }
                    block.currentSprite = if(s.isEmpty()) block.sprites.hashMap["main"] else block.sprites.hashMap[s]
                }
            }
        }
    }

    private fun removePuyoChain(){
        for(block in puyoChain[chainIndex]) {
            grid.array[block.x][block.y] = null
        }
        removedPuyos.add(puyoChain[chainIndex])
        puyoChain.removeAt(chainIndex)
        chainIndex = -1
    }

    private fun unmark(){
        for(i in 0 until grid.width) {
            for (j in 0 until grid.length) {
                grid.array[i][j]?.marked = false
            }
        }
    }

    private fun updatePuyoChain(){
        puyoChain.clear()
        for(i in 0 until grid.width) {
            for (j in 0 until grid.length) {
                if(!puyoChain.flatten().contains(grid.array[i][j])){
                    val index = puyoChain.size
                    findChain(i, j, grid.array[i][j]?.color, index)
                }
            }
        }
    }

    private fun findBigPuyoChain() : Int{
        updatePuyoChain()
        puyoChain.forEachIndexed { index, chain ->
            if(chain.size > 3 && !(chain.contains(puyo.first) && puyo.first.isFalling) && !(chain.contains(puyo.second) && puyo.second.isFalling)){
                chainIndex = index
                return index
            }
        }
        chainIndex = -1
        return -1
    }

    private fun isOutOfBounds(i: Int, j: Int) : Boolean {
        return i >= grid.width || j >= grid.length || i < 0 || j < 0
    }

    private fun findChain(i: Int, j: Int, color: PuyoColors?, index: Int): Boolean{ // sep into 2 methods
        if(isOutOfBounds(i, j) || grid.array[i][j] == null || grid.array[i][j]?.color != color ||
           grid.array[i][j]?.marked!!){
            return false; // canFall doesnt work bc puyo first is usually above puyo second before the fall
        } else {
            if(index < puyoChain.size){
                puyoChain[index] = puyoChain[index] + grid.array[i][j]!!
            } else {
                puyoChain.add(listOf(grid.array[i][j]!!))
            }
            grid.array[i][j]?.marked = true

            findChain(i, j - 1, color, index)
            findChain(i, j + 1, color, index)
            findChain(i + 1, j, color, index)
            findChain(i - 1, j, color, index)
            return true;
        }
    }

    private fun clearPrevPos(block: Block){
        grid.array[block.x][block.y] = null
    }

    private fun updateMovingPos(block: Block){
        grid.array[block.x][block.y] = block
    }

    private fun printGrid(){
        for (i in grid.length-1 downTo 0) {
            for(j in 0 until grid.width) {
                if (grid.array[j][i] == null) {
                    print("-")
                } else {
                    print("o")
                }
            }
            println()
        }
        println()
    }

    private fun canFall(block: Block) : Boolean {
        return !isOutOfBounds(block.x, block.y + 1) && grid.array[block.x][block.y + 1] == null
    }

    private fun isColliding(x: Int, y: Int) : Boolean{
        return isOutOfBounds(x, y) || grid.array[x][y] != null && !grid.array[x][y]?.isFalling!!
    }

    private fun spawnPuyo(){
        removedPuyos.clear()
        if(chainIndex >= 0){
            return
        }
        puyo = nextPuyos[0]
        nextPuyos.removeAt(0)
        nextPuyos.add(Puyo(Block(grid.width / 2, 0, puyoColors[Random.nextInt(0, puyoColors.size)]), Block(grid.width / 2, 1, puyoColors[Random.nextInt(0, puyoColors.size)])))
        updateMovingPos(puyo.first)
        updateMovingPos(puyo.second)
    }

    private fun dropAllBlocks() : Boolean{
        var dropped = false
        for (i in grid.length-1 downTo 0) {
            for(j in 0 until grid.width) {
                val block = grid.array[j][i]
                if(block != null){
                    dropBlock(block)
                    if(block.isFalling){
                        dropped = true
                    }
                }
            }
        }
        return dropped
    }

    private fun dropBlock(block: Block){
        block.isFalling = canFall(block)
        if(block.isFalling){
            clearPrevPos(block)
            block.y++
            updateMovingPos(block)
        }
    }

    private fun rotatePuyo(rotation: Int){
        val x: Int
        val y: Int
        if(rotation > 0){
            puyo.addRotationCount()
            x = if(puyo.rotateCount == 1 || puyo.rotateCount == 4) 1 else -1
            y = if(puyo.rotateCount == 1 || puyo.rotateCount == 2) 1 else -1
            if(isColliding(puyo.first.x + x, puyo.first.y + y)){
                puyo.removeRotationCount()
                return
            }
        } else {
            puyo.removeRotationCount()
            x = if(puyo.rotateCount == 1 || puyo.rotateCount == 2) 1 else -1
            y = if(puyo.rotateCount == 2 || puyo.rotateCount == 3) 1 else -1
            if(isColliding(puyo.first.x + x, puyo.first.y + y)){
                puyo.addRotationCount()
                return
            }
        }

        clearPrevPos(puyo.first)
        puyo.first.x += x
        puyo.first.y += y
        updateMovingPos(puyo.first)
    }

    private fun moveBlock(block: Block, direction: Int){
        clearPrevPos(block)
        block.x += direction
        updateMovingPos(block)
    }

    private fun movePuyo(direction: Int){
        if(isColliding(puyo.first.x + direction, puyo.first.y) || isColliding(puyo.second.x + direction, puyo.second.y)){
            return
        }
        if (puyo.first.x*direction < puyo.second.x*direction) {
            moveBlock(puyo.second, direction)
        }
        moveBlock(puyo.first, direction)
        if (puyo.first.x*direction >= puyo.second.x*direction) {
            moveBlock(puyo.second, direction)
        }
    }

    private fun drawBlocks(){
        val c = game.batch.color
        for(i in 0 until grid.width){
            for(j in 0 until grid.length){
                if(grid.array[i][j] == null || j == 0){
                    continue
                }
                if(grid.array[i][j]!!.flicker > 0 || grid.array[i][j]!!.beingRemoved) {
                    if (grid.array[i][j]!!.flicker > 5) {
                        game.batch.setColor(c.r, c.g, c.b, 1f)
                    } else {
                        game.batch.setColor(c.r, c.g, c.b, 0.6f)
                    }
                    grid.array[i][j]?.addFlicker()
                } else {
                    game.batch.setColor(c.r, c.g, c.b, 1f)
                }

                game.batch.draw(grid.array[i][j]!!.currentSprite,
                        GRID_START_X + i * CELL_SIZE,
                        GRID_START_Y - j * CELL_SIZE,
                        CELL_SIZE, CELL_SIZE)
            }
        }

        game.batch.setColor(c.r, c.g, c.b, 1f)
        unmark()
    }

    private fun drawTitle(){
        titleFont.draw(game.batch, "Puyo Puyo", GRID_START_X * 1.27f, SCREEN_HEIGHT * 0.94f)
    }

    private fun drawScore(){
        scoreFont.draw(game.batch, scoring.score.toString(), GRID_START_X * 1.6f, GRID_START_Y - GRID_START_Y * 0.87f)
    }

    override fun show() {

    }
    override fun resize(width: Int, height: Int) {
        viewport.update(width, height);
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