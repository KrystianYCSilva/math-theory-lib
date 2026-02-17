package mathsets.ordinal

import mathsets.kernel.NaturalNumber

/**
 * Implements ordinal arithmetic operations on Cantor Normal Form representations
 * with finite exponents.
 *
 * Provides addition, multiplication, exponentiation, and comparison of ordinals.
 * Note that ordinal addition and multiplication are **not commutative**:
 * for example, `1 + omega = omega`, but `omega + 1 != omega`.
 *
 * General ordinal division is not supported.
 */
object OrdinalArithmetic {
    /**
     * Compares two ordinals lexicographically by their CNF terms.
     *
     * @param a The first ordinal.
     * @param b The second ordinal.
     * @return A negative integer, zero, or a positive integer as [a] is less than,
     *         equal to, or greater than [b].
     */
    fun compare(a: Ordinal, b: Ordinal): Int =
        compareTerms(a.toCanonicalTerms(), b.toCanonicalTerms())

    /**
     * Computes the ordinal sum `a + b`.
     *
     * Ordinal addition absorbs lower-order terms of [a] that are dominated by the
     * leading term of [b]. This is why `1 + omega = omega` (the finite `1` is absorbed).
     *
     * @param a The left operand.
     * @param b The right operand.
     * @return The ordinal sum.
     */
    fun add(a: Ordinal, b: Ordinal): Ordinal {
        if (a.isZero()) return b
        if (b.isZero()) return a

        val aTerms = a.toCanonicalTerms()
        val bTerms = b.toCanonicalTerms()
        val beta = bTerms.first().exponent

        val splitIndex = aTerms.indexOfFirst { it.exponent <= beta }
        if (splitIndex == -1) {
            return fromCanonicalTerms(aTerms + bTerms)
        }

        val prefix = aTerms.take(splitIndex)
        val splitTerm = aTerms[splitIndex]
        return if (splitTerm.exponent == beta) {
            val merged = CNFTerm(
                exponent = beta,
                coefficient = splitTerm.coefficient + bTerms.first().coefficient
            )
            fromCanonicalTerms(prefix + merged + bTerms.drop(1))
        } else {
            fromCanonicalTerms(prefix + bTerms)
        }
    }

    /**
     * Computes the ordinal product `a * b`.
     *
     * Ordinal multiplication distributes over addition on the right.
     * Like addition, it is **not commutative**: `omega * 2 != 2 * omega`.
     *
     * @param a The left operand.
     * @param b The right operand.
     * @return The ordinal product.
     */
    fun multiply(a: Ordinal, b: Ordinal): Ordinal {
        if (a.isZero() || b.isZero()) return Ordinal.ZERO

        val finiteRight = b.asFiniteOrNull()
        if (finiteRight != null) {
            return multiplyByFiniteRight(a, finiteRight)
        }

        var result: Ordinal = Ordinal.ZERO
        for (term in b.toCanonicalTerms()) {
            val principal = multiplyByOmegaPower(a, term.exponent)
            val scaled = multiplyByFiniteRight(principal, term.coefficient)
            result = add(result, scaled)
        }
        return result
    }

    /**
     * Computes the ordinal power `base ^ exponent` for a finite exponent.
     *
     * Uses iterated multiplication: base * base * ... * base (exponent times).
     *
     * @param base The base ordinal.
     * @param exponent The finite exponent.
     * @return The ordinal exponentiation result.
     */
    fun power(base: Ordinal, exponent: NaturalNumber): Ordinal {
        if (exponent.isZero()) return Ordinal.ONE

        var remaining = exponent
        var result: Ordinal = Ordinal.ONE
        while (remaining > NaturalNumber.ZERO) {
            result = multiply(result, base)
            remaining = remaining.pred()
        }
        return result
    }

    private fun multiplyByFiniteRight(a: Ordinal, multiplier: NaturalNumber): Ordinal {
        if (multiplier.isZero()) return Ordinal.ZERO

        var remaining = multiplier
        var result: Ordinal = Ordinal.ZERO
        while (remaining > NaturalNumber.ZERO) {
            result = add(result, a)
            remaining = remaining.pred()
        }
        return result
    }

    private fun multiplyByOmegaPower(a: Ordinal, exponent: NaturalNumber): Ordinal {
        if (exponent.isZero()) return a

        val leading = a.leadingExponent()
        return fromCanonicalTerms(
            listOf(
                CNFTerm(
                    exponent = leading + exponent,
                    coefficient = NaturalNumber.ONE
                )
            )
        )
    }

    private fun compareTerms(left: List<CNFTerm>, right: List<CNFTerm>): Int {
        val minSize = minOf(left.size, right.size)
        for (i in 0 until minSize) {
            val exponentCmp = left[i].exponent.compareTo(right[i].exponent)
            if (exponentCmp != 0) return exponentCmp

            val coefficientCmp = left[i].coefficient.compareTo(right[i].coefficient)
            if (coefficientCmp != 0) return coefficientCmp
        }
        return left.size.compareTo(right.size)
    }

    private fun Ordinal.asFiniteOrNull(): NaturalNumber? = when (this) {
        is Ordinal.Finite -> value
        else -> null
    }
}
