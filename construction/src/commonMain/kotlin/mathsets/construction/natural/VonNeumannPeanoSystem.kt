package mathsets.construction.natural

import mathsets.logic.PeanoSystem

/**
 * Implementation of the [PeanoSystem] axioms over [VonNeumannNatural].
 *
 * Provides the canonical zero element, successor/predecessor operations,
 * injectivity and non-successor-of-zero verification, and primitive recursion
 * for the Von Neumann encoding of natural numbers.
 *
 * @see VonNeumannNatural
 * @see PeanoSystem
 */
object VonNeumannPeanoSystem : PeanoSystem<VonNeumannNatural> {
    /** The distinguished zero element: `0 = {}`. */
    override val zero: VonNeumannNatural = VonNeumannNatural.ZERO

    /**
     * Returns the successor of [n]: `S(n) = n U {n}`.
     *
     * @param n the natural number to take the successor of.
     * @return the successor `S(n)`.
     */
    override fun succ(n: VonNeumannNatural): VonNeumannNatural = n.succ()

    /**
     * Returns the predecessor of [n], or `null` if [n] is zero.
     *
     * @param n the natural number to take the predecessor of.
     * @return the predecessor, or `null` for zero.
     */
    override fun pred(n: VonNeumannNatural): VonNeumannNatural? = n.predOrNull()

    /**
     * Checks whether [n] is the zero element.
     *
     * @param n the natural number to test.
     * @return `true` if [n] is zero.
     */
    override fun isZero(n: VonNeumannNatural): Boolean = n == VonNeumannNatural.ZERO

    /**
     * Empirically verifies the injectivity of the successor function (Peano Axiom 3)
     * over a finite sample: `S(a) = S(b) => a = b`.
     *
     * @param sampleSize the number of naturals to check (0 until sampleSize).
     * @return `true` if injectivity holds for all tested pairs.
     */
    override fun verifyInjectivity(sampleSize: Int): Boolean {
        for (a in 0 until sampleSize) {
            for (b in 0 until sampleSize) {
                val na = VonNeumannNatural.of(a)
                val nb = VonNeumannNatural.of(b)
                if (succ(na) == succ(nb) && na != nb) return false
            }
        }
        return true
    }

    /**
     * Empirically verifies that zero is not the successor of any natural (Peano Axiom 2)
     * over a finite sample.
     *
     * @param sampleSize the number of naturals to check (0 until sampleSize).
     * @return `true` if the axiom holds for all tested values.
     */
    override fun verifyZeroNotSuccessor(sampleSize: Int): Boolean {
        for (n in 0 until sampleSize) {
            if (succ(VonNeumannNatural.of(n)) == zero) return false
        }
        return true
    }

    /**
     * Implements primitive recursion on Von Neumann naturals.
     *
     * Given a base value and a step function, returns a function that computes
     * the result of iterating the step from zero up to the given natural.
     *
     * @param R the result type.
     * @param base the value at zero.
     * @param step the step function `(current, accumulator) -> next accumulator`.
     * @return a function from [VonNeumannNatural] to [R] defined by primitive recursion.
     */
    override fun <R> recursion(base: R, step: (VonNeumannNatural, R) -> R): (VonNeumannNatural) -> R {
        return { n ->
            var acc = base
            var current: VonNeumannNatural = zero
            while (current != n) {
                acc = step(current, acc)
                current = succ(current)
            }
            acc
        }
    }
}
