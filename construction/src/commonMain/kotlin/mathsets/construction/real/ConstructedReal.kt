package mathsets.construction.real

import mathsets.construction.rational.ConstructedRational
import mathsets.construction.rational.toMathRational
import mathsets.kernel.Arithmetic
import mathsets.kernel.IntegerNumber
import mathsets.kernel.MathElement
import mathsets.kernel.NaturalNumber
import mathsets.kernel.RationalNumber
import mathsets.kernel.RealNumber

private const val DEFAULT_APPROXIMATION_INDEX: Int = 24
private const val DEFAULT_COMPARISON_INDEX: Int = 28
private const val DIVISION_RETRY_WINDOW: Int = 32

private interface CauchyRepresentative {
    val description: String
    fun term(index: NaturalNumber): ConstructedRational
    fun modulus(epsilonIndex: NaturalNumber): NaturalNumber
}

private class ConstantRepresentative(
    private val value: ConstructedRational
) : CauchyRepresentative {
    override val description: String = "constant"

    override fun term(index: NaturalNumber): ConstructedRational = value

    override fun modulus(epsilonIndex: NaturalNumber): NaturalNumber = NaturalNumber.ZERO
}

private class EventuallyConstantRepresentative(
    private val prefix: List<ConstructedRational>
) : CauchyRepresentative {
    init {
        require(prefix.isNotEmpty()) { "Prefix cannot be empty for eventual-constant representative." }
    }

    override val description: String = "eventually-constant-prefix"

    override fun term(index: NaturalNumber): ConstructedRational {
        val indexAsInt = index.toIndexInt()
        return if (indexAsInt < prefix.size) prefix[indexAsInt] else prefix.last()
    }

    override fun modulus(epsilonIndex: NaturalNumber): NaturalNumber =
        NaturalNumber.of((prefix.size - 1).coerceAtLeast(0))
}

private class BinaryRepresentative(
    private val left: CauchyRepresentative,
    private val right: CauchyRepresentative,
    override val description: String,
    private val operation: (ConstructedRational, ConstructedRational) -> ConstructedRational
) : CauchyRepresentative {
    override fun term(index: NaturalNumber): ConstructedRational =
        operation(left.term(index), right.term(index))

    override fun modulus(epsilonIndex: NaturalNumber): NaturalNumber =
        maxNatural(left.modulus(epsilonIndex.succ()), right.modulus(epsilonIndex.succ()))
}

private class UnaryRepresentative(
    private val origin: CauchyRepresentative,
    override val description: String,
    private val operation: (ConstructedRational) -> ConstructedRational
) : CauchyRepresentative {
    override fun term(index: NaturalNumber): ConstructedRational = operation(origin.term(index))

    override fun modulus(epsilonIndex: NaturalNumber): NaturalNumber = origin.modulus(epsilonIndex)
}

/**
 * Axiomatic construction of the real numbers as (computable) equivalence classes of
 * Cauchy sequences over [ConstructedRational].
 *
 * Each real number is represented by a [CauchyRepresentative] that provides:
 * - The n-th rational approximation via [approximateRational].
 * - A Cauchy modulus function via [modulusAt]: for a given epsilon index `k`,
 *   the modulus `N` guarantees `|a_i - a_j| < 1/k` for all `i, j >= N`.
 *
 * Arithmetic operations (addition, multiplication, etc.) compose representatives
 * lazily, building a tree of rational operations evaluated at each index.
 *
 * A kernel [RealNumber] projection is cached for interoperability with the
 * efficient kernel layer.
 *
 * @see ConstructedRealArithmetic
 * @see ConstructedRealIsomorphism
 * @see RationalRealEmbedding
 */
