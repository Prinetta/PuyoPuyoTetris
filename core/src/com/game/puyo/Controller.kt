package com.game.puyo

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.game.Block
import com.game.Tetris.TetrisGame
import com.game.Timer

class Controller(private val timer: Timer) {

    private var tapCount = 0
    val puyoGame = PuyoGame()

    private var time = System.currentTimeMillis()
    private var lastInput = Time(time, 80)
    private var lastChain = Time(time, puyoGame.puyo.chainSpeed)
    private var lastBlockDrop = Time(time, puyoGame.puyo.speed/2)
    private var lastGarbageDrop = Time(time, 70)
    private var lastPuyoStep = Time(time, puyoGame.puyo.speed/2)
    private var delay = Time(time, 500)
    private var doubleTap = Time(time, 3000)
    var count = 0

    fun mainLoop(){
        if(puyoGame.hasFoundChain()) {
            if (timer.hasPassed(lastChain)) {
                puyoGame.removeCombo()
                timer.reset(lastChain)
                puyoGame.allPuyosDropped = false
            }
        } else {
            if(puyoGame.puyo.rotateCount % 2 != 0){
                puyoGame.updateHorizontalPuyoState()
            }
            if(puyoGame.canDropMainPuyos()){
                if(timer.hasPassed(lastPuyoStep)){
                    if(puyoGame.puyo.gap == 0f){
                        puyoGame.puyo.gap = 0.5f
                    } else {
                        puyoGame.puyo.gap = 0f
                        puyoGame.dropMainPuyos()
                    }
                    timer.reset(lastPuyoStep)
                }
            } else {
                puyoGame.updatePuyoState()
                if (puyoGame.canDropPuyos()){
                    if(timer.hasPassed(lastBlockDrop)) {
                        puyoGame.dropRemainingPuyos()
                        timer.reset(lastBlockDrop)
                    }
                } else {
                    puyoGame.findBigPuyoChain()
                    if(!puyoGame.hasFoundChain()){
                        puyoGame.calculateChainScore()
                        if(puyoGame.hasReceivedGarbage() || !puyoGame.isDoneDroppingGarbage()){ // still need to test garbage placement more
                            if(puyoGame.hasReceivedGarbage() && timer.hasPassed(delay)){
                                puyoGame.placeGarbage()
                                timer.reset(delay)
                            } else if (timer.hasPassed(lastGarbageDrop)){
                                puyoGame.dropRemainingGarbage()
                                timer.reset(lastGarbageDrop)
                            }
                        } else {
                            if (puyoGame.allowSpawn()) {
                                puyoGame.spawnPuyo()
                            }
                        }
                    }
                }
            }
            timer.reset(lastChain)
        }
        puyoGame.connectPuyos()
        //puyoGame.updateSprites() // might delete that one
        puyoGame.unmark()
    }

    fun setTetris(tetris: TetrisGame){
        puyoGame.setTetris(tetris)
    }

    fun readInput(){
        if(allowInput()){
            when {
                Gdx.input.isKeyPressed(Input.Keys.A) -> movePuyo(-1)
                Gdx.input.isKeyPressed(Input.Keys.D) -> movePuyo(1)
                Gdx.input.isKeyPressed(Input.Keys.H) -> checkDoubleRotate(1)
                Gdx.input.isKeyPressed(Input.Keys.G) -> checkDoubleRotate(-1)
            }
            timer.reset(lastInput)
        } else if(Gdx.input.isKeyPressed(Input.Keys.S)){
            increaseSpeed()
        } else {
            decreaseSpeed()
        }
        lastPuyoStep.delay = puyoGame.puyo.speed/2
    }

    fun checkDoubleRotate(rotation: Int){
        tapCount++
        if(!timer.hasPassed(doubleTap) && tapCount > 1 && puyoGame.canQuickTurn()){
            puyoGame.quickTurn()
            tapCount = 0
        } else {
            if(timer.hasPassed(doubleTap) && tapCount > 1){
                tapCount = 0
            }
            rotatePuyo(rotation)
        }

        timer.reset(doubleTap)
    }

    fun displayPreview() : Boolean {
        return !puyoGame.puyo.bothDropped()
    }

    fun getPreviewCoords(): Array<Array<Int>> {
        val coords = arrayOf(puyoGame.getExpectedDrop(puyoGame.puyo.first), puyoGame.getExpectedDrop(puyoGame.puyo.second))
        if(coords[0][0] == coords[1][0] && coords[0][1] == coords[1][1]){
            if(puyoGame.puyo.first.y > puyoGame.puyo.second.y){
                coords[1][1]--
            } else {
                coords[0][1]--
            }
        }
        return coords
    }

    fun getCurrentPuyo() : Puyo{
        return puyoGame.puyo
    }

    fun getBlockAt(i: Int, j: Int): Block?{
        return puyoGame.grid[i][j]
    }

    fun getCurrentScore(): String {
        return puyoGame.scoring.score.toString()
    }

    fun getNextPuyo(index: Int): Puyo{
        return puyoGame.nextPuyos[index]
    }

    fun displayGarbage(): Boolean {
        return puyoGame.hasReceivedGarbage()
    }

    fun getGarbage(): Int {
        return puyoGame.scoring.garbageToReceive
    }

    private fun allowInput() : Boolean {
        return timer.hasPassed(lastInput) && !puyoGame.puyo.startedDrop()
    }

    private fun movePuyo(direction: Int){
        puyoGame.movePuyo(direction)
    }

    private fun rotatePuyo(direction: Int){
        puyoGame.rotatePuyo(direction)
    }

    private fun increaseSpeed(){
        puyoGame.puyo.speed = puyoGame.puyo.maxSpeed
    }

    private fun decreaseSpeed(){
        puyoGame.puyo.speed = puyoGame.puyo.minSpeed
    }
}