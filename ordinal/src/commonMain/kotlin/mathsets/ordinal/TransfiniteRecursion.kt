package mathsets.ordinal

import mathsets.kernel.NaturalNumber

/**
 * Recursão transfinitária em ordinais.
 *
 * Para ordinais limite, a função `limitCase` recebe aproximações ao longo de
 * ordinais finitos (< limite).
 */
object TransfiniteRecursion {
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

