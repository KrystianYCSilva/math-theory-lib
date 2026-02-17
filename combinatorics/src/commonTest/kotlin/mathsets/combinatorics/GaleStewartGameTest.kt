package mathsets.combinatorics

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class GaleStewartGameTest : FunSpec({
    test("first player can force win in one-move game") {
        val game = GaleStewartGame(
            legalMoves = listOf(0, 1),
            horizon = 1
        ) { history -> history.single() == 1 }

        game.firstPlayerHasWinningStrategy() shouldBe true
        game.bestMoveFor(emptyList()) shouldBe 1
    }

    test("second player can force loss for first when payoff depends on last move") {
        val game = GaleStewartGame(
            legalMoves = listOf(0, 1),
            horizon = 2
        ) { history -> history.last() == 1 }

        game.firstPlayerHasWinningStrategy() shouldBe false
        game.secondPlayerHasWinningStrategy() shouldBe true
        game.minimaxOutcome() shouldBe -1
    }
})

