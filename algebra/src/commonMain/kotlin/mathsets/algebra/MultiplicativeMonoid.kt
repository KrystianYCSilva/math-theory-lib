package mathsets.algebra

/**
 * A multiplicative monoid: a [Monoid] with `*` syntax and `one` as the identity.
 *
 * @param T The element type.
 */
interface MultiplicativeMonoid<T> : Monoid<T> {
    val one: T get() = identity
    fun multiply(a: T, b: T): T
    override fun op(a: T, b: T): T = multiply(a, b)
}

/**
 * A multiplicative group: a [Group] with `*`, `inverse`, and `one` syntax.
 *
 * Every non-identity element has a multiplicative inverse.
 *
 * @param T The element type.
 */
interface MultiplicativeGroup<T> : Group<T>, MultiplicativeMonoid<T> {
    fun reciprocal(a: T): T
    override fun inverse(a: T): T = reciprocal(a)
}
