package mathsets.logic

/**
 * Converts a [Formula] AST back into a human-readable Unicode string.
 *
 * The output uses standard mathematical notation with Unicode symbols:
 * `∈`, `=`, `¬`, `∧`, `∨`, `→`, `↔`, `∀`, `∃`.
 *
 * Parentheses are inserted around sub-formulas of connectives to preserve
 * readability, while atomic formulas (membership and equality) are printed
 * without surrounding parentheses.
 *
 * Usage:
 * ```kotlin
 * val text = FormulaPrettyPrinter.print(formula)
 * // "∀x(x ∈ A → x ∈ B)"
 * ```
 *
 * The output is compatible with [FormulaParser.parse], so
 * `FormulaParser.parse(FormulaPrettyPrinter.print(f))` round-trips correctly
 * for well-formed formulas.
 *
 * @see Formula
 * @see FormulaParser
 */
object FormulaPrettyPrinter {
    /**
     * Renders the given [formula] as a Unicode string in standard mathematical notation.
     *
     * @param formula The formula AST to render.
     * @return A human-readable Unicode string representation of the formula.
     */
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
