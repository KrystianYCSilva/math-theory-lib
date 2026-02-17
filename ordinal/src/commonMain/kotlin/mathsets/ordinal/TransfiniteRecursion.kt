package mathsets.ordinal

import mathsets.kernel.NaturalNumber

/**
 * Provides transfinite recursion over ordinals.
 *
 * Transfinite recursion generalizes ordinary recursion to ordinal-indexed sequences.
 * A value is computed for each ordinal by handling three cases:
 * - **Zero:** Returns the base value.
 * - **Successor:** Computes the value from the predecessor's result.
 * - **Limit:** Computes the value from a lazy sequence of approximations at all
 *   finite ordinals below the limit.
 */
object TransfiniteRecursion {
    /**
     * Performs transfinite recursion up to the given [ordinal].
     *
     * @param T The type of values computed at each ordinal stage.
     * @param ordinal The target ordinal to recurse up to.
     * @param base The value at ordinal zero.
     * @param successorCase A function that computes the value at a successor ordinal,
     *        given the predecessor ordinal and its computed value.
     * @param limitCase A function that computes the value at a limit ordinal,
     *        given the limit ordinal and a lazy [Sequence] of values at all
     *        finite ordinals below it.
     * @return The computed value at the given [ordinal].
     */
    fun <T> transfiniteRecursion(
        ordinal: Ordinal,
        base: T,
        successorCase: (predecessor: Ordinal, previousValue: T) -> T,
        limitCase: (limit: Ordinal, approximants: Sequence<T>) -> T
    ): T {
        if (ordinal.isZero()) return base

        val predecessor = ordinal.predOrNull()
        if (predecessor != null) {
            val previous = transfiniteRecursion(predecessor, base, successorCase, limitCase)
            return successorCase(predecessor, previous)
        }

        val approximants = sequence {
            var n = NaturalNumber.ZERO
            while (true) {
                val finite = Ordinal.finite(n)
                if (finite >= ordinal) break
                yield(transfiniteRecursion(finite, base, successorCase, limitCase))
                n = n.succ()
            }
        }

        return limitCase(ordinal, approximants)
    }
}
