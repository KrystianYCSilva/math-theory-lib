package mathsets.algebra

/**
 * An additive monoid: a [Monoid] with `+` syntax and `zero` as the identity.
 *
 * @param T The element type.
 */
interface AdditiveMonoid<T> : Monoid<T> {
    val zero: T get() = identity
    fun add(a: T, b: T): T
    override fun op(a: T, b: T): T = add(a, b)
}

/**
 * An additive group: a [Group] with `+`, `-`, and `zero` syntax.
 *
 * @param T The element type.
 */
interface AdditiveGroup<T> : Group<T>, AdditiveMonoid<T> {
    fun negate(a: T): T
    fun subtract(a: T, b: T): T = add(a, negate(b))
    override fun inverse(a: T): T = negate(a)
}

/**
 * An abelian additive group: an [AbelianGroup] with additive syntax.
 *
 * @param T The element type.
 */
interface AdditiveAbelianGroup<T> : AbelianGroup<T>, AdditiveGroup<T>
