package mathsets.galois

import mathsets.algebra.Field
import mathsets.polynomial.Polynomial

/**
 * A field automorphism sigma: L -> L that fixes the base field K.
 *
 * An automorphism is a bijective ring homomorphism from a field to itself.
 * In the context of a field extension L/K, we require sigma(k) = k for all k in K.
 *
 * Since an automorphism of K[x]/(p(x)) is completely determined by the
 * image of alpha (the root of p), we store only sigma(alpha).
 *
 * @param K The base field element type.
 * @property alphaImage The image sigma(alpha) of the generator under this automorphism.
 * @property name An optional label for display purposes.
 */
class FieldAutomorphism<K>(
    val alphaImage: ExtensionElement<K>,
    val name: String = ""
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FieldAutomorphism<*>) return false
        return alphaImage == other.alphaImage
    }

    override fun hashCode(): Int = alphaImage.hashCode()

    override fun toString(): String = if (name.isNotEmpty()) name else "σ(α)=$alphaImage"
}

/**
 * Utility for computing and composing field automorphisms of a [FieldExtension].
 *
 * @param K The base field element type.
 * @property extension The field extension L = K[x]/(p(x)).
 */
class FieldAutomorphismOps<K>(val extension: FieldExtension<K>) {

    private val baseField: Field<K> = extension.baseField

    /**
     * Applies an automorphism to an element of the extension field.
     *
     * Given sigma determined by sigma(alpha) = [automorphism].alphaImage,
     * for an element a_0 + a_1*alpha + ... + a_{n-1}*alpha^{n-1},
     * sigma maps it to a_0 + a_1*sigma(alpha) + ... + a_{n-1}*sigma(alpha)^{n-1}.
     *
     * @param automorphism The automorphism to apply.
     * @param element The element to transform.
     * @return sigma(element).
     */
    fun apply(automorphism: FieldAutomorphism<K>, element: ExtensionElement<K>): ExtensionElement<K> {
        if (element.coefficients.isEmpty()) return extension.zero
        var result = extension.zero
        var alphaPower = extension.one
        for (coeff in element.coefficients) {
            val term = extension.multiply(extension.embed(coeff), alphaPower)
            result = extension.add(result, term)
            alphaPower = extension.multiply(alphaPower, automorphism.alphaImage)
        }
        return result
    }

    /**
     * Composes two automorphisms: (sigma2 . sigma1)(alpha) = sigma2(sigma1(alpha)).
     *
     * @param sigma1 The first automorphism to apply.
     * @param sigma2 The second automorphism to apply (applied to the result of sigma1).
     * @return The composed automorphism.
     */
    fun compose(sigma1: FieldAutomorphism<K>, sigma2: FieldAutomorphism<K>): FieldAutomorphism<K> {
        val composedImage = apply(sigma2, sigma1.alphaImage)
        return FieldAutomorphism(composedImage, "${sigma2.name}∘${sigma1.name}")
    }

    /**
     * Returns the identity automorphism (sigma(alpha) = alpha).
     *
     * @return The identity automorphism.
     */
    fun identity(): FieldAutomorphism<K> = FieldAutomorphism(extension.alpha, "id")

    /**
     * Computes the inverse of an automorphism by searching through all
     * automorphisms for one that composes to the identity.
     *
     * @param sigma The automorphism to invert.
     * @param allAutomorphisms All automorphisms of the extension.
     * @return The inverse automorphism sigma^{-1}.
     * @throws NoSuchElementException if no inverse is found.
     */
    fun inverse(
        sigma: FieldAutomorphism<K>,
        allAutomorphisms: List<FieldAutomorphism<K>>
    ): FieldAutomorphism<K> {
        val id = identity()
        return allAutomorphisms.first { tau ->
            compose(sigma, tau).alphaImage == id.alphaImage
        }
    }

    /**
     * Finds all automorphisms of L/K by finding all roots of the minimal
     * polynomial in L.
     *
     * An automorphism is determined by sending alpha to another root of
     * the minimal polynomial in L. So we search for all elements beta in L
     * such that p(beta) = 0.
     *
     * @param elements All elements of L (only practical for finite extensions of finite fields).
     * @return A list of all K-automorphisms of L.
     */
    fun findAllAutomorphisms(elements: Set<ExtensionElement<K>>): List<FieldAutomorphism<K>> {
        val minPoly = extension.minimalPolynomial
        val roots = mutableListOf<ExtensionElement<K>>()

        for (beta in elements) {
            if (evaluateMinPoly(minPoly, beta) == extension.zero) {
                roots.add(beta)
            }
        }

        return roots.mapIndexed { i, root ->
            FieldAutomorphism(root, "σ$i")
        }
    }

    /**
     * Evaluates the minimal polynomial (with coefficients in K) at an element
     * of the extension field L.
     *
     * @param poly The polynomial with base field coefficients.
     * @param value The extension field element to evaluate at.
     * @return The result p(value) in L.
     */
    fun evaluateMinPoly(poly: Polynomial<K>, value: ExtensionElement<K>): ExtensionElement<K> {
        if (poly.isZero()) return extension.zero
        var result = extension.zero
        var power = extension.one
        for (coeff in poly.coefficients) {
            val term = extension.multiply(extension.embed(coeff), power)
            result = extension.add(result, term)
            power = extension.multiply(power, value)
        }
        return result
    }
}
