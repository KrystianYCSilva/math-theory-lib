package mathsets.kernel

/**
 * Represents a complex number of the form a + bi, where a is the real part
 * and b is the imaginary coefficient.
 *
 * Complex numbers form an algebraically closed field (ℂ) that extends ℝ.
 * Unlike real numbers, complex numbers do not have a canonical total order,
 * so this class does not implement [Comparable].
 *
 * @property real The real part (a) of this complex number.
 * @property imaginary The imaginary coefficient (b) such that the full form is a + bi.
 * @see RealNumber
 * @see ImaginaryNumber
 * @see MathElement
 */
data class ComplexNumber(
    val real: RealNumber,
    val imaginary: RealNumber
) : MathElement {

    /**
     * Adds two complex numbers component-wise.
     *
     * @param other The complex number to add.
     * @return The sum as a [ComplexNumber].
     */
    operator fun plus(other: ComplexNumber): ComplexNumber = ComplexNumber(
        real = real + other.real,
        imaginary = imaginary + other.imaginary
    )

    /**
     * Subtracts another complex number component-wise.
     *
     * @param other The complex number to subtract.
     * @return The difference as a [ComplexNumber].
     */
    operator fun minus(other: ComplexNumber): ComplexNumber = ComplexNumber(
        real = real - other.real,
        imaginary = imaginary - other.imaginary
    )

    /**
     * Negates this complex number: -(a + bi) = -a - bi.
     *
     * @return The additive inverse of this [ComplexNumber].
     */
    operator fun unaryMinus(): ComplexNumber = ComplexNumber(
        real = -real,
        imaginary = -imaginary
    )

    /**
     * Multiplies two complex numbers using the distributive law:
     * (a + bi)(c + di) = (ac - bd) + (ad + bc)i.
     *
     * @param other The complex number to multiply by.
     * @return The product as a [ComplexNumber].
     */
    operator fun times(other: ComplexNumber): ComplexNumber {
        val realPart = (real * other.real) - (imaginary * other.imaginary)
        val imaginaryPart = (real * other.imaginary) + (imaginary * other.real)
        return ComplexNumber(realPart, imaginaryPart)
    }

    /**
     * Divides this complex number by another using the conjugate method:
     * (a + bi) / (c + di) = ((ac + bd) + (bc - ad)i) / (c^2 + d^2).
     *
     * @param other The divisor complex number.
     * @return The quotient as a [ComplexNumber].
     * @throws IllegalArgumentException if [other] is zero.
     */
    operator fun div(other: ComplexNumber): ComplexNumber {
        val denominator = (other.real * other.real) + (other.imaginary * other.imaginary)
        require(!denominator.isZero()) { "Division by zero complex number." }

        val realPart = ((real * other.real) + (imaginary * other.imaginary)) / denominator
        val imaginaryPart = ((imaginary * other.real) - (real * other.imaginary)) / denominator
        return ComplexNumber(realPart, imaginaryPart)
    }

    /**
     * Returns the complex conjugate: conj(a + bi) = a - bi.
     *
     * @return The conjugate of this [ComplexNumber].
     */
    fun conjugate(): ComplexNumber = ComplexNumber(real, -imaginary)

    /**
     * Returns the squared modulus (norm squared): |z|^2 = a^2 + b^2.
     *
     * @return The squared modulus as a [RealNumber].
     */
    fun modulusSquared(): RealNumber = (real * real) + (imaginary * imaginary)

    /**
     * Checks whether this complex number is zero (0 + 0i).
     *
     * @return `true` if both real and imaginary parts are zero.
     */
    fun isZero(): Boolean = real.isZero() && imaginary.isZero()

    /**
     * Checks whether this complex number is purely real (imaginary part is zero).
     *
     * @return `true` if the imaginary part is zero.
     */
    fun isPurelyReal(): Boolean = imaginary.isZero()

    /**
     * Checks whether this complex number is purely imaginary (real part is zero, imaginary part non-zero).
     *
     * @return `true` if the real part is zero and the imaginary part is non-zero.
     */
    fun isPurelyImaginary(): Boolean = real.isZero() && !imaginary.isZero()

    /**
     * Extracts the real part as a [RealNumber].
     *
     * @return The real part of this complex number.
     */
    fun realPart(): RealNumber = real

    /**
     * Extracts the imaginary part as an [ImaginaryNumber].
     *
     * @return The imaginary component wrapped in [ImaginaryNumber].
     */
    fun imaginaryPart(): ImaginaryNumber = ImaginaryNumber(imaginary)

    override fun toString(): String {
        if (imaginary.isZero()) return real.toString()
        if (real.isZero()) return ImaginaryNumber(imaginary).toString()

        val sign = if (imaginary.signum() >= 0) "+" else "-"
        val absImaginary = imaginary.abs()
        val imagText = when (absImaginary) {
            RealNumber.ONE -> "i"
            else -> "${absImaginary}i"
        }
        return "$real $sign $imagText"
    }

    companion object {
        /** The complex number 0 + 0i. */
        val ZERO: ComplexNumber = ComplexNumber(RealNumber.ZERO, RealNumber.ZERO)

        /** The complex number 1 + 0i. */
        val ONE: ComplexNumber = ComplexNumber(RealNumber.ONE, RealNumber.ZERO)

        /** The imaginary unit 0 + 1i. */
        val I: ComplexNumber = ComplexNumber(RealNumber.ZERO, RealNumber.ONE)

        /**
         * Creates a [ComplexNumber] from [RealNumber] parts.
         *
         * @param real The real part.
         * @param imaginary The imaginary coefficient (defaults to zero).
         * @return The corresponding [ComplexNumber].
         */
        fun of(real: RealNumber, imaginary: RealNumber = RealNumber.ZERO): ComplexNumber =
            ComplexNumber(real, imaginary)

        /**
         * Creates a [ComplexNumber] from [Int] parts.
         *
         * @param real The real part.
         * @param imaginary The imaginary coefficient (defaults to 0).
         * @return The corresponding [ComplexNumber].
         */
        fun of(real: Int, imaginary: Int = 0): ComplexNumber =
            ComplexNumber(RealNumber.of(real), RealNumber.of(imaginary))

        /**
         * Creates a [ComplexNumber] from [Double] parts.
         *
         * @param real The real part.
         * @param imaginary The imaginary coefficient (defaults to 0.0).
         * @return The corresponding [ComplexNumber].
         */
        fun of(real: Double, imaginary: Double = 0.0): ComplexNumber =
            ComplexNumber(RealNumber.of(real), RealNumber.of(imaginary))

        /**
         * Creates a purely real [ComplexNumber] from a [RealNumber].
         *
         * @param real The real part.
         * @return A [ComplexNumber] with zero imaginary part.
         */
        fun fromReal(real: RealNumber): ComplexNumber = ComplexNumber(real, RealNumber.ZERO)

        /**
         * Creates a purely imaginary [ComplexNumber] from an [ImaginaryNumber].
         *
         * @param imaginary The imaginary number.
         * @return A [ComplexNumber] with zero real part.
         */
        fun fromImaginary(imaginary: ImaginaryNumber): ComplexNumber =
            ComplexNumber(RealNumber.ZERO, imaginary.coefficient)
    }
}
