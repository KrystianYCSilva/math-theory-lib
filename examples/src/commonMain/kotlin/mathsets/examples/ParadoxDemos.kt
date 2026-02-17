package mathsets.examples

/**
 * Demos didáticos de paradoxos clássicos.
 */
object ParadoxDemos {
    fun russellParadoxNarrative(): List<String> = listOf(
        "Defina R = { x | x nao pertence a x }.",
        "Se R pertence a R, entao pela definicao R nao pertence a R.",
        "Se R nao pertence a R, entao pela definicao R pertence a R.",
        "Conclusao: contradicao."
    )

    fun buraliFortiNarrative(): List<String> = listOf(
        "Considere O, o conjunto de todos os ordinais.",
        "A uniao de todos os ordinais em O seria um ordinal maior que todos eles.",
        "Esse ordinal tambem estaria em O, contradizendo maximalidade.",
        "Conclusao: nao existe conjunto de todos os ordinais."
    )

    fun cantorParadoxForFinite(cardinality: Int): Boolean {
        require(cardinality >= 0) { "cardinality must be non-negative." }
        val powerSetCardinality = 1 shl cardinality.coerceAtMost(30)
        return powerSetCardinality > cardinality
    }
}

