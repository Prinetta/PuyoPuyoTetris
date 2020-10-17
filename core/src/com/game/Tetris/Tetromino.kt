package com.game.Tetris

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.game.TC


class Tetromino(var column: Int, var row: Int, var type: Char, var texture: TextureRegion) {

    // Array is two dimensional [row][column]
    var shape: Array<com.badlogic.gdx.utils.Array<TetrisBlock>>
    var rotationState: Char = '0'
    var isFalling: Boolean

    var width: Float = 0f
    var height: Float = 0f
    var initColumns: Int = 0
    var initRows: Int = 0
    var pivotX: Int = 3
    var pivotY: Int = 3

    init {
        shape = Array(7) {com.badlogic.gdx.utils.Array<TetrisBlock>(7)}
        for (array in shape) {
            array.addAll(null, null, null, null, null, null, null)
        }
        when (type) {
            'T' -> createT()
            'O' -> createO()
            'I' -> createI()
            'J' -> createJ()
            'L' -> createL()
            'S' -> createS()
            'Z' -> createZ()
        }
        isFalling = true
        initColumns = getColumns()
        initRows = getRows()
        width = initColumns * TC.CELL_SIZE
        height = initRows * TC.CELL_SIZE
    }

    fun move(x: Int, y: Int) {
        for (i in shape.indices) {
            for (brick in shape[i]) {
                if (brick != null) {
                    brick.column += x
                    brick.row += y
                }
            }
        }
        this.column += x
        this.row += y
    }

    fun setPosition(x: Int, y: Int) {
        for (i in shape.indices) {
            for (j in 0 until shape[i].size) {
                if (shape[i][j] != null) {
                    shape[i][j].setPosition(x + (i - pivotX), y + (j - pivotY))
                }
            }
        }
        this.column = x
        this.row = y
    }

    fun contains(block: TetrisBlock): Boolean {
        for (i in shape.indices) {
            for (j in 0 until shape[i].size) {
                if (shape[i][j] == block) return true
            }
        }
       return false
    }

    fun turnLeft() {
        var newShape = Array(7) {com.badlogic.gdx.utils.Array<TetrisBlock>(7)}
        for (array in newShape) {
            array.addAll(null, null, null, null, null, null, null)
        }
        for (i in shape.indices) {
            for (j in 0 until shape[i].size) {
                if (shape[i][j] != null) {
                    // matrix formula, just for clarity sake I keep unnecessary steps (3/3 is usually middle)
                    var diffI: Int = i - pivotX
                    var diffJ: Int = j - pivotY
                    var newI: Int = (diffI * 0) + (diffJ * -1)
                    var newJ : Int= (diffI * 1) + (diffJ * 0)

                    var diffX: Int = shape[i][j].column - column
                    var diffY: Int = shape[i][j].row - row
                    var newX: Int = (diffX * 0) + (diffY * -1)
                    var newY: Int = (diffX * 1) + (diffY * 0)

                    newShape[pivotX - newI][pivotY - newJ] = shape[i][j]
                    newShape[pivotX - newI][(pivotY - newJ)].setPosition((column - newX), (row - newY))
                }
            }
        }
        shape = newShape
        updateState('L')
    }

    fun turnRight() {
        var newShape = Array(7) {com.badlogic.gdx.utils.Array<TetrisBlock>(7)}
        for (array in newShape) {
            array.addAll(null, null, null, null, null, null, null)
        }
        for (i in shape.indices) {
            for (j in 0 until shape[i].size) {
                if (shape[i][j] != null) {
                    // matrix formula, just for clarity sake I keep unnecessary steps (3/3 is middle)
                    var diffI: Int = i - pivotX
                    var diffJ: Int = j - pivotY
                    var newI: Int = (diffI * 0) + (diffJ * -1)
                    var newJ: Int = (diffI * 1) + (diffJ * 0)

                    var diffX: Int = shape[i][j].column - column
                    var diffY: Int = shape[i][j].row - row
                    var newX: Int = (diffX * 0) + (diffY * -1)
                    var newY: Int = (diffX * 1) + (diffY * 0)

                    newShape[(pivotX + newI)][(pivotY + newJ)] = shape[i][j]
                    // I do not know why I have to add for coordinates don't ask me it works
                    newShape[(pivotX + newI)][(pivotY + newJ)].setPosition((column + newX), (row + newY))
                }
            }
        }
        shape = newShape
        updateState('R')
    }

