package mathsets.construction.rational

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import mathsets.construction.integer.ConstructedInteger
import mathsets.construction.integer.toMathInteger
import mathsets.kernel.IntegerNumber
import mathsets.kernel.NaturalNumber
import mathsets.kernel.RationalNumber

class ConstructedRationalTest : FunSpec({
    test("equivalence relation on integer pairs matches rational quotient") {
        val p = ConstructedInteger.fromKernel(IntegerNumber.of(1)) to
            ConstructedInteger.fromKernel(IntegerNumber.of(2))
        val q = ConstructedInteger.fromKernel(IntegerNumber.of(2)) to
            ConstructedInteger.fromKernel(IntegerNumber.of(4))

        ConstructedRational.areEquivalent(p, q) shouldBe true
        ConstructedRational.of(p.first, p.second) shouldBe ConstructedRational.of(q.first, q.second)
    }

    test("natural to integer to rational chain is available") {
        val lifted = NaturalNumber.of(3).toMathInteger().toMathRational()
        lifted.toKernel() shouldBe RationalNumber.of(3, 1)
    }

    test("rational isomorphism roundtrip is stable on finite grid") {
        ConstructedRationalIsomorphism.verifyRoundTrip(range = 80) shouldBe true
    }

    test("density between one third and one half is five twelfths") {
        val oneThird = ConstructedRational.fromKernel(RationalNumber.of(1, 3))
        val oneHalf = ConstructedRational.fromKernel(RationalNumber.of(1, 2))

        RationalDensity.between(oneThird, oneHalf).toKernel() shouldBe RationalNumber.of(5, 12)
    }

    test("constructed rational arithmetic and order behave as expected") {
        val a = ConstructedRational.fromKernel(RationalNumber.of(1, 2))
        val b = ConstructedRational.fromKernel(RationalNumber.of(1, 3))

        (a + b).toKernel() shouldBe RationalNumber.of(5, 6)
        (a * b).toKernel() shouldBe RationalNumber.of(1, 6)
        ConstructedRationalOrder.greaterThan(a, b) shouldBe true
    }
})

