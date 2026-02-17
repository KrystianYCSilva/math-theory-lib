package mathsets.logic

/**
 * A first-order interpretation (model) that assigns meaning to the symbols
 * used in [Formula] and [Term] instances.
 *
 * An interpretation specifies:
 * - A **universe** of discourse (the domain over which quantifiers range).
 * - A **membership** relation defining when one element belongs to another.
 * - A mapping of **constant** names to domain values.
 * - A mapping of **function** names to executable functions over domain values.
 *
 * Pass an [Interpretation] to [ModelChecker.evaluate] to determine whether a
 * formula is satisfied.
 *
 * @property universe The finite set of all elements in the domain of discourse.
 * @property membership A binary predicate `(element, set) -> Boolean` implementing `∈`.
 * @property constants A map from constant names to their values in the domain.
 *   Defaults to an empty map.
 * @property functions A map from function symbol names to functions that accept a
 *   list of domain values and return a domain value. Defaults to an empty map.
 * @see ModelChecker
 * @see Formula
 */
data class Interpretation(
    val universe: Set<Any?>,
    val membership: (Any?, Any?) -> Boolean,
    val constants: Map<String, Any?> = emptyMap(),
    val functions: Map<String, (List<Any?>) -> Any?> = emptyMap()
)
