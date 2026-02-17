package mathsets.combinatorics

/**
 * Models a finite-horizon Gale-Stewart game with boolean payoff and minimax evaluation.
 *
 * In a Gale-Stewart game, two players alternate moves from a shared set of legal moves
 * for a fixed number of rounds (the horizon). At the end, a payoff function determines
 * whether Player I wins (`true`) or Player II wins (`false`).
 *
 * The game is solved using minimax: Player I maximizes (seeks `true`) and Player II
 * minimizes (seeks `false`).
 *
 * @param M The type of moves.
 * @property legalMoves The list of moves available to both players at each turn.
 * @property horizon The total number of moves in a game (must be non-negative).
 * @property payoffForFirstPlayer A function that evaluates the complete move history and
 *           returns `true` if Player I wins.
 * @throws IllegalArgumentException if [horizon] is negative or [legalMoves] is empty
 *         when horizon > 0.
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

    /**
     * Determines whether Player I has a winning strategy using minimax evaluation.
     *
     * @return `true` if Player I can guarantee a win regardless of Player II's moves.
     */
    fun firstPlayerHasWinningStrategy(): Boolean = evaluate(emptyList(), firstPlayerTurn = true)

    /**
     * Determines whether Player II has a winning strategy.
     *
     * By the determinacy of finite games, exactly one player has a winning strategy.
     *
     * @return `true` if Player II can guarantee a win.
     */
    fun secondPlayerHasWinningStrategy(): Boolean = !firstPlayerHasWinningStrategy()

    /**
     * Returns the minimax outcome of the game.
     *
     * @return `1` if Player I wins with optimal play, `-1` if Player II wins.
     */
    fun minimaxOutcome(): Int = if (firstPlayerHasWinningStrategy()) 1 else -1

    /**
     * Finds the best move for the current player given the game history so far.
     *
     * @param history The list of moves played so far (must be shorter than [horizon]).
     * @return The optimal move for the current player, or `null` if no winning move exists.
     * @throws IllegalArgumentException if [history] has already reached the horizon.
     */
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
