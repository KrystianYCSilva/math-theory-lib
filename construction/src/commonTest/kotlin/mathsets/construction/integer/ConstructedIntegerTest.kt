package mathsets.construction.integer

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import mathsets.construction.natural.VonNeumannNatural
import mathsets.kernel.IntegerNumber
import mathsets.kernel.NaturalNumber

class ConstructedIntegerTest : FunSpec({
    test("equivalence relation on pairs of naturals matches quotient definition") {
        val p = VonNeumannNatural.of(3) to VonNeumannNatural.of(1)
        val q = VonNeumannNatural.of(4) to VonNeumannNatural.of(2)

        ConstructedInteger.areEquivalent(p, q) shouldBe true
        ConstructedInteger.of(p.first, p.second) shouldBe ConstructedInteger.of(q.first, q.second)
    }

    test("constructed integer arithmetic follows expected class formulas") {
        val a = ConstructedInteger.of(VonNeumannNatural.of(3), VonNeumannNatural.of(1)) // 2
        val b = ConstructedInteger.of(VonNeumannNatural.of(2), VonNeumannNatural.of(5)) // -3

        (a + b).toKernel() shouldBe IntegerNumber.of(-1)
        (a - b).toKernel() shouldBe IntegerNumber.of(5)
        (a * b).toKernel() shouldBe IntegerNumber.of(-6)
        (-a).toKernel() shouldBe IntegerNumber.of(-2)
    }

    test("natural embedding sends n to class (n,0)") {
        NaturalIntegerEmbedding.embed(NaturalNumber.of(7)).toKernel() shouldBe IntegerNumber.of(7)
        NaturalNumber.of(3).toMathInteger().toKernel() shouldBe IntegerNumber.of(3)
    }

    test("integer isomorphism roundtrip is stable") {
        ConstructedIntegerIsomorphism.verifyRoundTrip(limit = 150) shouldBe true
    }

    test("constructed integer order matches kernel order") {
        val minusTwo = ConstructedInteger.fromKernel(IntegerNumber.of(-2))
        val five = ConstructedInteger.fromKernel(IntegerNumber.of(5))

        ConstructedIntegerOrder.lessThan(minusTwo, five) shouldBe true
        ConstructedIntegerOrder.greaterThan(five, minusTwo) shouldBe true
        ConstructedIntegerOrder.lessOrEqual(five, five) shouldBe true
    }
})

