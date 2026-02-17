package mathsets.cardinal

import mathsets.function.Bijection
import mathsets.kernel.Generators
import mathsets.kernel.IntegerNumber
import mathsets.kernel.NaturalNumber
import mathsets.kernel.RationalNumber
import mathsets.set.ExtensionalSet

/**
 * Bijeções construtivas para enumerabilidade.
 */
object Countability {
    private val two = NaturalNumber.of(2)

    private val rationalsIterator = Generators.rationals().iterator()
    private val naturalsToRationals = mutableListOf<RationalNumber>()
    private val rationalsToNaturals = linkedMapOf<RationalNumber, NaturalNumber>()

    fun naturalToInteger(n: NaturalNumber): IntegerNumber {
        if (n.isZero()) return IntegerNumber.ZERO
        return if (n.isOdd()) {
            IntegerNumber.parse(((n + NaturalNumber.ONE) / two).toString())
        } else {
            -IntegerNumber.parse((n / two).toString())
        }
    }

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

    fun naturalToRational(n: NaturalNumber): RationalNumber {
        val index = n.toIndexedInt()
        ensureRationalIndex(index)
        return naturalsToRationals[index]
    }

    fun rationalToNatural(rational: RationalNumber): NaturalNumber {
        ensureRationalKnown(rational)
        return rationalsToNaturals.getValue(rational)
    }

    fun verifyRationalRoundTrip(limit: Int): Boolean {
        require(limit >= 0) { "limit must be non-negative." }
        for (n in 0..limit) {
            val natural = NaturalNumber.of(n)
            if (rationalToNatural(naturalToRational(natural)) != natural) return false
        }
        return true
    }

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
