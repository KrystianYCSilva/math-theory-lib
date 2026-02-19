package mathsets.galois

import mathsets.algebra.Field
import mathsets.algebra.ZpField
import mathsets.polynomial.FieldPolynomialOps
import mathsets.polynomial.Polynomial
import mathsets.polynomial.PolynomialRing

/**
 * Utilities for cyclotomic polynomials and cyclotomic field extensions.
 *
 * The n-th cyclotomic polynomial Phi_n(x) is the minimal polynomial of
 * primitive n-th roots of unity over Q. Its degree is phi(n) (Euler's totient).
 *
 * Over finite fields GF(p) with gcd(n, p) = 1, cyclotomic polynomials
 * factor into irreducible polynomials of equal degree, and the splitting
 * field of x^n - 1 is GF(p^d) where d = ord_n(p) (the multiplicative
 * order of p modulo n).
 */
object CyclotomicPolynomials {

    /**
     * Computes Euler's totient function phi(n).
     *
     * @param n A positive integer.
     * @return The number of integers in {1, ..., n} coprime to n.
     */
    fun eulerTotient(n: Int): Int {
        require(n >= 1) { "n must be >= 1." }
        var result = n
        var temp = n
        var p = 2
        while (p * p <= temp) {
            if (temp % p == 0) {
                while (temp % p == 0) temp /= p
                result -= result / p
            }
            p++
        }
        if (temp > 1) result -= result / temp
        return result
    }

    /**
     * Computes the n-th cyclotomic polynomial Phi_n(x) over a field.
     *
     * Uses the recurrence: x^n - 1 = product of Phi_d(x) for d | n.
     * So Phi_n(x) = (x^n - 1) / product of Phi_d(x) for d | n, d != n.
     *
     * @param n The index of the cyclotomic polynomial (must be >= 1).
     * @param field The base field.
     * @return The n-th cyclotomic polynomial over the given field.
     */
    fun <K> cyclotomicPolynomial(n: Int, field: Field<K>): Polynomial<K> {
        require(n >= 1) { "n must be >= 1." }
        val polyRing = PolynomialRing(field)
        val polyOps = FieldPolynomialOps(field)

        val xNMinus1 = xPowerMinusOne(n, field)

        val divisors = divisors(n).filter { it != n }.sorted()

        var product = polyRing.one
        for (d in divisors) {
            val phiD = cyclotomicPolynomial(d, field)
            product = polyRing.multiply(product, phiD)
        }

        return polyOps.quotient(xNMinus1, product)
    }

    /**
     * Computes x^n - 1 over a field.
     *
     * @param n The exponent.
     * @param field The base field.
     * @return The polynomial x^n - 1.
     */
    fun <K> xPowerMinusOne(n: Int, field: Field<K>): Polynomial<K> {
        val coeffs = MutableList(n + 1) { field.zero }
        coeffs[0] = field.negate(field.one)
        coeffs[n] = field.one
        return Polynomial(coeffs)
    }

    /**
     * Returns all positive divisors of n.
     *
     * @param n A positive integer.
     * @return A sorted list of divisors.
     */
    fun divisors(n: Int): List<Int> {
        val result = mutableListOf<Int>()
        var i = 1
        while (i * i <= n) {
            if (n % i == 0) {
                result.add(i)
                if (i != n / i) result.add(n / i)
            }
            i++
        }
        return result.sorted()
    }

    /**
     * Computes the multiplicative order of a modulo n.
     * That is, the smallest positive integer d such that a^d = 1 (mod n).
     *
     * @param a The base (must be coprime to n).
     * @param n The modulus (must be >= 2).
     * @return The multiplicative order, or -1 if gcd(a, n) != 1.
     */
    fun multiplicativeOrder(a: Int, n: Int): Int {
        if (gcd(((a % n) + n) % n, n) != 1) return -1
        var power = ((a.toLong() % n) + n) % n
        for (d in 1..n) {
            if (power.toInt() == 1) return d
            power = power * (((a.toLong() % n) + n) % n) % n
        }
        return -1
    }

    private fun gcd(a: Int, b: Int): Int {
        var x = a
        var y = b
        while (y != 0) {
            val r = x % y
            x = y
            y = r
        }
        return x
    }
}

/**
 * Constructs the cyclotomic field extension for the n-th roots of unity
 * over a finite field GF(p).
 *
 * The splitting field of x^n - 1 over GF(p) (when gcd(n, p) = 1) is
 * GF(p^d) where d = ord_n(p), the multiplicative order of p modulo n.
 *
 * @property n The root of unity index.
 * @property prime The characteristic of the base field.
 * @throws IllegalArgumentException if gcd(n, prime) != 1.
 */
class CyclotomicFieldExtension(val n: Int, val prime: Int) {

    init {
        require(n >= 1) { "n must be >= 1." }
        require(gcd(n, prime) == 1) { "gcd(n, p) must be 1 for cyclotomic extensions." }
    }

    private val baseField = ZpField(prime)

    /**
     * The degree of the extension [GF(p^d) : GF(p)].
     */
    val extensionDegree: Int = CyclotomicPolynomials.multiplicativeOrder(prime, n)

    /**
     * The finite field GF(p^d) that serves as the splitting field of x^n - 1.
     */
    val splittingField: FiniteField by lazy {
        FiniteField(prime, extensionDegree)
    }

    /**
     * The n-th cyclotomic polynomial over GF(p).
     */
    val cyclotomicPolynomial: Polynomial<Int> by lazy {
        CyclotomicPolynomials.cyclotomicPolynomial(n, baseField)
    }

    /**
     * Euler's totient phi(n) = degree of the cyclotomic polynomial.
     */
    val totient: Int = CyclotomicPolynomials.eulerTotient(n)

    /**
     * Finds all n-th roots of unity in the splitting field.
     *
     * @return A list of elements omega such that omega^n = 1.
     */
    fun rootsOfUnity(): List<ExtensionElement<Int>> {
        val field = splittingField
        val allElements = field.elements()
        return allElements.filter { elem ->
            field.power(elem, n.toLong()) == field.one
        }
    }

    /**
     * Finds all primitive n-th roots of unity in the splitting field.
     *
     * A primitive n-th root of unity omega satisfies omega^n = 1 and
     * omega^k != 1 for 0 < k < n.
     *
     * @return A list of primitive n-th roots of unity.
     */
    fun primitiveRootsOfUnity(): List<ExtensionElement<Int>> {
        val field = splittingField
        val allElements = field.elements()
        return allElements.filter { elem ->
            if (field.power(elem, n.toLong()) != field.one) false
            else (1 until n).all { k -> field.power(elem, k.toLong()) != field.one }
        }
    }

    private fun gcd(a: Int, b: Int): Int {
        var x = a
        var y = b
        while (y != 0) {
            val r = x % y
            x = y
            y = r
        }
        return x
    }
}
