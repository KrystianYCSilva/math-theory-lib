package mathsets.galois

import mathsets.algebra.ZpField
import mathsets.polynomial.Polynomial
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SplittingFieldTest {

    @Test
    fun x2PlusXPlus1SplitsInGF4() {
        val gf2 = ZpField(2)
        val poly = Polynomial(listOf(1, 1, 1))
        val sf = SplittingField(gf2, poly)

        val ext = sf.asSimpleExtension()
        val elems = ext.elements(gf2.elements())

        assertTrue(sf.splitsIn(ext, elems),
            "x^2+x+1 should split in GF(4)")
    }

    @Test
    fun x2PlusXPlus1HasTwoRootsInGF4() {
        val gf2 = ZpField(2)
        val poly = Polynomial(listOf(1, 1, 1))
        val sf = SplittingField(gf2, poly)

        val ext = sf.asSimpleExtension()
        val elems = ext.elements(gf2.elements())
        val roots = sf.findRoots(ext, elems)

        assertEquals(2, roots.size, "x^2+x+1 should have exactly 2 roots in GF(4)")
    }

    @Test
    fun rootsAreActuallyRoots() {
        val gf2 = ZpField(2)
        val poly = Polynomial(listOf(1, 1, 1))
        val sf = SplittingField(gf2, poly)

        val ext = sf.asSimpleExtension()
        val elems = ext.elements(gf2.elements())
        val roots = sf.findRoots(ext, elems)
        val autOps = FieldAutomorphismOps(ext)

        for (root in roots) {
            assertEquals(ext.zero, autOps.evaluateMinPoly(poly, root),
                "Root $root should satisfy the polynomial")
        }
    }

    @Test
    fun cubicOverGF2SplitsInGF8() {
        val gf2 = ZpField(2)
        val gf8 = FiniteField(2, 3)
        val poly = gf8.irreduciblePolynomial

        val sf = SplittingField(gf2, poly)
        val ext = FieldExtension(gf2, poly)
        val elems = ext.elements(gf2.elements())

        assertTrue(sf.splitsIn(ext, elems),
            "Irreducible cubic over GF(2) should split in GF(8)")
        assertEquals(3, sf.findRoots(ext, elems).size)
    }

    @Test
    fun factorizationFormatIsCorrect() {
        val gf2 = ZpField(2)
        val poly = Polynomial(listOf(1, 1, 1))
        val sf = SplittingField(gf2, poly)

        val ext = sf.asSimpleExtension()
        val elems = ext.elements(gf2.elements())
        val roots = sf.findRoots(ext, elems)

        val factored = sf.factorization(roots)
        assertTrue(factored.contains("(x -"), "Factorization should contain linear factors")
    }
}
