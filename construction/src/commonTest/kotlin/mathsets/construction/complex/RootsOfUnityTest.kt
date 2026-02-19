package mathsets.construction.complex

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import mathsets.construction.real.ConstructedReal

class RootsOfUnityTest : FunSpec({
    test("real embedding into complex preserves real part") {
        val value = ConstructedReal.fromDecimalExpansion("2.5")
        val embedded = RealComplexEmbedding.embed(value)

        embedded.real.toKernel() shouldBe value.toKernel()
        embedded.imaginary.isZero() shouldBe true
    }

    test("cube roots of unity satisfy z^3 = 1") {
        RootsOfUnity.verifyEquation(n = 3, tolerance = 1e-5) shouldBe true
    }

    test("all n-th roots count is n") {
        RootsOfUnity.all(8).size shouldBe 8
    }
})
