package mathsets.set

/**
 * The result of running a set-theoretic paradox demonstration.
 *
 * @param T the type of the artifact produced by the demonstration.
 * @param title short name of the paradox (e.g. `"Russell"`, `"Cantor"`).
 * @param artifact the concrete object produced by the demonstration.
 * @param contradictionDetected `true` if the demonstration surfaced a contradiction
 *   (expected to be `false` under ZFC's restricted comprehension).
 * @param explanation a human-readable description of the outcome.
 */
data class ParadoxResult<T>(
    val title: String,
    val artifact: T,
    val contradictionDetected: Boolean,
    val explanation: String
)

/**
 * Demonstrations of classical set-theoretic paradoxes.
 *
 * These functions construct the paradoxical sets and report whether a contradiction
 * was detected. Under ZFC (which this library models), restricted comprehension
 * prevents the contradictions from actually arising.
 */
object Paradoxes {
    /**
     * Russell's Paradox: constructs the set `R = { x ∈ base | x ∉ x }`.
     *
     * Under unrestricted comprehension this leads to a contradiction (`R ∈ R ⟺ R ∉ R`).
     * Because this library uses restricted comprehension (Axiom of Separation), the
     * paradox does not manifest.
     *
     * @param base the collection of sets to filter.
     * @return a [ParadoxResult] describing the outcome.
     */
    fun russell(base: ExtensionalSet<MathSet<Any?>>): ParadoxResult<MathSet<MathSet<Any?>>> {
        val russellSet = base.filter { candidate -> candidate !in candidate }.materialize()
        return ParadoxResult(
            title = "Russell",
            artifact = russellSet,
            contradictionDetected = false,
            explanation = "No contradiction appears in restricted comprehension; paradox arises in unrestricted comprehension."
        )
    }

    /**
     * Cantor's Theorem demonstration: verifies that `|P(S)| > |S|` for a finite set.
     *
     * For any set `S`, its power set is strictly larger. This function computes both
     * cardinalities and checks whether the expected inequality holds.
     *
     * @param T the element type of the set.
     * @param set the finite set to analyze.
     * @return a [ParadoxResult] where [ParadoxResult.contradictionDetected] is `true`
     *   only if `|P(S)| <= |S|` (which should never happen).
     */
    fun <T> cantor(set: ExtensionalSet<T>): ParadoxResult<Int> {
        val powerCount = set.powerSet().elements().count()
        val baseCount = set.elements().count()
        val contradictionDetected = powerCount <= baseCount
        return ParadoxResult(
            title = "Cantor",
            artifact = powerCount,
            contradictionDetected = contradictionDetected,
            explanation = "For finite sets, |P(S)| = $powerCount and |S| = $baseCount."
        )
    }
}
