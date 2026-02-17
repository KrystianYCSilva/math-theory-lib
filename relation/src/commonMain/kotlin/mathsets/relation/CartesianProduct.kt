package mathsets.relation

import mathsets.set.ExtensionalSet
import mathsets.set.MathSet

/**
 * Computes the **Cartesian product** of this set with [other].
 *
 * The Cartesian product A × B is defined as the set of all ordered pairs (a, b)
 * where a ∈ A and b ∈ B:
 *
 * ```
 * A × B = { (a, b) | a ∈ A ∧ b ∈ B }
 * ```
 *
 * Both sets must be finite (eagerly enumerated) for this operation.
 *
 * @param A the element type of the first set (receiver)
 * @param B the element type of the second set
 * @param other the right-hand set in the product
 * @return a [MathSet] of [OrderedPair]s representing A × B
 */
fun <A, B> MathSet<A>.cartesianProduct(other: MathSet<B>): MathSet<OrderedPair<A, B>> {
    val pairs = this.elements().flatMap { a -> other.elements().map { b -> OrderedPair(a, b) } }.toSet()
    return ExtensionalSet(pairs)
}
