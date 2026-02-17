package mathsets.kernel

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import mathsets.kernel.platform.BI_ONE
import mathsets.kernel.platform.BI_ZERO
import mathsets.kernel.platform.toInt

class GeneratorTest : FunSpec({
    test("naturals generates 0..99 for first 100 values") {
        val actual = Generators.naturals().take(100).map { it.value.toInt() }.toList()
        actual shouldContainExactly (0 until 100).toList()
    }

    test("integers uses zigzag enumeration") {
        val actual = Generators.integers().take(10).toList()
        val expected = listOf(
            IntegerNumber.of(0),
            IntegerNumber.of(1),
            IntegerNumber.of(-1),
            IntegerNumber.of(2),
            IntegerNumber.of(-2),
            IntegerNumber.of(3),
            IntegerNumber.of(-3),
            IntegerNumber.of(4),
            IntegerNumber.of(-4),
            IntegerNumber.of(5)
        )
        actual shouldContainExactly expected
    }

    test("rationals are normalized and duplicate-free on prefix") {
        val sample = Generators.rationals().take(500).toList()
        sample.toSet().size shouldBe sample.size

        sample.forEach { rational ->
            (rational.denominator > BI_ZERO) shouldBe true
            gcd(rational.numerator.abs(), rational.denominator) shouldBe BI_ONE
        }
    }
})

private fun gcd(
    a: mathsets.kernel.platform.BigInteger,
    b: mathsets.kernel.platform.BigInteger
): mathsets.kernel.platform.BigInteger {
    var x = a
    var y = b
    while (y != BI_ZERO) {
        val temp = y
        y = x % y
        x = temp
    }
    return x
}
