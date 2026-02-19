package mathsets.construction.real

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import mathsets.kernel.RationalNumber
import mathsets.kernel.RealNumber

class DedekindRealTest : FunSpec({
    test("cauchy dedekind roundtrip holds on finite decimal sample") {
        val samples = (-50..49).map { n -> RealNumber.of(n) / RealNumber.of(10) }
        RealIsomorphism.verifyRoundTrip(samples) shouldBe true
    }

    test("dedekind arithmetic matches kernel projection") {
        val a = DedekindReal.fromKernel(RealNumber.parse("1.5"))
        val b = DedekindReal.fromKernel(RealNumber.parse("0.25"))

        (a + b).toKernel() shouldBe RealNumber.parse("1.75")
        (a * b).toKernel() shouldBe RealNumber.parse("0.375")
    }

    test("dedekind cut sampled lower-set sanity check") {
        val bound = CauchyReal.fromKernel(RealNumber.parse("1.2")).toDedekindReal().cut
        val samples = setOf(
            RationalNumber.of(-1, 1),
            RationalNumber.of(0, 1),
            RationalNumber.of(1, 1),
            RationalNumber.of(6, 5),
            RationalNumber.of(13, 10)
        )

        bound.isLowerSetOnSamples(samples) shouldBe true
        bound.hasNoGreatestElementOnSamples(samples) shouldBe true
    }
})
