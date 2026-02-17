package mathsets.construction.rational

import mathsets.construction.integer.ConstructedInteger
import mathsets.construction.integer.ConstructedIntegerIsomorphism
import mathsets.construction.integer.toMathInteger
import mathsets.kernel.Arithmetic
import mathsets.kernel.IntegerNumber
import mathsets.kernel.MathElement
import mathsets.kernel.NaturalNumber
import mathsets.kernel.RationalNumber

/**
 * Axiomatic construction of the rational numbers as equivalence classes of pairs
 * of constructed integers under the relation:
 *
 * `(a, b) ~ (c, d)  <=>  a * d = b * c`  with `b != 0` and `d != 0`.
 *
 * The pair `(a, b)` represents the fraction `a / b`. Equality and hashing are
 * defined by the canonical kernel value, so distinct representatives of the
 * same equivalence class compare as equal.
 *
 * @property numerator the numerator of the representative pair.
 * @property denominator the denominator of the representative pair (never zero).
 * @see ConstructedRationalArithmetic
 * @see ConstructedRationalIsomorphism
 */
class ConstructedRational private constructor(
    val numerator: ConstructedInteger,
    val denominator: ConstructedInteger,
    private val kernelValue: RationalNumber
) : Comparable<ConstructedRational>, MathElement {
    /**
     * Returns the representative pair `(numerator, denominator)` of this equivalence class.
     */
    fun representative(): Pair<ConstructedInteger, ConstructedInteger> = numerator to denominator

    /**
     * Projects this constructed rational to its kernel [RationalNumber] counterpart.
     */
    fun toKernel(): RationalNumber = kernelValue

    /**
     * Adds two rationals: `a/b + c/d = (a*d + b*c) / (b*d)`.
     *
     * @param other the addend.
     * @return the sum.
     */
    operator fun plus(other: ConstructedRational): ConstructedRational {
        val ad = numerator * other.denominator
        val bc = other.numerator * denominator
        val bd = denominator * other.denominator
        return of(ad + bc, bd)
    }

    /**
     * Subtracts [other] from this rational: `this - other = this + (-other)`.
     *
     * @param other the subtrahend.
     * @return the difference.
     */
    operator fun minus(other: ConstructedRational): ConstructedRational = this + (-other)

    /**
     * Returns the additive inverse: `-(a/b) = (-a)/b`.
     */
    operator fun unaryMinus(): ConstructedRational = of(-numerator, denominator)

    /**
     * Multiplies two rationals: `(a/b) * (c/d) = (a*c) / (b*d)`.
     *
     * @param other the multiplier.
     * @return the product.
     */
    operator fun times(other: ConstructedRational): ConstructedRational =
        of(numerator * other.numerator, denominator * other.denominator)

    /**
     * Divides this rational by [other]: `(a/b) / (c/d) = (a*d) / (b*c)`.
     *
     * @param other the divisor (must not be zero).
     * @return the quotient.
     * @throws IllegalArgumentException if [other] is zero.
     */
    operator fun div(other: ConstructedRational): ConstructedRational {
        require(other.numerator != ConstructedInteger.ZERO) { "Division by zero rational." }
        return of(numerator * other.denominator, denominator * other.numerator)
    }

    override fun compareTo(other: ConstructedRational): Int = kernelValue.compareTo(other.kernelValue)

    override fun equals(other: Any?): Boolean =
        other is ConstructedRational && kernelValue == other.kernelValue

    override fun hashCode(): Int = kernelValue.hashCode()

    override fun toString(): String = "[$numerator/$denominator]~($kernelValue)"

    companion object {
        /** The constructed rational zero `0/1`. */
        val ZERO: ConstructedRational = fromKernel(RationalNumber.ZERO)

        /** The constructed rational one `1/1`. */
        val ONE: ConstructedRational = fromKernel(RationalNumber.ONE)

        /**
         * Creates a [ConstructedRational] from a numerator and denominator.
         *
         * @param numerator the numerator.
         * @param denominator the denominator (must not be zero).
         * @return the constructed rational `numerator / denominator`.
         * @throws IllegalArgumentException if [denominator] is zero.
         */
        fun of(numerator: ConstructedInteger, denominator: ConstructedInteger): ConstructedRational {
            require(denominator != ConstructedInteger.ZERO) { "Rational denominator cannot be zero." }
            val kernel = RationalNumber.of(numerator.toKernel(), denominator.toKernel())
            return ConstructedRational(numerator, denominator, kernel)
        }

        /**
         * Creates a [ConstructedRational] from a kernel [RationalNumber].
         *
         * @param value the kernel rational.
         * @return the corresponding constructed rational.
         */
        fun fromKernel(value: RationalNumber): ConstructedRational {
            val numerator = ConstructedIntegerIsomorphism.fromKernel(value.numeratorAsInteger())
            val denominator = ConstructedIntegerIsomorphism.fromKernel(value.denominatorAsInteger())
            return of(numerator, denominator)
        }

        /**
         * Tests whether two pairs of constructed integers represent the same rational:
         * `(a, b) ~ (c, d) <=> a * d = b * c`.
         *
         * @param p the first pair `(numerator, denominator)`.
         * @param q the second pair `(numerator, denominator)`.
         * @return `true` if the pairs are equivalent.
         * @throws IllegalArgumentException if either denominator is zero.
         */
        fun areEquivalent(
            p: Pair<ConstructedInteger, ConstructedInteger>,
            q: Pair<ConstructedInteger, ConstructedInteger>
        ): Boolean {
            val (a, b) = p
            val (c, d) = q
            require(b != ConstructedInteger.ZERO && d != ConstructedInteger.ZERO) {
                "Equivalence requires non-zero denominators."
            }
            return (a * d) == (b * c)
        }
    }
}

