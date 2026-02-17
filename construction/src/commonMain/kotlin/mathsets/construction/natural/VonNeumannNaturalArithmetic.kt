package mathsets.construction.natural

import mathsets.kernel.Arithmetic

/**
 * Aritmética recursiva nos naturais de Von Neumann.
 */
object VonNeumannNaturalArithmetic : Arithmetic<VonNeumannNatural> {
    override val zero: VonNeumannNatural = VonNeumannNatural.ZERO
    override val one: VonNeumannNatural = VonNeumannNatural.ONE

    override fun add(a: VonNeumannNatural, b: VonNeumannNatural): VonNeumannNatural = when (b) {
        VonNeumannNatural.Zero -> a
        is VonNeumannNatural.Succ -> add(a, b.previous).succ()
    }

    override fun subtract(a: VonNeumannNatural, b: VonNeumannNatural): VonNeumannNatural {
        val kernelResult = NaturalIsomorphism.toKernel(a) - NaturalIsomorphism.toKernel(b)
        return NaturalIsomorphism.toVonNeumann(kernelResult)
    }

    override fun multiply(a: VonNeumannNatural, b: VonNeumannNatural): VonNeumannNatural = when (b) {
        VonNeumannNatural.Zero -> zero
        is VonNeumannNatural.Succ -> add(multiply(a, b.previous), a)
    }

    override fun divide(a: VonNeumannNatural, b: VonNeumannNatural): VonNeumannNatural {
        val kernelResult = NaturalIsomorphism.toKernel(a) / NaturalIsomorphism.toKernel(b)
        return NaturalIsomorphism.toVonNeumann(kernelResult)
    }

    fun power(base: VonNeumannNatural, exponent: VonNeumannNatural): VonNeumannNatural = when (exponent) {
        VonNeumannNatural.Zero -> one
        is VonNeumannNatural.Succ -> multiply(power(base, exponent.previous), base)
    }

    override fun compare(a: VonNeumannNatural, b: VonNeumannNatural): Int =
        NaturalIsomorphism.toKernel(a).compareTo(NaturalIsomorphism.toKernel(b))
}

