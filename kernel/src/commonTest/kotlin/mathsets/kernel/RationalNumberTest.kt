package mathsets.kernel

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

class RationalNumberTest : FunSpec({
    
    test("Normalization (2/4 == 1/2)") {
        RationalNumber.of(2, 4) shouldBe RationalNumber.of(1, 2)
    }

    test("Addition") {
        // 1/2 + 1/3 = 5/6
        (RationalNumber.of(1, 2) + RationalNumber.of(1, 3)) shouldBe RationalNumber.of(5, 6)
    }

    test("Addition commutativity") {
        checkAll(Arb.int(-1000, 1000), Arb.int(1, 1000), Arb.int(-1000, 1000), Arb.int(1, 1000)) { n1, d1, n2, d2 ->
            val r1 = RationalNumber.of(n1, d1)
            val r2 = RationalNumber.of(n2, d2)
            (r1 + r2) shouldBe (r2 + r1)
        }
    }

    test("Multiplication inverse (a/b * b/a = 1)") {
        checkAll(Arb.int(1, 1000), Arb.int(1, 1000)) { n, d ->
            val r1 = RationalNumber.of(n, d)
            val r2 = RationalNumber.of(d, n)
            (r1 * r2) shouldBe RationalNumber.ONE
        }
    }
})
