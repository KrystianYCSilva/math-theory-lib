package mathsets.construction.natural

import mathsets.kernel.MathElement
import mathsets.kernel.NaturalNumber
import mathsets.set.MathSet
import mathsets.set.mathSetOf

/**
 * Von Neumann encoding of the natural numbers as pure sets.
 *
 * Each natural number is identified with the set of all smaller naturals:
 * - `0 = {}` (the empty set)
 * - `S(n) = n U {n}` (the successor of `n`)
 *
 * This construction satisfies the Peano axioms and provides a set-theoretic
 * foundation for arithmetic via [VonNeumannNaturalArithmetic].
 *
 * @see VonNeumannPeanoSystem
 * @see VonNeumannNaturalArithmetic
 * @see NaturalIsomorphism
 */
sealed interface VonNeumannNatural : MathElement {
    /**
     * Returns the set-theoretic representation of this natural number.
     *
     * For `0` this is the empty set; for `S(n)` this is `n U {n}`.
     */
    fun toSet(): MathSet<VonNeumannNatural>

    /**
     * Returns the successor of this natural number: `S(this) = this U {this}`.
     */
    fun succ(): VonNeumannNatural = Succ(this)

    /**
     * Returns the predecessor of this natural number, or `null` if this is zero.
     */
    fun predOrNull(): VonNeumannNatural? = when (this) {
        Zero -> null
        is Succ -> previous
    }

    /**
     * The Von Neumann representation of zero, defined as the empty set `{}`.
     */
    data object Zero : VonNeumannNatural {
        override fun toSet(): MathSet<VonNeumannNatural> = MathSet.empty()
        override fun toString(): String = "0"
    }

    /**
     * The successor of a Von Neumann natural, defined as `previous U {previous}`.
     *
     * @property previous the natural number whose successor this represents.
     */
    data class Succ(val previous: VonNeumannNatural) : VonNeumannNatural {
        override fun toSet(): MathSet<VonNeumannNatural> =
            previous.toSet() union MathSet.singleton(previous)
    }

    companion object {
        /** The Von Neumann natural `0 = {}`. */
        val ZERO: VonNeumannNatural = Zero

        /** The Von Neumann natural `1 = {0} = {{}}`. */
        val ONE: VonNeumannNatural = Succ(Zero)

        /**
         * Converts a kernel [NaturalNumber] to its Von Neumann representation
         * by iteratively applying the successor operation.
         *
         * @param value the kernel natural number to convert.
         * @return the corresponding [VonNeumannNatural].
         */
        fun of(value: NaturalNumber): VonNeumannNatural {
            var result: VonNeumannNatural = Zero
            var current = NaturalNumber.ZERO
            while (current < value) {
                result = Succ(result)
                current = current.succ()
            }
            return result
        }

        /**
         * Converts a non-negative [Int] to its Von Neumann representation.
         *
         * @param value the integer to convert (must be non-negative).
         * @return the corresponding [VonNeumannNatural].
         * @throws IllegalArgumentException if [value] is negative.
         */
        fun of(value: Int): VonNeumannNatural = of(NaturalNumber.of(value))

        /**
         * Reconstructs a Von Neumann natural from its set-theoretic representation.
         *
         * Given an iterable of elements representing the members of a Von Neumann set,
         * returns the natural number whose set representation contains exactly those elements.
         *
         * @param elements the members of the set representation.
         * @return the [VonNeumannNatural] encoded by [elements].
         */
        fun fromSetRepresentation(elements: Iterable<VonNeumannNatural>): VonNeumannNatural {
            val ordered = elements.sortedBy { NaturalIsomorphism.toKernel(it) }
            return if (ordered.isEmpty()) {
                Zero
            } else {
                ordered.last().succ()
            }
        }

        /**
         * Returns a [MathSet] containing the first [size] Von Neumann naturals `{0, 1, ..., size-1}`.
         *
         * @param size the number of elements (must be non-negative).
         * @return a finite set of Von Neumann naturals.
         * @throws IllegalArgumentException if [size] is negative.
         */
        fun finitePrefix(size: Int): MathSet<VonNeumannNatural> {
            require(size >= 0) { "size must be non-negative" }
            return mathSetOf((0 until size).map { of(it) })
        }
    }
}
