package mathsets.kernel

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

class ComplexNumberTest : FunSpec({
    test("i squared is minus one") {
        (ComplexNumber.I * ComplexNumber.I) shouldBe ComplexNumber.of(-1, 0)
    }

    test("multiplication by conjugate produces modulus squared") {
        val z = ComplexNumber.of(3, 4)
        (z * z.conjugate()) shouldBe ComplexNumber.of(25, 0)
        z.modulusSquared() shouldBe RealNumber.of(25)
    }

    test("conjugate is involutive") {
        val z = ComplexNumber.of(7, -11)
        z.conjugate().conjugate() shouldBe z
    }

    test("addition commutativity") {
        checkAll(
            Arb.int(-100, 100),
            Arb.int(-100, 100),
            Arb.int(-100, 100),
            Arb.int(-100, 100)
        ) { ar, ai, br, bi ->
            val left = ComplexNumber.of(ar, ai)
            val right = ComplexNumber.of(br, bi)
            (left + right) shouldBe (right + left)
        }
    }

    test("division by i rotates clockwise by ninety degrees") {
        val z = ComplexNumber.of(1, 1)
        (z / ComplexNumber.I) shouldBe ComplexNumber.of(1, -1)
    }

    test("division by zero complex is rejected") {
        shouldThrow<IllegalArgumentException> {
            ComplexNumber.ONE / ComplexNumber.ZERO
        }
    }
})

