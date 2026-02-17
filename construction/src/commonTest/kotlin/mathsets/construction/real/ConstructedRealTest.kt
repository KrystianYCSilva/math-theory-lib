package mathsets.construction.real

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import mathsets.construction.rational.ConstructedRational
import mathsets.kernel.NaturalNumber
import mathsets.kernel.RationalNumber
import mathsets.kernel.RealNumber

class ConstructedRealTest : FunSpec({
    test("rational embedding preserves kernel value") {
        val half = RationalNumber.of(1, 2).toMathReal()
        half.toKernel() shouldBe RealNumber.of(RationalNumber.of(1, 2))
    }

    test("natural to real chain is available in construction layer") {
        NaturalNumber.of(3).toMathReal().toKernel() shouldBe RealNumber.of(3)
    }

    test("constructed real arithmetic and order follow kernel semantics") {
        val a = RealNumber.parse("1.5").toMathReal()
        val b = RealNumber.parse("0.25").toMathReal()

        (a + b).toKernel() shouldBe RealNumber.parse("1.75")
        (a * b).toKernel() shouldBe RealNumber.parse("0.375")
        ConstructedRealOrder.greaterThan(a, b) shouldBe true
    }

    test("real isomorphism roundtrip is stable for finite decimal samples") {
        ConstructedRealIsomorphism.verifyRoundTrip(
            decimalSamples = listOf("0", "1", "-2", "3.5", "-7.25", "10.125")
        ) shouldBe true
    }

    test("rational embedding verification passes on finite grid") {
        ConstructedRealIsomorphism.verifyRationalEmbedding(range = 80) shouldBe true
    }

    test("square root construction of 2 forms cauchy sequence on finite prefix") {
        val sqrt2 = ConstructedReal.squareRootOf(2, iterations = 28)
        sqrt2.isCauchyOnFinitePrefix(prefix = 8) shouldBe true
    }

    test("square root approximants satisfy x^2 <= 2 and converge in sampled precision") {
        val sqrt2 = ConstructedReal.squareRootOf(2, iterations = 40)
        val approx = sqrt2.approximateRational(30)
        val two = ConstructedRational.fromKernel(RationalNumber.of(2, 1))

        (approx * approx <= two) shouldBe true

        val diff = two - (approx * approx)
        val tolerance = ConstructedRational.fromKernel(RationalNumber.of(1, 1000))
        (diff < tolerance) shouldBe true
    }
})
