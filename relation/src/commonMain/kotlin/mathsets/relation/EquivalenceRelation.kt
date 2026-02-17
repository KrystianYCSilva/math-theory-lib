package mathsets.relation

import mathsets.set.ExtensionalSet
import mathsets.set.MathSet
import mathsets.kernel.Cardinality

class EquivalenceRelation<A>(
    private val universe: MathSet<A>,
    private val relation: Relation<A, A>
) {
    init {
        require(universe.cardinality is Cardinality.Finite) {
            "EquivalenceRelation verification currently requires a finite universe."
        }
        val props = RelationProperties(relation, universe)
        require(props.isReflexive()) { "Relation must be reflexive." }
        require(props.isSymmetric()) { "Relation must be symmetric." }
        require(props.isTransitive()) { "Relation must be transitive." }
    }

    fun equivalenceClass(element: A): MathSet<A> = ExtensionalSet(
        universe.elements().filter { candidate -> relation.contains(element, candidate) }.toSet()
    )

    fun quotientSet(): MathSet<MathSet<A>> {
        val classes = mutableSetOf<MathSet<A>>()
        for (element in universe.elements()) {
            classes.add(equivalenceClass(element).materialize())
        }
        return ExtensionalSet(classes)
    }

    fun toPartition(): Partition<A> = Partition(quotientSet(), universe)

    fun asRelation(): Relation<A, A> = relation
}

class Partition<A>(
    val parts: MathSet<MathSet<A>>,
    val universe: MathSet<A>
) {
    init {
        require(universe.cardinality is Cardinality.Finite) {
            "Partition verification currently requires a finite universe."
        }
        val normalizedParts = parts.elements().map { it.materialize() }.toList()
        require(normalizedParts.all { it.elements().any() }) { "Partition parts must be non-empty." }

        for (i in normalizedParts.indices) {
            for (j in (i + 1) until normalizedParts.size) {
                val disjoint = normalizedParts[i].elements().none { it in normalizedParts[j] }
                require(disjoint) { "Partition parts must be pairwise disjoint." }
            }
        }

        val union = normalizedParts.flatMap { it.elements().toList() }.toSet()
        require(union == universe.elements().toSet()) { "Union of all parts must equal the universe." }
    }

    fun toEquivalenceRelation(): EquivalenceRelation<A> {
        val pairs = mutableSetOf<OrderedPair<A, A>>()
        val normalizedParts = parts.elements().map { it.materialize() }.toList()
        for (part in normalizedParts) {
            val elements = part.elements().toList()
            for (a in elements) {
                for (b in elements) {
                    pairs.add(OrderedPair(a, b))
                }
            }
        }
        val relation = Relation(universe, universe, ExtensionalSet(pairs))
        return EquivalenceRelation(universe, relation)
    }
}
