package mathsets.descriptive

import mathsets.set.MathSet

/**
 * Represents the initial levels of the Borel hierarchy in descriptive set theory.
 *
 * The Borel hierarchy classifies sets by the complexity of the operations needed
 * to construct them from open sets:
 * - [SIGMA_0_1]: Open sets (Sigma^0_1).
 * - [PI_0_1]: Closed sets (Pi^0_1), complements of open sets.
 * - [SIGMA_0_2]: Countable unions of closed sets (F-sigma).
 * - [PI_0_2]: Countable intersections of open sets (G-delta).
 * - [HIGHER]: Sets at higher levels of the hierarchy.
 *
 * @property label A human-readable notation for this Borel level.
 */
enum class BorelLevel(val label: String) {
    SIGMA_0_1("Sigma^0_1"),
    PI_0_1("Pi^0_1"),
    SIGMA_0_2("Sigma^0_2"),
    PI_0_2("Pi^0_2"),
    HIGHER("Higher");
}

/**
 * A sealed hierarchy representing Borel sets built from open sets via complements,
 * countable unions, and countable intersections.
 *
 * @param T The element type of the underlying sets.
 */
sealed interface BorelSet<T> {
    /**
     * An open set in the topology.
     *
     * @property set The underlying mathematical set.
     */
    data class Open<T>(val set: MathSet<T>) : BorelSet<T>

    /**
     * A closed set in the topology (complement of an open set).
     *
     * @property set The underlying mathematical set.
     */
    data class Closed<T>(val set: MathSet<T>) : BorelSet<T>

    /**
     * A countable union of Borel sets.
     *
     * @property terms The list of Borel sets being unioned.
     */
    data class CountableUnion<T>(val terms: List<BorelSet<T>>) : BorelSet<T>

    /**
     * A countable intersection of Borel sets.
     *
     * @property terms The list of Borel sets being intersected.
     */
    data class CountableIntersection<T>(val terms: List<BorelSet<T>>) : BorelSet<T>

    /**
     * The complement of a Borel set.
     *
     * @property inner The Borel set being complemented.
     */
    data class Complement<T>(val inner: BorelSet<T>) : BorelSet<T>
}

/**
 * Classifies Borel sets into the initial levels of the Borel hierarchy.
 *
 * This classifier handles the first two levels (Sigma^0_1, Pi^0_1, Sigma^0_2, Pi^0_2)
 * and assigns [BorelLevel.HIGHER] to anything more complex.
 */
object BorelHierarchy {
    /**
     * Determines the Borel level of the given set.
     *
     * @param T The element type.
     * @param set The Borel set to classify.
     * @return The [BorelLevel] of the set.
     */
    fun <T> classify(set: BorelSet<T>): BorelLevel = when (set) {
        is BorelSet.Open -> BorelLevel.SIGMA_0_1
        is BorelSet.Closed -> BorelLevel.PI_0_1
        is BorelSet.Complement -> classifyComplement(set.inner)
        is BorelSet.CountableUnion -> classifyUnion(set.terms)
        is BorelSet.CountableIntersection -> classifyIntersection(set.terms)
    }

    private fun <T> classifyComplement(inner: BorelSet<T>): BorelLevel = when (classify(inner)) {
        BorelLevel.SIGMA_0_1 -> BorelLevel.PI_0_1
        BorelLevel.PI_0_1 -> BorelLevel.SIGMA_0_1
        BorelLevel.SIGMA_0_2 -> BorelLevel.PI_0_2
        BorelLevel.PI_0_2 -> BorelLevel.SIGMA_0_2
        BorelLevel.HIGHER -> BorelLevel.HIGHER
    }

    private fun <T> classifyUnion(terms: List<BorelSet<T>>): BorelLevel {
        if (terms.isEmpty()) return BorelLevel.SIGMA_0_1
        val levels = terms.map { classify(it) }.toSet()
        return when {
            levels.all { it == BorelLevel.SIGMA_0_1 } -> BorelLevel.SIGMA_0_1
            levels.all { it == BorelLevel.SIGMA_0_1 || it == BorelLevel.PI_0_1 } -> BorelLevel.SIGMA_0_2
            else -> BorelLevel.HIGHER
        }
    }

    private fun <T> classifyIntersection(terms: List<BorelSet<T>>): BorelLevel {
        if (terms.isEmpty()) return BorelLevel.PI_0_1
        val levels = terms.map { classify(it) }.toSet()
        return when {
            levels.all { it == BorelLevel.PI_0_1 } -> BorelLevel.PI_0_1
            levels.all { it == BorelLevel.SIGMA_0_1 || it == BorelLevel.PI_0_1 } -> BorelLevel.PI_0_2
            else -> BorelLevel.HIGHER
        }
    }
}
