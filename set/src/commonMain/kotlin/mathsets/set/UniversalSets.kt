package mathsets.set

import mathsets.kernel.Cardinality
import mathsets.kernel.ComplexNumber
import mathsets.kernel.ExtendedReal
import mathsets.kernel.Generators
import mathsets.kernel.ImaginaryNumber
import mathsets.kernel.IntegerNumber
import mathsets.kernel.IrrationalNumber
import mathsets.kernel.NaturalNumber
import mathsets.kernel.RationalNumber
import mathsets.kernel.RealNumber

/**
 * The set of natural numbers (`N = {0, 1, 2, ...}`).
 *
 * Countably infinite; elements are generated lazily via [Generators.naturals].
 */
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

/**
 * The set of integers (`Z = {..., -2, -1, 0, 1, 2, ...}`).
 *
 * Countably infinite; elements are generated lazily via [Generators.integers].
 */
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

/**
 * The set of rational numbers (`Q`).
 *
 * Countably infinite; elements are generated lazily via [Generators.rationals].
 */
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

/**
 * The set of real numbers (`R`).
 *
 * Uncountable; enumeration is not supported and will throw [UnsupportedOperationException].
 * Membership is determined by type — every [RealNumber] instance belongs to this set.
 */
object Reals : MathSet<RealNumber> {
    override val cardinality: Cardinality = Cardinality.Uncountable

    override fun contains(element: RealNumber): Boolean = true

    override fun elements(): Sequence<RealNumber> {
        throw UnsupportedOperationException("Reals is intentionally non-enumerable.")
    }

    override fun materialize(): ExtensionalSet<RealNumber> {
        throw InfiniteMaterializationException("Reals is non-enumerable and cannot be materialized")
    }

    override fun union(other: MathSet<RealNumber>): MathSet<RealNumber> = this

    override fun intersect(other: MathSet<RealNumber>): MathSet<RealNumber> = other
}

/**
 * The set of irrational numbers.
 *
 * Uncountable; enumeration is not supported. The library models irrationals symbolically
 * via [IrrationalNumber].
 */
object Irrationals : MathSet<IrrationalNumber> {
    override val cardinality: Cardinality = Cardinality.Uncountable

    override fun contains(element: IrrationalNumber): Boolean = true

    override fun elements(): Sequence<IrrationalNumber> {
        throw UnsupportedOperationException("Irrationals is intentionally non-enumerable.")
    }

    override fun materialize(): ExtensionalSet<IrrationalNumber> {
        throw InfiniteMaterializationException("Irrationals is non-enumerable and cannot be materialized")
    }

    override fun union(other: MathSet<IrrationalNumber>): MathSet<IrrationalNumber> = this

    override fun intersect(other: MathSet<IrrationalNumber>): MathSet<IrrationalNumber> = other
}

/**
 * The set of pure imaginary numbers.
 *
 * Uncountable; enumeration is not supported.
 */
object Imaginaries : MathSet<ImaginaryNumber> {
    override val cardinality: Cardinality = Cardinality.Uncountable

    override fun contains(element: ImaginaryNumber): Boolean = true

    override fun elements(): Sequence<ImaginaryNumber> {
        throw UnsupportedOperationException("Imaginaries is intentionally non-enumerable.")
    }

    override fun materialize(): ExtensionalSet<ImaginaryNumber> {
        throw InfiniteMaterializationException("Imaginaries is non-enumerable and cannot be materialized")
    }

    override fun union(other: MathSet<ImaginaryNumber>): MathSet<ImaginaryNumber> = this

    override fun intersect(other: MathSet<ImaginaryNumber>): MathSet<ImaginaryNumber> = other
}

/**
 * The set of complex numbers (`C`).
 *
 * Uncountable; enumeration is not supported.
 */
object Complexes : MathSet<ComplexNumber> {
    override val cardinality: Cardinality = Cardinality.Uncountable

    override fun contains(element: ComplexNumber): Boolean = true

    override fun elements(): Sequence<ComplexNumber> {
        throw UnsupportedOperationException("Complexes is intentionally non-enumerable.")
    }

    override fun materialize(): ExtensionalSet<ComplexNumber> {
        throw InfiniteMaterializationException("Complexes is non-enumerable and cannot be materialized")
    }

    override fun union(other: MathSet<ComplexNumber>): MathSet<ComplexNumber> = this

    override fun intersect(other: MathSet<ComplexNumber>): MathSet<ComplexNumber> = other
}

/**
 * The extended real line (`R ∪ {-∞, +∞}`).
 *
 * Uncountable; enumeration is not supported.
 */
object ExtendedReals : MathSet<ExtendedReal> {
    override val cardinality: Cardinality = Cardinality.Uncountable

    override fun contains(element: ExtendedReal): Boolean = true

    override fun elements(): Sequence<ExtendedReal> {
        throw UnsupportedOperationException("ExtendedReals is intentionally non-enumerable.")
    }

    override fun materialize(): ExtensionalSet<ExtendedReal> {
        throw InfiniteMaterializationException("ExtendedReals is non-enumerable and cannot be materialized")
    }

    override fun union(other: MathSet<ExtendedReal>): MathSet<ExtendedReal> = this

    override fun intersect(other: MathSet<ExtendedReal>): MathSet<ExtendedReal> = other
}
