package mathsets.examples

import mathsets.construction.integer.ConstructedInteger
import mathsets.construction.integer.toMathInteger
import mathsets.construction.rational.ConstructedRational
import mathsets.construction.rational.toMathRational
import mathsets.kernel.NaturalNumber

data class NumberConstructionSnapshot(
    val natural: NaturalNumber,
    val integer: ConstructedInteger,
    val rational: ConstructedRational
)

/**
 * Demo de construção N -> Z -> Q.
 */
object NumberConstructionDemo {
    fun fromNatural(value: Int): NumberConstructionSnapshot {
        val natural = NaturalNumber.of(value)
        val integer = natural.toMathInteger()
        val rational = integer.toMathRational()
        return NumberConstructionSnapshot(natural, integer, rational)
    }

    fun walkthrough(value: Int): List<String> {
        val snapshot = fromNatural(value)
        return listOf(
            "Natural: ${snapshot.natural}",
            "Embed em Z: ${snapshot.integer}",
            "Embed em Q: ${snapshot.rational}"
        )
    }
}

