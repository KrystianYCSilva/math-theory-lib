package mathsets.cardinal

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import mathsets.kernel.IntegerNumber
import mathsets.kernel.NaturalNumber

class CountabilityTest : FunSpec({
    test("natural to integer zigzag starts correctly") {
        val firstTen = (0..9).map { n ->
            Countability.naturalToInteger(NaturalNumber.of(n))
        }

        firstTen shouldBe listOf(
            IntegerNumber.of(0),
            IntegerNumber.of(1),
            IntegerNumber.of(-1),
            IntegerNumber.of(2),
            IntegerNumber.of(-2),
            IntegerNumber.of(3),
            IntegerNumber.of(-3),
            IntegerNumber.of(4),
            IntegerNumber.of(-4),
            IntegerNumber.of(5)
        )
    }

    test("N <-> Z constructive roundtrip holds on large finite prefix") {
        Countability.verifyIntegerRoundTrip(limit = 10_000) shouldBe true
    }

    test("N <-> Q constructive roundtrip holds on large finite prefix") {
        Countability.verifyRationalRoundTrip(limit = 10_000) shouldBe true
    }

    test("finite prefix mappings produce bijections") {
        Countability.integerBijectionPrefix(size = 120).isBijective() shouldBe true
        Countability.rationalBijectionPrefix(size = 220).isBijective() shouldBe true
    }
})

