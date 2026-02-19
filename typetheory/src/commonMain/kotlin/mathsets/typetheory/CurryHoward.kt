package mathsets.typetheory

/**
 * Minimal Curry-Howard helpers mapping proof patterns to types and terms.
 */
object CurryHoward {
    /**
     * Type for implication A -> B.
     *
     * @param premise Premise type A.
     * @param conclusion Conclusion type B.
     * @return Pi(A, B).
     */
    fun implicationType(premise: Type, conclusion: Type): Type = Type.Pi(premise, conclusion)

    /**
     * Type for conjunction A and B.
     *
     * @param left Left proposition type.
     * @param right Right proposition type.
     * @return Sigma(left, right).
     */
    fun conjunctionType(left: Type, right: Type): Type = Type.Sigma(left, right)

    /**
     * Canonical proof of A -> A.
     *
     * @param proposition Proposition type A.
     * @return Lambda term x:A. x.
     */
    fun identityProof(proposition: Type): Term =
        Term.Lambda("x", proposition, Term.Var("x"))

    /**
     * Canonical proof of A and B from proofs a:A and b:B.
     *
     * @param leftProof Proof of A.
     * @param rightProof Proof of B.
     * @return Pair(leftProof, rightProof).
     */
    fun conjunctionIntro(leftProof: Term, rightProof: Term): Term =
        Term.Pair(leftProof, rightProof)

    /**
     * Verifies that a term is a proof of a proposition type under a context.
     *
     * @param checker Type checker.
     * @param context Typing context.
     * @param proof Candidate proof term.
     * @param proposition Proposition type.
     * @return True when proof checks against proposition.
     */
    fun isProof(
        checker: TypeChecker,
        context: Context,
        proof: Term,
        proposition: Type
    ): Boolean = checker.check(context, proof, proposition)
}
