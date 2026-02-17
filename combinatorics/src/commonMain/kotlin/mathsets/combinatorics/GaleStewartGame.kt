package mathsets.combinatorics

/**
 * Jogo de Gale-Stewart em horizonte finito com avaliação minimax booleana.
 *
 * `true` significa vitória do Jogador I.
 */
class GaleStewartGame<M>(
    private val legalMoves: List<M>,
    private val horizon: Int,
    private val payoffForFirstPlayer: (List<M>) -> Boolean
) {
    init {
        require(horizon >= 0) { "Horizon must be non-negative." }
        require(legalMoves.isNotEmpty() || horizon == 0) {
            "At least one legal move is required when horizon > 0."
        }
    }

    fun firstPlayerHasWinningStrategy(): Boolean = evaluate(emptyList(), firstPlayerTurn = true)

    fun secondPlayerHasWinningStrategy(): Boolean = !firstPlayerHasWinningStrategy()

    fun minimaxOutcome(): Int = if (firstPlayerHasWinningStrategy()) 1 else -1

    fun bestMoveFor(history: List<M>): M? {
        require(history.size < horizon) { "History already reached terminal length." }
        val firstPlayerTurn = history.size % 2 == 0
        return if (firstPlayerTurn) {
            legalMoves.firstOrNull { move ->
                evaluate(history + move, firstPlayerTurn = false)
            }
        } else {
            legalMoves.firstOrNull { move ->
                !evaluate(history + move, firstPlayerTurn = true)
            }
        }
    }

    private fun evaluate(history: List<M>, firstPlayerTurn: Boolean): Boolean {
        if (history.size == horizon) return payoffForFirstPlayer(history)
        return if (firstPlayerTurn) {
            legalMoves.any { move -> evaluate(history + move, firstPlayerTurn = false) }
        } else {
            legalMoves.all { move -> evaluate(history + move, firstPlayerTurn = true) }
        }
    }
}

