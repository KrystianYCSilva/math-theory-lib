package mathsets.combinatorics

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import mathsets.kernel.NaturalNumber

class PartitionCalculusTest : FunSpec({
    test("all partitions count matches Bell number for n=3") {
        val partitions = PartitionCalculus.allPartitions(setOf(1, 2, 3))
        partitions.size shouldBe 5
    }

    test("bell number computes expected small values") {
        PartitionCalculus.bellNumber(0) shouldBe NaturalNumber.ONE
        PartitionCalculus.bellNumber(4) shouldBe NaturalNumber.of(15)
    }

    test("finite Erdős-Rado relation matches Ramsey threshold for (3,3)") {
        PartitionCalculus.erdosRadoArrow(n = 5, monochromaticSize = 3, subsetSize = 2, colors = 2) shouldBe false
        PartitionCalculus.erdosRadoArrow(n = 6, monochromaticSize = 3, subsetSize = 2, colors = 2) shouldBe true
    }
})

