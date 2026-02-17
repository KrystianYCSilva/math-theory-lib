package mathsets.kernel

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ExtendedRealTest : FunSpec({
    test("finite operations stay finite when defined") {
        (ExtendedReal.of(2) + ExtendedReal.of(3)) shouldBe ExtendedReal.of(5)
        (ExtendedReal.of(10) - ExtendedReal.of(4)) shouldBe ExtendedReal.of(6)
        (ExtendedReal.of(3) * ExtendedReal.of(-2)) shouldBe ExtendedReal.of(-6)
        (ExtendedReal.of(12) / ExtendedReal.of(3)) shouldBe ExtendedReal.of(4)
    }

    test("opposite infinities are indeterminate under addition") {
        (ExtendedReal.POSITIVE_INFINITY + ExtendedReal.NEGATIVE_INFINITY) shouldBe ExtendedReal.INDETERMINATE
    }

    test("infinity multiplied by zero is indeterminate") {
        (ExtendedReal.POSITIVE_INFINITY * ExtendedReal.ZERO) shouldBe ExtendedReal.INDETERMINATE
        (ExtendedReal.ZERO * ExtendedReal.NEGATIVE_INFINITY) shouldBe ExtendedReal.INDETERMINATE
    }

    test("division by zero follows sign rules and zero over zero is indeterminate") {
        (ExtendedReal.of(7) / ExtendedReal.ZERO) shouldBe ExtendedReal.POSITIVE_INFINITY
        (ExtendedReal.of(-7) / ExtendedReal.ZERO) shouldBe ExtendedReal.NEGATIVE_INFINITY
        (ExtendedReal.ZERO / ExtendedReal.ZERO) shouldBe ExtendedReal.INDETERMINATE
    }

    test("finite over infinity collapses to zero") {
        (ExtendedReal.of(15) / ExtendedReal.POSITIVE_INFINITY) shouldBe ExtendedReal.ZERO
        (ExtendedReal.of(-15) / ExtendedReal.NEGATIVE_INFINITY) shouldBe ExtendedReal.ZERO
    }

    test("infinity over finite keeps expected sign") {
        (ExtendedReal.POSITIVE_INFINITY / ExtendedReal.of(2)) shouldBe ExtendedReal.POSITIVE_INFINITY
        (ExtendedReal.POSITIVE_INFINITY / ExtendedReal.of(-2)) shouldBe ExtendedReal.NEGATIVE_INFINITY
        (ExtendedReal.NEGATIVE_INFINITY / ExtendedReal.of(-2)) shouldBe ExtendedReal.POSITIVE_INFINITY
    }

    test("infinity over infinity is indeterminate") {
        (ExtendedReal.POSITIVE_INFINITY / ExtendedReal.POSITIVE_INFINITY) shouldBe ExtendedReal.INDETERMINATE
        (ExtendedReal.NEGATIVE_INFINITY / ExtendedReal.NEGATIVE_INFINITY) shouldBe ExtendedReal.INDETERMINATE
    }

    test("ordering is defined for finite and infinite values") {
        (ExtendedReal.POSITIVE_INFINITY > ExtendedReal.of(1_000_000)) shouldBe true
        (ExtendedReal.NEGATIVE_INFINITY < ExtendedReal.of(-1_000_000)) shouldBe true
        (ExtendedReal.of(-3) < ExtendedReal.of(8)) shouldBe true
    }

    test("indeterminate values are not ordered") {
        shouldThrow<IllegalArgumentException> {
            ExtendedReal.INDETERMINATE.compareTo(ExtendedReal.ZERO)
        }
    }
})

