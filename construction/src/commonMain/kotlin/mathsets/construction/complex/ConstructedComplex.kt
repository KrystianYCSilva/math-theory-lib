package mathsets.construction.complex

import mathsets.construction.real.ConstructedReal
import mathsets.construction.real.toMathReal
import mathsets.kernel.AlgebraicArithmetic
import mathsets.kernel.ComplexNumber
import mathsets.kernel.MathElement

/**
 * Axiomatic construction of the complex numbers as ordered pairs of constructed reals:
 *
 * `(a, b)` represents the complex number `a + bi`.
 *
 * Arithmetic follows the standard rules for complex algebra:
 * - Addition: `(a, b) + (c, d) = (a + c, b + d)`
 * - Multiplication: `(a, b) * (c, d) = (ac - bd, ad + bc)`
 * - Division: `(a, b) / (c, d) = ((ac + bd)/(c^2 + d^2), (bc - ad)/(c^2 + d^2))`
 *
 * @property real the real part of this complex number.
 * @property imaginary the imaginary part of this complex number.
 * @see ConstructedComplexArithmetic
 * @see ConstructedComplexIsomorphism
 */
class ConstructedComplex(
    val real: ConstructedReal,
    val imaginary: ConstructedReal
) : MathElement {
    /**
     * Projects this constructed complex to its kernel [ComplexNumber] counterpart.
     */
    fun toKernel(): ComplexNumber = ComplexNumber.of(real.toKernel(), imaginary.toKernel())

    /**
     * Adds two complex numbers component-wise.
     *
     * @param other the addend.
     * @return the sum `(a + c, b + d)`.
     */
    operator fun plus(other: ConstructedComplex): ConstructedComplex = ConstructedComplex(
        real = real + other.real,
        imaginary = imaginary + other.imaginary
    )

    /**
     * Subtracts [other] from this complex number component-wise.
     *
     * @param other the subtrahend.
     * @return the difference `(a - c, b - d)`.
     */
    operator fun minus(other: ConstructedComplex): ConstructedComplex = ConstructedComplex(
        real = real - other.real,
        imaginary = imaginary - other.imaginary
    )

    /**
     * Returns the additive inverse `(-a, -b)`.
     */
    operator fun unaryMinus(): ConstructedComplex = ConstructedComplex(
        real = -real,
        imaginary = -imaginary
    )

    /**
     * Multiplies two complex numbers: `(a, b) * (c, d) = (ac - bd, ad + bc)`.
     *
     * @param other the multiplier.
     * @return the product.
     */
    operator fun times(other: ConstructedComplex): ConstructedComplex {
        val realPart = (real * other.real) - (imaginary * other.imaginary)
        val imaginaryPart = (real * other.imaginary) + (imaginary * other.real)
        return ConstructedComplex(realPart, imaginaryPart)
    }

    /**
     * Divides this complex number by [other] using the conjugate formula.
     *
     * @param other the divisor (must not be zero).
     * @return the quotient.
     * @throws IllegalArgumentException if [other] is zero.
     */
    operator fun div(other: ConstructedComplex): ConstructedComplex {
        val denominator = (other.real * other.real) + (other.imaginary * other.imaginary)
        require(!denominator.isZero()) { "Division by zero complex number." }

        val realPart = ((real * other.real) + (imaginary * other.imaginary)) / denominator
        val imaginaryPart = ((imaginary * other.real) - (real * other.imaginary)) / denominator
        return ConstructedComplex(realPart, imaginaryPart)
    }

    /**
     * Returns the complex conjugate `(a, -b)`.
     */
    fun conjugate(): ConstructedComplex = ConstructedComplex(real, -imaginary)

    /**
     * Returns the squared modulus `a^2 + b^2` as a [ConstructedReal].
     */
    fun modulusSquared(): ConstructedReal = (real * real) + (imaginary * imaginary)

    /**
     * Returns `true` if both real and imaginary parts are zero.
     */
    fun isZero(): Boolean = real.isZero() && imaginary.isZero()

    /**
     * Returns `true` if the imaginary part is zero (this is a real number).
     */
    fun isPurelyReal(): Boolean = imaginary.isZero()

    /**
     * Returns `true` if the real part is zero and the imaginary part is non-zero.
     */
    fun isPurelyImaginary(): Boolean = real.isZero() && !imaginary.isZero()

    override fun equals(other: Any?): Boolean =
        other is ConstructedComplex &&
            real == other.real &&
            imaginary == other.imaginary

    override fun hashCode(): Int = 31 * real.hashCode() + imaginary.hashCode()

    override fun toString(): String = toKernel().toString()

    companion object {
        /** The complex zero `0 + 0i`. */
        val ZERO: ConstructedComplex = ConstructedComplex(ConstructedReal.ZERO, ConstructedReal.ZERO)

        /** The complex one `1 + 0i`. */
        val ONE: ConstructedComplex = ConstructedComplex(ConstructedReal.ONE, ConstructedReal.ZERO)

        /** The imaginary unit `0 + 1i`. */
        val I: ConstructedComplex = ConstructedComplex(ConstructedReal.ZERO, ConstructedReal.ONE)

        /**
         * Creates a [ConstructedComplex] from real and imaginary parts.
         *
         * @param real the real part.
         * @param imaginary the imaginary part (defaults to zero).
         * @return the constructed complex number.
         */
        fun of(
            real: ConstructedReal,
            imaginary: ConstructedReal = ConstructedReal.ZERO
        ): ConstructedComplex = ConstructedComplex(real, imaginary)

        /**
         * Creates a purely real [ConstructedComplex] with zero imaginary part.
         *
         * @param real the real part.
         * @return the constructed complex number `real + 0i`.
         */
        fun fromReal(real: ConstructedReal): ConstructedComplex =
            ConstructedComplex(real, ConstructedReal.ZERO)

        /**
         * Creates a [ConstructedComplex] from a [ConstructedImaginary] (purely imaginary).
         *
         * @param imaginary the imaginary value.
         * @return the constructed complex number `0 + bi`.
         */
        fun fromImaginary(imaginary: ConstructedImaginary): ConstructedComplex =
            ConstructedComplex(ConstructedReal.ZERO, imaginary.coefficient)

        /**
         * Creates a [ConstructedComplex] from a kernel [ComplexNumber].
         *
         * @param value the kernel complex number.
         * @return the corresponding constructed complex.
         */
        fun fromKernel(value: ComplexNumber): ConstructedComplex =
            ConstructedComplex(value.real.toMathReal(), value.imaginary.toMathReal())
    }
}

