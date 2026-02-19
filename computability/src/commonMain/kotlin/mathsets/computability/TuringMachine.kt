package mathsets.computability

/**
 * Tape head movement direction.
 */
enum class MoveDirection {
    /** Move head to the left. */
    LEFT,

    /** Move head to the right. */
    RIGHT,

    /** Keep head in place. */
    STAY
}

/**
 * Transition instruction for a Turing machine.
 *
 * @param S State type.
 * @param A Alphabet symbol type.
 * @property nextState Next machine state.
 * @property writeSymbol Symbol to write on current tape cell.
 * @property moveDirection Head movement direction.
 */
data class Transition<S, A>(
    val nextState: S,
    val writeSymbol: A,
    val moveDirection: MoveDirection
)

/**
 * Deterministic single-tape Turing machine.
 *
 * @param S State type.
 * @param A Alphabet symbol type.
 * @property states Finite state set.
 * @property alphabet Tape alphabet.
 * @property blank Blank symbol.
 * @property initialState Initial state.
 * @property acceptingStates Accepting halting states.
 * @property rejectingStates Rejecting halting states.
 * @property transition Transition function delta(state, symbol).
 */
data class TuringMachine<S, A>(
    val states: Set<S>,
    val alphabet: Set<A>,
    val blank: A,
    val initialState: S,
    val acceptingStates: Set<S>,
    val rejectingStates: Set<S>,
    val transition: (S, A) -> Transition<S, A>?
)

/**
 * Current machine configuration.
 *
 * @param S State type.
 * @param A Alphabet symbol type.
 * @property state Current state.
 * @property headPosition Current head position on tape.
 * @property tape Sparse tape representation.
 */
data class Configuration<S, A>(
    val state: S,
    val headPosition: Int,
    val tape: Map<Int, A>
) {
    /**
     * Reads current tape symbol.
     *
     * @param blank Blank symbol used for missing cells.
     * @return Current symbol under the head.
     */
    fun read(blank: A): A = tape[headPosition] ?: blank
}

/**
 * Result of a bounded Turing simulation.
 *
 * @param S State type.
 * @param A Alphabet symbol type.
 * @property finalConfiguration Last reached configuration.
 * @property steps Number of performed transitions.
 * @property halted True when machine reached an accepting/rejecting state.
 * @property accepted True when machine halted in an accepting state.
 */
data class SimulationResult<S, A>(
    val finalConfiguration: Configuration<S, A>,
    val steps: Int,
    val halted: Boolean,
    val accepted: Boolean
)

/**
 * Bounded simulator for deterministic Turing machines.
 */
object TuringSimulator {
    /**
     * Runs a Turing machine for at most [maxSteps] transitions.
     *
     * @param S State type.
     * @param A Alphabet symbol type.
     * @param machine Machine definition.
     * @param input Initial tape word (positions starting at 0).
     * @param maxSteps Transition budget.
     * @return Simulation result.
     */
    fun <S, A> run(
        machine: TuringMachine<S, A>,
        input: List<A>,
        maxSteps: Int
    ): SimulationResult<S, A> {
        var config = Configuration(
            state = machine.initialState,
            headPosition = 0,
            tape = input.withIndex().associate { (index, value) -> index to value }
        )

        var steps = 0
        while (steps < maxSteps) {
            if (config.state in machine.acceptingStates) {
                return SimulationResult(config, steps, halted = true, accepted = true)
            }
            if (config.state in machine.rejectingStates) {
                return SimulationResult(config, steps, halted = true, accepted = false)
            }

            val currentSymbol = config.read(machine.blank)
            val transition = machine.transition(config.state, currentSymbol)
                ?: return SimulationResult(config, steps, halted = false, accepted = false)

            val newTape = config.tape.toMutableMap()
            newTape[config.headPosition] = transition.writeSymbol
            val newHead = when (transition.moveDirection) {
                MoveDirection.LEFT -> config.headPosition - 1
                MoveDirection.RIGHT -> config.headPosition + 1
                MoveDirection.STAY -> config.headPosition
            }

            config = Configuration(transition.nextState, newHead, newTape)
            steps++
        }

        val halted = config.state in machine.acceptingStates || config.state in machine.rejectingStates
        val accepted = config.state in machine.acceptingStates
        return SimulationResult(config, steps, halted, accepted)
    }
}
