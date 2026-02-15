package mathsets.set

sealed interface MathSet<T> {
    operator fun contains(element: @UnsafeVariance T): Boolean
    fun elements(): Sequence<T>
    fun materialize(): ExtensionalSet<T>

    infix fun union(other: MathSet<@UnsafeVariance T>): MathSet<T>
    infix fun intersect(other: MathSet<@UnsafeVariance T>): MathSet<T>

    // Separation: returns an intensional view by default
    fun filter(predicate: (T) -> Boolean): MathSet<T> = IntensionalSet(this) { predicate(it) }

    // Substitution: returns a mapped view
    fun <R> map(f: (T) -> R): MathSet<R> = MappedSet(this, f)

    companion object {
        fun <T> empty(): MathSet<T> = EmptySet as MathSet<T>
        fun <T> of(vararg elements: T): MathSet<T> = ExtensionalSet(elements.toSet())
        fun <T> singleton(element: T): MathSet<T> = ExtensionalSet(setOf(element))
    }

    // Power set: returns a lazy representation
    fun powerSet(): MathSet<MathSet<T>> = LazyPowerSet(this)
}