    fun updateState(rotation: Char) {
        if (rotation == 'L') {
            when(rotationState) {
                '0' -> rotationState = 'L'
                'L' -> rotationState = '2'
                'R' -> rotationState = '0'
                '2' -> rotationState = 'R'
            }
        } else if (rotation == 'R') {
            when(rotationState) {
                '0' -> rotationState = 'R'
                'L' -> rotationState = '0'
                'R' -> rotationState = '2'
                '2' -> rotationState = 'L'
            }
        }
    }

    fun getRows(): Int {
        var rows: Int = 0
        var indices: IntArray = IntArray(7)
        for (i in shape.indices) {
            for (j in 0 until shape[i].size) {
                if (shape[i][j] != null) {
                    if (!indices.contains(j)) {
                        indices[j] = j
                        rows++
                    }
                }
            }
        }
        return rows
    }

    fun getColumns(): Int {
        var columns: Int = 0
        var indices: IntArray = IntArray(7)
        for (i in shape.indices) {
            for (j in 0 until shape[i].size) {
                if (shape[i][j] != null) {
                    if (!indices.contains(i)) {
                        indices[i] = i
                        columns++
                    }
                }
            }
        }
        return columns
    }

    fun firstRow(): Int {
        for (i in shape.indices) {
            for (j in 0 until shape[i].size) {
                if (shape[j][i] != null) {
                    return i
                }
            }
        }
        return -1
    }

    fun firstColumn(): Int {
        for (i in shape.indices) {
            for (j in 0 until shape[i].size) {
                if (shape[i][j] != null) {
                    return i
                }
            }
        }
        return -1
    }

    fun lastRow(): Int {
        for (i in shape.size - 1 downTo 0) {
            for (j in shape[i].size - 1 downTo 0) {
                if (shape[j][i] != null) {
                    return i
                }
            }
        }
        return -1
    }

    fun lastColumn(): Int {
        for (i in shape.size - 1 downTo 0) {
            for (j in shape[i].size - 1 downTo 0) {
                if (shape[i][j] != null) {
                    return i
                }
            }
        }
        return -1
    }

    fun createT() {
        shape[2][3] = TetrisBlock(column - 1, row, texture)
        shape[3][3] = TetrisBlock(column, row, texture) // main brick for T-Block
        shape[4][3] = TetrisBlock(column + 1, row, texture)
        shape[3][2] = TetrisBlock(column, row - 1, texture)
    }

    fun createO() {
        shape[3][3] = TetrisBlock(column, row, texture) // main brick for O-Block
        shape[3][4] = TetrisBlock(column, row + 1, texture)
        shape[4][3] = TetrisBlock(column + 1, row, texture)
        shape[4][4] = TetrisBlock(column + 1, row + 1, texture)
    }

    fun createI() {
        shape[2][3] = TetrisBlock(column - 1, row, texture)
        shape[3][3] = TetrisBlock(column, row, texture) // main brick for I-Block (doesn't matter tho)
        shape[4][3] = TetrisBlock(column + 1, row, texture)
        shape[5][3] = TetrisBlock(column + 2, row, texture)
    }

    fun createJ() {
        shape[2][3] = TetrisBlock(column - 1, row, texture) // main brick for J-Block
        shape[3][3] = TetrisBlock(column, row, texture)
        shape[4][3] = TetrisBlock(column + 1, row, texture)
        shape[2][2] = TetrisBlock(column - 1, row - 1, texture)
    }

    fun createL() {
        shape[4][3] = TetrisBlock(column + 1, row, texture) // main brick for L-Block
        shape[3][3] = TetrisBlock(column, row, texture)
        shape[2][3] = TetrisBlock(column - 1, row, texture)
        shape[4][2] = TetrisBlock(column + 1, row - 1, texture)
    }

    fun createS() {
        shape[3][2] = TetrisBlock(column, row - 1, texture)
        shape[4][2] = TetrisBlock(column + 1, row - 1, texture)
        shape[2][3] = TetrisBlock(column - 1, row, texture)
        shape[3][3] = TetrisBlock(column, row, texture) // main brick for S-Block
    }

    fun createZ() {
        shape[3][2] = TetrisBlock(column, row - 1, texture)
        shape[2][2] = TetrisBlock(column - 1, row - 1, texture)
        shape[3][3] = TetrisBlock(column, row, texture) // main brick for Z-Block
        shape[4][3] = TetrisBlock(column + 1, row, texture)
    }
}