package mathsets.logic

/**
 * DSL marker annotation that restricts implicit receivers inside formula-building
 * blocks, preventing accidental scope leaking between nested builders.
 */
@DslMarker
annotation class FormulaDsl

/**
 * Receiver scope for the formula-building DSL.
 *
 * Provides infix extension functions on [String] and [Formula] that allow
 * formulas to be constructed with a natural, mathematical-looking syntax:
 *
 * ```kotlin
 * forAll("x") { "x" memberOf "A" implies ("x" memberOf "B") }
 * ```
 *
 * @see forAll
 * @see exists
 */
@FormulaDsl
class FormulaScope {
    /**
     * Creates a [Formula.Membership] asserting that this variable is a member of [other].
     *
     * @param other The name of the set variable.
     * @return A membership formula `this ∈ other`.
     */
    infix fun String.memberOf(other: String): Formula =
        Formula.Membership(Term.Var(this), Term.Var(other))

    /**
     * Creates a [Formula.Equals] asserting that this variable equals [other].
     *
     * @param other The name of the variable to compare against.
     * @return An equality formula `this = other`.
     */
    infix fun String.eq(other: String): Formula =
        Formula.Equals(Term.Var(this), Term.Var(other))

    /**
     * Creates a [Formula.And] conjunction of this formula with [other].
     *
     * @param other The right conjunct.
     * @return The conjunction `this ∧ other`.
     */
    infix fun Formula.and(other: Formula): Formula = Formula.And(this, other)

    /**
     * Creates a [Formula.Or] disjunction of this formula with [other].
     *
     * @param other The right disjunct.
     * @return The disjunction `this ∨ other`.
     */
    infix fun Formula.or(other: Formula): Formula = Formula.Or(this, other)

    /**
     * Creates a [Formula.Implies] implication from this formula to [other].
     *
     * @param other The consequent formula.
     * @return The implication `this → other`.
     */
    infix fun Formula.implies(other: Formula): Formula = Formula.Implies(this, other)

    /**
     * Creates a [Formula.Iff] biconditional between this formula and [other].
     *
     * @param other The right-hand side formula.
     * @return The biconditional `this ↔ other`.
     */
    infix fun Formula.iff(other: Formula): Formula = Formula.Iff(this, other)

    /**
     * Creates a [Formula.Not] negation of the given formula.
     *
     * @param inner The formula to negate.
     * @return The negation `¬inner`.
     */
    fun not(inner: Formula): Formula = Formula.Not(inner)
}

/**
 * Constructs a universally quantified formula `∀variable(body)` using the
 * formula-building DSL.
 *
 * ```kotlin
 * val f = forAll("x") { "x" memberOf "A" }
 * // Formula.ForAll("x", Formula.Membership(Var("x"), Var("A")))
 * ```
 *
 * @param variable The name of the bound variable.
 * @param block A DSL block evaluated in a [FormulaScope] that produces the quantifier body.
 * @return A [Formula.ForAll] wrapping the body produced by [block].
 * @see exists
 */
fun forAll(variable: String, block: FormulaScope.() -> Formula): Formula =
    Formula.ForAll(variable, FormulaScope().block())

/**
 * Constructs an existentially quantified formula `∃variable(body)` using the
 * formula-building DSL.
 *
 * ```kotlin
 * val f = exists("E") { not("x" memberOf "E") }
 * // Formula.Exists("E", Formula.Not(Formula.Membership(Var("x"), Var("E"))))
 * ```
 *
 * @param variable The name of the bound variable.
 * @param block A DSL block evaluated in a [FormulaScope] that produces the quantifier body.
 * @return A [Formula.Exists] wrapping the body produced by [block].
 * @see forAll
 */
fun exists(variable: String, block: FormulaScope.() -> Formula): Formula =
    Formula.Exists(variable, FormulaScope().block())
