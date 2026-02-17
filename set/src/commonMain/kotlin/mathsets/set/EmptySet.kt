package mathsets.set

import mathsets.kernel.Cardinality
import mathsets.kernel.NaturalNumber

/**
 * The empty set (`∅`), containing no elements.
 *
 * Implemented as a singleton [object] typed to [Nothing] so that it can be safely cast
 * to `MathSet<T>` for any `T` via [MathSet.empty].
 *
 * The empty set is the identity element for union and the annihilator for intersection.
 */
object EmptySet : MathSet<Nothing> {
    /** Always [Cardinality.Finite] with size zero. */
    override val cardinality: Cardinality = Cardinality.Finite(NaturalNumber.ZERO)

    override fun contains(element: Nothing): Boolean = false

    override fun elements(): Sequence<Nothing> = emptySequence()

    override fun materialize(): ExtensionalSet<Nothing> = ExtensionalSet(emptySet())

    override fun union(other: MathSet<@UnsafeVariance Nothing>): MathSet<Nothing> = other as MathSet<Nothing>

    override fun intersect(other: MathSet<@UnsafeVariance Nothing>): MathSet<Nothing> = this
}
