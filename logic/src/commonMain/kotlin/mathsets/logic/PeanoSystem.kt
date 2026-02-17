package mathsets.logic

import mathsets.kernel.NaturalNumber

/**
 * An abstract interface encoding the **Peano axioms** for a type [N] representing
 * natural numbers.
 *
 * Implementations must provide:
 * - A distinguished [zero] element.
 * - A [succ] (successor) function.
 * - A partial [pred] (predecessor) function.
 * - An [isZero] predicate.
 * - Sampling-based verification of **injectivity** and the **zero-is-not-a-successor**
 *   property.
 * - A [recursion] combinator implementing primitive recursion over [N].
 *
 * @param N The type representing natural numbers in this system.
 * @see NaturalPeanoSystem
 */
interface PeanoSystem<N> {
    /**
     * The zero element — the base natural number.
     */
    val zero: N

    /**
     * Returns the successor of the given natural number.
     *
     * @param n The natural number whose successor is requested.
     * @return The successor `n + 1`.
     */
    fun succ(n: N): N

    /**
     * Returns the predecessor of the given natural number, or `null` if [n] is zero.
     *
     * @param n The natural number whose predecessor is requested.
     * @return The predecessor `n - 1`, or `null` when `n` is zero.
     */
    fun pred(n: N): N?

    /**
     * Tests whether the given natural number is zero.
     *
     * @param n The natural number to test.
     * @return `true` if [n] is the zero element, `false` otherwise.
     */
    fun isZero(n: N): Boolean

    /**
     * Verifies the **injectivity** of [succ] by sampling: for all pairs `(a, b)`
     * in `0 ..< sampleSize`, checks that `succ(a) == succ(b)` implies `a == b`.
     *
     * @param sampleSize The number of natural numbers to sample (default 1000).
     * @return `true` if no injectivity violation is found within the sample.
     */
    fun verifyInjectivity(sampleSize: Int = 1000): Boolean

    /**
     * Verifies that **zero is not the successor** of any sampled natural number:
     * for all `n` in `0 ..< sampleSize`, checks that `succ(n) ≠ zero`.
     *
     * @param sampleSize The number of natural numbers to sample (default 1000).
     * @return `true` if no violation is found within the sample.
     */
    fun verifyZeroNotSuccessor(sampleSize: Int = 1000): Boolean

    /**
     * Constructs a function defined by **primitive recursion** over [N].
     *
     * Given a base value and a step function, returns a function `f` such that:
     * - `f(zero) = base`
     * - `f(succ(n)) = step(n, f(n))`
     *
     * @param R The result type of the recursive function.
     * @param base The value at zero.
     * @param step A function `(n, accumulator) -> next` applied at each successor step.
     * @return A function from [N] to [R] defined by primitive recursion.
     */
    fun <R> recursion(base: R, step: (N, R) -> R): (N) -> R
}

/**
 * A concrete [PeanoSystem] backed by the kernel's [NaturalNumber] type.
 *
 * This object wires the Peano axioms to the actual [NaturalNumber] implementation
 * from the `kernel` module, providing successor, predecessor, and recursion
 * operations over `NaturalNumber` values.
 *
 * @see PeanoSystem
 * @see NaturalNumber
 */
object NaturalPeanoSystem : PeanoSystem<NaturalNumber> {
    /** The natural number zero. */
    override val zero: NaturalNumber = NaturalNumber.ZERO

    /**
     * @param n The natural number whose successor is requested.
     * @return The successor of [n].
     */
    override fun succ(n: NaturalNumber): NaturalNumber = n.succ()

    /**
     * @param n The natural number whose predecessor is requested.
     * @return The predecessor of [n], or `null` if [n] is zero.
     */
    override fun pred(n: NaturalNumber): NaturalNumber? = if (n.isZero()) null else n.pred()

    /**
     * @param n The natural number to test.
     * @return `true` if [n] is zero.
     */
    override fun isZero(n: NaturalNumber): Boolean = n.isZero()

    /**
     * Checks successor injectivity by exhaustive pairing over `0 ..< sampleSize`.
     *
     * @param sampleSize The number of natural numbers to sample.
     * @return `true` if no injectivity violation is found.
     */
    override fun verifyInjectivity(sampleSize: Int): Boolean {
        for (a in 0 until sampleSize) {
            for (b in 0 until sampleSize) {
                val na = NaturalNumber.of(a)
                val nb = NaturalNumber.of(b)
                if (succ(na) == succ(nb) && na != nb) return false
            }
        }
        return true
    }

    /**
     * Checks that zero is not the successor of any number in `0 ..< sampleSize`.
     *
     * @param sampleSize The number of natural numbers to sample.
     * @return `true` if no violation is found.
     */
    override fun verifyZeroNotSuccessor(sampleSize: Int): Boolean {
        for (n in 0 until sampleSize) {
            if (succ(NaturalNumber.of(n)) == zero) return false
        }
        return true
    }

    /**
     * Builds a primitive-recursive function over [NaturalNumber] by iterating
     * from zero up to the target value.
     *
     * @param R The result type.
     * @param base The value returned for zero.
     * @param step The step function applied at each successor.
     * @return A function computing the result of primitive recursion.
     */
    override fun <R> recursion(base: R, step: (NaturalNumber, R) -> R): (NaturalNumber) -> R {
        return { n ->
            var acc = base
            var current = NaturalNumber.ZERO
            while (current < n) {
                acc = step(current, acc)
                current = current.succ()
            }
            acc
        }
    }
}
