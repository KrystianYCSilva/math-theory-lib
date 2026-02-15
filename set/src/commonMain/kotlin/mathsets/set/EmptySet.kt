package mathsets.set

object EmptySet : MathSet<Nothing> {
    override fun contains(element: Nothing): Boolean = false
    override fun elements(): Sequence<Nothing> = emptySequence()
    override fun materialize(): ExtensionalSet<Nothing> = ExtensionalSet(emptySet())
    override fun union(other: MathSet<@UnsafeVariance Nothing>): MathSet<Nothing> = other as MathSet<Nothing>
    override fun intersect(other: MathSet<@UnsafeVariance Nothing>): MathSet<Nothing> = this
}
