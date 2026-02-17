package mathsets.set

import io.kotest.core.spec.style.FunSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.set
import io.kotest.property.forAll

class SetAlgebraPropertiesTest : FunSpec({
    fun mathSetArb(): Arb<MathSet<Int>> =
        Arb.set(Arb.int(-100, 100), range = 0..40).map { ExtensionalSet(it) }

    fun membershipEquals(a: MathSet<Int>, b: MathSet<Int>, universe: Set<Int>): Boolean =
        universe.all { x -> (x in a) == (x in b) }

    test("Union and intersection are commutative") {
        forAll(mathSetArb(), mathSetArb()) { a, b ->
            val universe = a.elements().toSet() + b.elements().toSet()
            membershipEquals(a union b, b union a, universe) &&
                membershipEquals(a intersect b, b intersect a, universe)
        }
    }

    test("Union is associative") {
        forAll(mathSetArb(), mathSetArb(), mathSetArb()) { a, b, c ->
            val universe = a.elements().toSet() + b.elements().toSet() + c.elements().toSet()
            membershipEquals((a union b) union c, a union (b union c), universe)
        }
    }

    test("Idempotence and identity") {
        forAll(mathSetArb()) { a ->
            val empty = MathSet.empty<Int>()
            val universe = a.elements().toSet()
            membershipEquals(a union a, a, universe) &&
                membershipEquals(a union empty, a, universe) &&
                membershipEquals(a intersect empty, empty, universe)
        }
    }

    test("Absorption law") {
        forAll(mathSetArb(), mathSetArb()) { a, b ->
            val universe = a.elements().toSet() + b.elements().toSet()
            membershipEquals(a union (a intersect b), a, universe)
        }
    }

    test("De Morgan + involution") {
        forAll(mathSetArb(), mathSetArb(), mathSetArb()) { a, b, u ->
            val universe = ExtensionalSet(a.elements().toSet() + b.elements().toSet() + u.elements().toSet())
            val universeElements = universe.elements().toSet()
            val deMorganHolds = membershipEquals(
                (a union b).complement(universe),
                a.complement(universe) intersect b.complement(universe),
                universeElements
            )
            val involutionHolds = membershipEquals(
                a.complement(universe).complement(universe),
                a,
                universeElements
            )
            deMorganHolds && involutionHolds
        }
    }

    test("Extensionality on finite universe") {
        forAll(mathSetArb(), mathSetArb(), mathSetArb()) { a, b, u ->
            val universe = ExtensionalSet(a.elements().toSet() + b.elements().toSet() + u.elements().toSet())
            val sameMembership = universe.elements().all { x -> (x in a) == (x in b) }
            val extensionality = if (sameMembership) {
                (a isSubsetOf b) && (b isSubsetOf a)
            } else {
                true
            }
            extensionality
        }
    }
})

