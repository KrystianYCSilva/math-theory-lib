package mathsets.galois

import mathsets.algebra.ZpField
import mathsets.polynomial.Polynomial
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GaloisGroupTest {

    @Test
    fun galoisGroupOfGF4OverGF2IsCyclicOfOrder2() {
        val gf2 = ZpField(2)
        val poly = Polynomial(listOf(1, 1, 1))
        val ext = FieldExtension(gf2, poly)
        val elems = ext.elements(gf2.elements())

        val galois = GaloisGroup.of(ext, elems)

        assertEquals(2, galois.order, "Gal(GF(4)/GF(2)) should have order 2")
        assertTrue(galois.isGalois, "GF(4)/GF(2) should be Galois")
    }

    @Test
    fun galoisGroupOfGF8OverGF2IsCyclicOfOrder3() {
        val gf8 = FiniteField(2, 3)
        val ext = FieldExtension(ZpField(2), gf8.irreduciblePolynomial)
        val elems = ext.elements(ZpField(2).elements())

        val galois = GaloisGroup.of(ext, elems)

        assertEquals(3, galois.order, "Gal(GF(8)/GF(2)) should have order 3")
        assertTrue(galois.isGalois)
    }

    @Test
    fun galoisGroupOfGF9OverGF3IsCyclicOfOrder2() {
        val gf3 = ZpField(3)
        val gf9 = FiniteField(3, 2)
        val ext = FieldExtension(gf3, gf9.irreduciblePolynomial)
        val elems = ext.elements(gf3.elements())

        val galois = GaloisGroup.of(ext, elems)

        assertEquals(2, galois.order, "Gal(GF(9)/GF(3)) should have order 2")
        assertTrue(galois.isGalois)
    }

    @Test
    fun galoisGroupIdentityFixesAll() {
        val gf2 = ZpField(2)
        val poly = Polynomial(listOf(1, 1, 1))
        val ext = FieldExtension(gf2, poly)
        val elems = ext.elements(gf2.elements())

        val galois = GaloisGroup.of(ext, elems)
        val ops = FieldAutomorphismOps(ext)
        val id = galois.identity

        for (e in elems) {
            assertEquals(e, ops.apply(id, e), "Identity should fix $e")
        }
    }

    @Test
    fun galoisGroupCompositionIsAssociative() {
        val gf2 = ZpField(2)
        val gf8 = FiniteField(2, 3)
        val ext = FieldExtension(gf2, gf8.irreduciblePolynomial)
        val elems = ext.elements(gf2.elements())

        val galois = GaloisGroup.of(ext, elems)
        val auts = galois.automorphisms

        for (a in auts) {
            for (b in auts) {
                for (c in auts) {
                    val ab_c = galois.op(galois.op(a, b), c)
                    val a_bc = galois.op(a, galois.op(b, c))
                    assertEquals(ab_c.alphaImage, a_bc.alphaImage,
                        "Associativity failed for $a, $b, $c")
                }
            }
        }
    }

    @Test
    fun galoisGroupInverses() {
        val gf2 = ZpField(2)
        val poly = Polynomial(listOf(1, 1, 1))
        val ext = FieldExtension(gf2, poly)
        val elems = ext.elements(gf2.elements())

        val galois = GaloisGroup.of(ext, elems)

        for (sigma in galois.automorphisms) {
            val inv = galois.inverse(sigma)
            val composed = galois.op(sigma, inv)
            assertEquals(galois.identity.alphaImage, composed.alphaImage,
                "sigma * sigma^{-1} should be identity for $sigma")
        }
    }

    @Test
    fun fixedFieldOfFullGroupIsBaseField() {
        val gf2 = ZpField(2)
        val poly = Polynomial(listOf(1, 1, 1))
        val ext = FieldExtension(gf2, poly)
        val elems = ext.elements(gf2.elements())

        val galois = GaloisGroup.of(ext, elems)
        val fixed = galois.fixedField(galois.automorphisms, elems)

        val baseFieldElements = setOf(ext.zero, ext.one)
        assertEquals(baseFieldElements, fixed,
            "Fixed field of full Galois group should be the base field GF(2)")
    }

    @Test
    fun fixedFieldOfTrivialGroupIsWholeField() {
        val gf2 = ZpField(2)
        val poly = Polynomial(listOf(1, 1, 1))
        val ext = FieldExtension(gf2, poly)
        val elems = ext.elements(gf2.elements())

        val galois = GaloisGroup.of(ext, elems)
        val fixed = galois.fixedField(listOf(galois.identity), elems)

        assertEquals(elems, fixed,
            "Fixed field of trivial group should be the whole extension")
    }

    @Test
    fun automorphismsPreserveAddition() {
        val gf2 = ZpField(2)
        val poly = Polynomial(listOf(1, 1, 1))
        val ext = FieldExtension(gf2, poly)
        val elems = ext.elements(gf2.elements())
        val ops = FieldAutomorphismOps(ext)

        val galois = GaloisGroup.of(ext, elems)
        for (sigma in galois.automorphisms) {
            for (a in elems) {
                for (b in elems) {
                    val sigmaSum = ops.apply(sigma, ext.add(a, b))
                    val sumSigma = ext.add(ops.apply(sigma, a), ops.apply(sigma, b))
                    assertEquals(sigmaSum, sumSigma,
                        "sigma(a+b) should equal sigma(a)+sigma(b)")
                }
            }
        }
    }

    @Test
    fun automorphismsPreserveMultiplication() {
        val gf2 = ZpField(2)
        val poly = Polynomial(listOf(1, 1, 1))
        val ext = FieldExtension(gf2, poly)
        val elems = ext.elements(gf2.elements())
        val ops = FieldAutomorphismOps(ext)

        val galois = GaloisGroup.of(ext, elems)
        for (sigma in galois.automorphisms) {
            for (a in elems) {
                for (b in elems) {
                    val sigmaProd = ops.apply(sigma, ext.multiply(a, b))
                    val prodSigma = ext.multiply(ops.apply(sigma, a), ops.apply(sigma, b))
                    assertEquals(sigmaProd, prodSigma,
                        "sigma(a*b) should equal sigma(a)*sigma(b)")
                }
            }
        }
    }

    @Test
    fun automorphismsFixBaseField() {
        val gf2 = ZpField(2)
        val poly = Polynomial(listOf(1, 1, 1))
        val ext = FieldExtension(gf2, poly)
        val elems = ext.elements(gf2.elements())
        val ops = FieldAutomorphismOps(ext)

        val galois = GaloisGroup.of(ext, elems)
        for (sigma in galois.automorphisms) {
            assertEquals(ext.zero, ops.apply(sigma, ext.zero))
            assertEquals(ext.one, ops.apply(sigma, ext.one))
        }
    }
}
