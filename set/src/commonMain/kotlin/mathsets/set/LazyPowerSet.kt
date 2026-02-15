package mathsets.set

class LazyPowerSet<T>(private val original: MathSet<T>) : MathSet<MathSet<T>> {
    override fun contains(element: MathSet<T>): Boolean = element.elements().all { it in original }

    override fun elements(): Sequence<MathSet<T>> = when (original) {
        is ExtensionalSet -> {
            val list = original.elementsBackingPublic.toList()
            val n = list.size
            if (n > 30) throw UnsupportedOperationException("powerSet generation limited to sets of size <= 30")
            sequence {
                val limit = 1 shl n
                for (mask in 0 until limit) {
                    val subset = mutableSetOf<T>()
                    var i = 0
                    while (i < n) {
                        if ((mask shr i) and 1 == 1) subset.add(list[i])
                        i++
                    }
                    yield(ExtensionalSet(subset))
                }
            }
        }
        else -> throw UnsupportedOperationException("powerSet.elements() only supported for finite ExtensionalSet")
    }

    override fun materialize(): ExtensionalSet<MathSet<T>> = ExtensionalSet(elements().toSet())

    override fun union(other: MathSet<MathSet<T>>): MathSet<MathSet<T>> = materialize().union(other.materialize())
    override fun intersect(other: MathSet<MathSet<T>>): MathSet<MathSet<T>> = materialize().intersect(other.materialize())
}
