package mathsets.logic

sealed interface Formula {
    data class Membership(val element: Term, val set: Term) : Formula
    data class Equals(val left: Term, val right: Term) : Formula
    data class Not(val inner: Formula) : Formula
    data class And(val left: Formula, val right: Formula) : Formula
    data class Or(val left: Formula, val right: Formula) : Formula
    data class Implies(val premise: Formula, val conclusion: Formula) : Formula
    data class Iff(val left: Formula, val right: Formula) : Formula
    data class ForAll(val variable: String, val body: Formula) : Formula
    data class Exists(val variable: String, val body: Formula) : Formula
}

