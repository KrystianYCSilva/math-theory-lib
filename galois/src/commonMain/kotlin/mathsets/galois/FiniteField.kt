package mathsets.galois

import mathsets.algebra.Field
import mathsets.algebra.ZpField
import mathsets.polynomial.FieldPolynomialOps
import mathsets.polynomial.Polynomial
import mathsets.polynomial.PolynomialRing

/**
 * A finite field GF(p^n) constructed as (Z/pZ)[x]/(f(x)), where f(x)
 * is an irreducible polynomial of degree n over Z/pZ.
 *
 * When n = 1, this reduces to Z/pZ (the prime field).
 *
 * Elements are represented as [ExtensionElement]<Int>, i.e., polynomials
 * of degree < n with coefficients in {0, 1, ..., p-1}.
 *
 * @property prime The characteristic p (must be prime).
 * @property exponent The exponent n (must be >= 1).
 * @property irreducible The irreducible polynomial of degree n over GF(p).
 *   If not provided, a default irreducible polynomial is selected.
 * @throws IllegalArgumentException if p is not prime, n < 1, or the polynomial
 *   is not of degree n.
 */
class FiniteField(
    val prime: Int,
    val exponent: Int,
    irreducible: Polynomial<Int>? = null
) : Field<ExtensionElement<Int>> {

    private val primeField: ZpField = ZpField(prime)

    /**
     * The irreducible polynomial defining this field extension.
     */
    val irreduciblePolynomial: Polynomial<Int> = irreducible ?: findIrreducible(prime, exponent)

    private val extension: FieldExtension<Int> = FieldExtension(primeField, irreduciblePolynomial)

    init {
        require(exponent >= 1) { "Exponent must be >= 1." }
        require(irreduciblePolynomial.degree == exponent) {
            "Irreducible polynomial must have degree $exponent, got ${irreduciblePolynomial.degree}."
        }
    }

    /**
     * The order of this field: p^n.
     */
    val order: Long = pow(prime.toLong(), exponent)

    /**
     * The characteristic of this field: p.
     */
    val characteristic: Int = prime

    override val zero: ExtensionElement<Int> = extension.zero
    override val one: ExtensionElement<Int> = extension.one

    /**
     * The generator element alpha (a root of the irreducible polynomial).
     */
    val alpha: ExtensionElement<Int> = extension.alpha

    override fun add(a: ExtensionElement<Int>, b: ExtensionElement<Int>): ExtensionElement<Int> =
        extension.add(a, b)

    override fun negate(a: ExtensionElement<Int>): ExtensionElement<Int> =
        extension.negate(a)

    override fun multiply(a: ExtensionElement<Int>, b: ExtensionElement<Int>): ExtensionElement<Int> =
        extension.multiply(a, b)

    override fun reciprocal(a: ExtensionElement<Int>): ExtensionElement<Int> =
        extension.reciprocal(a)

    /**
     * Embeds a prime field element into GF(p^n).
     *
     * @param k An element of Z/pZ.
     * @return The corresponding element in GF(p^n).
     */
    fun embed(k: Int): ExtensionElement<Int> = extension.embed(primeField.add(k, 0))

    /**
     * Returns all elements of this finite field.
     *
     * @return A set of all p^n elements.
     */
    fun elements(): Set<ExtensionElement<Int>> =
        extension.elements(primeField.elements())

    /**
     * Computes the Frobenius endomorphism: x -> x^p.
     *
     * The Frobenius is a field automorphism of GF(p^n) that fixes GF(p).
     * It generates the Galois group Gal(GF(p^n)/GF(p)) which is cyclic of order n.
     *
     * @param a The element to apply the Frobenius to.
     * @return a^p.
     */
    fun frobenius(a: ExtensionElement<Int>): ExtensionElement<Int> = power(a, prime.toLong())

    /**
     * Computes a^k in this field using repeated squaring.
     *
     * @param a The base element.
     * @param k The exponent (non-negative).
     * @return a^k.
     */
    fun power(a: ExtensionElement<Int>, k: Long): ExtensionElement<Int> {
        require(k >= 0) { "Exponent must be non-negative." }
        if (k == 0L) return one
        var result = one
        var base = a
        var exp = k
        while (exp > 0) {
            if (exp % 2 == 1L) result = multiply(result, base)
            base = multiply(base, base)
            exp /= 2
        }
        return result
    }

    /**
     * Checks if an element is a generator (primitive element) of the
     * multiplicative group GF(p^n)*.
     *
     * @param a The element to test.
     * @return `true` if a generates the cyclic group of order p^n - 1.
     */
    fun isPrimitiveElement(a: ExtensionElement<Int>): Boolean {
        if (a == zero) return false
        val groupOrder = order - 1
        var current = a
        for (i in 1 until groupOrder) {
            if (current == one) return false
            current = multiply(current, a)
        }
        return current == one
    }

    /**
     * Finds a primitive element (generator of the multiplicative group).
     *
     * @return A primitive element of GF(p^n)*.
     */
    fun primitiveElement(): ExtensionElement<Int> =
        elements().first { it != zero && isPrimitiveElement(it) }

    companion object {
        private fun pow(base: Long, exp: Int): Long {
            var result = 1L
            repeat(exp) { result *= base }
            return result
        }

        internal fun findIrreducible(p: Int, n: Int): Polynomial<Int> {
            if (n == 1) return Polynomial(listOf(0, 1))

            val field = ZpField(p)
            val polyOps = FieldPolynomialOps(field)
            val polyRing = PolynomialRing(field)

            fun isIrreducible(poly: Polynomial<Int>): Boolean {
                if (poly.degree < 1) return false
                if (poly.degree <= 3) {
                    return (0 until p).none { polyRing.evaluate(poly, it) == 0 }
                }
                var xPowerP = xPowerModPoly(p, poly, field)
                val x = Polynomial(listOf(0, 1))
                for (i in 1..poly.degree / 2) {
                    val diff = polyRing.subtract(xPowerP, x)
                    val g = polyOps.gcd(poly, diff)
                    if (g.degree > 0) return false
                    xPowerP = xPowerModPolyFromPoly(xPowerP, p, poly, field)
                }
                return true
            }

            fun generateMonicPolynomials(deg: Int): Sequence<Polynomial<Int>> = sequence {
                val total = pow(p.toLong(), deg)
                for (idx in 0 until total) {
                    val coeffs = MutableList(deg + 1) { 0 }
                    coeffs[deg] = 1
                    var remaining = idx
                    for (i in 0 until deg) {
                        coeffs[i] = (remaining % p).toInt()
                        remaining /= p
                    }
                    yield(Polynomial(coeffs))
                }
            }

            return generateMonicPolynomials(n).first { isIrreducible(it) }
        }

        private fun xPowerModPoly(p: Int, modPoly: Polynomial<Int>, field: ZpField): Polynomial<Int> {
            val polyOps = FieldPolynomialOps(field)
            val polyRing = PolynomialRing(field)
            val x = Polynomial(listOf(0, 1))
            var result = polyRing.one
            var base = x
            var exp = p
            while (exp > 0) {
                if (exp % 2 == 1) {
                    result = polyOps.remainder(polyRing.multiply(result, base), modPoly)
                }
                base = polyOps.remainder(polyRing.multiply(base, base), modPoly)
                exp /= 2
            }
            return result
        }

        private fun xPowerModPolyFromPoly(
            currentXP: Polynomial<Int>,
            p: Int,
            modPoly: Polynomial<Int>,
            field: ZpField
        ): Polynomial<Int> {
            val polyOps = FieldPolynomialOps(field)
            val polyRing = PolynomialRing(field)
            var result = polyRing.one
            var base = currentXP
            var exp = p
            while (exp > 0) {
                if (exp % 2 == 1) {
                    result = polyOps.remainder(polyRing.multiply(result, base), modPoly)
                }
                base = polyOps.remainder(polyRing.multiply(base, base), modPoly)
                exp /= 2
            }
            return result
        }
    }
}
