package mathsets.set

/**
 * Verifiers for classical set-algebraic identities.
 *
 * Each function checks whether a particular identity (commutativity, associativity,
 * De Morgan's law, idempotence, etc.) holds for concrete set instances over a given
 * finite [universe] by comparing membership element-by-element.
 *
 * These are primarily used in property-based tests to validate the correctness of
 * [MathSet] implementations.
 */
object SetAlgebra {
    /**
     * Checks whether `A ∪ B = B ∪ A` over [universe].
     */
    fun <T> isUnionCommutative(a: MathSet<T>, b: MathSet<T>, universe: MathSet<T>): Boolean =
        sameMembership(a union b, b union a, universe)

    /**
     * Checks whether `A ∩ B = B ∩ A` over [universe].
     */
    fun <T> isIntersectionCommutative(a: MathSet<T>, b: MathSet<T>, universe: MathSet<T>): Boolean =
        sameMembership(a intersect b, b intersect a, universe)

    /**
     * Checks whether `(A ∪ B) ∪ C = A ∪ (B ∪ C)` over [universe].
     */
    fun <T> isUnionAssociative(a: MathSet<T>, b: MathSet<T>, c: MathSet<T>, universe: MathSet<T>): Boolean =
        sameMembership((a union b) union c, a union (b union c), universe)

    /**
     * Checks De Morgan's law for union: `(A ∪ B)' = A' ∩ B'` over [universe].
     */
    fun <T> isDeMorganForUnion(a: MathSet<T>, b: MathSet<T>, universe: MathSet<T>): Boolean =
        sameMembership(
            (a union b).complement(universe),
            a.complement(universe) intersect b.complement(universe),
            universe
        )

    /**
     * Checks idempotence of union: `A ∪ A = A` over [universe].
     */
    fun <T> isIdempotentUnion(a: MathSet<T>, universe: MathSet<T>): Boolean =
        sameMembership(a union a, a, universe)

    /**
     * Checks the identity law for union: `A ∪ ∅ = A` over [universe].
     */
    fun <T> hasIdentityUnion(a: MathSet<T>, universe: MathSet<T>): Boolean =
        sameMembership(a union MathSet.empty<T>(), a, universe)

    /**
     * Checks the absorption law: `A ∪ (A ∩ B) = A` over [universe].
     */
    fun <T> hasAbsorption(a: MathSet<T>, b: MathSet<T>, universe: MathSet<T>): Boolean =
        sameMembership(a union (a intersect b), a, universe)

    /**
     * Checks involution of complement: `(A')' = A` over [universe].
     */
    fun <T> hasInvolution(a: MathSet<T>, universe: MathSet<T>): Boolean =
        sameMembership(a.complement(universe).complement(universe), a, universe)

    /**
     * Checks the Axiom of Extensionality: two sets with the same members are equal.
     *
     * Returns `true` when same-membership implies mutual subset inclusion.
     */
    fun <T> extensionalityHolds(a: MathSet<T>, b: MathSet<T>, universe: MathSet<T>): Boolean {
        val same = universe.elements().all { x -> (x in a) == (x in b) }
        return !same || ((a isSubsetOf b) && (b isSubsetOf a))
    }

    /**
     * Compares two sets element-by-element over a [universe], returning `true` if every
     * element of [universe] has the same membership status in both [left] and [right].
     */
    private fun <T> sameMembership(left: MathSet<T>, right: MathSet<T>, universe: MathSet<T>): Boolean =
        universe.elements().all { x -> (x in left) == (x in right) }
}
