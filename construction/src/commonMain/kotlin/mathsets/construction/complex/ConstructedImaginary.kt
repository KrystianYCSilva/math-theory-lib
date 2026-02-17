package mathsets.construction.complex

import mathsets.construction.real.ConstructedReal
import mathsets.construction.real.toMathReal
import mathsets.kernel.ImaginaryNumber
import mathsets.kernel.MathElement

/**
 * Imaginário puro construído da forma bi, onde b é um real construído.
 */
class ConstructedImaginary private constructor(
    val coefficient: ConstructedReal,
    private val kernelValue: ImaginaryNumber
) : MathElement {
    fun toKernel(): ImaginaryNumber = kernelValue

    operator fun plus(other: ConstructedImaginary): ConstructedImaginary =
        fromKernel(kernelValue + other.kernelValue)

    operator fun minus(other: ConstructedImaginary): ConstructedImaginary =
        fromKernel(kernelValue - other.kernelValue)

    operator fun unaryMinus(): ConstructedImaginary = fromKernel(-kernelValue)

    operator fun times(other: ConstructedReal): ConstructedImaginary =
        fromKernel(kernelValue * other.toKernel())

    operator fun div(other: ConstructedReal): ConstructedImaginary =
        fromKernel(kernelValue / other.toKernel())

    /**
     * (bi) * (di) = -(bd), resultado real.
     */
    infix fun times(other: ConstructedImaginary): ConstructedReal =
        (kernelValue times other.kernelValue).toMathReal()

    fun isZero(): Boolean = kernelValue.isZero()

    fun toComplex(): ConstructedComplex = ConstructedComplex.fromKernel(kernelValue.toComplex())

    override fun equals(other: Any?): Boolean =
        other is ConstructedImaginary && kernelValue == other.kernelValue

    override fun hashCode(): Int = kernelValue.hashCode()

    override fun toString(): String = kernelValue.toString()

    companion object {
        val ZERO: ConstructedImaginary = fromKernel(ImaginaryNumber.ZERO)
        val I: ConstructedImaginary = fromKernel(ImaginaryNumber.I)

        fun of(coefficient: ConstructedReal): ConstructedImaginary =
            ConstructedImaginary(coefficient, ImaginaryNumber.of(coefficient.toKernel()))

        fun fromKernel(value: ImaginaryNumber): ConstructedImaginary =
            ConstructedImaginary(value.coefficient.toMathReal(), value)
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

