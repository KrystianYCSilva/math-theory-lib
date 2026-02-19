package mathsets.galois

import mathsets.algebra.Field
import mathsets.polynomial.FieldPolynomialOps
import mathsets.polynomial.Polynomial
import mathsets.polynomial.PolynomialRing

/**
 * Represents the splitting field of a polynomial over a base field K.
 *
 * The splitting field of f(x) in K[x] is the smallest extension L/K
 * in which f splits completely into linear factors.
 *
 * For finite fields GF(p), the splitting field of f in GF(p)[x] is always
 * GF(p^n) for some n, and can be computed concretely.
 *
 * This implementation handles the simple case where f is irreducible
 * (splitting field = K[x]/(f(x))), and the case where f factors into
 * irreducible components that all split in a single extension.
 *
 * @param K The base field element type.
 * @property baseField The base field K.
 * @property polynomial The polynomial whose splitting field we compute.
 */
class SplittingField<K>(
    val baseField: Field<K>,
    val polynomial: Polynomial<K>
) {
    private val polyOps = FieldPolynomialOps(baseField)
    private val polyRing = PolynomialRing(baseField)

    /**
     * For a single irreducible polynomial, the splitting field is K[x]/(f(x))
     * if f splits completely there, or requires iterated extensions otherwise.
     *
     * This method handles the simple case: f is irreducible, and returns
     * the extension K[x]/(f(x)).
     *
     * @return The [FieldExtension] that serves as the splitting field.
     */
    fun asSimpleExtension(): FieldExtension<K> = FieldExtension(baseField, polynomial)

    /**
     * Finds all roots of the polynomial in a given extension field.
     *
     * @param extension The extension field to search for roots.
     * @param elements All elements of the extension field.
     * @return The list of roots of [polynomial] in the extension.
     */
    fun findRoots(
        extension: FieldExtension<K>,
        elements: Set<ExtensionElement<K>>
    ): List<ExtensionElement<K>> {
        val autOps = FieldAutomorphismOps(extension)
        return elements.filter { beta ->
            autOps.evaluateMinPoly(polynomial, beta) == extension.zero
        }
    }

    /**
     * Checks whether the polynomial splits completely (into linear factors)
     * in a given extension field.
     *
     * @param extension The extension field to check.
     * @param elements All elements of the extension field.
     * @return `true` if the polynomial has exactly deg(f) roots in the extension.
     */
    fun splitsIn(
        extension: FieldExtension<K>,
        elements: Set<ExtensionElement<K>>
    ): Boolean {
        val roots = findRoots(extension, elements)
        return roots.size == polynomial.degree
    }

    /**
     * Factors the polynomial into linear factors in the extension field,
     * given the list of roots.
     *
     * @param roots The roots of the polynomial in the extension.
     * @return A string representation of the factorization.
     */
    fun factorization(roots: List<ExtensionElement<K>>): String {
        val lc = polynomial.leadingCoefficient()
        val factors = roots.map { root -> "(x - $root)" }
        return if (lc != null && lc != baseField.one) {
            "$lc * ${factors.joinToString(" * ")}"
        } else {
            factors.joinToString(" * ")
        }
    }
}
