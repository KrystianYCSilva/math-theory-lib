package mathsets.construction.complex

import mathsets.construction.real.ConstructedReal
import mathsets.construction.real.toMathReal
import mathsets.kernel.ImaginaryNumber
import mathsets.kernel.MathElement

/**
 * Imaginário puro construído da forma bi, com b em R construído.
 */
class ConstructedImaginary(
    val coefficient: ConstructedReal
) : MathElement {
    fun toKernel(): ImaginaryNumber = ImaginaryNumber.of(coefficient.toKernel())

    operator fun plus(other: ConstructedImaginary): ConstructedImaginary =
        ConstructedImaginary(coefficient + other.coefficient)

    operator fun minus(other: ConstructedImaginary): ConstructedImaginary =
        ConstructedImaginary(coefficient - other.coefficient)

    operator fun unaryMinus(): ConstructedImaginary = ConstructedImaginary(-coefficient)

    operator fun times(other: ConstructedReal): ConstructedImaginary =
        ConstructedImaginary(coefficient * other)

    operator fun div(other: ConstructedReal): ConstructedImaginary =
        ConstructedImaginary(coefficient / other)

    /**
     * (bi) * (di) = -(bd), resultado real.
     */
    infix fun times(other: ConstructedImaginary): ConstructedReal =
        -(coefficient * other.coefficient)

    fun isZero(): Boolean = coefficient.isZero()

    fun toComplex(): ConstructedComplex = ConstructedComplex.fromImaginary(this)

    override fun equals(other: Any?): Boolean =
        other is ConstructedImaginary && coefficient == other.coefficient

    override fun hashCode(): Int = coefficient.hashCode()

    override fun toString(): String = toKernel().toString()

    companion object {
        val ZERO: ConstructedImaginary = ConstructedImaginary(ConstructedReal.ZERO)
        val I: ConstructedImaginary = ConstructedImaginary(ConstructedReal.ONE)

        fun of(coefficient: ConstructedReal): ConstructedImaginary = ConstructedImaginary(coefficient)

        fun fromKernel(value: ImaginaryNumber): ConstructedImaginary =
            ConstructedImaginary(value.coefficient.toMathReal())
    }
}

object ConstructedImaginaryIsomorphism {
    fun toKernel(value: ConstructedImaginary): ImaginaryNumber = value.toKernel()

    fun fromKernel(value: ImaginaryNumber): ConstructedImaginary =
        ConstructedImaginary.fromKernel(value)

    fun verifyRoundTrip(limit: Int): Boolean {
        for (n in -limit..limit) {
            val kernel = ImaginaryNumber.of(n)
            if (toKernel(fromKernel(kernel)) != kernel) return false
        }
        return true
    }
}

fun ImaginaryNumber.toMathImaginary(): ConstructedImaginary =
    ConstructedImaginary.fromKernel(this)

