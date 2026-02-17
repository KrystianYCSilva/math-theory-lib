package mathsets.set

import mathsets.kernel.Cardinality
import mathsets.kernel.NaturalNumber

/**
 * A lazily evaluated power set of the given [original] set.
 *
 * The power set `P(S)` is the set of all subsets of `S`. For a finite set with
 * `n` elements the cardinality is `2^n`. Subsets are generated on demand via
 * bitmask enumeration and are not stored in memory all at once.
 *
 * Enumeration is limited to base sets of size <= 30 to avoid integer overflow
 * in the bitmask counter.
 *
 * @param T the element type of the original set.
 * @param original the base set whose subsets are produced.
 */
class LazyPowerSet<T>(private val original: MathSet<T>) : MathSet<MathSet<T>> {
    /**
     * The cardinality of this power set.
     *
     * Returns `2^n` as [Cardinality.Finite] when `n <= 30`; otherwise [Cardinality.Unknown].
     */
    override val cardinality: Cardinality
        get() = when (original.cardinality) {
            is Cardinality.Finite -> {
                val n = when (original) {
                    is ExtensionalSet -> original.elementsBackingPublic.size
                    else -> original.elements().count()
                }
                if (n >= 0 && n <= 30) {
                    Cardinality.Finite(NaturalNumber.of(1 shl n))
                } else {
                    Cardinality.Unknown
                }
            }
            else -> Cardinality.Unknown
        }

    /**
     * Tests whether [element] is a subset of the [original] set.
     *
     * @param element a candidate subset.
     * @return `true` if every member of [element] belongs to [original].
     */
    override fun contains(element: MathSet<T>): Boolean = element.elements().all { it in original }

    /**
     * Lazily enumerates all `2^n` subsets of the [original] set via bitmask iteration.
     *
     * @throws UnsupportedOperationException if the original set has more than 30 elements
     *   or is not a finite [ExtensionalSet].
     */
    override fun elements(): Sequence<MathSet<T>> = when (original) {
        is ExtensionalSet -> {
            val list = original.elementsBackingPublic.toList()
            val n = list.size
            if (n > 30) throw UnsupportedOperationException("powerSet generation limited to sets of size <= 30")
            sequence {
                val limit = 1 shl n
                for (mask in 0 until limit) {
                    val subset = mutableSetOf<T>()
                    var i = 0
                    while (i < n) {
                        if (((mask shr i) and 1) == 1) subset.add(list[i])
                        i++
                    }
                    yield(ExtensionalSet(subset))
                }
            }
        }
        else -> throw UnsupportedOperationException("powerSet.elements() only supported for finite ExtensionalSet")
    }

    /**
     * Materializes the power set into an [ExtensionalSet] of sets.
     *
     * @throws InfiniteMaterializationException if the original set is not finite.
     */
    override fun materialize(): ExtensionalSet<MathSet<T>> {
        if (!original.cardinality.isFinite()) {
            throw InfiniteMaterializationException("Cannot materialize power set of non-finite base set")
        }
        return ExtensionalSet(elements().toSet())
    }

    override fun union(other: MathSet<MathSet<T>>): MathSet<MathSet<T>> = UnionSetView(this, other)

    override fun intersect(other: MathSet<MathSet<T>>): MathSet<MathSet<T>> = IntersectionSetView(this, other)
}
