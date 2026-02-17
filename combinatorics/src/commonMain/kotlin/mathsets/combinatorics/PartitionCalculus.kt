package mathsets.combinatorics

import mathsets.kernel.NaturalNumber

/**
 * Utilidades de cálculo de partições e relação de Erdős-Rado (versão finita).
 */
object PartitionCalculus {
    fun <T> allPartitions(elements: Set<T>): Set<Set<Set<T>>> {
        if (elements.isEmpty()) return setOf(emptySet())

        val first = elements.first()
        val rest = elements - first
        val restPartitions = allPartitions(rest)
        val result = mutableSetOf<Set<Set<T>>>()

        for (partition in restPartitions) {
            val withSingletonBlock = partition.toMutableSet()
            withSingletonBlock.add(setOf(first))
            result.add(withSingletonBlock.toSet())

            for (block in partition) {
                val newBlock = block + first
                val updatedPartition = partition.toMutableSet()
                updatedPartition.remove(block)
                updatedPartition.add(newBlock)
                result.add(updatedPartition.toSet())
            }
        }

        return result
    }

    fun bellNumber(n: Int): NaturalNumber {
        require(n >= 0) { "n must be non-negative." }
        return NaturalNumber.of(allPartitions((0 until n).toSet()).size)
    }

    /**
     * Verifica finitamente n -> (monochromaticSize)^subsetSize_colors.
     */
    fun erdosRadoArrow(
        n: Int,
        monochromaticSize: Int,
        subsetSize: Int = 2,
        colors: Int = 2
    ): Boolean {
        require(n >= 0) { "n must be non-negative." }
        require(monochromaticSize >= subsetSize) { "monochromaticSize must be >= subsetSize." }
        require(subsetSize >= 1) { "subsetSize must be >= 1." }
        require(colors >= 2) { "colors must be >= 2." }

        val baseSet = (0 until n).toList()
        val coloredSubsets = combinations(baseSet, subsetSize)
        val assignments = intPow(colors, coloredSubsets.size)
        require(assignments <= 2_000_000L) {
            "Search space too large for brute-force finite verification."
        }

        val candidateMonochromatics = combinations(baseSet, monochromaticSize)
            .map { it.toSet() }

        for (code in 0L until assignments) {
            val coloring = decodeColoring(code, coloredSubsets, colors)
            val hasWitness = candidateMonochromatics.any { witness ->
                isMonochromatic(witness, subsetSize, coloring)
            }
            if (!hasWitness) return false
        }

        return true
    }

    private fun <T> decodeColoring(
        code: Long,
        subsets: List<List<T>>,
        colors: Int
    ): Map<Set<T>, Int> {
        var value = code
        val result = linkedMapOf<Set<T>, Int>()
        for (subset in subsets) {
            result[subset.toSet()] = (value % colors).toInt()
            value /= colors
        }
        return result
    }

    private fun <T> isMonochromatic(
        container: Set<T>,
        subsetSize: Int,
        coloring: Map<Set<T>, Int>
    ): Boolean {
        val innerSubsets = combinations(container.toList(), subsetSize).map { it.toSet() }
        if (innerSubsets.isEmpty()) return true

        val reference = coloring[innerSubsets.first()] ?: return false
        return innerSubsets.all { subset -> coloring[subset] == reference }
    }

    private fun intPow(base: Int, exponent: Int): Long {
        var result = 1L
        repeat(exponent) { result *= base }
        return result
    }
}
