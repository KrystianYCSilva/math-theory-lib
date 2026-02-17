package mathsets.kernel

import mathsets.kernel.platform.*
import mathsets.kernel.platform.BigDecimal
import kotlin.jvm.JvmInline

/**
 * Represents a computational real number (ℝ) backed by [BigDecimal].
 *
 * This representation covers exactly those values expressible as finite decimals.
 * Exact irrational values require symbolic representation (see [IrrationalNumber]).
 *
 * Implemented as a value class wrapping [BigDecimal] for multiplatform
 * arbitrary-precision decimal arithmetic.
 *
 * @property value The underlying [BigDecimal] representation.
 * @see IrrationalNumber
 * @see ExtendedReal
 * @see ComplexNumber
 * @see MathElement
 */
@JvmInline
value class RealNumber(val value: BigDecimal) : Comparable<RealNumber>, MathElement {

    /**
     * Adds two real numbers.
     *
     * @param other The real number to add.
     * @return The sum as a [RealNumber].
     */
    operator fun plus(other: RealNumber): RealNumber = RealNumber(value + other.value)

    /**
     * Subtracts another real number from this one.
     *
     * @param other The real number to subtract.
     * @return The difference as a [RealNumber].
     */
    operator fun minus(other: RealNumber): RealNumber = RealNumber(value - other.value)

    /**
     * Multiplies two real numbers.
     *
     * @param other The real number to multiply by.
     * @return The product as a [RealNumber].
     */
    operator fun times(other: RealNumber): RealNumber = RealNumber(value * other.value)

    /**
     * Divides this real number by another.
     *
     * @param other The divisor.
     * @return The quotient as a [RealNumber].
     * @throws IllegalArgumentException if [other] is zero.
     */
    operator fun div(other: RealNumber): RealNumber {
        require(!other.isZero()) { "Division by zero in RealNumber." }
        return RealNumber(value / other.value)
    }

    /**
     * Negates this real number.
     *
     * @return The additive inverse of this [RealNumber].
     */
    operator fun unaryMinus(): RealNumber = RealNumber(-value)

    /**
     * Raises this real number to a non-negative integer exponent.
     *
     * @param exponent The non-negative integer power.
     * @return The result of `this^exponent` as a [RealNumber].
     * @throws IllegalArgumentException if [exponent] is negative.
     */
    infix fun pow(exponent: Int): RealNumber {
        require(exponent >= 0) { "Negative exponent is not supported in this kernel operation." }
        return RealNumber(value.pow(exponent))
    }

    /**
     * Returns the absolute value of this real number.
     *
     * @return A non-negative [RealNumber] with the same magnitude.
     */
    fun abs(): RealNumber = RealNumber(value.abs())

    /**
     * Checks whether this real number is zero.
     *
     * @return `true` if this number equals 0.
     */
    fun isZero(): Boolean = value == BD_ZERO

    /**
     * Returns the signum function of this real number.
     *
     * @return 1 if positive, -1 if negative, 0 if zero.
     */
    fun signum(): Int = when {
        value > BD_ZERO -> 1
        value < BD_ZERO -> -1
        else -> 0
    }

    override fun compareTo(other: RealNumber): Int = value.compareTo(other.value)

    override fun toString(): String = value.toString()

    companion object {
        /** The real number 0. */
        val ZERO: RealNumber = RealNumber(BD_ZERO)

        /** The real number 1. */
        val ONE: RealNumber = RealNumber(BD_ONE)

        /** The real number 2. */
        val TWO: RealNumber = RealNumber(BD_TWO)

        /**
         * Creates a [RealNumber] from a [BigDecimal].
         *
         * @param value The [BigDecimal] value.
         * @return The corresponding [RealNumber].
         */
        fun of(value: BigDecimal): RealNumber = RealNumber(value)

        /**
         * Creates a [RealNumber] from a [BigInteger] by converting to [BigDecimal].
         *
         * @param value The [BigInteger] value.
         * @return The corresponding [RealNumber].
         */
        fun of(value: BigInteger): RealNumber = RealNumber(BigDecimal.fromBigInteger(value))

        /**
         * Creates a [RealNumber] from a [RationalNumber] by dividing numerator by denominator.
         *
         * @param value The [RationalNumber] to convert.
         * @return The corresponding [RealNumber] (decimal approximation if non-terminating).
         */
        fun of(value: RationalNumber): RealNumber {
            val numerator = BigDecimal.fromBigInteger(value.numerator)
            val denominator = BigDecimal.fromBigInteger(value.denominator)
            return RealNumber(numerator / denominator)
        }

        /**
         * Creates a [RealNumber] from an [Int].
         *
         * @param value The [Int] value.
         * @return The corresponding [RealNumber].
         */
        fun of(value: Int): RealNumber = RealNumber(bigDecimalOf(value))

        /**
         * Creates a [RealNumber] from a [Long].
         *
         * @param value The [Long] value.
         * @return The corresponding [RealNumber].
         */
        fun of(value: Long): RealNumber = RealNumber(bigDecimalOf(value))

        /**
         * Creates a [RealNumber] from a [Float].
         *
         * @param value The [Float] value.
         * @return The corresponding [RealNumber].
         */
        fun of(value: Float): RealNumber = RealNumber(bigDecimalOf(value))

        /**
         * Creates a [RealNumber] from a [Double].
         *
         * @param value The [Double] value.
         * @return The corresponding [RealNumber].
         */
        fun of(value: Double): RealNumber = RealNumber(bigDecimalOf(value))

        /**
         * Parses a [RealNumber] from its string representation.
         *
         * @param value A string representing a decimal number (leading/trailing whitespace is trimmed).
         * @return The corresponding [RealNumber].
         * @throws NumberFormatException if [value] is not a valid decimal.
         */
        fun parse(value: String): RealNumber = RealNumber(bigDecimalOf(value.trim()))
    }
}
