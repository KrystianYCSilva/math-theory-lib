package mathsets.logic

data class Interpretation(
    val universe: Set<Any?>,
    val membership: (Any?, Any?) -> Boolean,
    val constants: Map<String, Any?> = emptyMap(),
    val functions: Map<String, (List<Any?>) -> Any?> = emptyMap()
)

