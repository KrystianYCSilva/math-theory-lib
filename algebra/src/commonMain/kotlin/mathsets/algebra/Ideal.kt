package mathsets.algebra

/**
 * Represents an ideal I of a ring R.
 *
 * An ideal is a subset I of R such that:
 * 1. I is non-empty.
 * 2. I is closed under addition and additive inverses (I is an additive subgroup).
 * 3. I absorbs multiplication: for all r in R and a in I, both r*a and a*r are in I.
 *
 * @param T The element type.
 * @property ring The ring R.
 * @property elements The elements of the ideal.
 * @property ringElements The elements of R (finite, for verification).
 * @throws IllegalArgumentException if [elements] do not form an ideal.
 */
class Ideal<T>(
    val ring: Ring<T>,
    val elements: Set<T>,
    private val ringElements: Set<T>
) {
    init {
        require(elements.isNotEmpty()) { "Ideal must be non-empty." }
        require(ring.zero in elements) { "Ideal must contain zero." }
        require(elements.all { a ->
            elements.all { b -> ring.add(a, b) in elements }
        }) { "Ideal must be closed under addition." }
        require(elements.all { ring.negate(it) in elements }) {
            "Ideal must be closed under negation."
        }
        require(ringElements.all { r ->
            elements.all { a ->
                ring.multiply(r, a) in elements && ring.multiply(a, r) in elements
            }
        }) { "Ideal must absorb multiplication." }
    }

    fun isPrincipal(): Boolean =
        elements.any { generator ->
            val generated = ringElements.map { r -> ring.multiply(r, generator) }.toSet()
            elements.all { it in generated }
        }

    fun isPrime(): Boolean =
        elements != ringElements &&
            ringElements.all { a ->
                ringElements.all { b ->
                    if (ring.multiply(a, b) in elements) {
                        a in elements || b in elements
                    } else true
                }
            }

    fun isMaximal(): Boolean {
        if (elements == ringElements) return false
        val allSubsets = ringElements.subsets()
        return allSubsets.none { candidate ->
            candidate != elements &&
                candidate != ringElements &&
                elements.all { it in candidate } &&
                isIdealCandidate(candidate)
        }
    }

    private fun isIdealCandidate(candidate: Set<T>): Boolean {
        if (ring.zero !in candidate) return false
        if (!candidate.all { a -> candidate.all { b -> ring.add(a, b) in candidate } }) return false
        if (!candidate.all { ring.negate(it) in candidate }) return false
        return ringElements.all { r ->
            candidate.all { a ->
                ring.multiply(r, a) in candidate && ring.multiply(a, r) in candidate
            }
        }
    }
}

private fun <T> Set<T>.subsets(): Set<Set<T>> {
    val list = this.toList()
    val result = mutableSetOf<Set<T>>()
    val total = 1 shl list.size
    for (mask in 0 until total) {
        val subset = mutableSetOf<T>()
        for (i in list.indices) {
            if ((mask and (1 shl i)) != 0) subset.add(list[i])
        }
        result.add(subset)
    }
    return result
}
