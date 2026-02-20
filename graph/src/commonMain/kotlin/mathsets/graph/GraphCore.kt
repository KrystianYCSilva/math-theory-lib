package mathsets.graph

import mathsets.linalg.Matrix

/**
 * Unweighted edge.
 */
data class Edge<V>(
    val from: V,
    val to: V
)

/**
 * Weighted edge.
 */
data class WeightedEdge<V>(
    val from: V,
    val to: V,
    val weight: Double
) {
    init {
        require(weight.isFinite()) { "edge weight must be finite." }
    }
}

/**
 * Base graph contract.
 */
interface Graph<V> {
    val vertices: Set<V>

    fun neighbors(vertex: V): Set<V>

    fun edges(): Set<Edge<V>>
}

/**
 * Base weighted graph contract.
 */
interface WeightedGraph<V> {
    val vertices: Set<V>
    val directed: Boolean

    fun neighbors(vertex: V): Set<Pair<V, Double>>

    fun edges(): Set<WeightedEdge<V>>
}

/**
 * Immutable undirected graph.
 */
class UndirectedGraph<V> private constructor(
    override val vertices: Set<V>,
    private val adjacency: Map<V, Set<V>>
) : Graph<V> {
    override fun neighbors(vertex: V): Set<V> {
        require(vertex in vertices) { "vertex is not in graph." }
        return adjacency[vertex].orEmpty()
    }

    override fun edges(): Set<Edge<V>> {
        val seen = mutableSetOf<Pair<V, V>>()
        val result = mutableSetOf<Edge<V>>()
        for ((from, neighbors) in adjacency) {
            for (to in neighbors) {
                if ((to to from) !in seen) {
                    seen += from to to
                    result += Edge(from, to)
                }
            }
        }
        return result
    }

    companion object {
        /**
         * Builds an undirected graph from vertices and edges.
         */
        fun <V> of(vertices: Set<V>, edges: Iterable<Edge<V>>): UndirectedGraph<V> {
            require(vertices.isNotEmpty()) { "graph must have at least one vertex." }
            val map = vertices.associateWith { mutableSetOf<V>() }.toMutableMap()

            for (edge in edges) {
                require(edge.from in vertices && edge.to in vertices) {
                    "edge endpoints must belong to vertices."
                }
                map.getValue(edge.from) += edge.to
                map.getValue(edge.to) += edge.from
            }

            return UndirectedGraph(vertices, map.mapValues { it.value.toSet() })
        }
    }
}

/**
 * Immutable directed graph.
 */
class DirectedGraph<V> private constructor(
    override val vertices: Set<V>,
    private val adjacency: Map<V, Set<V>>
) : Graph<V> {
    override fun neighbors(vertex: V): Set<V> {
        require(vertex in vertices) { "vertex is not in graph." }
        return adjacency[vertex].orEmpty()
    }

    override fun edges(): Set<Edge<V>> = adjacency.entries.flatMap { (from, toSet) ->
        toSet.map { to -> Edge(from, to) }
    }.toSet()

    companion object {
        /**
         * Builds a directed graph from vertices and edges.
         */
        fun <V> of(vertices: Set<V>, edges: Iterable<Edge<V>>): DirectedGraph<V> {
            require(vertices.isNotEmpty()) { "graph must have at least one vertex." }
            val map = vertices.associateWith { mutableSetOf<V>() }.toMutableMap()

            for (edge in edges) {
                require(edge.from in vertices && edge.to in vertices) {
                    "edge endpoints must belong to vertices."
                }
                map.getValue(edge.from) += edge.to
            }

            return DirectedGraph(vertices, map.mapValues { it.value.toSet() })
        }
    }
}

/**
 * Immutable weighted undirected graph.
 */
