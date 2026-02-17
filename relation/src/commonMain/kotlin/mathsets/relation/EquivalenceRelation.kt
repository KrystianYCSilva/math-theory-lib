package mathsets.relation

import mathsets.set.ExtensionalSet
import mathsets.set.MathSet
import mathsets.kernel.Cardinality

/**
 * Represents an **equivalence relation** on a set.
 *
 * An equivalence relation is a binary relation R ⊆ A × A that is simultaneously
 * reflexive, symmetric, and transitive. These three properties guarantee that R
 * partitions the universe into disjoint equivalence classes.
 *
 * The constructor validates all three properties at initialization time.
 *
 * @param A the element type of the universe
 * @property universe the underlying set A
 * @property relation the reflexive, symmetric, and transitive relation on A
 * @throws IllegalArgumentException if the relation is not reflexive, symmetric, or transitive,
 *   or if the universe is not finite
 */
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

    /**
     * Returns the **equivalence class** of the given [element]: [a] = { x ∈ A | aRx }.
     *
     * @param element the representative element
     * @return the set of all elements equivalent to [element]
     */
    fun equivalenceClass(element: A): MathSet<A> = ExtensionalSet(
        universe.elements().filter { candidate -> relation.contains(element, candidate) }.toSet()
    )

    /**
     * Computes the **quotient set** A/R, the set of all distinct equivalence classes.
     *
     * @return a [MathSet] whose elements are the equivalence classes of R
     */
    fun quotientSet(): MathSet<MathSet<A>> {
        val classes = mutableSetOf<MathSet<A>>()
        for (element in universe.elements()) {
            classes.add(equivalenceClass(element).materialize())
        }
        return ExtensionalSet(classes)
    }

    /**
     * Converts this equivalence relation to the corresponding [Partition].
     *
     * By the **Fundamental Theorem of Equivalence Relations**, every equivalence
     * relation induces a unique partition of the universe, and vice versa.
     *
     * @return the partition induced by this equivalence relation
     */
    fun toPartition(): Partition<A> = Partition(quotientSet(), universe)

    /**
     * Returns the underlying [Relation] backing this equivalence relation.
     *
     * @return the raw relation R ⊆ A × A
     */
    fun asRelation(): Relation<A, A> = relation
}

/**
 * Represents a **partition** of a set into non-empty, pairwise disjoint subsets whose
 * union equals the universe.
 *
 * A partition Π of a set A is a collection of subsets {B₁, B₂, …} such that:
 * 1. Each Bᵢ is non-empty.
 * 2. Bᵢ ∩ Bⱼ = ∅ for all i ≠ j.
 * 3. ⋃ Bᵢ = A.
 *
 * All three conditions are validated at construction time.
 *
 * @param A the element type of the universe
 * @property parts the collection of partition blocks (subsets)
 * @property universe the underlying set A being partitioned
 * @throws IllegalArgumentException if any partition condition is violated or the universe is not finite
 */
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

    /**
     * Converts this partition back to the corresponding [EquivalenceRelation].
     *
     * By the **Fundamental Theorem of Equivalence Relations**, every partition induces
     * a unique equivalence relation where two elements are related if and only if they
     * belong to the same block.
     *
     * @return the equivalence relation induced by this partition
     */
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
