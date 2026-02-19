package mathsets.algebra

/**
 * A magma: a set equipped with a single closed binary operation.
 *
 * This is the most primitive algebraic structure. The only requirement is that
 * the operation produces an element of the same type (closure).
 *
 * Following the context-oriented programming pattern, the algebraic operation
 * lives in the context (this interface), not on the elements. This allows the
 * same type to participate in multiple algebraic structures without conflict.
 *
 * @param T The element type.
 */
interface Magma<T> {
    fun op(a: T, b: T): T
}

/**
 * A semigroup: a magma whose operation is associative.
 *
 * Law: `op(op(a, b), c) == op(a, op(b, c))` for all a, b, c.
 *
 * @param T The element type.
 */
interface Semigroup<T> : Magma<T>

/**
 * A monoid: a semigroup with an identity element.
 *
 * Laws:
 * - Associativity (inherited from [Semigroup]).
 * - Left identity: `op(identity, a) == a` for all a.
 * - Right identity: `op(a, identity) == a` for all a.
 *
 * @param T The element type.
 */
interface Monoid<T> : Semigroup<T> {
    val identity: T
}

/**
 * A group: a monoid where every element has an inverse.
 *
 * Laws:
 * - Associativity, identity (inherited from [Monoid]).
 * - Left inverse: `op(inverse(a), a) == identity` for all a.
 * - Right inverse: `op(a, inverse(a)) == identity` for all a.
 *
 * @param T The element type.
 */
interface Group<T> : Monoid<T> {
    fun inverse(a: T): T
}

/**
 * An abelian (commutative) group: a group whose operation is commutative.
 *
 * Laws:
 * - All [Group] laws.
 * - Commutativity: `op(a, b) == op(b, a)` for all a, b.
 *
 * @param T The element type.
 */
interface AbelianGroup<T> : Group<T>
