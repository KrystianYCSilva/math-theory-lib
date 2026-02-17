package mathsets.kernel

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ImaginaryNumberTest : FunSpec({
    test("imaginary multiplication collapses to negative real") {
        (ImaginaryNumber.I times ImaginaryNumber.I) shouldBe RealNumber.of(-1)
        (ImaginaryNumber.of(2) times ImaginaryNumber.of(3)) shouldBe RealNumber.of(-6)
    }

    test("conversion to complex preserves coefficient") {
        ImaginaryNumber.of(5).toComplex() shouldBe ComplexNumber.of(0, 5)
    }
})