class ConstructedReal private constructor(
    private val representative: CauchyRepresentative,
    private val kernelProjection: RealNumber
) : Comparable<ConstructedReal>, MathElement {
    /**
     * Returns a human-readable description of the internal Cauchy representative
     * (e.g. "constant", "sum(...)", "product(...)").
     */
    fun representativeDescription(): String = representative.description

    /**
     * Returns the rational approximation at the given [index] in the Cauchy sequence.
     *
     * @param index the sequence index (must be non-negative).
     * @return the [ConstructedRational] approximation at position [index].
     * @throws IllegalArgumentException if [index] is negative.
     */
    fun approximateRational(index: Int): ConstructedRational {
        require(index >= 0) { "index must be non-negative." }
        return representative.term(NaturalNumber.of(index))
    }

    /**
     * Returns the Cauchy modulus at the given epsilon index.
     *
     * For epsilon = `1/epsilonIndex`, the returned value `N` guarantees that
     * `|a_i - a_j| < epsilon` for all `i, j >= N`.
     *
     * @param epsilonIndex the inverse of epsilon (must be non-negative).
     * @return the modulus [NaturalNumber].
     * @throws IllegalArgumentException if [epsilonIndex] is negative.
     */
    fun modulusAt(epsilonIndex: Int): NaturalNumber {
        require(epsilonIndex >= 0) { "epsilonIndex must be non-negative." }
        return representative.modulus(NaturalNumber.of(epsilonIndex))
    }

    /**
     * Empirically verifies the Cauchy property on a finite prefix of the sequence.
     *
     * For each `k` in `1..prefix`, checks that all terms beyond the modulus at `k`
     * satisfy `|a_i - a_j| < 1/k`.
     *
     * @param prefix the number of epsilon levels to check (must be positive).
     * @return `true` if the Cauchy property holds for all tested levels.
     * @throws IllegalArgumentException if [prefix] is not positive.
     */
    fun isCauchyOnFinitePrefix(prefix: Int): Boolean {
        require(prefix > 0) { "prefix must be positive." }
        for (k in 1..prefix) {
            val epsilon = ConstructedRational.fromKernel(RationalNumber.of(1, k))
            val threshold = modulusAt(k).toIndexInt()
            val end = threshold + minOf(prefix, 10)
            for (i in threshold..end) {
                for (j in threshold..end) {
                    val difference = absolute(approximateRational(i) - approximateRational(j))
                    if (difference >= epsilon) return false
                }
            }
        }
        return true
    }

    /**
     * Projects this constructed real to its kernel [RealNumber] counterpart.
     */
    fun toKernel(): RealNumber = kernelProjection

    /**
     * Returns `true` if this real number is zero (compared at the default precision).
     */
    fun isZero(): Boolean = compareTo(ZERO) == 0

    /**
     * Adds two constructed reals by composing their Cauchy representatives term-wise.
     *
     * @param other the addend.
     * @return the sum.
     */
    operator fun plus(other: ConstructedReal): ConstructedReal = fromRepresentative(
        BinaryRepresentative(
            left = representative,
            right = other.representative,
            description = "sum(${representative.description},${other.representative.description})",
            operation = { a, b -> a + b }
        )
    )

    /**
     * Subtracts [other] from this real: `this - other = this + (-other)`.
     *
     * @param other the subtrahend.
     * @return the difference.
     */
    operator fun minus(other: ConstructedReal): ConstructedReal = this + (-other)

    /**
     * Returns the additive inverse by negating each term of the Cauchy sequence.
     */
    operator fun unaryMinus(): ConstructedReal = fromRepresentative(
        UnaryRepresentative(
            origin = representative,
            description = "neg(${representative.description})",
            operation = { value -> -value }
        )
    )

    /**
     * Multiplies two constructed reals by composing their Cauchy representatives term-wise.
     *
     * @param other the multiplier.
     * @return the product.
     */
    operator fun times(other: ConstructedReal): ConstructedReal = fromRepresentative(
        BinaryRepresentative(
            left = representative,
            right = other.representative,
            description = "product(${representative.description},${other.representative.description})",
            operation = { a, b -> a * b }
        )
    )

    /**
     * Divides this real by [other].
     *
     * The quotient representative searches for non-zero denominator terms within
     * a retry window to handle sequences that may pass through zero at early indices.
     *
     * @param other the divisor (must not be zero).
     * @return the quotient.
     * @throws IllegalArgumentException if [other] is zero.
     * @throws ArithmeticException if no non-zero denominator term is found within the retry window.
     */
    operator fun div(other: ConstructedReal): ConstructedReal {
        require(!other.isZero()) { "Division by zero in ConstructedReal." }
        val divisionRepresentative = object : CauchyRepresentative {
            override val description: String =
                "quotient(${representative.description},${other.representative.description})"

            override fun term(index: NaturalNumber): ConstructedRational {
                var current = index
                repeat(DIVISION_RETRY_WINDOW) {
                    val denominator = other.representative.term(current)
                    if (denominator != ConstructedRational.ZERO) {
                        return representative.term(current) / denominator
                    }
                    current = current.succ()
                }
                throw ArithmeticException("Unable to find non-zero denominator term for quotient representative.")
            }

            override fun modulus(epsilonIndex: NaturalNumber): NaturalNumber =
                maxNatural(
                    representative.modulus(epsilonIndex.succ()),
                    other.representative.modulus(epsilonIndex.succ())
                )
        }
        return fromRepresentative(divisionRepresentative)
    }

    override fun compareTo(other: ConstructedReal): Int =
        approximateRational(DEFAULT_COMPARISON_INDEX).compareTo(
            other.approximateRational(DEFAULT_COMPARISON_INDEX)
        )

    override fun equals(other: Any?): Boolean =
        other is ConstructedReal &&
            approximateRational(DEFAULT_COMPARISON_INDEX) ==
            other.approximateRational(DEFAULT_COMPARISON_INDEX)

    override fun hashCode(): Int = approximateRational(DEFAULT_COMPARISON_INDEX).hashCode()

    override fun toString(): String = toKernel().toString()

    companion object {
        /** The constructed real zero (constant Cauchy sequence of `0`). */
        val ZERO: ConstructedReal = of(ConstructedRational.ZERO)

        /** The constructed real one (constant Cauchy sequence of `1`). */
        val ONE: ConstructedReal = of(ConstructedRational.ONE)

        /** The constructed real two (constant Cauchy sequence of `2`). */
        val TWO: ConstructedReal = of(ConstructedRational.fromKernel(RationalNumber.of(2, 1)))

        /**
         * Creates a [ConstructedReal] from a constant [ConstructedRational].
         *
         * The resulting Cauchy sequence is constant: every term equals [rational].
         *
         * @param rational the constant rational value.
         * @return the corresponding constructed real.
         */
        fun of(rational: ConstructedRational): ConstructedReal =
            fromRepresentative(ConstantRepresentative(rational))

        /**
         * Creates a [ConstructedReal] from an eventually-constant list of rational approximants.
         *
         * Terms beyond the list length repeat the last element.
         *
         * @param approximants the non-empty list of rational approximations.
         * @return the corresponding constructed real.
         * @throws IllegalArgumentException if [approximants] is empty.
         */
        fun of(approximants: List<ConstructedRational>): ConstructedReal =
            fromRepresentative(EventuallyConstantRepresentative(approximants))

        /**
         * Creates a [ConstructedReal] from a kernel [RealNumber] by parsing its
         * decimal string into a rational constant.
         *
         * @param value the kernel real number.
         * @return the corresponding constructed real.
         */
        fun fromKernel(value: RealNumber): ConstructedReal {
            val rational = decimalStringToRational(value.toString())
            return of(ConstructedRational.fromKernel(rational))
        }

        /**
         * Constructs a monotone sequence of lower approximations for `sqrt(radicand)`
         * using rational bisection.
         *
         * @param radicand the non-negative integer whose square root to approximate.
         * @param iterations the number of bisection steps (default 40).
         * @return a [ConstructedReal] whose Cauchy sequence converges to the square root.
         * @throws IllegalArgumentException if [radicand] is negative or [iterations] is not positive.
         */
        fun squareRootOf(radicand: Int, iterations: Int = 40): ConstructedReal {
            require(radicand >= 0) { "radicand must be non-negative." }
            require(iterations > 0) { "iterations must be positive." }

            if (radicand == 0) return ZERO
            if (radicand == 1) return ONE

            val target = ConstructedRational.fromKernel(RationalNumber.of(radicand, 1))
            val two = ConstructedRational.fromKernel(RationalNumber.of(2, 1))

            var low = ConstructedRational.ZERO
            var high = ConstructedRational.fromKernel(RationalNumber.of(radicand, 1))
            val approximants = mutableListOf<ConstructedRational>()

            repeat(iterations) {
                val mid = (low + high) / two
                val square = mid * mid
                if (square <= target) {
                    low = mid
                } else {
                    high = mid
                }
                approximants.add(low)
            }

            return of(approximants)
        }

        /**
         * Constructs a [ConstructedReal] from a decimal string by generating a sequence
         * of truncated rational approximations (one per additional decimal digit).
         *
         * Supports optional sign, decimal point, and scientific notation (`e`/`E`).
         *
         * @param decimal the decimal string (e.g. "3.14159", "-2.5e3").
         * @return the corresponding constructed real.
         * @throws IllegalArgumentException if [decimal] is blank.
         */
        fun fromDecimalExpansion(decimal: String): ConstructedReal {
            val normalized = decimal.trim()
            require(normalized.isNotEmpty()) { "decimal expansion cannot be blank." }

            val scientific = normalized.contains('e', ignoreCase = true)
            if (scientific) return fromKernel(RealNumber.parse(normalized))

            val sign = if (normalized.startsWith("-")) "-" else ""
            val unsigned = normalized.removePrefix("-").removePrefix("+")
            val parts = unsigned.split('.')
            val whole = parts.firstOrNull().orEmpty().ifEmpty { "0" }
            val fraction = parts.getOrNull(1).orEmpty()

            if (fraction.isEmpty()) {
                return of(ConstructedRational.fromKernel(decimalStringToRational(normalized)))
            }

            val approximants = mutableListOf<ConstructedRational>()
            for (i in 1..fraction.length) {
                val prefix = "$sign$whole.${fraction.take(i)}"
                approximants.add(ConstructedRational.fromKernel(decimalStringToRational(prefix)))
            }

            if (approximants.lastOrNull()?.toKernel() != decimalStringToRational(normalized)) {
                approximants.add(ConstructedRational.fromKernel(decimalStringToRational(normalized)))
            }

            return of(approximants)
        }

        private fun fromRepresentative(representative: CauchyRepresentative): ConstructedReal {
            val approximant = representative.term(NaturalNumber.of(DEFAULT_APPROXIMATION_INDEX))
            val projection = RealNumber.of(approximant.toKernel())
            return ConstructedReal(representative, projection)
        }

        private fun decimalStringToRational(raw: String): RationalNumber {
            var text = raw.trim()
            require(text.isNotEmpty()) { "Cannot parse empty decimal string." }

            var sign = 1
            if (text.startsWith("-")) {
                sign = -1
                text = text.substring(1)
            } else if (text.startsWith("+")) {
                text = text.substring(1)
            }

            val exponentMarker = text.indexOfAny(charArrayOf('e', 'E'))
            val exponent = if (exponentMarker >= 0) {
                text.substring(exponentMarker + 1).toInt()
            } else {
                0
            }
            val mantissa = if (exponentMarker >= 0) text.substring(0, exponentMarker) else text

            val parts = mantissa.split('.')
            require(parts.size <= 2) { "Invalid decimal notation: $raw" }

            val wholePart = parts.getOrNull(0).orEmpty().ifEmpty { "0" }
            val fractionalPart = parts.getOrNull(1).orEmpty()

            val digits = (wholePart + fractionalPart).trimStart('0').ifEmpty { "0" }
            var numerator = IntegerNumber.parse(digits)
            var denominator = IntegerNumber.ONE

            val scale = fractionalPart.length - exponent
            if (scale > 0) {
                denominator = powerOfTen(scale)
            } else if (scale < 0) {
                numerator = numerator * powerOfTen(-scale)
            }

            if (sign < 0) numerator = -numerator
            return RationalNumber.of(numerator, denominator)
        }

        private fun powerOfTen(exponent: Int): IntegerNumber {
            var result = IntegerNumber.ONE
            repeat(exponent) { result = result * IntegerNumber.of(10) }
            return result
        }
    }
}

