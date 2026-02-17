package mathsets.set

object SetAlgebra {
    fun <T> isUnionCommutative(a: MathSet<T>, b: MathSet<T>, universe: MathSet<T>): Boolean =
        sameMembership(a union b, b union a, universe)

    fun <T> isIntersectionCommutative(a: MathSet<T>, b: MathSet<T>, universe: MathSet<T>): Boolean =
        sameMembership(a intersect b, b intersect a, universe)

    fun <T> isUnionAssociative(a: MathSet<T>, b: MathSet<T>, c: MathSet<T>, universe: MathSet<T>): Boolean =
        sameMembership((a union b) union c, a union (b union c), universe)

    fun <T> isDeMorganForUnion(a: MathSet<T>, b: MathSet<T>, universe: MathSet<T>): Boolean =
        sameMembership(
            (a union b).complement(universe),
            a.complement(universe) intersect b.complement(universe),
            universe
        )

    fun <T> isIdempotentUnion(a: MathSet<T>, universe: MathSet<T>): Boolean =
        sameMembership(a union a, a, universe)

    fun <T> hasIdentityUnion(a: MathSet<T>, universe: MathSet<T>): Boolean =
        sameMembership(a union MathSet.empty<T>(), a, universe)

    fun <T> hasAbsorption(a: MathSet<T>, b: MathSet<T>, universe: MathSet<T>): Boolean =
        sameMembership(a union (a intersect b), a, universe)

    fun <T> hasInvolution(a: MathSet<T>, universe: MathSet<T>): Boolean =
        sameMembership(a.complement(universe).complement(universe), a, universe)

    fun <T> extensionalityHolds(a: MathSet<T>, b: MathSet<T>, universe: MathSet<T>): Boolean {
        val same = universe.elements().all { x -> (x in a) == (x in b) }
        return !same || ((a isSubsetOf b) && (b isSubsetOf a))
    }

    private fun <T> sameMembership(left: MathSet<T>, right: MathSet<T>, universe: MathSet<T>): Boolean =
        universe.elements().all { x -> (x in left) == (x in right) }
}
