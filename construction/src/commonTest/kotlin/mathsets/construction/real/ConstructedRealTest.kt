package mathsets.construction.real

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
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
})

