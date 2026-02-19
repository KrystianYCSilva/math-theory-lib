package mathsets.algebra

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class QuotientRingTest {

    @Test
    fun idealInZ6() {
        val z6 = ZnRing(6)
        val idealElements = setOf(0, 2, 4)
        val ideal = Ideal(z6, idealElements, z6.elements())
        assertEquals(3, ideal.elements.size)
    }

    @Test
    fun quotientRingZ6ModIdeal2() {
        val z6 = ZnRing(6)
        val idealElements = setOf(0, 2, 4)
        val ideal = Ideal(z6, idealElements, z6.elements())
        val quotient = QuotientRing(z6, ideal, z6.elements())
        assertEquals(2, quotient.order())
    }

    @Test
    fun quotientRingZ6ModIdeal3() {
        val z6 = ZnRing(6)
        val idealElements = setOf(0, 3)
        val ideal = Ideal(z6, idealElements, z6.elements())
        val quotient = QuotientRing(z6, ideal, z6.elements())
        assertEquals(3, quotient.order())
    }

    @Test
    fun zpFieldPrimeIdealIsMaximal() {
        val z5 = ZpField(5)
        val zeroIdeal = Ideal(z5, setOf(0), z5.elements())
        assertTrue(zeroIdeal.isPrime())
    }

    @Test
    fun z6IdealOf2IsPrime() {
        val z6 = ZnRing(6)
        val idealElements = setOf(0, 2, 4)
        val ideal = Ideal(z6, idealElements, z6.elements())
        assertTrue(ideal.isPrime())
    }

    @Test
    fun z6ZeroIdealIsNotPrime() {
        val z6 = ZnRing(6)
        val ideal = Ideal(z6, setOf(0), z6.elements())
        assertFalse(ideal.isPrime())
    }

    @Test
    fun quotientOfZ5ByZeroIdealIsField() {
        val z5 = ZpField(5)
        val zeroIdeal = Ideal(z5, setOf(0), z5.elements())
        val quotient = QuotientRing(z5, zeroIdeal, z5.elements())
        assertEquals(5, quotient.order())
        assertTrue(quotient.isField())
    }
}
