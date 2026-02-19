package mathsets.computability

/**
 * Untyped lambda-calculus terms.
 */
sealed interface LambdaTerm {
    /**
     * Variable.
     *
     * @property name Variable identifier.
     */
    data class Var(val name: String) : LambdaTerm

    /**
     * Abstraction.
     *
     * @property parameter Bound variable.
     * @property body Function body.
     */
    data class Abs(val parameter: String, val body: LambdaTerm) : LambdaTerm

    /**
     * Application.
     *
     * @property function Function term.
     * @property argument Argument term.
     */
    data class App(val function: LambdaTerm, val argument: LambdaTerm) : LambdaTerm
}

/**
 * Lambda-calculus reduction engines and helpers.
 */
object LambdaCalculus {
    /**
     * Reduces a term using normal-order beta reduction.
     *
     * @param term Input term.
     * @param maxSteps Reduction step budget.
     * @return Reduced term (or best effort if budget is exhausted).
     */
    fun normalOrder(term: LambdaTerm, maxSteps: Int = 1_000): LambdaTerm {
        var current = term
        var steps = 0
        while (steps < maxSteps) {
            val next = stepNormal(current) ?: break
            current = next
            steps++
        }
        return current
    }

    /**
     * Reduces a term using applicative-order beta reduction.
     *
     * @param term Input term.
     * @param maxSteps Reduction step budget.
     * @return Reduced term (or best effort if budget is exhausted).
     */
    fun applicativeOrder(term: LambdaTerm, maxSteps: Int = 1_000): LambdaTerm {
        var current = term
        var steps = 0
        while (steps < maxSteps) {
            val next = stepApplicative(current) ?: break
            current = next
            steps++
        }
        return current
    }

    /**
     * Builds Church numeral n.
     *
     * @param n Non-negative integer.
     * @return Church numeral lambda term.
     */
    fun churchNumeral(n: Int): LambdaTerm {
        require(n >= 0) { "Church numeral requires n >= 0." }
        var body: LambdaTerm = LambdaTerm.Var("x")
        repeat(n) {
            body = LambdaTerm.App(LambdaTerm.Var("f"), body)
        }
        return LambdaTerm.Abs("f", LambdaTerm.Abs("x", body))
    }

    /**
     * Decodes a normalized Church numeral into Int when possible.
     *
     * @param term Candidate Church numeral.
     * @return Decoded integer, or null if term is not in numeral shape.
     */
    fun toInt(term: LambdaTerm): Int? {
        val normalized = normalOrder(term)
        if (normalized !is LambdaTerm.Abs) return null
        if (normalized.body !is LambdaTerm.Abs) return null

        val fName = normalized.parameter
        val xName = normalized.body.parameter

        var count = 0
        var cursor: LambdaTerm = normalized.body.body
        while (cursor is LambdaTerm.App && cursor.function == LambdaTerm.Var(fName)) {
            count++
            cursor = cursor.argument
        }
        return if (cursor == LambdaTerm.Var(xName)) count else null
    }

    /**
     * Performs substitution [variable := replacement] in [term].
     *
     * @param term Term where substitution occurs.
     * @param variable Variable to replace.
     * @param replacement Replacement term.
     * @return Substituted term.
     */
    fun substitute(term: LambdaTerm, variable: String, replacement: LambdaTerm): LambdaTerm = when (term) {
        is LambdaTerm.Var -> if (term.name == variable) replacement else term
        is LambdaTerm.Abs -> {
            if (term.parameter == variable) term
            else LambdaTerm.Abs(term.parameter, substitute(term.body, variable, replacement))
        }
        is LambdaTerm.App -> LambdaTerm.App(
            substitute(term.function, variable, replacement),
            substitute(term.argument, variable, replacement)
        )
    }

    private fun stepNormal(term: LambdaTerm): LambdaTerm? = when (term) {
        is LambdaTerm.Var -> null
        is LambdaTerm.Abs -> stepNormal(term.body)?.let { LambdaTerm.Abs(term.parameter, it) }
        is LambdaTerm.App -> {
            if (term.function is LambdaTerm.Abs) {
                substitute(term.function.body, term.function.parameter, term.argument)
            } else {
                stepNormal(term.function)?.let { LambdaTerm.App(it, term.argument) }
                    ?: stepNormal(term.argument)?.let { LambdaTerm.App(term.function, it) }
            }
        }
    }

    private fun stepApplicative(term: LambdaTerm): LambdaTerm? = when (term) {
        is LambdaTerm.Var -> null
        is LambdaTerm.Abs -> stepApplicative(term.body)?.let { LambdaTerm.Abs(term.parameter, it) }
        is LambdaTerm.App -> {
            stepApplicative(term.function)?.let { LambdaTerm.App(it, term.argument) }
                ?: stepApplicative(term.argument)?.let { LambdaTerm.App(term.function, it) }
                ?: if (term.function is LambdaTerm.Abs) {
                    substitute(term.function.body, term.function.parameter, term.argument)
                } else {
                    null
                }
        }
    }
}

/**
 * Expository statement about the halting problem.
 */
object HaltingProblem {
    /**
     * Returns a concise diagonalization statement of undecidability.
     *
     * @return Human-readable undecidability statement.
     */
    fun diagonalArgumentStatement(): String =
        "Nao existe algoritmo geral que decida, para toda maquina e entrada, se a computacao vai parar."
}
