package mathsets.construction.irrational

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import mathsets.construction.real.toMathReal
import mathsets.kernel.IrrationalNumber
import mathsets.kernel.RealNumber

class ConstructedIrrationalTest : FunSpec({
    test("known irrational constants are mapped and preserved") {
        ConstructedIrrational.PI.toKernel() shouldBe IrrationalNumber.PI
        ConstructedIrrational.E.toKernel() shouldBe IrrationalNumber.E
        ConstructedIrrationalIsomorphism.verifyKnownConstants() shouldBe true
    }

    test("custom symbolic irrational keeps symbol and approximation") {
        val approximation = RealNumber.parse("2.2360679").toMathReal()
        val sqrtFive = ConstructedIrrational.of("sqrt(5)", approximation)

        sqrtFive.symbol shouldBe "sqrt(5)"
        sqrtFive.toKernel().approximation shouldBe RealNumber.parse("2.2360679")
    }

    test("irrational arithmetic delegates to constructed reals") {
        val result = ConstructedIrrational.PI + ConstructedIrrational.E
        result.toKernel() shouldBe (IrrationalNumber.PI.approximation + IrrationalNumber.E.approximation)
    }
})

