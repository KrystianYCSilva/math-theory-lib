package mathsets.set

class ExtensionalSet<T>(private val elementsBacking: Set<T>) : MathSet<T> {
    private val elements = elementsBacking.toSet()

    override fun contains(element: T): Boolean = elements.contains(element)

    override fun elements(): Sequence<T> = elements.asSequence()

    override fun materialize(): ExtensionalSet<T> = this

    override fun union(other: MathSet<T>): MathSet<T> {
        // Simple strategy: if other is ExtensionalSet, merge; otherwise materialize other
        return when (other) {
            is ExtensionalSet -> ExtensionalSet(this.elements + other.elements)
            else -> ExtensionalSet(this.elements + other.materialize().elementsBackingPublic)
        }
    }

    override fun intersect(other: MathSet<T>): MathSet<T> {
        return when (other) {
            is ExtensionalSet -> ExtensionalSet(this.elements.intersect(other.elements))
            else -> ExtensionalSet(this.elements.filter { it in other }.toSet())
        }
    }

    // Expose backing for internal use
    internal val elementsBackingPublic: Set<T>
        get() = elements
}
