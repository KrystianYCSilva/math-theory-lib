package mathsets.kernel

import mathsets.kernel.platform.BI_ONE
import mathsets.kernel.platform.BI_ZERO
import mathsets.kernel.platform.BigInteger
import mathsets.kernel.platform.bigIntegerOf
import kotlin.jvm.JvmInline

/**
 * Represents an integer number (ℤ = {..., -2, -1, 0, 1, 2, ...}).
 *
 * Implemented as a value class wrapping [BigInteger] for zero-cost abstraction
 * with arbitrary-precision arithmetic. Unlike [NaturalNumber], no sign constraint
 * is imposed.
 *
 * @property value The underlying [BigInteger] representation.
 * @see NaturalNumber
 * @see RationalNumber
 * @see MathElement
 */
@JvmInline
value class IntegerNumber(val value: BigInteger) : Comparable<IntegerNumber>, MathElement {

    /**
     * Adds two integer numbers.
     *
     * @param other The integer to add.
     * @return The sum as an [IntegerNumber].
     */
    operator fun plus(other: IntegerNumber): IntegerNumber =
        IntegerNumber(this.value + other.value)

    /**
     * Subtracts another integer from this one.
     *
     * @param other The integer to subtract.
     * @return The difference as an [IntegerNumber].
     */
    operator fun minus(other: IntegerNumber): IntegerNumber =
        IntegerNumber(this.value - other.value)

    /**
     * Multiplies two integer numbers.
     *
     * @param other The integer to multiply by.
     * @return The product as an [IntegerNumber].
     */
    operator fun times(other: IntegerNumber): IntegerNumber =
        IntegerNumber(this.value * other.value)

    /**
     * Integer (truncated) division of two integers.
     *
     * @param other The divisor.
     * @return The quotient as an [IntegerNumber].
     * @throws ArithmeticException if [other] is zero.
     */
    operator fun div(other: IntegerNumber): IntegerNumber =
        IntegerNumber(this.value / other.value)

    /**
     * Negates this integer number.
     *
     * @return The additive inverse of this [IntegerNumber].
     */
    operator fun unaryMinus(): IntegerNumber =
        IntegerNumber(-this.value)

    /**
     * Returns the absolute value of this integer.
     *
     * @return A non-negative [IntegerNumber] with the same magnitude.
     */
    fun abs(): IntegerNumber = IntegerNumber(this.value.abs())

    /**
     * Converts this integer to a [NaturalNumber], returning `null` if negative.
     *
     * @return The corresponding [NaturalNumber], or `null` if `this < 0`.
     */
    fun toNaturalOrNull(): NaturalNumber? =
        if (this < ZERO) null else NaturalNumber.parse(this.toString())

    /**
     * Returns the absolute value of this integer as a [NaturalNumber].
     *
     * @return A [NaturalNumber] equal to `|this|`.
     */
    fun absNatural(): NaturalNumber = NaturalNumber.parse(this.abs().toString())

    override fun compareTo(other: IntegerNumber): Int =
        this.value.compareTo(other.value)

    override fun toString(): String = value.toString()

    companion object {
        /** The integer 0. */
        val ZERO = IntegerNumber(BI_ZERO)

        /** The integer 1. */
        val ONE = IntegerNumber(BI_ONE)

        /** The integer -1. */
        val MINUS_ONE = IntegerNumber(-BI_ONE)

        /**
         * Creates an [IntegerNumber] from a [BigInteger].
         *
         * @param value The [BigInteger] value.
         * @return The corresponding [IntegerNumber].
         */
        fun of(value: BigInteger): IntegerNumber = IntegerNumber(value)

        /**
         * Creates an [IntegerNumber] from a [Long].
         *
         * @param value The [Long] value.
         * @return The corresponding [IntegerNumber].
         */
        fun of(value: Long): IntegerNumber = IntegerNumber(bigIntegerOf(value))

        /**
         * Creates an [IntegerNumber] from an [Int].
         *
         * @param value The [Int] value.
         * @return The corresponding [IntegerNumber].
         */
        fun of(value: Int): IntegerNumber = IntegerNumber(bigIntegerOf(value))

        /**
         * Parses an [IntegerNumber] from its string representation.
         *
         * @param string A string representing an integer.
         * @return The corresponding [IntegerNumber].
         * @throws NumberFormatException if [string] is not a valid integer.
         */
        fun parse(string: String): IntegerNumber = IntegerNumber(bigIntegerOf(string))
    }
}
