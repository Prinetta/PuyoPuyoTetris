package com.game.Tetris

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.game.Garbage
import com.game.SpriteArea
import com.game.puyo.Puyo
import com.game.puyo.PuyoGame
import kotlin.random.Random

class TetrisGame() {
    private lateinit var puyo: PuyoGame
    var nextTetrominos: com.badlogic.gdx.utils.Array<Tetromino> = com.badlogic.gdx.utils.Array(5)
    private lateinit var currentTetromino: Tetromino
    var heldTetromino: Tetromino? = null

    private val tetrominoTypes: MutableList<Char> = mutableListOf('T', 'O', 'I', 'J', 'L', 'S', 'Z')
    private lateinit var currentTypes: MutableList<Char>

    private var gameIsOver: Boolean = false
    private var enableHold: Boolean = true
    private var tSpinInput: Boolean = false

    private var scoring: TetrisScoring = TetrisScoring()
    private var comboCount: Int = 0
    private var b2bBonus: Int = 0 // basically works as boolean here using 0 and 1

    // y-offsets have to be reversed from srs system
    var offsets02: Array<Pair<Int, Int>> = arrayOf(Pair(0, 0), Pair(0, 0), Pair(0, 0), Pair(0, 0), Pair(0, 0))
    var offsetsL: Array<Pair<Int, Int>> = arrayOf(Pair(0, 0), Pair(-1, 0), Pair(-1, 1), Pair(0, -2), Pair(-1, -2))
    var offsetsR: Array<Pair<Int, Int>> = arrayOf(Pair(0, 0), Pair(1, 0), Pair(1, 1), Pair(0, -2), Pair(-1, -2))

    var offsetsMap: MutableMap<Char, Array<Pair<Int, Int>>> = mutableMapOf('0' to offsets02, 'L' to offsetsL, 'R' to offsetsR, '2' to offsets02)

    // I has own offsets (what a troublesome tetromino)
    var iOffsets0: Array<Pair<Int, Int>> = arrayOf(Pair(0, 0), Pair(-1, 0), Pair(2, 0), Pair(-1, 0), Pair(2, 0))
    var iOffsetsL: Array<Pair<Int, Int>> = arrayOf(Pair(0, -1), Pair(0, -1), Pair(0, -1), Pair(0, 1), Pair(0, -2))
    var iOffsetsR: Array<Pair<Int, Int>> = arrayOf(Pair(-1, 0), Pair(0, 0), Pair(0, 0), Pair(0, -1), Pair(0, 2))
    var iOffsets2: Array<Pair<Int, Int>> = arrayOf(Pair(-1, -1), Pair(1, -1), Pair(-2, -1), Pair(1, 0), Pair(-2, 0))

    var iOffsetsMap: MutableMap<Char, Array<Pair<Int, Int>>> = mutableMapOf('0' to iOffsets0, 'L' to iOffsetsL, 'R' to iOffsetsR, '2' to iOffsets2)


    var dropTetrominoTimer: Float = 0f
    var downKeyHeldTimer: Float = 0f
    var moveKeyHeldTimer: Float = 0f

    var columns: Int = 10
    var rows: Int = 25
    var cells: Array<com.badlogic.gdx.utils.Array<TetrisBlock>> = Array(columns) {com.badlogic.gdx.utils.Array<TetrisBlock>(rows)}

    init {
        for (row in cells) {
            for (i in 0 until rows) {
                row.add(null)
            }
        }
        createRandomOrder()
        spawnTetromino()
        createNextTetrominos()
    }

    fun setPuyo(puyo: PuyoGame){
        this.puyo = puyo
    }

