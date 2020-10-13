package com.game.puyo

import com.game.*
import com.game.Tetris.TetrisGame
import com.sun.org.apache.xpath.internal.operations.Bool
import kotlin.random.Random

class PuyoGame (){
    private var puyoChain = mutableListOf<List<PuyoBlock>>()
    private var puyosToRemove = mutableListOf<List<PuyoBlock>>()
    private var garbageToRemove = mutableListOf<GarbageBlock>()
    var nextPuyos = mutableListOf<Puyo>()

    private val width = PC.GRID_WIDTH
    private val length = PC.GRID_LENGTH
    var allPuyosDropped = true
    private var allGarbageDropped = true
    private var chainIndex = -1
    private lateinit var tetris: TetrisGame;
    lateinit var puyo: Puyo
    val grid = Array(width) {Array<Block?>(length) {null} }
    var gameOver = false

    val scoring = PuyoScoring()

    init {
        generatePuyoList()
        spawnPuyo()
    }

    fun setTetris(tetris: TetrisGame){
        this.tetris = tetris
    }

    fun hasFoundChain(): Boolean{
        return chainIndex != -1
    }

    fun allowSpawn(): Boolean {
        return puyo.canSpawn() && !isColliding(width / 2, 1)
    }

    fun calculateChainScore(){
        if(puyosToRemove.isNotEmpty()){
            scoring.calculate(puyosToRemove)
            sendGarbage(scoring.garbageToSend)
            puyosToRemove.clear()
        }
    }

    fun isDoneDroppingPuyos(): Boolean {
        return allPuyosDropped
    }

    fun isDoneDroppingGarbage(): Boolean {
        return allGarbageDropped
    }

    fun dropRemainingPuyos(){
        allPuyosDropped = !dropPuyos()
    }

    fun quickTurn(){
        val tempY = puyo.first.y
        puyo.first.y = puyo.second.y
        puyo.second.y = tempY
        updateMovingPos(puyo.first)
        updateMovingPos(puyo.second)
    }

    fun canQuickTurn(): Boolean{
        return isColliding(puyo.first.x-1, puyo.first.y) && isColliding(puyo.first.x+1, puyo.first.y) &&
               isColliding(puyo.second.x-1, puyo.first.y) && isColliding(puyo.second.x+1, puyo.first.y)
    }

