package mathsets.analysis

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import mathsets.kernel.RealNumber

class SeriesAndFunctionsTest : FunSpec({
    test("geometric series converges to expected sum") {
        val sequence = Series.geometric(
            first = RealNumber.ONE,
            ratio = RealNumber.parse("0.5")
        )

        val result = Series.approximateInfiniteSum(
            sequence = sequence,
            tolerance = RealNumber.parse("0.001"),
            maxN = 1_500,
            window = 120
        )

        (result is Limit.Converges<RealNumber>) shouldBe true
        val value = (result as Limit.Converges<RealNumber>).value
        val expected = RealNumber.of(2)
        ((value - expected).abs() < RealNumber.parse("0.02")) shouldBe true
    }

    test("power series derivative matches expected polynomial") {
        val series = PowerSeries(
            listOf(
                RealNumber.of(1),
                RealNumber.of(2),
                RealNumber.of(3)
            )
        )

        val derivative = series.derivative()
        val valueAtTwo = derivative.evaluate(RealNumber.of(2))
        valueAtTwo shouldBe RealNumber.of(14)
    }

    test("elementary function approximations are consistent") {
        val one = RealNumber.ONE
        val expOne = ElementaryFunctions.exp(one)
        val lnExpOne = ElementaryFunctions.naturalLog(expOne)
        ((lnExpOne - one).abs() < RealNumber.parse("0.001")) shouldBe true

        val angle = RealNumber.parse("0.7")
        val sin = ElementaryFunctions.sin(angle)
        val cos = ElementaryFunctions.cos(angle)
        val identity = sin * sin + cos * cos
        ((identity - RealNumber.ONE).abs() < RealNumber.parse("0.01")) shouldBe true
    }

    test("fundamental theorem of calculus checker passes on x cubed") {
        val antiderivative = { x: RealNumber -> (x pow 3) / RealNumber.of(3) }
        val derivative = { x: RealNumber -> x * x }

        val ok = FundamentalTheoremOfCalculus.verify(
            antiderivative = antiderivative,
            derivative = derivative,
            a = RealNumber.ZERO,
            b = RealNumber.ONE,
            tolerance = RealNumber.parse("0.003"),
            partitions = 10_000
        )

        ok shouldBe true
    }

    test("absolute convergence and ratio test agree for geometric alternating series") {
        val sequence = RealSequence { n ->
            val sign = if (n % 2 == 0) RealNumber.of(-1) else RealNumber.ONE
            sign / (RealNumber.of(2) pow n)
        }

        Series.isAbsolutelyConvergent(
            sequence = sequence,
            tolerance = RealNumber.parse("0.001"),
            maxN = 4_000,
            window = 150
        ) shouldBe true

        Series.ratioTest(sequence) shouldBe SeriesConvergenceResult.Converges
    }

    test("root test detects divergence for powers of two") {
        val sequence = RealSequence { n -> RealNumber.of(2) pow n }
        Series.rootTest(sequence) shouldBe SeriesConvergenceResult.Diverges
    }

    test("comparison test infers convergence with p-series upper bound") {
        val candidate = RealSequence { n ->
            RealNumber.ONE / (RealNumber.of(n) * (RealNumber.of(n) + RealNumber.ONE))
        }
        val reference = RealSequence { n -> RealNumber.ONE / (RealNumber.of(n) pow 2) }

        Series.comparisonTest(
            candidate = candidate,
            reference = reference,
            referenceBehavior = SeriesConvergenceResult.Converges
        ) shouldBe SeriesConvergenceResult.Converges
    }

    test("basel series approximates pi squared over six") {
        val sequence = RealSequence { n -> RealNumber.ONE / (RealNumber.of(n) pow 2) }
        val result = Series.approximateInfiniteSum(
            sequence = sequence,
            tolerance = RealNumber.parse("0.0002"),
            maxN = 20_000,
            window = 300
        )

        (result is Limit.Converges<RealNumber>) shouldBe true
        val value = (result as Limit.Converges<RealNumber>).value
        val expected = RealNumber.parse("1.644934")
        ((value - expected).abs() < RealNumber.parse("0.01")) shouldBe true
    }

    test("hyperbolic identity cosh squared minus sinh squared equals one") {
        val x = RealNumber.parse("0.8")
        val sinh = ElementaryFunctions.sinh(x)
        val cosh = ElementaryFunctions.cosh(x)

        val identity = cosh * cosh - sinh * sinh
        ((identity - RealNumber.ONE).abs() < RealNumber.parse("0.01")) shouldBe true
    }
})
