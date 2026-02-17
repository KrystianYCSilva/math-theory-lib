package mathsets.relation

import mathsets.set.ExtensionalSet
import mathsets.set.MathSet
import mathsets.kernel.Cardinality

open class OrderedSet<A>(
    protected val universe: MathSet<A>,
    protected val relation: Relation<A, A>
) {
    protected fun leq(a: A, b: A): Boolean = relation.contains(a, b)
    protected fun lt(a: A, b: A): Boolean = leq(a, b) && !leq(b, a)

    fun minimum(): A? = universe.elements().firstOrNull { candidate ->
        universe.elements().all { other -> leq(candidate, other) }
    }

    fun maximum(): A? = universe.elements().firstOrNull { candidate ->
        universe.elements().all { other -> leq(other, candidate) }
    }

    fun minimals(): MathSet<A> = ExtensionalSet(
        universe.elements().filter { candidate ->
            universe.elements().none { other -> lt(other, candidate) }
        }.toSet()
    )

    fun maximals(): MathSet<A> = ExtensionalSet(
        universe.elements().filter { candidate ->
            universe.elements().none { other -> lt(candidate, other) }
        }.toSet()
    )

    fun supremum(subset: MathSet<A>): A? {
        val upperBounds = universe.elements().filter { candidate ->
            subset.elements().all { member -> leq(member, candidate) }
        }.toList()
        return upperBounds.firstOrNull { candidate ->
            upperBounds.all { other -> leq(candidate, other) }
        }
    }

    fun infimum(subset: MathSet<A>): A? {
        val lowerBounds = universe.elements().filter { candidate ->
            subset.elements().all { member -> leq(candidate, member) }
        }.toList()
        return lowerBounds.firstOrNull { candidate ->
            lowerBounds.all { other -> leq(other, candidate) }
        }
    }

    fun successor(element: A): A? {
        val greater = universe.elements().filter { candidate -> lt(element, candidate) }.toList()
        return greater.firstOrNull { candidate -> greater.all { other -> leq(candidate, other) } }
    }

    fun predecessor(element: A): A? {
        val lower = universe.elements().filter { candidate -> lt(candidate, element) }.toList()
        return lower.firstOrNull { candidate -> lower.all { other -> leq(other, candidate) } }
    }
}

open class PartialOrder<A>(
    universe: MathSet<A>,
    relation: Relation<A, A>
) : OrderedSet<A>(universe, relation) {
    init {
        val properties = RelationProperties(relation, universe)
        require(properties.isReflexive()) { "Partial order must be reflexive." }
        require(properties.isAntisymmetric()) { "Partial order must be antisymmetric." }
        require(properties.isTransitive()) { "Partial order must be transitive." }
    }
}

open class TotalOrder<A>(
    universe: MathSet<A>,
    relation: Relation<A, A>
) : PartialOrder<A>(universe, relation) {
    init {
        require(RelationProperties(relation, universe).isConnex()) {
            "Total order must be connex."
        }
    }
}

class WellOrder<A>(
    universe: MathSet<A>,
    relation: Relation<A, A>
) : TotalOrder<A>(universe, relation) {
    init {
        require(universe.cardinality is Cardinality.Finite) {
            "Well-order verification currently requires a finite universe."
        }
        val subsets = universe.powerSet().elements().filter { it.elements().any() }.toList()
        val hasMinimumInEachSubset = subsets.all { subset ->
            subset.elements().any { candidate ->
                subset.elements().all { other -> leq(candidate, other) }
            }
        }
        require(hasMinimumInEachSubset) { "Every non-empty subset must have a minimum." }
    }
}
