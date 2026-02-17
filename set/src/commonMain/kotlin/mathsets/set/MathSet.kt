package mathsets.set

import mathsets.kernel.Cardinality

/**
 * The root abstraction for all mathematical sets in this library.
 *
 * `MathSet` is a sealed interface whose variants capture the two classical modes of set
 * definition — extensional (by listing elements) and intensional (by predicate) — as well
 * as specialized representations such as [BitMathSet], [LazyPowerSet], and [MappedSet].
 *
 * All standard set-algebra operations (union, intersection, difference, complement,
 * symmetric difference, subset checks) are declared here with sensible default
 * implementations that subclasses may override for efficiency.
 *
 * @param T the element type of this set.
 */
sealed interface MathSet<T> {
    /**
     * Tests whether [element] belongs to this set.
     *
     * @param element the candidate element.
     * @return `true` if the set contains [element].
     */
    operator fun contains(element: @UnsafeVariance T): Boolean

    /**
     * Returns a lazy [Sequence] that enumerates the elements of this set.
     *
     * For infinite sets this sequence may be unbounded; for uncountable sets
     * (e.g. [Reals]) this operation throws [UnsupportedOperationException].
     */
    fun elements(): Sequence<T>

    /**
     * The cardinality of this set, expressed as a [Cardinality] value
     * (finite, countably infinite, uncountable, or unknown).
     */
    val cardinality: Cardinality

    /**
     * Materializes this set into an [ExtensionalSet] by eagerly collecting all elements.
     *
     * @throws InfiniteMaterializationException if the set is not finite.
     */
    fun materialize(): ExtensionalSet<T>

    /**
     * Returns the union of this set and [other] (`this ∪ other`).
     *
     * @param other the right-hand operand.
     * @return a [MathSet] containing every element that belongs to this set or [other].
     */
    infix fun union(other: MathSet<@UnsafeVariance T>): MathSet<T>

    /**
     * Returns the intersection of this set and [other] (`this ∩ other`).
     *
     * @param other the right-hand operand.
     * @return a [MathSet] containing every element that belongs to both this set and [other].
     */
    infix fun intersect(other: MathSet<@UnsafeVariance T>): MathSet<T>

    /**
     * Returns the set difference of this set and [other] (`this \ other`).
     *
     * For finite sets the result is eagerly materialized; for infinite sets an
     * [IntensionalSet] view is returned.
     *
     * @param other the set whose elements are excluded.
     * @return a [MathSet] containing every element of this set that is not in [other].
     */
    infix fun minus(other: MathSet<@UnsafeVariance T>): MathSet<T> =
        if (cardinality.isFinite()) {
            ExtensionalSet(elements().filter { it !in other }.toSet())
        } else {
            IntensionalSet(this) { value -> value !in other }
        }

    /**
     * Returns the symmetric difference of this set and [other] (`(this \ other) ∪ (other \ this)`).
     *
     * @param other the right-hand operand.
     * @return a [MathSet] containing elements that belong to exactly one of the two sets.
     */
    infix fun symmetricDiff(other: MathSet<@UnsafeVariance T>): MathSet<T> =
        (this minus other) union (other minus this)

    /**
     * Returns the complement of this set relative to [universe] (`universe \ this`).
     *
     * @param universe the universal set with respect to which the complement is taken.
     * @return a [MathSet] containing every element of [universe] that is not in this set.
     */
    fun complement(universe: MathSet<@UnsafeVariance T>): MathSet<T> = universe minus this

    /**
     * Tests whether this set is a subset of [other] (`this ⊆ other`).
     *
     * @param other the candidate superset.
     * @return `true` if every element of this set also belongs to [other].
     * @throws IllegalArgumentException if this set is not finite.
     */
    infix fun isSubsetOf(other: MathSet<@UnsafeVariance T>): Boolean {
        require(cardinality.isFinite()) {
            "Subset check requires finite left-hand set in this implementation."
        }
        return elements().all { it in other }
    }

