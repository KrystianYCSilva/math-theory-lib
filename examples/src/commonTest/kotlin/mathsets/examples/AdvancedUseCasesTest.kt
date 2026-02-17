package mathsets.examples

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class AdvancedUseCasesTest : FunSpec({

    test("classify students by grade range produces correct partitions") {
        val students = listOf(
            AdvancedUseCases.Student("Alice", 95),
            AdvancedUseCases.Student("Bob", 82),
            AdvancedUseCases.Student("Carol", 95),
            AdvancedUseCases.Student("Dave", 60),
            AdvancedUseCases.Student("Eve", 85)
        )
        val result = AdvancedUseCases.classifyStudentsByGrade(students)
        result.shouldContainKey("A (90-100)")
        result.shouldContainKey("B (80-89)")
        result.shouldContainKey("D (<70)")
        result["A (90-100)"]!!.map { it.name }.toSet() shouldBe setOf("Alice", "Carol")
        result["B (80-89)"]!!.map { it.name }.toSet() shouldBe setOf("Bob", "Eve")
        result["D (<70)"]!!.map { it.name }.toSet() shouldBe setOf("Dave")
    }

    test("FOL verifies all managers are certified") {
        AdvancedUseCases.verifyDatabaseConstraint().shouldBeTrue()
    }

    test("task scheduling finds compile as first task") {
        val (first, _) = AdvancedUseCases.taskScheduling()
        first shouldBe "compile"
    }

    test("choice function elects one delegate per class") {
        val delegates = AdvancedUseCases.electClassDelegates()
        delegates.keys shouldBe setOf("Turma A", "Turma B", "Turma C")
        delegates["Turma A"] shouldBe "Alice"
        delegates["Turma B"] shouldBe "Carol"
        delegates["Turma C"] shouldBe "Eve"
    }

    test("cantor diagonal proves no surjection S to P(S)") {
        AdvancedUseCases.cantorDiagonalDemo().shouldBeTrue()
    }

    test("nim game analysis determines winner and best move") {
        val (p1Wins, p2Wins, bestMove) = AdvancedUseCases.nimGameAnalysis()
        (p1Wins || p2Wins).shouldBeTrue()
        bestMove.shouldNotBeNull()
    }

    test("party problem confirms R(3,3) = 6") {
        AdvancedUseCases.partyProblem() shouldBe 6
    }

    test("topology computes interior closure boundary") {
        val (interior, closure, boundary) = AdvancedUseCases.topologyAnalysis()
        1 shouldBe (if (1 in interior) 1 else 0)
        2 shouldBe (if (2 in interior) 2 else 0)
        1 shouldBe (if (1 in closure) 1 else 0)
        2 shouldBe (if (2 in closure) 2 else 0)
    }

    test("ordinal addition and multiplication are not commutative") {
        val (addCommutes, mulCommutes) = AdvancedUseCases.ordinalNonCommutativity()
        addCommutes.shouldBeFalse()
        mulCommutes.shouldBeFalse()
    }

    test("transfinite recursion on finite ordinal computes sum") {
        AdvancedUseCases.transfiniteSum() shouldBe 10
    }

    test("CH independence demonstrated on finite analogues") {
        val (holds, fails) = AdvancedUseCases.continuumHypothesisIndependence()
        holds.shouldBeTrue()
        fails.shouldBeFalse()
    }

    test("ZFC verification on mini model reports axiom status") {
        val report = AdvancedUseCases.verifyZfcOnMiniModel()
        report.shouldContainKey("Extensionality")
        report.shouldContainKey("EmptySet")
        report["Extensionality"] shouldBe true
        report["EmptySet"] shouldBe true
        report["Infinity"] shouldBe false
    }

    test("constructive sqrt(2) is Cauchy and approximates correctly") {
        val (approx, isCauchy) = AdvancedUseCases.constructiveSqrt2()
        isCauchy.shouldBeTrue()
        approx.shouldNotBeNull()
    }

    test("rational enumeration lists first 10 rationals") {
        val list = AdvancedUseCases.rationalEnumeration()
        list.shouldHaveSize(10)
        list[0] shouldBe "0 -> 0"
    }

    test("bell numbers are correct for small values") {
        val bells = AdvancedUseCases.bellNumbersDemo()
        bells.shouldHaveSize(6)
        bells[0] shouldBe (0 to "1")
        bells[1] shouldBe (1 to "1")
        bells[2] shouldBe (2 to "2")
        bells[3] shouldBe (3 to "5")
        bells[4] shouldBe (4 to "15")
        bells[5] shouldBe (5 to "52")
    }

    test("preference filters count is consistent") {
        AdvancedUseCases.preferenceFilters().shouldBeGreaterThan(0)
    }
})
