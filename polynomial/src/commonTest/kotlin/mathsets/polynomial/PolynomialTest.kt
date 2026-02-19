package mathsets.polynomial

import mathsets.algebra.RationalField
import mathsets.algebra.ZpField
import mathsets.kernel.RationalNumber
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PolynomialTest {

    private val qRing = PolynomialRing(RationalField)
    private val qOps = FieldPolynomialOps(RationalField)

    private fun q(n: Int, d: Int = 1) = RationalNumber.of(n, d)

    private fun poly(coeffs: List<RationalNumber>) = Polynomial(coeffs)
    private fun poly(c0: RationalNumber) = Polynomial(listOf(c0))
    private fun poly(c0: RationalNumber, c1: RationalNumber) = Polynomial(listOf(c0, c1))
    private fun poly(c0: RationalNumber, c1: RationalNumber, c2: RationalNumber) = Polynomial(listOf(c0, c1, c2))
    private fun poly(c0: RationalNumber, c1: RationalNumber, c2: RationalNumber, c3: RationalNumber) = Polynomial(listOf(c0, c1, c2, c3))
    private fun poly(c0: RationalNumber, c1: RationalNumber, c2: RationalNumber, c3: RationalNumber, c4: RationalNumber) = Polynomial(listOf(c0, c1, c2, c3, c4))

    @Test
    fun zeroPolynomialHasDegreeMinusOne() {
        assertEquals(-1, Polynomial.zero<RationalNumber>().degree)
        assertTrue(Polynomial.zero<RationalNumber>().isZero())
    }

    @Test
    fun constantPolynomialHasDegreeZero() {
        val p = poly(q(5))
        assertEquals(0, p.degree)
    }

    @Test
    fun linearPolynomialHasDegreeOne() {
        val p = poly(q(3), q(2))
        assertEquals(1, p.degree)
        assertEquals(q(2), p.leadingCoefficient())
    }

    @Test
    fun additionOfPolynomials() {
        val a = poly(q(1), q(2), q(3))
        val b = poly(q(4), q(5))
        val sum = qRing.add(a, b)
        assertEquals(poly(q(5), q(7), q(3)), sum)
    }

    @Test
    fun subtractionOfPolynomials() {
        val a = poly(q(5), q(3))
        val b = poly(q(2), q(3))
        val diff = qRing.subtract(a, b)
        assertEquals(poly(q(3)), diff)
    }

    @Test
    fun multiplicationOfPolynomials() {
        val a = poly(q(1), q(1))
        val b = poly(q(-1), q(1))
        val prod = qRing.multiply(a, b)
        assertEquals(poly(q(-1), q(0), q(1)), prod)
    }

    @Test
    fun xSquaredMinusOneFactors() {
        val xMinus1 = poly(q(-1), q(1))
        val xPlus1 = poly(q(1), q(1))
        val prod = qRing.multiply(xMinus1, xPlus1)
        assertEquals(poly(q(-1), q(0), q(1)), prod)
    }

    @Test
    fun evaluationAtPoint() {
        val p = poly(q(1), q(2), q(3))
        val result = qRing.evaluate(p, q(2))
        assertEquals(q(17), result)
    }

    @Test
    fun evaluationOfZeroPolynomial() {
        val result = qRing.evaluate(Polynomial.zero(), q(42))
        assertEquals(q(0), result)
    }

    @Test
    fun euclideanDivision() {
        val a = poly(q(-1), q(0), q(0), q(0), q(1))
        val b = poly(q(-1), q(0), q(0), q(1))
        val (quotient, remainder) = qOps.divideAndRemainder(a, b)

        val reconstructed = qRing.add(qRing.multiply(quotient, b), remainder)
        assertEquals(a, reconstructed)
    }

    @Test
    fun gcdOfX4Minus1AndX3Minus1() {
        val x4m1 = poly(q(-1), q(0), q(0), q(0), q(1))
        val x3m1 = poly(q(-1), q(0), q(0), q(1))
        val g = qOps.gcd(x4m1, x3m1)
        assertEquals(poly(q(-1), q(1)), g)
    }

    @Test
    fun gcdWithZeroIsOther() {
        val p = poly(q(1), q(2), q(1))
        val g = qOps.gcd(p, Polynomial.zero())
        assertEquals(q(1), g.leadingCoefficient())
    }

    @Test
    fun multiplyByZeroIsZero() {
        val p = poly(q(1), q(2), q(3))
        val result = qRing.multiply(p, Polynomial.zero())
        assertTrue(result.isZero())
    }

    @Test
    fun polynomialRingIdentity() {
        val p = poly(q(3), q(-2), q(1))
        assertEquals(p, qRing.multiply(p, qRing.one))
        assertEquals(p, qRing.add(p, qRing.zero))
    }

    @Test
    fun polynomialOverFiniteField() {
        val f5 = ZpField(5)
        val ring = PolynomialRing(f5)
        val ops = FieldPolynomialOps(f5)

        val a = Polynomial(listOf(1, 2, 3))
        val b = Polynomial(listOf(4, 1))
        val prod = ring.multiply(a, b)

        val evaluated = ring.evaluate(prod, 2)
        val evalA = ring.evaluate(a, 2)
        val evalB = ring.evaluate(b, 2)
        assertEquals(f5.multiply(evalA, evalB), evaluated)
    }

    @Test
    fun scalarMultiplication() {
        val p = poly(q(1), q(2), q(3))
        val scaled = qRing.scalarMultiply(q(2), p)
        assertEquals(poly(q(2), q(4), q(6)), scaled)
    }

    @Test
    fun xPolynomial() {
        val x = qRing.x()
        assertEquals(1, x.degree)
        assertEquals(q(1), x.leadingCoefficient())
        assertEquals(q(0), x[0])
    }

    @Test
    fun polynomialAdditionIsCommutative() {
        val a = poly(q(1), q(2))
        val b = poly(q(3), q(4), q(5))
        assertEquals(qRing.add(a, b), qRing.add(b, a))
    }

    @Test
    fun polynomialMultiplicationIsAssociative() {
        val a = poly(q(1), q(1))
        val b = poly(q(2), q(3))
        val c = poly(q(-1), q(1))
        val ab_c = qRing.multiply(qRing.multiply(a, b), c)
        val a_bc = qRing.multiply(a, qRing.multiply(b, c))
        assertEquals(ab_c, a_bc)
    }

    @Test
    fun distributivityInPolynomialRing() {
        val a = poly(q(1), q(2))
        val b = poly(q(3))
        val c = poly(q(-1), q(0), q(1))
        val lhs = qRing.multiply(a, qRing.add(b, c))
        val rhs = qRing.add(qRing.multiply(a, b), qRing.multiply(a, c))
        assertEquals(lhs, rhs)
    }
}
