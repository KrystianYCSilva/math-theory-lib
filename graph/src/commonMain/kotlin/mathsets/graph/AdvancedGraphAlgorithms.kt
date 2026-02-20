package mathsets.graph

import mathsets.kernel.RealNumber
import mathsets.linalg.Matrix

/**
 * Utilities to build canonical graph families.
 */
object GraphFactory {
    /**
     * Complete graph K_n on vertices 0..n-1.
     */
    fun completeGraph(order: Int): UndirectedGraph<Int> {
        require(order > 0) { "order must be positive." }
        val vertices = (0 until order).toSet()
        val edges = mutableListOf<Edge<Int>>()
        for (i in 0 until order) {
            for (j in i + 1 until order) {
                edges += Edge(i, j)
            }
        }
        return UndirectedGraph.of(vertices, edges)
    }

    /**
     * Complete bipartite graph K_{m,n} on integer-labeled vertices.
     */
    fun completeBipartite(leftSize: Int, rightSize: Int): UndirectedGraph<Int> {
        require(leftSize > 0 && rightSize > 0) { "leftSize and rightSize must be positive." }
        val left = (0 until leftSize).toList()
        val right = (leftSize until leftSize + rightSize).toList()
        val vertices = (left + right).toSet()
        val edges = mutableListOf<Edge<Int>>()
        for (u in left) {
            for (v in right) {
                edges += Edge(u, v)
            }
        }
        return UndirectedGraph.of(vertices, edges)
    }

    /**
     * Directed path graph with weighted edges i -> i+1.
     */
    fun weightedDirectedPath(vertexCount: Int, weight: Double = 1.0): WeightedDirectedGraph<Int> {
        require(vertexCount > 0) { "vertexCount must be positive." }
        require(weight.isFinite()) { "weight must be finite." }
        val vertices = (0 until vertexCount).toSet()
        val edges = (0 until vertexCount - 1).map { i -> WeightedEdge(i, i + 1, weight) }
        return WeightedDirectedGraph.of(vertices, edges)
    }
}

/**
 * Maximum-flow result.
 */
data class MaxFlowResult<V>(
    val source: V,
    val sink: V,
    val value: Double,
    val flow: Map<Pair<V, V>, Double>
)

/**
 * Edmonds-Karp max-flow algorithm.
 */
object MaxFlow {
    /**
     * Computes a maximum flow in a capacitated directed graph.
     */
    fun <V> edmondsKarp(
        graph: WeightedDirectedGraph<V>,
        source: V,
        sink: V
    ): MaxFlowResult<V> {
        require(source in graph.vertices) { "source must belong to graph." }
        require(sink in graph.vertices) { "sink must belong to graph." }
        require(source != sink) { "source and sink must be distinct." }

        val capacity = mutableMapOf<Pair<V, V>, Double>()
        for (edge in graph.edges()) {
            require(edge.weight >= 0.0) { "max flow requires non-negative capacities." }
            val key = edge.from to edge.to
            capacity[key] = (capacity[key] ?: 0.0) + edge.weight
            if (edge.to to edge.from !in capacity) {
                capacity[edge.to to edge.from] = 0.0
            }
        }

        val residual = capacity.toMutableMap()
        val adjacency = mutableMapOf<V, MutableSet<V>>()
        for ((u, v) in residual.keys) {
            adjacency.getOrPut(u) { mutableSetOf() } += v
            adjacency.getOrPut(v) { mutableSetOf() }
        }

        var maxFlow = 0.0

        while (true) {
            val parent = mutableMapOf<V, V?>()
            val queue = mutableListOf<V>()
            var head = 0

            parent[source] = null
            queue += source

            while (head < queue.size && sink !in parent) {
                val current = queue[head]
                head += 1

                for (neighbor in adjacency[current].orEmpty()) {
                    val residualCapacity = residual[current to neighbor] ?: 0.0
                    if (residualCapacity <= 1e-12 || neighbor in parent) continue
                    parent[neighbor] = current
                    queue += neighbor
                }
            }

            if (sink !in parent) break

            var augment = Double.POSITIVE_INFINITY
            var v = sink
            while (v != source) {
                val u = parent.getValue(v)!!
                augment = minOf(augment, residual[u to v] ?: 0.0)
                v = u
            }

            v = sink
            while (v != source) {
                val u = parent.getValue(v)!!
                residual[u to v] = (residual[u to v] ?: 0.0) - augment
                residual[v to u] = (residual[v to u] ?: 0.0) + augment
                v = u
            }

            maxFlow += augment
        }

        val flow = mutableMapOf<Pair<V, V>, Double>()
        for (edge in graph.edges()) {
            val sent = residual[edge.to to edge.from] ?: 0.0
            flow[edge.from to edge.to] = sent
        }

        return MaxFlowResult(source, sink, maxFlow, flow)
    }
}

