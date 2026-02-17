package mathsets.relation

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import mathsets.set.ExtensionalSet

class RelationBasicsTest : FunSpec({
    test("OrderedPair preserves order") {
        OrderedPair(1, 2) shouldBe OrderedPair(1, 2)
        (OrderedPair(1, 2) != OrderedPair(2, 1)) shouldBe true
    }

    test("inverse and composition work") {
        val r = Relation(ExtensionalSet(setOf(OrderedPair(1, 2), OrderedPair(2, 3))))
        val inverse = r.inverse()
        inverse.graph.elements().toSet() shouldBe setOf(OrderedPair(2, 1), OrderedPair(3, 2))

        val composed = r.compose(r)
        composed.graph.elements().toSet() shouldBe setOf(OrderedPair(1, 3))
    }

    test("relation properties for <= on {1,2,3}") {
        val universe = ExtensionalSet(setOf(1, 2, 3))
        val relation = relationOf(universe) { a, b -> a <= b }
        val props = RelationProperties(relation, universe)
        props.isReflexive() shouldBe true
        props.isTransitive() shouldBe true
        props.isAntisymmetric() shouldBe true
    }

    test("equality relation is reflexive, symmetric, transitive") {
        val universe = ExtensionalSet(setOf(1, 2, 3))
        val relation = relationOf(universe) { a, b -> a == b }
        val props = RelationProperties(relation, universe)
        props.isReflexive() shouldBe true
        props.isSymmetric() shouldBe true
        props.isTransitive() shouldBe true
    }

    test("partition to equivalence and back preserves parity classes") {
        val universe = ExtensionalSet(setOf(1, 2, 3, 4, 5, 6))
        val odd = ExtensionalSet(setOf(1, 3, 5))
        val even = ExtensionalSet(setOf(2, 4, 6))
        val partition = Partition(ExtensionalSet(setOf(odd, even)), universe)
        val roundTrip = partition.toEquivalenceRelation().toPartition()

        val actualParts = roundTrip.parts.elements().map { it.materialize() }.toSet()
        actualParts shouldContainExactlyInAnyOrder setOf(odd, even)
    }

    test("divisibility partial order has correct minimals and maximals") {
        val universe = ExtensionalSet(setOf(1, 2, 3, 4, 6, 12))
        val relation = relationOf(universe) { a, b -> b % a == 0 }
        val order = PartialOrder(universe, relation)

        order.minimals().elements().toSet() shouldBe setOf(1)
        order.maximals().elements().toSet() shouldBe setOf(12)
    }
})