class WeightedUndirectedGraph<V> private constructor(
    override val vertices: Set<V>,
    private val adjacency: Map<V, Set<Pair<V, Double>>>
) : WeightedGraph<V> {
    override val directed: Boolean = false

    override fun neighbors(vertex: V): Set<Pair<V, Double>> {
        require(vertex in vertices) { "vertex is not in graph." }
        return adjacency[vertex].orEmpty()
    }

    override fun edges(): Set<WeightedEdge<V>> {
        val seen = mutableSetOf<Pair<V, V>>()
        val result = mutableSetOf<WeightedEdge<V>>()
        for ((from, neighbors) in adjacency) {
            for ((to, weight) in neighbors) {
                if ((to to from) !in seen) {
                    seen += from to to
                    result += WeightedEdge(from, to, weight)
                }
            }
        }
        return result
    }

    companion object {
        /**
         * Builds a weighted undirected graph.
         */
        fun <V> of(vertices: Set<V>, edges: Iterable<WeightedEdge<V>>): WeightedUndirectedGraph<V> {
            require(vertices.isNotEmpty()) { "graph must have at least one vertex." }
            val map = vertices.associateWith { mutableSetOf<Pair<V, Double>>() }.toMutableMap()

            for (edge in edges) {
                require(edge.from in vertices && edge.to in vertices) {
                    "edge endpoints must belong to vertices."
                }
                map.getValue(edge.from) += edge.to to edge.weight
                map.getValue(edge.to) += edge.from to edge.weight
            }

            return WeightedUndirectedGraph(vertices, map.mapValues { it.value.toSet() })
        }
    }
}

/**
 * Immutable weighted directed graph.
 */
class WeightedDirectedGraph<V> private constructor(
    override val vertices: Set<V>,
    private val adjacency: Map<V, Set<Pair<V, Double>>>
) : WeightedGraph<V> {
    override val directed: Boolean = true

    override fun neighbors(vertex: V): Set<Pair<V, Double>> {
        require(vertex in vertices) { "vertex is not in graph." }
        return adjacency[vertex].orEmpty()
    }

    override fun edges(): Set<WeightedEdge<V>> = adjacency.entries.flatMap { (from, toSet) ->
        toSet.map { (to, weight) -> WeightedEdge(from, to, weight) }
    }.toSet()

    companion object {
        /**
         * Builds a weighted directed graph.
         */
        fun <V> of(vertices: Set<V>, edges: Iterable<WeightedEdge<V>>): WeightedDirectedGraph<V> {
            require(vertices.isNotEmpty()) { "graph must have at least one vertex." }
            val map = vertices.associateWith { mutableSetOf<Pair<V, Double>>() }.toMutableMap()

            for (edge in edges) {
                require(edge.from in vertices && edge.to in vertices) {
                    "edge endpoints must belong to vertices."
                }
                map.getValue(edge.from) += edge.to to edge.weight
            }

            return WeightedDirectedGraph(vertices, map.mapValues { it.value.toSet() })
        }
    }
}

/**
 * Adjacency representations.
 */
object Adjacency {
    /**
     * Adjacency-list map for unweighted graphs.
     */
    fun <V> list(graph: Graph<V>): Map<V, Set<V>> =
        graph.vertices.associateWith { vertex -> graph.neighbors(vertex) }

    /**
     * Adjacency matrix (0/1) and corresponding vertex order.
     */
    fun <V> matrix(graph: Graph<V>): Pair<List<V>, Matrix<Int>> {
        require(graph.vertices.isNotEmpty()) { "adjacency matrix requires at least one vertex." }
        val order = graph.vertices.toList()
        val index = order.withIndex().associate { it.value to it.index }

        val matrix = Matrix.fill(order.size, order.size) { row, col ->
            val from = order[row]
            val to = order[col]
            if (to in graph.neighbors(from)) 1 else 0
        }

        require(index.size == order.size)
        return order to matrix
    }

    /**
     * Weighted adjacency matrix and corresponding vertex order.
     */
    fun <V> weightedMatrix(
        graph: WeightedGraph<V>,
        absent: Double = Double.POSITIVE_INFINITY
    ): Pair<List<V>, Matrix<Double>> {
        require(graph.vertices.isNotEmpty()) { "adjacency matrix requires at least one vertex." }
        val order = graph.vertices.toList()

        val matrix = Matrix.fill(order.size, order.size) { row, col ->
            val from = order[row]
            val to = order[col]
            if (row == col) {
                0.0
            } else {
                graph.neighbors(from).firstOrNull { (vertex, _) -> vertex == to }?.second ?: absent
            }
        }
        return order to matrix
    }
}
