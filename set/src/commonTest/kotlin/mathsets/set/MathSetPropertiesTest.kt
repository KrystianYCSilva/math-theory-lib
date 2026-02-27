package mathsets.set

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.set
import io.kotest.property.checkAll

class MathSetPropertiesTest : FunSpec({

    test("union is commutative for finite extensional MathSet<Int>") {
        checkAll(
            Arb.set(Arb.int(-1000..1000), 0..20),
            Arb.set(Arb.int(-1000..1000), 0..20)
        ) { a, b ->
            val setA = mathSetOf(a)
            val setB = mathSetOf(b)

            (setA union setB).materialize() shouldBe (setB union setA).materialize()
        }
    }

    test("materialize preserves membership for finite MathSet<Int>") {
        checkAll(Arb.set(Arb.int(-1000..1000), 0..20)) { data ->
            val s = mathSetOf(data)
            val materialized = s.materialize()

            data.forEach { element ->
                (element in s) shouldBe (element in materialized)
            }
        }
    }
})

