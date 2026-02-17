package mathsets.cardinal

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import mathsets.set.ExtensionalSet
import mathsets.set.MathSet

class CantorDiagonalTest : FunSpec({
    test("diagonal is never in image for random finite functions S -> P(S)") {
        val points = listOf(0, 1, 2)
        val domain = ExtensionalSet(points.toSet())

        checkAll(Arb.list(Arb.boolean(), 9..9)) { bits ->
            val mapping: (Int) -> MathSet<Int> = { x ->
                val offset = x * points.size
                val subset = points.filterIndexed { index, _ -> bits[offset + index] }.toSet()
                ExtensionalSet(subset)
            }

            CantorDiagonal.verifyNotSurjective(domain, mapping) shouldBe true
        }
    }
})