/**
 * [Arithmetic] implementation for [ConstructedReal].
 *
 * Delegates to the operator overloads defined on [ConstructedReal].
 */
object ConstructedRealArithmetic : Arithmetic<ConstructedReal> {
    /** The additive identity. */
    override val zero: ConstructedReal = ConstructedReal.ZERO

    /** The multiplicative identity. */
    override val one: ConstructedReal = ConstructedReal.ONE

    override fun add(a: ConstructedReal, b: ConstructedReal): ConstructedReal = a + b

    override fun subtract(a: ConstructedReal, b: ConstructedReal): ConstructedReal = a - b

    override fun multiply(a: ConstructedReal, b: ConstructedReal): ConstructedReal = a * b

    override fun divide(a: ConstructedReal, b: ConstructedReal): ConstructedReal = a / b

    override fun compare(a: ConstructedReal, b: ConstructedReal): Int = a.compareTo(b)
}

/**
 * Total order on [ConstructedReal] delegating to the natural [Comparable] implementation.
 */
object ConstructedRealOrder {
    /** Returns `true` if `a <= b`. */
    fun lessOrEqual(a: ConstructedReal, b: ConstructedReal): Boolean = a <= b

    /** Returns `true` if `a < b`. */
    fun lessThan(a: ConstructedReal, b: ConstructedReal): Boolean = a < b

