package mathsets.construction.complex

import mathsets.construction.real.ConstructedReal
import mathsets.construction.real.toMathReal
import mathsets.kernel.AlgebraicArithmetic
import mathsets.kernel.ComplexNumber
import mathsets.kernel.MathElement

/**
 * Complexo construído como par ordenado de reais construídos: (a, b) <-> a + bi.
 */
class ConstructedComplex(
    val real: ConstructedReal,
    val imaginary: ConstructedReal
) : MathElement {
    fun toKernel(): ComplexNumber = ComplexNumber.of(real.toKernel(), imaginary.toKernel())

    operator fun plus(other: ConstructedComplex): ConstructedComplex = ConstructedComplex(
        real = real + other.real,
        imaginary = imaginary + other.imaginary
    )

    operator fun minus(other: ConstructedComplex): ConstructedComplex = ConstructedComplex(
        real = real - other.real,
        imaginary = imaginary - other.imaginary
    )

    operator fun unaryMinus(): ConstructedComplex = ConstructedComplex(
        real = -real,
        imaginary = -imaginary
    )

    operator fun times(other: ConstructedComplex): ConstructedComplex {
        val realPart = (real * other.real) - (imaginary * other.imaginary)
        val imaginaryPart = (real * other.imaginary) + (imaginary * other.real)
        return ConstructedComplex(realPart, imaginaryPart)
    }

    operator fun div(other: ConstructedComplex): ConstructedComplex {
        val denominator = (other.real * other.real) + (other.imaginary * other.imaginary)
        require(!denominator.isZero()) { "Division by zero complex number." }

        val realPart = ((real * other.real) + (imaginary * other.imaginary)) / denominator
        val imaginaryPart = ((imaginary * other.real) - (real * other.imaginary)) / denominator
        return ConstructedComplex(realPart, imaginaryPart)
    }

    fun conjugate(): ConstructedComplex = ConstructedComplex(real, -imaginary)

    fun modulusSquared(): ConstructedReal = (real * real) + (imaginary * imaginary)

    fun isZero(): Boolean = real.isZero() && imaginary.isZero()

    fun isPurelyReal(): Boolean = imaginary.isZero()

    fun isPurelyImaginary(): Boolean = real.isZero() && !imaginary.isZero()

    override fun equals(other: Any?): Boolean =
        other is ConstructedComplex &&
            real == other.real &&
            imaginary == other.imaginary

    override fun hashCode(): Int = 31 * real.hashCode() + imaginary.hashCode()

    override fun toString(): String = toKernel().toString()

    companion object {
        val ZERO: ConstructedComplex = ConstructedComplex(ConstructedReal.ZERO, ConstructedReal.ZERO)
        val ONE: ConstructedComplex = ConstructedComplex(ConstructedReal.ONE, ConstructedReal.ZERO)
        val I: ConstructedComplex = ConstructedComplex(ConstructedReal.ZERO, ConstructedReal.ONE)

        fun of(
            real: ConstructedReal,
            imaginary: ConstructedReal = ConstructedReal.ZERO
        ): ConstructedComplex = ConstructedComplex(real, imaginary)

        fun fromReal(real: ConstructedReal): ConstructedComplex =
            ConstructedComplex(real, ConstructedReal.ZERO)

        fun fromImaginary(imaginary: ConstructedImaginary): ConstructedComplex =
            ConstructedComplex(ConstructedReal.ZERO, imaginary.coefficient)

        fun fromKernel(value: ComplexNumber): ConstructedComplex =
            ConstructedComplex(value.real.toMathReal(), value.imaginary.toMathReal())
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

