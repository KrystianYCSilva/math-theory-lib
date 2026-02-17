package mathsets.construction.complex

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import mathsets.kernel.ComplexNumber
import mathsets.kernel.ImaginaryNumber
import mathsets.kernel.RealNumber

class ConstructedImaginaryTest : FunSpec({
    test("product of pure imaginaries yields negative real") {
        val twoI = ImaginaryNumber.of(2).toMathImaginary()
        val threeI = ImaginaryNumber.of(3).toMathImaginary()

        (twoI times threeI).toKernel() shouldBe RealNumber.of(-6)
    }

    test("imaginary to complex embedding is consistent") {
        ConstructedImaginary.I.toComplex().toKernel() shouldBe ComplexNumber.I
    }

    test("imaginary isomorphism roundtrip is stable") {
        ConstructedImaginaryIsomorphism.verifyRoundTrip(limit = 120) shouldBe true
    }
})

