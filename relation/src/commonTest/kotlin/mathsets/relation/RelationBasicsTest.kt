package mathsets.relation

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import mathsets.set.ExtensionalSet

class RelationBasicsTest : FunSpec({
    test("OrderedPair equality and kuratowski") {
        OrderedPair(1,2) shouldBe OrderedPair(1,2)
        (OrderedPair(1,2) != OrderedPair(2,1)) shouldBe true
    }

    test("inverse and compose") {
        val pairs = setOf(OrderedPair(1,2), OrderedPair(2,3))
        val r = Relation(ExtensionalSet(pairs))
        val inv = r.inverse()
        inv.graph.elements().toSet() shouldBe setOf(OrderedPair(2,1), OrderedPair(3,2))
        val r2 = r.compose(r)
        r2.graph.elements().toSet() shouldBe setOf(OrderedPair(1,3))
    }

})
