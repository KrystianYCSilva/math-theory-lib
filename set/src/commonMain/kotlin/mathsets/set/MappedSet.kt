package mathsets.set

import mathsets.kernel.Cardinality
import mathsets.kernel.NaturalNumber

class MappedSet<T, R>(
    private val source: MathSet<T>,
    private val f: (T) -> R
) : MathSet<R> {
    override val cardinality: Cardinality
        get() = when (source.cardinality) {
            is Cardinality.Finite -> Cardinality.Finite(NaturalNumber.of(elements().toSet().size))
            else -> Cardinality.Unknown
        }

    override fun contains(element: R): Boolean = source.elements().any { f(it) == element }

    override fun elements(): Sequence<R> = source.elements().map(f).distinct()

    override fun materialize(): ExtensionalSet<R> {
        if (!source.cardinality.isFinite()) {
            throw InfiniteMaterializationException("Cannot materialize mapped set over non-finite source")
        }
        return ExtensionalSet(elements().toSet())
    }

    override fun union(other: MathSet<R>): MathSet<R> = when {
        this.cardinality.isFinite() && other.cardinality.isFinite() ->
            ExtensionalSet(this.elements().toSet() + other.elements().toSet())
        else -> UnionSetView(this, other)
    }

    override fun intersect(other: MathSet<R>): MathSet<R> = when {
        this.cardinality.isFinite() -> ExtensionalSet(this.elements().filter { it in other }.toSet())
        other.cardinality.isFinite() -> ExtensionalSet(other.elements().filter { it in this }.toSet())
        else -> IntersectionSetView(this, other)
    }
}

