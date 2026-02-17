package mathsets.set

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ZFCVerifierTest : FunSpec({
    test("verifier reports expected axioms on closed finite model") {
        val universe = ExtensionalSet(setOf(1, 2))
        val empty = MathSet.empty<Int>()
        val one = mathSetOf(1)
        val two = mathSetOf(2)
        val both = mathSetOf(1, 2)
        val sets = ExtensionalSet<MathSet<Int>>(setOf(empty, one, two, both))
        val model = FiniteModel(universe, sets)

        val report = ZFCVerifier.verify(model)
        report.isSatisfied("Extensionality") shouldBe true
        report.isSatisfied("EmptySet") shouldBe true
        report.isSatisfied("Pairing") shouldBe true
        report.isSatisfied("Union") shouldBe true
        report.isSatisfied("PowerSet") shouldBe true
        report.isSatisfied("Infinity") shouldBe false
    }

    test("verifier detects missing empty set") {
        val universe = ExtensionalSet(setOf(1))
        val sets = ExtensionalSet<MathSet<Int>>(setOf(mathSetOf(1)))
        val model = FiniteModel(universe, sets)
        val report = ZFCVerifier.verify(model)
        report.isSatisfied("EmptySet") shouldBe false
    }

    test("cantor demo shows power set larger than base set") {
        val result = Paradoxes.cantor(ExtensionalSet(setOf(1, 2, 3)))
        result.artifact shouldBe 8
        result.contradictionDetected shouldBe false
    }
})

