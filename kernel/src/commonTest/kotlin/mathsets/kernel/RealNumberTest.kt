package mathsets.kernel

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

class RealNumberTest : FunSpec({
    test("addition commutativity for integer-backed reals") {
        checkAll(Arb.int(-10_000, 10_000), Arb.int(-10_000, 10_000)) { a, b ->
            val left = RealNumber.of(a)
            val right = RealNumber.of(b)
            (left + right) shouldBe (right + left)
        }
    }

    test("multiplication commutativity for integer-backed reals") {
        checkAll(Arb.int(-10_000, 10_000), Arb.int(-10_000, 10_000)) { a, b ->
            val left = RealNumber.of(a)
            val right = RealNumber.of(b)
            (left * right) shouldBe (right * left)
        }
    }

    test("identity elements for addition and multiplication") {
        checkAll(Arb.int(-10_000, 10_000)) { n ->
            val value = RealNumber.of(n)
            (value + RealNumber.ZERO) shouldBe value
            (value * RealNumber.ONE) shouldBe value
        }
    }

    test("order mirrors integer order for integer-backed values") {
        checkAll(Arb.int(-10_000, 10_000), Arb.int(-10_000, 10_000)) { a, b ->
            RealNumber.of(a).compareTo(RealNumber.of(b)) shouldBe a.compareTo(b)
        }
    }

    test("division by zero is rejected") {
        shouldThrow<IllegalArgumentException> {
            RealNumber.of(3) / RealNumber.ZERO
        }
    }

    test("abs and pow are consistent on small values") {
        RealNumber.of(-3).abs() shouldBe RealNumber.of(3)
        (RealNumber.of(2) pow 10) shouldBe RealNumber.of(1024)
    }

    test("rational conversion keeps expected finite values") {
        RealNumber.of(RationalNumber.of(1, 2)) shouldBe RealNumber.parse("0.5")
        RealNumber.of(RationalNumber.of(-3, 4)) shouldBe RealNumber.parse("-0.75")
    }
})

