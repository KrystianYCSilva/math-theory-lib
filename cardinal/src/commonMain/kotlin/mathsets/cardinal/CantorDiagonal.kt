package mathsets.cardinal

import mathsets.kernel.Cardinality
import mathsets.set.ExtensionalSet
import mathsets.set.MathSet

/**
 * Holds the result of a Cantor diagonal argument computation.
 *
 * @param T The element type.
 * @property diagonal The diagonal set D = { x in S | x not in f(x) }.
 * @property appearsInImage Whether the diagonal set appears in the image of the mapping.
 */
data class CantorDiagonalResult<T>(
    val diagonal: MathSet<T>,
    val appearsInImage: Boolean
)

/**
 * Implements Cantor's diagonal argument on finite domains.
 *
 * Given a set S and a mapping f: S -> P(S), the diagonal set is constructed as:
 *
 *     D = { x in S | x not in f(x) }
 *
 * By construction, D differs from f(x) for every x in S, proving that no surjection
 * from S to P(S) exists. This is the core of Cantor's theorem that |P(S)| > |S|.
 */
object CantorDiagonal {
    /**
     * Constructs the diagonal set D = { x in [domain] | x not in [mapping](x) }.
     *
     * @param T The element type.
     * @param domain The finite domain set S.
     * @param mapping A function from elements of S to subsets of S.
     * @return The diagonal set.
     * @throws IllegalArgumentException if [domain] is not finite.
     */
    fun <T> diagonalSet(
        domain: MathSet<T>,
        mapping: (T) -> MathSet<T>
    ): MathSet<T> {
        require(domain.cardinality is Cardinality.Finite) {
            "Diagonal construction currently requires a finite domain."
        }
        return ExtensionalSet(domain.elements().filter { x -> x !in mapping(x) }.toSet())
    }

    /**
     * Checks whether the diagonal set appears in the image of the mapping.
     *
     * @param T The element type.
     * @param domain The finite domain set S.
     * @param mapping A function from elements of S to subsets of S.
     * @return `true` if there exists some x in S such that f(x) equals the diagonal set.
     */
    fun <T> isDiagonalInImage(
        domain: MathSet<T>,
        mapping: (T) -> MathSet<T>
    ): Boolean {
        val diagonal = diagonalSet(domain, mapping).materialize()
        return domain.elements().any { x ->
            mapping(x).materialize() == diagonal
        }
    }

    /**
     * Verifies that the mapping is not surjective by confirming the diagonal set
     * is **not** in the image.
     *
     * @param T The element type.
     * @param domain The finite domain set S.
     * @param mapping A function from elements of S to subsets of S.
     * @return `true` if the mapping is not surjective (the expected result).
     */
    fun <T> verifyNotSurjective(
        domain: MathSet<T>,
        mapping: (T) -> MathSet<T>
    ): Boolean = !isDiagonalInImage(domain, mapping)

    /**
     * Runs the full diagonal argument, returning both the diagonal set and
     * whether it appears in the image.
     *
     * @param T The element type.
     * @param domain The finite domain set S.
     * @param mapping A function from elements of S to subsets of S.
     * @return A [CantorDiagonalResult] containing the diagonal set and surjectivity check.
     */
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
