package mathsets.construction.integer

import mathsets.construction.natural.NaturalIsomorphism
import mathsets.construction.natural.VonNeumannNatural
import mathsets.construction.natural.VonNeumannNaturalArithmetic
import mathsets.construction.natural.toVonNeumannNatural
import mathsets.kernel.Arithmetic
import mathsets.kernel.IntegerNumber
import mathsets.kernel.MathElement
import mathsets.kernel.NaturalNumber

/**
 * Inteiro construído como classe de equivalência de pares de naturais:
 * (a, b) ~ (c, d) <=> a + d = b + c.
 */
class ConstructedInteger private constructor(
    val left: VonNeumannNatural,
    val right: VonNeumannNatural,
    private val kernelValue: IntegerNumber
) : Comparable<ConstructedInteger>, MathElement {
    fun representative(): Pair<VonNeumannNatural, VonNeumannNatural> = left to right

    fun toKernel(): IntegerNumber = kernelValue

    operator fun plus(other: ConstructedInteger): ConstructedInteger = of(
        left = VonNeumannNaturalArithmetic.add(left, other.left),
        right = VonNeumannNaturalArithmetic.add(right, other.right)
    )

    operator fun minus(other: ConstructedInteger): ConstructedInteger = this + (-other)

    operator fun unaryMinus(): ConstructedInteger = of(left = right, right = left)

    operator fun times(other: ConstructedInteger): ConstructedInteger {
        val ac = VonNeumannNaturalArithmetic.multiply(left, other.left)
        val bd = VonNeumannNaturalArithmetic.multiply(right, other.right)
        val ad = VonNeumannNaturalArithmetic.multiply(left, other.right)
        val bc = VonNeumannNaturalArithmetic.multiply(right, other.left)

        return of(
            left = VonNeumannNaturalArithmetic.add(ac, bd),
            right = VonNeumannNaturalArithmetic.add(ad, bc)
        )
    }

    override fun compareTo(other: ConstructedInteger): Int = kernelValue.compareTo(other.kernelValue)

    override fun equals(other: Any?): Boolean =
        other is ConstructedInteger && kernelValue == other.kernelValue

    override fun hashCode(): Int = kernelValue.hashCode()

    override fun toString(): String = "[$left,$right]~($kernelValue)"

    companion object {
        val ZERO: ConstructedInteger = fromKernel(IntegerNumber.ZERO)
        val ONE: ConstructedInteger = fromKernel(IntegerNumber.ONE)

        fun of(left: VonNeumannNatural, right: VonNeumannNatural): ConstructedInteger {
            val kernelLeft = NaturalIsomorphism.toKernel(left)
            val kernelRight = NaturalIsomorphism.toKernel(right)
            val kernel = IntegerNumber.parse(kernelLeft.toString()) - IntegerNumber.parse(kernelRight.toString())
            return ConstructedInteger(left, right, kernel)
        }

        fun fromKernel(value: IntegerNumber): ConstructedInteger = if (value >= IntegerNumber.ZERO) {
            val natural = NaturalNumber.parse(value.toString())
            of(natural.toVonNeumannNatural(), VonNeumannNatural.ZERO)
        } else {
            val magnitude = value.absNatural()
            of(VonNeumannNatural.ZERO, magnitude.toVonNeumannNatural())
        }

        fun areEquivalent(
            p: Pair<VonNeumannNatural, VonNeumannNatural>,
            q: Pair<VonNeumannNatural, VonNeumannNatural>
        ): Boolean {
            val (a, b) = p
            val (c, d) = q
            val leftSide = VonNeumannNaturalArithmetic.add(a, d)
            val rightSide = VonNeumannNaturalArithmetic.add(b, c)
            return leftSide == rightSide
        }
    }
}

object ConstructedIntegerArithmetic : Arithmetic<ConstructedInteger> {
    override val zero: ConstructedInteger = ConstructedInteger.ZERO
    override val one: ConstructedInteger = ConstructedInteger.ONE

    override fun add(a: ConstructedInteger, b: ConstructedInteger): ConstructedInteger = a + b

    override fun subtract(a: ConstructedInteger, b: ConstructedInteger): ConstructedInteger = a - b

    override fun multiply(a: ConstructedInteger, b: ConstructedInteger): ConstructedInteger = a * b

    override fun divide(a: ConstructedInteger, b: ConstructedInteger): ConstructedInteger {
        val kernel = a.toKernel() / b.toKernel()
        return ConstructedInteger.fromKernel(kernel)
    }

    override fun compare(a: ConstructedInteger, b: ConstructedInteger): Int = a.compareTo(b)
}

object ConstructedIntegerOrder {
    fun lessOrEqual(a: ConstructedInteger, b: ConstructedInteger): Boolean = a <= b
    fun lessThan(a: ConstructedInteger, b: ConstructedInteger): Boolean = a < b
    fun greaterOrEqual(a: ConstructedInteger, b: ConstructedInteger): Boolean = a >= b
    fun greaterThan(a: ConstructedInteger, b: ConstructedInteger): Boolean = a > b
}

object NaturalIntegerEmbedding {
    fun embed(natural: VonNeumannNatural): ConstructedInteger =
        ConstructedInteger.of(natural, VonNeumannNatural.ZERO)

    fun embed(natural: NaturalNumber): ConstructedInteger =
        ConstructedInteger.of(natural.toVonNeumannNatural(), VonNeumannNatural.ZERO)
}

object ConstructedIntegerIsomorphism {
    fun toKernel(value: ConstructedInteger): IntegerNumber = value.toKernel()

    fun fromKernel(value: IntegerNumber): ConstructedInteger = ConstructedInteger.fromKernel(value)

    fun verifyRoundTrip(limit: Int): Boolean {
        for (n in -limit..limit) {
            val kernel = IntegerNumber.of(n)
            if (toKernel(fromKernel(kernel)) != kernel) return false
            if (fromKernel(toKernel(fromKernel(kernel))) != fromKernel(kernel)) return false
        }
        return true
    }
}

fun NaturalNumber.toMathInteger(): ConstructedInteger = NaturalIntegerEmbedding.embed(this)

fun IntegerNumber.toMathInteger(): ConstructedInteger = ConstructedInteger.fromKernel(this)
