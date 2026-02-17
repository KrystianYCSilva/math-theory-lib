package mathsets.logic

object ModelChecker {
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

