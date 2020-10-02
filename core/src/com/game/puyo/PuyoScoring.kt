package com.game.puyo

private const val TARGET_POINTS = 70.0

class PuyoScoring(){
    private val colorBonuses = createColorBonuses()
    private val chainBonuses = createChainBonuses()
    private val puyoBonuses = createPuyoBonuses()

    private var leftover = 0.0
    var score = 0
    var trash = 0

    private fun createColorBonuses() : HashMap<Int, Int>{
        return hashMapOf(1 to 0, 2 to 3, 3 to 6, 4 to 12)
    }

    private fun createChainBonuses() : HashMap<Int, Int>{
        return hashMapOf(1 to 0, 2 to 8, 3 to 16, 4 to 32, 5 to 64, 6 to 96, 7 to 128, 8 to 160, 9 to 192, 10 to 224, 11 to 256,
        12 to 288, 13 to 320, 14 to 352, 15 to 384, 16 to 416, 17 to 448, 18 to 480, 19 to 512, 20 to 544, 21 to 576, 22 to 608,
        23 to 640, 24 to 672)
    }

    private fun createPuyoBonuses() : HashMap<Int, Int>{
        return hashMapOf(4 to 0, 5 to 2, 6 to 3, 7 to 4, 8 to 5, 9 to 6, 10 to 7, 11 to 10)
    }

    private fun calculateTrash(chainScore : Int){
        val nuisancePoints : Double = chainScore / TARGET_POINTS + leftover
        trash = nuisancePoints.toInt()
        leftover = nuisancePoints - trash

        println("the chain score was $chainScore")
        println("so thats $nuisancePoints nuisancePoints")
        println("and $trash trash")
        println("with $leftover leftover")
    }

    private fun calculateBonus(chains: List<List<PuyoBlock>>) : Int{
        if(chains.isEmpty()){
            return 1
        }
        val colorBonus = colorBonuses[chains.flatten().distinctBy { it.color }.size]?: 0
        val chainPower = chainBonuses[if(chains.size <= 24) chains.size else 24]?: 0
        val puyoBonus = chains.fold(0){sum, chain -> sum + (puyoBonuses[chain.size]?: puyoBonuses[11]!!)}
        return if(colorBonus + chainPower + puyoBonus == 0) 1 else colorBonus + chainPower + puyoBonus
    }

    fun calculate(chains: List<List<PuyoBlock>>){
        val chainScore = 10 * chains.flatten().size * calculateBonus(chains) // (10 * PC) * (CP + CB + GB)
        score += chainScore
        calculateTrash(chainScore)
    }
}

