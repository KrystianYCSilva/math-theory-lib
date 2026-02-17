package mathsets.forcing

data class GenericFilter<T>(val conditions: Set<T>)

object GenericFilterBuilder {
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

    fun <T> isGeneric(
        poset: Poset<T>,
        candidate: GenericFilter<T>,
        denseFamilies: Set<Set<T>>
    ): Boolean = poset.isFilter(candidate.conditions) &&
        denseFamilies.all { dense -> candidate.conditions.any { it in dense } }
}

