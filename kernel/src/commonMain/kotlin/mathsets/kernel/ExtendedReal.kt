package mathsets.kernel

/**
 * The extended real number line (ℝ* = ℝ ∪ {-∞, +∞}) with an additional
 * [Indeterminate] case for undefined arithmetic forms.
 *
 * This sealed interface models the four cases of extended real arithmetic:
 * - [Finite]: a concrete [RealNumber] value.
 * - [PositiveInfinity]: represents +∞.
 * - [NegativeInfinity]: represents -∞.
 * - [Indeterminate]: represents undefined forms such as ∞ - ∞, 0 * ∞, 0/0, ∞/∞.
 *
 * Arithmetic follows standard extended real conventions where indeterminate
 * forms propagate as [Indeterminate].
 *
 * @see RealNumber
 * @see MathElement
 */
sealed interface ExtendedReal : Comparable<ExtendedReal>, MathElement {

    /**
     * A finite extended real wrapping a concrete [RealNumber].
     *
     * @property value The underlying [RealNumber].
     */
    data class Finite(val value: RealNumber) : ExtendedReal

    /** Positive infinity (+∞). */
    data object PositiveInfinity : ExtendedReal

    /** Negative infinity (-∞). */
    data object NegativeInfinity : ExtendedReal

    /**
     * Indeterminate result for undefined arithmetic forms (e.g., ∞ - ∞, 0/0).
     */
    data object Indeterminate : ExtendedReal

    /**
     * Checks whether this extended real is a finite value.
     *
     * @return `true` if this is a [Finite] instance.
     */
    fun isFinite(): Boolean = this is Finite

    /**
     * Checks whether this extended real is infinite (+∞ or -∞).
     *
     * @return `true` if this is [PositiveInfinity] or [NegativeInfinity].
     */
    fun isInfinite(): Boolean = this is PositiveInfinity || this is NegativeInfinity

    /**
     * Extracts the underlying [RealNumber] if finite, or returns `null` otherwise.
     *
     * @return The [RealNumber] value, or `null` if infinite or indeterminate.
     */
    fun toRealOrNull(): RealNumber? = (this as? Finite)?.value

    /**
     * Negates this extended real value.
     *
     * @return The negated value: -∞ becomes +∞, +∞ becomes -∞, finite values are negated,
     *         and [Indeterminate] remains [Indeterminate].
     */
    operator fun unaryMinus(): ExtendedReal = when (this) {
        is Finite -> Finite(-value)
        PositiveInfinity -> NegativeInfinity
        NegativeInfinity -> PositiveInfinity
        Indeterminate -> Indeterminate
    }

    /**
     * Adds two extended reals with standard infinity arithmetic rules.
     *
     * Notable: +∞ + (-∞) = [Indeterminate].
     *
     * @param other The extended real to add.
     * @return The sum as an [ExtendedReal].
     */
    operator fun plus(other: ExtendedReal): ExtendedReal = when {
        this is Indeterminate || other is Indeterminate -> Indeterminate
        this is PositiveInfinity && other is NegativeInfinity -> Indeterminate
        this is NegativeInfinity && other is PositiveInfinity -> Indeterminate
        this is PositiveInfinity || other is PositiveInfinity -> PositiveInfinity
        this is NegativeInfinity || other is NegativeInfinity -> NegativeInfinity
        this is Finite && other is Finite -> Finite(this.value + other.value)
        else -> Indeterminate
    }

    /**
     * Subtracts another extended real: a - b = a + (-b).
     *
     * @param other The extended real to subtract.
     * @return The difference as an [ExtendedReal].
     */
    operator fun minus(other: ExtendedReal): ExtendedReal = this + (-other)

    /**
     * Multiplies two extended reals with standard infinity arithmetic rules.
     *
     * Notable: 0 * ∞ = [Indeterminate].
     *
     * @param other The extended real to multiply by.
     * @return The product as an [ExtendedReal].
     */
    operator fun times(other: ExtendedReal): ExtendedReal {
        if (this is Indeterminate || other is Indeterminate) return Indeterminate

        if ((this is Finite && this.value.isZero() && other.isInfinite()) ||
            (other is Finite && other.value.isZero() && this.isInfinite())
        ) {
            return Indeterminate
        }

        if (this is Finite && other is Finite) {
            return Finite(this.value * other.value)
        }

        val leftSign = this.signumForInfinityArithmetic()
        val rightSign = other.signumForInfinityArithmetic()

        if (leftSign == 0 || rightSign == 0) return Indeterminate

        return if (leftSign * rightSign > 0) PositiveInfinity else NegativeInfinity
    }

