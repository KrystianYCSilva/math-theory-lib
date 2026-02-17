package mathsets.function

import mathsets.kernel.Cardinality
import mathsets.set.MathSet

infix fun <A, B> MathSet<A>.isEquipotentTo(other: MathSet<B>): Boolean =
    findBijectionTo(other) != null

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

