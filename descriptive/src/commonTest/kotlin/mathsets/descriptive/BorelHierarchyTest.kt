package mathsets.descriptive

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import mathsets.set.mathSetOf

class BorelHierarchyTest : FunSpec({
    test("classifies open and closed sets at level 1") {
        val open = BorelSet.Open(mathSetOf(1, 2))
        val closed = BorelSet.Closed(mathSetOf(1, 2))

        BorelHierarchy.classify(open) shouldBe BorelLevel.SIGMA_0_1
        BorelHierarchy.classify(closed) shouldBe BorelLevel.PI_0_1
    }

    test("union and intersection of level 1 sets reach level 2") {
        val open = BorelSet.Open(mathSetOf(1))
        val closed = BorelSet.Closed(mathSetOf(2))

        val sigma2 = BorelSet.CountableUnion(listOf(open, closed))
        val pi2 = BorelSet.CountableIntersection(listOf(open, closed))

        BorelHierarchy.classify(sigma2) shouldBe BorelLevel.SIGMA_0_2
        BorelHierarchy.classify(pi2) shouldBe BorelLevel.PI_0_2
    }

    test("complement swaps sigma and pi levels") {
        val open = BorelSet.Open(mathSetOf(1))
        val closed = BorelSet.Closed(mathSetOf(1))

        BorelHierarchy.classify(BorelSet.Complement(open)) shouldBe BorelLevel.PI_0_1
        BorelHierarchy.classify(BorelSet.Complement(closed)) shouldBe BorelLevel.SIGMA_0_1
    }
})

