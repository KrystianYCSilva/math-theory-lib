package mathsets.set

import mathsets.kernel.Cardinality
import mathsets.kernel.NaturalNumber

internal class UnionSetView<T>(
    private val left: MathSet<T>,
    private val right: MathSet<T>
) : MathSet<T> {
    override val cardinality: Cardinality
        get() = when {
            left.cardinality.isFinite() && right.cardinality.isFinite() ->
                Cardinality.Finite(NaturalNumber.of((left.elements().toSet() + right.elements().toSet()).size))
            left.cardinality == Cardinality.CountablyInfinite || right.cardinality == Cardinality.CountablyInfinite ->
                Cardinality.CountablyInfinite
            else -> Cardinality.Unknown
        }

    override fun contains(element: T): Boolean = (element in left) || (element in right)

    override fun elements(): Sequence<T> = sequence {
        val seen = mutableSetOf<T>()
        val leftIterator = left.elements().iterator()
        val rightIterator = right.elements().iterator()
        while (leftIterator.hasNext() || rightIterator.hasNext()) {
            if (leftIterator.hasNext()) {
                val next = leftIterator.next()
                if (seen.add(next)) yield(next)
            }
            if (rightIterator.hasNext()) {
                val next = rightIterator.next()
                if (seen.add(next)) yield(next)
            }
        }
    }

    override fun materialize(): ExtensionalSet<T> {
        if (!left.cardinality.isFinite() || !right.cardinality.isFinite()) {
            throw InfiniteMaterializationException("Cannot materialize union of non-finite sets")
        }
        return ExtensionalSet(elements().toSet())
    }

    override fun union(other: MathSet<T>): MathSet<T> = UnionSetView(this, other)

    override fun intersect(other: MathSet<T>): MathSet<T> = IntersectionSetView(this, other)
}

internal class IntersectionSetView<T>(
    private val left: MathSet<T>,
    private val right: MathSet<T>
) : MathSet<T> {
    override val cardinality: Cardinality
        get() = when {
            left.cardinality.isFinite() -> Cardinality.Finite(NaturalNumber.of(left.elements().count { it in right }))
            right.cardinality.isFinite() -> Cardinality.Finite(NaturalNumber.of(right.elements().count { it in left }))
            else -> Cardinality.Unknown
        }

    override fun contains(element: T): Boolean = (element in left) && (element in right)

    override fun elements(): Sequence<T> = when {
        left.cardinality.isFinite() -> left.elements().filter { it in right }
        right.cardinality.isFinite() -> right.elements().filter { it in left }
        else -> left.elements().filter { it in right }
    }

    override fun materialize(): ExtensionalSet<T> {
        if (!left.cardinality.isFinite() && !right.cardinality.isFinite()) {
            throw InfiniteMaterializationException("Cannot materialize intersection when both sides are non-finite")
        }
        return ExtensionalSet(elements().toSet())
    }

    override fun union(other: MathSet<T>): MathSet<T> = UnionSetView(this, other)

    override fun intersect(other: MathSet<T>): MathSet<T> = IntersectionSetView(this, other)
}

