package mathsets.set

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class MathSetBasicsTest {
    @Test
    fun extensionalContainsWorks() {
        val a = ExtensionalSet(setOf(1,2,3))
        assertTrue(2 in a)
        assertFalse(5 in a)
    }

    @Test
    fun intensionalContainsWorks() {
        // Use a finite extensional domain to avoid sealed-type anonymous object issues
        val naturalsDomain = ExtensionalSet((0..100).toSet())
        val evens = IntensionalSet(naturalsDomain) { it % 2 == 0 }
        assertTrue(4 in evens)
        assertFalse(5 in evens)
    }
}
