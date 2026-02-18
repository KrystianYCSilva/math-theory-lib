package mathsets.construction.complex

import mathsets.construction.real.ConstructedReal
import mathsets.construction.real.toMathReal
import mathsets.kernel.ImaginaryNumber
import mathsets.kernel.MathElement

/**
 * Axiomatic construction of a purely imaginary number of the form `bi`,
 * where `b` is a [ConstructedReal].
 *
 * Arithmetic between two pure imaginaries follows the rule `(bi)(di) = -bd`,
 * which produces a real result. Mixed operations with [ConstructedReal] scalars
 * stay in the imaginary domain.
 *
 * @property coefficient the real coefficient `b` in `bi`.
 * @see ConstructedComplex
 * @see ConstructedImaginaryIsomorphism
 */
class ConstructedImaginary(
    val coefficient: ConstructedReal
) : MathElement {
    /**
     * Projects this constructed imaginary to its kernel [ImaginaryNumber] counterpart.
     */
    fun toKernel(): ImaginaryNumber = ImaginaryNumber.of(coefficient.toKernel())

    /**
     * Adds two pure imaginaries: `bi + di = (b + d)i`.
     *
     * @param other the addend.
     * @return the sum.
     */
    operator fun plus(other: ConstructedImaginary): ConstructedImaginary =
        ConstructedImaginary(coefficient + other.coefficient)

    /**
     * Subtracts a pure imaginary: `bi - di = (b - d)i`.
     *
     * @param other the subtrahend.
     * @return the difference.
     */
    operator fun minus(other: ConstructedImaginary): ConstructedImaginary =
        ConstructedImaginary(coefficient - other.coefficient)

    /**
     * Returns the additive inverse `-(bi) = (-b)i`.
     */
    operator fun unaryMinus(): ConstructedImaginary = ConstructedImaginary(-coefficient)

    /**
     * Scales this imaginary by a real factor: `(bi) * r = (b * r)i`.
     *
     * @param other the real scalar.
     * @return the scaled imaginary.
     */
    operator fun times(other: ConstructedReal): ConstructedImaginary =
        ConstructedImaginary(coefficient * other)

    /**
     * Divides this imaginary by a real factor: `(bi) / r = (b / r)i`.
     *
     * @param other the real divisor.
     * @return the scaled imaginary.
     */
    operator fun div(other: ConstructedReal): ConstructedImaginary =
        ConstructedImaginary(coefficient / other)

    /**
     * Multiplies two pure imaginaries: `(bi)(di) = -(b * d)`, yielding a real result.
     *
     * @param other the imaginary multiplier.
     * @return the real-valued product `-(b * d)`.
     */
    infix fun times(other: ConstructedImaginary): ConstructedReal =
        -(coefficient * other.coefficient)

    /**
     * Returns `true` if the coefficient is zero.
     */
    fun isZero(): Boolean = coefficient.isZero()

    /**
     * Converts this pure imaginary to a [ConstructedComplex] of the form `0 + bi`.
     */
    fun toComplex(): ConstructedComplex = ConstructedComplex.fromImaginary(this)

    override fun equals(other: Any?): Boolean =
        other is ConstructedImaginary && coefficient == other.coefficient

    override fun hashCode(): Int = coefficient.hashCode()

    override fun toString(): String = toKernel().toString()

    companion object {
        /** The imaginary zero `0i`. */
        val ZERO: ConstructedImaginary = ConstructedImaginary(ConstructedReal.ZERO)

        /** The imaginary unit `i = 1i`. */
        val I: ConstructedImaginary = ConstructedImaginary(ConstructedReal.ONE)

        /**
         * Creates a [ConstructedImaginary] from a real coefficient.
         *
         * @param coefficient the real coefficient `b` in `bi`.
         * @return the constructed imaginary `bi`.
         */
        fun of(coefficient: ConstructedReal): ConstructedImaginary = ConstructedImaginary(coefficient)

        /**
         * Creates a [ConstructedImaginary] from a kernel [ImaginaryNumber].
         *
         * @param value the kernel imaginary number.
         * @return the corresponding constructed imaginary.
         */
        fun fromKernel(value: ImaginaryNumber): ConstructedImaginary =
            ConstructedImaginary(value.coefficient.toMathReal())
    }
}

/**
 * Isomorphism between [ConstructedImaginary] and the kernel [ImaginaryNumber],
 * with round-trip verification.
 */
object ConstructedImaginaryIsomorphism {
    /**
     * Projects a [ConstructedImaginary] to its kernel [ImaginaryNumber].
     */
    fun toKernel(value: ConstructedImaginary): ImaginaryNumber = value.toKernel()

    /**
     * Lifts a kernel [ImaginaryNumber] to a [ConstructedImaginary].
     */
    fun fromKernel(value: ImaginaryNumber): ConstructedImaginary =
        ConstructedImaginary.fromKernel(value)

    /**
     * Verifies the round-trip property for integer imaginary coefficients in `-limit..limit`.
     *
     * @param limit the bound for the integer coefficient.
     * @return `true` if the round-trip holds for every value.
     */
    fun verifyRoundTrip(limit: Int): Boolean {
        for (n in -limit..limit) {
            val kernel = ImaginaryNumber.of(n)
            if (toKernel(fromKernel(kernel)) != kernel) return false
        }
        return true
    }
}

/**
 * Extension to convert a kernel [ImaginaryNumber] to [ConstructedImaginary].
 */
fun ImaginaryNumber.toMathImaginary(): ConstructedImaginary =
    ConstructedImaginary.fromKernel(this)
