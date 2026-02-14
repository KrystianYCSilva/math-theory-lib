package mathsets.kernel

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.checkAll

class IntegerNumberTest : FunSpec({
    
    test("Addition commutativity") {
        checkAll(Arb.long(), Arb.long()) { a, b ->
            val n1 = IntegerNumber.of(a)
            val n2 = IntegerNumber.of(b)
            (n1 + n2) shouldBe (n2 + n1)
        }
    }

    test("Inverse property (a + (-a) = 0)") {
        checkAll(Arb.long()) { a ->
            val n = IntegerNumber.of(a)
            (n + (-n)) shouldBe IntegerNumber.ZERO
        }
    }

    test("Subtraction definition (a - b = a + (-b))") {
        checkAll(Arb.long(), Arb.long()) { a, b ->
            val n1 = IntegerNumber.of(a)
            val n2 = IntegerNumber.of(b)
            (n1 - n2) shouldBe (n1 + (-n2))
        }
    }
})
