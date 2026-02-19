package mathsets.polynomial

import mathsets.algebra.CommutativeRing
import mathsets.algebra.Field

/**
 * A univariate polynomial over a ring R, represented as a list of coefficients
 * in ascending order of degree: `coefficients[i]` is the coefficient of x^i.
 *
 * The zero polynomial is represented by an empty coefficient list.
 * Trailing zeros are stripped on construction to maintain a canonical form.
 *
 * @param R The coefficient type.
 * @property coefficients The coefficients in ascending degree order.
 */
data class Polynomial<R>(val coefficients: List<R>) {

    val degree: Int get() = if (coefficients.isEmpty()) -1 else coefficients.size - 1

    fun isZero(): Boolean = coefficients.isEmpty()

    fun leadingCoefficient(): R? = coefficients.lastOrNull()

    operator fun get(index: Int): R? =
        if (index in coefficients.indices) coefficients[index] else null

    override fun toString(): String {
        if (isZero()) return "0"
        return coefficients.mapIndexedNotNull { i, c ->
            val cs = c.toString()
            if (cs == "0") null
            else when (i) {
                0 -> cs
                1 -> if (cs == "1") "x" else "${cs}x"
                else -> if (cs == "1") "x^$i" else "${cs}x^$i"
            }
        }.reversed().joinToString(" + ").ifEmpty { "0" }
    }

    companion object {
        fun <R> zero(): Polynomial<R> = Polynomial(emptyList())

        fun <R> constant(value: R, ring: CommutativeRing<R>): Polynomial<R> =
            if (value == ring.zero) zero() else Polynomial(listOf(value))

        fun <R> monomial(coefficient: R, degree: Int, ring: CommutativeRing<R>): Polynomial<R> {
            if (coefficient == ring.zero) return zero()
            val coeffs = MutableList<R>(degree + 1) { ring.zero }
            coeffs[degree] = coefficient
            return Polynomial(coeffs)
        }
    }
}

/**
 * Provides polynomial arithmetic operations over a commutative ring R.
 *
 * This object acts as the polynomial ring R[x], implementing addition,
 * subtraction, multiplication, evaluation, and (over fields) Euclidean
 * division and GCD.
 *
 * @param R The coefficient type.
 * @property ring The base ring of coefficients.
 */
class PolynomialRing<R>(val ring: CommutativeRing<R>) : CommutativeRing<Polynomial<R>> {

    override val zero: Polynomial<R> = Polynomial.zero()
    override val one: Polynomial<R> = Polynomial(listOf(ring.one))

    private fun normalize(coeffs: List<R>): List<R> {
        var end = coeffs.size
        while (end > 0 && coeffs[end - 1] == ring.zero) end--
        return if (end == coeffs.size) coeffs else coeffs.subList(0, end)
    }

    private fun poly(coeffs: List<R>): Polynomial<R> = Polynomial(normalize(coeffs))

    override fun add(a: Polynomial<R>, b: Polynomial<R>): Polynomial<R> {
        val maxLen = maxOf(a.coefficients.size, b.coefficients.size)
        val result = MutableList(maxLen) { i ->
            val ai = a.coefficients.getOrElse(i) { ring.zero }
            val bi = b.coefficients.getOrElse(i) { ring.zero }
            ring.add(ai, bi)
        }
        return poly(result)
    }

    override fun negate(a: Polynomial<R>): Polynomial<R> =
        Polynomial(a.coefficients.map { ring.negate(it) })

    override fun multiply(a: Polynomial<R>, b: Polynomial<R>): Polynomial<R> {
        if (a.isZero() || b.isZero()) return zero
        val result = MutableList(a.coefficients.size + b.coefficients.size - 1) { ring.zero }
        for (i in a.coefficients.indices) {
            for (j in b.coefficients.indices) {
                result[i + j] = ring.add(result[i + j], ring.multiply(a.coefficients[i], b.coefficients[j]))
            }
        }
        return poly(result)
    }

    fun scalarMultiply(scalar: R, p: Polynomial<R>): Polynomial<R> =
        poly(p.coefficients.map { ring.multiply(scalar, it) })

    fun evaluate(p: Polynomial<R>, x: R): R {
        if (p.isZero()) return ring.zero
        var result = ring.zero
        var power = ring.one
        for (coeff in p.coefficients) {
            result = ring.add(result, ring.multiply(coeff, power))
            power = ring.multiply(power, x)
        }
        return result
    }

    fun x(): Polynomial<R> = Polynomial(listOf(ring.zero, ring.one))

    fun fromCoefficients(vararg coeffs: R): Polynomial<R> = poly(coeffs.toList())
}

/**
 * Provides Euclidean division and GCD for polynomials over a field.
 *
 * @param R The coefficient type (must be a field).
 * @property field The base field.
 */
class FieldPolynomialOps<R>(val field: Field<R>) {
    private val polyRing = PolynomialRing(field)

    fun divideAndRemainder(a: Polynomial<R>, b: Polynomial<R>): Pair<Polynomial<R>, Polynomial<R>> {
        require(!b.isZero()) { "Cannot divide by zero polynomial." }
        if (a.isZero()) return Polynomial.zero<R>() to Polynomial.zero()
        if (a.degree < b.degree) return Polynomial.zero<R>() to a

        val leadB = b.leadingCoefficient()!!
        val leadBInv = field.reciprocal(leadB)

        var remainder = a.coefficients.toMutableList()
        val quotientCoeffs = MutableList(a.degree - b.degree + 1) { field.zero }

        for (i in (a.degree - b.degree) downTo 0) {
            if (remainder.size <= b.degree + i - 1 && remainder.getOrElse(b.degree + i) { field.zero } == field.zero) continue
            val coeff = field.multiply(remainder.getOrElse(b.degree + i) { field.zero }, leadBInv)
            quotientCoeffs[i] = coeff
            for (j in b.coefficients.indices) {
                val idx = i + j
                if (idx < remainder.size) {
                    remainder[idx] = field.subtract(remainder[idx], field.multiply(coeff, b.coefficients[j]))
                }
            }
        }

        val normRemainder = remainder.toMutableList()
        while (normRemainder.isNotEmpty() && normRemainder.last() == field.zero) {
            normRemainder.removeAt(normRemainder.lastIndex)
        }
        val normQuotient = quotientCoeffs.toMutableList()
        while (normQuotient.isNotEmpty() && normQuotient.last() == field.zero) {
            normQuotient.removeAt(normQuotient.lastIndex)
        }

        return Polynomial(normQuotient) to Polynomial(normRemainder)
    }

    fun quotient(a: Polynomial<R>, b: Polynomial<R>): Polynomial<R> = divideAndRemainder(a, b).first

    fun remainder(a: Polynomial<R>, b: Polynomial<R>): Polynomial<R> = divideAndRemainder(a, b).second

    fun gcd(a: Polynomial<R>, b: Polynomial<R>): Polynomial<R> {
        var x = a
        var y = b
        while (!y.isZero()) {
            val r = remainder(x, y)
            x = y
            y = r
        }
        if (x.isZero()) return x
        val leadInv = field.reciprocal(x.leadingCoefficient()!!)
        return polyRing.scalarMultiply(leadInv, x)
    }

    fun add(a: Polynomial<R>, b: Polynomial<R>): Polynomial<R> = polyRing.add(a, b)
    fun multiply(a: Polynomial<R>, b: Polynomial<R>): Polynomial<R> = polyRing.multiply(a, b)
    fun evaluate(p: Polynomial<R>, x: R): R = polyRing.evaluate(p, x)
}
