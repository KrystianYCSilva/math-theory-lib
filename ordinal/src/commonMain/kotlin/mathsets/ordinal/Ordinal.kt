package mathsets.ordinal

import mathsets.kernel.MathElement
import mathsets.kernel.NaturalNumber

/**
 * Represents an ordinal number, either finite or in Cantor Normal Form (CNF).
 *
 * This sealed interface models ordinals with finite exponents, which is sufficient for
 * classical operations involving omega, omega + n, omega * n, and omega^k.
 *
 * Ordinals are well-ordered and comparable. Arithmetic operators [plus], [times], and [pow]
 * are provided as extension functions.
 *
 * @see CantorNormalForm
 * @see OrdinalArithmetic
 */
sealed interface Ordinal : MathElement, Comparable<Ordinal> {
    /**
     * Returns `true` if this ordinal is zero.
     */
    fun isZero(): Boolean

    /**
     * Returns the successor of this ordinal (i.e., this + 1).
     */
    fun succ(): Ordinal = this + ONE

    /**
     * Returns the predecessor of this ordinal, or `null` if this ordinal is zero or a limit ordinal.
     */
    fun predOrNull(): Ordinal?

    override fun compareTo(other: Ordinal): Int = OrdinalArithmetic.compare(this, other)

    /**
     * A finite ordinal backed by a [NaturalNumber].
     *
     * Finite ordinals correspond to the natural numbers 0, 1, 2, ...
     *
     * @property value The natural number value of this finite ordinal.
     */
    data class Finite(val value: NaturalNumber) : Ordinal {
        override fun isZero(): Boolean = value.isZero()

        override fun predOrNull(): Ordinal? =
            if (value.isZero()) null else Finite(value.pred())

        override fun toString(): String = value.toString()
    }

    /**
     * An ordinal represented in Cantor Normal Form.
     *
     * CNF ordinals are of the form omega^a1 * c1 + omega^a2 * c2 + ... where
     * a1 > a2 > ... and each coefficient ci is a positive natural number.
     *
     * @property normalForm The Cantor Normal Form representation of this ordinal.
     */
    data class CNF(val normalForm: CantorNormalForm) : Ordinal {
        override fun isZero(): Boolean = false

        override fun predOrNull(): Ordinal? {
            val terms = normalForm.terms
            val last = terms.last()
            if (last.exponent > NaturalNumber.ZERO) {
                return null
            }

            val newTerms = if (last.coefficient > NaturalNumber.ONE) {
                val decremented = CNFTerm(last.exponent, last.coefficient - NaturalNumber.ONE)
                terms.dropLast(1) + decremented
            } else {
                terms.dropLast(1)
            }

            return fromCanonicalTerms(newTerms)
        }

        override fun toString(): String = normalForm.terms.joinToString(" + ") { term ->
            when {
                term.exponent.isZero() -> term.coefficient.toString()
                term.exponent == NaturalNumber.ONE && term.coefficient == NaturalNumber.ONE -> "ω"
                term.exponent == NaturalNumber.ONE -> "${term.coefficient}ω"
                term.coefficient == NaturalNumber.ONE -> "ω^${term.exponent}"
                else -> "${term.coefficient}ω^${term.exponent}"
            }
        }
    }

    companion object {
        /** The ordinal zero. */
        val ZERO: Ordinal = Finite(NaturalNumber.ZERO)

        /** The ordinal one. */
        val ONE: Ordinal = Finite(NaturalNumber.ONE)

        /** The first transfinite ordinal omega (the order type of the natural numbers). */
        val OMEGA: Ordinal = cnf(
            listOf(CNFTerm(exponent = NaturalNumber.ONE, coefficient = NaturalNumber.ONE))
        )

        /**
         * Creates a finite ordinal from a [NaturalNumber].
         *
         * @param value The natural number value.
         * @return A finite ordinal representing the given value.
         */
        fun finite(value: NaturalNumber): Ordinal = Finite(value)

        /**
         * Creates a finite ordinal from an [Int].
         *
         * @param value The integer value (must be non-negative).
         * @return A finite ordinal representing the given value.
         */
        fun finite(value: Int): Ordinal = finite(NaturalNumber.of(value))

        /**
         * Creates a finite ordinal from a [Long].
         *
         * @param value The long value (must be non-negative).
         * @return A finite ordinal representing the given value.
         */
        fun finite(value: Long): Ordinal = finite(NaturalNumber.of(value))

        /**
         * Creates an ordinal from a list of [CNFTerm]s. The terms are normalized
         * (sorted by descending exponent, like-exponents merged) before construction.
         *
         * @param terms The list of CNF terms.
         * @return The ordinal in Cantor Normal Form, or [ZERO] if the list normalizes to empty.
         */
        fun cnf(terms: List<CNFTerm>): Ordinal = fromCanonicalTerms(normalizeTerms(terms))

        /**
         * Creates the ordinal omega^[exponent].
         *
         * @param exponent The non-negative exponent.
         * @return The ordinal omega raised to the given power.
         * @throws IllegalArgumentException if [exponent] is negative.
         */
        fun omegaPower(exponent: Int): Ordinal {
            require(exponent >= 0) { "Exponent must be non-negative." }
            return if (exponent == 0) {
                ONE
            } else {
                cnf(
                    listOf(
                        CNFTerm(
                            exponent = NaturalNumber.of(exponent),
                            coefficient = NaturalNumber.ONE
                        )
                    )
                )
            }
        }
    }
}

internal fun Ordinal.toCanonicalTerms(): List<CNFTerm> = when (this) {
    is Ordinal.Finite -> {
        if (value.isZero()) {
            emptyList()
        } else {
            listOf(CNFTerm(exponent = NaturalNumber.ZERO, coefficient = value))
        }
    }

    is Ordinal.CNF -> normalForm.terms
}

internal fun Ordinal.leadingExponent(): NaturalNumber = toCanonicalTerms()
    .firstOrNull()
    ?.exponent
    ?: NaturalNumber.ZERO

internal fun fromCanonicalTerms(terms: List<CNFTerm>): Ordinal {
    val normalized = normalizeTerms(terms)
    if (normalized.isEmpty()) return Ordinal.ZERO

    val single = normalized.singleOrNull()
    if (single != null && single.exponent.isZero()) {
        return Ordinal.Finite(single.coefficient)
    }

    return Ordinal.CNF(CantorNormalForm.of(normalized))
}

/**
 * Adds two ordinals using ordinal addition (which is not commutative).
 *
 * @receiver The left operand.
 * @param other The right operand.
 * @return The ordinal sum.
 */
operator fun Ordinal.plus(other: Ordinal): Ordinal = OrdinalArithmetic.add(this, other)

/**
 * Multiplies two ordinals using ordinal multiplication (which is not commutative).
 *
 * @receiver The left operand.
 * @param other The right operand.
 * @return The ordinal product.
 */
operator fun Ordinal.times(other: Ordinal): Ordinal = OrdinalArithmetic.multiply(this, other)

/**
 * Raises this ordinal to a finite power using ordinal exponentiation.
 *
 * @receiver The base ordinal.
 * @param exponent The exponent as a [NaturalNumber].
 * @return The ordinal power.
 */
infix fun Ordinal.pow(exponent: NaturalNumber): Ordinal = OrdinalArithmetic.power(this, exponent)

/**
 * Raises this ordinal to a finite power using ordinal exponentiation.
 *
 * @receiver The base ordinal.
 * @param exponent The exponent as an [Int].
 * @return The ordinal power.
 */
infix fun Ordinal.pow(exponent: Int): Ordinal = this pow NaturalNumber.of(exponent)
