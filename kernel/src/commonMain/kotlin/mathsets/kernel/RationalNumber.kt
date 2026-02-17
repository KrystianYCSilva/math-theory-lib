package mathsets.kernel

import mathsets.kernel.platform.BI_ONE
import mathsets.kernel.platform.BI_ZERO
import mathsets.kernel.platform.BigInteger
import mathsets.kernel.platform.bigIntegerOf
import kotlin.jvm.JvmInline

/**
 * Representa um número racional (ℚ).
 *
 * Armazena par (numerador, denominador) sempre normalizado.
 */
@JvmInline
value class RationalNumber private constructor(private val packed: Pair<BigInteger, BigInteger>) :
    Comparable<RationalNumber>, MathElement {

    val numerator: BigInteger get() = packed.first
    val denominator: BigInteger get() = packed.second

    fun numeratorAsInteger(): IntegerNumber = IntegerNumber.parse(numerator.toString())
    fun denominatorAsInteger(): IntegerNumber = IntegerNumber.parse(denominator.toString())

    operator fun plus(other: RationalNumber): RationalNumber {
        // a/b + c/d = (ad + bc) / bd
        val num = (this.numerator * other.denominator) + (this.denominator * other.numerator)
        val den = this.denominator * other.denominator
        return create(num, den)
    }

    operator fun minus(other: RationalNumber): RationalNumber {
        val num = (this.numerator * other.denominator) - (this.denominator * other.numerator)
        val den = this.denominator * other.denominator
        return create(num, den)
    }

    operator fun times(other: RationalNumber): RationalNumber {
        return create(
            this.numerator * other.numerator,
            this.denominator * other.denominator
        )
    }

    operator fun div(other: RationalNumber): RationalNumber {
        if (other.numerator == BI_ZERO) throw ArithmeticException("Division by zero")
        return create(
            this.numerator * other.denominator,
            this.denominator * other.numerator
        )
    }

    operator fun unaryMinus(): RationalNumber = RationalNumber(Pair(-numerator, denominator))

    override fun compareTo(other: RationalNumber): Int {
        val left = this.numerator * other.denominator
        val right = this.denominator * other.numerator
        return left.compareTo(right)
    }

    override fun toString(): String {
        return if (denominator == BI_ONE) numerator.toString() else "$numerator/$denominator"
    }

    companion object {
        val ZERO = RationalNumber(Pair(BI_ZERO, BI_ONE))
        val ONE = RationalNumber(Pair(BI_ONE, BI_ONE))

        fun of(numerator: BigInteger, denominator: BigInteger = BI_ONE): RationalNumber {
            return create(numerator, denominator)
        }

        fun of(numerator: Int, denominator: Int = 1): RationalNumber {
            return create(bigIntegerOf(numerator), bigIntegerOf(denominator))
        }

        fun of(numerator: IntegerNumber, denominator: IntegerNumber = IntegerNumber.ONE): RationalNumber {
            return create(numerator.value, denominator.value)
        }

        private fun create(numerator: BigInteger, denominator: BigInteger): RationalNumber {
            if (denominator == BI_ZERO) throw ArithmeticException("Denominator cannot be zero")

            var num = numerator
            var den = denominator

            if (den < BI_ZERO) {
                num = -num
                den = -den
            }

            val common = gcd(num.abs(), den)
            return RationalNumber(Pair(num / common, den / common))
        }

        private fun gcd(a: BigInteger, b: BigInteger): BigInteger {
            var x = a
            var y = b
            while (y != BI_ZERO) {
                val temp = y
                y = x % y
                x = temp
            }
            return x
        }
    }
}
