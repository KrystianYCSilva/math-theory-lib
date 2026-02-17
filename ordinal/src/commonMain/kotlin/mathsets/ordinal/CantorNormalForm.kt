package mathsets.ordinal

import mathsets.kernel.NaturalNumber

/**
 * Represents a single term in a Cantor Normal Form expression: `coefficient * omega^exponent`.
 *
 * Each term has a positive coefficient and a non-negative exponent. Terms are the building
 * blocks of [CantorNormalForm].
 *
 * @property exponent The power of omega in this term.
 * @property coefficient The positive natural number coefficient (must be > 0).
 * @throws IllegalArgumentException if [coefficient] is zero.
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
 * Represents an ordinal in Cantor Normal Form (CNF).
 *
 * Every ordinal alpha > 0 can be uniquely written as:
 *
 *     omega^a1 * c1 + omega^a2 * c2 + ... + omega^ak * ck
 *
 * where a1 > a2 > ... > ak >= 0 and each ci > 0.
 *
 * This class enforces that [terms] is non-empty and that exponents are strictly descending.
 *
 * @property terms The list of CNF terms in strictly descending order of exponents.
 * @throws IllegalArgumentException if [terms] is empty or exponents are not strictly descending.
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
        /**
         * Creates a [CantorNormalForm] from the given terms after normalization.
         *
         * Terms are sorted by descending exponent and terms with equal exponents are
         * merged by summing their coefficients.
         *
         * @param terms The raw list of CNF terms.
         * @return A normalized Cantor Normal Form.
         * @throws IllegalArgumentException if the normalized list is empty (i.e., represents zero).
         */
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
