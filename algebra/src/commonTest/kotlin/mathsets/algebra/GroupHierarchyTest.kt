package mathsets.algebra

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GroupHierarchyTest {

    @Test
    fun cyclicGroupZ1IsTrival() {
        val g = CyclicGroup(1)
        assertEquals(setOf(0), g.elements())
        assertEquals(0, g.op(0, 0))
        assertEquals(0, g.inverse(0))
    }

    @Test
    fun cyclicGroupZ6SatisfiesGroupAxioms() {
        val g = CyclicGroup(6)
        assertTrue(AlgebraicLaws.verifyAbelianGroupAxioms(g, g.elements()))
    }

    @Test
    fun cyclicGroupZ7SatisfiesGroupAxioms() {
        val g = CyclicGroup(7)
        assertTrue(AlgebraicLaws.verifyAbelianGroupAxioms(g, g.elements()))
    }

    @Test
    fun cyclicGroupGeneratorHasCorrectOrder() {
        val g = CyclicGroup(12)
        assertEquals(12, g.orderOf(g.generator))
    }

    @Test
    fun cyclicGroupElementOrderDividesGroupOrder() {
        val g = CyclicGroup(12)
        for (e in g.elements()) {
            val order = g.orderOf(e)
            assertEquals(0, 12 % order, "Order of $e ($order) must divide 12")
        }
    }

    @Test
    fun permutationGroupS3SatisfiesGroupAxioms() {
        val s3 = PermutationGroup(3)
        assertTrue(AlgebraicLaws.verifyGroupAxioms(s3, s3.elements()))
    }

    @Test
    fun permutationGroupS3HasCorrectOrder() {
        val s3 = PermutationGroup(3)
        assertEquals(6, s3.elements().size)
        assertEquals(6L, s3.order())
    }

    @Test
    fun permutationGroupS3IsNotAbelian() {
        val s3 = PermutationGroup(3)
        assertFalse(AlgebraicLaws.verifyCommutativity(s3, s3.elements()))
    }

    @Test
    fun permutationGroupS4HasCorrectOrder() {
        val s4 = PermutationGroup(4)
        assertEquals(24, s4.elements().size)
    }

    @Test
    fun permutationIdentityIsIdentity() {
        val id = Permutation.identity(5)
        assertTrue(id.isIdentity())
        assertEquals(1, id.order())
    }

    @Test
    fun permutationCompositionAndInverse() {
        val sigma = Permutation.cycle(4, 0, 1, 2)
        val inv = sigma.invert()
        assertTrue(sigma.compose(inv).isIdentity())
        assertTrue(inv.compose(sigma).isIdentity())
    }

    @Test
    fun permutationTranspositionHasOrder2() {
        val t = Permutation.transposition(5, 1, 3)
        assertEquals(2, t.order())
        assertEquals(-1, t.sign())
    }

    @Test
    fun permutation3CycleHasOrder3() {
        val c = Permutation.cycle(5, 0, 2, 4)
        assertEquals(3, c.order())
        assertEquals(1, c.sign())
    }

    @Test
    fun alternatingGroupA3Has3Elements() {
        val s3 = PermutationGroup(3)
        val a3 = s3.alternatingGroupElements()
        assertEquals(3, a3.size)
        assertTrue(a3.all { it.sign() == 1 })
    }

    @Test
    fun dihedralGroupD3SatisfiesGroupAxioms() {
        val d3 = DihedralGroup(3)
        assertTrue(AlgebraicLaws.verifyGroupAxioms(d3, d3.elements()))
    }

    @Test
    fun dihedralGroupD4SatisfiesGroupAxioms() {
        val d4 = DihedralGroup(4)
        assertTrue(AlgebraicLaws.verifyGroupAxioms(d4, d4.elements()))
    }

    @Test
    fun dihedralGroupD3HasCorrectOrder() {
        val d3 = DihedralGroup(3)
        assertEquals(6, d3.order())
        assertEquals(6, d3.elements().size)
    }

    @Test
    fun dihedralGroupD4IsNotAbelian() {
        val d4 = DihedralGroup(4)
        assertFalse(AlgebraicLaws.verifyCommutativity(d4, d4.elements()))
    }

    @Test
    fun dihedralGroupReflectionHasOrder2() {
        val d5 = DihedralGroup(5)
        val s = d5.s
        assertEquals(d5.identity, d5.op(s, s))
    }

    @Test
    fun dihedralGroupRotationHasOrderN() {
        val d5 = DihedralGroup(5)
        var current = d5.identity
        for (i in 1..5) {
            current = d5.op(current, d5.r)
        }
        assertEquals(d5.identity, current)
    }
}
