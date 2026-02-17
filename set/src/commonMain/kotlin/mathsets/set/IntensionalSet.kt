package mathsets.set

import mathsets.kernel.Cardinality
import mathsets.kernel.NaturalNumber

class IntensionalSet<T>(
    private val domain: MathSet<T>,
    private val predicate: (T) -> Boolean
) : MathSet<T> {
    private var cachedMaterialization: ExtensionalSet<T>? = null

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

