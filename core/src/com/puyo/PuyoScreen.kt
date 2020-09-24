package com.puyo

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.GridPoint2

class PuyoScreen(val puyo : PuyoPuyoTetris) : Screen {
    private val grid = Grid(6, 12)
    private val CELL_SIZE = 60f
    private var milliseconds = 0

    val SCREEN_WIDTH = 700f
    val SCREEN_HEIGHT = 800f
    val GRID_START_X = SCREEN_WIDTH/4
    val GRID_START_Y = SCREEN_HEIGHT-SCREEN_HEIGHT*0.05f-CELL_SIZE

    var camera = OrthographicCamera()
    var shapeRenderer = ShapeRenderer()

    private lateinit var block: PuyoBlock
    private var isFalling = false;

    init {
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT)
        spawnBlock()
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(255f, 189f/255f, 205f/255f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        camera.update()
        puyo.batch.projectionMatrix = camera.combined
        shapeRenderer.projectionMatrix = camera.combined;

        drawBackground()
        drawBlocks()

        if(milliseconds > 60){
            moveBlock()
            milliseconds = 0
        }
        milliseconds++;
    }

    private fun spawnBlock(){
        block = PuyoBlock(GridPoint2(grid.width/2, 0), Color.BLUE)
        updateArray()
    }

    private fun clearPrevPosition(){
        grid.array[block.first.x][block.first.y] = 0
        grid.array[block.second.x][block.second.y] = 0
    }

    private fun updateArray(){
        grid.array[block.first.x][block.first.y] = 1;
        grid.array[block.second.x][block.second.y] = 1;
    }

    private fun isColliding(firstPos: GridPoint2, secondPos: GridPoint2) : Boolean{
        return firstPos.x >= grid.width || firstPos.x < 0 || secondPos.x >= grid.width || secondPos.x < 0 ||
               firstPos.y >= grid.length || firstPos.y < 0 || secondPos.y >= grid.length || secondPos.y < 0 ||
               grid.array[firstPos.x][firstPos.y] == 1 || grid.array[secondPos.x][secondPos.y] == 1
    }

    private fun moveBlock(){
        clearPrevPosition()
        if(!isColliding(GridPoint2(block.first.x, block.first.y+1), GridPoint2(block.second.x, block.second.y+1))){
            block.first.y++
            block.second.y++
        } else {
            spawnBlock()
        }
        updateArray()
    }

    private fun drawBlocks(){
        for(i in 0 until grid.width){
            for(j in 0 until grid.length){
                if(grid.array[i][j] == 1){
                    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
                    shapeRenderer.setColor(0.8f, 0.4f, 0.5f, 1f)
                    shapeRenderer.rect(GRID_START_X+i*CELL_SIZE, GRID_START_Y-j*CELL_SIZE, CELL_SIZE, CELL_SIZE)
                    shapeRenderer.end()
                }
            }
        }
    }

    private fun drawTitle(){
        puyo.batch.begin()
        puyo.font.draw(puyo.batch, "Puyo Puyo Tetris", SCREEN_HEIGHT-100, SCREEN_WIDTH/2)
        puyo.batch.end()
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