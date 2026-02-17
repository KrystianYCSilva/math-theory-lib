package mathsets.kernel

/**
 * Representa a cardinalidade de um conjunto.
 */
sealed interface Cardinality : Comparable<Cardinality> {

    /** Cardinalidade finita: |A| = n */
    data class Finite(val n: NaturalNumber) : Cardinality {
        override fun compareTo(other: Cardinality): Int {
            return when (other) {
                is Finite -> this.n.compareTo(other.n)
                else -> -1 // Finito < Infinito
            }
        }
    }

    /** Cardinalidade infinita enumerável (ℵ₀) */
    data object CountablyInfinite : Cardinality {
        override fun compareTo(other: Cardinality): Int {
            return when (other) {
                is Finite -> 1
                is CountablyInfinite -> 0
                else -> -1
            }
        }

        override fun toString() = "ℵ₀"
    }

    /** Cardinalidade não-enumerável (geral) */
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

    /** Cardinalidade desconhecida (para conjuntos intensionais não avaliados) */
    data object Unknown : Cardinality {
        override fun compareTo(other: Cardinality): Int = if (other is Unknown) 0 else 1
        override fun toString() = "?"
    }
}
