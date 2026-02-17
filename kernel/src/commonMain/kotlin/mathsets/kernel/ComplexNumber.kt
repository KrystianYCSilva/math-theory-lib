package mathsets.kernel

/**
 * Número complexo da forma a + bi.
 */
data class ComplexNumber(
    val real: RealNumber,
    val imaginary: RealNumber
) : MathElement {
    operator fun plus(other: ComplexNumber): ComplexNumber = ComplexNumber(
        real = real + other.real,
        imaginary = imaginary + other.imaginary
    )

    operator fun minus(other: ComplexNumber): ComplexNumber = ComplexNumber(
        real = real - other.real,
        imaginary = imaginary - other.imaginary
    )

    operator fun unaryMinus(): ComplexNumber = ComplexNumber(
        real = -real,
        imaginary = -imaginary
    )

    operator fun times(other: ComplexNumber): ComplexNumber {
        val realPart = (real * other.real) - (imaginary * other.imaginary)
        val imaginaryPart = (real * other.imaginary) + (imaginary * other.real)
        return ComplexNumber(realPart, imaginaryPart)
    }

    operator fun div(other: ComplexNumber): ComplexNumber {
        val denominator = (other.real * other.real) + (other.imaginary * other.imaginary)
        require(!denominator.isZero()) { "Division by zero complex number." }

        val realPart = ((real * other.real) + (imaginary * other.imaginary)) / denominator
        val imaginaryPart = ((imaginary * other.real) - (real * other.imaginary)) / denominator
        return ComplexNumber(realPart, imaginaryPart)
    }

    fun conjugate(): ComplexNumber = ComplexNumber(real, -imaginary)

    fun modulusSquared(): RealNumber = (real * real) + (imaginary * imaginary)

    fun isZero(): Boolean = real.isZero() && imaginary.isZero()

    fun isPurelyReal(): Boolean = imaginary.isZero()

    fun isPurelyImaginary(): Boolean = real.isZero() && !imaginary.isZero()

    fun realPart(): RealNumber = real

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
        val ZERO: ComplexNumber = ComplexNumber(RealNumber.ZERO, RealNumber.ZERO)
        val ONE: ComplexNumber = ComplexNumber(RealNumber.ONE, RealNumber.ZERO)
        val I: ComplexNumber = ComplexNumber(RealNumber.ZERO, RealNumber.ONE)

        fun of(real: RealNumber, imaginary: RealNumber = RealNumber.ZERO): ComplexNumber =
            ComplexNumber(real, imaginary)

        fun of(real: Int, imaginary: Int = 0): ComplexNumber =
            ComplexNumber(RealNumber.of(real), RealNumber.of(imaginary))

        fun of(real: Double, imaginary: Double = 0.0): ComplexNumber =
            ComplexNumber(RealNumber.of(real), RealNumber.of(imaginary))

        fun fromReal(real: RealNumber): ComplexNumber = ComplexNumber(real, RealNumber.ZERO)
        fun fromImaginary(imaginary: ImaginaryNumber): ComplexNumber =
            ComplexNumber(RealNumber.ZERO, imaginary.coefficient)
    }
}

