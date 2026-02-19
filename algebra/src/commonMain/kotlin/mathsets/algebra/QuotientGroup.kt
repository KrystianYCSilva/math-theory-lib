package mathsets.algebra

/**
 * The quotient group G/N, formed by partitioning G into cosets of a normal subgroup N.
 *
 * Elements of the quotient group are left cosets `gN = { g * n | n in N }`, represented
 * as [Set]s. The operation is coset multiplication: `(gN)(hN) = (gh)N`.
 *
 * @param T The element type of the parent group.
 * @property group The parent group G.
 * @property normalSubgroup The normal subgroup N.
 * @property parentElements The elements of the parent group (finite).
 * @throws IllegalArgumentException if [normalSubgroup] is not normal in [group].
 */
class QuotientGroup<T>(
    val group: Group<T>,
    val normalSubgroup: Subgroup<T>,
    val parentElements: Set<T>
) : Group<Set<T>> {

    init {
        require(normalSubgroup.isNormal(parentElements)) {
            "Subgroup must be normal to form a quotient group."
        }
    }

    private val cosetMap: Map<T, Set<T>> = parentElements.associateWith { g ->
        normalSubgroup.elements.map { n -> group.op(g, n) }.toSet()
    }

    val cosets: Set<Set<T>> = cosetMap.values.toSet()

    override val identity: Set<T> = normalSubgroup.elements

    override fun op(a: Set<T>, b: Set<T>): Set<T> {
        val repA = a.first()
        val repB = b.first()
        val product = group.op(repA, repB)
        return cosetOf(product)
    }

    override fun inverse(a: Set<T>): Set<T> {
        val rep = a.first()
        return cosetOf(group.inverse(rep))
    }

    fun cosetOf(element: T): Set<T> =
        normalSubgroup.elements.map { n -> group.op(element, n) }.toSet()

    fun order(): Int = cosets.size

    fun elements(): Set<Set<T>> = cosets
}
