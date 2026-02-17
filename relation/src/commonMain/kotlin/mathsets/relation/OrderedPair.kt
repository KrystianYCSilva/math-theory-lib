package mathsets.relation

import mathsets.set.ExtensionalSet

data class OrderedPair<A, B>(val first: A, val second: B) {
    fun toKuratowski(): mathsets.set.MathSet<mathsets.set.MathSet<Any?>> {
        val aSet: mathsets.set.MathSet<Any?> = ExtensionalSet(setOf(first as Any?))
        val abSet: mathsets.set.MathSet<Any?> = ExtensionalSet(setOf(first as Any?, second as Any?))
        return ExtensionalSet(setOf(aSet, abSet))
    }
}
