package mathsets.set

import mathsets.kernel.Cardinality
import mathsets.kernel.Generators
import mathsets.kernel.IntegerNumber
import mathsets.kernel.NaturalNumber
import mathsets.kernel.RationalNumber

object Naturals : MathSet<NaturalNumber> {
    override val cardinality: Cardinality = Cardinality.CountablyInfinite

    override fun contains(element: NaturalNumber): Boolean = true

    override fun elements(): Sequence<NaturalNumber> = Generators.naturals()

    override fun materialize(): ExtensionalSet<NaturalNumber> {
        throw InfiniteMaterializationException("Naturals is infinite and cannot be materialized")
    }

    override fun union(other: MathSet<NaturalNumber>): MathSet<NaturalNumber> = UnionSetView(this, other)

    override fun intersect(other: MathSet<NaturalNumber>): MathSet<NaturalNumber> = IntersectionSetView(this, other)
}

object Integers : MathSet<IntegerNumber> {
    override val cardinality: Cardinality = Cardinality.CountablyInfinite

    override fun contains(element: IntegerNumber): Boolean = true

    override fun elements(): Sequence<IntegerNumber> = Generators.integers()

    override fun materialize(): ExtensionalSet<IntegerNumber> {
        throw InfiniteMaterializationException("Integers is infinite and cannot be materialized")
    }

    override fun union(other: MathSet<IntegerNumber>): MathSet<IntegerNumber> = UnionSetView(this, other)

    override fun intersect(other: MathSet<IntegerNumber>): MathSet<IntegerNumber> = IntersectionSetView(this, other)
}

object Rationals : MathSet<RationalNumber> {
    override val cardinality: Cardinality = Cardinality.CountablyInfinite

    override fun contains(element: RationalNumber): Boolean = true

    override fun elements(): Sequence<RationalNumber> = Generators.rationals()

    override fun materialize(): ExtensionalSet<RationalNumber> {
        throw InfiniteMaterializationException("Rationals is infinite and cannot be materialized")
    }

    override fun union(other: MathSet<RationalNumber>): MathSet<RationalNumber> = UnionSetView(this, other)

    override fun intersect(other: MathSet<RationalNumber>): MathSet<RationalNumber> = IntersectionSetView(this, other)
}

