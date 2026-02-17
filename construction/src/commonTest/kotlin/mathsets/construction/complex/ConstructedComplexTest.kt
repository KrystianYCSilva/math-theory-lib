package mathsets.construction.complex

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import mathsets.kernel.ComplexNumber
import mathsets.kernel.RealNumber

class ConstructedComplexTest : FunSpec({
    test("i squared equals minus one") {
        (ConstructedComplex.I * ConstructedComplex.I).toKernel() shouldBe ComplexNumber.of(-1, 0)
    }

    test("roots of x^2 + 1 = 0 are ±i") {
        val plusI = ConstructedComplex.I
        val minusI = -ConstructedComplex.I

        ((plusI * plusI) + ConstructedComplex.ONE).isZero() shouldBe true
        ((minusI * minusI) + ConstructedComplex.ONE).isZero() shouldBe true
    }

    test("conjugate and modulus squared match kernel behavior") {
        val z = ComplexNumber.of(3, 4).toMathComplex()

        z.conjugate().toKernel() shouldBe ComplexNumber.of(3, -4)
        z.modulusSquared().toKernel() shouldBe RealNumber.of(25)
    }

    test("complex isomorphism roundtrip is stable") {
        ConstructedComplexIsomorphism.verifyRoundTrip(limit = 25) shouldBe true
    }
})

