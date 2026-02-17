package mathsets.set

import mathsets.kernel.Cardinality
import mathsets.kernel.NaturalNumber

object EmptySet : MathSet<Nothing> {
    override val cardinality: Cardinality = Cardinality.Finite(NaturalNumber.ZERO)

    override fun contains(element: Nothing): Boolean = false

    override fun elements(): Sequence<Nothing> = emptySequence()

    override fun materialize(): ExtensionalSet<Nothing> = ExtensionalSet(emptySet())

    override fun union(other: MathSet<@UnsafeVariance Nothing>): MathSet<Nothing> = other as MathSet<Nothing>

    override fun intersect(other: MathSet<@UnsafeVariance Nothing>): MathSet<Nothing> = this
}

