package mathsets.computability

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ComputabilityTest {

    @Test
    fun turingMachineUnaryIncrementAcceptsAndWritesOne() {
        val machine = TuringMachine(
            states = setOf("q0", "qa"),
            alphabet = setOf(0, 1),
            blank = 0,
            initialState = "q0",
            acceptingStates = setOf("qa"),
            rejectingStates = emptySet(),
            transition = { state, symbol ->
                when {
                    state == "q0" && symbol == 1 -> Transition("q0", 1, MoveDirection.RIGHT)
                    state == "q0" && symbol == 0 -> Transition("qa", 1, MoveDirection.STAY)
                    else -> null
                }
            }
        )

        val result = TuringSimulator.run(machine, input = listOf(1, 1), maxSteps = 20)

        assertTrue(result.halted)
        assertTrue(result.accepted)
        assertEquals(1, result.finalConfiguration.tape[2])
    }

    @Test
    fun lambdaNormalReductionWorks() {
        val id = LambdaTerm.Abs("x", LambdaTerm.Var("x"))
        val arg = LambdaTerm.Abs("y", LambdaTerm.Var("y"))
        val app = LambdaTerm.App(id, arg)

        val reduced = LambdaCalculus.normalOrder(app)
        assertEquals(arg, reduced)
    }

    @Test
    fun churchNumeralRoundTrip() {
        val three = LambdaCalculus.churchNumeral(3)
        assertEquals(3, LambdaCalculus.toInt(three))
    }

    @Test
    fun boundedSimulationCanRemainNonHalting() {
        val machine = TuringMachine(
            states = setOf("loop"),
            alphabet = setOf(0),
            blank = 0,
            initialState = "loop",
            acceptingStates = emptySet(),
            rejectingStates = emptySet(),
            transition = { _, _ -> Transition("loop", 0, MoveDirection.RIGHT) }
        )

        val result = TuringSimulator.run(machine, input = emptyList(), maxSteps = 10)
        assertFalse(result.halted)
        assertEquals(10, result.steps)
    }
}
