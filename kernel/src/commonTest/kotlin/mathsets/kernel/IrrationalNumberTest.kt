package mathsets.kernel

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class IrrationalNumberTest : FunSpec({
    test("symbolic identity is preserved") {
        IrrationalNumber.PI.toString() shouldBe "pi"
        IrrationalNumber.SQRT2.toString() shouldBe "sqrt(2)"
    }

    test("custom irrational can be created from approximation") {
        val alpha = IrrationalNumber.of("alpha", RealNumber.parse("1.2345"))
        alpha.symbol shouldBe "alpha"
        alpha.toReal() shouldBe RealNumber.parse("1.2345")
    }

    test("irrational arithmetic returns real approximation") {
        val sum = IrrationalNumber.PI + IrrationalNumber.E
        sum.shouldBeInstanceOf<RealNumber>()
        (IrrationalNumber.SQRT2 * RealNumber.of(2)).signum() shouldBe 1
    }

    test("ordering is derived from approximation") {
        (IrrationalNumber.PI > IrrationalNumber.E) shouldBe true
    }
})

