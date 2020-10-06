package drop

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.utils.Array
import com.puyo.TetrisControl
import com.puyo.Tetromino
import kotlin.random.Random

class TetrisScreen(val game: PuyoPuyoTetris) : Screen {

    val CELL_SIZE: Int = 30
    val HEIGHT: Float = Gdx.graphics.height.toFloat()
    val WIDTH: Float = Gdx.graphics.width.toFloat()
    val ROWS: Int = 25
    val COLUMNS: Int = 10
    val GRID_LEFT_X: Float = (WIDTH - (CELL_SIZE * COLUMNS)) / 2
    val GRID_TOP_Y: Float = HEIGHT - ((HEIGHT - (CELL_SIZE * (ROWS - 1))) / 2)
    val GRID_RIGHT_X: Float = GRID_LEFT_X + CELL_SIZE * COLUMNS
    val NEXT_BLOCK_FIELD_X: Float = GRID_RIGHT_X + (CELL_SIZE * 0.7f) // next block field
    val NEXT_BLOCK_FIELD_Y: Float = GRID_TOP_Y - (CELL_SIZE * 4)
    val NEXT_BLOCK_FIELD2_Y: Float = NEXT_BLOCK_FIELD_Y - (CELL_SIZE * 3) // 2nd next block field
    val NEXT_BLOCK_FIELD2_TOP_Y: Float = NEXT_BLOCK_FIELD2_Y + (2.5f * CELL_SIZE)


    var control: TetrisControl = TetrisControl()

    private var camera: OrthographicCamera
    var shapeRenderer: ShapeRenderer

