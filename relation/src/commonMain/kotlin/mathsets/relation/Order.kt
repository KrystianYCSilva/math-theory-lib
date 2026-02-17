package mathsets.relation

import mathsets.set.ExtensionalSet
import mathsets.set.MathSet
import mathsets.kernel.Cardinality

/**
 * Base class for sets equipped with an ordering relation.
 *
 * An [OrderedSet] pairs a [universe] with a binary [relation] that defines a notion
 * of "less than or equal to" between elements. This class provides operations common
 * to all ordered structures: finding minima, maxima, bounds, and immediate neighbors.
 *
 * Subclasses impose additional axioms on the relation (e.g., antisymmetry for partial
 * orders, connexity for total orders).
 *
 * @param A the element type
 * @property universe the underlying set of elements
 * @property relation the ordering relation R on the universe
 */
open class OrderedSet<A>(
    protected val universe: MathSet<A>,
    protected val relation: Relation<A, A>
) {
    /**
     * Tests whether [a] is less than or equal to [b] under the ordering: aRb.
     */
    protected fun leq(a: A, b: A): Boolean = relation.contains(a, b)

    /**
     * Tests strict ordering: a < b iff a <= b and not b <= a.
     */
    protected fun lt(a: A, b: A): Boolean = leq(a, b) && !leq(b, a)

    /**
     * Returns the **minimum** (least element) of the universe, if one exists.
     *
     * An element m is a minimum if m <= x for every x in the universe.
     *
     * @return the minimum element, or `null` if no minimum exists
     */
    fun minimum(): A? = universe.elements().firstOrNull { candidate ->
        universe.elements().all { other -> leq(candidate, other) }
    }

    /**
     * Returns the **maximum** (greatest element) of the universe, if one exists.
     *
     * An element m is a maximum if x <= m for every x in the universe.
     *
     * @return the maximum element, or `null` if no maximum exists
     */
    fun maximum(): A? = universe.elements().firstOrNull { candidate ->
        universe.elements().all { other -> leq(other, candidate) }
    }

    /**
     * Returns the set of all **minimal elements** of the universe.
     *
     * An element m is minimal if there is no x in the universe with x < m.
     * Unlike the minimum, minimal elements need not be comparable to all others.
     *
     * @return a [MathSet] containing all minimal elements
     */
    fun minimals(): MathSet<A> = ExtensionalSet(
        universe.elements().filter { candidate ->
            universe.elements().none { other -> lt(other, candidate) }
        }.toSet()
    )

    /**
     * Returns the set of all **maximal elements** of the universe.
     *
     * An element m is maximal if there is no x in the universe with m < x.
     *
     * @return a [MathSet] containing all maximal elements
     */
    fun maximals(): MathSet<A> = ExtensionalSet(
        universe.elements().filter { candidate ->
            universe.elements().none { other -> lt(candidate, other) }
        }.toSet()
    )

    /**
     * Computes the **supremum** (least upper bound) of a [subset], if one exists.
     *
     * The supremum is the smallest element in the universe that is an upper bound
     * for every element of the subset: sup(S) = min{ x | ∀ s ∈ S, s <= x }.
     *
     * @param subset the subset to find the supremum of
     * @return the least upper bound, or `null` if none exists
     */
    fun supremum(subset: MathSet<A>): A? {
        val upperBounds = universe.elements().filter { candidate ->
            subset.elements().all { member -> leq(member, candidate) }
        }.toList()
        return upperBounds.firstOrNull { candidate ->
            upperBounds.all { other -> leq(candidate, other) }
        }
    }

    /**
     * Computes the **infimum** (greatest lower bound) of a [subset], if one exists.
     *
     * The infimum is the largest element in the universe that is a lower bound
     * for every element of the subset: inf(S) = max{ x | ∀ s ∈ S, x <= s }.
     *
     * @param subset the subset to find the infimum of
     * @return the greatest lower bound, or `null` if none exists
     */
    fun infimum(subset: MathSet<A>): A? {
        val lowerBounds = universe.elements().filter { candidate ->
            subset.elements().all { member -> leq(candidate, member) }
        }.toList()
        return lowerBounds.firstOrNull { candidate ->
            lowerBounds.all { other -> leq(other, candidate) }
        }
    }

    /**
     * Returns the **immediate successor** of [element] in the ordering, if one exists.
     *
     * The successor is the smallest element strictly greater than [element]:
     * succ(a) = min{ x | a < x }.
     *
     * @param element the element to find the successor of
     * @return the immediate successor, or `null` if none exists
     */
    fun successor(element: A): A? {
        val greater = universe.elements().filter { candidate -> lt(element, candidate) }.toList()
        return greater.firstOrNull { candidate -> greater.all { other -> leq(candidate, other) } }
    }

    /**
     * Returns the **immediate predecessor** of [element] in the ordering, if one exists.
     *
     * The predecessor is the largest element strictly less than [element]:
     * pred(a) = max{ x | x < a }.
     *
     * @param element the element to find the predecessor of
     * @return the immediate predecessor, or `null` if none exists
     */
    fun predecessor(element: A): A? {
        val lower = universe.elements().filter { candidate -> lt(candidate, element) }.toList()
        return lower.firstOrNull { candidate -> lower.all { other -> leq(other, candidate) } }
    }
}

/**
 * Represents a **partial order** (poset): a reflexive, antisymmetric, and transitive relation.
 *
 * A partial order allows some pairs of elements to be incomparable (neither a <= b nor b <= a).
 * All three axioms are validated at construction time.
 *
 * @param A the element type
 * @throws IllegalArgumentException if the relation is not reflexive, antisymmetric, or transitive
 */
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

/**
 * Represents a **total order** (linear order): a partial order that is also connex.
 *
 * In a total order, every pair of elements is comparable: for all a, b either a <= b or b <= a.
 * Connexity is validated at construction time (in addition to the partial order axioms).
 *
 * @param A the element type
 * @throws IllegalArgumentException if the relation is not connex (or not a valid partial order)
 */
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

/**
 * Represents a **well-order**: a total order in which every non-empty subset has a minimum.
 *
 * The well-ordering property is fundamental in set theory and is equivalent to the
 * Axiom of Choice. Verification currently requires a finite universe, where every
 * non-empty subset of the power set is checked for the existence of a minimum.
 *
 * @param A the element type
 * @throws IllegalArgumentException if any non-empty subset lacks a minimum, or if the
 *   universe is not finite, or if the relation is not a valid total order
 */
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