    fun removeCombo(){
        removePuyoChain()
        while (findBigPuyoChain() != -1){
            removePuyoChain()
        }
        if(garbageToRemove.isNotEmpty()) garbageToRemove.forEach { grid[it.x][it.y] = null }
        garbageToRemove.clear()
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

    private fun canWallKick(x: Int, y: Int): Boolean{
        return (x < 0  && !isColliding(puyo.second.x+1, puyo.second.y)) || (x >= width && !isColliding(puyo.second.x-1, puyo.second.y))
    }

    private fun canFloorKick(x: Int, y: Int): Boolean{
        return y >= length && x in 0..width && !isColliding(puyo.second.x, puyo.second.y-1)
    }

    private fun floorKick(){
        clearPrevPos(puyo.first)
        puyo.first.x = puyo.second.x
        puyo.first.y = puyo.second.y
        puyo.second.y--
        updateMovingPos(puyo.first)
        updateMovingPos(puyo.second)
    }

    private fun wallkick(x: Int){
        clearPrevPos(puyo.first)
        puyo.first.x = puyo.second.x
        puyo.first.y = puyo.second.y
        if(x < 0){
            puyo.second.x++
        } else if (x >= width){
            puyo.second.x--
        }
        updateMovingPos(puyo.first)
        updateMovingPos(puyo.second)
    }

    fun rotatePuyo(rotation: Int){
        val x: Int
        val y: Int

        puyo.updateRotationCount(rotation)
        if(rotation > 0) {
            x = if (puyo.rotateCount == 1 || puyo.rotateCount == 4) 1 else -1
            y = if (puyo.rotateCount == 1 || puyo.rotateCount == 2) 1 else -1
        } else {
            x = if(puyo.rotateCount == 1 || puyo.rotateCount == 2) 1 else -1
            y = if(puyo.rotateCount == 2 || puyo.rotateCount == 3) 1 else -1
        }

        if(canWallKick(puyo.first.x + x, puyo.first.y + y)){
            wallkick(puyo.first.x + x)
        } else if(canFloorKick(puyo.first.x + x, puyo.first.y + y)){
            floorKick()
        } else if(isColliding(puyo.first.x + x, puyo.first.y + y)){
            puyo.updateRotationCount(-rotation)
            return
        } else {
            setBlockTo(puyo.first, puyo.first.x + x, puyo.first.y + y)
        }
    }

    private fun setBlockTo(block: Block, x: Int, y: Int){
        clearPrevPos(block)
        block.x = x
        block.y = y
        updateMovingPos(block)
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
        findAllChains()
        puyoChain.forEachIndexed { index, chain -> // found big puyo chain
            if(chain.size > 3 && !chain.any { it.isFalling }){
                chainIndex = index
                findAdjacentGarbage()
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

    private fun placeGarbageBlocks(blocks: MutableList<GarbageBlock>){
        val randomNumbers = (0 until width).toList().shuffled()
        for (i in 0 until length){
            for (j in randomNumbers){
                if(!isColliding(j, i) && blocks.isNotEmpty()){
                    val block = blocks.first()
                    block.set(j, i)
                    grid[j][i] = block
                    blocks.remove(block)
                }
            }
        }
        if(blocks.isNotEmpty()){
            gameOver = true
            println("you lost")
        }
    }

    fun dropRemainingGarbage(){
        allGarbageDropped = !dropAllGarbadge()
    }

    fun placeGarbage(){
        val garbageBlocks = MutableList(scoring.garbageToReceive) { GarbageBlock(0, 0) }
        placeGarbageBlocks(garbageBlocks)
        dropRemainingGarbage()
        scoring.garbageToReceive = 0
    }

    fun hasReceivedGarbage() : Boolean{
        return scoring.garbageToReceive > 0
    }

    private fun sendGarbage(amount: Int){
        if(amount < 4){
            return
        }
        val garbage = Garbage.puyoToTetris.getOrElse(amount) {
            Garbage.puyoToTetris.getValue(Garbage.puyoToTetris.keys.last { it <= amount })
        }
        tetris.receiveGarbage(garbage)
        scoring.garbageToSend = 0
    }

    fun receiveGarbage(amount: Int){
        scoring.garbageToReceive += amount
    }

    fun getExpectedDrop(block: PuyoBlock): Array<Int>{
        var y = block.y
        while (!isColliding(block.x, y+1)){
            y++
        }
        if(y == block.y){
            y = -1
        }
        return arrayOf(block.x, y)
    }

    private fun clearPrevPos(block: Block){
        grid[block.x][block.y] = null
    }

    private fun updateMovingPos(block: Block){
        grid[block.x][block.y] = block
    }

    private fun isOutOfBounds(i: Int, j: Int) : Boolean {
        return i !in 0 until width || j !in 0 until length
    }

    private fun canFall(block: Block) : Boolean {
        return !isOutOfBounds(block.x, block.y + 1) && grid[block.x][block.y + 1] == null
    }

    private fun isMainPuyo(block: Block) : Boolean {
        return block == puyo.first || block == puyo.second
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
        if(isOutOfBounds(i, j)){
            return false
        }
        val block = grid[i][j]
        if(block == null || block.marked || block !is PuyoBlock || block.color != color ||
           (block == puyo.first && getExpectedDrop(block)[1] != -1) ||
            block == puyo.second && getExpectedDrop(block)[1] != -1){
            return false
        }
        if(index < puyoChain.size){
            puyoChain[index] = puyoChain[index] + block
        } else {
            puyoChain.add(listOf(block))
        }
        grid[i][j]?.marked = true

        findChain(i, j - 1, color, index)
        findChain(i, j + 1, color, index)
        findChain(i + 1, j, color, index)
        findChain(i - 1, j, color, index)
        return true
    }

    private fun findAllChains(){
        puyoChain.clear()
        for(i in 0 until width) {
            for (j in 0 until length) {
                val block = grid[i][j]
                if(block is PuyoBlock && !puyoChain.flatten().contains(block)){
                    val index = puyoChain.size
                    findChain(i, j, block.color, index)
                }
            }
        }
        if(puyo.first.color == puyo.second.color && !puyoChain.flatten().any{it == puyo.first || it == puyo.second}) puyoChain.add(mutableListOf(puyo.first, puyo.second))
    }

    private fun findAdjacentGarbage(){
        for (block in puyoChain[chainIndex]){
            if(!isOutOfBounds(block.x, block.y-1)){
                val garbage = grid[block.x][block.y-1]
                if(garbage is GarbageBlock) garbageToRemove.add(garbage)
            }
            if(!isOutOfBounds(block.x, block.y+1)){
                val garbage = grid[block.x][block.y+1]
                if(garbage is GarbageBlock) garbageToRemove.add(garbage)
            }
            if(!isOutOfBounds(block.x-1, block.y)){
                val garbage = grid[block.x-1][block.y]
                if(garbage is GarbageBlock) garbageToRemove.add(garbage)
            }
            if(!isOutOfBounds(block.x-1, block.y)){
                val garbage = grid[block.x-1][block.y]
                if(garbage is GarbageBlock) garbageToRemove.add(garbage)
            }
        }

        if(garbageToRemove.isNotEmpty()) garbageToRemove.forEach {it.isBeingRemoved = true}
    }

    private fun removePuyoChain(){
        for(block in puyoChain[chainIndex]) {
            grid[block.x][block.y] = null
        }
        puyosToRemove.add(puyoChain[chainIndex])
        puyoChain.removeAt(chainIndex)
        chainIndex = -1
    }

    private fun dropAllGarbadge(): Boolean {
        var dropped = false
        for (i in length-1 downTo 0) {
            for(j in 0 until width) {
                val block = grid[j][i]
                if(block != null && block is GarbageBlock){
                    dropBlock(block)
                    if(block.isFalling){
                        dropped = true
                    }
                }
            }
        }
        return dropped
    }

    private fun dropPuyos() : Boolean{
        var dropped = false
        for (i in length-1 downTo 0) {
            for(j in 0 until width) {
                val block = grid[j][i]
                if(block != null && block !is GarbageBlock){
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
            if(block is PuyoBlock){
                block.updateSprite("main")
            }
        }
    }

    private fun moveBlock(block: PuyoBlock, direction: Int){
        clearPrevPos(block)
        block.x += direction
        updateMovingPos(block)
    }

    fun updateSprites(){
        val chainedPuyos = puyoChain.flatten()
        for (i in 0 until width){
            for (j in 0 until length){
                val block = grid[i][j]
                if(block is PuyoBlock && !chainedPuyos.contains(block)){
                    block.updateSprite("main")
                }
            }
        }
    }

    fun connectPuyos(){
        for (chain in puyoChain){
            if(chain.isEmpty()){
                continue
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
                    block.updateSprite(s)
                    if(chain.size >= 4){
                        block.isBeingRemoved = true
                        if(block.removeFrames > 20){
                            block.updateSprite("s")
                        }
                    }
                }
            }
        }
    }
}