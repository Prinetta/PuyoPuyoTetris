package com.game.puyo

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.game.Block
import com.game.Sounds
import com.game.Tetris.TetrisGame

class Controller() {

    private var tapCount = 0
    val puyoGame = PuyoGame()

    private var lastInput = Time(80)
    private var lastChain = Time(puyoGame.puyo.chainSpeed)
    private var lastBlockDrop = Time(puyoGame.puyo.speed/2)
    private var lastGarbageDrop = Time(70)
    private var lastPuyoStep = Time(puyoGame.puyo.speed/2)
    private var delay = Time(500)
    private var doubleTap = Time(3000)
    var count = 0

    private var placedGarbage = 0
    private var playedDropSound = false

    fun mainLoop(){
        if(puyoGame.hasFoundChain()) {
            if (lastChain.hasPassed()) {
                puyoGame.removeCombo()
                lastChain.reset()
                puyoGame.allPuyosDropped = false
                playChainSound(puyoGame.puyosToRemove.size)
            }
        } else {
            if(puyoGame.puyo.rotateCount % 2 != 0){
                puyoGame.updateHorizontalPuyoState()
            }
            if(puyoGame.canDropMainPuyos()){
                if(lastPuyoStep.hasPassed()){
                    if(puyoGame.puyo.gap == 0f){
                        puyoGame.puyo.gap = 0.5f
                    } else {
                        puyoGame.puyo.gap = 0f
                        puyoGame.dropMainPuyos()
                    }
                    lastPuyoStep.reset()
                }
            } else {
                puyoGame.updatePuyoState()
                if (puyoGame.canDropPuyos()){
                    if(lastBlockDrop.hasPassed()) {
                        puyoGame.dropRemainingPuyos()
                        puyoGame.dropRemainingGarbage()
                        lastBlockDrop.reset()
                    }
                } else {
                    puyoGame.findBigPuyoChain()
                    if(!puyoGame.hasFoundChain()){
                        puyoGame.calculateChainScore()
                        if(puyoGame.hasReceivedGarbage() || !puyoGame.isDoneDroppingGarbage()){ // still need to test garbage placement more
                            if(puyoGame.hasReceivedGarbage() && delay.hasPassed()){
                                placedGarbage = puyoGame.scoring.garbageToReceive
                                puyoGame.placeGarbage()
                                delay.reset()
                            } else if (lastGarbageDrop.hasPassed()){
                                puyoGame.dropRemainingGarbage()
                                lastGarbageDrop.reset()
                            }
                        } else {
                            if(placedGarbage > 0){
                                if(placedGarbage >= 6){
                                    Sounds.garbage2.play()
                                } else {
                                    Sounds.garbage.play()
                                }
                                placedGarbage = 0
                            }
                            if (puyoGame.allowSpawn()) {
                                puyoGame.spawnPuyo()
                                playedDropSound = false
                            } else {
                                if(puyoGame.hasLost() && !puyoGame.gameOver){
                                    println("puyo lost")
                                    puyoGame.gameOver = true
                                    //Sounds.plost.play()
                                }
                            }
                        }
                    }
                }
            }
            lastChain.reset()
        }
        puyoGame.connectPuyos()
        //puyoGame.updateSprites() // might delete that one
        puyoGame.unmark()
    }

    private fun playChainSound(chainCount: Int){
        if(chainCount > 7){
            Sounds.chainSounds["pchain7"]?.play()
        } else {
            Sounds.chainSounds["pchain$chainCount"]?.play()
        }
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
            lastInput.reset()
        } else if(Gdx.input.isKeyPressed(Input.Keys.S)){
            increaseSpeed()
        } else {
            decreaseSpeed()
        }
        lastPuyoStep.delay = puyoGame.puyo.speed/2
    }

    fun checkDoubleRotate(rotation: Int){
        tapCount++
        if(!doubleTap.hasPassed() && tapCount > 1 && puyoGame.canQuickTurn()){
            puyoGame.quickTurn()
            tapCount = 0
        } else {
            if(doubleTap.hasPassed() && tapCount > 1){
                tapCount = 0
            }
            rotatePuyo(rotation)
        }

        doubleTap.reset()
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
        return lastInput.hasPassed() && !puyoGame.puyo.startedDrop()
    }

    private fun movePuyo(direction: Int){
        puyoGame.movePuyo(direction)
        Sounds.pmove.play()
    }

    private fun rotatePuyo(direction: Int){
        puyoGame.rotatePuyo(direction)
        Sounds.protate.play()
    }

    private fun increaseSpeed(){
        puyoGame.puyo.speed = puyoGame.puyo.maxSpeed
    }

    private fun decreaseSpeed(){
        puyoGame.puyo.speed = puyoGame.puyo.minSpeed
    }
}