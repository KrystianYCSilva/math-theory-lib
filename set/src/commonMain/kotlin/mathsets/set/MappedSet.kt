package mathsets.set

import mathsets.kernel.Cardinality
import mathsets.kernel.NaturalNumber

/**
 * A set obtained by applying a mapping function [f] to every element of a [source] set.
 *
 * Corresponds to the image `{ f(x) | x ∈ source }` and models the ZFC Axiom of
 * Replacement. Elements are computed lazily and duplicates are removed.
 *
 * @param T the element type of the source set.
 * @param R the element type of the resulting mapped set.
 * @param source the original set whose elements are transformed.
 * @param f the mapping function applied to each element.
 */
class MappedSet<T, R>(
    private val source: MathSet<T>,
    private val f: (T) -> R
) : MathSet<R> {
    /**
     * The cardinality of this mapped set.
     *
     * For finite sources the exact count is computed (accounting for collisions
     * introduced by [f]); otherwise [Cardinality.Unknown] is returned.
     */
    override val cardinality: Cardinality
        get() = when (source.cardinality) {
            is Cardinality.Finite -> Cardinality.Finite(NaturalNumber.of(elements().toSet().size))
            else -> Cardinality.Unknown
        }

    /**
     * Tests membership by scanning source elements — `true` if any `x ∈ source` satisfies `f(x) == element`.
     */
    override fun contains(element: R): Boolean = source.elements().any { f(it) == element }

    /**
     * Lazily maps and deduplicates source elements.
     */
    override fun elements(): Sequence<R> = source.elements().map(f).distinct()

    /**
     * Materializes this mapped set into an [ExtensionalSet].
     *
     * @throws InfiniteMaterializationException if the source set is not finite.
     */
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
