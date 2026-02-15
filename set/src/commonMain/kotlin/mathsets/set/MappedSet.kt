package mathsets.set

class MappedSet<T, R>(private val source: MathSet<T>, private val f: (T) -> R) : MathSet<R> {
    override fun contains(element: R): Boolean = source.elements().any { f(it) == element }

    override fun elements(): Sequence<R> = source.elements().map(f)

    override fun materialize(): ExtensionalSet<R> = ExtensionalSet(elements().toSet())

    override fun union(other: MathSet<R>): MathSet<R> {
        // Conservative strategy: materialize both sides (safe but may be expensive)
        val a = this.materialize().elementsBackingPublic
        val b = when (other) {
            is ExtensionalSet -> other.elementsBackingPublic
            else -> other.materialize().elementsBackingPublic
        }
        return ExtensionalSet(a + b)
    }

    override fun intersect(other: MathSet<R>): MathSet<R> {
        val a = this.materialize().elementsBackingPublic
        val b = when (other) {
            is ExtensionalSet -> other.elementsBackingPublic
            else -> other.materialize().elementsBackingPublic
        }
        return ExtensionalSet(a.intersect(b))
    }
}
