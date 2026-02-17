package mathsets.forcing

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PosetTest : FunSpec({
    test("poset computes density antichains and filters") {
        val poset = Poset(setOf(0, 1, 2)) { a, b -> a <= b }

        poset.isDense(setOf(0, 1)) shouldBe true
        poset.isAntichain(setOf(1, 2)) shouldBe false
        poset.isAntichain(setOf(1)) shouldBe true
        poset.filters().size shouldBe 3
    }
})