    /**
     * Tests whether this set is a proper subset of [other] (`this ⊂ other`).
     *
     * A proper subset is a subset that is not equal to [other].
     *
     * @param other the candidate strict superset.
     * @return `true` if `this ⊆ other` and `other ⊄ this`.
     */
    infix fun isProperSubsetOf(other: MathSet<@UnsafeVariance T>): Boolean =
        (this isSubsetOf other) && !(other isSubsetOf this)

    /**
     * Tests whether this set and [other] are disjoint (have no elements in common).
     *
     * At least one of the two operands must be finite; otherwise an
     * [UnsupportedOperationException] is thrown.
     *
     * @param other the other set.
     * @return `true` if the two sets share no elements.
     */
    infix fun isDisjointWith(other: MathSet<@UnsafeVariance T>): Boolean {
        return when {
            cardinality.isFinite() -> elements().none { it in other }
            other.cardinality.isFinite() -> other.elements().none { it in this }
            else -> throw UnsupportedOperationException(
                "Disjointness check needs at least one finite side in this implementation."
            )
        }
    }

    /**
     * Returns an [IntensionalSet] view that retains only elements satisfying [predicate].
     *
     * This corresponds to the ZFC Axiom of Separation (Restricted Comprehension).
     *
     * @param predicate the membership criterion.
     * @return a filtered [MathSet].
     */
    fun filter(predicate: (T) -> Boolean): MathSet<T> = IntensionalSet(this) { predicate(it) }

    /**
     * Returns a [MappedSet] view whose elements are the images of this set's elements under [f].
     *
     * This corresponds to the ZFC Axiom of Replacement.
     *
     * @param R the element type of the resulting set.
     * @param f the mapping function.
     * @return a [MathSet] of mapped elements.
     */
    fun <R> map(f: (T) -> R): MathSet<R> = MappedSet(this, f)

    /**
     * Returns the power set of this set — the set of all subsets.
     *
     * The result is a [LazyPowerSet] that enumerates subsets lazily.
     *
     * @return a [MathSet] whose elements are all subsets of this set.
     */
    fun powerSet(): MathSet<MathSet<T>> = LazyPowerSet(this)

    companion object {
        /**
         * Returns the canonical empty set, typed to [T].
         *
         * @param T the phantom element type.
         * @return the [EmptySet] singleton cast to `MathSet<T>`.
         */
        @Suppress("UNCHECKED_CAST")
        fun <T> empty(): MathSet<T> = EmptySet as MathSet<T>

        /**
         * Creates a finite [ExtensionalSet] from the given [elements].
         *
         * @param T the element type.
         * @param elements the elements to include.
         * @return an [ExtensionalSet] containing the supplied elements.
         */
        fun <T> of(vararg elements: T): MathSet<T> = ExtensionalSet(elements.toSet())

        /**
         * Creates a singleton [ExtensionalSet] containing exactly [element].
         *
         * @param T the element type.
         * @param element the sole member.
         * @return a single-element [ExtensionalSet].
         */
        fun <T> singleton(element: T): MathSet<T> = ExtensionalSet(setOf(element))
    }
}

/**
 * Top-level factory that creates an [ExtensionalSet] from the given [elements].
 *
 * @param T the element type.
 * @param elements the elements to include.
 * @return an [ExtensionalSet] containing the supplied elements.
 */
fun <T> mathSetOf(vararg elements: T): MathSet<T> = MathSet.of(*elements)

/**
 * Top-level factory that creates an [ExtensionalSet] from an [Iterable].
 *
 * @param T the element type.
 * @param elements the elements to include.
 * @return an [ExtensionalSet] containing the supplied elements.
 */
fun <T> mathSetOf(elements: Iterable<T>): MathSet<T> = ExtensionalSet(elements.toSet())

/**
 * Top-level factory that creates an [ExtensionalSet] of [Int] from an [IntRange].
 *
 * @param range the integer range whose values become set members.
 * @return an [ExtensionalSet] containing every integer in [range].
 */
fun mathSetOf(range: IntRange): MathSet<Int> = ExtensionalSet(range.toSet())

internal fun Cardinality.isFinite(): Boolean = this is Cardinality.Finite
