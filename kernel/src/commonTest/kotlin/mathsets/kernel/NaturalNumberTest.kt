package mathsets.kernel

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.checkAll

class NaturalNumberTest : FunSpec({
    
    test("Addition commutativity") {
        checkAll(Arb.long(0, Long.MAX_VALUE), Arb.long(0, Long.MAX_VALUE)) { a, b ->
            val n1 = NaturalNumber.of(a)
            val n2 = NaturalNumber.of(b)
            (n1 + n2) shouldBe (n2 + n1)
        }
    }

    test("Multiplication commutativity") {
         checkAll(Arb.long(0, Long.MAX_VALUE), Arb.long(0, Long.MAX_VALUE)) { a, b ->
             // Limit range to avoid overflow if using standard Long for check, 
             // but BigInteger handles it.
             // However, Arb.long generates Longs, which fit in BigInteger.
             val n1 = NaturalNumber.of(a)
             val n2 = NaturalNumber.of(b)
             (n1 * n2) shouldBe (n2 * n1)
         }
    }

    test("Identity element for addition (0)") {
        checkAll(Arb.long(0, Long.MAX_VALUE)) { a ->
            val n = NaturalNumber.of(a)
            (n + NaturalNumber.ZERO) shouldBe n
        }
    }

    test("Identity element for multiplication (1)") {
        checkAll(Arb.long(0, Long.MAX_VALUE)) { a ->
            val n = NaturalNumber.of(a)
            (n * NaturalNumber.ONE) shouldBe n
        }
    }

    test("Successor vs Addition") {
        checkAll(Arb.long(0, Long.MAX_VALUE)) { a ->
            val n = NaturalNumber.of(a)
            n.succ() shouldBe (n + NaturalNumber.ONE)
        }
    }
})
