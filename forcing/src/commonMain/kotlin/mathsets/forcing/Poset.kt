package mathsets.forcing

/**
 * Represents a finite partially ordered set (poset) with operations for density,
 * antichains, and filters.
 *
 * A poset is a set equipped with a reflexive, antisymmetric, and transitive relation.
 * These axioms are validated at construction time.
 *
 * In the context of forcing, posets serve as the "conditions" that approximate
 * generic objects. Dense subsets and filters are key concepts for constructing
 * generic filters.
 *
 * @param T The element type.
 * @property elements The non-empty set of elements in this poset.
 * @param leq The partial order relation: `leq(a, b)` means "a is below b" (a <= b).
 * @throws IllegalArgumentException if [elements] is empty or [leq] does not satisfy
 *         the partial order axioms (reflexivity, antisymmetry, transitivity).
 */
class Poset<T>(
    val elements: Set<T>,
    private val leq: (T, T) -> Boolean
) {
    init {
        require(elements.isNotEmpty()) { "Poset elements cannot be empty." }
        require(validateAxioms()) { "Relation does not satisfy partial-order axioms." }
    }

    /**
     * Tests whether [a] is below [b] in the partial order (a <= b).
     *
     * @param a The first element.
     * @param b The second element.
     * @return `true` if a <= b.
     */
    fun isBelow(a: T, b: T): Boolean = leq(a, b)

    /**
     * Checks whether the given [subset] is dense in this poset.
     *
     * A subset D is dense if for every element p in the poset, there exists
     * an element q in D such that q <= p.
     *
     * @param subset The subset to test for density.
     * @return `true` if [subset] is dense.
     * @throws IllegalArgumentException if [subset] contains elements not in this poset.
     */
    fun isDense(subset: Set<T>): Boolean {
        require(subset.all { it in elements }) { "Dense subset must be contained in poset elements." }
        return elements.all { p -> subset.any { q -> isBelow(q, p) } }
    }

    /**
     * Enumerates all dense subsets of this poset.
     *
     * @return The set of all subsets of [elements] that are dense.
     */
    fun denseSubsets(): Set<Set<T>> = allSubsets(elements).filterTo(mutableSetOf()) { isDense(it) }

    /**
     * Checks whether the given [subset] is an antichain (no two elements are comparable).
     *
     * @param subset The subset to test.
     * @return `true` if no two distinct elements of [subset] satisfy a <= b or b <= a.
     * @throws IllegalArgumentException if [subset] contains elements not in this poset.
     */
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

    /**
     * Enumerates all antichains of this poset.
     *
     * @return The set of all antichains (subsets with no comparable pairs).
     */
    fun antichains(): Set<Set<T>> = allSubsets(elements).filterTo(mutableSetOf()) { isAntichain(it) }

    /**
     * Checks whether the given [candidate] is a filter on this poset.
     *
     * A filter F is a non-empty, upward-closed, directed subset:
     * 1. F is non-empty.
     * 2. If p in F and p <= q, then q in F (upward closed).
     * 3. For any p, q in F, there exists r in F such that r <= p and r <= q (directed).
     *
     * @param candidate The subset to test.
     * @return `true` if [candidate] is a filter.
     */
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

    /**
     * Enumerates all filters of this poset.
     *
     * @return The set of all subsets of [elements] that are filters.
     */
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
