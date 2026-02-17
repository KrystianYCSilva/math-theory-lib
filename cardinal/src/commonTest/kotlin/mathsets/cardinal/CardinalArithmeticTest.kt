package mathsets.cardinal

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import mathsets.kernel.Cardinality
import mathsets.kernel.NaturalNumber

class CardinalArithmeticTest : FunSpec({
    test("finite cardinal arithmetic matches natural arithmetic") {
        val a = Cardinality.Finite(NaturalNumber.of(4))
        val b = Cardinality.Finite(NaturalNumber.of(7))

        CardinalArithmetic.add(a, b) shouldBe Cardinality.Finite(NaturalNumber.of(11))
        CardinalArithmetic.multiply(a, b) shouldBe Cardinality.Finite(NaturalNumber.of(28))
        CardinalArithmetic.power(a, Cardinality.Finite(NaturalNumber.of(2))) shouldBe
            Cardinality.Finite(NaturalNumber.of(16))
    }

    test("aleph zero arithmetic laws hold") {
        val aleph0 = CardinalArithmetic.aleph0

        CardinalArithmetic.add(aleph0, aleph0) shouldBe Cardinality.CountablyInfinite
        CardinalArithmetic.multiply(aleph0, aleph0) shouldBe Cardinality.CountablyInfinite
    }

    test("two to aleph zero is continuum in this model") {
        val two = Cardinality.Finite(NaturalNumber.of(2))
        val result = CardinalArithmetic.power(two, CardinalArithmetic.aleph0)

        result shouldBe Cardinality.Uncountable
    }
})

