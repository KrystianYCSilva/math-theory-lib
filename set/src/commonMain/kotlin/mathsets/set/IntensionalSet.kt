package mathsets.set

class IntensionalSet<T>(private val domain: MathSet<T>, private val predicate: (T) -> Boolean) : MathSet<T> {
    override fun contains(element: T): Boolean = (element in domain) && predicate(element)

    override fun elements(): Sequence<T> = domain.elements().filter(predicate)

    override fun materialize(): ExtensionalSet<T> {
        // Conservative: attempt to materialize by collecting elements (may be infinite)
        val collected = elements().toSet()
        return ExtensionalSet(collected)
    }

    override fun union(other: MathSet<T>): MathSet<T> {
        // Return an intensional view where appropriate
        return IntensionalSet(domain) { e -> this.contains(e) || e in other }
    }

    override fun intersect(other: MathSet<T>): MathSet<T> {
        return IntensionalSet(domain) { e -> this.contains(e) && e in other }
    }
}
