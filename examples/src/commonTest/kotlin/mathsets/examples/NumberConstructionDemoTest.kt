package mathsets.examples

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import mathsets.kernel.RationalNumber

class NumberConstructionDemoTest : FunSpec({
    test("construction snapshot preserves natural value in Z and Q") {
        val snapshot = NumberConstructionDemo.fromNatural(3)
        snapshot.integer.toKernel().toString() shouldBe "3"
        snapshot.rational.toKernel() shouldBe RationalNumber.of(3, 1)
    }

    test("walkthrough contains three explicit steps") {
        NumberConstructionDemo.walkthrough(5).size shouldBe 3
    }
})

