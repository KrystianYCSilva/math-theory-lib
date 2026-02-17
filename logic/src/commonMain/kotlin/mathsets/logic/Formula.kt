package mathsets.logic

/**
 * A sealed hierarchy representing formulas in first-order logic with set membership.
 *
 * This AST covers the full syntax of first-order logic over the language of set
 * theory: atomic predicates (membership `∈`, equality `=`), propositional
 * connectives (¬, ∧, ∨, →, ↔), and quantifiers (∀, ∃).
 *
 * Formulas are immutable data classes, enabling safe structural recursion,
 * pattern matching, and equality comparisons.
 *
 * @see Term
 * @see FormulaBuilder
 * @see FormulaPrettyPrinter
 */
sealed interface Formula {
    /**
     * Atomic membership predicate: `element ∈ set`.
     *
     * @property element The term on the left-hand side of `∈`.
     * @property set The term on the right-hand side of `∈`.
     */
    data class Membership(val element: Term, val set: Term) : Formula

    /**
     * Atomic equality predicate: `left = right`.
     *
     * @property left The left-hand side term.
     * @property right The right-hand side term.
     */
    data class Equals(val left: Term, val right: Term) : Formula

    /**
     * Logical negation: `¬inner`.
     *
     * @property inner The formula being negated.
     */
    data class Not(val inner: Formula) : Formula

    /**
     * Logical conjunction: `left ∧ right`.
     *
     * @property left The left conjunct.
     * @property right The right conjunct.
     */
    data class And(val left: Formula, val right: Formula) : Formula

    /**
     * Logical disjunction: `left ∨ right`.
     *
     * @property left The left disjunct.
     * @property right The right disjunct.
     */
    data class Or(val left: Formula, val right: Formula) : Formula

    /**
     * Material implication: `premise → conclusion`.
     *
     * @property premise The antecedent formula.
     * @property conclusion The consequent formula.
     */
    data class Implies(val premise: Formula, val conclusion: Formula) : Formula

    /**
     * Biconditional (if and only if): `left ↔ right`.
     *
     * @property left The left-hand side formula.
     * @property right The right-hand side formula.
     */
    data class Iff(val left: Formula, val right: Formula) : Formula

    /**
     * Universal quantifier: `∀variable(body)`.
     *
     * @property variable The name of the bound variable.
     * @property body The formula in the scope of the quantifier.
     */
    data class ForAll(val variable: String, val body: Formula) : Formula

    /**
     * Existential quantifier: `∃variable(body)`.
     *
     * @property variable The name of the bound variable.
     * @property body The formula in the scope of the quantifier.
     */
    data class Exists(val variable: String, val body: Formula) : Formula
}
