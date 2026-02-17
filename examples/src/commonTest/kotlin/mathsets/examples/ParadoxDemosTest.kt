package mathsets.examples

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class ParadoxDemosTest : FunSpec({
    test("russell and burali-forti narratives provide contradiction steps") {
        ParadoxDemos.russellParadoxNarrative().shouldHaveSize(4)
        ParadoxDemos.buraliFortiNarrative().shouldHaveSize(4)
    }

    test("cantor paradox finite check holds for sampled cardinalities") {
        (0..8).all { n -> ParadoxDemos.cantorParadoxForFinite(n) } shouldBe true
    }
})

