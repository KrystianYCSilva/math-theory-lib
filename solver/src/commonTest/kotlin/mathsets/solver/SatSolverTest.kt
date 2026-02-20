package mathsets.solver

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldNotBeNull

class SatSolverTest : FunSpec({
    test("sat solver finds satisfying assignment for simple formula") {
        // (A ∨ B) ∧ (¬A ∨ C)
        val cnf = CnfFormula(listOf(
            listOf(Literals.Pos("A"), Literals.Pos("B")),
            listOf(Literals.Neg("A"), Literals.Pos("C"))
        ))
        
        val result = SatSolver.solve(cnf)
        result.shouldNotBeNull()
    }
    
    test("sat solver detects unsatisfiable formula") {
        // A ∧ ¬A
        val cnf = CnfFormula(listOf(
            listOf(Literals.Pos("A")),
            listOf(Literals.Neg("A"))
        ))
        
        val result = SatSolver.solve(cnf)
        result shouldBe null
    }
    
    test("sat solver handles tautology") {
        val cnf = CnfFormula.tautology()
        val result = SatSolver.solve(cnf)
        result.shouldNotBeNull()
    }
})
