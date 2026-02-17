package mathsets.combinatorics

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class RamseyTest : FunSpec({
    test("finds a monochromatic clique when one is explicitly present") {
        val vertices = setOf(1, 2, 3, 4)
        val special = setOf(1, 2, 3)

        val clique = Ramsey.findMonochromaticClique(vertices, cliqueSize = 3) { a, b ->
            if (a in special && b in special) 1 else 0
        }

        clique shouldBe setOf(1, 2, 3)
    }

    test("search bounds finds R(3,3)=6 in finite brute force") {
        Ramsey.searchBounds(cliqueSize = 3, colors = 2, maxVertices = 6) shouldBe 6
    }
})