/**
 * [Arithmetic] implementation for [ConstructedRational].
 *
 * Delegates to the operator overloads defined on [ConstructedRational].
 */
object ConstructedRationalArithmetic : Arithmetic<ConstructedRational> {
    /** The additive identity. */
    override val zero: ConstructedRational = ConstructedRational.ZERO

    /** The multiplicative identity. */
    override val one: ConstructedRational = ConstructedRational.ONE

    override fun add(a: ConstructedRational, b: ConstructedRational): ConstructedRational = a + b
    override fun subtract(a: ConstructedRational, b: ConstructedRational): ConstructedRational = a - b
    override fun multiply(a: ConstructedRational, b: ConstructedRational): ConstructedRational = a * b
    override fun divide(a: ConstructedRational, b: ConstructedRational): ConstructedRational = a / b
    override fun compare(a: ConstructedRational, b: ConstructedRational): Int = a.compareTo(b)
}

/**
 * Total order on [ConstructedRational] delegating to the natural [Comparable] implementation.
 */
object ConstructedRationalOrder {
    /** Returns `true` if `a <= b`. */
    fun lessOrEqual(a: ConstructedRational, b: ConstructedRational): Boolean = a <= b

    /** Returns `true` if `a < b`. */
    fun lessThan(a: ConstructedRational, b: ConstructedRational): Boolean = a < b

    /** Returns `true` if `a >= b`. */
    fun greaterOrEqual(a: ConstructedRational, b: ConstructedRational): Boolean = a >= b

    /** Returns `true` if `a > b`. */
    fun greaterThan(a: ConstructedRational, b: ConstructedRational): Boolean = a > b
}

/**
 * Canonical embedding of constructed integers into constructed rationals.
 *
 * Every integer `z` is embedded as `z / 1`.
 */
object IntegerRationalEmbedding {
    /**
     * Embeds a [ConstructedInteger] into [ConstructedRational] as `integer / 1`.
     *
     * @param integer the constructed integer to embed.
     * @return the corresponding constructed rational.
     */
    fun embed(integer: ConstructedInteger): ConstructedRational =
        ConstructedRational.of(integer, ConstructedInteger.ONE)

    /**
     * Embeds a kernel [IntegerNumber] into [ConstructedRational].
     *
     * @param integer the kernel integer to embed.
     * @return the corresponding constructed rational.
     */
    fun embed(integer: IntegerNumber): ConstructedRational =
        embed(integer.toMathInteger())
}

/**
 * Isomorphism between [ConstructedRational] and the kernel [RationalNumber],
 * with round-trip verification.
 */
object ConstructedRationalIsomorphism {
    /**
     * Projects a [ConstructedRational] to its kernel [RationalNumber].
     */
    fun toKernel(value: ConstructedRational): RationalNumber = value.toKernel()

    /**
     * Lifts a kernel [RationalNumber] to a [ConstructedRational].
     */
    fun fromKernel(value: RationalNumber): ConstructedRational = ConstructedRational.fromKernel(value)

    /**
     * Verifies the round-trip property for all rationals `n/d` with
     * `n` in `-range..range` and `d` in `1..range`.
     *
     * @param range the bound for numerator and denominator.
     * @return `true` if the round-trip holds for every value.
     */
    fun verifyRoundTrip(range: Int): Boolean {
        for (n in -range..range) {
            for (d in 1..range) {
                val kernel = RationalNumber.of(n, d)
                val roundTrip = toKernel(fromKernel(kernel))
                if (roundTrip != kernel) return false
            }
        }
        return true
    }
}

/**
 * Utility for demonstrating the density of the rationals.
 *
 * Between any two distinct rationals there exists another rational (their midpoint).
 */
object RationalDensity {
    private val TWO: ConstructedRational =
        ConstructedRational.of(ConstructedInteger.fromKernel(IntegerNumber.of(2)), ConstructedInteger.ONE)

    /**
     * Returns a rational strictly between [a] and [b] (their arithmetic mean).
     *
     * @param a the lower bound.
     * @param b the upper bound.
     * @return `(a + b) / 2`.
     */
    fun between(a: ConstructedRational, b: ConstructedRational): ConstructedRational =
        (a + b) / TWO
}

/**
 * Extension to embed a [ConstructedInteger] into [ConstructedRational].
 */
fun ConstructedInteger.toMathRational(): ConstructedRational = IntegerRationalEmbedding.embed(this)

/**
 * Extension to embed a kernel [IntegerNumber] into [ConstructedRational].
 */
fun IntegerNumber.toMathRational(): ConstructedRational = IntegerRationalEmbedding.embed(this)

/**
 * Extension to embed a kernel [NaturalNumber] into [ConstructedRational].
 */
fun NaturalNumber.toMathRational(): ConstructedRational = this.toMathInteger().toMathRational()
