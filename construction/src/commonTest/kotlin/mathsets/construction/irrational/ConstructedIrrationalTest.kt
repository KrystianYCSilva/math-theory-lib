package mathsets.construction.irrational

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import mathsets.construction.rational.ConstructedRational
import mathsets.kernel.RationalNumber

class ConstructedIrrationalTest : FunSpec({
    test("algebraic irrationals expose algebraic foundation") {
        ConstructedIrrational.SQRT2.foundation shouldBe IrrationalFoundation.ALGEBRAIC_CONSTRUCTION
        ConstructedIrrational.SQRT3.foundation shouldBe IrrationalFoundation.ALGEBRAIC_CONSTRUCTION
        ConstructedIrrational.GOLDEN_RATIO.foundation shouldBe IrrationalFoundation.ALGEBRAIC_CONSTRUCTION
    }

    test("sqrt2 lower cut behaves as expected on basic rationals") {
        val one = ConstructedRational.fromKernel(RationalNumber.of(1, 1))
        val two = ConstructedRational.fromKernel(RationalNumber.of(2, 1))

        ConstructedIrrational.SQRT2.lowerCutContains(one) shouldBe true
        ConstructedIrrational.SQRT2.lowerCutContains(two) shouldBe false
    }

    test("irrationality witness refutes rational equality candidates") {
        val threeHalves = ConstructedRational.fromKernel(RationalNumber.of(3, 2))
        ConstructedIrrational.SQRT2.refutesAsExactRational(threeHalves) shouldBe true
    }

    test("axiomatic constants are marked and roundtrip through kernel") {
        ConstructedIrrational.PI.foundation shouldBe IrrationalFoundation.AXIOMATIC_SYMBOL
        ConstructedIrrational.E.foundation shouldBe IrrationalFoundation.AXIOMATIC_SYMBOL
        ConstructedIrrationalIsomorphism.verifyKnownConstants() shouldBe true
    }
})
