package mathsets.combinatorics

/**
 * Utilidades finitas para Ramsey (k=2).
 */
object Ramsey {
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
     * Busca o menor n (até maxVertices) tal que n -> (cliqueSize)^2_colors.
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

