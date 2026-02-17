package mathsets.logic

object FormulaPrettyPrinter {
    fun print(formula: Formula): String = when (formula) {
        is Formula.Membership -> "${printTerm(formula.element)} ∈ ${printTerm(formula.set)}"
        is Formula.Equals -> "${printTerm(formula.left)} = ${printTerm(formula.right)}"
        is Formula.Not -> "¬(${print(formula.inner)})"
        is Formula.And -> "${wrap(formula.left)} ∧ ${wrap(formula.right)}"
        is Formula.Or -> "${wrap(formula.left)} ∨ ${wrap(formula.right)}"
        is Formula.Implies -> "${wrap(formula.premise)} → ${wrap(formula.conclusion)}"
        is Formula.Iff -> "${wrap(formula.left)} ↔ ${wrap(formula.right)}"
        is Formula.ForAll -> "∀${formula.variable}(${print(formula.body)})"
        is Formula.Exists -> "∃${formula.variable}(${print(formula.body)})"
    }

    private fun wrap(formula: Formula): String = when (formula) {
        is Formula.Membership, is Formula.Equals -> print(formula)
        else -> "(${print(formula)})"
    }

    private fun printTerm(term: Term): String = when (term) {
        is Term.Var -> term.name
        is Term.Const -> term.value
        is Term.App -> "${term.function}(${term.args.joinToString(",") { printTerm(it) }})"
    }
}

