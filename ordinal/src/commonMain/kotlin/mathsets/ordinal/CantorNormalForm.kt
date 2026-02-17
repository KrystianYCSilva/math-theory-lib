package mathsets.ordinal

import mathsets.kernel.NaturalNumber

/**
 * Termo de Cantor (coeficiente * ω^exponente), com coeficiente > 0.
 */
data class CNFTerm(
    val exponent: NaturalNumber,
    val coefficient: NaturalNumber
) {
    init {
        require(coefficient > NaturalNumber.ZERO) { "CNF coefficient must be positive." }
    }
}

/**
 * Forma normal de Cantor para ordinais com expoentes finitos.
 */
@ConsistentCopyVisibility
data class CantorNormalForm private constructor(
    val terms: List<CNFTerm>
) {
    init {
        require(terms.isNotEmpty()) { "CantorNormalForm cannot be empty." }
        for (i in 0 until terms.lastIndex) {
            require(terms[i].exponent > terms[i + 1].exponent) {
                "CNF exponents must be strictly descending."
            }
        }
    }

    companion object {
        fun of(terms: List<CNFTerm>): CantorNormalForm {
            val normalized = normalizeTerms(terms)
            require(normalized.isNotEmpty()) { "CantorNormalForm cannot represent zero." }
            return CantorNormalForm(normalized)
        }
    }
}

internal fun normalizeTerms(rawTerms: List<CNFTerm>): List<CNFTerm> {
    if (rawTerms.isEmpty()) return emptyList()

    val sorted = rawTerms.sortedByDescending { it.exponent }
    val merged = mutableListOf<CNFTerm>()

    for (term in sorted) {
        val last = merged.lastOrNull()
        if (last != null && last.exponent == term.exponent) {
            merged[merged.lastIndex] = CNFTerm(
                exponent = last.exponent,
                coefficient = last.coefficient + term.coefficient
            )
        } else {
            merged.add(term)
        }
    }

    return merged
}
