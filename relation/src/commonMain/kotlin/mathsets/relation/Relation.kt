package mathsets.relation

import mathsets.set.ExtensionalSet
import mathsets.set.MathSet

class Relation<A, B>(val graph: MathSet<OrderedPair<A, B>>) {
    fun domain(): MathSet<A> = ExtensionalSet(graph.elements().map { it.first }.toSet())
    fun codomain(): MathSet<B> = ExtensionalSet(graph.elements().map { it.second }.toSet())

    fun inverse(): Relation<B, A> {
        val inv = ExtensionalSet(graph.elements().map { OrderedPair(it.second, it.first) }.toSet())
        return Relation(inv)
    }

    fun <C> compose(other: Relation<B, C>): Relation<A, C> {
        val pairs = mutableSetOf<OrderedPair<A, C>>()
        val g = graph.elements().toList()
        val h = other.graph.elements().toList()
        for (p in g) {
            for (q in h) {
                if (p.second == q.first) pairs.add(OrderedPair(p.first, q.second))
            }
        }
        return Relation(ExtensionalSet(pairs))
    }
}
