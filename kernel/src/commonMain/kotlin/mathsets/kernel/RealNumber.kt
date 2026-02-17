package mathsets.kernel

import mathsets.kernel.platform.*
import mathsets.kernel.platform.BigDecimal
import kotlin.jvm.JvmInline

/**
 * Número real computacional baseado em BigDecimal multiplataforma.
 *
 * Nota: esta representação cobre exatamente valores decimais finitos.
 * Irracionais exatos exigem representação simbólica, fora do escopo atual.
 */
@JvmInline
value class RealNumber(val value: BigDecimal) : Comparable<RealNumber>, MathElement {
    operator fun plus(other: RealNumber): RealNumber = RealNumber(value + other.value)
    operator fun minus(other: RealNumber): RealNumber = RealNumber(value - other.value)
    operator fun times(other: RealNumber): RealNumber = RealNumber(value * other.value)

    operator fun div(other: RealNumber): RealNumber {
        require(!other.isZero()) { "Division by zero in RealNumber." }
        return RealNumber(value / other.value)
    }

    operator fun unaryMinus(): RealNumber = RealNumber(-value)

    infix fun pow(exponent: Int): RealNumber {
        require(exponent >= 0) { "Negative exponent is not supported in this kernel operation." }
        return RealNumber(value.pow(exponent))
    }

    fun abs(): RealNumber = RealNumber(value.abs())

    fun isZero(): Boolean = value == BD_ZERO

    fun signum(): Int = when {
        value > BD_ZERO -> 1
        value < BD_ZERO -> -1
        else -> 0
    }

    override fun compareTo(other: RealNumber): Int = value.compareTo(other.value)

    override fun toString(): String = value.toString()

    companion object {
        val ZERO: RealNumber = RealNumber(BD_ZERO)
        val ONE: RealNumber = RealNumber(BD_ONE)
        val TWO: RealNumber = RealNumber(BD_TWO)

        fun of(value: BigDecimal): RealNumber = RealNumber(value)
        fun of(value: BigInteger): RealNumber = RealNumber(BigDecimal.fromBigInteger(value))
        fun of(value: RationalNumber): RealNumber {
            val numerator = BigDecimal.fromBigInteger(value.numerator)
            val denominator = BigDecimal.fromBigInteger(value.denominator)
            return RealNumber(numerator / denominator)
        }

        fun of(value: Int): RealNumber = RealNumber(bigDecimalOf(value))
        fun of(value: Long): RealNumber = RealNumber(bigDecimalOf(value))
        fun of(value: Float): RealNumber = RealNumber(bigDecimalOf(value))
        fun of(value: Double): RealNumber = RealNumber(bigDecimalOf(value))
        fun parse(value: String): RealNumber = RealNumber(bigDecimalOf(value.trim()))
    }
}
