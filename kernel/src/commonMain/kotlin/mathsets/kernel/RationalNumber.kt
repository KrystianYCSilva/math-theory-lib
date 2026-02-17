package mathsets.kernel

import mathsets.kernel.platform.BI_ONE
import mathsets.kernel.platform.BI_ZERO
import mathsets.kernel.platform.BigInteger
import mathsets.kernel.platform.bigIntegerOf
import kotlin.jvm.JvmInline

/**
 * Represents a rational number (ℚ = { p/q | p ∈ ℤ, q ∈ ℤ, q ≠ 0 }).
 *
 * Internally stores a (numerator, denominator) pair that is always kept in
 * normalized (reduced) form with a positive denominator. The normalization is
 * performed via GCD reduction at construction time through the private [create]
 * factory method.
 *
 * Implemented as a value class wrapping a [Pair] of [BigInteger] values.
 *
 * @see IntegerNumber
 * @see RealNumber
 * @see MathElement
 */
@JvmInline
value class RationalNumber private constructor(private val packed: Pair<BigInteger, BigInteger>) :
    Comparable<RationalNumber>, MathElement {

    /** The numerator of this rational number (may be negative). */
    val numerator: BigInteger get() = packed.first

    /** The denominator of this rational number (always positive after normalization). */
    val denominator: BigInteger get() = packed.second

    /**
     * Returns the numerator as an [IntegerNumber].
     *
     * @return The numerator wrapped in [IntegerNumber].
     */
    fun numeratorAsInteger(): IntegerNumber = IntegerNumber.parse(numerator.toString())

    /**
     * Returns the denominator as an [IntegerNumber].
     *
     * @return The denominator wrapped in [IntegerNumber].
     */
    fun denominatorAsInteger(): IntegerNumber = IntegerNumber.parse(denominator.toString())

    /**
     * Adds two rational numbers using cross-multiplication: a/b + c/d = (ad + bc) / bd.
     *
     * @param other The rational number to add.
     * @return The normalized sum as a [RationalNumber].
     */
    operator fun plus(other: RationalNumber): RationalNumber {
        // a/b + c/d = (ad + bc) / bd
        val num = (this.numerator * other.denominator) + (this.denominator * other.numerator)
        val den = this.denominator * other.denominator
        return create(num, den)
    }

    /**
     * Subtracts another rational number from this one.
     *
     * @param other The rational number to subtract.
     * @return The normalized difference as a [RationalNumber].
     */
    operator fun minus(other: RationalNumber): RationalNumber {
        val num = (this.numerator * other.denominator) - (this.denominator * other.numerator)
        val den = this.denominator * other.denominator
        return create(num, den)
    }

    /**
     * Multiplies two rational numbers: (a/b) * (c/d) = ac / bd.
     *
     * @param other The rational number to multiply by.
     * @return The normalized product as a [RationalNumber].
     */
    operator fun times(other: RationalNumber): RationalNumber {
        return create(
            this.numerator * other.numerator,
            this.denominator * other.denominator
        )
    }

    /**
     * Divides this rational number by another: (a/b) / (c/d) = ad / bc.
     *
     * @param other The divisor rational number.
     * @return The normalized quotient as a [RationalNumber].
     * @throws ArithmeticException if [other] is zero.
     */
    operator fun div(other: RationalNumber): RationalNumber {
        if (other.numerator == BI_ZERO) throw ArithmeticException("Division by zero")
        return create(
            this.numerator * other.denominator,
            this.denominator * other.numerator
        )
    }

    /**
     * Negates this rational number.
     *
     * @return The additive inverse of this [RationalNumber].
     */
    operator fun unaryMinus(): RationalNumber = RationalNumber(Pair(-numerator, denominator))

    override fun compareTo(other: RationalNumber): Int {
        val left = this.numerator * other.denominator
        val right = this.denominator * other.numerator
        return left.compareTo(right)
    }

    override fun toString(): String {
        return if (denominator == BI_ONE) numerator.toString() else "$numerator/$denominator"
    }

    companion object {
        /** The rational number 0/1. */
        val ZERO = RationalNumber(Pair(BI_ZERO, BI_ONE))

        /** The rational number 1/1. */
        val ONE = RationalNumber(Pair(BI_ONE, BI_ONE))

        /**
         * Creates a [RationalNumber] from [BigInteger] numerator and denominator.
         *
         * @param numerator The numerator value.
         * @param denominator The denominator value (defaults to 1).
         * @return A normalized [RationalNumber].
         * @throws ArithmeticException if [denominator] is zero.
         */
        fun of(numerator: BigInteger, denominator: BigInteger = BI_ONE): RationalNumber {
            return create(numerator, denominator)
        }

        /**
         * Creates a [RationalNumber] from [Int] numerator and denominator.
         *
         * @param numerator The numerator value.
         * @param denominator The denominator value (defaults to 1).
         * @return A normalized [RationalNumber].
         * @throws ArithmeticException if [denominator] is zero.
         */
        fun of(numerator: Int, denominator: Int = 1): RationalNumber {
            return create(bigIntegerOf(numerator), bigIntegerOf(denominator))
        }

        /**
         * Creates a [RationalNumber] from [IntegerNumber] numerator and denominator.
         *
         * @param numerator The numerator as an [IntegerNumber].
         * @param denominator The denominator as an [IntegerNumber] (defaults to [IntegerNumber.ONE]).
         * @return A normalized [RationalNumber].
         * @throws ArithmeticException if [denominator] is zero.
         */
        fun of(numerator: IntegerNumber, denominator: IntegerNumber = IntegerNumber.ONE): RationalNumber {
            return create(numerator.value, denominator.value)
        }

        private fun create(numerator: BigInteger, denominator: BigInteger): RationalNumber {
            if (denominator == BI_ZERO) throw ArithmeticException("Denominator cannot be zero")

            var num = numerator
            var den = denominator

            if (den < BI_ZERO) {
                num = -num
                den = -den
            }

            val common = gcd(num.abs(), den)
            return RationalNumber(Pair(num / common, den / common))
        }

        private fun gcd(a: BigInteger, b: BigInteger): BigInteger {
            var x = a
            var y = b
            while (y != BI_ZERO) {
                val temp = y
                y = x % y
                x = temp
            }
            return x
        }
    }
}
