package com.game.Tetris

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.game.Garbage
import com.game.Sounds
import com.game.SpriteArea
import com.game.puyo.PuyoGame
import com.game.puyo.Time
import kotlin.random.Random

class TetrisGame {
    private lateinit var puyo: PuyoGame
    var nextTetrominos: com.badlogic.gdx.utils.Array<Tetromino> = com.badlogic.gdx.utils.Array(5)
    lateinit var currentTetromino: Tetromino
    var heldTetromino: Tetromino? = null

    private val tetrominoTypes: MutableList<Char> = mutableListOf('T', 'O', 'I', 'J', 'L', 'S', 'Z')
    private lateinit var currentTypes: MutableList<Char>

    var gameOver: Boolean = false
    private var enableHold: Boolean = true
    private var wallKicked: Boolean = false
    private var tSpinInput: Boolean = false
    var hasStarted = false

    var scoring: TetrisScoring = TetrisScoring()
    var comboCount: Int = 0
    private var b2bBonus: Int = 0 // basically works as boolean here using 0 and 1

    // y-offsets have to be reversed from srs system
    private var offsets02: Array<Pair<Int, Int>> = arrayOf(Pair(0, 0), Pair(0, 0), Pair(0, 0), Pair(0, 0), Pair(0, 0))
    private var offsetsL: Array<Pair<Int, Int>> = arrayOf(Pair(0, 0), Pair(-1, 0), Pair(-1, 1), Pair(0, -2), Pair(-1, -2))
    private var offsetsR: Array<Pair<Int, Int>> = arrayOf(Pair(0, 0), Pair(1, 0), Pair(1, 1), Pair(0, -2), Pair(-1, -2))

    private var offsetsMap: MutableMap<Char, Array<Pair<Int, Int>>> = mutableMapOf('0' to offsets02, 'L' to offsetsL, 'R' to offsetsR, '2' to offsets02)

    // I has own offsets (what a troublesome tetromino)
    private var iOffsets0: Array<Pair<Int, Int>> = arrayOf(Pair(0, 0), Pair(-1, 0), Pair(2, 0), Pair(-1, 0), Pair(2, 0))
    private var iOffsetsL: Array<Pair<Int, Int>> = arrayOf(Pair(0, -1), Pair(0, -1), Pair(0, -1), Pair(0, 1), Pair(0, -2))
    private var iOffsetsR: Array<Pair<Int, Int>> = arrayOf(Pair(-1, 0), Pair(0, 0), Pair(0, 0), Pair(0, -1), Pair(0, 2))
    private var iOffsets2: Array<Pair<Int, Int>> = arrayOf(Pair(-1, -1), Pair(1, -1), Pair(-2, -1), Pair(1, 0), Pair(-2, 0))

    private var iOffsetsMap: MutableMap<Char, Array<Pair<Int, Int>>> = mutableMapOf('0' to iOffsets0, 'L' to iOffsetsL, 'R' to iOffsetsR, '2' to iOffsets2)


    var dropTetrominoTime = Time(500)
    var downKeyHeldTime = Time(200)
    var moveKeyHeldTime = Time(230)
    var removeLineTime = Time(250)
    var hardDropTime = Time(120)
    var comboTime = Time(100)
    var b2bTime = Time(100)
    var tetrisTime = Time(100)
    var tSpin0Time = Time(100)
    var tSpin1Time = Time(100)
    var tSpin2Time = Time(100)
    var tSpin3Time = Time(100)
    var tSpinMini0Time = Time(100)
    var tSpinMiniTime = Time(100)


    var columns: Int = 10
    var rows: Int = 25
    var cells: Array<com.badlogic.gdx.utils.Array<TetrisBlock>> = Array(columns) {com.badlogic.gdx.utils.Array<TetrisBlock>(rows)}

    init {
        for (row in cells) {
            for (i in 0 until rows) {
                row.add(null)
            }
        }
        cancelAnimTimers()
        createRandomOrder()
        spawnTetromino()
        createNextTetrominos()
    }

    fun setPuyo(puyo: PuyoGame){
        this.puyo = puyo
    }

    fun cancelAnimTimers() {
        removeLineTime.cancel()
        hardDropTime.cancel()
        comboTime.cancel()
        b2bTime.cancel()
        tetrisTime.cancel()
        tSpin0Time.cancel()
        tSpin1Time.cancel()
        tSpin2Time.cancel()
        tSpin3Time.cancel()
        tSpinMini0Time.cancel()
        tSpinMiniTime.cancel()
    }

