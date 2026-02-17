package mathsets.combinatorics

import mathsets.kernel.NaturalNumber

/**
 * Utilities for partition calculus and the finite Erdos-Rado arrow relation.
 *
 * Provides enumeration of all set partitions, Bell number computation, and
 * brute-force verification of the Erdos-Rado arrow notation `n -> (m)^k_c`,
 * which asserts that for every c-coloring of k-element subsets of an n-element set,
 * there exists a monochromatic m-element subset.
 */
object PartitionCalculus {
    /**
     * Enumerates all partitions of the given set of elements.
     *
     * A partition of a set S is a collection of non-empty, pairwise disjoint subsets
     * whose union is S.
     *
     * @param T The element type.
     * @param elements The set to partition.
     * @return The set of all possible partitions of [elements].
     */
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

    /**
     * Computes the Bell number B(n), which counts the number of partitions of
     * an n-element set.
     *
     * @param n The size of the set (must be non-negative).
     * @return The Bell number B(n) as a [NaturalNumber].
     * @throws IllegalArgumentException if [n] is negative.
     */
    fun bellNumber(n: Int): NaturalNumber {
        require(n >= 0) { "n must be non-negative." }
        return NaturalNumber.of(allPartitions((0 until n).toSet()).size)
    }

    /**
     * Verifies the finite Erdos-Rado arrow relation: `n -> (monochromaticSize)^subsetSize_colors`.
     *
     * This checks by brute force that for every coloring of [subsetSize]-element subsets
     * of `{0, ..., n-1}` using [colors] colors, there exists a monochromatic subset
     * of size [monochromaticSize].
     *
     * @param n The size of the ground set.
     * @param monochromaticSize The required size of the monochromatic subset.
     * @param subsetSize The size of subsets being colored (default: 2, i.e., edges).
     * @param colors The number of colors (default: 2).
     * @return `true` if the arrow relation holds, `false` if a counterexample exists.
     * @throws IllegalArgumentException if parameters are out of range or search space is too large.
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
