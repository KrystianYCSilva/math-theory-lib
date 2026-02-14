package mathsets.kernel

import mathsets.kernel.platform.BigInteger
import mathsets.kernel.platform.BI_ZERO
import mathsets.kernel.platform.BI_ONE
import mathsets.kernel.platform.bigIntegerOf
import mathsets.kernel.platform.*
import kotlin.jvm.JvmInline

/**
 * Representa um número inteiro (ℤ = {..., -1, 0, 1, ...}).
 *
 * Implementado como value class sobre BigInteger.
 */
@JvmInline
value class IntegerNumber(val value: BigInteger) : Comparable<IntegerNumber> {

    operator fun plus(other: IntegerNumber): IntegerNumber =
        IntegerNumber(this.value + other.value)

    operator fun minus(other: IntegerNumber): IntegerNumber =
        IntegerNumber(this.value - other.value)

    operator fun times(other: IntegerNumber): IntegerNumber =
        IntegerNumber(this.value * other.value)

    operator fun div(other: IntegerNumber): IntegerNumber =
        IntegerNumber(this.value / other.value)

    operator fun unaryMinus(): IntegerNumber =
        IntegerNumber(-this.value)

    fun abs(): IntegerNumber = IntegerNumber(this.value.abs())

    override fun compareTo(other: IntegerNumber): Int =
        this.value.compareTo(other.value)

    override fun toString(): String = value.toString()

    companion object {
        val ZERO = IntegerNumber(BI_ZERO)
        val ONE = IntegerNumber(BI_ONE)
        val MINUS_ONE = IntegerNumber(-BI_ONE)

        fun of(value: BigInteger): IntegerNumber = IntegerNumber(value)
        fun of(value: Long): IntegerNumber = IntegerNumber(bigIntegerOf(value))
        fun of(value: Int): IntegerNumber = IntegerNumber(bigIntegerOf(value))
        
        fun parse(string: String): IntegerNumber = IntegerNumber(bigIntegerOf(string))
    }
}
