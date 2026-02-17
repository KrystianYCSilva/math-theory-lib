package mathsets.kernel

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ArithmeticTest : FunSpec({
    test("natural arithmetic delegates operations") {
        val a = NaturalNumber.of(8)
        val b = NaturalNumber.of(3)
        NaturalArithmetic.add(a, b) shouldBe NaturalNumber.of(11)
        NaturalArithmetic.multiply(a, b) shouldBe NaturalNumber.of(24)
        NaturalArithmetic.divide(a, b) shouldBe NaturalNumber.of(2)
    }

    test("integer arithmetic handles negatives") {
        val a = IntegerNumber.of(5)
        val b = IntegerNumber.of(-7)
        IntegerArithmetic.add(a, b) shouldBe IntegerNumber.of(-2)
        IntegerArithmetic.subtract(a, b) shouldBe IntegerNumber.of(12)
    }

    test("rational arithmetic keeps canonical form") {
        val a = RationalNumber.of(1, 2)
        val b = RationalNumber.of(1, 3)
        RationalArithmetic.add(a, b) shouldBe RationalNumber.of(5, 6)
        RationalArithmetic.multiply(a, b) shouldBe RationalNumber.of(1, 6)
    }

    test("real arithmetic delegates operations") {
        val a = RealNumber.of(3)
        val b = RealNumber.of(2)
        RealArithmetic.add(a, b) shouldBe RealNumber.of(5)
        RealArithmetic.subtract(a, b) shouldBe RealNumber.of(1)
        RealArithmetic.multiply(a, b) shouldBe RealNumber.of(6)
        RealArithmetic.divide(a, b) shouldBe RealNumber.of(1.5)
    }

    test("extended real arithmetic keeps indeterminate forms explicit") {
        ExtendedRealArithmetic.add(
            ExtendedReal.POSITIVE_INFINITY,
            ExtendedReal.NEGATIVE_INFINITY
        ) shouldBe ExtendedReal.INDETERMINATE

        ExtendedRealArithmetic.divide(
            ExtendedReal.of(1),
            ExtendedReal.ZERO
        ) shouldBe ExtendedReal.POSITIVE_INFINITY
    }

    test("complex arithmetic delegates operations without ordering") {
        val a = ComplexNumber.of(2, 3)
        val b = ComplexNumber.of(1, -4)

        ComplexArithmetic.add(a, b) shouldBe ComplexNumber.of(3, -1)
        ComplexArithmetic.subtract(a, b) shouldBe ComplexNumber.of(1, 7)
        ComplexArithmetic.multiply(a, b) shouldBe ComplexNumber.of(14, -5)
    }
})
