package mathsets.typetheory

/**
 * Bidirectional type checker for a minimal MLTT subset.
 */
class TypeChecker {
    /**
     * Infers the type of a term.
     *
     * @param context Typing context.
     * @param term Term to infer.
     * @return Inferred type when successful, or null when inference fails.
     */
    fun infer(context: Context, term: Term): Type? {
        return when (term) {
            is Term.Var -> context.lookup(term.name)
            is Term.Lambda -> {
                val bodyType = infer(context.withBinding(term.parameter, term.parameterType), term.body) ?: return null
                Type.Pi(term.parameterType, bodyType)
            }
            is Term.App -> {
                val fnType = infer(context, term.function) as? Type.Pi ?: return null
                if (!check(context, term.argument, fnType.domain)) return null
                fnType.codomain
            }
            is Term.Pair -> {
                val left = infer(context, term.first) ?: return null
                val right = infer(context, term.second) ?: return null
                Type.Sigma(left, right)
            }
            is Term.Proj1 -> {
                val pairType = infer(context, term.value) as? Type.Sigma ?: return null
                pairType.base
            }
            is Term.Proj2 -> {
                val pairType = infer(context, term.value) as? Type.Sigma ?: return null
                pairType.fiber
            }
            is Term.Refl -> {
                val t = infer(context, term.value) ?: return null
                Type.Id(t, term.value, term.value)
            }
            Term.Zero -> Type.Nat
            is Term.Succ -> if (check(context, term.predecessor, Type.Nat)) Type.Nat else null
            is Term.NatRec -> inferNatRec(context, term)
            Term.UnitValue -> Type.Unit
            Term.True,
            Term.False -> Type.Bool
        }
    }

    /**
     * Checks whether a term has an expected type.
     *
     * @param context Typing context.
     * @param term Candidate term.
     * @param expected Expected type.
     * @return True when term checks against expected.
     */
    fun check(context: Context, term: Term, expected: Type): Boolean {
        if (term is Term.Lambda && expected is Type.Pi) {
            if (term.parameterType != expected.domain) return false
            return check(context.withBinding(term.parameter, expected.domain), term.body, expected.codomain)
        }
        return infer(context, term) == expected
    }

    private fun inferNatRec(context: Context, term: Term.NatRec): Type? {
        if (!check(context, term.target, Type.Nat)) return null

        val zeroType = infer(context, term.zeroCase) ?: return null
        val expectedSuccType = Type.Pi(Type.Nat, Type.Pi(zeroType, zeroType))
        if (!check(context, term.succCase, expectedSuccType)) return null

        return zeroType
    }
}
