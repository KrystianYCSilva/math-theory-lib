package mathsets.logic

/**
 * Evaluates [Formula] instances against a given [Interpretation], implementing
 * Tarski-style model-theoretic semantics for first-order logic.
 *
 * The checker recursively descends through the formula AST, resolving terms via
 * the interpretation's constants, functions, and variable assignments, and
 * evaluating logical connectives and quantifiers according to their standard
 * truth-table / domain-ranging semantics.
 *
 * Usage:
 * ```kotlin
 * val interp = Interpretation(
 *     universe = setOf(1, 2, 3),
 *     membership = { elem, set -> (set as? Set<*>)?.contains(elem) == true }
 * )
 * val result = ModelChecker.evaluate(formula, interp)
 * ```
 *
 * @see Interpretation
 * @see Formula
 */
object ModelChecker {
    /**
     * Evaluates whether the given [formula] is satisfied under the provided
     * [interpretation] and variable [assignment].
     *
     * Quantifiers (`∀`, `∃`) range over [Interpretation.universe]. Free variables
     * must be bound in [assignment] or defined as constants in the interpretation;
     * otherwise an [IllegalStateException] is thrown.
     *
     * @param formula The formula to evaluate.
     * @param interpretation The first-order interpretation providing the domain,
     *   membership relation, constants, and functions.
     * @param assignment A map from variable names to their current values in the
     *   domain. Defaults to an empty map (no free variables).
     * @return `true` if the formula is satisfied, `false` otherwise.
     * @throws IllegalStateException if a variable is unbound or a function symbol
     *   is not defined in the interpretation.
     */
    fun evaluate(
        formula: Formula,
        interpretation: Interpretation,
        assignment: Map<String, Any?> = emptyMap()
    ): Boolean = when (formula) {
        is Formula.Membership -> interpretation.membership(
            evaluateTerm(formula.element, interpretation, assignment),
            evaluateTerm(formula.set, interpretation, assignment)
        )
        is Formula.Equals -> evaluateTerm(formula.left, interpretation, assignment) ==
            evaluateTerm(formula.right, interpretation, assignment)
        is Formula.Not -> !evaluate(formula.inner, interpretation, assignment)
        is Formula.And -> evaluate(formula.left, interpretation, assignment) &&
            evaluate(formula.right, interpretation, assignment)
        is Formula.Or -> evaluate(formula.left, interpretation, assignment) ||
            evaluate(formula.right, interpretation, assignment)
        is Formula.Implies -> !evaluate(formula.premise, interpretation, assignment) ||
            evaluate(formula.conclusion, interpretation, assignment)
        is Formula.Iff -> evaluate(formula.left, interpretation, assignment) ==
            evaluate(formula.right, interpretation, assignment)
        is Formula.ForAll -> interpretation.universe.all { value ->
            evaluate(formula.body, interpretation, assignment + (formula.variable to value))
        }
        is Formula.Exists -> interpretation.universe.any { value ->
            evaluate(formula.body, interpretation, assignment + (formula.variable to value))
        }
    }

    private fun evaluateTerm(
        term: Term,
        interpretation: Interpretation,
        assignment: Map<String, Any?>
    ): Any? = when (term) {
        is Term.Var -> assignment[term.name]
            ?: interpretation.constants[term.name]
            ?: error("Unbound variable '${term.name}'")
        is Term.Const -> interpretation.constants[term.value] ?: term.value
        is Term.App -> {
            val function = interpretation.functions[term.function]
                ?: error("Unknown function '${term.function}'")
            function(term.args.map { evaluateTerm(it, interpretation, assignment) })
        }
    }
}
