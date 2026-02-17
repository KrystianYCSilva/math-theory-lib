package mathsets.function

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import mathsets.set.ExtensionalSet
import mathsets.set.MathSet

class MathFunctionTest : FunSpec({
    test("bijection inverse satisfies f^-1(f(x)) = x") {
        val domain = ExtensionalSet(setOf(1, 2, 3))
        val codomain = ExtensionalSet(setOf("a", "b", "c"))
        val mapping = mapOf(1 to "a", 2 to "b", 3 to "c")

        val f = Bijection(domain, codomain) { x -> mapping.getValue(x) }
        val inverse = f.inverse()

        inverse(f(1)) shouldBe 1
        inverse(f(2)) shouldBe 2
        inverse(f(3)) shouldBe 3
    }

    test("composition obeys (g ∘ f)(x) = g(f(x))") {
        val domain = ExtensionalSet((0..100).toSet())
        val middle = ExtensionalSet((1..101).toSet())
        val codomain = ExtensionalSet((2..202 step 2).toSet())

        val f = MathFunction(domain, middle) { x -> x + 1 }
        val g = MathFunction(middle, codomain) { y -> y * 2 }
        val composed = g.compose(f)

        checkAll(Arb.int(0, 100)) { x ->
            composed(x) shouldBe g(f(x))
        }
    }

    test("choice function selects one element from each non-empty set") {
        val a = ExtensionalSet(setOf(1, 2))
        val b = ExtensionalSet(setOf(3, 4))
        val c = ExtensionalSet(setOf(5, 6))
        val family = ExtensionalSet<MathSet<Int>>(setOf(a, b, c))

        val choices = ChoiceFunction(family).choose()
        choices.size shouldBe 3
        choices.forEach { (subset, value) ->
            (value in subset) shouldBe true
        }
    }

    test("finite sets with same size are equipotent") {
        val left = ExtensionalSet(setOf(1, 2, 3))
        val right = ExtensionalSet(setOf("x", "y", "z"))
        (left isEquipotentTo right) shouldBe true
        (left.findBijectionTo(right) != null) shouldBe true
    }
})
