package mathsets.function

import mathsets.kernel.Cardinality
import mathsets.set.MathSet

/**
 * Tests whether this set is **equipotent** (has the same cardinality) to [other].
 *
 * Two sets A and B are equipotent (written |A| = |B|) if there exists a bijection
 * f: A -> B. This is the set-theoretic definition of "same size" and generalizes
 * naturally to infinite sets.
 *
 * Currently limited to finite sets.
 *
 * @param A the element type of this set
 * @param B the element type of the other set
 * @param other the set to compare cardinality with
 * @return `true` if a bijection exists between the two sets
 */
infix fun <A, B> MathSet<A>.isEquipotentTo(other: MathSet<B>): Boolean =
    findBijectionTo(other) != null

/**
 * Attempts to construct an explicit [Bijection] from this set to [other].
 *
 * If both sets are finite and have the same size, a bijection is constructed by
 * pairing elements in iteration order. Returns `null` if the sets have different
 * sizes or either set is infinite.
 *
 * @param A the element type of this set
 * @param B the element type of the other set
 * @param other the target set
 * @return a [Bijection] from this set to [other], or `null` if none can be constructed
 */
fun <A, B> MathSet<A>.findBijectionTo(other: MathSet<B>): Bijection<A, B>? {
    if (this.cardinality !is Cardinality.Finite || other.cardinality !is Cardinality.Finite) {
        return null
    }

    val left = this.elements().toList()
    val right = other.elements().toList()
    if (left.size != right.size) return null

    val map = left.zip(right).toMap()
    return Bijection(this, other) { a ->
        map[a] ?: throw IllegalArgumentException("No mapping for element $a")
    }
}
