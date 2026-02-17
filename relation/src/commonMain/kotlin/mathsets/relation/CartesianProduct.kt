package mathsets.relation

import mathsets.set.ExtensionalSet
import mathsets.set.MathSet

fun <A, B> MathSet<A>.cartesianProduct(other: MathSet<B>): MathSet<OrderedPair<A, B>> {
    val pairs = this.elements().flatMap { a -> other.elements().map { b -> OrderedPair(a, b) } }.toSet()
    return ExtensionalSet(pairs)
}