    /** Returns `true` if `a >= b`. */
    fun greaterOrEqual(a: ConstructedReal, b: ConstructedReal): Boolean = a >= b

    /** Returns `true` if `a > b`. */
    fun greaterThan(a: ConstructedReal, b: ConstructedReal): Boolean = a > b
}

/**
 * Canonical embedding of constructed rationals into constructed reals.
 *
 * Every rational `q` is embedded as the constant Cauchy sequence `(q, q, q, ...)`.
 */
object RationalRealEmbedding {
    /**
     * Embeds a [ConstructedRational] into [ConstructedReal] as a constant sequence.
     *
     * @param rational the constructed rational to embed.
     * @return the corresponding constructed real.
     */
    fun embed(rational: ConstructedRational): ConstructedReal = ConstructedReal.of(rational)

    /**
     * Embeds a kernel [RationalNumber] into [ConstructedReal].
     *
     * @param rational the kernel rational to embed.
     * @return the corresponding constructed real.
     */
    fun embed(rational: RationalNumber): ConstructedReal =
        ConstructedReal.of(ConstructedRational.fromKernel(rational))
}

/**
 * Isomorphism between [ConstructedReal] and the kernel [RealNumber],
 * with round-trip and embedding verification.
 */
object ConstructedRealIsomorphism {
    /**
     * Projects a [ConstructedReal] to its kernel [RealNumber].
     */
    fun toKernel(value: ConstructedReal): RealNumber = value.toKernel()

