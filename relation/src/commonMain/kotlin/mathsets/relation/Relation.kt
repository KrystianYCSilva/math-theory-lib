package mathsets.relation

import mathsets.set.ExtensionalSet
import mathsets.set.MathSet
import mathsets.kernel.Cardinality

/**
 * Represents a binary relation R ⊆ A × B between two sets.
 *
 * A relation is defined by three components: a [domain] A, a [codomain] B, and a
 * [graph] G ⊆ A × B that specifies which pairs are related. We write aRb (or (a, b) ∈ G)
 * when elements a and b are related.
 *
 * @param A the type of elements in the domain
 * @param B the type of elements in the codomain
 * @property domain the source set A
 * @property codomain the target set B
 * @property graph the set of ordered pairs that define the relation
 */
class Relation<A, B>(
    val domain: MathSet<A>,
    val codomain: MathSet<B>,
    val graph: MathSet<OrderedPair<A, B>>
) {
    /**
     * Constructs a relation from its [graph] alone, inferring the domain as the set of
     * all first components and the codomain as the set of all second components.
     *
     * @param graph the set of ordered pairs defining the relation
     */
    constructor(graph: MathSet<OrderedPair<A, B>>) : this(
        domain = ExtensionalSet(graph.elements().map { it.first }.toSet()),
        codomain = ExtensionalSet(graph.elements().map { it.second }.toSet()),
        graph = graph
    )

    /**
     * Tests whether elements [a] and [b] are related under this relation.
     *
     * @param a an element from the domain
     * @param b an element from the codomain
     * @return `true` if (a, b) ∈ graph, `false` otherwise
     */
    fun contains(a: A, b: B): Boolean = OrderedPair(a, b) in graph

    /**
     * Computes the **inverse relation** R⁻¹ ⊆ B × A.
     *
     * The inverse is obtained by swapping every pair: R⁻¹ = { (b, a) | (a, b) ∈ R }.
     *
     * @return the inverse relation with domain and codomain swapped
     */
    fun inverse(): Relation<B, A> {
        val inverseGraph = ExtensionalSet(
            graph.elements().map { OrderedPair(it.second, it.first) }.toSet()
        )
        return Relation(codomain, domain, inverseGraph)
    }

    /**
     * Computes the **composition** of this relation with [other]: (R ; S) ⊆ A × C.
     *
     * The composition is defined as:
     * ```
     * R ; S = { (a, c) | ∃ b : (a, b) ∈ R ∧ (b, c) ∈ S }
     * ```
     *
     * At least one of the middle sets (this relation's codomain or [other]'s domain)
     * must be finite for the current implementation.
     *
     * @param C the element type of the resulting codomain
     * @param other the relation to compose with (applied after this relation)
     * @return the composed relation R ; S
     * @throws IllegalArgumentException if both middle domains are infinite
     */
    fun <C> compose(other: Relation<B, C>): Relation<A, C> {
        require(codomain.cardinality is Cardinality.Finite || other.domain.cardinality is Cardinality.Finite) {
            "Composition currently requires at least one finite middle domain."
        }
        val pairs = mutableSetOf<OrderedPair<A, C>>()
        val left = graph.elements().toList()
        val right = other.graph.elements().toList()

        for (p in left) {
            for (q in right) {
                if (p.second == q.first) {
                    pairs.add(OrderedPair(p.first, q.second))
                }
            }
        }
        return Relation(domain, other.codomain, ExtensionalSet(pairs))
    }
}
