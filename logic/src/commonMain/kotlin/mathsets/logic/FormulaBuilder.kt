package mathsets.logic

@DslMarker
annotation class FormulaDsl

@FormulaDsl
class FormulaScope {
    infix fun String.memberOf(other: String): Formula =
        Formula.Membership(Term.Var(this), Term.Var(other))

    infix fun String.eq(other: String): Formula =
        Formula.Equals(Term.Var(this), Term.Var(other))

    infix fun Formula.and(other: Formula): Formula = Formula.And(this, other)
    infix fun Formula.or(other: Formula): Formula = Formula.Or(this, other)
    infix fun Formula.implies(other: Formula): Formula = Formula.Implies(this, other)
    infix fun Formula.iff(other: Formula): Formula = Formula.Iff(this, other)
    fun not(inner: Formula): Formula = Formula.Not(inner)
}

fun forAll(variable: String, block: FormulaScope.() -> Formula): Formula =
    Formula.ForAll(variable, FormulaScope().block())

fun exists(variable: String, block: FormulaScope.() -> Formula): Formula =
    Formula.Exists(variable, FormulaScope().block())

