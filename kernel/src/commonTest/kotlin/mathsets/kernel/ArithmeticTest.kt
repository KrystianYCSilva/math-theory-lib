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
})

