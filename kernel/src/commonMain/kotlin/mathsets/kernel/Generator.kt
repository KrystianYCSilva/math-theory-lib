package mathsets.kernel

import mathsets.kernel.platform.bigIntegerOf

/**
 * Lazy generators for fundamental numeric domains.
 *
 * Each generator produces an infinite [Sequence] that enumerates elements
 * of its respective number set. Sequences are lazily evaluated and can be
 * composed with standard Kotlin sequence operations (take, filter, map, etc.).
 *
 * @see Generator
 */
object Generators {

    /**
     * Generates an infinite sequence of natural numbers starting from [start].
     *
     * The sequence yields 0, 1, 2, 3, ... (or from the given start value)
     * using the Peano successor function.
     *
     * @param start The first natural number to yield (defaults to [NaturalNumber.ZERO]).
     * @return An infinite [Sequence] of [NaturalNumber] values.
     */
    fun naturals(start: NaturalNumber = NaturalNumber.ZERO): Sequence<NaturalNumber> = sequence {
        var current = start
        while (true) {
            yield(current)
            current = current.succ()
        }
    }

    /**
     * Generates an infinite sequence of integers in the order 0, 1, -1, 2, -2, 3, -3, ...
     *
     * This zigzag enumeration ensures every integer is eventually reached,
     * establishing a bijection between the naturals and the integers.
     *
     * @return An infinite [Sequence] of [IntegerNumber] values.
     */
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

    /**
     * Generates an infinite sequence of rational numbers using diagonal (Stern-Brocot style)
     * enumeration.
     *
     * Yields 0 first, then enumerates reduced fractions by increasing diagonal sum
     * (|numerator| + denominator), producing both positive and negative variants.
     *
     * @return An infinite [Sequence] of [RationalNumber] values covering all of Q.
     */
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
 * Compatibility alias that delegates to [Generators].
 *
 * Maintains backward compatibility with code that references `Generator` instead
 * of `Generators`.
 *
 * @see Generators
 */
object Generator {

    /**
     * Generates an infinite sequence of natural numbers starting from [start].
     *
     * @param start The first natural number to yield (defaults to [NaturalNumber.ZERO]).
     * @return An infinite [Sequence] of [NaturalNumber] values.
     * @see Generators.naturals
     */
    fun naturals(start: NaturalNumber = NaturalNumber.ZERO): Sequence<NaturalNumber> =
        Generators.naturals(start)

    /**
     * Generates an infinite sequence of integers.
     *
     * @return An infinite [Sequence] of [IntegerNumber] values.
     * @see Generators.integers
     */
    fun integers(): Sequence<IntegerNumber> = Generators.integers()

    /**
     * Generates an infinite sequence of rational numbers.
     *
     * @return An infinite [Sequence] of [RationalNumber] values.
     * @see Generators.rationals
     */
    fun rationals(): Sequence<RationalNumber> = Generators.rationals()
}
