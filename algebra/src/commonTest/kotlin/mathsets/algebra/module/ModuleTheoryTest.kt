package mathsets.algebra.module

import mathsets.algebra.RationalField
import mathsets.algebra.ZpField
import mathsets.kernel.RationalNumber
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ModuleTheoryTest {

    @Test
    fun q3HasCanonicalBasis() {
        val q3 = FiniteDimensionalVectorSpace(RationalField, 3)
        val basis = q3.canonicalBasis()

        assertEquals(3, basis.dimension)
        assertEquals(q3.vector(listOf(q(1), q(0), q(0))), basis.vectors[0])
        assertEquals(q3.vector(listOf(q(0), q(1), q(0))), basis.vectors[1])
        assertEquals(q3.vector(listOf(q(0), q(0), q(1))), basis.vectors[2])
    }

    @Test
    fun rankNullityTheoremHoldsOnQ3ToQ2Map() {
        val domain = FiniteDimensionalVectorSpace(RationalField, 3)
        val codomain = FiniteDimensionalVectorSpace(RationalField, 2)

        val map = FiniteDimensionalLinearMap(
            domain = domain,
            codomain = codomain,
            matrix = listOf(
                listOf(q(1), q(0), q(1)),
                listOf(q(0), q(1), q(1))
            )
        )

        val rank = map.rank()
        val nullity = map.nullity()

        assertEquals(2, rank)
        assertEquals(1, nullity)
        assertEquals(domain.dimension, rank + nullity)
    }

    @Test
    fun shortExactSequenceIsVerified() {
        val q1a = FiniteDimensionalVectorSpace(RationalField, 1)
        val q2 = FiniteDimensionalVectorSpace(RationalField, 2)
        val q1b = FiniteDimensionalVectorSpace(RationalField, 1)

        val injective = FiniteDimensionalLinearMap(
            domain = q1a,
            codomain = q2,
            matrix = listOf(
                listOf(q(1)),
                listOf(q(0))
            )
        )

        val surjective = FiniteDimensionalLinearMap(
            domain = q2,
            codomain = q1b,
            matrix = listOf(
                listOf(q(0), q(1))
            )
        )

        val exact = ExactSequence(injective, surjective)
        val shortExact = ShortExactSequence(injective, surjective)

        assertTrue(exact.compositionIsZero())
        assertTrue(exact.isExactAtMiddle())
        assertTrue(shortExact.isShortExact())
    }

    @Test
    fun submoduleValidationOverGF2() {
        val gf2 = ZpField(2)
        val v2 = FiniteDimensionalVectorSpace(gf2, 2)

        val zero = v2.vector(listOf(0, 0))
        val e1 = v2.vector(listOf(1, 0))

        val submodule = Submodule(
            parent = v2,
            elements = setOf(zero, e1),
            validationScalars = setOf(0, 1)
        )

        assertTrue(submodule.contains(zero))
        assertTrue(submodule.contains(e1))
        assertTrue(submodule.isClosedUnderAddition())
        assertTrue(submodule.isClosedUnderScalarMultiplication())
    }

    @Test
    fun tensorProductSupportsAdditionAndScalarMultiplication() {
        val z5 = ZpField(5)
        val tensor = TensorProduct<Int, Int, Int>(z5)

        val t = tensor.of(1, 2)
        val twoT = tensor.scalarMultiply(2, t)
        val threeT = tensor.scalarMultiply(3, t)
        val sum = tensor.add(twoT, threeT)

        assertEquals(tensor.zero(), sum)
    }

    private fun q(numerator: Int, denominator: Int = 1): RationalNumber =
        RationalNumber.of(numerator, denominator)
}
