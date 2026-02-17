package mathsets.relation

import mathsets.set.ExtensionalSet
import mathsets.set.MathSet

class RelationProperties<A>(
    private val relation: Relation<A, A>,
    private val universe: MathSet<A>
) {
    private fun related(a: A, b: A): Boolean = relation.contains(a, b)

    fun isReflexive(): Boolean = universe.elements().all { a -> related(a, a) }

    fun isSymmetric(): Boolean = relation.graph.elements().all { (a, b) -> related(b, a) }

    fun isTransitive(): Boolean {
        val edges = relation.graph.elements().toList()
        for (ab in edges) {
            for (bc in edges) {
                if (ab.second == bc.first && !related(ab.first, bc.second)) return false
            }
        }
        return true
    }

    fun isAntisymmetric(): Boolean =
        relation.graph.elements().all { (a, b) -> a == b || !related(b, a) }

    fun isIrreflexive(): Boolean = universe.elements().none { a -> related(a, a) }

    fun isTrichotomous(): Boolean =
        universe.elements().all { a ->
            universe.elements().all { b ->
                related(a, b) || a == b || related(b, a)
            }
        }

    fun isConnex(): Boolean =
        universe.elements().all { a ->
            universe.elements().all { b ->
                a == b || related(a, b) || related(b, a)
            }
        }
}

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

