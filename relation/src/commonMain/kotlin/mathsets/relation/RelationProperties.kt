package mathsets.relation

import mathsets.set.ExtensionalSet
import mathsets.set.MathSet

/**
 * Analyzes standard properties of a **homogeneous** binary relation R ⊆ A × A.
 *
 * Given a relation and its universe, this class provides predicate methods that test
 * whether the relation satisfies classical properties such as reflexivity, symmetry,
 * transitivity, antisymmetry, and others. These properties form the building blocks
 * for defining equivalence relations, partial orders, and total orders.
 *
 * @param A the element type of both the domain and codomain
 * @property relation the homogeneous relation R ⊆ A × A to analyze
 * @property universe the underlying set A over which the relation is defined
 */
class RelationProperties<A>(
    private val relation: Relation<A, A>,
    private val universe: MathSet<A>
) {
    private fun related(a: A, b: A): Boolean = relation.contains(a, b)

    /**
     * Tests whether the relation is **reflexive**: ∀ a ∈ A, aRa.
     *
     * @return `true` if every element is related to itself
     */
    fun isReflexive(): Boolean = universe.elements().all { a -> related(a, a) }

    /**
     * Tests whether the relation is **symmetric**: ∀ a, b ∈ A, aRb → bRa.
     *
     * @return `true` if relatedness is always mutual
     */
    fun isSymmetric(): Boolean = relation.graph.elements().all { (a, b) -> related(b, a) }

    /**
     * Tests whether the relation is **transitive**: ∀ a, b, c ∈ A, (aRb ∧ bRc) → aRc.
     *
     * @return `true` if the relation is closed under composition with itself
     */
    fun isTransitive(): Boolean {
        val edges = relation.graph.elements().toList()
        for (ab in edges) {
            for (bc in edges) {
                if (ab.second == bc.first && !related(ab.first, bc.second)) return false
            }
        }
        return true
    }

    /**
     * Tests whether the relation is **antisymmetric**: ∀ a, b ∈ A, (aRb ∧ bRa) → a = b.
     *
     * @return `true` if mutual relatedness implies equality
     */
    fun isAntisymmetric(): Boolean =
        relation.graph.elements().all { (a, b) -> a == b || !related(b, a) }

    /**
     * Tests whether the relation is **irreflexive**: ∀ a ∈ A, ¬(aRa).
     *
     * @return `true` if no element is related to itself
     */
    fun isIrreflexive(): Boolean = universe.elements().none { a -> related(a, a) }

    /**
     * Tests whether the relation is **trichotomous**: ∀ a, b ∈ A, exactly one of
     * aRb, a = b, or bRa holds.
     *
     * @return `true` if for every pair of elements, at least one of the three conditions holds
     */
    fun isTrichotomous(): Boolean =
        universe.elements().all { a ->
            universe.elements().all { b ->
                related(a, b) || a == b || related(b, a)
            }
        }

    /**
     * Tests whether the relation is **connex** (also called *total* or *complete*):
     * ∀ a, b ∈ A, a ≠ b → (aRb ∨ bRa).
     *
     * A connex relation ensures every pair of distinct elements is comparable.
     *
     * @return `true` if all distinct pairs are related in at least one direction
     */
    fun isConnex(): Boolean =
        universe.elements().all { a ->
            universe.elements().all { b ->
                a == b || related(a, b) || related(b, a)
            }
        }
}

/**
 * Factory function that constructs a homogeneous [Relation] over a [universe] from a
 * binary [predicate].
 *
 * The graph is built by testing every pair (a, b) ∈ A × A against the predicate and
 * including only those pairs for which the predicate returns `true`.
 *
 * @param A the element type
 * @param universe the set over which the relation is defined
 * @param predicate a function `(A, A) → Boolean` that determines relatedness
 * @return a [Relation] whose graph contains all pairs satisfying the predicate
 */
fun <A> relationOf(
    universe: MathSet<A>,
    predicate: (A, A) -> Boolean
): Relation<A, A> {
    val graph = universe.elements()
        .flatMap { a -> universe.elements().map { b -> OrderedPair(a, b) } }
        .filter { (a, b) -> predicate(a, b) }
        .toSet()
    return Relation(universe, universe, ExtensionalSet(graph))
}
