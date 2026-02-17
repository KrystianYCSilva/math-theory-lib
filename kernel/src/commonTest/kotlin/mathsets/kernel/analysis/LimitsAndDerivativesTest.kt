package mathsets.kernel.analysis

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import mathsets.kernel.ExtendedReal
import mathsets.kernel.RealNumber

class LimitsAndDerivativesTest : FunSpec({
    test("reciprocal and one-sided reciprocal at zero follow expected signs") {
        Limits.reciprocal(RealNumber.of(2)) shouldBe ExtendedReal.of(0.5)
        Limits.reciprocal(RealNumber.of(-2)) shouldBe ExtendedReal.of(-0.5)
        Limits.reciprocal(RealNumber.ZERO) shouldBe ExtendedReal.POSITIVE_INFINITY

        Limits.reciprocalAtZero(Side.RIGHT) shouldBe ExtendedReal.POSITIVE_INFINITY
        Limits.reciprocalAtZero(Side.LEFT) shouldBe ExtendedReal.NEGATIVE_INFINITY
        Limits.twoSidedReciprocalAtZero() shouldBe ExtendedReal.INDETERMINATE
    }

    test("difference quotient handles finite and indeterminate cases") {
        val square: (RealNumber) -> RealNumber = { x -> x * x }

        Limits.differenceQuotient(
            f = square,
            at = RealNumber.of(3),
            delta = RealNumber.of(1)
        ) shouldBe ExtendedReal.of(7)

        Limits.differenceQuotient(
            f = square,
            at = RealNumber.of(3),
            delta = RealNumber.ZERO
        ) shouldBe ExtendedReal.INDETERMINATE
    }

    test("forward and symmetric differences provide derivative base operators") {
        val square: (RealNumber) -> RealNumber = { x -> x * x }

        Derivatives.forwardDifference(
            f = square,
            at = RealNumber.of(3),
            h = RealNumber.of(1)
        ) shouldBe ExtendedReal.of(7)

        Derivatives.symmetricDifference(
            f = square,
            at = RealNumber.of(3),
            h = RealNumber.of(1)
        ) shouldBe ExtendedReal.of(6)
    }

    test("forward difference is exact for linear functions with any non-zero step") {
        val linear: (RealNumber) -> RealNumber = { x -> (RealNumber.of(5) * x) + RealNumber.of(2) }

        checkAll(Arb.int(-50, 50), Arb.int(-20, 20)) { at, h ->
            if (h != 0) {
                Derivatives.forwardDifference(
                    f = linear,
                    at = RealNumber.of(at),
                    h = RealNumber.of(h)
                ) shouldBe ExtendedReal.of(5)
            }
        }
    }
})
