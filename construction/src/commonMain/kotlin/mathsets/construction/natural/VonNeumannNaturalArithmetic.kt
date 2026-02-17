package mathsets.construction.natural

import mathsets.kernel.Arithmetic

/**
 * Recursive arithmetic on Von Neumann naturals, implementing the [Arithmetic] interface.
 *
 * Addition and multiplication are defined purely by structural recursion on the
 * successor representation (the axiomatic path). Subtraction, division, and comparison
 * delegate to the kernel via [NaturalIsomorphism] for efficiency.
 *
 * @see VonNeumannNatural
 * @see NaturalIsomorphism
 */
object VonNeumannNaturalArithmetic : Arithmetic<VonNeumannNatural> {
    /** The additive identity `0`. */
    override val zero: VonNeumannNatural = VonNeumannNatural.ZERO

    /** The multiplicative identity `1`. */
    override val one: VonNeumannNatural = VonNeumannNatural.ONE

    /**
     * Computes `a + b` by structural recursion on [b]:
     * - `a + 0 = a`
     * - `a + S(b') = S(a + b')`
     *
     * @param a the left operand.
     * @param b the right operand.
     * @return the sum `a + b`.
     */
    override fun add(a: VonNeumannNatural, b: VonNeumannNatural): VonNeumannNatural = when (b) {
        VonNeumannNatural.Zero -> a
        is VonNeumannNatural.Succ -> add(a, b.previous).succ()
    }

    /**
     * Computes `a - b` by delegating to the kernel isomorphism.
     *
     * @param a the minuend.
     * @param b the subtrahend (must satisfy `b <= a`).
     * @return the difference `a - b`.
     */
    override fun subtract(a: VonNeumannNatural, b: VonNeumannNatural): VonNeumannNatural {
        val kernelResult = NaturalIsomorphism.toKernel(a) - NaturalIsomorphism.toKernel(b)
        return NaturalIsomorphism.toVonNeumann(kernelResult)
    }

    /**
     * Computes `a * b` by structural recursion on [b]:
     * - `a * 0 = 0`
     * - `a * S(b') = (a * b') + a`
     *
     * @param a the left operand.
     * @param b the right operand.
     * @return the product `a * b`.
     */
    override fun multiply(a: VonNeumannNatural, b: VonNeumannNatural): VonNeumannNatural = when (b) {
        VonNeumannNatural.Zero -> zero
        is VonNeumannNatural.Succ -> add(multiply(a, b.previous), a)
    }

    /**
     * Computes `a / b` (integer division) by delegating to the kernel isomorphism.
     *
     * @param a the dividend.
     * @param b the divisor (must not be zero).
     * @return the quotient `a / b`.
     */
    override fun divide(a: VonNeumannNatural, b: VonNeumannNatural): VonNeumannNatural {
        val kernelResult = NaturalIsomorphism.toKernel(a) / NaturalIsomorphism.toKernel(b)
        return NaturalIsomorphism.toVonNeumann(kernelResult)
    }

    /**
     * Computes `base ^ exponent` by structural recursion on [exponent]:
     * - `base ^ 0 = 1`
     * - `base ^ S(e') = (base ^ e') * base`
     *
     * @param base the base.
     * @param exponent the exponent.
     * @return the power `base ^ exponent`.
     */
    fun power(base: VonNeumannNatural, exponent: VonNeumannNatural): VonNeumannNatural = when (exponent) {
        VonNeumannNatural.Zero -> one
        is VonNeumannNatural.Succ -> multiply(power(base, exponent.previous), base)
    }

    /**
     * Compares two Von Neumann naturals via their kernel representations.
     *
     * @param a the first operand.
     * @param b the second operand.
     * @return a negative integer, zero, or a positive integer as [a] is less than,
     *         equal to, or greater than [b].
     */
    override fun compare(a: VonNeumannNatural, b: VonNeumannNatural): Int =
        NaturalIsomorphism.toKernel(a).compareTo(NaturalIsomorphism.toKernel(b))
}
