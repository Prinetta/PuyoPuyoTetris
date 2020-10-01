package com.puyo

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import java.lang.System.currentTimeMillis
import kotlin.random.Random

class PuyoScreen(val game: PuyoPuyoTetris) : Screen {
    private val grid = Grid(6, 13)
    private var lastInputTime = currentTimeMillis()
    private var lastChainTime = currentTimeMillis()
    private var lastDropTime = currentTimeMillis()
    private val puyoColors = PuyoColors.values()
    private var puyoChain = mutableListOf<List<Block>>()
    private var chainIndex = -1
    private var allBlocksStanding = true

    val SCREEN_WIDTH = 700f
    val SCREEN_HEIGHT = 800f
    val CELL_SIZE = 60f
    val GRID_START_X = SCREEN_WIDTH/4 // Middle of Screen
    val GRID_START_Y = SCREEN_HEIGHT-SCREEN_HEIGHT*0.95f + grid.length*CELL_SIZE-CELL_SIZE

    var camera = OrthographicCamera()
    var shapeRenderer = ShapeRenderer()

    private lateinit var puyo: Puyo

    init {
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT)
        spawnPuyo()
    }

    override fun render(delta: Float) {
        game.batch.begin()
        game.batch.draw(Texture(Gdx.files.internal("akech.jpg")),
                0f, 0f, SCREEN_WIDTH, SCREEN_HEIGHT)
        game.batch.end()
        //Gdx.gl.glClearColor(255f, 189f / 255f, 205f / 255f, 1f)
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        camera.update()
        game.batch.projectionMatrix = camera.combined
        shapeRenderer.projectionMatrix = camera.combined;

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
                if(currentTimeMillis() - puyo.dropTime > puyo.speed && chainIndex == -1){
                    if(puyo.canSpawn()){
                        if(!isColliding(grid.width / 2, 0)){ // needs open space at top
                            spawnPuyo()
                        } else {
                            println("you lost lmaooo loser")
                        }
                    }
                    puyo.dropTime = currentTimeMillis()
                }
            }
            lastChainTime = currentTimeMillis()
        }

        connectPuyos()
        drawBackground()
        drawBlocks()
    }

    private fun connectFallingPuyos(){
        if(puyo.first.color == puyo.second.color){
            if(puyo.first.x == puyo.second.x){ // vertical
                if(puyo.first.y < puyo.second.y){
                    puyo.first.currentSprite = puyo.first.sprites.get("d")
                    puyo.second.currentSprite = puyo.second.sprites.get("u")
                } else {
                    puyo.first.currentSprite = puyo.first.sprites.get("u")
                    puyo.second.currentSprite = puyo.second.sprites.get("d")
                }
            } else if(puyo.first.y == puyo.second.y){ // horizontal
                if(puyo.first.x < puyo.second.x){
                    puyo.first.currentSprite = puyo.first.sprites.get("r")
                    puyo.second.currentSprite = puyo.second.sprites.get("l")
                } else {
                    puyo.first.currentSprite = puyo.first.sprites.get("l")
                    puyo.second.currentSprite = puyo.second.sprites.get("r")
                }
            } else {
                puyo.first.currentSprite = puyo.first.sprites.get("main")
                puyo.second.currentSprite = puyo.second.sprites.get("main")
            }
        }
    }

    private fun connectPuyos(){
        for (chain in puyoChain){
            if(chain.size <= 1){
                continue
            } else if (chain.size >= 4){
                for(block in chain){
                    block.currentSprite = block.sprites.get("s")
                    block.addFlicker()
                }
            } else {
                for(block in chain){
                    var s = ""
                    if(!isOutOfBounds(block.x, block.y-1) && chain.contains(grid.array[block.x][block.y-1])){
                        s += "u"
                    }
                    if(!isOutOfBounds(block.x+1, block.y) && chain.contains(grid.array[block.x+1][block.y])){
                        s += "r"
                    }
                    if(!isOutOfBounds(block.x, block.y+1) && chain.contains(grid.array[block.x][block.y+1])){
                        s += "d"
                    }
                    if(!isOutOfBounds(block.x-1, block.y) && chain.contains(grid.array[block.x-1][block.y])){
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
        puyoChain = mutableListOf()
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

    private fun isMainPuyo(block: Block) : Boolean {
        return grid.array[block.x][block.y] == puyo.first || grid.array[block.x][block.y] == puyo.second
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
        return !isOutOfBounds(block.x, block.y+1) && grid.array[block.x][block.y+1] == null
    }

    private fun isColliding(x: Int, y: Int) : Boolean{
        return isOutOfBounds(x, y) || grid.array[x][y] != null && !grid.array[x][y]?.isFalling!!
    }

    private fun spawnPuyo(){
        if(chainIndex >= 0){
            return
        }
        puyo = Puyo(Block(grid.width / 2, 0, puyoColors[Random.nextInt(0, puyoColors.size)]), Block(grid.width / 2, 1, puyoColors[Random.nextInt(0, puyoColors.size)]))
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

    private fun dropPuyo(){
        if(grid.array[puyo.first.x][puyo.first.y] != null && grid.array[puyo.second.x][puyo.second.y] != null){
            if(puyo.first.y < puyo.second.y){
                dropBlock(puyo.second)
            }
            dropBlock(puyo.first)
            if(puyo.first.y >= puyo.second.y){
                dropBlock(puyo.second)
            }
        } else if(grid.array[puyo.first.x][puyo.first.y] != null){
            dropBlock(puyo.first)
        } else if(grid.array[puyo.second.x][puyo.second.y] != null){
            dropBlock(puyo.second)
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
        game.batch.begin()
        for(i in 0 until grid.width){
            for(j in 0 until grid.length){
                if(grid.array[i][j] == null || j == 0){
                    continue
                }
                val c = game.batch.color
                if(grid.array[i][j]!!.flicker > 0) {
                    if (grid.array[i][j]!!.flicker > 10) {
                        game.batch.setColor(c.r, c.g, c.b, 1f)
                    } else {
                        game.batch.setColor(c.r, c.g, c.b, 0.2f)
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
        game.batch.end()
        unmark()
    }

    private fun drawTitle(){
        game.batch.begin()
        game.font.draw(game.batch, "Puyo Puyo Tetris", SCREEN_HEIGHT - 100, SCREEN_WIDTH / 2)
        game.batch.end()
    }

    private fun drawBackground(){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 1f)
        shapeRenderer.rect(GRID_START_X, SCREEN_HEIGHT-SCREEN_HEIGHT*0.95f, grid.width * CELL_SIZE, (grid.length-1) * CELL_SIZE)
        shapeRenderer.end()
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