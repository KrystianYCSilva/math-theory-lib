package mathsets.graph

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import mathsets.kernel.RealNumber

class AdvancedGraphAlgorithmsTest : FunSpec({
    test("graph factory builds complete graph K_n") {
        val k4 = GraphFactory.completeGraph(4)
        k4.vertices.size shouldBe 4
        k4.edges().size shouldBe 6
        k4.vertices.all { v -> k4.neighbors(v).size == 3 } shouldBe true
    }

    test("graph factory builds complete bipartite K_m,n") {
        val k23 = GraphFactory.completeBipartite(2, 3)
        k23.vertices.size shouldBe 5
        k23.edges().size shouldBe 6
    }

    test("edmonds karp computes max flow correctly") {
        val graph = WeightedDirectedGraph.of(
            vertices = setOf("S", "A", "B", "T"),
            edges = listOf(
                WeightedEdge("S", "A", 10.0),
                WeightedEdge("S", "B", 5.0),
                WeightedEdge("A", "B", 15.0),
                WeightedEdge("A", "T", 10.0),
                WeightedEdge("B", "T", 10.0)
            )
        )

        val result = MaxFlow.edmondsKarp(graph, "S", "T")
        result.value shouldBe 15.0
    }

    test("hopcroft karp finds maximum matching") {
        val left = setOf("L1", "L2", "L3")
        val right = setOf("R1", "R2", "R3")
        val adjacency = mapOf(
            "L1" to setOf("R1", "R2"),
            "L2" to setOf("R2", "R3"),
            "L3" to setOf("R3")
        )
        val graph = BipartiteGraph(left, right, adjacency)

        val matching = HopcroftKarp.maximumMatching(graph)
        matching.size shouldBe 3
    }

    test("greedy coloring is proper") {
        val graph = GraphFactory.completeGraph(4)
        val coloring = Coloring.greedy(graph)
        Coloring.isProper(graph, coloring) shouldBe true
        coloring.values.toSet().size shouldBe 4
    }

    test("chromatic number of K_n equals n") {
        for (n in 1..5) {
            val graph = GraphFactory.completeGraph(n)
            Coloring.chromaticNumber(graph) shouldBe n
        }
    }

    test("chromatic number of bipartite graph equals 2") {
        val graph = GraphFactory.completeBipartite(3, 3)
        Coloring.chromaticNumber(graph) shouldBe 2
    }

    test("isomorphism detects identical graphs") {
        val g1 = UndirectedGraph.of(
            vertices = setOf(1, 2, 3, 4),
            edges = listOf(
                Edge(1, 2), Edge(2, 3), Edge(3, 4), Edge(4, 1)
            )
        )
        val g2 = UndirectedGraph.of(
            vertices = setOf("A", "B", "C", "D"),
            edges = listOf(
                Edge("A", "B"), Edge("B", "C"), Edge("C", "D"), Edge("D", "A")
            )
        )

        GraphIsomorphism.areIsomorphic(g1, g2) shouldBe true
    }

    test("isomorphism rejects non-isomorphic graphs") {
        val square = UndirectedGraph.of(
            vertices = setOf(1, 2, 3, 4),
            edges = listOf(
                Edge(1, 2), Edge(2, 3), Edge(3, 4), Edge(4, 1)
            )
        )
        val path4 = UndirectedGraph.of(
            vertices = setOf(1, 2, 3, 4),
            edges = listOf(
                Edge(1, 2), Edge(2, 3), Edge(3, 4)
            )
        )

        GraphIsomorphism.areIsomorphic(square, path4) shouldBe false
    }

    test("planarity test detects K5 as non-planar") {
        val k5 = GraphFactory.completeGraph(5)
        PlanarityTest.isNonPlanar(k5) shouldBe true
    }

    test("planarity test detects K3,3 as non-planar") {
        val k33 = GraphFactory.completeBipartite(3, 3)
        PlanarityTest.isNonPlanar(k33) shouldBe true
    }

    test("spectral helpers return correct theoretical spectrum for K_n") {
        val spectrum = SpectralGraph.completeGraphSpectrum(5)
        spectrum[4] shouldBe 1
        spectrum[-1] shouldBe 4
    }

    test("adjacency matrix builds correctly") {
        val graph = UndirectedGraph.of(
            vertices = setOf("A", "B", "C"),
            edges = listOf(Edge("A", "B"), Edge("B", "C"))
        )
        val (order, matrix) = SpectralGraph.adjacencyMatrix(graph)
        order.size shouldBe 3
        val idx = order.withIndex().associate { it.value to it.index }
        matrix[idx["A"]!!, idx["B"]!!] shouldBe RealNumber.ONE
        matrix[idx["A"]!!, idx["C"]!!] shouldBe RealNumber.ZERO
    }
})
