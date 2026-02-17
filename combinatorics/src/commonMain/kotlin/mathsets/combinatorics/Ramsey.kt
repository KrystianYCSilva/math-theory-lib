package mathsets.combinatorics

/**
 * Finite utilities for Ramsey theory (edge-coloring variant, k=2).
 *
 * Ramsey theory studies conditions under which order must appear in combinatorial structures.
 * The classic result R(s,s) guarantees that any 2-coloring of edges of a sufficiently large
 * complete graph contains a monochromatic clique of size s.
 */
object Ramsey {
    /**
     * Searches for a monochromatic clique of the given size in a vertex-colored complete graph.
     *
     * @param V The vertex type.
     * @param vertices The set of all vertices.
     * @param cliqueSize The required clique size (must be at least 2).
     * @param colorOf A function that returns the color of the edge between two vertices.
     * @return A monochromatic clique of the requested size, or `null` if none exists.
     * @throws IllegalArgumentException if [cliqueSize] is less than 2.
     */
    fun <V> findMonochromaticClique(
        vertices: Set<V>,
        cliqueSize: Int,
        colorOf: (V, V) -> Int
    ): Set<V>? {
        require(cliqueSize >= 2) { "cliqueSize must be at least 2." }
        val list = vertices.toList()
        if (cliqueSize > list.size) return null

        for (subset in combinations(list, cliqueSize)) {
            if (isMonochromaticClique(subset, colorOf)) {
                return subset.toSet()
            }
        }
        return null
    }

    /**
     * Searches for the smallest n (up to [maxVertices]) such that the Erdos-Rado arrow
     * relation `n -> (cliqueSize)^2_colors` holds.
     *
     * This effectively computes a Ramsey number upper bound by brute force.
     *
     * @param cliqueSize The required monochromatic clique size (must be at least 2).
     * @param colors The number of colors (default: 2).
     * @param maxVertices The maximum number of vertices to search up to (default: 8).
     * @return The smallest n for which the arrow relation holds, or `null` if not found
     *         within the search range.
     * @throws IllegalArgumentException if parameters are out of range.
     */
    fun searchBounds(
        cliqueSize: Int,
        colors: Int = 2,
        maxVertices: Int = 8
    ): Int? {
        require(cliqueSize >= 2) { "cliqueSize must be at least 2." }
        require(colors >= 2) { "colors must be at least 2." }
        require(maxVertices >= cliqueSize) { "maxVertices must be >= cliqueSize." }

        for (n in cliqueSize..maxVertices) {
            if (PartitionCalculus.erdosRadoArrow(n, cliqueSize, subsetSize = 2, colors = colors)) {
                return n
            }
        }
        return null
    }

    private fun <V> isMonochromaticClique(
        subset: List<V>,
        colorOf: (V, V) -> Int
    ): Boolean {
        var referenceColor: Int? = null
        for (i in subset.indices) {
            for (j in i + 1 until subset.size) {
                val color = colorOf(subset[i], subset[j])
                if (referenceColor == null) {
                    referenceColor = color
                } else if (referenceColor != color) {
                    return false
                }
            }
        }
        return true
    }
}

internal fun <T> combinations(items: List<T>, size: Int): List<List<T>> {
    if (size == 0) return listOf(emptyList())
    if (size > items.size) return emptyList()

    val result = mutableListOf<List<T>>()

    fun backtrack(start: Int, acc: MutableList<T>) {
        if (acc.size == size) {
            result.add(acc.toList())
            return
        }
        for (i in start until items.size) {
            acc.add(items[i])
            backtrack(i + 1, acc)
            acc.removeAt(acc.lastIndex)
        }
    }

    backtrack(0, mutableListOf())
    return result
}
