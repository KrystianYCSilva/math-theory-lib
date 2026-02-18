package mathsets.examples

/**
 * Didactic demonstrations of classical set-theoretic paradoxes.
 *
 * These paradoxes historically motivated the development of axiomatic set theory (ZFC)
 * as a replacement for naive set theory.
 */
object ParadoxDemos {
    /**
     * Returns a step-by-step narrative of Russell's Paradox.
     *
     * Russell's Paradox shows that the "set of all sets that do not contain themselves"
     * leads to a contradiction, proving that unrestricted comprehension is inconsistent.
     *
     * @return A list of strings forming the narrative.
     */
    fun russellParadoxNarrative(): List<String> = listOf(
        "Defina R = { x | x nao pertence a x }.",
        "Se R pertence a R, entao pela definicao R nao pertence a R.",
        "Se R nao pertence a R, entao pela definicao R pertence a R.",
        "Conclusao: contradicao."
    )

    /**
     * Returns a step-by-step narrative of the Burali-Forti Paradox.
     *
     * The Burali-Forti Paradox shows that "the set of all ordinals" cannot exist,
     * because its union would be an ordinal greater than all ordinals in the set.
     *
     * @return A list of strings forming the narrative.
     */
    fun buraliFortiNarrative(): List<String> = listOf(
        "Considere O, o conjunto de todos os ordinais.",
        "A uniao de todos os ordinais em O seria um ordinal maior que todos eles.",
        "Esse ordinal tambem estaria em O, contradizendo maximalidade.",
        "Conclusao: nao existe conjunto de todos os ordinais."
    )

    /**
     * Demonstrates Cantor's Paradox for a finite set: |P(S)| > |S|.
     *
     * For any finite set of the given cardinality, this verifies that the power set
     * is strictly larger.
     *
     * @param cardinality The size of the set (must be non-negative).
     * @return `true` if the power set cardinality exceeds the original (always true for finite sets).
     * @throws IllegalArgumentException if [cardinality] is negative.
     */
    fun cantorParadoxForFinite(cardinality: Int): Boolean {
        require(cardinality >= 0) { "cardinality must be non-negative." }
        val powerSetCardinality = 1 shl cardinality.coerceAtMost(30)
        return powerSetCardinality > cardinality
    }
}
