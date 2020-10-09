package com.game.puyo

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.game.Block
import com.game.Tetris.TetrisGame
import com.game.Timer

class Controller(private val timer: Timer) {

    val puyoGame = PuyoGame()
    var tapCount = 0

    private var time = System.currentTimeMillis()
    private var lastInput = Time(time, 50)
    private var lastChain = Time(time, puyoGame.puyo.chainSpeed)
    private var lastBlockDrop = Time(time, puyoGame.puyo.speed)
    private var lastGarbageDrop = Time(time, 70)
    private var lastPuyoDrop = Time(time, puyoGame.puyo.speed)
    private var delay = Time(time, 500)
    private var doubleTap = Time(time, 3000)

    fun mainLoop(){
        if(puyoGame.hasFoundChain()) {
            if (timer.hasPassed(lastChain)) {
                puyoGame.removeCombo()
                timer.reset(lastChain)
            }
        } else {
            if (timer.hasPassed(lastBlockDrop)) {
                puyoGame.dropRemainingPuyos()
                timer.reset(lastBlockDrop)
                if(!puyoGame.isDoneDroppingPuyos()){
                    timer.reset(delay)
                }
            } else if(puyoGame.isDoneDroppingPuyos()){
                puyoGame.findBigPuyoChain()
                if(!puyoGame.hasFoundChain()){
                    if(puyoGame.hasReceivedGarbage() || !puyoGame.isDoneDroppingGarbage()){
                        if(puyoGame.hasReceivedGarbage() && timer.hasPassed(delay)){
                            puyoGame.placeGarbage()
                            timer.reset(delay)
                        } else if (timer.hasPassed(lastGarbageDrop)){
                            puyoGame.dropRemainingGarbage()
                            timer.reset(lastGarbageDrop)
                        }
                    } else {
                        puyoGame.calculateChainScore()
                        if(timer.hasPassed(lastPuyoDrop)) {
                            if (puyoGame.allowSpawn()) {
                                puyoGame.spawnPuyo()
                            }
                            timer.reset(lastPuyoDrop)
                        }
                    }
                }
            }
            timer.reset(lastChain)
        }
        puyoGame.connectPuyos()
        puyoGame.updateSprites()
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
        lastBlockDrop.delay = puyoGame.puyo.speed
    }

    fun checkDoubleRotate(rotation: Int){ // only works for a bit?
        tapCount++
        if(!timer.hasPassed(doubleTap) && tapCount == 2 && puyoGame.canQuickTurn()){
            puyoGame.quickTurn()
            tapCount = 0
        } else {
            if(timer.hasPassed(doubleTap) && tapCount == 2){
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