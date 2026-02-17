package mathsets.construction.natural

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import mathsets.kernel.NaturalNumber
import mathsets.set.MathSet
import mathsets.set.mathSetOf

class VonNeumannNaturalConstructionTest : FunSpec({
    test("von neumann representation of two is {empty, {empty}}") {
        val zero = VonNeumannNatural.ZERO
        val one = VonNeumannNatural.ONE
        val two = one.succ()

        zero.toSet() shouldBe MathSet.empty<VonNeumannNatural>()
        one.toSet() shouldBe MathSet.singleton(zero)
        two.toSet() shouldBe mathSetOf(zero, one)
    }

    test("von neumann peano system validates injectivity and zero not successor") {
        VonNeumannPeanoSystem.verifyInjectivity(sampleSize = 300) shouldBe true
        VonNeumannPeanoSystem.verifyZeroNotSuccessor(sampleSize = 300) shouldBe true
    }

    test("kernel-von-neumann isomorphism roundtrip works up to one hundred") {
        NaturalIsomorphism.verifyKernelRoundTrip(limit = 100) shouldBe true
        NaturalIsomorphism.verifyVonNeumannRoundTrip(limit = 100) shouldBe true
    }

    test("isomorphism preserves addition and multiplication") {
        NaturalIsomorphism.verifyAdditionPreservation(limit = 30) shouldBe true
        NaturalIsomorphism.verifyMultiplicationPreservation(limit = 20) shouldBe true
    }

    test("recursive arithmetic matches expected finite values") {
        val two = VonNeumannNatural.of(2)
        val three = VonNeumannNatural.of(3)

        NaturalIsomorphism.toKernel(VonNeumannNaturalArithmetic.add(two, three)) shouldBe NaturalNumber.of(5)
        NaturalIsomorphism.toKernel(VonNeumannNaturalArithmetic.multiply(two, three)) shouldBe NaturalNumber.of(6)
        NaturalIsomorphism.toKernel(VonNeumannNaturalArithmetic.power(two, three)) shouldBe NaturalNumber.of(8)
    }

    test("order is defined by existence of witness c where a + c = b") {
        val two = VonNeumannNatural.of(2)
        val five = VonNeumannNatural.of(5)

        VonNeumannNaturalOrder.lessOrEqual(two, five) shouldBe true
        VonNeumannNaturalOrder.lessOrEqual(five, two) shouldBe false
        VonNeumannNaturalOrder.lessThan(two, five) shouldBe true
        VonNeumannNaturalOrder.greaterThan(five, two) shouldBe true
    }

    test("peano recursion builds triangular number on finite prefix") {
        val triangular = VonNeumannPeanoSystem.recursion(base = NaturalNumber.ZERO) { current, acc ->
            acc + NaturalIsomorphism.toKernel(current).succ()
        }

        triangular(VonNeumannNatural.of(4)) shouldBe NaturalNumber.of(10)
    }
})
