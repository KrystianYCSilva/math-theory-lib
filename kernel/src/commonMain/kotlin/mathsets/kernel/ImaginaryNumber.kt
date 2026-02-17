package mathsets.kernel

import kotlin.jvm.JvmInline

/**
 * Número imaginário puro da forma bi.
 */
@JvmInline
value class ImaginaryNumber(val coefficient: RealNumber) : MathElement {
    operator fun plus(other: ImaginaryNumber): ImaginaryNumber =
        ImaginaryNumber(coefficient + other.coefficient)

    operator fun minus(other: ImaginaryNumber): ImaginaryNumber =
        ImaginaryNumber(coefficient - other.coefficient)

    operator fun unaryMinus(): ImaginaryNumber = ImaginaryNumber(-coefficient)

    operator fun times(other: RealNumber): ImaginaryNumber = ImaginaryNumber(coefficient * other)

    operator fun div(other: RealNumber): ImaginaryNumber = ImaginaryNumber(coefficient / other)

    /**
     * (bi) * (di) = -(bd)
     */
    infix fun times(other: ImaginaryNumber): RealNumber = -(coefficient * other.coefficient)

    fun isZero(): Boolean = coefficient.isZero()

    fun toComplex(): ComplexNumber = ComplexNumber(RealNumber.ZERO, coefficient)

    override fun toString(): String = when (coefficient) {
        RealNumber.ONE -> "i"
        RealNumber.of(-1) -> "-i"
        else -> "${coefficient}i"
    }

    companion object {
        val ZERO: ImaginaryNumber = ImaginaryNumber(RealNumber.ZERO)
        val I: ImaginaryNumber = ImaginaryNumber(RealNumber.ONE)

        fun of(coefficient: RealNumber): ImaginaryNumber = ImaginaryNumber(coefficient)
        fun of(coefficient: Int): ImaginaryNumber = ImaginaryNumber(RealNumber.of(coefficient))
        fun of(coefficient: Long): ImaginaryNumber = ImaginaryNumber(RealNumber.of(coefficient))
        fun of(coefficient: Double): ImaginaryNumber = ImaginaryNumber(RealNumber.of(coefficient))
    }
}

