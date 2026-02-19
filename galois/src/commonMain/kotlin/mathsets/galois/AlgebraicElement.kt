package mathsets.galois

import mathsets.algebra.Field
import mathsets.polynomial.FieldPolynomialOps
import mathsets.polynomial.Polynomial
import mathsets.polynomial.PolynomialRing

/**
 * An algebraic element over a field K, defined by its minimal polynomial.
 *
 * An element alpha is algebraic over K if there exists a non-zero polynomial
 * p(x) in K[x] such that p(alpha) = 0. The minimal polynomial is the unique
 * monic polynomial of least degree with this property.
 *
 * @param K The base field element type.
 * @property minimalPolynomial The monic irreducible polynomial of least degree
 *   that alpha satisfies.
 * @property representation The representation of alpha in its extension field
 *   K[x]/(minimalPolynomial).
 */
data class AlgebraicElement<K>(
    val minimalPolynomial: Polynomial<K>,
    val representation: ExtensionElement<K>
) {
    /**
     * The degree of this algebraic element over K, i.e., [K(alpha):K].
     */
    val degree: Int get() = minimalPolynomial.degree

    override fun toString(): String = "AlgebraicElement(minPoly=$minimalPolynomial, repr=$representation)"
}

/**
 * Utility for constructing and working with algebraic elements over a field.
 *
 * @param K The base field element type.
 * @property baseField The base field K.
 */
class AlgebraicElementFactory<K>(val baseField: Field<K>) {

    private val polyOps = FieldPolynomialOps(baseField)
    private val polyRing = PolynomialRing(baseField)

    /**
     * Creates an algebraic element from a monic irreducible polynomial.
     * The element is the image of x in K[x]/(p(x)).
     *
     * @param irreducible The irreducible polynomial p(x) over K.
     * @return The algebraic element alpha such that p(alpha) = 0.
     * @throws IllegalArgumentException if the polynomial has degree < 1.
     */
    fun rootOf(irreducible: Polynomial<K>): AlgebraicElement<K> {
        require(irreducible.degree >= 1) { "Polynomial must have degree >= 1." }
        val monic = makeMonic(irreducible)
        val ext = FieldExtension(baseField, monic)
        return AlgebraicElement(monic, ext.alpha)
    }

    /**
     * Checks if a polynomial is irreducible over the base field by attempting
     * to find a root via brute force (only practical for finite fields).
     *
     * @param p The polynomial to test.
     * @param elements All elements of the base field.
     * @return `true` if p has no roots in K and has degree <= 3, or degree <= 1.
     */
    fun isIrreducibleByRootTest(p: Polynomial<K>, elements: Set<K>): Boolean {
        if (p.degree <= 1) return true
        for (e in elements) {
            if (polyRing.evaluate(p, e) == baseField.zero) return false
        }
        return p.degree <= 3
    }

    /**
     * Makes a polynomial monic by dividing by its leading coefficient.
     *
     * @param p The polynomial to make monic.
     * @return The monic version of p.
     */
    fun makeMonic(p: Polynomial<K>): Polynomial<K> {
        if (p.isZero()) return p
        val lc = p.leadingCoefficient()!!
        if (lc == baseField.one) return p
        val lcInv = baseField.reciprocal(lc)
        return Polynomial(p.coefficients.map { baseField.multiply(lcInv, it) })
    }
}
