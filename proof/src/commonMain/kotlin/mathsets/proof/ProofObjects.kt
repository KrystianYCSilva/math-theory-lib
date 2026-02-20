package mathsets.proof

import mathsets.logic.Formula

/**
 * Proof object representing a derivation.
 */
sealed interface Proof {
    /**
     * Conclusion of this proof step.
     */
    val conclusion: Formula
    
    /**
     * Axiom instance.
     */
    data class Axiom(override val conclusion: Formula) : Proof
    
    /**
     * Assumption (dischargeable).
     */
    data class Assumption(override val conclusion: Formula) : Proof
    
    /**
     * Modus Ponens: from P and P → Q, derive Q.
     */
    data class ModusPonens(
        override val conclusion: Formula,
        val premise: Proof,
        val implication: Proof
    ) : Proof
    
    /**
     * Universal Generalization: from φ(x), derive ∀x.φ(x).
     */
    data class UniversalGeneralization(
        override val conclusion: Formula,
        val premise: Proof,
        val variable: String
    ) : Proof
    
    /**
     * Contradiction: from P and ¬P, derive ⊥.
     */
    data class Contradiction(
        override val conclusion: Formula,
        val left: Proof,
        val right: Proof
    ) : Proof
}

/**
 * Result of proof verification.
 */
sealed interface VerificationResult {
    object Valid : VerificationResult
    data class Invalid(val reason: String) : VerificationResult
}

/**
 * Proof checker that verifies derivations.
 */
class ProofChecker {
    
    /**
     * Verifies a proof object.
     */
    fun verify(proof: Proof, axioms: Set<Formula>): VerificationResult {
        return when (proof) {
            is Proof.Axiom -> {
                if (proof.conclusion in axioms) VerificationResult.Valid
                else VerificationResult.Invalid("Not an axiom: ${proof.conclusion}")
            }
            is Proof.Assumption -> VerificationResult.Valid // Assumptions always valid
            is Proof.ModusPonens -> {
                val premiseResult = verify(proof.premise, axioms)
                if (premiseResult is VerificationResult.Invalid) return premiseResult
                
                val implResult = verify(proof.implication, axioms)
                if (implResult is VerificationResult.Invalid) return implResult
                
                // Check that implication has correct form
                val expectedImpl = Formula.Implies(proof.premise.conclusion, proof.conclusion)
                if (proof.implication.conclusion != expectedImpl) {
                    return VerificationResult.Invalid("Modus Ponens: wrong implication form")
                }
                
                VerificationResult.Valid
            }
            is Proof.UniversalGeneralization -> {
                val premiseResult = verify(proof.premise, axioms)
                if (premiseResult is VerificationResult.Invalid) return premiseResult
                
                // Check generalization is well-formed
                val expected = Formula.ForAll(proof.variable, proof.premise.conclusion)
                if (proof.conclusion != expected) {
                    return VerificationResult.Invalid("Universal Generalization: wrong form")
                }
                
                VerificationResult.Valid
            }
            is Proof.Contradiction -> {
                val leftResult = verify(proof.left, axioms)
                if (leftResult is VerificationResult.Invalid) return leftResult
                
                val rightResult = verify(proof.right, axioms)
                if (rightResult is VerificationResult.Invalid) return rightResult
                
                // Check that conclusions are contradictory
                if (!areContradictory(proof.left.conclusion, proof.right.conclusion)) {
                    return VerificationResult.Invalid("Not a contradiction")
                }
                
                VerificationResult.Valid
            }
        }
    }
    
    private fun areContradictory(left: Formula, right: Formula): Boolean {
        return when (left) {
            is Formula.Not -> left.inner == right
            else -> right is Formula.Not && right.inner == left
        }
    }
}

/**
 * Registry of proven theorems.
 */
class TheoremRegistry {
    private val theorems = mutableMapOf<String, Proof>()
    private val axioms = mutableSetOf<Formula>()
    private val checker = ProofChecker()
    
    /**
     * Adds an axiom.
     */
    fun addAxiom(formula: Formula) {
        axioms += formula
    }
    
    /**
     * Registers a proven theorem.
     */
    fun registerTheorem(name: String, proof: Proof): VerificationResult {
        val result = checker.verify(proof, axioms)
        if (result is VerificationResult.Valid) {
            theorems[name] = proof
        }
        return result
    }
    
    /**
     * Gets a theorem by name.
     */
    fun getTheorem(name: String): Proof? = theorems[name]
    
    /**
     * Lists all registered theorems.
     */
    fun listTheorems(): List<String> = theorems.keys.toList()
}