    fun handleInputs(delta: Float) {
        if (dropTetrominoTimer > 0.5f) {
            if (currentTetromino.isFalling) {
                dropTetromino(currentTetromino)
            } else {
                updateRows()
                spawnTetromino()
                tSpinInput = false
                enableHold = true
                sendGarbage(2) //TODO: delete this!!! this is debugging only nhh
            }
            dropTetrominoTimer = 0f
        }
        else dropTetrominoTimer += delta


        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            moveLeft(currentTetromino)
            moveKeyHeldTimer = -0.4f
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if (!tetrominoLanded(currentTetromino)) {
                moveKeyHeldTimer += delta + 0.02f
                if (moveKeyHeldTimer > 0.1f) {
                    moveLeft(currentTetromino)
                    moveKeyHeldTimer = 0f
                }
            }
        }


        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            moveRight(currentTetromino)
            moveKeyHeldTimer = -0.4f
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (!tetrominoLanded(currentTetromino)) {
                moveKeyHeldTimer += delta + 0.02f
                if (moveKeyHeldTimer > 0.1f) {
                    moveRight(currentTetromino)
                    moveKeyHeldTimer = 0f
                }
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            moveKeyHeldTimer = 0f
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            if (!tetrominoLanded(currentTetromino)) dropTetromino(currentTetromino)
            if (currentTetromino.isFalling) dropTetrominoTimer = 0f
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            if (!tetrominoLanded(currentTetromino)) {
                downKeyHeldTimer += delta + 0.02f
                if (downKeyHeldTimer > 0.5f) dropTetrominoTimer += 0.25f
            }
        } else downKeyHeldTimer = 0f


        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            if (currentTetromino.isFalling) dropTetrominoTimer = 0.3f
            while (currentTetromino.isFalling) {
                dropTetromino(currentTetromino)
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.PERIOD)) {
            if (currentTetromino.isFalling) turnLeft(currentTetromino)
            if (tetrominoLanded(currentTetromino) && currentTetromino.type == 'T') tSpinInput = true
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {
            if (currentTetromino.isFalling) turnRight(currentTetromino)
            if (tetrominoLanded(currentTetromino) && currentTetromino.type == 'T') tSpinInput = true
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && enableHold) {
            holdTetromino()
        }
    }

    fun spawnTetromino(){
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

    fun createNextTetrominos() {
        for (i in 0 until 5) {
            // gotta add stuff
            nextTetrominos.add(Tetromino(4, 1, currentTypes.removeAt(0),
                    TetrisSprite.values()[Random.nextInt(TetrisSprite.values().size-1)].sprite))
        }
    }

    fun createRandomOrder() {
        currentTypes = tetrominoTypes.toMutableList()
        currentTypes.shuffle()
    }

    fun holdTetromino() {
        dropTetrominoTimer = 0f
        if (heldTetromino != null) {
            var temp: Tetromino = heldTetromino!!
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
    }

    fun addTetromino (block: Tetromino) {
        if (wrongState(block)) gameOver()
        else {
            for (i in block.shape.indices) {
                for (cell in block.shape[i]) {
                    if (cell != null) {
                        cells[cell.column][cell.row] = cell
                    }
                }
            }
            if (tetrominoLanded(block)) block.isFalling = false
        }
    }

    fun dropTetromino (block: Tetromino) {
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
        } else if (tetrominoLanded(block)) block.isFalling = false
    }

    fun removeTetromino(block: Tetromino) {
        for (i in block.shape.indices) {
            for (j in 0 until block.shape[i].size) {
                if (block.shape[i][j] != null && cells[block.shape[i][j].column][block.shape[i][j].row] == block.shape[i][j]) {
                    cells[block.shape[i][j].column][block.shape[i][j].row] = null
                }
            }
        }
    }

    fun tetrominoLanded (block: Tetromino): Boolean { // only gives back if can't move down
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

    fun moveRight(block: Tetromino) {
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
        }
    }

    fun rightIsBlocked (block: Tetromino): Boolean {
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

    fun moveLeft(block: Tetromino) {
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
        }
    }

    fun leftIsBlocked (block: Tetromino): Boolean {
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

    fun turnLeft(block: Tetromino){ // might change for I
        if (block.type != 'O') {
            for (i in block.shape.indices) {
                for (j in 0 until block.shape[i].size) {
                    if (block.shape[i][j] != null) {
                        cells[block.shape[i][j].column][block.shape[i][j].row] = null
                    }
                }
            }
            wallKickDef(block, 'L')
            for (i in block.shape.indices) {
                for (j in 0 until block.shape[i].size) {
                    if (block.shape[i][j] != null) {
                        cells[block.shape[i][j].column][block.shape[i][j].row] = block.shape[i][j]
                    }
                }
            }
        }
    }

    fun wallKickDef(block: Tetromino, rotation: Char) {
        val oldState: Char = block.rotationState
        if (rotation == 'L') block.turnLeft()
        else if (rotation == 'R') block.turnRight()

        val map: MutableMap<Char, Array<Pair<Int, Int>>>
        if (block.type != 'I') map = offsetsMap
        else map = iOffsetsMap

        for (i in 0..4) {
            block.move(map[oldState]!![i].first - map[block.rotationState]!!.get(i).first,
                    map[oldState]!!.get(i).second - map.get(block.rotationState)!!.get(i).second)
            if (wrongState(block)) {
                block.move(-(map.get(oldState)!!.get(i).first - map.get(block.rotationState)!!.get(i).first),
                        -(map.get(oldState)!!.get(i).second - map.get(block.rotationState)!!.get(i).second))
            } else break
        }
        if (wrongState(block)) {
            if (rotation == 'L') block.turnRight() else block.turnLeft()
        }
    }

    fun turnRight(block: Tetromino){ // not compatible with I
        if (block.type != 'O') {
            for (i in block.shape.indices) {
                for (j in 0 until block.shape[i].size) {
                    if (block.shape[i][j] != null) {
                        // matrix formula, just for clarity sake I keep unnecessary steps
                        cells[block.shape[i][j].column][block.shape[i][j].row] = null
                    }
                }
            }
            wallKickDef(block, 'R')
            for (i in block.shape.indices) {
                for (j in 0 until block.shape[i].size) {
                    if (block.shape[i][j] != null) {
                        cells[block.shape[i][j].column][block.shape[i][j].row] = block.shape[i][j]
                    }
                }
            }
        }
    }

    fun wrongState(block: Tetromino): Boolean {
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

    fun updateRows() {
        var pointsAdded: Boolean = false
        val fullRows: com.badlogic.gdx.utils.Array<Int> = getFullRows()
        if (tSpinInput) println("T-Spin")
        if (fullRows.size > 0) {
            if (isTSpin()) {
                if (b2bBonus == 1) println("Back-to-back")
                scoring.puyoGarbage += scoring.tSpinClearBonus[fullRows.size]!! + b2bBonus
                pointsAdded = true
                b2bBonus = 1
            }
            for (row in fullRows) {
                for (j in row downTo 1) {
                    for (i in cells.size - 1 downTo 0) {
                        cells[i][j] = cells[i][j - 1]
                    }
                }
            }
            if (isPerfectClear()) {
                scoring.puyoGarbage += scoring.perfectClearBonus
                b2bBonus = 0
            }
            else if (!pointsAdded) {
                if (fullRows.size == 4) {
                    println("Tetris")
                    if (b2bBonus == 1) println("Back-to-back")
                    scoring.puyoGarbage += scoring.clearBonus[fullRows.size]!! + b2bBonus
                    b2bBonus = 1
                } else {
                    scoring.puyoGarbage += scoring.clearBonus[fullRows.size]!!
                    b2bBonus = 0
                }
            }
            comboCount++
            println("Combo: $comboCount")
        } else {
            scoring.puyoGarbage += scoring.getComboBonus(comboCount)
            comboCount = 0
            sendGarbage(scoring.puyoGarbage)
            fillGarbage()
        }
    }

    fun isTSpin(): Boolean {
        if (currentTetromino.type == 'T') {
            var onWall: Boolean = false
            var blocks: Int = 0
            for (i in currentTetromino.column - 1..currentTetromino.column + 1) {
                for (j in currentTetromino.row - 1..currentTetromino.row + 1) {
                    if (i >= columns || i < 0 || j >= rows) {
                        if (!onWall) {
                            blocks += 2
                            onWall = true
                        }
                    }
                    else if (cells[i][j] != null && !currentTetromino.contains(cells[i][j])) {
                        blocks++
                    }
                }
            }
            if (tSpinInput && blocks >= 3) return true
        }
        return false
    }

    fun getFullRows(): com.badlogic.gdx.utils.Array<Int> {
        var rowIsFull: Boolean = true
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

    fun isFull(): Boolean {
        for (i in 0 until cells.size) {
            if (cells[i][0] != null) {
                return true
            }
        }
        return false
    }

    fun isPerfectClear(): Boolean {
        for (i in cells.indices) {
            for (j in 0 until cells[i].size) {
                if (cells[i][j] != null) return false
            }
        }
        return true
    }

    fun fillGarbage() {
        if (scoring.tetrisGarbage > 0) {
            for (line in 1..scoring.tetrisGarbage) {
                if (!isFull()) {
                    for (i in 0 until rows - 1) {
                        for (j in 0 until columns) {
                            cells[j][i] = cells[j][i + 1]
                        }
                    }
                    var freeColumn: Int = Random.nextInt(columns - 1)
                    for (i in 0 until columns) {
                        if (i != freeColumn) cells[i][rows - 1] = TetrisBlock(i, rows - 1, SpriteArea.tetrisSprites.get("garbage")!!)
                        else cells[i][rows - 1] = null
                    }
                }
            }
            scoring.tetrisGarbage = 0
        }
    }

    fun getShadowCoordinates(): MutableList<Pair<Int, Int>>? {
        if (currentTetromino != null && !tetrominoLanded(currentTetromino)) {
            var coordinates: MutableList<Pair<Int, Int>> = mutableListOf()
            var dropsTillLand: Int = Int.MAX_VALUE
            var currentDrops: Int = 0
            for (i in currentTetromino.shape.indices) {
                for (j in 0 until currentTetromino.shape[i].size) {
                    if (currentTetromino.shape[i][j] != null) {
                        var block: TetrisBlock = currentTetromino.shape[i][j]
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

    fun gameOver() {
        gameIsOver = true
        println("Tetris lost")
    }

    fun sendGarbage(amount: Int) {
        if (amount > scoring.tetrisGarbage) {
            if (amount in 1..30) puyo.receiveGarbage(Garbage.tetrisToPuyo[amount - scoring.tetrisGarbage]!!)
            // sends 30 amount because game should end anyway
            else if (amount > 0) puyo.receiveGarbage(Garbage.tetrisToPuyo[30 - scoring.tetrisGarbage]!!)
        }
        scoring.puyoGarbage = 0
    }

    fun receiveGarbage(amount: Int) {
        scoring.tetrisGarbage += amount
    }
}