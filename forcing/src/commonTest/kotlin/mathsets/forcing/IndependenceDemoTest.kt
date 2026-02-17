package mathsets.forcing

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class IndependenceDemoTest : FunSpec({
    test("independence demo provides one model where CH analogue holds and one where fails") {
        val (holds, fails) = IndependenceDemo.summary()
        holds shouldBe true
        fails shouldBe false
    }
})

