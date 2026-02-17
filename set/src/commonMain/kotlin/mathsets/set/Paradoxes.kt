package mathsets.set

data class ParadoxResult<T>(
    val title: String,
    val artifact: T,
    val contradictionDetected: Boolean,
    val explanation: String
)

object Paradoxes {
    fun russell(base: ExtensionalSet<MathSet<Any?>>): ParadoxResult<MathSet<MathSet<Any?>>> {
        val russellSet = base.filter { candidate -> candidate !in candidate }.materialize()
        return ParadoxResult(
            title = "Russell",
            artifact = russellSet,
            contradictionDetected = false,
            explanation = "No contradiction appears in restricted comprehension; paradox arises in unrestricted comprehension."
        )
    }

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
