package mathsets.kernel

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PredicateTest : FunSpec({
    val even: Predicate<Int> = { it % 2 == 0 }
    val positive: Predicate<Int> = { it > 0 }

    test("and/or/not combinators") {
        (even and positive)(2) shouldBe true
        (even and positive)(-2) shouldBe false
        (even or positive)(-3) shouldBe false
        (even or positive)(-2) shouldBe true
        even.not()(3) shouldBe true
    }

    test("implies and iff combinators") {
        (positive implies even)(-1) shouldBe true
        (positive implies even)(3) shouldBe false
        (even iff positive)(2) shouldBe true
        (even iff positive)(-2) shouldBe false
    }
})

