package mathsets.relation

import mathsets.set.ExtensionalSet
import mathsets.set.MathSet
import mathsets.kernel.Cardinality

class Relation<A, B>(
    val domain: MathSet<A>,
    val codomain: MathSet<B>,
    val graph: MathSet<OrderedPair<A, B>>
) {
    constructor(graph: MathSet<OrderedPair<A, B>>) : this(
        domain = ExtensionalSet(graph.elements().map { it.first }.toSet()),
        codomain = ExtensionalSet(graph.elements().map { it.second }.toSet()),
        graph = graph
    )

    fun contains(a: A, b: B): Boolean = OrderedPair(a, b) in graph

    fun inverse(): Relation<B, A> {
        val inverseGraph = ExtensionalSet(
            graph.elements().map { OrderedPair(it.second, it.first) }.toSet()
        )
        return Relation(codomain, domain, inverseGraph)
    }

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
