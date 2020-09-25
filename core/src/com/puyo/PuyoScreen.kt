package com.puyo

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import java.lang.System.currentTimeMillis
import kotlin.random.Random

class PuyoScreen(val game : PuyoPuyoTetris) : Screen {
    private val grid = Grid(6, 12)
    private var lastDropTime = currentTimeMillis()
    private var lastInputTime = currentTimeMillis()
    private val puyoColors = PuyoColors.values()

    val SCREEN_WIDTH = 700f
    val SCREEN_HEIGHT = 800f
    val CELL_SIZE = 60f
    val GRID_START_X = SCREEN_WIDTH/4
    val GRID_START_Y = SCREEN_HEIGHT-SCREEN_HEIGHT*0.05f-CELL_SIZE

    var camera = OrthographicCamera()
    var shapeRenderer = ShapeRenderer()

    private lateinit var puyo: Puyo

    init {
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT)
        spawnPuyo()
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(255f, 189f/255f, 205f/255f, 1f)
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

        if(currentTimeMillis() - lastDropTime > puyo.speed){
            dropPuyo()
            lastDropTime = currentTimeMillis();
        }

        updatePuyoState()
        drawBackground()
        drawBlocks()
    }

    private fun unmark(){
        for(i in 0 until grid.width) {
            for (j in 0 until grid.length) {
                grid.array[i][j]?.marked = false
            }
        }
    }

    private fun removeBlocks(i: Int, j: Int, color: PuyoColors){
        if(i >= grid.width || j >= grid.length || i < 0 || j < 0 ||
                grid.array[i][j] == null || grid.array[i][j]?.color != color){
            return
        }
        grid.array[i][j] = null
        removeBlocks(i, j-1, color)
        removeBlocks(i, j+1, color)
        removeBlocks(i+1, j, color)
        removeBlocks(i-1, j, color)
    }

    private fun hasChain(): Boolean{
        for(i in 0 until grid.width) {
            for (j in 0 until grid.length) {
                val neighbours = countNeighbours(i, j, grid.array[i][j]?.color)
                if(neighbours > 3){
                    removeBlocks(i, j, grid.array[i][j]?.color!!)
                    unmark()
                    dropAllBlocks()
                    return true
                }
            }
        }
        unmark()
        dropAllBlocks()
        return false
    }

    private fun countNeighbours(i: Int, j: Int, color: PuyoColors?) : Int{
        if(i >= grid.width || j >= grid.length || i < 0 || j < 0 ||
                grid.array[i][j] == null || grid.array[i][j]?.color != color || grid.array[i][j]?.marked!!){
            return 0
        } else {
            grid.array[i][j]?.marked = true
            return 1 + countNeighbours(i, j-1, color) + countNeighbours(i, j+1, color) + countNeighbours(i+1, j, color) + countNeighbours(i-1, j, color)
        }
    }

    private fun clearPrevPos(block: Block){
        grid.array[block.x][block.y] = null
    }

    private fun updateMovingPos(block: Block){
        grid.array[block.x][block.y] = block
    }

    private fun updatePuyoState(){
        if(puyo.bothDropped()){
            printGrid()
            while(hasChain()) continue
            dropAllBlocks()
            spawnPuyo()
        }
    }

    private fun printGrid(){
        for (i in 0 until grid.width) {
            for(j in 0 until grid.length) {
                if (grid.array[i][j] == null) {
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
        puyo = Puyo(Block(grid.width/2, 0, puyoColors[Random.nextInt(0, puyoColors.size)]), Block(grid.width/2, 1, puyoColors[Random.nextInt(0, puyoColors.size)]))
        updateMovingPos(puyo.first)
        updateMovingPos(puyo.second)
    }

    private fun dropAllBlocks(){
        for(i in 0 until grid.width) {
            for (j in grid.length-1 downTo 0) {
                if(grid.array[i][j] != null && grid.array[i][j] != puyo.first && grid.array[i][j] != puyo.second){
                    dropBlock(grid.array[i][j]!!)
                }
            }
        }
    }

    private fun dropBlock(block: Block){
        block.falling = !isColliding(block.x, block.y+1)
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
        if(isColliding(puyo.first.x+direction, puyo.first.y) || isColliding(puyo.second.x+direction, puyo.second.y)){
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
                if(grid.array[i][j] == null){
                    continue
                }
                val color = grid.array[i][j]!!.color
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
                shapeRenderer.setColor(color.color.r, color.color.g, color.color.b, 1f)
                shapeRenderer.circle(GRID_START_X+i*CELL_SIZE+CELL_SIZE/2, GRID_START_Y-j*CELL_SIZE+CELL_SIZE/2, CELL_SIZE/2);
                //shapeRenderer.rect(GRID_START_X+i*CELL_SIZE, GRID_START_Y-j*CELL_SIZE, CELL_SIZE, CELL_SIZE)
                shapeRenderer.end()
            }
        }
    }

    private fun drawTitle(){
        game.batch.begin()
        game.font.draw(game.batch, "Puyo Puyo Tetris", SCREEN_HEIGHT-100, SCREEN_WIDTH/2)
        game.batch.end()
    }

    private fun drawBackground(){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 1f)
        shapeRenderer.rect(GRID_START_X, SCREEN_HEIGHT*0.05f, grid.width*CELL_SIZE, grid.length*CELL_SIZE)
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