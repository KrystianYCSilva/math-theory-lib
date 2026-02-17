package mathsets.kernel

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.shouldBe

class CardinalityTest : FunSpec({
    test("finite cardinalities are ordered by n") {
        Cardinality.Finite(NaturalNumber.of(2))
            .compareTo(Cardinality.Finite(NaturalNumber.of(5))) shouldBeLessThan 0
    }

    test("finite is smaller than countably infinite") {
        Cardinality.Finite(NaturalNumber.of(100))
            .compareTo(Cardinality.CountablyInfinite) shouldBeLessThan 0
    }

    test("countably infinite is smaller than uncountable") {
        Cardinality.CountablyInfinite.compareTo(Cardinality.Uncountable) shouldBeLessThan 0
    }

    test("unknown compares equal only with unknown") {
        Cardinality.Unknown.compareTo(Cardinality.Unknown) shouldBe 0
        Cardinality.Unknown.compareTo(Cardinality.Finite(NaturalNumber.ONE)) shouldBeGreaterThan 0
    }
})

