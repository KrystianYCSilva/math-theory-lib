package mathsets.cardinal

import mathsets.kernel.Cardinality
import mathsets.set.ExtensionalSet
import mathsets.set.MathSet

data class CantorDiagonalResult<T>(
    val diagonal: MathSet<T>,
    val appearsInImage: Boolean
)

/**
 * Construção diagonal de Cantor em domínio finito:
 * D = { x em S | x !in f(x) }.
 */
object CantorDiagonal {
    fun <T> diagonalSet(
        domain: MathSet<T>,
        mapping: (T) -> MathSet<T>
    ): MathSet<T> {
        require(domain.cardinality is Cardinality.Finite) {
            "Diagonal construction currently requires a finite domain."
        }
        return ExtensionalSet(domain.elements().filter { x -> x !in mapping(x) }.toSet())
    }

    fun <T> isDiagonalInImage(
        domain: MathSet<T>,
        mapping: (T) -> MathSet<T>
    ): Boolean {
        val diagonal = diagonalSet(domain, mapping).materialize()
        return domain.elements().any { x ->
            mapping(x).materialize() == diagonal
        }
    }

    fun <T> verifyNotSurjective(
        domain: MathSet<T>,
        mapping: (T) -> MathSet<T>
    ): Boolean = !isDiagonalInImage(domain, mapping)

    fun <T> run(
        domain: MathSet<T>,
        mapping: (T) -> MathSet<T>
    ): CantorDiagonalResult<T> {
        val diagonal = diagonalSet(domain, mapping).materialize()
        return CantorDiagonalResult(
            diagonal = diagonal,
            appearsInImage = isDiagonalInImage(domain, mapping)
        )
    }
}

