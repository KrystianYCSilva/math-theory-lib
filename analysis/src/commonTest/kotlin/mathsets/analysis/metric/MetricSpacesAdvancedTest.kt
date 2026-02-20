package mathsets.analysis.metric

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import mathsets.kernel.RealNumber

class MetricSpacesAdvancedTest : FunSpec({
    test("euclidean space satisfies completeness for convergent sequence") {
        val euclidean = EuclideanSpace(dimension = 1)

        // Sequence: 1/n converges to 0
        val sequence: (Int) -> RealVector = { n ->
            RealVector(listOf(RealNumber.ONE / RealNumber.of(n)))
        }
        val limit = RealVector(listOf(RealNumber.ZERO))

        val isCauchy = Completeness.isCauchy(
            space = euclidean,
            sequence = sequence,
            tolerance = RealNumber.parse("0.001"),
            maxN = 5_000,
            window = 200
        )

        val converges = Completeness.hasCauchyConvergence(
            space = euclidean,
            sequence = sequence,
            candidateLimit = limit,
            tolerance = RealNumber.parse("0.001"),
            maxN = 5_000,
            window = 200
        )

        isCauchy shouldBe true
        converges shouldBe true
    }

    test("banach space sample completeness holds for geometric series") {
        val euclidean = EuclideanSpace(dimension = 1)

        // Partial sums of geometric series: s_n = Σ(1/2^k) from k=0 to n
        // This converges to 2
        val sequence: (Int) -> RealVector = { n ->
            var sum = RealNumber.ZERO
            for (k in 0..n) {
                sum += RealNumber.ONE / (RealNumber.TWO pow k)
            }
            RealVector(listOf(sum))
        }

        val limit = RealVector(listOf(RealNumber.TWO))

        val converges = Completeness.hasCauchyConvergence(
            space = euclidean,
            sequence = sequence,
            candidateLimit = limit,
            tolerance = RealNumber.parse("0.001"),
            maxN = 3_000,
            window = 150
        )

        converges shouldBe true
    }

    test("cauchy schwarz inequality holds for multiple vector pairs") {
        val euclidean = EuclideanSpace(dimension = 3)

        val testCases = listOf(
            Triple(
                RealVector(listOf(RealNumber.ONE, RealNumber.TWO, RealNumber.of(3))),
                RealVector(listOf(RealNumber.of(4), RealNumber.of(-5), RealNumber.of(6))),
                "general vectors"
            ),
            Triple(
                RealVector(listOf(RealNumber.ZERO, RealNumber.ZERO, RealNumber.ONE)),
                RealVector(listOf(RealNumber.ONE, RealNumber.ZERO, RealNumber.ZERO)),
                "orthogonal unit vectors"
            ),
            Triple(
                RealVector(listOf(RealNumber.ONE, RealNumber.ONE, RealNumber.ONE)),
                RealVector(listOf(RealNumber.TWO, RealNumber.TWO, RealNumber.TWO)),
                "parallel vectors"
            )
        )

        for ((a, b, description) in testCases) {
            euclidean.satisfiesCauchySchwarz(a, b) shouldBe true
        }
    }

    test("hilbert space structure on R^n with inner product") {
        val hilbert = EuclideanSpace(dimension = 2)

        val u = RealVector(listOf(RealNumber.ONE, RealNumber.ZERO))
        val v = RealVector(listOf(RealNumber.ZERO, RealNumber.ONE))

        // Orthonormal basis check
        hilbert.innerProduct(u, u) shouldBe RealNumber.ONE
        hilbert.innerProduct(v, v) shouldBe RealNumber.ONE
        hilbert.innerProduct(u, v).abs() shouldBe RealNumber.ZERO

        // Norm consistency: ||v||² = ⟨v,v⟩
        hilbert.norm(u) * hilbert.norm(u) shouldBe hilbert.innerProduct(u, u)
        hilbert.norm(v) * hilbert.norm(v) shouldBe hilbert.innerProduct(v, v)
    }

    test("open and closed balls behave correctly at boundary") {
        val euclidean = EuclideanSpace(dimension = 1)
        val center = RealVector(listOf(RealNumber.ZERO))
        val radius = RealNumber.ONE

        val openBall = OpenBall(euclidean, center, radius)
        val closedBall = ClosedBall(euclidean, center, radius)

        // Interior point
        val interior = RealVector(listOf(RealNumber.parse("0.5")))
        openBall.contains(interior) shouldBe true
        closedBall.contains(interior) shouldBe true

        // Boundary point
        val boundary = RealVector(listOf(RealNumber.ONE))
        openBall.contains(boundary) shouldBe false
        closedBall.contains(boundary) shouldBe true

        // Exterior point
        val exterior = RealVector(listOf(RealNumber.TWO))
        openBall.contains(exterior) shouldBe false
        closedBall.contains(exterior) shouldBe false
    }

    test("discrete metric space properties") {
        val discrete = DiscreteMetricSpace<String>()

        val samples = listOf("a", "b", "c", "d")
        discrete.satisfiesAxioms(samples) shouldBe true

        // Distance properties
        discrete.distance("a", "a") shouldBe RealNumber.ZERO
        discrete.distance("a", "b") shouldBe RealNumber.ONE
        discrete.distance("b", "a") shouldBe RealNumber.ONE // Symmetry
    }

    test("p-adic metric reflects divisibility by prime") {
        val pAdic2 = PAdicMetricSpace(prime = 2)
        val pAdic3 = PAdicMetricSpace(prime = 3)

        // Powers of 2 should have decreasing distance to 0
        pAdic2.distance(0, 8) shouldBe RealNumber.parse("0.125") // 2^(-3)
        pAdic2.distance(0, 4) shouldBe RealNumber.parse("0.25")  // 2^(-2)
        pAdic2.distance(0, 2) shouldBe RealNumber.parse("0.5")   // 2^(-1)

        // Powers of 3 for p=3
        pAdic3.distance(0, 9) shouldBe RealNumber.parse("0.1111111") // ≈ 3^(-2)
        pAdic3.distance(0, 3) shouldBe RealNumber.parse("0.3333333") // ≈ 3^(-1)
    }

    test("normed space operations are consistent") {
        val euclidean = EuclideanSpace(dimension = 2)

        val v = RealVector(listOf(RealNumber.of(3), RealNumber.of(4)))

        // ||v|| = 5 for (3,4)
        val norm = euclidean.norm(v)
        ((norm - RealNumber.of(5)).abs() < RealNumber.parse("0.0001")) shouldBe true

        // Homogeneity: ||α*v|| = |α|*||v||
        val alpha = RealNumber.TWO
        val scaled = euclidean.scalarMultiply(alpha, v)
        val leftSide = euclidean.norm(scaled)
        val rightSide = alpha.abs() * norm
        ((leftSide - rightSide).abs() < RealNumber.parse("0.0001")) shouldBe true
    }
})
