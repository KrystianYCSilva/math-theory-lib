package mathsets.descriptive

import mathsets.kernel.Cardinality
import mathsets.set.ExtensionalSet
import mathsets.set.MathSet

/**
 * Represents a finite topological space and provides topological operations.
 *
 * A topology on a set X is a collection of subsets (called open sets) that satisfies:
 * 1. The empty set and X itself are open.
 * 2. Any union of open sets is open.
 * 3. Any finite intersection of open sets is open.
 *
 * This class validates these axioms at construction time and provides operations for
 * computing the interior, closure, and boundary of subsets.
 *
 * @param T The element type of the universe.
 * @property universe The finite ground set.
 * @property openSets The family of open sets defining the topology.
 * @throws IllegalArgumentException if the universe or open sets are not finite, or if
 *         the provided family does not satisfy the topology axioms.
 */
class FiniteTopology<T>(
    val universe: MathSet<T>,
    val openSets: MathSet<MathSet<T>>
) {
    private val universeFinite: ExtensionalSet<T> = universe.materialize()
    private val opensFinite: Set<ExtensionalSet<T>> = openSets.elements().map { it.materialize() }.toSet()

    init {
        require(universe.cardinality is Cardinality.Finite) {
            "FiniteTopology requires finite universe."
        }
        require(openSets.cardinality is Cardinality.Finite) {
            "FiniteTopology currently requires finite family of open sets."
        }
        require(validateAxioms()) { "Provided family is not a topology on the given universe." }
    }

    /**
     * Checks whether the given set is open in this topology.
     *
     * @param candidate The set to test.
     * @return `true` if [candidate] is an open set in this topology.
     */
    fun isOpen(candidate: MathSet<T>): Boolean = candidate.materialize() in opensFinite

    /**
     * Computes the interior of the given set (the largest open set contained in it).
     *
     * The interior is the union of all open sets that are subsets of [target].
     *
     * @param target The set whose interior is computed.
     * @return The interior of [target].
     */
    fun interior(target: MathSet<T>): MathSet<T> {
        val targetFinite = target.materialize()
        var result: MathSet<T> = MathSet.empty()
        for (open in opensFinite) {
            if (open isSubsetOf targetFinite) {
                result = result union open
            }
        }
        return result.materialize()
    }

    /**
     * Computes the closure of the given set (the smallest closed set containing it).
     *
     * The closure is the complement of the interior of the complement of [target].
     *
     * @param target The set whose closure is computed.
     * @return The closure of [target].
     */
    fun closure(target: MathSet<T>): MathSet<T> {
        val targetFinite = target.materialize()
        val complement = (universeFinite minus targetFinite).materialize()
        val interiorOfComplement = interior(complement).materialize()
        return (universeFinite minus interiorOfComplement).materialize()
    }

    /**
     * Computes the boundary of the given set.
     *
     * The boundary is the intersection of the closure of [target] and the closure of
     * its complement: boundary(A) = closure(A) intersect closure(X \ A).
     *
     * @param target The set whose boundary is computed.
     * @return The boundary of [target].
     */
    fun boundary(target: MathSet<T>): MathSet<T> {
        val targetFinite = target.materialize()
        val complement = (universeFinite minus targetFinite).materialize()
        return (closure(targetFinite) intersect closure(complement)).materialize()
    }

    private fun validateAxioms(): Boolean {
        val empty = MathSet.empty<T>().materialize()
        if (empty !in opensFinite) return false
        if (universeFinite !in opensFinite) return false
        if (opensFinite.any { !(it isSubsetOf universeFinite) }) return false

        val openList = opensFinite.toList()
        for (i in openList.indices) {
            for (j in openList.indices) {
                val union = (openList[i] union openList[j]).materialize()
                val intersection = (openList[i] intersect openList[j]).materialize()
                if (union !in opensFinite) return false
                if (intersection !in opensFinite) return false
            }
        }
        return true
    }
}
