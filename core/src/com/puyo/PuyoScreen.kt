package com.puyo

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import java.lang.System.currentTimeMillis
import kotlin.random.Random

class PuyoScreen(val game: PuyoPuyoTetris) : Screen {
    private val grid = Grid(6, 13)
    private var lastDropTime = currentTimeMillis()
    private var lastComboTime = currentTimeMillis()
    private var lastInputTime = currentTimeMillis()
    private val puyoColors = PuyoColors.values()
    private var puyoChain = mutableListOf<List<Block>>()
    private var chainIndex = -1

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
        Gdx.gl.glClearColor(255f, 189f / 255f, 205f / 255f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
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

        if(chainIndex == -1){
            findBigPuyoChain()
            if(chainIndex == -1 && currentTimeMillis() - lastDropTime > puyo.speed){
                dropPuyo()
                updatePuyoState()
                lastDropTime = currentTimeMillis();
            }
        } else {
            if(currentTimeMillis() - lastComboTime > puyo.comboSpeed){
                removePuyoChain()
                lastComboTime = currentTimeMillis();
            }
        }
        drawBackground()
        drawBlocks()
        connectPuyos()
    }

    private fun connectPuyos(){
        for (chain in puyoChain){
            if(chain.size <= 1){
                continue
            }
            for(block in chain){
                if(!isOutOfBounds(block.x, block.y-1) && chain.contains(grid.array[block.x][block.y-1])){
                    // draw up sprite
                }
                if(!isOutOfBounds(block.x-1, block.y) && chain.contains(grid.array[block.x-1][block.y])){
                    // draw left sprite
                }
                if(!isOutOfBounds(block.x+1, block.y) && chain.contains(grid.array[block.x+1][block.y])){
                    // draw right sprite
                }
                if(!isOutOfBounds(block.x, block.y+1) && chain.contains(grid.array[block.x][block.y+1])){
                    // draw down sprite
                }
            }
        }
    }

    private fun removePuyoChain(){
        for(block in puyoChain[chainIndex]) {
            grid.array[block.x][block.y] = null
        }
        for(block in puyoChain[chainIndex]){
            val blockAbove: Block? = if(block.y > 0) grid.array[block.x][block.y - 1] else null
            if(blockAbove != null && !puyoChain[chainIndex].contains(blockAbove)){
                do {
                    dropBlock(blockAbove)
                } while(blockAbove.falling)
            }
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

    private fun findBigPuyoChain(){
        puyoChain = mutableListOf()
        for(i in 0 until grid.width) {
            for (j in 0 until grid.length) {
                if(!puyoChain.flatten().contains(grid.array[i][j])){
                    val index = puyoChain.size
                    if(grid.array[i][j] == null || !findChain(i, j, grid.array[i][j]?.color, index)){
                        continue
                    }
                    if(puyoChain[index].size > 3){
                        chainIndex = index
                        return
                    }
                }
            }
        }
        chainIndex = -1
        dropAllBlocks()
    }

    private fun isOutOfBounds(i: Int, j: Int) : Boolean {
        return i >= grid.width || j >= grid.length || i < 0 || j < 0
    }

    private fun isMainPuyo(i: Int, j: Int) : Boolean {
        return grid.array[i][j] == puyo.first || grid.array[i][j] == puyo.second
    }

    private fun findChain(i: Int, j: Int, color: PuyoColors?, index: Int): Boolean{
        if(isOutOfBounds(i, j) || grid.array[i][j] == null || grid.array[i][j]?.color != color ||
           grid.array[i][j]?.marked!! || isMainPuyo(i, j)){
            return false;
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

    private fun updatePuyoState(){
        dropAllBlocks()
        if(puyo.bothDropped()){
            spawnPuyo()
        }
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

    private fun isColliding(x: Int, y: Int) : Boolean{
        return x >= grid.width || x < 0 || y >= grid.length || y < 0 || grid.array[x][y] != null && !grid.array[x][y]?.falling!!
    }

    private fun spawnPuyo(){
        if(chainIndex >= 0){
            return
        }
        puyo = Puyo(Block(grid.width / 2, 0, puyoColors[Random.nextInt(0, puyoColors.size)]), Block(grid.width / 2, 1, puyoColors[Random.nextInt(0, puyoColors.size)]))
        updateMovingPos(puyo.first)
        updateMovingPos(puyo.second)
    }

    private fun dropAllBlocks(){
        for (i in grid.length-1 downTo 0) {
            for(j in 0 until grid.width) {
                if(grid.array[j][i] != null && grid.array[j][i] != puyo.first && grid.array[j][i] != puyo.second){
                    dropBlock(grid.array[j][i]!!)
                }
            }
        }
    }

    private fun dropBlock(block: Block){
        block.falling = !isColliding(block.x, block.y + 1)
        if(block.falling){
            clearPrevPos(block)
            block.y++
            updateMovingPos(block)
        }
    }

    private fun dropPuyo(){
        if(puyo.first.y < puyo.second.y){
            dropBlock(puyo.second)
        }
        dropBlock(puyo.first)
        if(puyo.first.y >= puyo.second.y){
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
        for(i in 0 until grid.width){
            for(j in 0 until grid.length){
                if(grid.array[i][j] == null || j == 0){
                    continue
                }
                game.batch.begin()
                game.batch.draw(grid.array[i][j]!!.sprite.main,
                        GRID_START_X + i * CELL_SIZE,
                        GRID_START_Y - j * CELL_SIZE,
                        CELL_SIZE, CELL_SIZE)
                game.batch.end()
            }
        }
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