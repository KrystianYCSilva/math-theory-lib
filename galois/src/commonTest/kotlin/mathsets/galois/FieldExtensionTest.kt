package mathsets.galois

import mathsets.algebra.ZpField
import mathsets.polynomial.Polynomial
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FieldExtensionTest {

    private val gf2 = ZpField(2)
    private val gf3 = ZpField(3)

    @Test
    fun fieldExtensionOverGF2HasCorrectDegree() {
        val poly = Polynomial(listOf(1, 1, 1))
        val ext = FieldExtension(gf2, poly)
        assertEquals(2, ext.degree)
    }

    @Test
    fun gf4ElementCount() {
        val poly = Polynomial(listOf(1, 1, 1))
        val ext = FieldExtension(gf2, poly)
        val elems = ext.elements(gf2.elements())
        assertEquals(4, elems.size)
    }

    @Test
    fun gf4AdditionIsCommutative() {
        val poly = Polynomial(listOf(1, 1, 1))
        val ext = FieldExtension(gf2, poly)
        val elems = ext.elements(gf2.elements()).toList()
        for (a in elems) {
            for (b in elems) {
                assertEquals(ext.add(a, b), ext.add(b, a))
            }
        }
    }

    @Test
    fun gf4MultiplicationIsCommutative() {
        val poly = Polynomial(listOf(1, 1, 1))
        val ext = FieldExtension(gf2, poly)
        val elems = ext.elements(gf2.elements()).toList()
        for (a in elems) {
            for (b in elems) {
                assertEquals(ext.multiply(a, b), ext.multiply(b, a))
            }
        }
    }

    @Test
    fun gf4EveryNonZeroElementHasInverse() {
        val poly = Polynomial(listOf(1, 1, 1))
        val ext = FieldExtension(gf2, poly)
        val elems = ext.elements(gf2.elements()).toList()
        for (a in elems) {
            if (a != ext.zero) {
                val inv = ext.reciprocal(a)
                assertEquals(ext.one, ext.multiply(a, inv), "Failed for $a")
            }
        }
    }

    @Test
    fun gf4Distributivity() {
        val poly = Polynomial(listOf(1, 1, 1))
        val ext = FieldExtension(gf2, poly)
        val elems = ext.elements(gf2.elements()).toList()
        for (a in elems) {
            for (b in elems) {
                for (c in elems) {
                    val left = ext.multiply(a, ext.add(b, c))
                    val right = ext.add(ext.multiply(a, b), ext.multiply(a, c))
                    assertEquals(left, right, "Distributivity failed for $a, $b, $c")
                }
            }
        }
    }

    @Test
    fun gf8HasCorrectOrder() {
        val gf8 = FiniteField(2, 3)
        assertEquals(8L, gf8.order)
        assertEquals(8, gf8.elements().size)
    }

    @Test
    fun gf8IsField() {
        val gf8 = FiniteField(2, 3)
        val elems = gf8.elements().toList()
        for (a in elems) {
            if (a != gf8.zero) {
                val inv = gf8.reciprocal(a)
                assertEquals(gf8.one, gf8.multiply(a, inv), "Failed for $a")
            }
        }
    }

    @Test
    fun gf9HasNineElements() {
        val gf9 = FiniteField(3, 2)
        assertEquals(9L, gf9.order)
        assertEquals(9, gf9.elements().size)
    }

    @Test
    fun gf9NonZeroInverses() {
        val gf9 = FiniteField(3, 2)
        val elems = gf9.elements().toList()
        for (a in elems) {
            if (a != gf9.zero) {
                val inv = gf9.reciprocal(a)
                assertEquals(gf9.one, gf9.multiply(a, inv))
            }
        }
    }

    @Test
    fun gf25HasCorrectOrder() {
        val gf25 = FiniteField(5, 2)
        assertEquals(25L, gf25.order)
        assertEquals(25, gf25.elements().size)
    }

    @Test
    fun gf4FrobeniusIsAutomorphism() {
        val gf4 = FiniteField(2, 2)
        val elems = gf4.elements().toList()
        for (a in elems) {
            for (b in elems) {
                val frobAB = gf4.frobenius(gf4.add(a, b))
                val frobA = gf4.frobenius(a)
                val frobB = gf4.frobenius(b)
                assertEquals(frobAB, gf4.add(frobA, frobB), "Frobenius not additive")

                val frobABMul = gf4.frobenius(gf4.multiply(a, b))
                assertEquals(frobABMul, gf4.multiply(frobA, frobB), "Frobenius not multiplicative")
            }
        }
    }

    @Test
    fun gf4FrobeniusSquaredIsIdentity() {
        val gf4 = FiniteField(2, 2)
        val elems = gf4.elements().toList()
        for (a in elems) {
            val frob2 = gf4.frobenius(gf4.frobenius(a))
            assertEquals(a, frob2, "Frobenius^2 != identity for $a")
        }
    }

    @Test
    fun embedPreservesBaseFieldArithmetic() {
        val gf4 = FiniteField(2, 2)
        for (a in 0..1) {
            for (b in 0..1) {
                val sumBase = gf2.add(a, b)
                val sumExt = gf4.add(gf4.embed(a), gf4.embed(b))
                assertEquals(gf4.embed(sumBase), sumExt)

                val prodBase = gf2.multiply(a, b)
                val prodExt = gf4.multiply(gf4.embed(a), gf4.embed(b))
                assertEquals(gf4.embed(prodBase), prodExt)
            }
        }
    }

    @Test
    fun gf4HasPrimitiveElement() {
        val gf4 = FiniteField(2, 2)
        val primitives = gf4.elements().filter { it != gf4.zero && gf4.isPrimitiveElement(it) }
        assertTrue(primitives.isNotEmpty(), "GF(4) should have primitive elements")
    }

    @Test
    fun gf8HasPrimitiveElement() {
        val gf8 = FiniteField(2, 3)
        val primitives = gf8.elements().filter { it != gf8.zero && gf8.isPrimitiveElement(it) }
        assertTrue(primitives.isNotEmpty(), "GF(8) should have primitive elements")
        assertEquals(CyclotomicPolynomials.eulerTotient(7), primitives.size)
    }

    @Test
    fun fieldExtensionOverGF3() {
        val poly = Polynomial(listOf(1, 0, 1))
        val ext = FieldExtension(gf3, poly)
        assertEquals(2, ext.degree)
        val elems = ext.elements(gf3.elements())
        assertEquals(9, elems.size)
    }

    @Test
    fun algebraicElementRootOf() {
        val factory = AlgebraicElementFactory(gf2)
        val irreducible = Polynomial(listOf(1, 1, 1))
        val alpha = factory.rootOf(irreducible)
        assertEquals(2, alpha.degree)
    }

    @Test
    fun makeMonic() {
        val factory = AlgebraicElementFactory(gf3)
        val poly = Polynomial(listOf(2, 0, 2))
        val monic = factory.makeMonic(poly)
        assertEquals(gf3.one, monic.leadingCoefficient())
    }

    @Test
    fun irreducibilityTestForLinearPolynomial() {
        val factory = AlgebraicElementFactory(gf2)
        val linear = Polynomial(listOf(1, 1))
        assertTrue(factory.isIrreducibleByRootTest(linear, gf2.elements()))
    }

    @Test
    fun irreducibilityTestForX2PlusXPlus1OverGF2() {
        val factory = AlgebraicElementFactory(gf2)
        val poly = Polynomial(listOf(1, 1, 1))
        assertTrue(factory.isIrreducibleByRootTest(poly, gf2.elements()))
    }

    @Test
    fun reducibilityTestForX2Plus1OverGF2() {
        val factory = AlgebraicElementFactory(gf2)
        val poly = Polynomial(listOf(1, 0, 1))
        assertFalse(factory.isIrreducibleByRootTest(poly, gf2.elements()))
    }
}
