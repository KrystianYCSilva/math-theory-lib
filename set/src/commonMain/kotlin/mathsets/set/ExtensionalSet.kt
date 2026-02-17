package mathsets.set

import mathsets.kernel.Cardinality
import mathsets.kernel.NaturalNumber

/**
 * A finite mathematical set defined by explicitly enumerating its members (the extensional mode).
 *
 * This is the most common concrete [MathSet] — it stores its elements in a Kotlin [Set]
 * and delegates membership, equality, and hashing to the backing collection.
 *
 * Extensional sets correspond to the classical notation `{a, b, c}`.
 *
 * @param T the element type.
 * @param elementsBacking the Kotlin [Set] that supplies the members.
 */
class ExtensionalSet<T>(private val elementsBacking: Set<T>) : MathSet<T> {
    private val elements = elementsBacking.toSet()

    /**
     * The cardinality of this set, always [Cardinality.Finite] with size equal to the
     * number of distinct elements.
     */
    override val cardinality: Cardinality = Cardinality.Finite(NaturalNumber.of(elements.size))

    override fun contains(element: T): Boolean = element in elements

    override fun elements(): Sequence<T> = elements.asSequence()

    /**
     * Returns `this` — an extensional set is already materialized.
     */
    override fun materialize(): ExtensionalSet<T> = this

    override fun union(other: MathSet<T>): MathSet<T> = when {
        other is ExtensionalSet -> ExtensionalSet(this.elements + other.elements)
        other.cardinality.isFinite() -> ExtensionalSet(this.elements + other.materialize().elementsBackingPublic)
        else -> UnionSetView(this, other)
    }

    override fun intersect(other: MathSet<T>): MathSet<T> = when {
        other is ExtensionalSet -> ExtensionalSet(this.elements.intersect(other.elements))
        else -> ExtensionalSet(this.elements.filter { it in other }.toSet())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ExtensionalSet<*>) return false
        return elements == other.elements
    }

    override fun hashCode(): Int = elements.hashCode()

    override fun toString(): String = elements.toString()

    /**
     * Exposes the backing [Set] for internal use by other set implementations.
     */
    internal val elementsBackingPublic: Set<T>
        get() = elements
}
