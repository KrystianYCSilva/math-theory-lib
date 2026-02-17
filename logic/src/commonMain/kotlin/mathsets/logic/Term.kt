package mathsets.logic

sealed interface Term {
    data class Var(val name: String) : Term
    data class Const(val value: String) : Term
    data class App(val function: String, val args: List<Term>) : Term
}

