package mathsets.construction.natural

import mathsets.kernel.NaturalNumber

/**
 * Isomorfismo entre naturais do kernel e naturais de Von Neumann.
 */
object NaturalIsomorphism {
    fun toVonNeumann(value: NaturalNumber): VonNeumannNatural = VonNeumannNatural.of(value)

    fun toKernel(value: VonNeumannNatural): NaturalNumber = when (value) {
        VonNeumannNatural.Zero -> NaturalNumber.ZERO
        is VonNeumannNatural.Succ -> toKernel(value.previous).succ()
    }

    fun verifyKernelRoundTrip(limit: Int): Boolean {
        for (n in 0..limit) {
            val kernel = NaturalNumber.of(n)
            if (toKernel(toVonNeumann(kernel)) != kernel) return false
        }
        return true
    }

    fun verifyVonNeumannRoundTrip(limit: Int): Boolean {
        for (n in 0..limit) {
            val vonNeumann = VonNeumannNatural.of(n)
            if (toVonNeumann(toKernel(vonNeumann)) != vonNeumann) return false
        }
        return true
    }

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

fun NaturalNumber.toVonNeumannNatural(): VonNeumannNatural =
    NaturalIsomorphism.toVonNeumann(this)

fun VonNeumannNatural.toKernelNatural(): NaturalNumber =
    NaturalIsomorphism.toKernel(this)