    fun run() {
        if (!gameOver && hasStarted) {
            handleTimers()
            handleInputs()
        }
    }

    private fun handleTimers() {
        // more time to act when tetromino is about to land
        if (tetrominoLanded(currentTetromino) && currentTetromino.isFalling) dropTetrominoTime.delay = 800
        else dropTetrominoTime.delay = 500

        if (dropTetrominoTime.hasPassed()) {
            if (currentTetromino.isFalling) {
                dropTetromino(currentTetromino)
            } else {
                spawnTetromino()
                tSpinInput = false
                enableHold = true
            }
            dropTetrominoTime.delay = 500
            dropTetrominoTime.reset()
        }

        if (removeLineTime.isRunning()) {
            if (getFullRows().size > 0) {
                dropTetrominoTime.startAt(dropTetrominoTime.delay - (removeLineTime.delay.toLong() - removeLineTime.runtime()) - 50)
                if (removeLineTime.hasPassed()) {
                    removeLineTime.cancel()
                    updateRows()
                }
            } else {
                updateRows()
                removeLineTime.cancel()
            }
        }

        if (hardDropTime.hasPassed()) hardDropTime.cancel()

        if (comboCount < 1 && comboTime.isRunning()) comboTime.cancel()
    }

    private fun handleInputs() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            moveLeft(currentTetromino)
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if (moveKeyHeldTime.hasPassed()) {
                moveLeft(currentTetromino)
                moveKeyHeldTime.startAt(190)
            }
        }


        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            moveRight(currentTetromino)
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (moveKeyHeldTime.hasPassed()) {
                moveRight(currentTetromino)
                moveKeyHeldTime.startAt(190)
            }
        }

        if (!Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            moveKeyHeldTime.reset()
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            if (currentTetromino.isFalling) {
                dropTetromino(currentTetromino)
                if (currentTetromino.isFalling) {
                    dropTetrominoTime.reset()
                    scoring.tetrisScore++
                    Sounds.tfall.play()
                } else dropTetrominoTime.startAt(380)
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            if (!tetrominoLanded(currentTetromino)) {
                if (downKeyHeldTime.hasPassed()) {
                    dropTetromino(currentTetromino)
                    downKeyHeldTime.startAt(160)
                    scoring.tetrisScore++
                    dropTetrominoTime.reset()
                    Sounds.tfall.play()
                }
            }
        } else downKeyHeldTime.reset()


        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            if (currentTetromino.isFalling) {
                dropTetrominoTime.startAt(380)
                hardDropTime.reset()
                Sounds.thdrop.play()
            }
            while (currentTetromino.isFalling) {
                dropTetromino(currentTetromino)
                if (currentTetromino.isFalling) scoring.tetrisScore += 2
            }

        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.PERIOD)) {
            if (currentTetromino.isFalling) {
                turnLeft(currentTetromino)
                Sounds.trotate.play()
            }
            if (tetrominoLanded(currentTetromino) && currentTetromino.type == 'T') tSpinInput = true
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {
            if (currentTetromino.isFalling) {
                turnRight(currentTetromino)
                Sounds.trotate.play()
            }
            if (tetrominoLanded(currentTetromino) && currentTetromino.type == 'T') tSpinInput = true
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && enableHold) {
            holdTetromino()
        }
    }

    private fun spawnTetromino(){
        if (currentTypes.isEmpty()) createRandomOrder()
        if (nextTetrominos.size > 0) {
            currentTetromino = nextTetrominos.pop()
            nextTetrominos.insert(0, Tetromino(4, 1, currentTypes.removeAt(0),
                    TetrisSprite.values()[Random.nextInt(TetrisSprite.values().size-1)].sprite))
        }
        else currentTetromino = Tetromino(4, 1, currentTypes.removeAt(0),
                TetrisSprite.values()[Random.nextInt(TetrisSprite.values().size-1)].sprite)
        addTetromino(currentTetromino)
    }

    private fun createNextTetrominos() {
        for (i in 0 until 5) {
            // gotta add stuff
            nextTetrominos.add(Tetromino(4, 1, currentTypes.removeAt(0),
                    TetrisSprite.values()[Random.nextInt(TetrisSprite.values().size-1)].sprite))
        }
    }

    private fun createRandomOrder() {
        currentTypes = tetrominoTypes.toMutableList()
        currentTypes.shuffle()
    }

    private fun holdTetromino() {
        if (currentTetromino.isFalling) {
            dropTetrominoTime.reset()
            if (heldTetromino != null) {
                val temp: Tetromino = heldTetromino!!
                heldTetromino = currentTetromino
                currentTetromino = temp

                removeTetromino(heldTetromino!!) // has to be removed before setting current tetrominos' position
                currentTetromino.setPosition(4, 1) // y calc for some reason necessary
                addTetromino(currentTetromino)
            } else {
                heldTetromino = currentTetromino
                removeTetromino(heldTetromino!!)
                spawnTetromino()
            }
            while (heldTetromino!!.rotationState != '0') {
                heldTetromino!!.turnLeft()
            }
            enableHold = false
            Sounds.thold.play()
        }
    }

    private fun addTetromino (block: Tetromino) {
        if (wrongState(block)) gameOver()
        else {
            for (i in block.shape.indices) {
                for (cell in block.shape[i]) {
                    if (cell != null) {
                        cells[cell.column][cell.row] = cell
                    }
                }
            }
        }
    }

    private fun dropTetromino (block: Tetromino) {
        if (block.isFalling && !tetrominoLanded(block)) {
            for (i in block.shape.size - 1 downTo 0) {
                for (j in block.shape[i].size - 1 downTo 0) {
                    if (block.shape[j][i] != null) {
                        cells[block.shape[j][i].column][block.shape[j][i].row + 1] = block.shape[j][i]
                        cells[block.shape[j][i].column][block.shape[j][i].row] = null
                    }
                }
            }
            block.move(0, 1)
        } else if (tetrominoLanded(block)) {
            block.isFalling = false
            if (getFullRows().size == 0) Sounds.tdown.play()
        }
        if (!currentTetromino.isFalling) removeLineTime.reset()
    }

    private fun removeTetromino(block: Tetromino) {
        for (i in block.shape.indices) {
            for (j in 0 until block.shape[i].size) {
                if (block.shape[i][j] != null && cells[block.shape[i][j].column][block.shape[i][j].row] == block.shape[i][j]) {
                    cells[block.shape[i][j].column][block.shape[i][j].row] = null
                }
            }
        }
    }

    private fun tetrominoLanded (block: Tetromino): Boolean { // only gives back if can't move down
        for (i in block.shape.indices) {
            for (j in 0 until block.shape[i].size) {
                if (block.shape[i][j] != null) {
                    if (block.shape[i][j].row < rows - 1) {
                        if (cells[block.shape[i][j].column][block.shape[i][j].row + 1] != null &&
                                block.shape[i][j + 1] == null) {
                            return true
                        }
                    } else {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun moveRight(block: Tetromino) {
        if (block.isFalling && !rightIsBlocked(block)) {
            for (i in block.shape[0].size - 1 downTo 0) { // reverse order is necessary
                for (j in block.shape.size - 1 downTo 0) {
                    if(block.shape[i][j] != null) {
                        cells[block.shape[i][j].column + 1][block.shape[i][j].row] = block.shape[i][j]
                        cells[block.shape[i][j].column][block.shape[i][j].row] = null
                    }
                }
            }
            block.move(1, 0)
            Sounds.tmove.play()
        }
    }

    private fun rightIsBlocked (block: Tetromino): Boolean {
        for (i in block.shape.indices) {
            for (j in 0 until block.shape[i].size) {
                if (block.shape[i][j] != null) {
                    if (block.shape[i][j].column < columns - 1) {
                        if (cells[block.shape[i][j].column + 1][block.shape[i][j].row] != null &&
                                cells[block.shape[i][j].column + 1][block.shape[i][j].row] != block.shape[i + 1][j]) {
                            return true
                        }
                    } else return true
                }
            }
        }
        return false
    }

    private fun moveLeft(block: Tetromino) {
        if (block.isFalling && !leftIsBlocked(block)) {
            for (i in 0 until block.shape[0].size) {
                for (j in block.shape.indices) {
                    if(block.shape[i][j] != null) {
                        cells[block.shape[i][j].column - 1][block.shape[i][j].row] = block.shape[i][j]
                        cells[block.shape[i][j].column][block.shape[i][j].row] = null
                    }
                }
            }
            block.move(-1, 0)
            Sounds.tmove.play()
        }
    }

    private fun leftIsBlocked (block: Tetromino): Boolean {
        for (i in block.shape.indices) {
            for (j in 0 until block.shape[i].size) {
                if (block.shape[i][j] != null) {
                    if (block.shape[i][j].column > 0) {
                        if (cells[block.shape[i][j].column - 1][block.shape[i][j].row] != null &&
                                cells[block.shape[i][j].column - 1][block.shape[i][j].row] != block.shape[i - 1][j]) {
                            return true
                        }
                    } else return true
                }
            }
        }
        return false
    }

    private fun turnLeft(block: Tetromino){ // might change for I
        if (block.type != 'O') {
            for (i in block.shape.indices) {
                for (j in 0 until block.shape[i].size) {
                    if (block.shape[i][j] != null) {
                        cells[block.shape[i][j].column][block.shape[i][j].row] = null
                    }
                }
            }
            wallKick(block, 'L')
            for (i in block.shape.indices) {
                for (j in 0 until block.shape[i].size) {
                    if (block.shape[i][j] != null) {
                        cells[block.shape[i][j].column][block.shape[i][j].row] = block.shape[i][j]
                    }
                }
            }
        }
    }

    private fun wallKick(block: Tetromino, rotation: Char) {
        val oldState: Char = block.rotationState
        if (rotation == 'L') block.turnLeft()
        else if (rotation == 'R') block.turnRight()

        val map: MutableMap<Char, Array<Pair<Int, Int>>> = if (block.type != 'I') offsetsMap else iOffsetsMap

        for (i in 0..4) {
            block.move(map[oldState]!![i].first - map[block.rotationState]!![i].first,
                    map[oldState]!![i].second - map[block.rotationState]!![i].second)
            if (wrongState(block)) {
                block.move(-(map[oldState]!![i].first - map[block.rotationState]!![i].first),
                        -(map[oldState]!![i].second - map[block.rotationState]!![i].second))
            } else {
                wallKicked = i != 0
                break
            }
        }
        if (wrongState(block)) {
            if (rotation == 'L') block.turnRight() else block.turnLeft()
        }
    }

    private fun turnRight(block: Tetromino){ // not compatible with I
        if (block.type != 'O') {
            for (i in block.shape.indices) {
                for (j in 0 until block.shape[i].size) {
                    if (block.shape[i][j] != null) {
                        // matrix formula, just for clarity sake I keep unnecessary steps
                        cells[block.shape[i][j].column][block.shape[i][j].row] = null
                    }
                }
            }
            wallKick(block, 'R')
            for (i in block.shape.indices) {
                for (j in 0 until block.shape[i].size) {
                    if (block.shape[i][j] != null) {
                        cells[block.shape[i][j].column][block.shape[i][j].row] = block.shape[i][j]
                    }
                }
            }
        }
    }

    private fun wrongState(block: Tetromino): Boolean {
        for (i in block.shape.indices) {
            for (j in 0 until block.shape[i].size) {
                if (block.shape[i][j] != null) {
                    if (block.shape[i][j].column < 0 || block.shape[i][j].column >= columns ||
                            block.shape[i][j].row < 0 || block.shape[i][j].row >= rows) {
                        return true
                    } else if (cells[block.shape[i][j].column][block.shape[i][j].row] != null) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun updateRows() {
        var pointsAdded = false
        val fullRows: com.badlogic.gdx.utils.Array<Int> = getFullRows()
        if (fullRows.size > 0) {
            if (isTSpin()) {
                tSpinClear(fullRows.size)
                pointsAdded = true
            }
            removeRows(fullRows)
            if (isPerfectClear()) perfectClear(fullRows.size)
            else if (!pointsAdded) {
                if (fullRows.size == 4) tetrisClear() else lineClear(fullRows.size)
            }
            comboCount++
            if (comboCount > 1) comboTime.reset()
            if (comboCount >= 20) scoring.tetrisScore += 1000
            else scoring.tetrisScore += comboCount * scoring.scoreComboBonus
            println("Combo: $comboCount")
        } else {
            if (isTSpin()) {
                tSpinZeroBonus()
            }
            scoring.puyoGarbage += scoring.getComboBonus(comboCount)
            comboCount = 0
            handleGarbage()
        }
        wallKicked = false
    }

    private fun removeRows(fullRows: com.badlogic.gdx.utils.Array<Int>) {
        for (row in fullRows) {
            for (j in row downTo 1) {
                for (i in cells.size - 1 downTo 0) {
                    cells[i][j] = cells[i][j - 1]
                }
            }
            for (i in cells.indices) {
                cells[i][0] = null
            }
        }
    }

    private fun handleGarbage() {
        if(scoring.tetrisGarbage > 0){
            scoring.tetrisGarbage -= scoring.puyoGarbage
            if(scoring.tetrisGarbage < 0){
                scoring.puyoGarbage = -scoring.tetrisGarbage
                scoring.tetrisGarbage = 0
                sendGarbage()
            }
        } else sendGarbage()
        fillGarbage()
    }

    private fun lineClear(lines: Int) {
        scoring.puyoGarbage += scoring.clearBonus[lines]!!
        scoring.tetrisScore += scoring.scoreClearBonus[lines]!!
        b2bBonus = 0
        Sounds.tkesiline.play()
    }

    private fun tetrisClear() {
        println("Tetris")
        tetrisTime.reset()
        if (b2bBonus == 1) {
            println("Back-to-back")
            b2bTime.reset()
            Sounds.tkesib2b.play()
        } else Sounds.tkesitetris.play()
        scoring.puyoGarbage += scoring.clearBonus[4]!! + b2bBonus
        scoring.tetrisScore += (scoring.scoreClearBonus[4]!! * (1 + (b2bBonus * 0.5f))).toInt()
        b2bBonus = 1
    }

    private fun perfectClear(lines: Int) {
        scoring.puyoGarbage += scoring.perfectClearBonus
        scoring.tetrisScore += scoring.scorePerfectClearBonus[lines]!!
        b2bBonus = 0
    }

    private fun cancelTSpinTimers() {
        tSpin0Time.cancel()
        tSpin1Time.cancel()
        tSpin2Time.cancel()
        tSpin3Time.cancel()
        tSpinMini0Time.cancel()
        tSpinMiniTime.cancel()
    }

    fun getCurrentTSpinTimer(): Time? {
        if (tSpin1Time.isRunning()) return tSpin1Time
        else if (tSpin2Time.isRunning()) return tSpin2Time
        else if (tSpin3Time.isRunning()) return tSpin3Time
        else if (tSpinMiniTime.isRunning()) return tSpinMiniTime
        else if (tSpin0Time.isRunning()) return tSpin0Time
        else if (tSpinMini0Time.isRunning()) return tSpinMini0Time
        else return null
    }

    private fun tSpinClear(lines: Int) {
        cancelTSpinTimers()
        if (b2bBonus == 1) {
            println("Back-to-back")
            b2bTime.reset()
            Sounds.tkesib2b.play()
        } else Sounds.tkesispin.play()
        if (!wallKicked || lines > 1) {
            scoring.puyoGarbage += scoring.tSpinClearBonus[lines]!! + b2bBonus
            scoring.tetrisScore += (scoring.scoreTSpinClearBonus[lines]!! * (1 + (b2bBonus * 0.5f))).toInt()
            when (lines) {
                1 -> tSpin1Time.reset()
                2 -> tSpin2Time.reset()
                3 -> tSpin3Time.reset()
            }
        } else {
            scoring.tetrisScore += (scoring.scoreTSpinMiniBonus * (1 + (b2bBonus * 0.5f))).toInt()
            println("Mini t spin single")
            tSpinMiniTime.reset()
        }
        b2bBonus = 1
    }

    private fun tSpinZeroBonus() {
        cancelTSpinTimers()
        if (!wallKicked) {
            scoring.tetrisScore += scoring.scoreTSpinZeroBonus
            println("T-Spin")
            tSpin0Time.reset()
        } else {
            scoring.tetrisScore += scoring.scoreTSpinMiniZeroBonus
            println("Mini-T-Spin")
            tSpinMini0Time.reset()
        }
        Sounds.tspin.play()
    }

    private fun isTSpin(): Boolean {
        if (currentTetromino.type == 'T') {
            var onWall = false
            var blocks = 0
            for (i in currentTetromino.column - 1..currentTetromino.column + 1) {
                for (j in currentTetromino.row - 1..currentTetromino.row + 1) {
                    if (i >= columns || i < 0 || j >= rows) {
                        if (!onWall) {
                            blocks += 2
                            onWall = true
                        }
                    }
                    else if (cells[i][j] != null && !currentTetromino.contains(cells[i][j])) blocks++
                }
            }
            if (tSpinInput && blocks >= 3) return true
        }
        return false
    }

    fun getFullRows(): com.badlogic.gdx.utils.Array<Int> {
        var rowIsFull = true
        // LibGDX Array because apparently ArrayList takes memory space
        val fullRows: com.badlogic.gdx.utils.Array<Int> = com.badlogic.gdx.utils.Array()
        for (i in 0 until cells[0].size) {
            for (cell in cells) {
                if (cell[i] == null) rowIsFull = false
            }
            if (rowIsFull) {
                fullRows.add(i)
            } else rowIsFull = true
        }
        return fullRows
    }

    private fun isFull(): Boolean {
        for (column in cells) {
            if (column[0] != null) {
                return true
            }
        }
        return false
    }

    private fun isPerfectClear(): Boolean {
        for (i in cells.indices) {
            for (j in 0 until cells[i].size) {
                if (cells[i][j] != null) return false
            }
        }
        return true
    }

    private fun fillGarbage() {
        if (scoring.tetrisGarbage > 0) {
            for (line in 1..scoring.tetrisGarbage) {
                if (!isFull()) {
                    for (i in 0 until rows - 1) {
                        for (j in 0 until columns) {
                            cells[j][i] = cells[j][i + 1]
                        }
                    }
                    val freeColumn: Int = Random.nextInt(columns - 1)
                    for (i in 0 until columns) {
                        if (i != freeColumn) cells[i][rows - 1] = TetrisBlock(i, rows - 1, SpriteArea.tetrisSprites["garbage"]!!)
                        else cells[i][rows - 1] = null
                    }
                }
            }
            if (scoring.tetrisGarbage < 8) Sounds.tlinedown.play() else Sounds.tlineup.play()
            scoring.tetrisGarbage = 0
        }
    }

    fun getShadowCoordinates(): MutableList<Pair<Int, Int>>? {
        if (!tetrominoLanded(currentTetromino)) {
            val coordinates: MutableList<Pair<Int, Int>> = mutableListOf()
            var dropsTillLand: Int = Int.MAX_VALUE
            var currentDrops = 0
            for (i in currentTetromino.shape.indices) {
                for (j in 0 until currentTetromino.shape[i].size) {
                    if (currentTetromino.shape[i][j] != null) {
                        val block: TetrisBlock = currentTetromino.shape[i][j]
                        coordinates.add(Pair(block.column, block.row))
                        while (block.row + currentDrops < rows && (cells[block.column][block.row + currentDrops] == null
                                || currentTetromino.contains(cells[block.column][block.row + currentDrops]))) {
                            currentDrops++
                        }
                        if (currentDrops <= dropsTillLand) dropsTillLand = currentDrops - 1
                        currentDrops = 0
                    }
                }
            }
            for (i in coordinates.indices) {
                coordinates[i] = Pair(coordinates[i].first, coordinates[i].second + dropsTillLand)
            }
            return coordinates
        }
        return null
    }

    private fun gameOver() {
        if (!gameOver) Sounds.tover.play()
        gameOver = true
        //println("Tetris lost")
    }

    private fun sendGarbage() {
        if(scoring.puyoGarbage > 0){
            puyo.receiveGarbage(Garbage.tetrisToPuyo.getOrDefault(scoring.puyoGarbage, 30))
            println("Tetris sent ${Garbage.tetrisToPuyo.getOrDefault(scoring.puyoGarbage, 30)} Puyo Garbage")
            when {
                Garbage.tetrisToPuyo.getOrDefault(scoring.puyoGarbage, 30) in 1..5 -> Sounds.gsend1.play()
                Garbage.tetrisToPuyo.getOrDefault(scoring.puyoGarbage, 30) in 6..29 -> Sounds.gsend2.play()
                Garbage.tetrisToPuyo.getOrDefault(scoring.puyoGarbage, 30) in 30..179 -> Sounds.gsend3.play()
                Garbage.tetrisToPuyo.getOrDefault(scoring.puyoGarbage, 30) >= 180 -> Sounds.gsend4.play()
            }
            scoring.puyoGarbage = 0
        }
    }

    fun receiveGarbage(amount: Int) {
        scoring.tetrisGarbage += amount
        println("Tetris received $amount Tetris Garbage")
    }

}