package mathsets.algebra

import mathsets.kernel.ComplexNumber
import mathsets.kernel.IntegerNumber
import mathsets.kernel.NaturalNumber
import mathsets.kernel.RationalNumber
import mathsets.kernel.RealNumber

object IntAdditiveGroup : AdditiveAbelianGroup<IntegerNumber> {
    override val identity: IntegerNumber = IntegerNumber.ZERO
    override fun add(a: IntegerNumber, b: IntegerNumber): IntegerNumber = a + b
    override fun negate(a: IntegerNumber): IntegerNumber = -a
    override fun op(a: IntegerNumber, b: IntegerNumber): IntegerNumber = a + b
    override fun inverse(a: IntegerNumber): IntegerNumber = -a
}

object RationalAdditiveGroup : AdditiveAbelianGroup<RationalNumber> {
    override val identity: RationalNumber = RationalNumber.ZERO
    override fun add(a: RationalNumber, b: RationalNumber): RationalNumber = a + b
    override fun negate(a: RationalNumber): RationalNumber = -a
    override fun op(a: RationalNumber, b: RationalNumber): RationalNumber = a + b
    override fun inverse(a: RationalNumber): RationalNumber = -a
}

object RealAdditiveGroup : AdditiveAbelianGroup<RealNumber> {
    override val identity: RealNumber = RealNumber.ZERO
    override fun add(a: RealNumber, b: RealNumber): RealNumber = a + b
    override fun negate(a: RealNumber): RealNumber = -a
    override fun op(a: RealNumber, b: RealNumber): RealNumber = a + b
    override fun inverse(a: RealNumber): RealNumber = -a
}

object ComplexAdditiveGroup : AdditiveAbelianGroup<ComplexNumber> {
    override val identity: ComplexNumber = ComplexNumber.ZERO
    override fun add(a: ComplexNumber, b: ComplexNumber): ComplexNumber = a + b
    override fun negate(a: ComplexNumber): ComplexNumber = -a
    override fun op(a: ComplexNumber, b: ComplexNumber): ComplexNumber = a + b
    override fun inverse(a: ComplexNumber): ComplexNumber = -a
}

object NaturalAdditiveMonoid : AdditiveMonoid<NaturalNumber> {
    override val identity: NaturalNumber = NaturalNumber.ZERO
    override fun add(a: NaturalNumber, b: NaturalNumber): NaturalNumber = a + b
    override fun op(a: NaturalNumber, b: NaturalNumber): NaturalNumber = a + b
}

object NaturalMultiplicativeMonoid : MultiplicativeMonoid<NaturalNumber> {
    override val identity: NaturalNumber = NaturalNumber.ONE
    override fun multiply(a: NaturalNumber, b: NaturalNumber): NaturalNumber = a * b
    override fun op(a: NaturalNumber, b: NaturalNumber): NaturalNumber = a * b
}

object IntegerRing : EuclideanDomain<IntegerNumber> {
    override val zero: IntegerNumber = IntegerNumber.ZERO
    override val one: IntegerNumber = IntegerNumber.ONE
    override fun add(a: IntegerNumber, b: IntegerNumber): IntegerNumber = a + b
    override fun negate(a: IntegerNumber): IntegerNumber = -a
    override fun multiply(a: IntegerNumber, b: IntegerNumber): IntegerNumber = a * b

    override fun euclideanFunction(a: IntegerNumber): Long =
        a.abs().toString().toLong()

    override fun divideAndRemainder(a: IntegerNumber, b: IntegerNumber): Pair<IntegerNumber, IntegerNumber> {
        val q = a / b
        val r = a - (q * b)
        return q to r
    }
}

object RationalField : OrderedField<RationalNumber> {
    override val zero: RationalNumber = RationalNumber.ZERO
    override val one: RationalNumber = RationalNumber.ONE
    override fun add(a: RationalNumber, b: RationalNumber): RationalNumber = a + b
    override fun negate(a: RationalNumber): RationalNumber = -a
    override fun multiply(a: RationalNumber, b: RationalNumber): RationalNumber = a * b
    override fun reciprocal(a: RationalNumber): RationalNumber = RationalNumber.ONE / a
    override fun compare(a: RationalNumber, b: RationalNumber): Int = a.compareTo(b)
}

object RealField : OrderedField<RealNumber> {
    override val zero: RealNumber = RealNumber.ZERO
    override val one: RealNumber = RealNumber.ONE
    override fun add(a: RealNumber, b: RealNumber): RealNumber = a + b
    override fun negate(a: RealNumber): RealNumber = -a
    override fun multiply(a: RealNumber, b: RealNumber): RealNumber = a * b
    override fun reciprocal(a: RealNumber): RealNumber = RealNumber.ONE / a
    override fun compare(a: RealNumber, b: RealNumber): Int = a.compareTo(b)
}

object ComplexField : Field<ComplexNumber> {
    override val zero: ComplexNumber = ComplexNumber.ZERO
    override val one: ComplexNumber = ComplexNumber.ONE
    override fun add(a: ComplexNumber, b: ComplexNumber): ComplexNumber = a + b
    override fun negate(a: ComplexNumber): ComplexNumber = -a
    override fun multiply(a: ComplexNumber, b: ComplexNumber): ComplexNumber = a * b
    override fun reciprocal(a: ComplexNumber): ComplexNumber = ComplexNumber.ONE / a
}
