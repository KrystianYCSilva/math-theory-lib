package mathsets.construction.real

import mathsets.construction.rational.ConstructedRational
import mathsets.construction.rational.toMathRational
import mathsets.kernel.Arithmetic
import mathsets.kernel.IntegerNumber
import mathsets.kernel.MathElement
import mathsets.kernel.NaturalNumber
import mathsets.kernel.RationalNumber
import mathsets.kernel.RealNumber

/**
 * Real construído a partir de aproximações racionais e com canal explícito
 * para o kernel numérico.
 *
 * A lista de aproximações representa o lado construtivo (sequência racional),
 * enquanto `kernelValue` permite interoperabilidade e verificação eficiente.
 */
class ConstructedReal private constructor(
    private val rationalApproximants: List<ConstructedRational>,
    private val kernelValue: RealNumber
) : Comparable<ConstructedReal>, MathElement {
    fun approximants(): List<ConstructedRational> = rationalApproximants.toList()

    fun toKernel(): RealNumber = kernelValue

    operator fun plus(other: ConstructedReal): ConstructedReal =
        fromKernel(kernelValue + other.kernelValue)

    operator fun minus(other: ConstructedReal): ConstructedReal =
        fromKernel(kernelValue - other.kernelValue)

    operator fun unaryMinus(): ConstructedReal = fromKernel(-kernelValue)

    operator fun times(other: ConstructedReal): ConstructedReal =
        fromKernel(kernelValue * other.kernelValue)

    operator fun div(other: ConstructedReal): ConstructedReal =
        fromKernel(kernelValue / other.kernelValue)

    override fun compareTo(other: ConstructedReal): Int = kernelValue.compareTo(other.kernelValue)

    override fun equals(other: Any?): Boolean =
        other is ConstructedReal && kernelValue == other.kernelValue

    override fun hashCode(): Int = kernelValue.hashCode()

    override fun toString(): String = kernelValue.toString()

    companion object {
        val ZERO: ConstructedReal = fromKernel(RealNumber.ZERO)
        val ONE: ConstructedReal = fromKernel(RealNumber.ONE)

        fun of(rational: ConstructedRational): ConstructedReal =
            ConstructedReal(
                rationalApproximants = listOf(rational),
                kernelValue = RealNumber.of(rational.toKernel())
            )

        fun of(approximants: List<ConstructedRational>): ConstructedReal {
            require(approximants.isNotEmpty()) {
                "ConstructedReal requires at least one rational approximant."
            }
            val kernel = RealNumber.of(approximants.last().toKernel())
            return ConstructedReal(approximants, kernel)
        }

        fun fromKernel(value: RealNumber): ConstructedReal =
            ConstructedReal(rationalApproximants = emptyList(), kernelValue = value)
    }
}

object ConstructedRealArithmetic : Arithmetic<ConstructedReal> {
    override val zero: ConstructedReal = ConstructedReal.ZERO
    override val one: ConstructedReal = ConstructedReal.ONE

    override fun add(a: ConstructedReal, b: ConstructedReal): ConstructedReal = a + b

    override fun subtract(a: ConstructedReal, b: ConstructedReal): ConstructedReal = a - b

    override fun multiply(a: ConstructedReal, b: ConstructedReal): ConstructedReal = a * b

    override fun divide(a: ConstructedReal, b: ConstructedReal): ConstructedReal = a / b

    override fun compare(a: ConstructedReal, b: ConstructedReal): Int = a.compareTo(b)
}

object ConstructedRealOrder {
    fun lessOrEqual(a: ConstructedReal, b: ConstructedReal): Boolean = a <= b
    fun lessThan(a: ConstructedReal, b: ConstructedReal): Boolean = a < b
    fun greaterOrEqual(a: ConstructedReal, b: ConstructedReal): Boolean = a >= b
    fun greaterThan(a: ConstructedReal, b: ConstructedReal): Boolean = a > b
}

object RationalRealEmbedding {
    fun embed(rational: ConstructedRational): ConstructedReal = ConstructedReal.of(rational)

    fun embed(rational: RationalNumber): ConstructedReal = ConstructedReal.of(
        ConstructedRational.fromKernel(rational)
    )
}

object ConstructedRealIsomorphism {
    fun toKernel(value: ConstructedReal): RealNumber = value.toKernel()

    fun fromKernel(value: RealNumber): ConstructedReal = ConstructedReal.fromKernel(value)

    fun verifyRoundTrip(decimalSamples: Iterable<String>): Boolean {
        for (sample in decimalSamples) {
            val kernel = RealNumber.parse(sample)
            val roundTrip = toKernel(fromKernel(kernel))
            if (roundTrip != kernel) return false
        }
        return true
    }

    fun verifyRationalEmbedding(range: Int): Boolean {
        require(range > 0) { "range must be > 0" }
        for (n in -range..range) {
            for (d in 1..range) {
                val rational = RationalNumber.of(n, d)
                val constructed = RationalRealEmbedding.embed(rational)
                if (constructed.toKernel() != RealNumber.of(rational)) return false
            }
        }
        return true
    }
}

fun ConstructedRational.toMathReal(): ConstructedReal = RationalRealEmbedding.embed(this)

fun RationalNumber.toMathReal(): ConstructedReal = RationalRealEmbedding.embed(this)

fun IntegerNumber.toMathReal(): ConstructedReal = this.toMathRational().toMathReal()

fun NaturalNumber.toMathReal(): ConstructedReal = this.toMathRational().toMathReal()

fun RealNumber.toMathReal(): ConstructedReal = ConstructedReal.fromKernel(this)
