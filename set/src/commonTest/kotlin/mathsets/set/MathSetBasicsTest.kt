package mathsets.set

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MathSetBasicsTest : FunSpec({
    test("extensional and intensional membership") {
        val a = ExtensionalSet(setOf(1, 2, 3))
        val evens = IntensionalSet(ExtensionalSet((0..100).toSet())) { it % 2 == 0 }
        (2 in a) shouldBe true
        (5 in a) shouldBe false
        (4 in evens) shouldBe true
        (5 in evens) shouldBe false
    }

    test("subset, proper subset and disjoint checks") {
        val a = mathSetOf(1, 2)
        val b = mathSetOf(1, 2, 3)
        val c = mathSetOf(4, 5)
        (a isSubsetOf b) shouldBe true
        (a isProperSubsetOf b) shouldBe true
        (a isDisjointWith c) shouldBe true
    }

    test("factory helpers") {
        val byVararg = mathSetOf(1, 2, 3)
        val byRange = mathSetOf(1..3)
        val byIterable = mathSetOf(listOf(1, 2, 3))
        (byVararg isSubsetOf byRange) shouldBe true
        (byRange isSubsetOf byIterable) shouldBe true
    }
})

