package mathsets.galois

import mathsets.algebra.Field
import mathsets.polynomial.FieldPolynomialOps
import mathsets.polynomial.Polynomial
import mathsets.polynomial.PolynomialRing

/**
 * Represents an element of a simple algebraic field extension K[x]/(p(x)),
 * where p(x) is an irreducible polynomial over the base field K.
 *
 * Each element is represented as a polynomial of degree < deg(p),
 * stored in ascending coefficient order.
 *
 * @param K The base field element type.
 * @property coefficients The polynomial representative in ascending degree order.
 */
data class ExtensionElement<K>(val coefficients: List<K>) {
    override fun toString(): String {
        if (coefficients.isEmpty()) return "0"
        return coefficients.mapIndexedNotNull { i, c ->
            val cs = c.toString()
            if (cs == "0") null
            else when (i) {
                0 -> cs
                1 -> if (cs == "1") "α" else "${cs}α"
                else -> if (cs == "1") "α^$i" else "${cs}α^$i"
            }
        }.reversed().joinToString(" + ").ifEmpty { "0" }
    }
}

/**
 * A simple algebraic field extension K[x]/(p(x)), where p(x) is an
 * irreducible polynomial over the base field K.
 *
 * Elements are represented as polynomials of degree strictly less than
 * deg(p), with arithmetic performed modulo p(x).
 *
 * This implements [Field] over [ExtensionElement]<K>, providing a
 * concrete computational model for field extensions.
 *
 * @param K The base field element type.
 * @property baseField The base field K.
 * @property minimalPolynomial The irreducible polynomial p(x) defining the extension.
 * @throws IllegalArgumentException if [minimalPolynomial] has degree < 1.
 */
class FieldExtension<K>(
    val baseField: Field<K>,
    val minimalPolynomial: Polynomial<K>
) : Field<ExtensionElement<K>> {

    private val polyOps = FieldPolynomialOps(baseField)
    private val polyRing = PolynomialRing(baseField)

    /**
     * The degree of this field extension [L:K] = deg(minimalPolynomial).
     */
    val degree: Int = minimalPolynomial.degree

    init {
        require(degree >= 1) { "Minimal polynomial must have degree >= 1." }
    }

    override val zero: ExtensionElement<K> = ExtensionElement(emptyList())

    override val one: ExtensionElement<K> = ExtensionElement(listOf(baseField.one))

    /**
     * The generator element alpha (the image of x in K[x]/(p(x))).
     */
    val alpha: ExtensionElement<K> = if (degree == 1) {
        val negConstOverLead = baseField.negate(
            baseField.divide(
                minimalPolynomial.coefficients[0],
                minimalPolynomial.leadingCoefficient()!!
            )
        )
        ExtensionElement(listOf(negConstOverLead))
    } else {
        val coeffs = MutableList(2) { baseField.zero }
        coeffs[1] = baseField.one
        ExtensionElement(coeffs)
    }

    private fun toPoly(e: ExtensionElement<K>): Polynomial<K> =
        if (e.coefficients.isEmpty()) Polynomial.zero() else Polynomial(e.coefficients)

    private fun fromPoly(p: Polynomial<K>): ExtensionElement<K> {
        if (p.isZero()) return zero
        val reduced = polyOps.remainder(p, minimalPolynomial)
        return if (reduced.isZero()) zero
        else ExtensionElement(reduced.coefficients)
    }

    private fun normalize(coeffs: List<K>): List<K> {
        var end = coeffs.size
        while (end > 0 && coeffs[end - 1] == baseField.zero) end--
        return if (end == coeffs.size) coeffs else coeffs.subList(0, end)
    }

    override fun add(a: ExtensionElement<K>, b: ExtensionElement<K>): ExtensionElement<K> {
        val maxLen = maxOf(a.coefficients.size, b.coefficients.size)
        val result = List(maxLen) { i ->
            val ai = a.coefficients.getOrElse(i) { baseField.zero }
            val bi = b.coefficients.getOrElse(i) { baseField.zero }
            baseField.add(ai, bi)
        }
        return ExtensionElement(normalize(result))
    }

    override fun negate(a: ExtensionElement<K>): ExtensionElement<K> =
        ExtensionElement(a.coefficients.map { baseField.negate(it) })

    override fun multiply(a: ExtensionElement<K>, b: ExtensionElement<K>): ExtensionElement<K> =
        fromPoly(polyRing.multiply(toPoly(a), toPoly(b)))

    override fun reciprocal(a: ExtensionElement<K>): ExtensionElement<K> {
        require(a.coefficients.isNotEmpty() && a.coefficients.any { it != baseField.zero }) {
            "Cannot invert zero element."
        }
        val (g, s, _) = extendedGcd(toPoly(a), minimalPolynomial)
        val leadInv = baseField.reciprocal(g.leadingCoefficient()!!)
        return fromPoly(polyRing.scalarMultiply(leadInv, s))
    }

    /**
     * Embeds a base field element into this extension.
     *
     * @param k The base field element.
     * @return The corresponding [ExtensionElement].
     */
    fun embed(k: K): ExtensionElement<K> =
        if (k == baseField.zero) zero else ExtensionElement(listOf(k))

    /**
     * Returns all elements of this extension field when the base field is finite.
     *
     * @param baseElements All elements of the base field K.
     * @return A set of all elements in this extension.
     */
    fun elements(baseElements: Set<K>): Set<ExtensionElement<K>> {
        val elems = baseElements.toList()
        var result = listOf(emptyList<K>())
        repeat(degree) {
            result = result.flatMap { prefix ->
                elems.map { e -> prefix + e }
            }
        }
        return result.map { ExtensionElement(normalize(it)) }.toSet()
    }

    /**
     * Evaluates an element of this extension at a given value from the base field.
     * This treats the [ExtensionElement] as a polynomial and evaluates it.
     *
     * @param element The extension element to evaluate.
     * @param value The base field value to substitute for alpha.
     * @return The result of evaluation in the base field.
     */
    fun evaluate(element: ExtensionElement<K>, value: K): K =
        polyRing.evaluate(toPoly(element), value)

    private data class ExtGcdResult<R>(val gcd: Polynomial<R>, val s: Polynomial<R>, val t: Polynomial<R>)

    private fun extendedGcd(a: Polynomial<K>, b: Polynomial<K>): ExtGcdResult<K> {
        var oldR = a; var r = b
        var oldS = polyRing.one; var s = polyRing.zero
        var oldT = polyRing.zero; var t = polyRing.one

        while (!r.isZero()) {
            val q = polyOps.quotient(oldR, r)
            val newR = polyOps.remainder(oldR, r)
            val newS = polyRing.subtract(oldS, polyRing.multiply(q, s))
            val newT = polyRing.subtract(oldT, polyRing.multiply(q, t))
            oldR = r; r = newR
            oldS = s; s = newS
            oldT = t; t = newT
        }
        return ExtGcdResult(oldR, oldS, oldT)
    }
}
