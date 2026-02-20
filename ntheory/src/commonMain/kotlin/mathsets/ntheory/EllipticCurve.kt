package mathsets.ntheory

import mathsets.algebra.Field

/**
 * An elliptic curve over a field K in short Weierstrass form: y² = x³ + ax + b
 *
 * This implementation assumes characteristic(K) ≠ 2, 3 for simplicity.
 *
 * @param K The field type.
 * @property a The coefficient 'a' in the curve equation.
 * @property b The coefficient 'b' in the curve equation.
 */
data class EllipticCurve<K>(
    val a: K,
    val b: K,
    private val field: Field<K>
) {
    init {
        // Verify non-singularity: 4a³ + 27b² ≠ 0
        val two = field.add(field.one, field.one)
        val three = field.add(two, field.one)
        val four = field.multiply(two, two)
        val nine = field.multiply(three, three)
        val twentySeven = field.multiply(three, nine)
        
        val aCubed = field.multiply(a, field.multiply(a, a))
        val bSquared = field.multiply(b, b)
        val discriminantPart = field.add(
            field.multiply(four, aCubed),
            field.multiply(twentySeven, bSquared)
        )
        require(discriminantPart != field.zero) { "Curve must be non-singular (4a³ + 27b² ≠ 0)." }
    }

    /**
     * A point on the elliptic curve, including the point at infinity.
     */
    sealed class Point<out K> {
        /**
         * The point at infinity (identity element).
         */
        object Infinity : Point<Nothing>()

        /**
         * An affine point (x, y) on the curve.
         */
        data class Affine<K>(val x: K, val y: K) : Point<K>()

        override fun toString(): String = when (this) {
            is Infinity -> "O"
            is Affine -> "($x, $y)"
        }
    }

    /**
     * Checks if a point lies on the curve.
     */
    fun isOnCurve(point: Point<K>): Boolean {
        return when (point) {
            is Point.Infinity -> true
            is Point.Affine -> {
                val left = field.multiply(point.y, point.y)
                val xCubed = field.multiply(point.x, field.multiply(point.x, point.x))
                val right = field.add(xCubed, field.add(field.multiply(a, point.x), b))
                left == right
            }
        }
    }

    /**
     * Elliptic curve point addition.
     *
     * Uses the standard chord-and-tangent law.
     */
    fun add(p: Point<K>, q: Point<K>): Point<K> {
        return when (p) {
            is Point.Infinity -> q
            is Point.Affine -> when (q) {
                is Point.Infinity -> p
                is Point.Affine -> addAffine(p, q)
            }
        }
    }

    private fun addAffine(p: Point.Affine<K>, q: Point.Affine<K>): Point<K> {
        if (p.x == q.x) {
            if (p.y == field.negate(q.y)) {
                return Point.Infinity as Point<K> // P + (-P) = O
            } else if (p.y == q.y) {
                // Point doubling
                if (p.y == field.zero) return Point.Infinity as Point<K>
                
                val two = field.add(field.one, field.one)
                val three = field.add(two, field.one)
                
                val num = field.add(field.multiply(three, field.multiply(p.x, p.x)), a)
                val denom = field.multiply(two, p.y)
                val lambda = field.divide(num, denom)
                
                val x3 = field.subtract(
                    field.multiply(lambda, lambda),
                    field.add(p.x, q.x)
                )
                val y3 = field.subtract(
                    field.multiply(lambda, field.subtract(p.x, x3)),
                    p.y
                )
                
                return Point.Affine(x3, y3)
            }
        }
        
        // General case: P ≠ Q
        val num = field.subtract(p.y, q.y)
        val denom = field.subtract(p.x, q.x)
        val lambda = field.divide(num, denom)
        
        val x3 = field.subtract(
            field.multiply(lambda, lambda),
            field.add(p.x, q.x)
        )
        val y3 = field.subtract(
            field.multiply(lambda, field.subtract(p.x, x3)),
            p.y
        )
        
        return Point.Affine(x3, y3)
    }

    /**
     * Scalar multiplication via double-and-add algorithm.
     */
    fun multiply(n: Long, p: Point<K>): Point<K> {
        if (n == 0L || p is Point.Infinity) return Point.Infinity as Point<K>
        if (n < 0L) {
            val negP = when (p) {
                is Point.Infinity -> p
                is Point.Affine -> Point.Affine(p.x, field.negate(p.y))
            }
            return multiply(-n, negP)
        }
        
        var result: Point<K> = Point.Infinity as Point<K>
        var current: Point<K> = p
        var exp = n
        
        while (exp > 0L) {
            if ((exp and 1L) == 1L) {
                result = add(result, current)
            }
            current = add(current, current)
            exp = exp ushr 1
        }
        
        return result
    }

    /**
     * Returns the order of a point (smallest positive n such that n*P = O).
     *
     * Limited search up to [maxOrder] to avoid infinite loops.
     */
    fun pointOrder(p: Point<K>, maxOrder: Long = 10_000): Long? {
        if (p is Point.Infinity) return 1L
        
        var current = p
        for (n in 1L..maxOrder) {
            current = add(current, p)
            if (current is Point.Infinity) return n
        }
        return null
    }

    companion object {
        /**
         * Creates an elliptic curve from coefficients.
         */
        fun <K> of(a: K, b: K, field: Field<K>): EllipticCurve<K> = EllipticCurve(a, b, field)
    }
}
