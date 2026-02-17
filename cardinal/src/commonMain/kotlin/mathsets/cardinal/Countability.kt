package mathsets.cardinal

import mathsets.function.Bijection
import mathsets.kernel.Generators
import mathsets.kernel.IntegerNumber
import mathsets.kernel.NaturalNumber
import mathsets.kernel.RationalNumber
import mathsets.set.ExtensionalSet

/**
 * Provides constructive bijections demonstrating the countability of the integers and rationals.
 *
 * Contains explicit mappings between natural numbers, integers, and rationals, along with
 * round-trip verification methods and finite prefix bijection construction.
 *
 * The natural-to-integer mapping uses the standard zigzag enumeration:
 * 0 -> 0, 1 -> 1, 2 -> -1, 3 -> 2, 4 -> -2, ...
 *
 * The natural-to-rational mapping uses a lazy Stern-Brocot/Calkin-Wilf style enumeration
 * that produces each rational exactly once.
 */
object Countability {
    private val two = NaturalNumber.of(2)

    private val rationalsIterator = Generators.rationals().iterator()
    private val naturalsToRationals = mutableListOf<RationalNumber>()
    private val rationalsToNaturals = linkedMapOf<RationalNumber, NaturalNumber>()

    /**
     * Maps a natural number to an integer using the zigzag enumeration.
     *
     * The mapping is: 0 -> 0, 1 -> 1, 2 -> -1, 3 -> 2, 4 -> -2, ...
     *
     * @param n The natural number to map.
     * @return The corresponding integer.
     */
    fun naturalToInteger(n: NaturalNumber): IntegerNumber {
        if (n.isZero()) return IntegerNumber.ZERO
        return if (n.isOdd()) {
            IntegerNumber.parse(((n + NaturalNumber.ONE) / two).toString())
        } else {
            -IntegerNumber.parse((n / two).toString())
        }
    }

    /**
     * Maps an integer to a natural number (inverse of [naturalToInteger]).
     *
     * @param z The integer to map.
     * @return The corresponding natural number.
     */
    fun integerToNatural(z: IntegerNumber): NaturalNumber = when {
        z == IntegerNumber.ZERO -> NaturalNumber.ZERO
        z > IntegerNumber.ZERO -> {
            val doubled = z + z
            NaturalNumber.parse(doubled.toString()) - NaturalNumber.ONE
        }

        else -> {
            val doubledAbs = (-z) + (-z)
            NaturalNumber.parse(doubledAbs.toString())
        }
    }

    /**
     * Verifies the round-trip property of the natural-integer bijection for all
     * naturals in `0..limit` and all integers in `-limit..limit`.
     *
     * @param limit The upper bound for verification (must be non-negative).
     * @return `true` if every round-trip succeeds, `false` otherwise.
     * @throws IllegalArgumentException if [limit] is negative.
     */
    fun verifyIntegerRoundTrip(limit: Int): Boolean {
        require(limit >= 0) { "limit must be non-negative." }
        for (n in 0..limit) {
            val natural = NaturalNumber.of(n)
            if (integerToNatural(naturalToInteger(natural)) != natural) return false
        }
        for (z in -limit..limit) {
            val integer = IntegerNumber.of(z)
            if (naturalToInteger(integerToNatural(integer)) != integer) return false
        }
        return true
    }

    /**
     * Maps a natural number to a rational number using a lazy enumeration.
     *
     * The enumeration produces each rational exactly once. Results are memoized, so
     * calling this with increasing indices is efficient.
     *
     * @param n The natural number index.
     * @return The rational number at position [n] in the enumeration.
     * @throws IllegalArgumentException if [n] is too large for indexed access.
     */
    fun naturalToRational(n: NaturalNumber): RationalNumber {
        val index = n.toIndexedInt()
        ensureRationalIndex(index)
        return naturalsToRationals[index]
    }

    /**
     * Maps a rational number back to its natural number index (inverse of [naturalToRational]).
     *
     * If the rational has not been encountered yet, the enumeration advances until it is found.
     *
     * @param rational The rational number to look up.
     * @return The natural number index of the given rational.
     */
    fun rationalToNatural(rational: RationalNumber): NaturalNumber {
        ensureRationalKnown(rational)
        return rationalsToNaturals.getValue(rational)
    }

    /**
     * Verifies the round-trip property of the natural-rational bijection for
     * all naturals in `0..limit`.
     *
     * @param limit The upper bound for verification (must be non-negative).
     * @return `true` if every round-trip succeeds, `false` otherwise.
     * @throws IllegalArgumentException if [limit] is negative.
     */
    fun verifyRationalRoundTrip(limit: Int): Boolean {
        require(limit >= 0) { "limit must be non-negative." }
        for (n in 0..limit) {
            val natural = NaturalNumber.of(n)
            if (rationalToNatural(naturalToRational(natural)) != natural) return false
        }
        return true
    }

    /**
     * Constructs a finite prefix bijection from naturals `{0, ..., size-1}` to integers.
     *
     * @param size The number of elements in the bijection (must be non-negative).
     * @return A [Bijection] from natural numbers to integers.
     * @throws IllegalArgumentException if [size] is negative.
     */
    fun integerBijectionPrefix(size: Int): Bijection<NaturalNumber, IntegerNumber> {
        require(size >= 0) { "size must be non-negative." }
        val domainValues = (0 until size).map { NaturalNumber.of(it) }.toSet()
        val mapping = domainValues.associateWith { naturalToInteger(it) }
        val domain = ExtensionalSet(domainValues)
        val codomain = ExtensionalSet(mapping.values.toSet())
        return Bijection(domain, codomain) { input ->
            mapping[input] ?: error("Missing mapping for $input")
        }
    }

    /**
     * Constructs a finite prefix bijection from naturals `{0, ..., size-1}` to rationals.
     *
     * @param size The number of elements in the bijection (must be non-negative).
     * @return A [Bijection] from natural numbers to rational numbers.
     * @throws IllegalArgumentException if [size] is negative.
     */
    fun rationalBijectionPrefix(size: Int): Bijection<NaturalNumber, RationalNumber> {
        require(size >= 0) { "size must be non-negative." }
        val domainValues = (0 until size).map { NaturalNumber.of(it) }.toSet()
        val mapping = domainValues.associateWith { naturalToRational(it) }
        val domain = ExtensionalSet(domainValues)
        val codomain = ExtensionalSet(mapping.values.toSet())
        return Bijection(domain, codomain) { input ->
            mapping[input] ?: error("Missing mapping for $input")
        }
    }

    private fun ensureRationalIndex(index: Int) {
        while (naturalsToRationals.size <= index) {
            appendNextDistinctRational()
        }
    }

    private fun ensureRationalKnown(target: RationalNumber) {
        while (target !in rationalsToNaturals) {
            appendNextDistinctRational()
        }
    }

    private fun appendNextDistinctRational() {
        while (true) {
            val next = rationalsIterator.next()
            if (next !in rationalsToNaturals) {
                val index = NaturalNumber.of(naturalsToRationals.size)
                naturalsToRationals.add(next)
                rationalsToNaturals[next] = index
                return
            }
        }
    }

    private fun NaturalNumber.toIndexedInt(): Int =
        toString().toIntOrNull()
            ?: throw IllegalArgumentException("Natural index too large for this mapping.")
}
