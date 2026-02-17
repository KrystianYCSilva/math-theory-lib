package mathsets.construction.rational

import mathsets.construction.integer.ConstructedInteger
import mathsets.construction.integer.ConstructedIntegerIsomorphism
import mathsets.construction.integer.toMathInteger
import mathsets.kernel.Arithmetic
import mathsets.kernel.IntegerNumber
import mathsets.kernel.MathElement
import mathsets.kernel.NaturalNumber
import mathsets.kernel.RationalNumber

/**
 * Racional construído como classe de equivalência de pares de inteiros:
 * (a, b) ~ (c, d) <=> a*d = b*c, com b != 0 e d != 0.
 */
class ConstructedRational private constructor(
    val numerator: ConstructedInteger,
    val denominator: ConstructedInteger,
    private val kernelValue: RationalNumber
) : Comparable<ConstructedRational>, MathElement {
    fun representative(): Pair<ConstructedInteger, ConstructedInteger> = numerator to denominator

    fun toKernel(): RationalNumber = kernelValue

    operator fun plus(other: ConstructedRational): ConstructedRational {
        val ad = numerator * other.denominator
        val bc = other.numerator * denominator
        val bd = denominator * other.denominator
        return of(ad + bc, bd)
    }

    operator fun minus(other: ConstructedRational): ConstructedRational = this + (-other)

    operator fun unaryMinus(): ConstructedRational = of(-numerator, denominator)

    operator fun times(other: ConstructedRational): ConstructedRational =
        of(numerator * other.numerator, denominator * other.denominator)

    operator fun div(other: ConstructedRational): ConstructedRational {
        require(other.numerator != ConstructedInteger.ZERO) { "Division by zero rational." }
        return of(numerator * other.denominator, denominator * other.numerator)
    }

    override fun compareTo(other: ConstructedRational): Int = kernelValue.compareTo(other.kernelValue)

    override fun equals(other: Any?): Boolean =
        other is ConstructedRational && kernelValue == other.kernelValue

    override fun hashCode(): Int = kernelValue.hashCode()

    override fun toString(): String = "[$numerator/$denominator]~($kernelValue)"

    companion object {
        val ZERO: ConstructedRational = fromKernel(RationalNumber.ZERO)
        val ONE: ConstructedRational = fromKernel(RationalNumber.ONE)

        fun of(numerator: ConstructedInteger, denominator: ConstructedInteger): ConstructedRational {
            require(denominator != ConstructedInteger.ZERO) { "Rational denominator cannot be zero." }
            val kernel = RationalNumber.of(numerator.toKernel(), denominator.toKernel())
            return ConstructedRational(numerator, denominator, kernel)
        }

        fun fromKernel(value: RationalNumber): ConstructedRational {
            val numerator = ConstructedIntegerIsomorphism.fromKernel(value.numeratorAsInteger())
            val denominator = ConstructedIntegerIsomorphism.fromKernel(value.denominatorAsInteger())
            return of(numerator, denominator)
        }

        fun areEquivalent(
            p: Pair<ConstructedInteger, ConstructedInteger>,
            q: Pair<ConstructedInteger, ConstructedInteger>
        ): Boolean {
            val (a, b) = p
            val (c, d) = q
            require(b != ConstructedInteger.ZERO && d != ConstructedInteger.ZERO) {
                "Equivalence requires non-zero denominators."
            }
            return (a * d) == (b * c)
        }
    }
}

object ConstructedRationalArithmetic : Arithmetic<ConstructedRational> {
    override val zero: ConstructedRational = ConstructedRational.ZERO
    override val one: ConstructedRational = ConstructedRational.ONE

    override fun add(a: ConstructedRational, b: ConstructedRational): ConstructedRational = a + b
    override fun subtract(a: ConstructedRational, b: ConstructedRational): ConstructedRational = a - b
    override fun multiply(a: ConstructedRational, b: ConstructedRational): ConstructedRational = a * b
    override fun divide(a: ConstructedRational, b: ConstructedRational): ConstructedRational = a / b
    override fun compare(a: ConstructedRational, b: ConstructedRational): Int = a.compareTo(b)
}

object ConstructedRationalOrder {
    fun lessOrEqual(a: ConstructedRational, b: ConstructedRational): Boolean = a <= b
    fun lessThan(a: ConstructedRational, b: ConstructedRational): Boolean = a < b
    fun greaterOrEqual(a: ConstructedRational, b: ConstructedRational): Boolean = a >= b
    fun greaterThan(a: ConstructedRational, b: ConstructedRational): Boolean = a > b
}

object IntegerRationalEmbedding {
    fun embed(integer: ConstructedInteger): ConstructedRational =
        ConstructedRational.of(integer, ConstructedInteger.ONE)

    fun embed(integer: IntegerNumber): ConstructedRational =
        embed(integer.toMathInteger())
}

object ConstructedRationalIsomorphism {
    fun toKernel(value: ConstructedRational): RationalNumber = value.toKernel()

    fun fromKernel(value: RationalNumber): ConstructedRational = ConstructedRational.fromKernel(value)

    fun verifyRoundTrip(range: Int): Boolean {
        for (n in -range..range) {
            for (d in 1..range) {
                val kernel = RationalNumber.of(n, d)
                val roundTrip = toKernel(fromKernel(kernel))
                if (roundTrip != kernel) return false
            }
        }
        return true
    }
}

object RationalDensity {
    private val TWO: ConstructedRational =
        ConstructedRational.of(ConstructedInteger.fromKernel(IntegerNumber.of(2)), ConstructedInteger.ONE)

    fun between(a: ConstructedRational, b: ConstructedRational): ConstructedRational =
        (a + b) / TWO
}

fun ConstructedInteger.toMathRational(): ConstructedRational = IntegerRationalEmbedding.embed(this)

fun IntegerNumber.toMathRational(): ConstructedRational = IntegerRationalEmbedding.embed(this)

fun NaturalNumber.toMathRational(): ConstructedRational = this.toMathInteger().toMathRational()
