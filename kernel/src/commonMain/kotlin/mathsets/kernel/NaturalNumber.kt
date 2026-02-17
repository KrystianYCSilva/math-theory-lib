package mathsets.kernel

import mathsets.kernel.platform.BigInteger
import mathsets.kernel.platform.BI_ONE
import mathsets.kernel.platform.BI_ZERO
import mathsets.kernel.platform.bigIntegerOf
import mathsets.kernel.platform.toBigInteger
import mathsets.kernel.platform.*
import kotlin.jvm.JvmInline

/**
 * Representa um número natural (ℕ = {0, 1, 2, ...}).
 * 
 * Implementado como uma value class sobre BigInteger para performance.
 */
@JvmInline
value class NaturalNumber(val value: BigInteger) : Comparable<NaturalNumber>, MathElement {
    
    init {
        // Validation handled at factory or assumed for performance in internal paths
    }

    operator fun plus(other: NaturalNumber): NaturalNumber = 
        NaturalNumber(this.value + other.value)

    operator fun times(other: NaturalNumber): NaturalNumber = 
        NaturalNumber(this.value * other.value)

    operator fun minus(other: NaturalNumber): NaturalNumber {
        require(this >= other) { "Subtraction is partial in N: $this - $other is undefined" }
        return NaturalNumber(this.value - other.value)
    }

    infix fun pow(exponent: NaturalNumber): NaturalNumber {
        require(exponent.value <= MAX_INT_BIG_INTEGER) {
            "Exponent too large for BigInteger.pow(Int): ${exponent.value}"
        }
        return NaturalNumber(this.value.pow(exponent.value.toInt()))
    }

    operator fun div(other: NaturalNumber): NaturalNumber =
        NaturalNumber(this.value / other.value) 

    operator fun rem(other: NaturalNumber): NaturalNumber =
        NaturalNumber(this.value % other.value)

    override fun compareTo(other: NaturalNumber): Int = 
        this.value.compareTo(other.value)

    fun succ(): NaturalNumber = NaturalNumber(this.value + BI_ONE)

    fun pred(): NaturalNumber {
        if (isZero()) throw ArithmeticException("Predecessor of 0 is undefined in N")
        return NaturalNumber(this.value - BI_ONE)
    }

    fun isZero(): Boolean = this.value == BI_ZERO
    
    fun isEven(): Boolean = (this.value % 2.toBigInteger()) == BI_ZERO
    
    fun isOdd(): Boolean = !isEven()

    fun isPrime(): Boolean {
        if (value < TWO_BIG_INTEGER) return false
        if (value == TWO_BIG_INTEGER || value == THREE_BIG_INTEGER) return true
        if (value % TWO_BIG_INTEGER == BI_ZERO || value % THREE_BIG_INTEGER == BI_ZERO) return false

        var i = bigIntegerOf(5)
        var step = bigIntegerOf(2)
        while (i * i <= value) {
            if (value % i == BI_ZERO) return false
            i += step
            step = if (step == bigIntegerOf(2)) bigIntegerOf(4) else bigIntegerOf(2)
        }
        return true
    }

    override fun toString(): String = value.toString()

    companion object {
        val ZERO = NaturalNumber(BI_ZERO)
        val ONE = NaturalNumber(BI_ONE)
        private val TWO_BIG_INTEGER = bigIntegerOf(2)
        private val THREE_BIG_INTEGER = bigIntegerOf(3)
        private val MAX_INT_BIG_INTEGER = bigIntegerOf(Int.MAX_VALUE)

        fun of(value: BigInteger): NaturalNumber {
            require(value >= BI_ZERO) { "NaturalNumber must be non-negative: $value" }
            return NaturalNumber(value)
        }

        fun of(value: Long): NaturalNumber {
            require(value >= 0) { "NaturalNumber must be non-negative: $value" }
            return NaturalNumber(bigIntegerOf(value))
        }

        fun of(value: Int): NaturalNumber {
            require(value >= 0) { "NaturalNumber must be non-negative: $value" }
            return NaturalNumber(bigIntegerOf(value))
        }
        
        fun parse(string: String): NaturalNumber {
             val bigInt = bigIntegerOf(string)
             return of(bigInt)
        }
    }
}
