package mathsets.cardinal

import mathsets.kernel.Cardinality
import mathsets.kernel.NaturalNumber

/**
 * Type alias representing a cardinal number as a [Cardinality].
 */
typealias CardinalNumber = Cardinality

/**
 * Implements basic cardinal arithmetic for finite, countably infinite, and uncountable cardinals.
 *
 * Cardinal arithmetic follows different rules than ordinal arithmetic:
 * - Addition and multiplication of infinite cardinals yield the larger cardinal.
 * - Exponentiation can jump cardinality levels (e.g., 2^aleph0 = continuum).
 *
 * @see Cardinality
 */
object CardinalArithmetic {
    /** The cardinality aleph-0 (countably infinite). */
    val aleph0: CardinalNumber = Cardinality.CountablyInfinite

    /** The cardinality of the continuum (uncountable, |R| = 2^aleph0). */
    val continuum: CardinalNumber = Cardinality.Uncountable

    /**
     * Computes the cardinal sum of [a] and [b].
     *
     * For infinite cardinals, the result is the maximum of the two. For finite
     * cardinals, the result is the arithmetic sum.
     *
     * @param a The first cardinal.
     * @param b The second cardinal.
     * @return The cardinal sum.
     */
    fun add(a: CardinalNumber, b: CardinalNumber): CardinalNumber = when {
        a is Cardinality.Unknown || b is Cardinality.Unknown -> Cardinality.Unknown
        a is Cardinality.Uncountable || b is Cardinality.Uncountable -> Cardinality.Uncountable
        a is Cardinality.CountablyInfinite || b is Cardinality.CountablyInfinite -> Cardinality.CountablyInfinite
        a is Cardinality.Finite && b is Cardinality.Finite -> Cardinality.Finite(a.n + b.n)
        else -> Cardinality.Unknown
    }

    /**
     * Computes the cardinal product of [a] and [b].
     *
     * If either operand is zero, the result is zero. For infinite cardinals, the
     * result is the maximum of the two.
     *
     * @param a The first cardinal.
     * @param b The second cardinal.
     * @return The cardinal product.
     */
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

    /**
     * Computes the cardinal exponentiation `base ^ exponent`.
     *
     * Notable results:
     * - Any cardinal to the power 0 is 1.
     * - 0 to any positive power is 0.
     * - 2^aleph0 = continuum (uncountable).
     * - aleph0^aleph0 = continuum.
     *
     * @param base The base cardinal.
     * @param exponent The exponent cardinal.
     * @return The cardinal power, or [Cardinality.Unknown] if undetermined.
     */
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

/**
 * Represents the Continuum Hypothesis (CH) and its status within ZFC set theory.
 *
 * The Continuum Hypothesis states that there is no set whose cardinality is strictly
 * between that of the natural numbers (aleph-0) and the real numbers (continuum).
 * It was proven independent of ZFC by Godel (consistency, 1940) and Cohen (independence, 1963).
 */
object ContinuumHypothesis {
    /** A human-readable statement of the Continuum Hypothesis. */
    const val statement: String =
        "Nao existe cardinal estritamente entre o enumeravel (aleph_0) e o continuo."

    /** A description of the CH's status within ZFC. */
    const val statusInZfc: String = "Independente de ZFC (nao provavel nem refutavel em ZFC)."
}
