package mathsets.algebra

import mathsets.kernel.IntegerNumber
import mathsets.kernel.RationalNumber
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RingHierarchyTest {

    @Test
    fun integerRingAdditiveGroupIdentityAndInverse() {
        val group = IntegerRing.additiveGroup()
        val a = IntegerNumber.of(7)
        assertEquals(IntegerNumber.ZERO, group.identity)
        assertEquals(a, group.op(a, group.identity))
        assertEquals(group.identity, group.op(a, group.inverse(a)))
    }

    @Test
    fun integerRingAdditiveGroupIsCommutative() {
        val group = IntegerRing.additiveGroup()
        val a = IntegerNumber.of(3)
        val b = IntegerNumber.of(-5)
        assertEquals(group.op(a, b), group.op(b, a))
    }

    @Test
    fun integerRingAdditiveGroupIsAssociative() {
        val group = IntegerRing.additiveGroup()
        val a = IntegerNumber.of(2)
        val b = IntegerNumber.of(-3)
        val c = IntegerNumber.of(7)
        assertEquals(group.op(group.op(a, b), c), group.op(a, group.op(b, c)))
    }

    @Test
    fun integerRingDistributivity() {
        val elements = (-3..3).map { IntegerNumber.of(it) }.toSet()
        assertTrue(AlgebraicLaws.verifyDistributivity(IntegerRing, elements))
    }

    @Test
    fun integerRingIsIntegralDomain() {
        val elements = (-5..5).map { IntegerNumber.of(it) }.toSet()
        assertTrue(elements.all { a ->
            elements.all { b ->
                if (IntegerRing.multiply(a, b) == IntegerRing.zero) {
                    a == IntegerRing.zero || b == IntegerRing.zero
                } else true
            }
        })
    }

    @Test
    fun integerRingGcd() {
        val a = IntegerNumber.of(12)
        val b = IntegerNumber.of(8)
        val g = IntegerRing.gcd(a, b)
        assertEquals(IntegerNumber.of(4), g.let { if (it < IntegerNumber.ZERO) -it else it })
    }

    @Test
    fun integerRingEuclideanDivision() {
        val a = IntegerNumber.of(17)
        val b = IntegerNumber.of(5)
        val (q, r) = IntegerRing.divideAndRemainder(a, b)
        assertEquals(a, IntegerRing.add(IntegerRing.multiply(q, b), r))
    }

    @Test
    fun rationalFieldReciprocalProperty() {
        val testValues = listOf(
            RationalNumber.ONE,
            RationalNumber.of(-1, 1),
            RationalNumber.of(1, 2),
            RationalNumber.of(3, 7),
            RationalNumber.of(-5, 3)
        )
        testValues.forEach { a ->
            assertEquals(
                RationalField.one,
                RationalField.multiply(a, RationalField.reciprocal(a)),
                "a * a^-1 should be 1 for a=$a"
            )
        }
    }

    @Test
    fun rationalFieldDistributivity() {
        val a = RationalNumber.of(1, 2)
        val b = RationalNumber.of(3, 7)
        val c = RationalNumber.of(-2, 5)
        val lhs = RationalField.multiply(a, RationalField.add(b, c))
        val rhs = RationalField.add(RationalField.multiply(a, b), RationalField.multiply(a, c))
        assertEquals(lhs, rhs)
    }

    @Test
    fun rationalFieldCommutativity() {
        val elements = listOf(
            RationalNumber.ZERO,
            RationalNumber.ONE,
            RationalNumber.of(1, 2),
            RationalNumber.of(3, 7)
        ).toSet()
        assertTrue(AlgebraicLaws.verifyCommutativity(RationalField.multiplicativeMonoid(), elements))
    }

    @Test
    fun znRingZ6SatisfiesRingAxioms() {
        val zn = ZnRing(6)
        assertTrue(AlgebraicLaws.verifyRingAxioms(zn, zn.elements()))
    }

    @Test
    fun znRingZ6IsNotField() {
        val zn = ZnRing(6)
        assertFalse(zn.isField())
    }

    @Test
    fun znRingZ6HasZeroDivisors() {
        val zn = ZnRing(6)
        assertEquals(0, zn.multiply(2, 3))
    }

    @Test
    fun zpFieldZ5SatisfiesFieldAxioms() {
        val zp = ZpField(5)
        assertTrue(AlgebraicLaws.verifyFieldAxioms(zp, zp.elements()))
    }

    @Test
    fun zpFieldZ7SatisfiesFieldAxioms() {
        val zp = ZpField(7)
        assertTrue(AlgebraicLaws.verifyFieldAxioms(zp, zp.elements()))
    }

    @Test
    fun zpFieldZ5ReciprocalsAreCorrect() {
        val zp = ZpField(5)
        for (a in 1 until 5) {
            assertEquals(1, zp.multiply(a, zp.reciprocal(a)))
        }
    }

    @Test
    fun zpFieldZ11SatisfiesFieldAxioms() {
        val zp = ZpField(11)
        assertTrue(AlgebraicLaws.verifyFieldAxioms(zp, zp.elements()))
    }

    @Test
    fun complexFieldAdditiveGroupIdentityAndInverse() {
        val group = ComplexField.additiveGroup()
        val z = mathsets.kernel.ComplexNumber.of(3, -2)
        assertEquals(mathsets.kernel.ComplexNumber.ZERO, group.identity)
        assertEquals(z, group.op(z, group.identity))
        assertEquals(group.identity, group.op(z, group.inverse(z)))
    }

    @Test
    fun complexFieldAdditiveGroupIsCommutative() {
        val group = ComplexField.additiveGroup()
        val a = mathsets.kernel.ComplexNumber.of(1, 2)
        val b = mathsets.kernel.ComplexNumber.of(3, -1)
        assertEquals(group.op(a, b), group.op(b, a))
    }
}
