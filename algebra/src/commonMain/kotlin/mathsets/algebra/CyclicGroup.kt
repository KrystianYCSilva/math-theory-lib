package mathsets.algebra

/**
 * The cyclic group Z/nZ of integers modulo n under addition.
 *
 * Elements are represented as [Int] values in `{0, 1, ..., n-1}`.
 * The generator is `1`.
 *
 * @property order The order of the group (must be at least 1).
 * @throws IllegalArgumentException if [order] is less than 1.
 */
class CyclicGroup(val order: Int) : AbelianGroup<Int> {
    init {
        require(order >= 1) { "Order must be at least 1." }
    }

    override val identity: Int = 0

    override fun op(a: Int, b: Int): Int = ((a % order + b % order) % order + order) % order

    override fun inverse(a: Int): Int = if (a == 0) 0 else order - (a.mod(order))

    val generator: Int = if (order == 1) 0 else 1

    fun elements(): Set<Int> = (0 until order).toSet()

    fun orderOf(element: Int): Int {
        require(element in 0 until order) { "Element must be in {0, ..., ${order - 1}}." }
        if (element == 0) return 1
        var current = element
        var count = 1
        while (current != 0) {
            current = op(current, element)
            count++
        }
        return count
    }
}
