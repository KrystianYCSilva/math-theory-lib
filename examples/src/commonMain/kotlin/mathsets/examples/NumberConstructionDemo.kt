package mathsets.examples

import mathsets.construction.integer.ConstructedInteger
import mathsets.construction.integer.toMathInteger
import mathsets.construction.rational.ConstructedRational
import mathsets.construction.rational.toMathRational
import mathsets.kernel.NaturalNumber

/**
 * A snapshot of the number construction chain N -> Z -> Q for a single value.
 *
 * @property natural The original natural number.
 * @property integer The integer obtained by embedding the natural into Z.
 * @property rational The rational obtained by embedding the integer into Q.
 */
data class NumberConstructionSnapshot(
    val natural: NaturalNumber,
    val integer: ConstructedInteger,
    val rational: ConstructedRational
)

/**
 * Demonstrates the axiomatic construction of number systems: N -> Z -> Q.
 *
 * Shows how a natural number is embedded into the integers (via equivalence classes
 * of pairs) and then into the rationals (via equivalence classes of integer pairs).
 */
object NumberConstructionDemo {
    /**
     * Constructs the full N -> Z -> Q chain for the given integer value.
     *
     * @param value The non-negative integer to start from.
     * @return A [NumberConstructionSnapshot] with all three representations.
     */
    fun fromNatural(value: Int): NumberConstructionSnapshot {
        val natural = NaturalNumber.of(value)
        val integer = natural.toMathInteger()
        val rational = integer.toMathRational()
        return NumberConstructionSnapshot(natural, integer, rational)
    }

    /**
     * Produces a human-readable walkthrough of the N -> Z -> Q embedding chain.
     *
     * @param value The non-negative integer to demonstrate.
     * @return A list of strings describing each step of the construction.
     */
    fun walkthrough(value: Int): List<String> {
        val snapshot = fromNatural(value)
        return listOf(
            "Natural: ${snapshot.natural}",
            "Embed em Z: ${snapshot.integer}",
            "Embed em Q: ${snapshot.rational}"
        )
    }
}
