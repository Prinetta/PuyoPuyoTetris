package drop

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.game.TConstants
import com.puyo.TetrisGame
import com.puyo.Tetromino

class TetrisScreen(val game: PuyoPuyoTetris) : Screen {

    //val HEIGHT: Float = Gdx.graphics.height.toFloat()
    //val WIDTH: Float = Gdx.graphics.width.toFloat()


    var tetrisGame: TetrisGame = TetrisGame()

    override fun render(delta: Float) {
        game.batch.begin()
        game.font.draw(game.batch, "NEXT", TConstants.NEXT_BLOCK_FIELD_X + (TConstants.CELL_SIZE * 0.2f), TConstants.GRID_TOP_Y - (TConstants.CELL_SIZE * 0.2f))

        for (i in 0 until tetrisGame.cells.size) { // invisible rows are nice
            for (j in 1 until tetrisGame.cells[i].size) {
                if (tetrisGame.cells[i][j] != null) {
                    game.batch.draw(tetrisGame.cells[i][j].texture, i.toFloat() * TConstants.CELL_SIZE + TConstants.GRID_LEFT_X, TConstants.GRID_TOP_Y - (j.toFloat() * TConstants.CELL_SIZE),
                            TConstants.CELL_SIZE.toFloat(), TConstants.CELL_SIZE.toFloat())
                }
            }
        }

        var nextBlock: Tetromino = tetrisGame.nextTetrominos.peek()
        for (i in nextBlock.shape.indices) {
            for (j in 0 until nextBlock.shape[i].size) {
                if (nextBlock.shape[i][j] != null) {
                    game.batch.draw(nextBlock.shape[i][j].texture,
                            TConstants.NEXT_BLOCK_FIELD_X + ((TConstants.CELL_SIZE * 4.5f - (nextBlock.width * 0.9f)) / 2) + ((i - nextBlock.firstColumn()) * TConstants.CELL_SIZE * 0.9f),
                            TConstants.GRID_TOP_Y - (TConstants.CELL_SIZE * 0.9f) - ((TConstants.CELL_SIZE * 4f - (nextBlock.height * 0.9f)) / 2) - ((j - nextBlock.firstRow()) * TConstants.CELL_SIZE * 0.9f),
                            TConstants.CELL_SIZE * 0.9f, TConstants.CELL_SIZE * 0.9f)
                }
            }
        }

        for (field in 0..3) {
            nextBlock = tetrisGame.nextTetrominos[3 - field]
            for (i in nextBlock.shape.indices) {
                for (j in 0 until nextBlock.shape[i].size) {
                    if (nextBlock.shape[i][j] != null) {
                        game.batch.draw(nextBlock.shape[i][j].texture,
                                TConstants.NEXT_BLOCK_FIELD_X + ((TConstants.CELL_SIZE * 3.5f - (nextBlock.width * 0.7f)) / 2) + ((i - nextBlock.firstColumn()) * TConstants.CELL_SIZE * 0.7f),
                                TConstants.NEXT_BLOCK_FIELD2_TOP_Y - (((field * 3) + 0.7f) * TConstants.CELL_SIZE) - ((TConstants.CELL_SIZE * 2.5f - (nextBlock.height * 0.7f)) / 2) - ((j - nextBlock.firstRow()) * TConstants.CELL_SIZE * 0.7f),
                                TConstants.CELL_SIZE * 0.7f, TConstants.CELL_SIZE * 0.7f)
                    }
                }
            }
        }

        game.batch.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setAutoShapeType(true)
        Gdx.gl.glEnable(GL20.GL_BLEND) // allows changing opacity I guess
        shapeRenderer.color = Color.LIGHT_GRAY

        // main next block field
        shapeRenderer.rectLine(TConstants.NEXT_BLOCK_FIELD_X, TConstants.NEXT_BLOCK_FIELD_Y + (TConstants.CELL_SIZE * 4f),
                TConstants.NEXT_BLOCK_FIELD_X + (TConstants.CELL_SIZE * 4.5f), TConstants.NEXT_BLOCK_FIELD_Y + (TConstants.CELL_SIZE * 4f), 1f)
        shapeRenderer.rectLine(TConstants.NEXT_BLOCK_FIELD_X, TConstants.NEXT_BLOCK_FIELD_Y, TConstants.NEXT_BLOCK_FIELD_X + (TConstants.CELL_SIZE * 4.5f), TConstants.NEXT_BLOCK_FIELD_Y, 1f)
        shapeRenderer.rectLine(TConstants.NEXT_BLOCK_FIELD_X, TConstants.NEXT_BLOCK_FIELD_Y, TConstants.NEXT_BLOCK_FIELD_X, TConstants.NEXT_BLOCK_FIELD_Y + (TConstants.CELL_SIZE * 4f), 1f)
        shapeRenderer.rectLine(TConstants.NEXT_BLOCK_FIELD_X + (TConstants.CELL_SIZE * 4.5f), TConstants.NEXT_BLOCK_FIELD_Y,
                TConstants.NEXT_BLOCK_FIELD_X + (TConstants.CELL_SIZE * 4.5f), TConstants.NEXT_BLOCK_FIELD_Y + (TConstants.CELL_SIZE * 4f), 1f)

        // secondary next blocks field
        for (i in 0..9 step 3) {
            shapeRenderer.rectLine(TConstants.NEXT_BLOCK_FIELD_X, TConstants.NEXT_BLOCK_FIELD2_Y - (i * TConstants.CELL_SIZE),
                    TConstants.NEXT_BLOCK_FIELD_X + (TConstants.CELL_SIZE * 3.5f), TConstants.NEXT_BLOCK_FIELD2_Y - (i * TConstants.CELL_SIZE), 1f)
            shapeRenderer.rectLine(TConstants.NEXT_BLOCK_FIELD_X, TConstants.NEXT_BLOCK_FIELD2_TOP_Y - (i * TConstants.CELL_SIZE),
                    TConstants.NEXT_BLOCK_FIELD_X + (TConstants.CELL_SIZE * 3.5f), TConstants.NEXT_BLOCK_FIELD2_TOP_Y - (i * TConstants.CELL_SIZE), 1f)
            shapeRenderer.rectLine(TConstants.NEXT_BLOCK_FIELD_X, TConstants.NEXT_BLOCK_FIELD2_TOP_Y - (i * TConstants.CELL_SIZE),
                    TConstants.NEXT_BLOCK_FIELD_X, TConstants.NEXT_BLOCK_FIELD2_Y - (i * TConstants.CELL_SIZE), 1f)
            shapeRenderer.rectLine(TConstants.NEXT_BLOCK_FIELD_X + (TConstants.CELL_SIZE * 3.5f), TConstants.NEXT_BLOCK_FIELD2_TOP_Y - (i * TConstants.CELL_SIZE),
                    TConstants.NEXT_BLOCK_FIELD_X + (TConstants.CELL_SIZE * 3.5f), TConstants.NEXT_BLOCK_FIELD2_Y - (i * TConstants.CELL_SIZE), 1f)
        }

        shapeRenderer.color = Color(Color.rgba8888(0.71f, 0.71f, 0.71f, 0.2f))

        for (i in 0..tetrisGame.rows) { // invisible rows are nice
            shapeRenderer.rectLine(TConstants.GRID_LEFT_X, TConstants.GRID_TOP_Y - (i * TConstants.CELL_SIZE), TConstants.GRID_RIGHT_X, TConstants.GRID_TOP_Y - (i * TConstants.CELL_SIZE), 1f)
        }

        for (j in 0..tetrisGame.columns) {
            shapeRenderer.rectLine(TConstants.GRID_LEFT_X + (j * TConstants.CELL_SIZE), TConstants.GRID_TOP_Y, TConstants.GRID_LEFT_X + (j * TConstants.CELL_SIZE), TConstants.GRID_TOP_Y - ((tetrisGame.rows - 1) * TConstants.CELL_SIZE), 1f)
        }

        shapeRenderer.end()

        tetrisGame.handleInputs(delta)

        camera.update()
    }

    // the following overrides are no-ops, unused in tutorial, but needed in
    //    order to compile a class that implements Screen
    override fun resize(width: Int, height: Int) { }
    override fun hide() { }
    override fun pause() { }
    override fun resume() { }

    override fun show() {

    }

    override fun dispose() {

    }

}