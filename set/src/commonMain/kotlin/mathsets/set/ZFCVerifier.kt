package mathsets.set

/**
 * A finite model of a set-theoretic universe, consisting of ground-level elements
 * and a collection of sets built from those elements.
 *
 * Used as input to [ZFCVerifier.verify] to check how many ZFC axioms the model satisfies.
 *
 * @param T the element type.
 * @param universe the finite set of ground-level elements.
 * @param sets the finite collection of sets (each a [MathSet] over [T]) present in the model.
 */
data class FiniteModel<T>(
    val universe: ExtensionalSet<T>,
    val sets: ExtensionalSet<MathSet<T>>
)

/**
 * The result of a ZFC axiom verification run.
 *
 * Maps each axiom name (e.g. `"Extensionality"`, `"Pairing"`) to a [Boolean] indicating
 * whether the axiom holds in the inspected [FiniteModel].
 *
 * @param byAxiom axiom-name to satisfaction-status mapping.
 */
data class ZFCReport(
    val byAxiom: Map<String, Boolean>
) {
    /**
     * Returns `true` if the named [axiom] is satisfied.
     *
     * @param axiom the axiom name (must match a key in [byAxiom]).
     */
    fun isSatisfied(axiom: String): Boolean = byAxiom[axiom] == true
}

/**
 * Verifies a [FiniteModel] against the axioms of Zermelo-Fraenkel set theory with Choice (ZFC).
 *
 * Because the models are finite, the Axiom of Infinity is always reported as `false`.
 * Replacement, Choice, and Foundation are trivially `true` in finite models.
 *
 * Axioms checked:
 * - **Extensionality** — sets with the same members are equal.
 * - **Empty Set** — the empty set exists in the model.
 * - **Pairing** — for every two elements, `{a, b}` exists.
 * - **Union** — the model is closed under pairwise union.
 * - **Power Set** — every subset of every set in the model is also in the model.
 * - **Infinity** — always `false` for finite models.
 * - **Separation** — approximated by power-set closure.
 * - **Replacement** — trivially `true`.
 * - **Choice** — trivially `true`.
 * - **Foundation** — trivially `true`.
 */
object ZFCVerifier {
    /**
     * Runs the full ZFC axiom check on the supplied [model] and returns a [ZFCReport].
     *
     * @param T the element type.
     * @param model the finite model to verify.
     * @return a [ZFCReport] mapping each axiom to its satisfaction status.
     */
    fun <T> verify(model: FiniteModel<T>): ZFCReport {
        val universe = model.universe
        val setList = model.sets.elements().map { it.materialize() }.toList()

        fun containsSet(target: MathSet<T>): Boolean =
            setList.any { existing -> equivalent(existing, target, universe) }

        val extensionality = setList.all { a ->
            setList.all { b ->
                val sameMembership = equivalent(a, b, universe)
                !sameMembership || ((a isSubsetOf b) && (b isSubsetOf a))
            }
        }

        val emptySet = containsSet(MathSet.empty())

        val pairing = universe.elements().all { a ->
            universe.elements().all { b ->
                containsSet(mathSetOf(a, b))
            }
        }

        val unionClosure = setList.all { a ->
            setList.all { b ->
                containsSet(a union b)
            }
        }

        val powerSetClosure = setList.all { set ->
            val subsets = set.powerSet().elements().toList()
            subsets.all { subset -> containsSet(subset) }
        }

        val infinity = false
        val separation = powerSetClosure
        val replacement = true
        val choice = true
        val foundation = true

        return ZFCReport(
            byAxiom = mapOf(
                "Extensionality" to extensionality,
                "EmptySet" to emptySet,
                "Pairing" to pairing,
                "Union" to unionClosure,
                "PowerSet" to powerSetClosure,
                "Infinity" to infinity,
                "Separation" to separation,
                "Replacement" to replacement,
                "Choice" to choice,
                "Foundation" to foundation
            )
        )
    }

    /**
     * Tests extensional equivalence of two sets over a [universe].
     */
    private fun <T> equivalent(a: MathSet<T>, b: MathSet<T>, universe: MathSet<T>): Boolean =
        universe.elements().all { x -> (x in a) == (x in b) }
}
