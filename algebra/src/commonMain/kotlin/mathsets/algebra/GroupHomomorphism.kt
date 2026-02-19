package mathsets.algebra

/**
 * A group homomorphism from group G to group H: a function that preserves
 * the group operation.
 *
 * Law: `f(op_G(a, b)) == op_H(f(a), f(b))` for all a, b in G.
 *
 * @param A The element type of the domain group.
 * @param B The element type of the codomain group.
 * @property domain The domain group G.
 * @property codomain The codomain group H.
 * @property map The mapping function.
 */
class GroupHomomorphism<A, B>(
    val domain: Group<A>,
    val codomain: Group<B>,
    val map: (A) -> B
) {
    operator fun invoke(a: A): B = map(a)

    fun verifyHomomorphism(elements: Set<A>): Boolean =
        elements.all { a ->
            elements.all { b ->
                map(domain.op(a, b)) == codomain.op(map(a), map(b))
            }
        }

    fun kernel(domainElements: Set<A>): Set<A> =
        domainElements.filter { map(it) == codomain.identity }.toSet()

    fun image(domainElements: Set<A>): Set<B> =
        domainElements.map { map(it) }.toSet()

    fun isInjective(domainElements: Set<A>): Boolean {
        val k = kernel(domainElements)
        return k.size == 1 && k.single() == domain.identity
    }

    fun isSurjective(domainElements: Set<A>, codomainElements: Set<B>): Boolean =
        image(domainElements) == codomainElements
}

/**
 * A ring homomorphism from ring R to ring S: a function that preserves
 * both addition and multiplication.
 *
 * Laws:
 * - `f(add_R(a, b)) == add_S(f(a), f(b))`
 * - `f(multiply_R(a, b)) == multiply_S(f(a), f(b))`
 * - `f(one_R) == one_S`
 *
 * @param A The element type of the domain ring.
 * @param B The element type of the codomain ring.
 * @property domain The domain ring R.
 * @property codomain The codomain ring S.
 * @property map The mapping function.
 */
class RingHomomorphism<A, B>(
    val domain: Ring<A>,
    val codomain: Ring<B>,
    val map: (A) -> B
) {
    operator fun invoke(a: A): B = map(a)

    fun verifyHomomorphism(elements: Set<A>): Boolean =
        map(domain.one) == codomain.one &&
            elements.all { a ->
                elements.all { b ->
                    map(domain.add(a, b)) == codomain.add(map(a), map(b)) &&
                        map(domain.multiply(a, b)) == codomain.multiply(map(a), map(b))
                }
            }

    fun kernel(domainElements: Set<A>): Set<A> =
        domainElements.filter { map(it) == codomain.zero }.toSet()

    fun image(domainElements: Set<A>): Set<B> =
        domainElements.map { map(it) }.toSet()
}
