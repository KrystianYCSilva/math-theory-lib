package mathsets.algebra

/**
 * Verifies and represents a subgroup of a given group.
 *
 * A subset H of a group G is a subgroup if:
 * 1. H is non-empty.
 * 2. The identity of G is in H.
 * 3. H is closed under the group operation.
 * 4. H is closed under taking inverses.
 *
 * @param T The element type.
 * @property group The parent group.
 * @property elements The elements of the subgroup.
 * @throws IllegalArgumentException if [elements] do not form a subgroup of [group].
 */
class Subgroup<T>(
    val group: Group<T>,
    val elements: Set<T>
) : Group<T> by group {

    init {
        require(elements.isNotEmpty()) { "Subgroup must be non-empty." }
        require(group.identity in elements) { "Subgroup must contain the identity." }
        require(elements.all { a ->
            elements.all { b -> group.op(a, b) in elements }
        }) { "Subgroup must be closed under the group operation." }
        require(elements.all { group.inverse(it) in elements }) {
            "Subgroup must be closed under inverses."
        }
    }

    fun order(): Int = elements.size

    fun isNormal(parentElements: Set<T>): Boolean =
        parentElements.all { g ->
            elements.all { h ->
                group.op(group.op(g, h), group.inverse(g)) in elements
            }
        }

    fun leftCoset(g: T): Set<T> = elements.map { group.op(g, it) }.toSet()

    fun rightCoset(g: T): Set<T> = elements.map { group.op(it, g) }.toSet()

    fun index(parentElements: Set<T>): Int {
        val cosets = mutableSetOf<Set<T>>()
        for (g in parentElements) {
            cosets.add(leftCoset(g))
        }
        return cosets.size
    }
}
