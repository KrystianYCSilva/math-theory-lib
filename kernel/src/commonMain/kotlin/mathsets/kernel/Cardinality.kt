package mathsets.kernel

/**
 * Represents the cardinality (size) of a set in the set-theoretic sense.
 *
 * Cardinality is modeled as a sealed hierarchy supporting four cases:
 * - [Finite]: a concrete count as a [NaturalNumber].
 * - [CountablyInfinite]: the cardinality of countably infinite sets (aleph-null).
 * - [Uncountable]: the cardinality of uncountable sets (e.g., the continuum).
 * - [Unknown]: a placeholder for intensional sets whose cardinality has not been evaluated.
 *
 * The natural ordering is: Finite < CountablyInfinite < Uncountable < Unknown.
 *
 * @see NaturalNumber
 */
sealed interface Cardinality : Comparable<Cardinality> {

    /**
     * Finite cardinality representing |A| = n for some natural number n.
     *
     * @property n The cardinality as a [NaturalNumber].
     */
    data class Finite(val n: NaturalNumber) : Cardinality {
        override fun compareTo(other: Cardinality): Int {
            return when (other) {
                is Finite -> this.n.compareTo(other.n)
                else -> -1 // Finito < Infinito
            }
        }
    }

    /**
     * Countably infinite cardinality, denoted aleph-null (&#x2135;&#x2080;).
     *
     * This is the cardinality of the natural numbers, integers, and rationals.
     */
    data object CountablyInfinite : Cardinality {
        override fun compareTo(other: Cardinality): Int {
            return when (other) {
                is Finite -> 1
                is CountablyInfinite -> 0
                else -> -1
            }
        }

        override fun toString() = "\u2135\u2080"
    }

    /**
     * Uncountable cardinality, representing sets strictly larger than countably infinite
     * (e.g., the real numbers).
     */
    data object Uncountable : Cardinality {
        override fun compareTo(other: Cardinality): Int {
            return when (other) {
                is Uncountable -> 0
                is Unknown -> -1 // Assumindo Unknown > tudo ou incomparável?
                else -> 1
            }
        }

        override fun toString() = "Uncountable"
    }

    /**
     * Unknown cardinality, used as a placeholder for intensional sets whose
     * cardinality has not yet been determined or is not computable.
     */
    data object Unknown : Cardinality {
        override fun compareTo(other: Cardinality): Int = if (other is Unknown) 0 else 1
        override fun toString() = "?"
    }
}
