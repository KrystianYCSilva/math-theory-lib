package mathsets.cardinal

import mathsets.kernel.Cardinality
import mathsets.kernel.NaturalNumber

typealias CardinalNumber = Cardinality

/**
 * Aritmética cardinal básica para finitos, enumeráveis e não enumeráveis.
 */
object CardinalArithmetic {
    val aleph0: CardinalNumber = Cardinality.CountablyInfinite
    val continuum: CardinalNumber = Cardinality.Uncountable

    fun add(a: CardinalNumber, b: CardinalNumber): CardinalNumber = when {
        a is Cardinality.Unknown || b is Cardinality.Unknown -> Cardinality.Unknown
        a is Cardinality.Uncountable || b is Cardinality.Uncountable -> Cardinality.Uncountable
        a is Cardinality.CountablyInfinite || b is Cardinality.CountablyInfinite -> Cardinality.CountablyInfinite
        a is Cardinality.Finite && b is Cardinality.Finite -> Cardinality.Finite(a.n + b.n)
        else -> Cardinality.Unknown
    }

    fun multiply(a: CardinalNumber, b: CardinalNumber): CardinalNumber {
        val aFinite = a.asFiniteOrNull()
        val bFinite = b.asFiniteOrNull()

        if ((aFinite != null && aFinite.isZero()) || (bFinite != null && bFinite.isZero())) {
            return Cardinality.Finite(NaturalNumber.ZERO)
        }

        return when {
            a is Cardinality.Unknown || b is Cardinality.Unknown -> Cardinality.Unknown
            a is Cardinality.Uncountable || b is Cardinality.Uncountable -> Cardinality.Uncountable
            a is Cardinality.CountablyInfinite || b is Cardinality.CountablyInfinite -> Cardinality.CountablyInfinite
            aFinite != null && bFinite != null -> Cardinality.Finite(aFinite * bFinite)
            else -> Cardinality.Unknown
        }
    }

    fun power(base: CardinalNumber, exponent: CardinalNumber): CardinalNumber {
        val baseFinite = base.asFiniteOrNull()
        val exponentFinite = exponent.asFiniteOrNull()

        if (exponentFinite != null && exponentFinite.isZero()) {
            return Cardinality.Finite(NaturalNumber.ONE)
        }

        if (baseFinite != null && baseFinite.isZero()) {
            return Cardinality.Finite(NaturalNumber.ZERO)
        }

        if (base is Cardinality.Unknown || exponent is Cardinality.Unknown) {
            return Cardinality.Unknown
        }

        if (baseFinite != null && exponentFinite != null) {
            return Cardinality.Finite(baseFinite pow exponentFinite)
        }

        if (exponent is Cardinality.CountablyInfinite) {
            return when {
                baseFinite == NaturalNumber.ONE -> Cardinality.Finite(NaturalNumber.ONE)
                baseFinite != null && baseFinite >= NaturalNumber.of(2) -> Cardinality.Uncountable
                base is Cardinality.CountablyInfinite -> Cardinality.Uncountable
                base is Cardinality.Uncountable -> Cardinality.Uncountable
                else -> Cardinality.Unknown
            }
        }

        if (exponent is Cardinality.Uncountable) {
            return when {
                baseFinite == NaturalNumber.ONE -> Cardinality.Finite(NaturalNumber.ONE)
                else -> Cardinality.Uncountable
            }
        }

        if (exponentFinite != null) {
            if (base is Cardinality.CountablyInfinite || base is Cardinality.Uncountable) {
                return if (exponentFinite.isZero()) {
                    Cardinality.Finite(NaturalNumber.ONE)
                } else {
                    base
                }
            }
        }

        return Cardinality.Unknown
    }

    private fun CardinalNumber.asFiniteOrNull(): NaturalNumber? =
        (this as? Cardinality.Finite)?.n
}

object ContinuumHypothesis {
    const val statement: String =
        "Nao existe cardinal estritamente entre o enumeravel (aleph_0) e o continuo."
    const val statusInZfc: String = "Independente de ZFC (nao provavel nem refutavel em ZFC)."
}

