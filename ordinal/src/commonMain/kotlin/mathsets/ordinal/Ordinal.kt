package mathsets.ordinal

import mathsets.kernel.MathElement
import mathsets.kernel.NaturalNumber

/**
 * Ordinal finito ou em forma normal de Cantor (CNF).
 *
 * Esta implementação cobre ordinais com expoentes finitos, suficientes para
 * operações clássicas com ω, ω + n, ω * n e ω^k.
 */
sealed interface Ordinal : MathElement, Comparable<Ordinal> {
    fun isZero(): Boolean
    fun succ(): Ordinal = this + ONE
    fun predOrNull(): Ordinal?

    override fun compareTo(other: Ordinal): Int = OrdinalArithmetic.compare(this, other)

    data class Finite(val value: NaturalNumber) : Ordinal {
        override fun isZero(): Boolean = value.isZero()

        override fun predOrNull(): Ordinal? =
            if (value.isZero()) null else Finite(value.pred())

        override fun toString(): String = value.toString()
    }

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
        val ZERO: Ordinal = Finite(NaturalNumber.ZERO)
        val ONE: Ordinal = Finite(NaturalNumber.ONE)
        val OMEGA: Ordinal = cnf(
            listOf(CNFTerm(exponent = NaturalNumber.ONE, coefficient = NaturalNumber.ONE))
        )

        fun finite(value: NaturalNumber): Ordinal = Finite(value)

        fun finite(value: Int): Ordinal = finite(NaturalNumber.of(value))

        fun finite(value: Long): Ordinal = finite(NaturalNumber.of(value))

        fun cnf(terms: List<CNFTerm>): Ordinal = fromCanonicalTerms(normalizeTerms(terms))

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

operator fun Ordinal.plus(other: Ordinal): Ordinal = OrdinalArithmetic.add(this, other)

operator fun Ordinal.times(other: Ordinal): Ordinal = OrdinalArithmetic.multiply(this, other)

infix fun Ordinal.pow(exponent: NaturalNumber): Ordinal = OrdinalArithmetic.power(this, exponent)

infix fun Ordinal.pow(exponent: Int): Ordinal = this pow NaturalNumber.of(exponent)
