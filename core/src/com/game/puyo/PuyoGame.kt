package com.game.puyo

import com.game.GRID_LENGTH
import com.game.GRID_WIDTH
import com.game.Garbage
import kotlin.math.abs
import kotlin.random.Random

class PuyoGame (){
    private var puyoChain = mutableListOf<List<PuyoBlock>>()
    private var removedPuyos = mutableListOf<List<PuyoBlock>>()
    var nextPuyos = mutableListOf<Puyo>()

    private val width = GRID_WIDTH
    private val length = GRID_LENGTH
    private var allBlocksStanding = true
    private var chainIndex = -1
    lateinit var puyo: Puyo
    val grid = Array(width) {Array<PuyoBlock?>(length) {null} }
    var gameOver = false

    val scoring = PuyoScoring()

    init {
        generatePuyoList()
        spawnPuyo()
    }

    fun hasFoundChain(): Boolean{
        return chainIndex != -1
    }

    fun allowSpawn(): Boolean {
        return puyo.canSpawn() && !isColliding(width / 2, 1)
    }

    fun calculateChainScore(){
        if(removedPuyos.isNotEmpty()){
            scoring.calculate(removedPuyos)
            sendGarbage(scoring.garbage)
            removedPuyos.clear()
        }
    }

    fun isDoneDroppingBlocks(): Boolean {
        return allBlocksStanding
    }

    fun dropRemainingBlocks(){
        allBlocksStanding = !dropAllBlocks()
    }

    fun removeCombo(){
        removePuyoChain()
        while (findBigPuyoChain() != -1){
            removePuyoChain()
        }
    }

    fun movePuyo(direction: Int){
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

    fun rotatePuyo(rotation: Int){
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

    fun spawnPuyo(){
        val puyoColors = PuyoColor.values()
        puyo = nextPuyos[0]
        nextPuyos.removeAt(0)
        nextPuyos.add(Puyo(PuyoBlock(width / 2, 0, puyoColors[Random.nextInt(0, puyoColors.size)]), PuyoBlock(width / 2, 1, puyoColors[Random.nextInt(0, puyoColors.size)])))

        updateMovingPos(puyo.first)
        updateMovingPos(puyo.second)
    }

    fun findBigPuyoChain() : Int{
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

    fun unmark(){
        for(i in 0 until width) {
            for (j in 0 until length) {
                grid[i][j]?.marked = false
            }
        }
    }

    fun placeGarbageBlock(){
        for (i in 0 until width){
            for (j in 0 until length){
                if(!isColliding(i, j)){
                    //grid[i][j] = PuyoBlock(i, j, )
                }
            }
        }
    }

    fun dropGarbage(){
        val garbageBlocks = mutableListOf<PuyoBlock>()

        scoring.garbage = 0
    }

    fun hasReceivedGarbage() : Boolean{
        return scoring.garbage > 0
    }

    fun sendGarbage(amount: Int){
        if(amount < 4){
            return
        }
        val garbage = Garbage.puyoToTetris.getOrElse(amount) {
            Garbage.puyoToTetris[Garbage.puyoToTetris.keys.filter{ key -> key < amount }.lastIndex] // converts garbage to tetris
        }!!
        //tetris.receiveGarbage(amount)
        receiveGarbage(garbage)
    }

    fun receiveGarbage(amount: Int){
        scoring.garbage = amount
    }

    private fun clearPrevPos(block: PuyoBlock){
        grid[block.x][block.y] = null
    }

    private fun updateMovingPos(block: PuyoBlock){
        grid[block.x][block.y] = block
    }

    private fun isOutOfBounds(i: Int, j: Int) : Boolean {
        return i !in 0 until width || j !in 0 until length
    }

    private fun canFall(block: PuyoBlock) : Boolean {
        return !isOutOfBounds(block.x, block.y + 1) && grid[block.x][block.y + 1] == null
    }

    private fun isColliding(x: Int, y: Int) : Boolean{
        return isOutOfBounds(x, y) || grid[x][y] != null && !grid[x][y]?.isFalling!!
    }

    private fun generatePuyoList(){
        val puyoColors = PuyoColor.values()
        nextPuyos.addAll(listOf(
                Puyo(PuyoBlock(width / 2, 0, puyoColors[Random.nextInt(0, puyoColors.size)]), PuyoBlock(width / 2, 1, puyoColors[Random.nextInt(0, puyoColors.size)])),
                Puyo(PuyoBlock(width / 2, 0, puyoColors[Random.nextInt(0, puyoColors.size)]), PuyoBlock(width / 2, 1, puyoColors[Random.nextInt(0, puyoColors.size)]))
        ))
    }

    private fun findChain(i: Int, j: Int, color: PuyoColor?, index: Int): Boolean{
        if(isOutOfBounds(i, j) || grid[i][j] == null || grid[i][j]?.color != color || grid[i][j]?.marked!!){
            return false;
        }
        if(index < puyoChain.size){
            puyoChain[index] = puyoChain[index] + grid[i][j]!!
        } else {
            puyoChain.add(listOf(grid[i][j]!!))
        }
        grid[i][j]?.marked = true

        findChain(i, j - 1, color, index)
        findChain(i, j + 1, color, index)
        findChain(i + 1, j, color, index)
        findChain(i - 1, j, color, index)
        return true;
    }

    private fun updatePuyoChain(){
        puyoChain.clear()
        for(i in 0 until width) {
            for (j in 0 until length) {
                if(!puyoChain.flatten().contains(grid[i][j])){
                    val index = puyoChain.size
                    findChain(i, j, grid[i][j]?.color, index)
                }
            }
        }
    }

    private fun removePuyoChain(){
        for(block in puyoChain[chainIndex]) {
            grid[block.x][block.y] = null
        }
        removedPuyos.add(puyoChain[chainIndex])
        puyoChain.removeAt(chainIndex)
        chainIndex = -1
    }

    private fun dropAllBlocks() : Boolean{
        var dropped = false
        for (i in length-1 downTo 0) {
            for(j in 0 until width) {
                val block = grid[j][i]
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

    private fun dropBlock(block: PuyoBlock){
        block.isFalling = canFall(block)
        if(block.isFalling){
            clearPrevPos(block)
            block.y++
            updateMovingPos(block)
        }
    }

    private fun moveBlock(block: PuyoBlock, direction: Int){
        clearPrevPos(block)
        block.x += direction
        updateMovingPos(block)
    }

    fun connectPuyos(){
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
                    if(!isOutOfBounds(block.x, block.y - 1) && chain.contains(grid[block.x][block.y - 1])){
                        s += "u"
                    }
                    if(!isOutOfBounds(block.x + 1, block.y) && chain.contains(grid[block.x + 1][block.y])){
                        s += "r"
                    }
                    if(!isOutOfBounds(block.x, block.y + 1) && chain.contains(grid[block.x][block.y + 1])){
                        s += "d"
                    }
                    if(!isOutOfBounds(block.x - 1, block.y) && chain.contains(grid[block.x - 1][block.y])){
                        s += "l"
                    }
                    block.currentSprite = if(s.isEmpty()) block.sprites.hashMap["main"] else block.sprites.hashMap[s]
                }
            }
        }
    }
}