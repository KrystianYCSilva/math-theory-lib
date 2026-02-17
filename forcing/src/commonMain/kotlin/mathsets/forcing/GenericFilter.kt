package mathsets.forcing

/**
 * Represents a generic filter over a poset.
 *
 * A generic filter is a filter that intersects every dense subset from a given
 * family of dense sets. Generic filters are the central construction in Cohen's
 * forcing technique.
 *
 * @param T The element type of the poset conditions.
 * @property conditions The set of conditions comprising this filter.
 */
data class GenericFilter<T>(val conditions: Set<T>)

/**
 * Builder for constructing generic filters over finite posets.
 *
 * Given a poset and a family of dense subsets, this builder finds a filter
 * that meets every dense set in the family.
 */
object GenericFilterBuilder {
    /**
     * Builds a generic filter for the given [poset] that meets all specified [denseFamilies].
     *
     * Searches through all filters of the poset and returns the first one that
     * intersects every dense set in [denseFamilies].
     *
     * @param T The element type.
     * @param poset The finite poset.
     * @param denseFamilies A set of dense subsets that the filter must intersect.
     * @return A [GenericFilter] meeting all dense sets, or `null` if none exists.
     */
    fun <T> build(
        poset: Poset<T>,
        denseFamilies: Set<Set<T>> = emptySet()
    ): GenericFilter<T>? {
        val filters = poset.filters().sortedWith(
            compareBy<Set<T>> { it.size }.thenBy { it.hashCode() }
        )

        val witness = filters.firstOrNull { filter ->
            denseFamilies.all { dense -> filter.any { it in dense } }
        }
        return witness?.let { GenericFilter(it) }
    }

    /**
     * Verifies that the given [candidate] is a valid generic filter for the [poset]
     * with respect to the specified [denseFamilies].
     *
     * @param T The element type.
     * @param poset The finite poset.
     * @param candidate The candidate generic filter to verify.
     * @param denseFamilies The family of dense subsets the filter must intersect.
     * @return `true` if [candidate] is a filter that meets every dense set.
     */
    fun <T> isGeneric(
        poset: Poset<T>,
        candidate: GenericFilter<T>,
        denseFamilies: Set<Set<T>>
    ): Boolean = poset.isFilter(candidate.conditions) &&
        denseFamilies.all { dense -> candidate.conditions.any { it in dense } }
}
