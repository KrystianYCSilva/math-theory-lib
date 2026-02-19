package mathsets.analysis.metric

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import mathsets.kernel.RealNumber

class MetricSpacesTest : FunSpec({
    test("euclidean space satisfies sampled metric axioms") {
        val euclidean = EuclideanSpace(dimension = 2)
        val samples = listOf(
            RealVector(listOf(RealNumber.ZERO, RealNumber.ZERO)),
            RealVector(listOf(RealNumber.ONE, RealNumber.ZERO)),
            RealVector(listOf(RealNumber.ZERO, RealNumber.ONE)),
            RealVector(listOf(RealNumber.ONE, RealNumber.ONE))
        )

        euclidean.satisfiesAxioms(samples) shouldBe true
    }

    test("euclidean norm gives 3-4-5 distance") {
        val euclidean = EuclideanSpace(dimension = 2)
        val a = RealVector(listOf(RealNumber.ZERO, RealNumber.ZERO))
        val b = RealVector(listOf(RealNumber.of(3), RealNumber.of(4)))
        val distance = euclidean.distance(a, b)

        ((distance - RealNumber.of(5)).abs() < RealNumber.parse("0.0001")) shouldBe true
    }

    test("open and closed balls treat boundary differently") {
        val euclidean = EuclideanSpace(dimension = 1)
        val center = RealVector(listOf(RealNumber.ZERO))
        val boundaryPoint = RealVector(listOf(RealNumber.ONE))

        val open = OpenBall(euclidean, center, RealNumber.ONE)
        val closed = ClosedBall(euclidean, center, RealNumber.ONE)

        open.contains(boundaryPoint) shouldBe false
        closed.contains(boundaryPoint) shouldBe true
    }

    test("discrete metric is valid") {
        val discrete = DiscreteMetricSpace<Int>()
        val samples = listOf(1, 2, 3)

        discrete.satisfiesAxioms(samples) shouldBe true
        discrete.distance(2, 2) shouldBe RealNumber.ZERO
        discrete.distance(2, 3) shouldBe RealNumber.ONE
    }

    test("simplified p-adic metric reflects divisibility") {
        val pAdic = PAdicMetricSpace(prime = 2)
        val distance08 = pAdic.distance(0, 8)
        val distance06 = pAdic.distance(0, 6)

        distance08 shouldBe RealNumber.parse("0.125")
        distance06 shouldBe RealNumber.parse("0.5")
    }

    test("euclidean sample completeness holds for 1/n in R") {
        val euclidean = EuclideanSpace(dimension = 1)
        val sequence: (Int) -> RealVector = { n ->
            RealVector(listOf(RealNumber.ONE / RealNumber.of(n)))
        }
        val limit = RealVector(listOf(RealNumber.ZERO))

        val converges = Completeness.hasCauchyConvergence(
            space = euclidean,
            sequence = sequence,
            candidateLimit = limit,
            tolerance = RealNumber.parse("0.01"),
            maxN = 5_000,
            window = 200
        )

        converges shouldBe true
    }

    test("cauchy-schwarz inequality is satisfied in euclidean space") {
        val euclidean = EuclideanSpace(dimension = 2)
        val a = RealVector(listOf(RealNumber.of(1), RealNumber.of(2)))
        val b = RealVector(listOf(RealNumber.of(3), RealNumber.of(-4)))

        euclidean.satisfiesCauchySchwarz(a, b) shouldBe true
    }
})
