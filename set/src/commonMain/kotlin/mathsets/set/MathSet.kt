package mathsets.set

import mathsets.kernel.Cardinality

sealed interface MathSet<T> {
    operator fun contains(element: @UnsafeVariance T): Boolean
    fun elements(): Sequence<T>
    val cardinality: Cardinality
    fun materialize(): ExtensionalSet<T>

    infix fun union(other: MathSet<@UnsafeVariance T>): MathSet<T>
    infix fun intersect(other: MathSet<@UnsafeVariance T>): MathSet<T>

    infix fun minus(other: MathSet<@UnsafeVariance T>): MathSet<T> =
        if (cardinality.isFinite()) {
            ExtensionalSet(elements().filter { it !in other }.toSet())
        } else {
            IntensionalSet(this) { value -> value !in other }
        }

    infix fun symmetricDiff(other: MathSet<@UnsafeVariance T>): MathSet<T> =
        (this minus other) union (other minus this)

    fun complement(universe: MathSet<@UnsafeVariance T>): MathSet<T> = universe minus this

    infix fun isSubsetOf(other: MathSet<@UnsafeVariance T>): Boolean {
        require(cardinality.isFinite()) {
            "Subset check requires finite left-hand set in this implementation."
        }
        return elements().all { it in other }
    }

    infix fun isProperSubsetOf(other: MathSet<@UnsafeVariance T>): Boolean =
        (this isSubsetOf other) && !(other isSubsetOf this)

    infix fun isDisjointWith(other: MathSet<@UnsafeVariance T>): Boolean {
        return when {
            cardinality.isFinite() -> elements().none { it in other }
            other.cardinality.isFinite() -> other.elements().none { it in this }
            else -> throw UnsupportedOperationException(
                "Disjointness check needs at least one finite side in this implementation."
            )
        }
    }

    // Separation: returns an intensional view by default.
    fun filter(predicate: (T) -> Boolean): MathSet<T> = IntensionalSet(this) { predicate(it) }

    // Substitution: returns a mapped view.
    fun <R> map(f: (T) -> R): MathSet<R> = MappedSet(this, f)

    // Power set: returns a lazy representation.
    fun powerSet(): MathSet<MathSet<T>> = LazyPowerSet(this)

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun <T> empty(): MathSet<T> = EmptySet as MathSet<T>
        fun <T> of(vararg elements: T): MathSet<T> = ExtensionalSet(elements.toSet())
        fun <T> singleton(element: T): MathSet<T> = ExtensionalSet(setOf(element))
    }
}

fun <T> mathSetOf(vararg elements: T): MathSet<T> = MathSet.of(*elements)

fun <T> mathSetOf(elements: Iterable<T>): MathSet<T> = ExtensionalSet(elements.toSet())

fun mathSetOf(range: IntRange): MathSet<Int> = ExtensionalSet(range.toSet())

internal fun Cardinality.isFinite(): Boolean = this is Cardinality.Finite
