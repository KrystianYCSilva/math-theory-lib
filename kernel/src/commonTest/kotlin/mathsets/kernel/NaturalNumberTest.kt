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

    test("Only zero satisfies isZero") {
        checkAll(Arb.long(0, Long.MAX_VALUE)) { a ->
            val n = NaturalNumber.of(a)
            n.isZero() shouldBe (a == 0L)
        }
    }

    test("isPrime classifies small known values") {
        val primes = listOf(2, 3, 5, 7, 11, 13, 17, 19, 97, 9973)
        primes.forEach { n ->
            NaturalNumber.of(n).isPrime() shouldBe true
        }

        val nonPrimes = listOf(0, 1, 4, 6, 8, 9, 10, 12, 21, 100)
        nonPrimes.forEach { n ->
            NaturalNumber.of(n).isPrime() shouldBe false
        }
    }

    test("pow follows repeated multiplication for small values") {
        checkAll(Arb.long(0, 12), Arb.long(0, 8)) { a, b ->
            val base = NaturalNumber.of(a)
            val exponent = NaturalNumber.of(b)
            val expected = (0 until b.toInt()).fold(NaturalNumber.ONE) { acc, _ -> acc * base }
            (base pow exponent) shouldBe expected
        }
    }
})