    init {
        camera = OrthographicCamera()
        camera.setToOrtho(false, HEIGHT, WIDTH)
        game.batch.projectionMatrix = camera.combined
        shapeRenderer = ShapeRenderer()
        shapeRenderer.projectionMatrix = camera.combined
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.begin()
        game.font.draw(game.batch, "NEXT", NEXT_BLOCK_FIELD_X + (CELL_SIZE * 0.2f), GRID_TOP_Y - (CELL_SIZE * 0.2f))

        for (i in 0 until control.cells.size) { // invisible rows are nice
            for (j in 1 until control.cells[i].size) {
                if (control.cells[i][j] != null) {
                    game.batch.draw(control.cells[i][j].texture, i.toFloat() * CELL_SIZE + GRID_LEFT_X, GRID_TOP_Y - (j.toFloat() * CELL_SIZE),
                            CELL_SIZE.toFloat(), CELL_SIZE.toFloat())
                }
            }
        }

        var nextBlock: Tetromino = control.nextTetrominos.peek()
        for (i in nextBlock.shape.indices) {
            for (j in 0 until nextBlock.shape[i].size) {
                if (nextBlock.shape[i][j] != null) {
                    game.batch.draw(nextBlock.shape[i][j].texture,
                            NEXT_BLOCK_FIELD_X + ((CELL_SIZE * 4.5f - (nextBlock.width * 0.9f)) / 2) + ((i - nextBlock.firstColumn()) * CELL_SIZE * 0.9f),
                            GRID_TOP_Y - (CELL_SIZE * 0.9f) - ((CELL_SIZE * 4f - (nextBlock.height * 0.9f)) / 2) - ((j - nextBlock.firstRow()) * CELL_SIZE * 0.9f),
                            CELL_SIZE * 0.9f, CELL_SIZE * 0.9f)
                }
            }
        }

        for (field in 0..3) {
            nextBlock = control.nextTetrominos[3 - field]
            for (i in nextBlock.shape.indices) {
                for (j in 0 until nextBlock.shape[i].size) {
                    if (nextBlock.shape[i][j] != null) {
                        game.batch.draw(nextBlock.shape[i][j].texture,
                                NEXT_BLOCK_FIELD_X + ((CELL_SIZE * 3.5f - (nextBlock.width * 0.7f)) / 2) + ((i - nextBlock.firstColumn()) * CELL_SIZE * 0.7f),
                                NEXT_BLOCK_FIELD2_TOP_Y - (((field * 3) + 0.7f) * CELL_SIZE) - ((CELL_SIZE * 2.5f - (nextBlock.height * 0.7f)) / 2) - ((j - nextBlock.firstRow()) * CELL_SIZE * 0.7f),
                                CELL_SIZE * 0.7f, CELL_SIZE * 0.7f)
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
        shapeRenderer.rectLine(NEXT_BLOCK_FIELD_X, NEXT_BLOCK_FIELD_Y + (CELL_SIZE * 4f),
                NEXT_BLOCK_FIELD_X + (CELL_SIZE * 4.5f), NEXT_BLOCK_FIELD_Y + (CELL_SIZE * 4f), 1f)
        shapeRenderer.rectLine(NEXT_BLOCK_FIELD_X, NEXT_BLOCK_FIELD_Y, NEXT_BLOCK_FIELD_X + (CELL_SIZE * 4.5f), NEXT_BLOCK_FIELD_Y, 1f)
        shapeRenderer.rectLine(NEXT_BLOCK_FIELD_X, NEXT_BLOCK_FIELD_Y, NEXT_BLOCK_FIELD_X, NEXT_BLOCK_FIELD_Y + (CELL_SIZE * 4f), 1f)
        shapeRenderer.rectLine(NEXT_BLOCK_FIELD_X + (CELL_SIZE * 4.5f), NEXT_BLOCK_FIELD_Y,
                NEXT_BLOCK_FIELD_X + (CELL_SIZE * 4.5f), NEXT_BLOCK_FIELD_Y + (CELL_SIZE * 4f), 1f)

        // secondary next blocks field
        for (i in 0..9 step 3) {
            shapeRenderer.rectLine(NEXT_BLOCK_FIELD_X, NEXT_BLOCK_FIELD2_Y - (i * CELL_SIZE),
                    NEXT_BLOCK_FIELD_X + (CELL_SIZE * 3.5f), NEXT_BLOCK_FIELD2_Y - (i * CELL_SIZE), 1f)
            shapeRenderer.rectLine(NEXT_BLOCK_FIELD_X, NEXT_BLOCK_FIELD2_TOP_Y - (i * CELL_SIZE),
                    NEXT_BLOCK_FIELD_X + (CELL_SIZE * 3.5f), NEXT_BLOCK_FIELD2_TOP_Y - (i * CELL_SIZE), 1f)
            shapeRenderer.rectLine(NEXT_BLOCK_FIELD_X, NEXT_BLOCK_FIELD2_TOP_Y - (i * CELL_SIZE),
                    NEXT_BLOCK_FIELD_X, NEXT_BLOCK_FIELD2_Y - (i * CELL_SIZE), 1f)
            shapeRenderer.rectLine(NEXT_BLOCK_FIELD_X + (CELL_SIZE * 3.5f), NEXT_BLOCK_FIELD2_TOP_Y - (i * CELL_SIZE),
                    NEXT_BLOCK_FIELD_X + (CELL_SIZE * 3.5f), NEXT_BLOCK_FIELD2_Y - (i * CELL_SIZE), 1f)
        }


        /*var nextBlock: com.puyo.TetrisBlock = nextBlocks.peek()
        for (i in nextBlock.shape.indices) {
            for (j in 0 until nextBlock.shape[i].size) {
                if (nextBlock.shape[i][j] != null) {
                    shapeRenderer.rect(NEXT_BLOCK_FIELD_X + ((CELL_SIZE * 4.5f - (nextBlock.width * 0.9f)) / 2) + ((j - nextBlock.firstColumn()) * CELL_SIZE * 0.9f),
                            GRID_TOP_Y - (CELL_SIZE * 0.9f) - ((CELL_SIZE * 4f - (nextBlock.height * 0.9f)) / 2) - ((i - nextBlock.firstRow()) * CELL_SIZE * 0.9f),
                            CELL_SIZE * 0.9f, CELL_SIZE * 0.9f)
                }
            }
        }

        for (field in 0..3) {
            nextBlock = nextBlocks[3 - field]
            for (i in nextBlock.shape.indices) {
                for (j in 0 until nextBlock.shape[i].size) {
                    if (nextBlock.shape[i][j] != null) {
                        shapeRenderer.rect(NEXT_BLOCK_FIELD_X + ((CELL_SIZE * 3.5f - (nextBlock.width * 0.7f)) / 2) + ((j - nextBlock.firstColumn()) * CELL_SIZE * 0.7f),
                                NEXT_BLOCK_FIELD2_TOP_Y - (((field * 3) + 0.7f) * CELL_SIZE) - ((CELL_SIZE * 2.5f - (nextBlock.height * 0.7f)) / 2) - ((i - nextBlock.firstRow()) * CELL_SIZE * 0.7f),
                                CELL_SIZE * 0.7f, CELL_SIZE * 0.7f)
                    }
                }
            }
        }*/


        shapeRenderer.color = Color(Color.rgba8888(0.71f, 0.71f, 0.71f, 0.2f))

        for (i in 0..control.rows) { // invisible rows are nice
            shapeRenderer.rectLine(GRID_LEFT_X, GRID_TOP_Y - (i * CELL_SIZE), GRID_RIGHT_X, GRID_TOP_Y - (i * CELL_SIZE), 1f)
        }

        for (j in 0..control.columns) {
            shapeRenderer.rectLine(GRID_LEFT_X + (j * CELL_SIZE), GRID_TOP_Y, GRID_LEFT_X + (j * CELL_SIZE), GRID_TOP_Y - ((control.rows - 1) * CELL_SIZE), 1f)
        }

        shapeRenderer.end()

        control.handleInputs(delta)

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