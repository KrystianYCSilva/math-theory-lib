package mathsets.forcing

/**
 * Poset finito com operações para densidade, anticadeias e filtros.
 */
class Poset<T>(
    val elements: Set<T>,
    private val leq: (T, T) -> Boolean
) {
    init {
        require(elements.isNotEmpty()) { "Poset elements cannot be empty." }
        require(validateAxioms()) { "Relation does not satisfy partial-order axioms." }
    }

    fun isBelow(a: T, b: T): Boolean = leq(a, b)

    fun isDense(subset: Set<T>): Boolean {
        require(subset.all { it in elements }) { "Dense subset must be contained in poset elements." }
        return elements.all { p -> subset.any { q -> isBelow(q, p) } }
    }

    fun denseSubsets(): Set<Set<T>> = allSubsets(elements).filterTo(mutableSetOf()) { isDense(it) }

    fun isAntichain(subset: Set<T>): Boolean {
        require(subset.all { it in elements }) { "Antichain must be contained in poset elements." }
        val list = subset.toList()
        for (i in list.indices) {
            for (j in i + 1 until list.size) {
                val a = list[i]
                val b = list[j]
                if (isBelow(a, b) || isBelow(b, a)) return false
            }
        }
        return true
    }

    fun antichains(): Set<Set<T>> = allSubsets(elements).filterTo(mutableSetOf()) { isAntichain(it) }

    fun isFilter(candidate: Set<T>): Boolean {
        if (candidate.isEmpty()) return false
        if (candidate.any { it !in elements }) return false

        val upwardClosed = candidate.all { p ->
            elements.all { q -> !isBelow(p, q) || q in candidate }
        }
        if (!upwardClosed) return false

        val directed = candidate.all { p ->
            candidate.all { q ->
                candidate.any { r -> isBelow(r, p) && isBelow(r, q) }
            }
        }

        return directed
    }

    fun filters(): Set<Set<T>> = allSubsets(elements).filterTo(mutableSetOf()) { isFilter(it) }

    private fun validateAxioms(): Boolean {
        for (a in elements) {
            if (!isBelow(a, a)) return false
        }
        for (a in elements) {
            for (b in elements) {
                if (isBelow(a, b) && isBelow(b, a) && a != b) return false
            }
        }
        for (a in elements) {
            for (b in elements) {
                for (c in elements) {
                    if (isBelow(a, b) && isBelow(b, c) && !isBelow(a, c)) return false
                }
            }
        }
        return true
    }
}

internal fun <T> allSubsets(base: Set<T>): Set<Set<T>> {
    val list = base.toList()
    val result = mutableSetOf<Set<T>>()
    val total = 1 shl list.size
    for (mask in 0 until total) {
        val subset = mutableSetOf<T>()
        for (i in list.indices) {
            if ((mask and (1 shl i)) != 0) subset.add(list[i])
        }
        result.add(subset)
    }
    return result
}

