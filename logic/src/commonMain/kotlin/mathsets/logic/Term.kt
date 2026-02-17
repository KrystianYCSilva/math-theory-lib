package mathsets.logic

/**
 * A sealed hierarchy representing terms in first-order logic.
 *
 * Terms are the basic building blocks of formulas. They denote objects in the
 * domain of discourse and can be variables, constants, or function applications.
 *
 * @see Formula
 */
sealed interface Term {
    /**
     * A variable term, representing a free or bound variable in a formula.
     *
     * @property name The identifier of the variable (e.g., `"x"`, `"A"`).
     */
    data class Var(val name: String) : Term

    /**
     * A constant term, representing a fixed element in the domain of discourse.
     *
     * @property value The literal value or name of the constant (e.g., `"0"`, `"∅"`).
     */
    data class Const(val value: String) : Term

    /**
     * A function application term, representing the result of applying a named
     * function symbol to a list of argument terms.
     *
     * For example, `App("succ", listOf(Var("x")))` represents `succ(x)`.
     *
     * @property function The name of the function symbol being applied.
     * @property args The list of argument terms passed to the function.
     */
    data class App(val function: String, val args: List<Term>) : Term
}
