package mathsets.kernel

import mathsets.kernel.platform.*
import kotlin.jvm.JvmInline

/**
 * Represents a natural number (ℕ = {0, 1, 2, ...}).
 *
 * Implemented as a value class wrapping [BigInteger] for zero-cost abstraction
 * with arbitrary-precision arithmetic. The invariant `value >= 0` is enforced
 * at construction time.
 *
 * @property value The underlying non-negative [BigInteger] representation.
 * @throws ArithmeticException if constructed with a negative value.
 * @see IntegerNumber
 * @see MathElement
 */
@JvmInline
value class NaturalNumber(val value: BigInteger) : Comparable<NaturalNumber>, MathElement {

    init {
        if (value.signum() < 0) {
            throw ArithmeticException(
                "Natural numbers must be non-negative (>= 0): $value"
            )
        }
    }

    /**
     * Adds two natural numbers.
     *
     * @param other The natural number to add.
     * @return The sum as a [NaturalNumber].
     */
    operator fun plus(other: NaturalNumber): NaturalNumber =
        NaturalNumber(this.value + other.value)

    /**
     * Multiplies two natural numbers.
     *
     * @param other The natural number to multiply by.
     * @return The product as a [NaturalNumber].
     */
    operator fun times(other: NaturalNumber): NaturalNumber =
        NaturalNumber(this.value * other.value)

    /**
     * Subtracts another natural number from this one.
     *
     * Subtraction is a partial operation in ℕ: the result is defined only when
     * `this >= other`. Otherwise, an [IllegalArgumentException] is thrown.
     *
     * @param other The natural number to subtract.
     * @return The difference as a [NaturalNumber].
     * @throws IllegalArgumentException if `this < other`.
     */
    operator fun minus(other: NaturalNumber): NaturalNumber {
        require(this >= other) { "Subtraction is partial in N: $this - $other is undefined" }
        return NaturalNumber(this.value - other.value)
    }

    /**
     * Raises this natural number to the given exponent.
     *
     * @param exponent The power to raise to; must fit in [Int] range.
     * @return The result of `this^exponent` as a [NaturalNumber].
     * @throws IllegalArgumentException if the exponent exceeds [Int.MAX_VALUE].
     */
    infix fun pow(exponent: NaturalNumber): NaturalNumber {
        require(exponent.value <= MAX_INT_BIG_INTEGER) {
            "Exponent too large for BigInteger.pow(Int): ${exponent.value}"
        }
        return NaturalNumber(this.value.pow(exponent.value.toInt()))
    }

    /**
     * Integer (Euclidean) division of natural numbers.
     *
     * @param other The divisor.
     * @return The quotient as a [NaturalNumber].
     * @throws ArithmeticException if [other] is zero.
     */
    operator fun div(other: NaturalNumber): NaturalNumber =
        NaturalNumber(this.value / other.value)

    /**
     * Remainder of integer division of natural numbers.
     *
     * @param other The divisor.
     * @return The remainder as a [NaturalNumber].
     * @throws ArithmeticException if [other] is zero.
     */
    operator fun rem(other: NaturalNumber): NaturalNumber =
        NaturalNumber(this.value % other.value)

    override fun compareTo(other: NaturalNumber): Int =
        this.value.compareTo(other.value)

    /**
     * Returns the successor of this natural number (n + 1).
     *
     * @return The next [NaturalNumber] in the Peano sequence.
     */
    fun succ(): NaturalNumber = NaturalNumber(this.value + BI_ONE)

    /**
     * Returns the predecessor of this natural number (n - 1).
     *
     * @return The previous [NaturalNumber] in the Peano sequence.
     * @throws ArithmeticException if this number is zero.
     */
    fun pred(): NaturalNumber {
        if (isZero()) throw ArithmeticException("Predecessor of 0 is undefined in N")
        return NaturalNumber(this.value - BI_ONE)
    }

    /**
     * Checks whether this natural number is zero.
     *
     * @return `true` if this number equals 0.
     */
    fun isZero(): Boolean = this.value == BI_ZERO

    /**
     * Checks whether this natural number is even.
     *
     * @return `true` if this number is divisible by 2.
     */
    fun isEven(): Boolean = (this.value % 2.toBigInteger()) == BI_ZERO

    /**
     * Checks whether this natural number is odd.
     *
     * @return `true` if this number is not divisible by 2.
     */
    fun isOdd(): Boolean = !isEven()

    /**
     * Tests primality using trial division with 6k ± 1 optimization.
     *
     * @return `true` if this natural number is a prime number.
     */
    fun isPrime(): Boolean {
        if (value < TWO_BIG_INTEGER) return false
        if (value == TWO_BIG_INTEGER || value == THREE_BIG_INTEGER) return true
        if (value % TWO_BIG_INTEGER == BI_ZERO || value % THREE_BIG_INTEGER == BI_ZERO) return false

        var i = bigIntegerOf(5)
        var step = bigIntegerOf(2)
        while (i * i <= value) {
            if (value % i == BI_ZERO) return false
            i += step
            step = if (step == bigIntegerOf(2)) bigIntegerOf(4) else bigIntegerOf(2)
        }
        return true
    }

    override fun toString(): String = value.toString()

    companion object {
        /** The natural number 0. */
        val ZERO = NaturalNumber(BI_ZERO)

        /** The natural number 1. */
        val ONE = NaturalNumber(BI_ONE)

        private val TWO_BIG_INTEGER = bigIntegerOf(2)
        private val THREE_BIG_INTEGER = bigIntegerOf(3)
        private val MAX_INT_BIG_INTEGER = bigIntegerOf(Int.MAX_VALUE)

        /**
         * Creates a [NaturalNumber] from a [BigInteger].
         *
         * @param value A non-negative [BigInteger].
         * @return The corresponding [NaturalNumber].
         * @throws IllegalArgumentException if [value] is negative.
         */
        fun of(value: BigInteger): NaturalNumber {
            require(value >= BI_ZERO) { "NaturalNumber must be non-negative: $value" }
            return NaturalNumber(value)
        }

        /**
         * Creates a [NaturalNumber] from a [Long].
         *
         * @param value A non-negative [Long].
         * @return The corresponding [NaturalNumber].
         * @throws IllegalArgumentException if [value] is negative.
         */
        fun of(value: Long): NaturalNumber {
            require(value >= 0) { "NaturalNumber must be non-negative: $value" }
            return NaturalNumber(bigIntegerOf(value))
        }

        /**
         * Creates a [NaturalNumber] from an [Int].
         *
         * @param value A non-negative [Int].
         * @return The corresponding [NaturalNumber].
         * @throws IllegalArgumentException if [value] is negative.
         */
        fun of(value: Int): NaturalNumber {
            require(value >= 0) { "NaturalNumber must be non-negative: $value" }
            return NaturalNumber(bigIntegerOf(value))
        }

        /**
         * Parses a [NaturalNumber] from its string representation.
         *
         * @param string A string representing a non-negative integer.
         * @return The corresponding [NaturalNumber].
         * @throws IllegalArgumentException if the parsed value is negative.
         * @throws NumberFormatException if [string] is not a valid integer.
         */
        fun parse(string: String): NaturalNumber {
            val bigInt = bigIntegerOf(string)
            return of(bigInt)
        }
    }
}
