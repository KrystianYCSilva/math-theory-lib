package mathsets.construction.complex

import mathsets.construction.real.ConstructedReal
import mathsets.construction.real.toMathReal
import mathsets.kernel.AlgebraicArithmetic
import mathsets.kernel.ComplexNumber
import mathsets.kernel.MathElement

/**
 * Complexo construído da forma a + bi, com a,b reais construídos.
 */
class ConstructedComplex private constructor(
    val real: ConstructedReal,
    val imaginary: ConstructedReal,
    private val kernelValue: ComplexNumber
) : MathElement {
    fun toKernel(): ComplexNumber = kernelValue

    operator fun plus(other: ConstructedComplex): ConstructedComplex =
        fromKernel(kernelValue + other.kernelValue)

    operator fun minus(other: ConstructedComplex): ConstructedComplex =
        fromKernel(kernelValue - other.kernelValue)

    operator fun unaryMinus(): ConstructedComplex = fromKernel(-kernelValue)

    operator fun times(other: ConstructedComplex): ConstructedComplex =
        fromKernel(kernelValue * other.kernelValue)

    operator fun div(other: ConstructedComplex): ConstructedComplex =
        fromKernel(kernelValue / other.kernelValue)

    fun conjugate(): ConstructedComplex = fromKernel(kernelValue.conjugate())

    fun modulusSquared(): ConstructedReal = kernelValue.modulusSquared().toMathReal()

    fun isZero(): Boolean = kernelValue.isZero()

    fun isPurelyReal(): Boolean = kernelValue.isPurelyReal()

    fun isPurelyImaginary(): Boolean = kernelValue.isPurelyImaginary()

    override fun equals(other: Any?): Boolean =
        other is ConstructedComplex && kernelValue == other.kernelValue

    override fun hashCode(): Int = kernelValue.hashCode()

    override fun toString(): String = kernelValue.toString()

    companion object {
        val ZERO: ConstructedComplex = fromKernel(ComplexNumber.ZERO)
        val ONE: ConstructedComplex = fromKernel(ComplexNumber.ONE)
        val I: ConstructedComplex = fromKernel(ComplexNumber.I)

        fun of(
            real: ConstructedReal,
            imaginary: ConstructedReal = ConstructedReal.ZERO
        ): ConstructedComplex = fromKernel(ComplexNumber.of(real.toKernel(), imaginary.toKernel()))

        fun fromReal(real: ConstructedReal): ConstructedComplex =
            of(real, ConstructedReal.ZERO)

        fun fromImaginary(imaginary: ConstructedImaginary): ConstructedComplex =
            of(ConstructedReal.ZERO, imaginary.coefficient)

        fun fromKernel(value: ComplexNumber): ConstructedComplex =
            ConstructedComplex(
                real = value.real.toMathReal(),
                imaginary = value.imaginary.toMathReal(),
                kernelValue = value
            )
    }
}

object ConstructedComplexArithmetic : AlgebraicArithmetic<ConstructedComplex> {
    override val zero: ConstructedComplex = ConstructedComplex.ZERO
    override val one: ConstructedComplex = ConstructedComplex.ONE

    override fun add(a: ConstructedComplex, b: ConstructedComplex): ConstructedComplex = a + b

    override fun subtract(a: ConstructedComplex, b: ConstructedComplex): ConstructedComplex = a - b

    override fun multiply(a: ConstructedComplex, b: ConstructedComplex): ConstructedComplex = a * b

    override fun divide(a: ConstructedComplex, b: ConstructedComplex): ConstructedComplex = a / b
}

object ConstructedComplexIsomorphism {
    fun toKernel(value: ConstructedComplex): ComplexNumber = value.toKernel()

    fun fromKernel(value: ComplexNumber): ConstructedComplex = ConstructedComplex.fromKernel(value)

    fun verifyRoundTrip(limit: Int): Boolean {
        for (a in -limit..limit) {
            for (b in -limit..limit) {
                val kernel = ComplexNumber.of(a, b)
                if (toKernel(fromKernel(kernel)) != kernel) return false
            }
        }
        return true
    }
}

fun ComplexNumber.toMathComplex(): ConstructedComplex = ConstructedComplex.fromKernel(this)

fun ConstructedReal.toMathComplex(): ConstructedComplex = ConstructedComplex.fromReal(this)

fun ConstructedImaginary.toMathComplex(): ConstructedComplex = ConstructedComplex.fromImaginary(this)