/**
 * Immutable bipartite graph with explicit left/right partition.
 */
data class BipartiteGraph<L, R>(
    val left: Set<L>,
    val right: Set<R>,
    private val adjacency: Map<L, Set<R>>
) {
    init {
        require(left.isNotEmpty()) { "left partition cannot be empty." }
        require(right.isNotEmpty()) { "right partition cannot be empty." }
        require(adjacency.keys.all { it in left }) { "adjacency keys must be in left partition." }
        require(adjacency.values.flatten().all { it in right }) { "adjacency targets must be in right partition." }
    }

    /**
     * Right-neighbors of a left vertex.
     */
    fun neighbors(leftVertex: L): Set<R> {
        require(leftVertex in left) { "vertex must belong to left partition." }
        return adjacency[leftVertex].orEmpty()
    }
}

/**
 * Matching result for bipartite graphs.
 */
data class BipartiteMatching<L, R>(
    val leftToRight: Map<L, R>
) {
    val size: Int get() = leftToRight.size
}

/**
 * Hopcroft-Karp maximum matching.
 */
object HopcroftKarp {
    /**
     * Computes a maximum-cardinality bipartite matching.
     */
    fun <L, R> maximumMatching(graph: BipartiteGraph<L, R>): BipartiteMatching<L, R> {
        val pairLeft = graph.left.associateWith { null as R? }.toMutableMap()
        val pairRight = graph.right.associateWith { null as L? }.toMutableMap()
        val distance = mutableMapOf<L, Int>()

        fun bfs(): Boolean {
            val queue = mutableListOf<L>()
            var head = 0
            var found = false

            for (u in graph.left) {
                if (pairLeft.getValue(u) == null) {
                    distance[u] = 0
                    queue += u
                } else {
                    distance[u] = Int.MAX_VALUE
                }
            }

            while (head < queue.size) {
                val u = queue[head]
                head += 1

                for (v in graph.neighbors(u)) {
                    val matchedLeft = pairRight.getValue(v)
                    if (matchedLeft == null) {
                        found = true
                    } else if (distance.getValue(matchedLeft) == Int.MAX_VALUE) {
                        distance[matchedLeft] = distance.getValue(u) + 1
                        queue += matchedLeft
                    }
                }
            }

            return found
        }

        fun dfs(u: L): Boolean {
            for (v in graph.neighbors(u)) {
                val matchedLeft = pairRight.getValue(v)
                if (matchedLeft == null ||
                    (distance.getValue(matchedLeft) == distance.getValue(u) + 1 && dfs(matchedLeft))
                ) {
                    pairLeft[u] = v
                    pairRight[v] = u
                    return true
                }
            }

            distance[u] = Int.MAX_VALUE
            return false
        }

        while (bfs()) {
            for (u in graph.left) {
                if (pairLeft.getValue(u) == null) dfs(u)
            }
        }

        val matching = pairLeft.filterValues { it != null }.mapValues { it.value!! }
        return BipartiteMatching(matching)
    }
}

/**
 * Coloring algorithms.
 */
object Coloring {
    /**
     * Greedy coloring in the given vertex order.
     */
    fun <V> greedy(graph: Graph<V>, ordering: List<V> = graph.vertices.toList()): Map<V, Int> {
        require(ordering.toSet() == graph.vertices) { "ordering must include all graph vertices exactly once." }

        val colors = mutableMapOf<V, Int>()
        for (vertex in ordering) {
            val forbidden = graph.neighbors(vertex).mapNotNull { colors[it] }.toSet()
            var color = 0
            while (color in forbidden) color += 1
            colors[vertex] = color
        }
        return colors
    }

    /**
     * Checks whether a coloring is proper.
     */
    fun <V> isProper(graph: Graph<V>, coloring: Map<V, Int>): Boolean {
        if (coloring.keys != graph.vertices) return false
        return graph.edges().all { edge -> coloring[edge.from] != coloring[edge.to] }
    }

    /**
     * Exact chromatic number by backtracking for small graphs.
     *
     * Falls back to greedy upper bound when graph size exceeds [maxExactVertices].
     */
    fun <V> chromaticNumber(graph: Graph<V>, maxExactVertices: Int = 12): Int {
        require(graph.vertices.isNotEmpty()) { "graph must have at least one vertex." }

        val order = graph.vertices.sortedByDescending { graph.neighbors(it).size }
        if (order.size > maxExactVertices) {
            return (greedy(graph, order).values.maxOrNull() ?: 0) + 1
        }

        var best = order.size
        val assignment = mutableMapOf<V, Int>()

        fun backtrack(index: Int, usedColors: Int) {
            if (usedColors >= best) return
            if (index == order.size) {
                best = minOf(best, usedColors)
                return
            }

            val vertex = order[index]
            val forbidden = graph.neighbors(vertex).mapNotNull { assignment[it] }.toSet()

            for (color in 0 until usedColors) {
                if (color in forbidden) continue
                assignment[vertex] = color
                backtrack(index + 1, usedColors)
                assignment.remove(vertex)
            }

            assignment[vertex] = usedColors
            backtrack(index + 1, usedColors + 1)
            assignment.remove(vertex)
        }

        backtrack(0, 0)
        return best
    }
}

