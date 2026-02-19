package mathsets.analysis

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import mathsets.kernel.RealNumber

class RealAnalysisTest : FunSpec({
    test("limit of 1/n converges to 0") {
        val sequence = RealSequence { n -> RealNumber.ONE / RealNumber.of(n) }
        val result = sequence.convergence(
            tolerance = RealNumber.parse("0.001"),
            maxN = 20_000,
            window = 200
        )

        (result is Limit.Converges<RealNumber>) shouldBe true
        val value = (result as Limit.Converges<RealNumber>).value
        (value.abs() < RealNumber.parse("0.01")) shouldBe true
    }

    test("limit of (1 + 1/n)^n approximates e") {
        val sequence = RealSequence { n ->
            (RealNumber.ONE + (RealNumber.ONE / RealNumber.of(n))) pow n
        }

        val result = sequence.convergence(
            tolerance = RealNumber.parse("0.005"),
            maxN = 10_000,
            window = 120
        )

        (result is Limit.Converges<RealNumber>) shouldBe true
        val value = (result as Limit.Converges<RealNumber>).value
        val eApprox = RealNumber.parse("2.7182818")
        ((value - eApprox).abs() < RealNumber.parse("0.05")) shouldBe true
    }

    test("x^2 is continuous at x=1") {
        val f = { x: RealNumber -> x * x }
        Continuity.isContinuousAt(f, RealNumber.ONE) shouldBe true
    }

    test("derivative of x^3 at x=2 is approximately 12") {
        val cubic = { x: RealNumber -> x * x * x }
        val derivative = Differentiation.derivativeAt(cubic, RealNumber.of(2))
        val expected = RealNumber.of(12)

        ((derivative - expected).abs() < RealNumber.parse("0.01")) shouldBe true
    }

    test("integral of x^2 from 0 to 1 approximates 1/3") {
        val square = { x: RealNumber -> x * x }
        val integral = RiemannIntegral.integrate(square, RealNumber.ZERO, RealNumber.ONE, partitions = 8_000)
        val expected = RealNumber.parse("0.3333333")

        ((integral - expected).abs() < RealNumber.parse("0.005")) shouldBe true
    }
})
