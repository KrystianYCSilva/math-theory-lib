package mathsets.ordinal

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TransfiniteRecursionTest : FunSpec({
    test("recursion on finite ordinal behaves like primitive recursion") {
        val five = Ordinal.finite(5)
        val result = TransfiniteRecursion.transfiniteRecursion(
            ordinal = five,
            base = 0,
            successorCase = { _, previous -> previous + 1 },
            limitCase = { _, _ -> error("Finite recursion must not call limit case.") }
        )

        result shouldBe 5
    }

    test("limit case receives approximants for omega") {
        val result = TransfiniteRecursion.transfiniteRecursion(
            ordinal = Ordinal.OMEGA,
            base = 0,
            successorCase = { _, previous -> previous + 1 },
            limitCase = { _, approximants -> approximants.take(6).last() }
        )

        result shouldBe 5
    }

    test("omega is limit and omega plus finite has predecessor") {
        val omega = Ordinal.OMEGA
        omega.predOrNull() shouldBe null

        val omegaPlusThree = omega + Ordinal.finite(3)
        (omegaPlusThree.predOrNull() != null) shouldBe true
    }
})

