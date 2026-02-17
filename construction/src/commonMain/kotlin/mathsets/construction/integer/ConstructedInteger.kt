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
 * Axiomatic construction of the integers as equivalence classes of pairs of
 * Von Neumann naturals under the relation:
 *
 * `(a, b) ~ (c, d)  <=>  a + d = b + c`
 *
 * The pair `(a, b)` represents the integer `a - b`. Equality and hashing are
 * defined by the canonical kernel value, so distinct representatives of the
 * same equivalence class compare as equal.
 *
 * @property left the first component of the representative pair.
 * @property right the second component of the representative pair.
 * @see ConstructedIntegerArithmetic
 * @see ConstructedIntegerIsomorphism
 */
class ConstructedInteger private constructor(
    val left: VonNeumannNatural,
    val right: VonNeumannNatural,
    private val kernelValue: IntegerNumber
) : Comparable<ConstructedInteger>, MathElement {
    /**
     * Returns the representative pair `(left, right)` of this equivalence class.
     */
    fun representative(): Pair<VonNeumannNatural, VonNeumannNatural> = left to right

    /**
     * Projects this constructed integer to its kernel [IntegerNumber] counterpart.
     */
    fun toKernel(): IntegerNumber = kernelValue

    /**
     * Adds two constructed integers by component-wise addition of representatives:
     * `(a, b) + (c, d) = (a + c, b + d)`.
     *
     * @param other the addend.
     * @return the sum.
     */
    operator fun plus(other: ConstructedInteger): ConstructedInteger = of(
        left = VonNeumannNaturalArithmetic.add(left, other.left),
        right = VonNeumannNaturalArithmetic.add(right, other.right)
    )

    /**
     * Subtracts [other] from this integer: `this - other = this + (-other)`.
     *
     * @param other the subtrahend.
     * @return the difference.
     */
    operator fun minus(other: ConstructedInteger): ConstructedInteger = this + (-other)

    /**
     * Returns the additive inverse by swapping the pair components: `-(a, b) = (b, a)`.
     */
    operator fun unaryMinus(): ConstructedInteger = of(left = right, right = left)

    /**
     * Multiplies two constructed integers using the identity:
     * `(a, b) * (c, d) = (ac + bd, ad + bc)`.
     *
     * @param other the multiplier.
     * @return the product.
     */
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
        /** The constructed integer zero, represented as `(0, 0)`. */
        val ZERO: ConstructedInteger = fromKernel(IntegerNumber.ZERO)

        /** The constructed integer one, represented as `(1, 0)`. */
        val ONE: ConstructedInteger = fromKernel(IntegerNumber.ONE)

        /**
         * Creates a [ConstructedInteger] from a pair of Von Neumann naturals.
         *
         * @param left the first component (positive part).
         * @param right the second component (negative part).
         * @return the constructed integer representing `left - right`.
         */
        fun of(left: VonNeumannNatural, right: VonNeumannNatural): ConstructedInteger {
            val kernelLeft = NaturalIsomorphism.toKernel(left)
            val kernelRight = NaturalIsomorphism.toKernel(right)
            val kernel = IntegerNumber.parse(kernelLeft.toString()) - IntegerNumber.parse(kernelRight.toString())
            return ConstructedInteger(left, right, kernel)
        }

        /**
         * Creates a [ConstructedInteger] from a kernel [IntegerNumber] in canonical form.
         *
         * Non-negative values are represented as `(n, 0)`; negative values as `(0, |n|)`.
         *
         * @param value the kernel integer.
         * @return the corresponding constructed integer.
         */
        fun fromKernel(value: IntegerNumber): ConstructedInteger = if (value >= IntegerNumber.ZERO) {
            val natural = NaturalNumber.parse(value.toString())
            of(natural.toVonNeumannNatural(), VonNeumannNatural.ZERO)
        } else {
            val magnitude = value.absNatural()
            of(VonNeumannNatural.ZERO, magnitude.toVonNeumannNatural())
        }

        /**
         * Tests whether two pairs of Von Neumann naturals belong to the same
         * equivalence class: `(a, b) ~ (c, d) <=> a + d = b + c`.
         *
         * @param p the first pair.
         * @param q the second pair.
         * @return `true` if the pairs are equivalent.
         */
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

/**
 * [Arithmetic] implementation for [ConstructedInteger].
 *
 * Delegates to the operator overloads defined on [ConstructedInteger], with
 * division falling back to the kernel for truncated integer division.
 */
object ConstructedIntegerArithmetic : Arithmetic<ConstructedInteger> {
    /** The additive identity. */
    override val zero: ConstructedInteger = ConstructedInteger.ZERO

    /** The multiplicative identity. */
    override val one: ConstructedInteger = ConstructedInteger.ONE

    override fun add(a: ConstructedInteger, b: ConstructedInteger): ConstructedInteger = a + b

    override fun subtract(a: ConstructedInteger, b: ConstructedInteger): ConstructedInteger = a - b

    override fun multiply(a: ConstructedInteger, b: ConstructedInteger): ConstructedInteger = a * b

    /**
     * Integer (truncated) division, delegated to the kernel.
     *
     * @param a the dividend.
     * @param b the divisor (must not be zero).
     * @return the quotient.
     */
    override fun divide(a: ConstructedInteger, b: ConstructedInteger): ConstructedInteger {
        val kernel = a.toKernel() / b.toKernel()
        return ConstructedInteger.fromKernel(kernel)
    }

    override fun compare(a: ConstructedInteger, b: ConstructedInteger): Int = a.compareTo(b)
}

/**
 * Total order on [ConstructedInteger] delegating to the natural [Comparable] implementation.
 */
object ConstructedIntegerOrder {
    /** Returns `true` if `a <= b`. */
    fun lessOrEqual(a: ConstructedInteger, b: ConstructedInteger): Boolean = a <= b

    /** Returns `true` if `a < b`. */
    fun lessThan(a: ConstructedInteger, b: ConstructedInteger): Boolean = a < b

    /** Returns `true` if `a >= b`. */
    fun greaterOrEqual(a: ConstructedInteger, b: ConstructedInteger): Boolean = a >= b

    /** Returns `true` if `a > b`. */
    fun greaterThan(a: ConstructedInteger, b: ConstructedInteger): Boolean = a > b
}

/**
 * Canonical embedding of natural numbers into the constructed integers.
 *
 * Every natural `n` is embedded as the equivalence class `(n, 0)`.
 */
object NaturalIntegerEmbedding {
    /**
     * Embeds a [VonNeumannNatural] into [ConstructedInteger].
     *
     * @param natural the Von Neumann natural to embed.
     * @return the corresponding constructed integer `(natural, 0)`.
     */
    fun embed(natural: VonNeumannNatural): ConstructedInteger =
        ConstructedInteger.of(natural, VonNeumannNatural.ZERO)

    /**
     * Embeds a kernel [NaturalNumber] into [ConstructedInteger].
     *
     * @param natural the kernel natural to embed.
     * @return the corresponding constructed integer.
     */
    fun embed(natural: NaturalNumber): ConstructedInteger =
        ConstructedInteger.of(natural.toVonNeumannNatural(), VonNeumannNatural.ZERO)
}

/**
 * Isomorphism between [ConstructedInteger] and the kernel [IntegerNumber],
 * with round-trip verification.
 */
object ConstructedIntegerIsomorphism {
    /**
     * Projects a [ConstructedInteger] to its kernel [IntegerNumber].
     */
    fun toKernel(value: ConstructedInteger): IntegerNumber = value.toKernel()

    /**
     * Lifts a kernel [IntegerNumber] to a [ConstructedInteger].
     */
    fun fromKernel(value: IntegerNumber): ConstructedInteger = ConstructedInteger.fromKernel(value)

    /**
     * Verifies the round-trip property for all integers in `-limit..limit`.
     *
     * @param limit the upper bound (inclusive) of the test range.
     * @return `true` if the round-trip holds for every value.
     */
    fun verifyRoundTrip(limit: Int): Boolean {
        for (n in -limit..limit) {
            val kernel = IntegerNumber.of(n)
            if (toKernel(fromKernel(kernel)) != kernel) return false
            if (fromKernel(toKernel(fromKernel(kernel))) != fromKernel(kernel)) return false
        }
        return true
    }
}

/**
 * Extension to embed a kernel [NaturalNumber] into [ConstructedInteger].
 */
fun NaturalNumber.toMathInteger(): ConstructedInteger = NaturalIntegerEmbedding.embed(this)

/**
 * Extension to convert a kernel [IntegerNumber] to [ConstructedInteger].
 */
fun IntegerNumber.toMathInteger(): ConstructedInteger = ConstructedInteger.fromKernel(this)