/**
 * [AlgebraicArithmetic] implementation for [ConstructedComplex].
 *
 * Delegates to the operator overloads defined on [ConstructedComplex].
 * Note: complex numbers do not have a total order, so this implements
 * [AlgebraicArithmetic] rather than [mathsets.kernel.Arithmetic].
 */
object ConstructedComplexArithmetic : AlgebraicArithmetic<ConstructedComplex> {
    /** The additive identity `0 + 0i`. */
    override val zero: ConstructedComplex = ConstructedComplex.ZERO

    /** The multiplicative identity `1 + 0i`. */
    override val one: ConstructedComplex = ConstructedComplex.ONE

    override fun add(a: ConstructedComplex, b: ConstructedComplex): ConstructedComplex = a + b

    override fun subtract(a: ConstructedComplex, b: ConstructedComplex): ConstructedComplex = a - b

    override fun multiply(a: ConstructedComplex, b: ConstructedComplex): ConstructedComplex = a * b

    override fun divide(a: ConstructedComplex, b: ConstructedComplex): ConstructedComplex = a / b
}

/**
 * Isomorphism between [ConstructedComplex] and the kernel [ComplexNumber],
 * with round-trip verification.
 */
object ConstructedComplexIsomorphism {
    /**
     * Projects a [ConstructedComplex] to its kernel [ComplexNumber].
     */
    fun toKernel(value: ConstructedComplex): ComplexNumber = value.toKernel()

    /**
     * Lifts a kernel [ComplexNumber] to a [ConstructedComplex].
     */
    fun fromKernel(value: ComplexNumber): ConstructedComplex = ConstructedComplex.fromKernel(value)

    /**
     * Verifies the round-trip property for all Gaussian integers `a + bi` with
     * `a, b` in `-limit..limit`.
     *
     * @param limit the bound for real and imaginary integer parts.
     * @return `true` if the round-trip holds for every value.
     */
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

/**
 * Extension to convert a kernel [ComplexNumber] to [ConstructedComplex].
 */
fun ComplexNumber.toMathComplex(): ConstructedComplex = ConstructedComplex.fromKernel(this)

/**
 * Extension to embed a [ConstructedReal] into [ConstructedComplex] with zero imaginary part.
 */
fun ConstructedReal.toMathComplex(): ConstructedComplex = ConstructedComplex.fromReal(this)

/**
 * Extension to convert a [ConstructedImaginary] to [ConstructedComplex].
 */
fun ConstructedImaginary.toMathComplex(): ConstructedComplex = ConstructedComplex.fromImaginary(this)
