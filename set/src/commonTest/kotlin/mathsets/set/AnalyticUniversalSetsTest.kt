package mathsets.set

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import mathsets.kernel.Cardinality
import mathsets.kernel.ComplexNumber
import mathsets.kernel.ExtendedReal
import mathsets.kernel.ImaginaryNumber
import mathsets.kernel.IrrationalNumber
import mathsets.kernel.RealNumber

class AnalyticUniversalSetsTest : FunSpec({
    test("analytic universal sets expose uncountable cardinality and membership") {
        Reals.cardinality shouldBe Cardinality.Uncountable
        Irrationals.cardinality shouldBe Cardinality.Uncountable
        Imaginaries.cardinality shouldBe Cardinality.Uncountable
        Complexes.cardinality shouldBe Cardinality.Uncountable
        ExtendedReals.cardinality shouldBe Cardinality.Uncountable

        (RealNumber.of(3) in Reals) shouldBe true
        (IrrationalNumber.PI in Irrationals) shouldBe true
        (ImaginaryNumber.of(2) in Imaginaries) shouldBe true
        (ComplexNumber.of(3, -5) in Complexes) shouldBe true
        (ExtendedReal.POSITIVE_INFINITY in ExtendedReals) shouldBe true
    }

    test("analytic universal sets are intentionally non-enumerable") {
        shouldThrow<UnsupportedOperationException> { Reals.elements() }
        shouldThrow<UnsupportedOperationException> { Complexes.elements() }
        shouldThrow<UnsupportedOperationException> { ExtendedReals.elements() }
    }

    test("materialization is rejected for non-enumerable universes") {
        shouldThrow<InfiniteMaterializationException> { Reals.materialize() }
        shouldThrow<InfiniteMaterializationException> { Irrationals.materialize() }
        shouldThrow<InfiniteMaterializationException> { Complexes.materialize() }
    }

    test("intensional filters over reals preserve membership semantics") {
        val nonNegative = Reals.filter { it.signum() >= 0 }
        (RealNumber.of(10) in nonNegative) shouldBe true
        (RealNumber.of(-1) in nonNegative) shouldBe false
    }

    test("universal union/intersection keep identity semantics") {
        val finite = mathSetOf(RealNumber.of(-1), RealNumber.of(2))

        val union = Reals union finite
        val intersection = Reals intersect finite

        (RealNumber.of(999) in union) shouldBe true
        (RealNumber.of(-1) in intersection) shouldBe true
        (RealNumber.of(5) in intersection) shouldBe false
    }
})

