package mathsets.set

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PowerSetTest {
    @Test
    fun finitePowerSetCount() {
        val a = MathSet.of(1,2,3)
        val p = a.powerSet()
        assertEquals(8, p.elements().count())
    }

    @Test
    fun powerSetContainsSubset() {
        val a = MathSet.of(1,2,3)
        val p = a.powerSet()
        val subset = MathSet.of(1)
        assertTrue(subset in p)
    }
}