    /**
     * Lifts a kernel [RealNumber] to a [ConstructedReal].
     */
    fun fromKernel(value: RealNumber): ConstructedReal = ConstructedReal.fromKernel(value)

    /**
     * Verifies the round-trip property for a collection of decimal string samples.
     *
     * @param decimalSamples decimal strings to test (e.g. "1.5", "-3.14").
     * @return `true` if the round-trip holds for every sample.
     */
    fun verifyRoundTrip(decimalSamples: Iterable<String>): Boolean {
        for (sample in decimalSamples) {
            val kernel = RealNumber.parse(sample)
            val roundTrip = toKernel(fromKernel(kernel))
            if (roundTrip != kernel) return false
        }
        return true
    }

    /**
     * Verifies that the rational embedding is preserved through the real isomorphism
     * for all rationals `n/d` with `n` in `-range..range` and `d` in `1..range`.
     *
     * @param range the bound for numerator and denominator.
     * @return `true` if the embedding is preserved.
     */
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

/**
 * Extension to embed a [ConstructedRational] into [ConstructedReal].
 */
fun ConstructedRational.toMathReal(): ConstructedReal = RationalRealEmbedding.embed(this)

/**
 * Extension to embed a kernel [RationalNumber] into [ConstructedReal].
 */
fun RationalNumber.toMathReal(): ConstructedReal = RationalRealEmbedding.embed(this)

/**
 * Extension to embed a kernel [IntegerNumber] into [ConstructedReal].
 */
fun IntegerNumber.toMathReal(): ConstructedReal = this.toMathRational().toMathReal()

/**
 * Extension to embed a kernel [NaturalNumber] into [ConstructedReal].
 */
fun NaturalNumber.toMathReal(): ConstructedReal = this.toMathRational().toMathReal()

/**
 * Extension to convert a kernel [RealNumber] to [ConstructedReal].
 */
fun RealNumber.toMathReal(): ConstructedReal = ConstructedReal.fromKernel(this)

private fun maxNatural(a: NaturalNumber, b: NaturalNumber): NaturalNumber = if (a >= b) a else b

private fun absolute(value: ConstructedRational): ConstructedRational =
    if (value < ConstructedRational.ZERO) -value else value

private fun NaturalNumber.toIndexInt(): Int = toString().toIntOrNull() ?: Int.MAX_VALUE
