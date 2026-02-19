package mathsets.algebra

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HomomorphismTest {

    @Test
    fun projectionZ6ToZ3IsHomomorphism() {
        val z6 = CyclicGroup(6)
        val z3 = CyclicGroup(3)
        val phi = GroupHomomorphism(z6, z3) { it % 3 }
        assertTrue(phi.verifyHomomorphism(z6.elements()))
    }

    @Test
    fun kernelOfProjectionZ6ToZ3() {
        val z6 = CyclicGroup(6)
        val z3 = CyclicGroup(3)
        val phi = GroupHomomorphism(z6, z3) { it % 3 }
        val ker = phi.kernel(z6.elements())
        assertEquals(setOf(0, 3), ker)
    }

    @Test
    fun imageOfProjectionZ6ToZ3() {
        val z6 = CyclicGroup(6)
        val z3 = CyclicGroup(3)
        val phi = GroupHomomorphism(z6, z3) { it % 3 }
        val img = phi.image(z6.elements())
        assertEquals(z3.elements(), img)
    }

    @Test
    fun projectionZ6ToZ3IsSurjective() {
        val z6 = CyclicGroup(6)
        val z3 = CyclicGroup(3)
        val phi = GroupHomomorphism(z6, z3) { it % 3 }
        assertTrue(phi.isSurjective(z6.elements(), z3.elements()))
    }

    @Test
    fun identityHomomorphismIsInjective() {
        val z5 = CyclicGroup(5)
        val phi = GroupHomomorphism(z5, z5) { it }
        assertTrue(phi.isInjective(z5.elements()))
    }

    @Test
    fun firstIsomorphismTheoremForZ6ToZ3() {
        val z6 = CyclicGroup(6)
        val z3 = CyclicGroup(3)
        val phi = GroupHomomorphism(z6, z3) { it % 3 }
        val ker = phi.kernel(z6.elements())
        val img = phi.image(z6.elements())
        val kerSubgroup = Subgroup(z6, ker)
        val quotient = QuotientGroup(z6, kerSubgroup, z6.elements())
        assertEquals(img.size, quotient.order())
    }

    @Test
    fun ringHomomorphismZToZ6() {
        val z6 = ZnRing(6)
        val hom = RingHomomorphism(z6, z6) { it }
        assertTrue(hom.verifyHomomorphism(z6.elements()))
    }

    @Test
    fun subgroupOfZ6() {
        val z6 = CyclicGroup(6)
        val h = Subgroup(z6, setOf(0, 2, 4))
        assertEquals(3, h.order())
        assertTrue(AlgebraicLaws.verifyGroupAxioms(z6, h.elements))
    }

    @Test
    fun lagrangeTheoremForZ12Subgroups() {
        val z12 = CyclicGroup(12)
        val h4 = Subgroup(z12, setOf(0, 3, 6, 9))
        assertTrue(AlgebraicLaws.verifyLagrangeTheorem(z12, z12.elements(), h4))
        val h3 = Subgroup(z12, setOf(0, 4, 8))
        assertTrue(AlgebraicLaws.verifyLagrangeTheorem(z12, z12.elements(), h3))
    }

    @Test
    fun quotientGroupZ6ModZ3() {
        val z6 = CyclicGroup(6)
        val h = Subgroup(z6, setOf(0, 3))
        assertTrue(h.isNormal(z6.elements()))
        val quotient = QuotientGroup(z6, h, z6.elements())
        assertEquals(3, quotient.order())
        assertTrue(AlgebraicLaws.verifyGroupAxioms(quotient, quotient.elements()))
    }
}
