package mathsets.construction.real

import mathsets.kernel.Arithmetic
import mathsets.kernel.MathElement
import mathsets.kernel.RationalNumber
import mathsets.kernel.RealNumber

private const val DEFAULT_DEDEKIND_PRECISION_INDEX: Int = 32

/**
 * Explicit Cauchy-real wrapper around [ConstructedReal].
 *
 * This type makes the Cauchy construction explicit in APIs where both
 * Cauchy and Dedekind presentations coexist.
 *
 * @property value Underlying constructed real represented by a Cauchy sequence.
 */
data class CauchyReal(val value: ConstructedReal) : Comparable<CauchyReal>, MathElement {
    /**
     * Projects this Cauchy real to kernel representation.
     *
     * @return Kernel [RealNumber].
     */
    fun toKernel(): RealNumber = value.toKernel()

    override fun compareTo(other: CauchyReal): Int = value.compareTo(other.value)

    companion object {
        /**
         * Lifts a kernel real to a Cauchy real.
         *
         * @param value Kernel real value.
         * @return Cauchy real wrapper.
         */
        fun fromKernel(value: RealNumber): CauchyReal = CauchyReal(value.toMathReal())
    }
}

/**
 * Computable approximation of a Dedekind cut.
 *
 * The cut is represented by a witness real `bound` and interpreted as
 * `q in L` iff `q < bound`.
 *
 * @property bound Witness real defining the lower cut.
 */
class DedekindCut(val bound: ConstructedReal) {
    /**
     * Checks whether a rational belongs to this lower cut.
     *
     * @param q Rational candidate.
     * @return True when `q < bound`.
     */
    fun contains(q: RationalNumber): Boolean = q.toMathReal() < bound

    /**
     * Finite sanity check for lower-set closure over sampled rationals.
     *
     * @param samples Rational samples.
     * @return True when sampled closure holds.
     */
    fun isLowerSetOnSamples(samples: Set<RationalNumber>): Boolean =
        samples.all { x ->
            if (!contains(x)) true
            else samples.all { y -> (y <= x).not() || contains(y) }
        }

    /**
     * Finite sanity check that sampled members do not exhibit a greatest element.
     *
     * @param samples Rational samples.
     * @return True when each sampled member has a strictly larger sampled member
     * still in the cut.
     */
    fun hasNoGreatestElementOnSamples(samples: Set<RationalNumber>): Boolean {
        val inside = samples.filter(::contains)
        if (inside.isEmpty()) return true
        return inside.all { x -> inside.any { y -> y > x } }
    }
}

/**
 * Explicit Dedekind-real wrapper with a computable cut witness.
 *
 * @property cut Underlying computable cut.
 * @property witness Witness real used for arithmetic and conversion.
 */
class DedekindReal private constructor(
    val cut: DedekindCut,
    val witness: ConstructedReal
) : Comparable<DedekindReal>, MathElement {
    /**
     * Projects this Dedekind real to kernel representation.
     *
     * @return Kernel [RealNumber].
     */
    fun toKernel(): RealNumber = witness.toKernel()

    /**
     * Approximates this Dedekind real by rational term index.
     *
     * @param index Approximation index.
     * @return Rational approximation.
     */
    fun approximateRational(index: Int = DEFAULT_DEDEKIND_PRECISION_INDEX): RationalNumber =
        witness.approximateRational(index).toKernel()

    /**
     * Adds two Dedekind reals.
     *
     * @param other Addend.
     * @return Sum.
     */
    operator fun plus(other: DedekindReal): DedekindReal = fromCauchy(CauchyReal(witness + other.witness))

    /**
     * Subtracts two Dedekind reals.
     *
     * @param other Subtrahend.
     * @return Difference.
     */
    operator fun minus(other: DedekindReal): DedekindReal = fromCauchy(CauchyReal(witness - other.witness))

    /**
     * Multiplies two Dedekind reals.
     *
     * @param other Multiplier.
     * @return Product.
     */
    operator fun times(other: DedekindReal): DedekindReal = fromCauchy(CauchyReal(witness * other.witness))

    /**
     * Divides two Dedekind reals.
     *
     * @param other Divisor.
     * @return Quotient.
     */
    operator fun div(other: DedekindReal): DedekindReal = fromCauchy(CauchyReal(witness / other.witness))

    override fun compareTo(other: DedekindReal): Int = witness.compareTo(other.witness)

    override fun equals(other: Any?): Boolean =
        other is DedekindReal && witness == other.witness

    override fun hashCode(): Int = witness.hashCode()

    override fun toString(): String = witness.toString()

    companion object {
        /**
         * Creates a Dedekind real from a Cauchy real.
         *
         * @param cauchy Cauchy representation.
         * @return Dedekind representation.
         */
        fun fromCauchy(cauchy: CauchyReal): DedekindReal = DedekindReal(
            cut = DedekindCut(cauchy.value),
            witness = cauchy.value
        )

        /**
         * Creates a Dedekind real from a kernel real.
         *
         * @param value Kernel value.
         * @return Dedekind representation.
         */
        fun fromKernel(value: RealNumber): DedekindReal = fromCauchy(CauchyReal.fromKernel(value))
    }
}

/**
 * Isomorphism utilities between Cauchy and Dedekind presentations.
 */
object RealIsomorphism {
    /**
     * Maps Cauchy -> Dedekind.
     *
     * @param value Cauchy real.
     * @return Dedekind real.
     */
    fun cauchyToDedekind(value: CauchyReal): DedekindReal = DedekindReal.fromCauchy(value)

    /**
     * Maps Dedekind -> Cauchy.
     *
     * @param value Dedekind real.
     * @return Cauchy real.
     */
    fun dedekindToCauchy(value: DedekindReal): CauchyReal = CauchyReal(value.witness)

    /**
     * Verifies roundtrip Cauchy -> Dedekind -> Cauchy on sampled kernel reals.
     *
     * @param samples Sample kernel reals.
     * @return True when roundtrip is preserved for all samples.
     */
    fun verifyRoundTrip(samples: Iterable<RealNumber>): Boolean =
        samples.all { sample ->
            val c = CauchyReal.fromKernel(sample)
            val roundTrip = dedekindToCauchy(cauchyToDedekind(c))
            roundTrip.toKernel() == sample
        }
}

/**
 * Arithmetic context for [DedekindReal].
 */
object DedekindRealArithmetic : Arithmetic<DedekindReal> {
    override val zero: DedekindReal = DedekindReal.fromKernel(RealNumber.ZERO)
    override val one: DedekindReal = DedekindReal.fromKernel(RealNumber.ONE)

    override fun add(a: DedekindReal, b: DedekindReal): DedekindReal = a + b
    override fun subtract(a: DedekindReal, b: DedekindReal): DedekindReal = a - b
    override fun multiply(a: DedekindReal, b: DedekindReal): DedekindReal = a * b
    override fun divide(a: DedekindReal, b: DedekindReal): DedekindReal = a / b
    override fun compare(a: DedekindReal, b: DedekindReal): Int = a.compareTo(b)
}

/**
 * Converts a Cauchy real wrapper to Dedekind wrapper.
 *
 * @return Dedekind representation.
 */
fun CauchyReal.toDedekindReal(): DedekindReal = RealIsomorphism.cauchyToDedekind(this)

/**
 * Converts a Dedekind real wrapper to Cauchy wrapper.
 *
 * @return Cauchy representation.
 */
fun DedekindReal.toCauchyReal(): CauchyReal = RealIsomorphism.dedekindToCauchy(this)