/**
 * Graph isomorphism checks (backtracking, small graphs).
 */
object GraphIsomorphism {
    /**
     * Checks isomorphism between undirected graphs.
     */
    fun <A, B> areIsomorphic(
        first: UndirectedGraph<A>,
        second: UndirectedGraph<B>,
        maxVertices: Int = 10
    ): Boolean {
        if (first.vertices.size != second.vertices.size) return false
        if (first.edges().size != second.edges().size) return false
        if (first.vertices.size > maxVertices) return false

        val firstVertices = first.vertices.toList()
        val secondVertices = second.vertices.toList()

        val firstDegrees = firstVertices.associateWith { first.neighbors(it).size }
        val secondDegrees = secondVertices.associateWith { second.neighbors(it).size }

        if (firstDegrees.values.sorted() != secondDegrees.values.sorted()) return false

        val secondByDegree = secondVertices.groupBy { secondDegrees.getValue(it) }
        val mapping = mutableMapOf<A, B>()
        val used = mutableSetOf<B>()

        fun isConsistent(a: A, b: B): Boolean {
            for ((alreadyA, alreadyB) in mapping) {
                val firstAdjacent = alreadyA in first.neighbors(a)
                val secondAdjacent = alreadyB in second.neighbors(b)
                if (firstAdjacent != secondAdjacent) return false
            }
            return true
        }

        fun backtrack(index: Int): Boolean {
            if (index == firstVertices.size) return true
            val a = firstVertices[index]
            val degree = firstDegrees.getValue(a)

            for (candidate in secondByDegree[degree].orEmpty()) {
                if (candidate in used) continue
                if (!isConsistent(a, candidate)) continue

                mapping[a] = candidate
                used += candidate

                if (backtrack(index + 1)) return true

                mapping.remove(a)
                used.remove(candidate)
            }

            return false
        }

        return backtrack(0)
    }
}

/**
 * Planarity heuristics and signature detection.
 */
object PlanarityTest {
    /**
     * Returns true when graph is detected as non-planar by implemented criteria.
     */
    fun <V> isNonPlanar(graph: UndirectedGraph<V>): Boolean {
        val n = graph.vertices.size
        val m = graph.edges().size

        if (n >= 3 && m > 3 * n - 6) return true
        if (n == 5 && m == 10) return true // K5

        if (n == 6 && m == 9 && graph.vertices.all { graph.neighbors(it).size == 3 } && isBipartite(graph)) {
            return true // K3,3 signature for 6-vertex 3-regular bipartite case.
        }

        return false
    }

    /**
     * Returns true when no non-planar signature is detected.
     */
    fun <V> isPlanarByHeuristics(graph: UndirectedGraph<V>): Boolean = !isNonPlanar(graph)

    private fun <V> isBipartite(graph: UndirectedGraph<V>): Boolean {
        val color = mutableMapOf<V, Int>()
        for (start in graph.vertices) {
            if (start in color) continue

            val queue = mutableListOf(start)
            var head = 0
            color[start] = 0

            while (head < queue.size) {
                val current = queue[head]
                head += 1
                val currentColor = color.getValue(current)

                for (neighbor in graph.neighbors(current)) {
                    val neighborColor = color[neighbor]
                    if (neighborColor == null) {
                        color[neighbor] = 1 - currentColor
                        queue += neighbor
                    } else if (neighborColor == currentColor) {
                        return false
                    }
                }
            }
        }
        return true
    }
}

/**
 * Spectral helpers.
 */
object SpectralGraph {
    /**
     * Theoretical spectrum of K_n:
     * - eigenvalue n-1 with multiplicity 1
     * - eigenvalue -1 with multiplicity n-1
     */
    fun completeGraphSpectrum(order: Int): Map<Int, Int> {
        require(order > 0) { "order must be positive." }
        return mapOf((order - 1) to 1, -1 to (order - 1))
    }

    /**
     * Adjacency matrix over real numbers.
     */
    fun <V> adjacencyMatrix(graph: UndirectedGraph<V>): Pair<List<V>, Matrix<RealNumber>> {
        val (order, matrix) = Adjacency.matrix(graph)
        val realMatrix = Matrix.fill(matrix.rows, matrix.cols) { r, c ->
            if (matrix[r, c] == 0) RealNumber.ZERO else RealNumber.ONE
        }
        return order to realMatrix
    }
}
