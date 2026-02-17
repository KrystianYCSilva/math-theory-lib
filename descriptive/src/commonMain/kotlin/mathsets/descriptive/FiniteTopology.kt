package mathsets.descriptive

import mathsets.kernel.Cardinality
import mathsets.set.ExtensionalSet
import mathsets.set.MathSet

/**
 * Topologia finita: valida axiomas e fornece interior/fecho/fronteira.
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

    fun isOpen(candidate: MathSet<T>): Boolean = candidate.materialize() in opensFinite

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

    fun closure(target: MathSet<T>): MathSet<T> {
        val targetFinite = target.materialize()
        val complement = (universeFinite minus targetFinite).materialize()
        val interiorOfComplement = interior(complement).materialize()
        return (universeFinite minus interiorOfComplement).materialize()
    }

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

