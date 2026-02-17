package mathsets.descriptive

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import mathsets.set.ExtensionalSet
import mathsets.set.MathSet

class FiniteTopologyTest : FunSpec({
    test("discrete topology computes interior closure and boundary") {
        val universe = ExtensionalSet(setOf(1, 2))
        val empty = MathSet.empty<Int>().materialize()
        val one = ExtensionalSet(setOf(1))
        val two = ExtensionalSet(setOf(2))
        val opens = ExtensionalSet<MathSet<Int>>(setOf(empty, one, two, universe))

        val topology = FiniteTopology(universe, opens)
        topology.interior(one).materialize() shouldBe one
        topology.closure(one).materialize() shouldBe one
        topology.boundary(one).materialize() shouldBe empty
    }

    test("indiscrete topology behaves as expected") {
        val universe = ExtensionalSet(setOf(1, 2))
        val empty = MathSet.empty<Int>().materialize()
        val opens = ExtensionalSet<MathSet<Int>>(setOf(empty, universe))
        val subset = ExtensionalSet(setOf(1))

        val topology = FiniteTopology(universe, opens)
        topology.interior(subset).materialize() shouldBe empty
        topology.closure(subset).materialize() shouldBe universe
        topology.boundary(subset).materialize() shouldBe universe
    }
})