    /**
     * Divides this extended real by another with standard infinity arithmetic rules.
     *
     * Notable: 0/0 = [Indeterminate], ∞/∞ = [Indeterminate], finite/∞ = 0.
     *
     * @param other The divisor extended real.
     * @return The quotient as an [ExtendedReal].
     */
    operator fun div(other: ExtendedReal): ExtendedReal {
        if (this is Indeterminate || other is Indeterminate) return Indeterminate

        if (this is Finite && other is Finite) {
            if (other.value.isZero()) {
                if (this.value.isZero()) return Indeterminate
                return if (this.value.signum() > 0) PositiveInfinity else NegativeInfinity
            }
            return Finite(this.value / other.value)
        }

        if (this.isInfinite() && other.isInfinite()) return Indeterminate

        if (this.isInfinite() && other is Finite) {
            if (other.value.isZero()) return Indeterminate
            val sign = this.signumForInfinityArithmetic() * other.value.signum()
            return if (sign > 0) PositiveInfinity else NegativeInfinity
        }

        if (this is Finite && other.isInfinite()) {
            return Finite(RealNumber.ZERO)
        }

        return Indeterminate
    }

    override fun compareTo(other: ExtendedReal): Int {
        require(this !is Indeterminate && other !is Indeterminate) {
            "Indeterminate values are not ordered."
        }

        return when {
            this is PositiveInfinity && other is PositiveInfinity -> 0
            this is NegativeInfinity && other is NegativeInfinity -> 0
            this is PositiveInfinity -> 1
            other is PositiveInfinity -> -1
            this is NegativeInfinity -> -1
            other is NegativeInfinity -> 1
            this is Finite && other is Finite -> this.value.compareTo(other.value)
            else -> 0
        }
    }

    companion object {
        /** The extended real 0. */
        val ZERO: ExtendedReal = Finite(RealNumber.ZERO)

        /** The extended real 1. */
        val ONE: ExtendedReal = Finite(RealNumber.ONE)

        /** Positive infinity (+∞). */
        val POSITIVE_INFINITY: ExtendedReal = PositiveInfinity

        /** Negative infinity (-∞). */
        val NEGATIVE_INFINITY: ExtendedReal = NegativeInfinity

        /** The indeterminate form. */
        val INDETERMINATE: ExtendedReal = Indeterminate

        /**
         * Wraps a [RealNumber] as a finite [ExtendedReal].
         *
         * @param value The real number to wrap.
         * @return A [Finite] extended real.
         */
        fun of(value: RealNumber): ExtendedReal = Finite(value)

        /**
         * Converts a [RationalNumber] to a finite [ExtendedReal].
         *
         * @param value The rational number to convert.
         * @return A [Finite] extended real.
         */
        fun of(value: RationalNumber): ExtendedReal = Finite(RealNumber.of(value))

        /**
         * Creates a finite [ExtendedReal] from an [Int].
         *
         * @param value The integer value.
         * @return A [Finite] extended real.
         */
        fun of(value: Int): ExtendedReal = Finite(RealNumber.of(value))

        /**
         * Creates a finite [ExtendedReal] from a [Long].
         *
         * @param value The long value.
         * @return A [Finite] extended real.
         */
        fun of(value: Long): ExtendedReal = Finite(RealNumber.of(value))

        /**
         * Creates a finite [ExtendedReal] from a [Double].
         *
         * @param value The double value.
         * @return A [Finite] extended real.
         */
        fun of(value: Double): ExtendedReal = Finite(RealNumber.of(value))

        /**
         * Parses a finite [ExtendedReal] from a decimal string.
         *
         * @param value A string representing a decimal number.
         * @return A [Finite] extended real.
         * @throws NumberFormatException if [value] is not a valid decimal.
         */
        fun parse(value: String): ExtendedReal = Finite(RealNumber.parse(value))
    }
}

/**
 * Extension function to lift a [RealNumber] into the [ExtendedReal] domain.
 *
 * @return A [ExtendedReal.Finite] wrapping this real number.
 */
fun RealNumber.toExtendedReal(): ExtendedReal = ExtendedReal.of(this)

/**
 * Extension function to lift a [RationalNumber] into the [ExtendedReal] domain.
 *
 * @return A [ExtendedReal.Finite] wrapping the real conversion of this rational.
 */
fun RationalNumber.toExtendedReal(): ExtendedReal = ExtendedReal.of(this)

private fun ExtendedReal.signumForInfinityArithmetic(): Int = when (this) {
    is ExtendedReal.Finite -> value.signum()
    ExtendedReal.PositiveInfinity -> 1
    ExtendedReal.NegativeInfinity -> -1
    ExtendedReal.Indeterminate -> 0
}
