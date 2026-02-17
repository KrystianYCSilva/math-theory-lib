package mathsets.set

import mathsets.kernel.Cardinality
import mathsets.kernel.NaturalNumber

class ExtensionalSet<T>(private val elementsBacking: Set<T>) : MathSet<T> {
    private val elements = elementsBacking.toSet()

    override val cardinality: Cardinality = Cardinality.Finite(NaturalNumber.of(elements.size))

    override fun contains(element: T): Boolean = element in elements

    override fun elements(): Sequence<T> = elements.asSequence()

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

    internal val elementsBackingPublic: Set<T>
        get() = elements
}

