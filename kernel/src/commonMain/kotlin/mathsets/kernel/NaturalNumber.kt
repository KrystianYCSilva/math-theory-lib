package mathsets.kernel

import mathsets.kernel.platform.BigInteger
import mathsets.kernel.platform.BI_ZERO
import mathsets.kernel.platform.BI_ONE
import mathsets.kernel.platform.toBigInteger
import mathsets.kernel.platform.bigIntegerOf
import mathsets.kernel.platform.*
import kotlin.jvm.JvmInline

/**
 * Representa um número natural (ℕ = {0, 1, 2, ...}).
 * 
 * Implementado como uma value class sobre BigInteger para performance.
 */
@JvmInline
value class NaturalNumber(val value: BigInteger) : Comparable<NaturalNumber> {
    
    init {
        // Validation handled at factory or assumed for performance in internal paths
    }

    operator fun plus(other: NaturalNumber): NaturalNumber = 
        NaturalNumber(this.value + other.value)

    operator fun times(other: NaturalNumber): NaturalNumber = 
        NaturalNumber(this.value * other.value)

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

    override fun toString(): String = value.toString()

    companion object {
        val ZERO = NaturalNumber(BI_ZERO)
        val ONE = NaturalNumber(BI_ONE)

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
