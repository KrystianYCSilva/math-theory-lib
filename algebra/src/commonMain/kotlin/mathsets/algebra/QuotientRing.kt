package mathsets.algebra

/**
 * The quotient ring R/I, formed by partitioning a ring R by an ideal I.
 *
 * Elements of the quotient ring are cosets `a + I = { a + i | i in I }`.
 * Operations are defined on cosets: `(a+I) + (b+I) = (a+b)+I` and `(a+I)(b+I) = (ab)+I`.
 *
 * @param T The element type of the original ring.
 * @property ring The original ring R.
 * @property ideal The ideal I.
 * @property ringElements The elements of R (finite).
 */
class QuotientRing<T>(
    val ring: Ring<T>,
    val ideal: Ideal<T>,
    private val ringElements: Set<T>
) : Ring<Set<T>> {

    private fun cosetOf(a: T): Set<T> =
        ideal.elements.map { i -> ring.add(a, i) }.toSet()

    val cosets: Set<Set<T>> = ringElements.map { cosetOf(it) }.toSet()

    override val zero: Set<T> = ideal.elements

    override val one: Set<T> = cosetOf(ring.one)

    override fun add(a: Set<T>, b: Set<T>): Set<T> =
        cosetOf(ring.add(a.first(), b.first()))

    override fun negate(a: Set<T>): Set<T> =
        cosetOf(ring.negate(a.first()))

    override fun multiply(a: Set<T>, b: Set<T>): Set<T> =
        cosetOf(ring.multiply(a.first(), b.first()))

    fun order(): Int = cosets.size

    fun elements(): Set<Set<T>> = cosets

    fun isField(): Boolean {
        if (cosets.size <= 1) return false
        return cosets.all { coset ->
            coset == zero || cosets.any { other ->
                multiply(coset, other) == one
            }
        }
    }
}
