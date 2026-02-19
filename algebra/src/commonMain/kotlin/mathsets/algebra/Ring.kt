package mathsets.algebra

/**
 * A semiring: a set with two binary operations (addition and multiplication) where:
 * - (T, +, 0) is a commutative monoid.
 * - (T, *, 1) is a monoid.
 * - Multiplication distributes over addition.
 * - 0 is an annihilator for multiplication: `a * 0 == 0 == 0 * a`.
 *
 * @param T The element type.
 */
interface Semiring<T> {
    val zero: T
    val one: T
    fun add(a: T, b: T): T
    fun multiply(a: T, b: T): T
}

/**
 * A ring: a semiring where (T, +, 0) is an abelian group.
 *
 * Laws:
 * - (T, +, 0) is an abelian group.
 * - (T, *, 1) is a monoid.
 * - Distributivity: `a * (b + c) == a*b + a*c` and `(a + b) * c == a*c + b*c`.
 *
 * @param T The element type.
 */
interface Ring<T> : Semiring<T> {
    fun negate(a: T): T
    fun subtract(a: T, b: T): T = add(a, negate(b))

    fun additiveGroup(): AdditiveAbelianGroup<T> {
        val ring = this
        return object : AdditiveAbelianGroup<T> {
            override val identity: T get() = ring.zero
            override val zero: T get() = ring.zero
            override fun add(a: T, b: T): T = ring.add(a, b)
            override fun negate(a: T): T = ring.negate(a)
            override fun subtract(a: T, b: T): T = ring.add(a, ring.negate(b))
            override fun op(a: T, b: T): T = ring.add(a, b)
            override fun inverse(a: T): T = ring.negate(a)
        }
    }

    fun multiplicativeMonoid(): MultiplicativeMonoid<T> {
        val ring = this
        return object : MultiplicativeMonoid<T> {
            override val identity: T get() = ring.one
            override val one: T get() = ring.one
            override fun multiply(a: T, b: T): T = ring.multiply(a, b)
            override fun op(a: T, b: T): T = ring.multiply(a, b)
        }
    }
}

/**
 * A commutative ring: a [Ring] whose multiplication is commutative.
 *
 * Law: `multiply(a, b) == multiply(b, a)` for all a, b.
 *
 * @param T The element type.
 */
interface CommutativeRing<T> : Ring<T>

/**
 * An integral domain: a commutative ring with no zero divisors.
 *
 * Law: `multiply(a, b) == zero` implies `a == zero` or `b == zero`.
 *
 * @param T The element type.
 */
interface IntegralDomain<T> : CommutativeRing<T>

/**
 * A Euclidean domain: an integral domain equipped with a Euclidean function
 * and a division algorithm.
 *
 * Law: For all a and b != 0, there exist q, r such that `a == add(multiply(q, b), r)`
 * and either `r == zero` or `euclideanFunction(r) < euclideanFunction(b)`.
 *
 * @param T The element type.
 */
interface EuclideanDomain<T> : IntegralDomain<T> {
    fun euclideanFunction(a: T): Long
    fun divideAndRemainder(a: T, b: T): Pair<T, T>
    fun quotient(a: T, b: T): T = divideAndRemainder(a, b).first
    fun remainder(a: T, b: T): T = divideAndRemainder(a, b).second

    fun gcd(a: T, b: T): T {
        var x = a
        var y = b
        while (y != zero) {
            val r = remainder(x, y)
            x = y
            y = r
        }
        return x
    }
}

/**
 * A field: a commutative ring where every non-zero element has a multiplicative inverse.
 *
 * Equivalently, (T \ {0}, *, 1) is an abelian group.
 *
 * @param T The element type.
 */
interface Field<T> : IntegralDomain<T> {
    fun reciprocal(a: T): T
    fun divide(a: T, b: T): T = multiply(a, reciprocal(b))
}

/**
 * An ordered field: a [Field] equipped with a total order compatible with the field operations.
 *
 * Laws:
 * - If `a <= b` then `a + c <= b + c`.
 * - If `0 <= a` and `0 <= b` then `0 <= a * b`.
 *
 * @param T The element type.
 */
interface OrderedField<T> : Field<T> {
    fun compare(a: T, b: T): Int
}
