package mathsets.kernel

import kotlin.jvm.JvmInline

/**
 * Represents a pure imaginary number of the form bi, where b is a real coefficient
 * and i is the imaginary unit (i^2 = -1).
 *
 * Implemented as a value class wrapping the real [coefficient]. For full complex
 * arithmetic, convert to [ComplexNumber] via [toComplex].
 *
 * @property coefficient The real-valued coefficient b in the expression bi.
 * @see ComplexNumber
 * @see RealNumber
 * @see MathElement
 */
@JvmInline
value class ImaginaryNumber(val coefficient: RealNumber) : MathElement {

    /**
     * Adds two pure imaginary numbers: bi + di = (b+d)i.
     *
     * @param other The imaginary number to add.
     * @return The sum as an [ImaginaryNumber].
     */
    operator fun plus(other: ImaginaryNumber): ImaginaryNumber =
        ImaginaryNumber(coefficient + other.coefficient)

    /**
     * Subtracts another pure imaginary number: bi - di = (b-d)i.
     *
     * @param other The imaginary number to subtract.
     * @return The difference as an [ImaginaryNumber].
     */
    operator fun minus(other: ImaginaryNumber): ImaginaryNumber =
        ImaginaryNumber(coefficient - other.coefficient)

    /**
     * Negates this imaginary number: -(bi) = (-b)i.
     *
     * @return The additive inverse of this [ImaginaryNumber].
     */
    operator fun unaryMinus(): ImaginaryNumber = ImaginaryNumber(-coefficient)

    /**
     * Scales this imaginary number by a real factor: (bi) * r = (br)i.
     *
     * @param other The real scalar.
     * @return The scaled [ImaginaryNumber].
     */
    operator fun times(other: RealNumber): ImaginaryNumber = ImaginaryNumber(coefficient * other)

    /**
     * Divides this imaginary number by a real factor: (bi) / r = (b/r)i.
     *
     * @param other The real divisor.
     * @return The scaled [ImaginaryNumber].
     * @throws IllegalArgumentException if [other] is zero.
     */
    operator fun div(other: RealNumber): ImaginaryNumber = ImaginaryNumber(coefficient / other)

    /**
     * Multiplies two pure imaginary numbers: (bi)(di) = -bd.
     *
     * The result is a real number because i^2 = -1.
     *
     * @param other The imaginary number to multiply by.
     * @return The real-valued product as a [RealNumber].
     */
    infix fun times(other: ImaginaryNumber): RealNumber = -(coefficient * other.coefficient)

    /**
     * Checks whether this imaginary number is zero (0i).
     *
     * @return `true` if the coefficient is zero.
     */
    fun isZero(): Boolean = coefficient.isZero()

    /**
     * Converts this pure imaginary number to a [ComplexNumber] with zero real part.
     *
     * @return A [ComplexNumber] of the form 0 + bi.
     */
    fun toComplex(): ComplexNumber = ComplexNumber(RealNumber.ZERO, coefficient)

    override fun toString(): String = when (coefficient) {
        RealNumber.ONE -> "i"
        RealNumber.of(-1) -> "-i"
        else -> "${coefficient}i"
    }

    companion object {
        /** The imaginary number 0i. */
        val ZERO: ImaginaryNumber = ImaginaryNumber(RealNumber.ZERO)

        /** The imaginary unit i. */
        val I: ImaginaryNumber = ImaginaryNumber(RealNumber.ONE)

        /**
         * Creates an [ImaginaryNumber] from a [RealNumber] coefficient.
         *
         * @param coefficient The real-valued coefficient.
         * @return The corresponding [ImaginaryNumber].
         */
        fun of(coefficient: RealNumber): ImaginaryNumber = ImaginaryNumber(coefficient)

        /**
         * Creates an [ImaginaryNumber] from an [Int] coefficient.
         *
         * @param coefficient The integer coefficient.
         * @return The corresponding [ImaginaryNumber].
         */
        fun of(coefficient: Int): ImaginaryNumber = ImaginaryNumber(RealNumber.of(coefficient))

        /**
         * Creates an [ImaginaryNumber] from a [Long] coefficient.
         *
         * @param coefficient The long coefficient.
         * @return The corresponding [ImaginaryNumber].
         */
        fun of(coefficient: Long): ImaginaryNumber = ImaginaryNumber(RealNumber.of(coefficient))

        /**
         * Creates an [ImaginaryNumber] from a [Double] coefficient.
         *
         * @param coefficient The double coefficient.
         * @return The corresponding [ImaginaryNumber].
         */
        fun of(coefficient: Double): ImaginaryNumber = ImaginaryNumber(RealNumber.of(coefficient))
    }
}
