package mathsets.kernel

import mathsets.kernel.platform.bigIntegerOf

/**
 * Geradores lazy para domínios numéricos fundamentais.
 */
object Generators {
    fun naturals(start: NaturalNumber = NaturalNumber.ZERO): Sequence<NaturalNumber> = sequence {
        var current = start
        while (true) {
            yield(current)
            current = current.succ()
        }
    }

    fun integers(): Sequence<IntegerNumber> = sequence {
        yield(IntegerNumber.ZERO)
        var n = 1L
        while (true) {
            val positive = IntegerNumber.of(n)
            yield(positive)
            yield(-positive)
            n++
        }
    }

    fun rationals(): Sequence<RationalNumber> = sequence {
        yield(RationalNumber.ZERO)

        var diagonal = 2L
        while (true) {
            for (denominator in 1L..diagonal) {
                val numeratorAbs = diagonal - denominator
                if (numeratorAbs <= 0L) continue
                if (gcd(numeratorAbs, denominator) != 1L) continue

                val positive = RationalNumber.of(
                    numerator = bigIntegerOf(numeratorAbs),
                    denominator = bigIntegerOf(denominator)
                )
                yield(positive)
                yield(-positive)
            }
            diagonal++
        }
    }

    private fun gcd(a: Long, b: Long): Long {
        var x = if (a < 0) -a else a
        var y = if (b < 0) -b else b
        while (y != 0L) {
            val temp = y
            y = x % y
            x = temp
        }
        return x
    }
}

/**
 * Alias nominal para manter compatibilidade com roteiros que referenciam `Generator`.
 */
object Generator {
    fun naturals(start: NaturalNumber = NaturalNumber.ZERO): Sequence<NaturalNumber> =
        Generators.naturals(start)

    fun integers(): Sequence<IntegerNumber> = Generators.integers()

    fun rationals(): Sequence<RationalNumber> = Generators.rationals()
}
