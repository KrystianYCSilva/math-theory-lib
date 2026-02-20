package mathsets.proof

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldNotBeNull
import mathsets.logic.Formula
import mathsets.logic.Term as LogicTerm

class ProofObjectsTest : FunSpec({
    test("proof checker validates modus ponens") {
        val p = Formula.Equals(LogicTerm.Const("c1"), LogicTerm.Const("c2"))
        val q = Formula.Equals(LogicTerm.Const("c3"), LogicTerm.Const("c4"))
        val impliesPQ = Formula.Implies(p, q)
        
        val proofP = Proof.Axiom(p)
        val proofImplies = Proof.Axiom(impliesPQ)
        val proofQ = Proof.ModusPonens(q, proofP, proofImplies)
        
        val checker = ProofChecker()
        val axioms = setOf(p, impliesPQ)
        
        val result = checker.verify(proofQ, axioms)
        result shouldBe VerificationResult.Valid
    }
    
    test("proof checker rejects invalid modus ponens") {
        val p = Formula.Equals(LogicTerm.Const("c1"), LogicTerm.Const("c2"))
        val q = Formula.Equals(LogicTerm.Const("c3"), LogicTerm.Const("c4"))
        val r = Formula.Equals(LogicTerm.Const("c5"), LogicTerm.Const("c6"))
        
        // Wrong implication: P → R instead of P → Q
        val wrongImplies = Formula.Implies(p, r)
        
        val proofP = Proof.Axiom(p)
        val proofWrong = Proof.Axiom(wrongImplies)
        val proofQ = Proof.ModusPonens(q, proofP, proofWrong)
        
        val checker = ProofChecker()
        val axioms = setOf(p, wrongImplies)
        
        val result = checker.verify(proofQ, axioms)
        (result is VerificationResult.Invalid) shouldBe true
    }
    
    test("theorem registry stores and retrieves theorems") {
        val registry = TheoremRegistry()
        val p = Formula.Equals(LogicTerm.Const("c1"), LogicTerm.Const("c1"))
        
        registry.addAxiom(p)
        val proof = Proof.Axiom(p)
        
        val result = registry.registerTheorem("test_theorem", proof)
        result shouldBe VerificationResult.Valid
        
        registry.getTheorem("test_theorem").shouldNotBeNull()
    }
})
