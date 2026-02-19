package mathsets.typetheory

/**
 * Evaluator for the minimal MLTT term language.
 */
object Evaluator {
    /**
     * Normalizes a term by repeated small-step reduction.
     *
     * @param term Term to normalize.
     * @param maxSteps Reduction step budget.
     * @return Normal form (or best effort if budget is exhausted).
     */
    fun normalize(term: Term, maxSteps: Int = 1_000): Term {
        var current = term
        var steps = 0
        while (steps < maxSteps) {
            val next = step(current) ?: break
            current = next
            steps++
        }
        return current
    }

    /**
     * Executes one reduction step when possible.
     *
     * @param term Input term.
     * @return Reduced term or null when no reduction applies.
     */
    fun step(term: Term): Term? = when (term) {
        is Term.App -> reduceApplication(term)
        is Term.Proj1 -> reduceProj1(term)
        is Term.Proj2 -> reduceProj2(term)
        is Term.Succ -> step(term.predecessor)?.let { Term.Succ(it) }
        is Term.NatRec -> reduceNatRec(term)
        is Term.Lambda,
        is Term.Var,
        Term.Zero,
        is Term.Pair,
        is Term.Refl,
        Term.UnitValue,
        Term.True,
        Term.False -> null
    }

    /**
     * Performs capture-avoiding substitution for this minimal syntax.
     *
     * @param term Term where substitution occurs.
     * @param variable Variable name to replace.
     * @param replacement Replacement term.
     * @return Substituted term.
     */
    fun substitute(term: Term, variable: String, replacement: Term): Term = when (term) {
        is Term.Var -> if (term.name == variable) replacement else term
        is Term.Lambda -> {
            if (term.parameter == variable) term
            else Term.Lambda(term.parameter, term.parameterType, substitute(term.body, variable, replacement))
        }
        is Term.App -> Term.App(
            substitute(term.function, variable, replacement),
            substitute(term.argument, variable, replacement)
        )
        is Term.Pair -> Term.Pair(
            substitute(term.first, variable, replacement),
            substitute(term.second, variable, replacement)
        )
        is Term.Proj1 -> Term.Proj1(substitute(term.value, variable, replacement))
        is Term.Proj2 -> Term.Proj2(substitute(term.value, variable, replacement))
        is Term.Refl -> Term.Refl(substitute(term.value, variable, replacement))
        is Term.Succ -> Term.Succ(substitute(term.predecessor, variable, replacement))
        is Term.NatRec -> Term.NatRec(
            substitute(term.target, variable, replacement),
            substitute(term.zeroCase, variable, replacement),
            substitute(term.succCase, variable, replacement)
        )
        Term.Zero,
        Term.UnitValue,
        Term.True,
        Term.False -> term
    }

    private fun reduceApplication(term: Term.App): Term? {
        val functionStep = step(term.function)
        if (functionStep != null) return Term.App(functionStep, term.argument)

        val argumentStep = step(term.argument)
        if (argumentStep != null) return Term.App(term.function, argumentStep)

        if (term.function is Term.Lambda) {
            return substitute(term.function.body, term.function.parameter, term.argument)
        }
        return null
    }

    private fun reduceProj1(term: Term.Proj1): Term? {
        val innerStep = step(term.value)
        if (innerStep != null) return Term.Proj1(innerStep)
        return if (term.value is Term.Pair) term.value.first else null
    }

    private fun reduceProj2(term: Term.Proj2): Term? {
        val innerStep = step(term.value)
        if (innerStep != null) return Term.Proj2(innerStep)
        return if (term.value is Term.Pair) term.value.second else null
    }

    private fun reduceNatRec(term: Term.NatRec): Term? {
        val targetStep = step(term.target)
        if (targetStep != null) {
            return Term.NatRec(targetStep, term.zeroCase, term.succCase)
        }

        return when (val target = term.target) {
            Term.Zero -> term.zeroCase
            is Term.Succ -> {
                val recursiveCall = Term.NatRec(target.predecessor, term.zeroCase, term.succCase)
                Term.App(Term.App(term.succCase, target.predecessor), recursiveCall)
            }
            else -> null
        }
    }
}
