package mathsets.set

import io.kotest.core.spec.style.FunSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.set
import io.kotest.property.arbitrary.map
import io.kotest.property.forAll

class SetAlgebraPropertiesTest : FunSpec({

    // Simple arbitrary producing finite extensional MathSet<Int>
    fun mathSetArb(): Arb<MathSet<Int>> = Arb.set(Arb.int()).map { ExtensionalSet(it.toSet()) }

    test("Union is commutative") {
        forAll(mathSetArb(), mathSetArb()) { a, b ->
            val left = a union b
            val right = b union a
            // Compare by checking equality of membership for elements in union of elements
            val universe = ExtensionalSet((a.elements().toSet() + b.elements().toSet()))
            universe.elements().all { x -> (x in left) == (x in right) }
        }
    }

    test("Intersect is commutative") {
        forAll(mathSetArb(), mathSetArb()) { a, b ->
            val left = a intersect b
            val right = b intersect a
            val universe = ExtensionalSet((a.elements().toSet() + b.elements().toSet()))
            universe.elements().all { x -> (x in left) == (x in right) }
        }
    }

    test("Union is associative") {
        forAll(mathSetArb(), mathSetArb(), mathSetArb()) { a, b, c ->
            val left = (a union b) union c
            val right = a union (b union c)
            val universe = ExtensionalSet((a.elements().toSet() + b.elements().toSet() + c.elements().toSet()))
            universe.elements().all { x -> (x in left) == (x in right) }
        }
    }

    test("Idempotence: A union A == A") {
        forAll(mathSetArb()) { a ->
            val left = a union a
            val universe = ExtensionalSet(a.elements().toSet())
            universe.elements().all { x -> (x in left) == (x in a) }
        }
    }

    test("Identity: A union Empty == A and A intersect Empty == Empty") {
        forAll(mathSetArb()) { a ->
            val empty = MathSet.empty<Int>()
            val union = a union empty
            val intersect = a intersect empty
            val universe = ExtensionalSet(a.elements().toSet())
            val unionOk = universe.elements().all { x -> (x in union) == (x in a) }
            val intersectOk = universe.elements().all { x -> (x in intersect) == false }
            unionOk && intersectOk
        }
    }

    test("De Morgan: (A ∪ B)^c = A^c ∩ B^c (element-wise)") {
        forAll(mathSetArb(), mathSetArb(), mathSetArb()) { a, b, u ->
            // universe is u ∪ a ∪ b to ensure complements taken against a finite universe
            val universeSet = a.elements().toSet() + b.elements().toSet() + u.elements().toSet()
            val universe = ExtensionalSet(universeSet)
            universe.elements().all { x ->
                val left = !(x in (a union b))
                val right = (!(x in a)) && (!(x in b))
                left == right
            }
        }
    }

})
