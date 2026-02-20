package mathsets.graph

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

class GraphAlgorithmsTest : FunSpec({
    val unweighted = UndirectedGraph.of(
        vertices = setOf("A", "B", "C", "D", "E"),
        edges = listOf(
            Edge("A", "B"),
            Edge("A", "C"),
            Edge("B", "D"),
            Edge("C", "D"),
            Edge("D", "E")
        )
    )

    test("bfs computes unweighted shortest-path distances") {
        val distances = BFS.distances(unweighted, "A")
        distances["A"] shouldBe 0
        distances["B"] shouldBe 1
        distances["C"] shouldBe 1
        distances["D"] shouldBe 2
        distances["E"] shouldBe 3
    }

    test("bfs path reconstructs shortest route") {
        val path = BFS.path(unweighted, "A", "E")
        path shouldContainExactly listOf("A", "B", "D", "E")
    }

    test("dfs traversal visits all reachable vertices") {
        val order = DFS.traverse(unweighted, "A")
        order.toSet() shouldBe unweighted.vertices
    }

    test("adjacency matrix has expected edge indicators") {
        val (order, matrix) = Adjacency.matrix(unweighted)
        val index = order.withIndex().associate { it.value to it.index }

        matrix[index.getValue("A"), index.getValue("B")] shouldBe 1
        matrix[index.getValue("A"), index.getValue("E")] shouldBe 0
        matrix[index.getValue("D"), index.getValue("E")] shouldBe 1
    }

    val weightedDirected = WeightedDirectedGraph.of(
        vertices = setOf("A", "B", "C", "D"),
        edges = listOf(
            WeightedEdge("A", "B", 4.0),
            WeightedEdge("A", "C", 1.0),
            WeightedEdge("C", "B", 2.0),
            WeightedEdge("B", "D", 1.0),
            WeightedEdge("C", "D", 5.0)
        )
    )

    test("dijkstra returns shortest distances and path") {
        val result = Dijkstra.shortestPaths(weightedDirected, "A")
        result.distances["A"] shouldBe 0.0
        result.distances["D"] shouldBe 4.0
        result.pathTo("D") shouldContainExactly listOf("A", "C", "B", "D")
    }

    test("bellman-ford handles negative edges without negative cycle") {
        val graph = WeightedDirectedGraph.of(
            vertices = setOf("S", "A", "B", "T"),
            edges = listOf(
                WeightedEdge("S", "A", 4.0),
                WeightedEdge("S", "B", 5.0),
                WeightedEdge("A", "B", -2.0),
                WeightedEdge("B", "T", 3.0),
                WeightedEdge("A", "T", 6.0)
            )
        )

        val result = BellmanFord.shortestPaths(graph, "S")
        result.hasNegativeCycle shouldBe false
        result.distances["T"] shouldBe 5.0
        result.pathTo("T") shouldContainExactly listOf("S", "A", "B", "T")
    }

    val weightedUndirected = WeightedUndirectedGraph.of(
        vertices = setOf(1, 2, 3, 4),
        edges = listOf(
            WeightedEdge(1, 2, 1.0),
            WeightedEdge(1, 3, 4.0),
            WeightedEdge(2, 3, 2.0),
            WeightedEdge(2, 4, 7.0),
            WeightedEdge(3, 4, 3.0)
        )
    )

    test("kruskal and prim agree on mst total weight") {
        val kruskal = Kruskal.minimumSpanningTree(weightedUndirected)
        val prim = Prim.minimumSpanningTree(weightedUndirected, start = 1)

        kruskal.connected shouldBe true
        prim.connected shouldBe true
        kruskal.edges.size shouldBe 3
        prim.edges.size shouldBe 3
        kruskal.totalWeight shouldBe 6.0
        prim.totalWeight shouldBe 6.0
    }
})
