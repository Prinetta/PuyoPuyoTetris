package com.game.puyo

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.game.Block
import com.game.Garbage
import com.game.Timer

class Controller(private val timer: Timer) {

    private val puyoGame = PuyoGame() // and tetris

    fun mainLoop(){
        if(puyoGame.hasFoundChain()) {
            if (timer.hasChainTimePassed(puyoGame.puyo)) {
                puyoGame.removeCombo()
                timer.resetChainTime()
            }
        } else {
            if (timer.hasBlockDropTimePassed(puyoGame.puyo)) {
                puyoGame.dropRemainingBlocks()
                timer.resetBlockDropTime()
            } else if(puyoGame.isDoneDroppingBlocks()){
                puyoGame.findBigPuyoChain()
                if(!puyoGame.hasFoundChain()){
                    puyoGame.calculateChainScore()
                    if(puyoGame.hasReceivedGarbage()){
                        puyoGame.dropGarbage()
                    } else if(timer.hasPuyoDropTimePassed(puyoGame.puyo)){
                        if(puyoGame.allowSpawn()){
                            puyoGame.spawnPuyo()
                        }
                        timer.resetPuyoDropTime()
                    }
                }
            }
            timer.resetChainTime()
        }
        puyoGame.connectPuyos()
        puyoGame.unmark()
    }

    fun readInput(){
        if(allowInput()){
            when {
                Gdx.input.isKeyPressed(Input.Keys.LEFT) -> movePuyo(-1)
                Gdx.input.isKeyPressed(Input.Keys.RIGHT) -> movePuyo(1)
                Gdx.input.isKeyPressed(Input.Keys.E) -> rotatePuyo(1)
                Gdx.input.isKeyPressed(Input.Keys.Q) -> rotatePuyo(-1)
            }
            timer.resetInputTime()
        } else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            increaseSpeed()
        } else {
            decreaseSpeed()
        }
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

    private fun allowInput() : Boolean {
        return timer.hasInputTimePassed() && !puyoGame.puyo.startedDrop()
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