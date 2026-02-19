package mathsets.galois

import mathsets.algebra.ZpField
import mathsets.polynomial.Polynomial
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GaloisCorrespondenceTest {

    @Test
    fun correspondenceForGF4OverGF2() {
        val gf2 = ZpField(2)
        val poly = Polynomial(listOf(1, 1, 1))
        val ext = FieldExtension(gf2, poly)
        val elems = ext.elements(gf2.elements())

        val galois = GaloisGroup.of(ext, elems)
        val correspondence = GaloisCorrespondence(galois, elems)

        val result = correspondence.verify()
        assertTrue(result.isValid, "Galois correspondence should be valid for GF(4)/GF(2)")
        assertTrue(result.indexDegreeProduct, "|H| * [L^H : K] = [L : K]")
        assertTrue(result.bijectivity, "Correspondence should be bijective")
    }

    @Test
    fun correspondenceForGF8OverGF2() {
        val gf2 = ZpField(2)
        val gf8 = FiniteField(2, 3)
        val ext = FieldExtension(gf2, gf8.irreduciblePolynomial)
        val elems = ext.elements(gf2.elements())

        val galois = GaloisGroup.of(ext, elems)
        val correspondence = GaloisCorrespondence(galois, elems)

        val result = correspondence.verify()
        assertTrue(result.isValid, "Galois correspondence should be valid for GF(8)/GF(2)")
    }

    @Test
    fun correspondenceIndexDegreeProductHolds() {
        val gf2 = ZpField(2)
        val poly = Polynomial(listOf(1, 1, 1))
        val ext = FieldExtension(gf2, poly)
        val elems = ext.elements(gf2.elements())

        val galois = GaloisGroup.of(ext, elems)
        val correspondence = GaloisCorrespondence(galois, elems)

        val entries = correspondence.computeCorrespondence()
        for (entry in entries) {
            assertEquals(
                ext.degree,
                entry.subgroupOrder * entry.fixedFieldDegree,
                "|H| * [L^H : K] should equal [L:K] for subgroup of order ${entry.subgroupOrder}"
            )
        }
    }

    @Test
    fun correspondenceForGF9OverGF3() {
        val gf3 = ZpField(3)
        val gf9 = FiniteField(3, 2)
        val ext = FieldExtension(gf3, gf9.irreduciblePolynomial)
        val elems = ext.elements(gf3.elements())

        val galois = GaloisGroup.of(ext, elems)
        val correspondence = GaloisCorrespondence(galois, elems)

        val result = correspondence.verify()
        assertTrue(result.isValid, "Galois correspondence should be valid for GF(9)/GF(3)")
    }

    @Test
    fun normalSubgroupsCorrespondToGaloisExtensions() {
        val gf2 = ZpField(2)
        val gf8 = FiniteField(2, 3)
        val ext = FieldExtension(gf2, gf8.irreduciblePolynomial)
        val elems = ext.elements(gf2.elements())

        val galois = GaloisGroup.of(ext, elems)
        val correspondence = GaloisCorrespondence(galois, elems)
        val entries = correspondence.computeCorrespondence()

        val trivialEntry = entries.first { it.subgroupOrder == 1 }
        assertTrue(trivialEntry.isNormal, "Trivial subgroup is always normal")

        val fullEntry = entries.first { it.subgroupOrder == galois.order }
        assertTrue(fullEntry.isNormal, "Full group is always normal")
    }
}
