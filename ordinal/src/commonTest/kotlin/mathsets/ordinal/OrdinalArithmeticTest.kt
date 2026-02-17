package mathsets.ordinal

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import mathsets.kernel.NaturalNumber

class OrdinalArithmeticTest : FunSpec({
    test("omega plus one is not equal to one plus omega") {
        val omega = Ordinal.OMEGA
        val one = Ordinal.ONE

        (omega + one == one + omega) shouldBe false
    }

    test("omega times two equals omega plus omega") {
        val omega = Ordinal.OMEGA
        val two = Ordinal.finite(2)

        (omega * two) shouldBe (omega + omega)
    }

    test("omega squared is greater than omega times every tested finite n") {
        val omega = Ordinal.OMEGA
        val omegaSquared = Ordinal.omegaPower(2)

        checkAll(Arb.int(1, 60)) { n ->
            val omegaTimesN = omega * Ordinal.finite(n)
            (omegaSquared > omegaTimesN) shouldBe true
        }
    }

    test("cnf normalization merges equal exponents") {
        val raw = Ordinal.cnf(
            listOf(
                CNFTerm(NaturalNumber.ONE, NaturalNumber.ONE),
                CNFTerm(NaturalNumber.ONE, NaturalNumber.of(2)),
                CNFTerm(NaturalNumber.ZERO, NaturalNumber.of(3))
            )
        )

        raw shouldBe Ordinal.cnf(
            listOf(
                CNFTerm(NaturalNumber.ONE, NaturalNumber.of(3)),
                CNFTerm(NaturalNumber.ZERO, NaturalNumber.of(3))
            )
        )
    }
})

