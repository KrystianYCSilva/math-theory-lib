package mathsets.forcing

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class GenericFilterTest : FunSpec({
    test("builds a generic filter meeting all dense families") {
        val poset = Poset(setOf(0, 1, 2)) { a, b -> a <= b }
        val denseFamilies = setOf(setOf(0, 1), setOf(0, 2))

        val generic = GenericFilterBuilder.build(poset, denseFamilies)
        generic.shouldNotBeNull()

        GenericFilterBuilder.isGeneric(poset, generic, denseFamilies) shouldBe true
    }
})

