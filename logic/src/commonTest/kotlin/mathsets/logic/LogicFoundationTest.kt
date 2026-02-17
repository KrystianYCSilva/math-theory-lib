package mathsets.logic

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class LogicFoundationTest : FunSpec({
    test("parse and pretty-print roundtrip") {
        val input = "∀x(x ∈ A → x ∈ B)"
        val parsed = FormulaParser.parse(input)
        FormulaPrettyPrinter.print(parsed) shouldBe input
    }

    test("DSL builds same AST as manual construction") {
        val manual = Formula.ForAll(
            "x",
            Formula.Implies(
                Formula.Membership(Term.Var("x"), Term.Var("A")),
                Formula.Membership(Term.Var("x"), Term.Var("B"))
            )
        )
        val built = forAll("x") {
            ("x" memberOf "A") implies ("x" memberOf "B")
        }
        built shouldBe manual
    }

    test("natural peano system checks injectivity and zero-not-successor") {
        NaturalPeanoSystem.verifyInjectivity(sampleSize = 1000) shouldBe true
        NaturalPeanoSystem.verifyZeroNotSuccessor(sampleSize = 1000) shouldBe true
    }

    test("model checker evaluates quantified membership formula") {
        val formula = FormulaParser.parse("∀x(x ∈ A → x ∈ B)")
        val interpretation = Interpretation(
            universe = setOf(1, 2, 3),
            membership = { element, set ->
                @Suppress("UNCHECKED_CAST")
                (set as? Set<Any?>)?.contains(element) == true
            },
            constants = mapOf(
                "A" to setOf(1, 2),
                "B" to setOf(1, 2, 3)
            )
        )
        ModelChecker.evaluate(formula, interpretation) shouldBe true
    }
})

