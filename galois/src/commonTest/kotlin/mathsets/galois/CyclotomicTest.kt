package mathsets.galois

import mathsets.algebra.ZpField
import mathsets.polynomial.PolynomialRing
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CyclotomicTest {

    @Test
    fun eulerTotientOfPrime() {
        assertEquals(1, CyclotomicPolynomials.eulerTotient(1))
        assertEquals(1, CyclotomicPolynomials.eulerTotient(2))
        assertEquals(2, CyclotomicPolynomials.eulerTotient(3))
        assertEquals(4, CyclotomicPolynomials.eulerTotient(5))
        assertEquals(6, CyclotomicPolynomials.eulerTotient(7))
    }

    @Test
    fun eulerTotientOfComposite() {
        assertEquals(2, CyclotomicPolynomials.eulerTotient(4))
        assertEquals(2, CyclotomicPolynomials.eulerTotient(6))
        assertEquals(4, CyclotomicPolynomials.eulerTotient(8))
        assertEquals(4, CyclotomicPolynomials.eulerTotient(12))
    }

    @Test
    fun divisorsOfSmallNumbers() {
        assertEquals(listOf(1), CyclotomicPolynomials.divisors(1))
        assertEquals(listOf(1, 2), CyclotomicPolynomials.divisors(2))
        assertEquals(listOf(1, 2, 3, 6), CyclotomicPolynomials.divisors(6))
        assertEquals(listOf(1, 2, 3, 4, 6, 12), CyclotomicPolynomials.divisors(12))
    }

    @Test
    fun cyclotomicPhi1IsXMinus1() {
        val gf5 = ZpField(5)
        val phi1 = CyclotomicPolynomials.cyclotomicPolynomial(1, gf5)
        assertEquals(1, phi1.degree)
        assertEquals(listOf(4, 1), phi1.coefficients)
    }

    @Test
    fun cyclotomicPhi2IsXPlus1() {
        val gf5 = ZpField(5)
        val phi2 = CyclotomicPolynomials.cyclotomicPolynomial(2, gf5)
        assertEquals(1, phi2.degree)
        assertEquals(listOf(1, 1), phi2.coefficients)
    }

    @Test
    fun productOfCyclotomicPolynomialsIsXnMinus1() {
        val gf5 = ZpField(5)
        val polyRing = PolynomialRing(gf5)

        for (n in listOf(3, 4, 6)) {
            val divs = CyclotomicPolynomials.divisors(n)
            var product = polyRing.one
            for (d in divs) {
                val phiD = CyclotomicPolynomials.cyclotomicPolynomial(d, gf5)
                product = polyRing.multiply(product, phiD)
            }
            val xnMinus1 = CyclotomicPolynomials.xPowerMinusOne(n, gf5)
            assertEquals(xnMinus1, product,
                "Product of Phi_d(x) for d|$n should equal x^$n - 1")
        }
    }

    @Test
    fun cyclotomicFieldExtensionDegree() {
        val cf3over5 = CyclotomicFieldExtension(3, 5)
        val ord = CyclotomicPolynomials.multiplicativeOrder(5, 3)
        assertEquals(ord, cf3over5.extensionDegree)
    }

    @Test
    fun rootsOfUnityHaveCorrectCount() {
        val cf = CyclotomicFieldExtension(3, 2)
        val roots = cf.rootsOfUnity()
        assertEquals(3, roots.size, "There should be 3 cube roots of unity in the splitting field")
    }

    @Test
    fun primitiveRootsOfUnityHaveCorrectCount() {
        val cf = CyclotomicFieldExtension(3, 2)
        val primitiveRoots = cf.primitiveRootsOfUnity()
        assertEquals(CyclotomicPolynomials.eulerTotient(3), primitiveRoots.size,
            "Number of primitive 3rd roots of unity should be phi(3)")
    }

    @Test
    fun allRootsOfUnitySatisfyXnEquals1() {
        val cf = CyclotomicFieldExtension(3, 2)
        val field = cf.splittingField
        val roots = cf.rootsOfUnity()
        for (root in roots) {
            assertEquals(field.one, field.power(root, 3),
                "Root of unity $root should satisfy x^3 = 1")
        }
    }

    @Test
    fun multiplicativeOrderForCoprimePairs() {
        assertEquals(1, CyclotomicPolynomials.multiplicativeOrder(1, 2))
        assertEquals(2, CyclotomicPolynomials.multiplicativeOrder(2, 3))
        assertEquals(4, CyclotomicPolynomials.multiplicativeOrder(2, 5))
        assertEquals(3, CyclotomicPolynomials.multiplicativeOrder(2, 7))
    }

    @Test
    fun fourthRootsOfUnityOverGF5() {
        val cf = CyclotomicFieldExtension(4, 5)
        val roots = cf.rootsOfUnity()
        val field = cf.splittingField
        assertEquals(4, roots.size, "Should have 4 fourth roots of unity")
        for (root in roots) {
            assertEquals(field.one, field.power(root, 4))
        }
    }
}
