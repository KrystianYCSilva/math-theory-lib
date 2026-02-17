package mathsets.descriptive

import mathsets.set.MathSet

enum class BorelLevel(val label: String) {
    SIGMA_0_1("Sigma^0_1"),
    PI_0_1("Pi^0_1"),
    SIGMA_0_2("Sigma^0_2"),
    PI_0_2("Pi^0_2"),
    HIGHER("Higher");
}

sealed interface BorelSet<T> {
    data class Open<T>(val set: MathSet<T>) : BorelSet<T>
    data class Closed<T>(val set: MathSet<T>) : BorelSet<T>
    data class CountableUnion<T>(val terms: List<BorelSet<T>>) : BorelSet<T>
    data class CountableIntersection<T>(val terms: List<BorelSet<T>>) : BorelSet<T>
    data class Complement<T>(val inner: BorelSet<T>) : BorelSet<T>
}

/**
 * Classificador simples de níveis iniciais da hierarquia de Borel.
 */
object BorelHierarchy {
    fun <T> classify(set: BorelSet<T>): BorelLevel = when (set) {
        is BorelSet.Open -> BorelLevel.SIGMA_0_1
        is BorelSet.Closed -> BorelLevel.PI_0_1
        is BorelSet.Complement -> classifyComplement(set.inner)
        is BorelSet.CountableUnion -> classifyUnion(set.terms)
        is BorelSet.CountableIntersection -> classifyIntersection(set.terms)
    }

    private fun <T> classifyComplement(inner: BorelSet<T>): BorelLevel = when (classify(inner)) {
        BorelLevel.SIGMA_0_1 -> BorelLevel.PI_0_1
        BorelLevel.PI_0_1 -> BorelLevel.SIGMA_0_1
        BorelLevel.SIGMA_0_2 -> BorelLevel.PI_0_2
        BorelLevel.PI_0_2 -> BorelLevel.SIGMA_0_2
        BorelLevel.HIGHER -> BorelLevel.HIGHER
    }

    private fun <T> classifyUnion(terms: List<BorelSet<T>>): BorelLevel {
        if (terms.isEmpty()) return BorelLevel.SIGMA_0_1
        val levels = terms.map { classify(it) }.toSet()
        return when {
            levels.all { it == BorelLevel.SIGMA_0_1 } -> BorelLevel.SIGMA_0_1
            levels.all { it == BorelLevel.SIGMA_0_1 || it == BorelLevel.PI_0_1 } -> BorelLevel.SIGMA_0_2
            else -> BorelLevel.HIGHER
        }
    }

    private fun <T> classifyIntersection(terms: List<BorelSet<T>>): BorelLevel {
        if (terms.isEmpty()) return BorelLevel.PI_0_1
        val levels = terms.map { classify(it) }.toSet()
        return when {
            levels.all { it == BorelLevel.PI_0_1 } -> BorelLevel.PI_0_1
            levels.all { it == BorelLevel.SIGMA_0_1 || it == BorelLevel.PI_0_1 } -> BorelLevel.PI_0_2
            else -> BorelLevel.HIGHER
        }
    }
}

