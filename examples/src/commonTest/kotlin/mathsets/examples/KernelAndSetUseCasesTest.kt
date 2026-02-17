package mathsets.examples

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import mathsets.kernel.ComplexNumber
import mathsets.kernel.ExtendedReal
import mathsets.kernel.RealNumber

class KernelAndSetUseCasesTest : FunSpec({
    test("reciprocal handles zero with infinity") {
        KernelAndSetUseCases.reciprocal(RealNumber.of(2)) shouldBe ExtendedReal.of(0.5)
        KernelAndSetUseCases.reciprocal(RealNumber.ZERO) shouldBe ExtendedReal.POSITIVE_INFINITY
    }

    test("square difference quotient handles delta equals zero") {
        KernelAndSetUseCases.squareDifferenceQuotient(RealNumber.of(5)) shouldBe ExtendedReal.of(5)
        KernelAndSetUseCases.squareDifferenceQuotient(RealNumber.ZERO) shouldBe ExtendedReal.INDETERMINATE
    }

    test("roots of x^2 + 1 are ±i") {
        KernelAndSetUseCases.rootsOfXSquarePlusOne() shouldBe Pair(ComplexNumber.I, -ComplexNumber.I)
    }

    test("partition by parity splits finite domain correctly") {
        val (evens, odds) = KernelAndSetUseCases.partitionByParity(1..6)
        evens.elements().toSet() shouldBe setOf(2, 4, 6)
        odds.elements().toSet() shouldBe setOf(1, 3, 5)
    }

    test("bijection roundtrip preserves the original value") {
        KernelAndSetUseCases.bijectionRoundTrip(1) shouldBe 1
        KernelAndSetUseCases.bijectionRoundTrip(2) shouldBe 2
        KernelAndSetUseCases.bijectionRoundTrip(3) shouldBe 3
    }

    test("bijection roundtrip rejects input outside finite domain") {
        shouldThrow<IllegalArgumentException> {
            KernelAndSetUseCases.bijectionRoundTrip(10)
        }
    }
})

