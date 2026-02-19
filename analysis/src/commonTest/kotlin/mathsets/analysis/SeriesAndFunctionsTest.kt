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
})
