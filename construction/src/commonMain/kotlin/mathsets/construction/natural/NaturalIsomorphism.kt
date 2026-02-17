package mathsets.construction.natural

import mathsets.kernel.NaturalNumber

/**
 * Structure-preserving bijection between kernel [NaturalNumber] values and
 * axiomatic [VonNeumannNatural] values.
 *
 * This isomorphism bridges the efficient kernel representation with the
 * set-theoretic Von Neumann construction, enabling interoperability while
 * preserving additive and multiplicative structure.
 *
 * @see VonNeumannNatural
 * @see NaturalNumber
 */
object NaturalIsomorphism {
    /**
     * Maps a kernel [NaturalNumber] to its Von Neumann representation.
     *
     * @param value the kernel natural number.
     * @return the corresponding [VonNeumannNatural].
     */
    fun toVonNeumann(value: NaturalNumber): VonNeumannNatural = VonNeumannNatural.of(value)

    /**
     * Maps a [VonNeumannNatural] back to the kernel [NaturalNumber] by
     * recursively counting predecessors.
     *
     * @param value the Von Neumann natural.
     * @return the corresponding kernel [NaturalNumber].
     */
    fun toKernel(value: VonNeumannNatural): NaturalNumber = when (value) {
        VonNeumannNatural.Zero -> NaturalNumber.ZERO
        is VonNeumannNatural.Succ -> toKernel(value.previous).succ()
    }

    /**
     * Verifies the round-trip property `toKernel(toVonNeumann(k)) == k` for all
     * kernel naturals in `0..limit`.
     *
     * @param limit the upper bound (inclusive) of the test range.
     * @return `true` if the round-trip holds for every value in the range.
     */
    fun verifyKernelRoundTrip(limit: Int): Boolean {
        for (n in 0..limit) {
            val kernel = NaturalNumber.of(n)
            if (toKernel(toVonNeumann(kernel)) != kernel) return false
        }
        return true
    }

    /**
     * Verifies the round-trip property `toVonNeumann(toKernel(v)) == v` for all
     * Von Neumann naturals in `0..limit`.
     *
     * @param limit the upper bound (inclusive) of the test range.
     * @return `true` if the round-trip holds for every value in the range.
     */
    fun verifyVonNeumannRoundTrip(limit: Int): Boolean {
        for (n in 0..limit) {
            val vonNeumann = VonNeumannNatural.of(n)
            if (toVonNeumann(toKernel(vonNeumann)) != vonNeumann) return false
        }
        return true
    }

    /**
     * Verifies that the isomorphism preserves addition:
     * `toKernel(vonAdd(toVonNeumann(a), toVonNeumann(b))) == a + b`
     * for all pairs in `0..limit`.
     *
     * @param limit the upper bound (inclusive) of the test range.
     * @return `true` if addition is preserved for all tested pairs.
     */
    fun verifyAdditionPreservation(limit: Int): Boolean {
        for (a in 0..limit) {
            for (b in 0..limit) {
                val left = toKernel(
                    VonNeumannNaturalArithmetic.add(
                        toVonNeumann(NaturalNumber.of(a)),
                        toVonNeumann(NaturalNumber.of(b))
                    )
                )
                val right = NaturalNumber.of(a) + NaturalNumber.of(b)
                if (left != right) return false
            }
        }
        return true
    }

    /**
     * Verifies that the isomorphism preserves multiplication:
     * `toKernel(vonMul(toVonNeumann(a), toVonNeumann(b))) == a * b`
     * for all pairs in `0..limit`.
     *
     * @param limit the upper bound (inclusive) of the test range.
     * @return `true` if multiplication is preserved for all tested pairs.
     */
    fun verifyMultiplicationPreservation(limit: Int): Boolean {
        for (a in 0..limit) {
            for (b in 0..limit) {
                val left = toKernel(
                    VonNeumannNaturalArithmetic.multiply(
                        toVonNeumann(NaturalNumber.of(a)),
                        toVonNeumann(NaturalNumber.of(b))
                    )
                )
                val right = NaturalNumber.of(a) * NaturalNumber.of(b)
                if (left != right) return false
            }
        }
        return true
    }
}

/**
 * Extension function to convert a kernel [NaturalNumber] to [VonNeumannNatural].
 */
fun NaturalNumber.toVonNeumannNatural(): VonNeumannNatural =
    NaturalIsomorphism.toVonNeumann(this)

/**
 * Extension function to convert a [VonNeumannNatural] to a kernel [NaturalNumber].
 */
fun VonNeumannNatural.toKernelNatural(): NaturalNumber =
    NaturalIsomorphism.toKernel(this)
