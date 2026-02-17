package mathsets.set

import mathsets.kernel.Cardinality
import mathsets.kernel.NaturalNumber

/**
 * A mathematical set defined by a [predicate] over a [domain] (the intensional mode).
 *
 * Corresponds to the set-builder notation `{ x ∈ domain | predicate(x) }` and directly
 * models the ZFC Axiom of Separation (Restricted Comprehension).
 *
 * Membership is evaluated lazily: an element belongs to this set if and only if it
 * belongs to the [domain] **and** satisfies the [predicate].
 *
 * @param T the element type.
 * @param domain the parent set from which elements are drawn.
 * @param predicate the membership criterion applied to domain elements.
 */
class IntensionalSet<T>(
    private val domain: MathSet<T>,
    private val predicate: (T) -> Boolean
) : MathSet<T> {
    private var cachedMaterialization: ExtensionalSet<T>? = null

    /**
     * The cardinality of this set.
     *
     * If the [domain] is finite the cardinality is computed eagerly (and cached after
     * the first materialization); otherwise [Cardinality.Unknown] is returned.
     */
    override val cardinality: Cardinality
        get() = when (domain.cardinality) {
            is Cardinality.Finite -> {
                cachedMaterialization?.cardinality ?: Cardinality.Finite(
                    NaturalNumber.of(elements().count())
                )
            }
            else -> Cardinality.Unknown
        }

    override fun contains(element: T): Boolean = (element in domain) && predicate(element)

    override fun elements(): Sequence<T> = domain.elements().filter(predicate)

    /**
     * Materializes this intensional set into an [ExtensionalSet].
     *
     * The result is cached so that subsequent calls return the same instance.
     *
     * @throws InfiniteMaterializationException if the domain is not finite.
     */
    override fun materialize(): ExtensionalSet<T> {
        cachedMaterialization?.let { return it }
        if (!domain.cardinality.isFinite()) {
            throw InfiniteMaterializationException("Cannot materialize intensional set over non-finite domain")
        }
        return ExtensionalSet(elements().toSet()).also { cachedMaterialization = it }
    }

    override fun union(other: MathSet<T>): MathSet<T> = UnionSetView(this, other)

    override fun intersect(other: MathSet<T>): MathSet<T> = IntersectionSetView(this, other)
}
