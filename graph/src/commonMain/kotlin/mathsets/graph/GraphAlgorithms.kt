package mathsets.graph

/**
 * Breadth-first-search algorithms.
 */
object BFS {
    /**
     * BFS traversal order from [start].
     */
    fun <V> traverse(graph: Graph<V>, start: V): List<V> {
        require(start in graph.vertices) { "start vertex must belong to graph." }

        val visited = mutableSetOf<V>()
        val queue = mutableListOf<V>()
        val order = mutableListOf<V>()

        visited += start
        queue += start
        var head = 0

        while (head < queue.size) {
            val current = queue[head]
            head += 1
            order += current

            for (neighbor in graph.neighbors(current)) {
                if (neighbor !in visited) {
                    visited += neighbor
                    queue += neighbor
                }
            }
        }

        return order
    }

    /**
     * Unweighted shortest-path distances from [start].
     */
    fun <V> distances(graph: Graph<V>, start: V): Map<V, Int> {
        require(start in graph.vertices) { "start vertex must belong to graph." }

        val distances = mutableMapOf<V, Int>()
        val queue = mutableListOf<V>()
        distances[start] = 0
        queue += start
        var head = 0

        while (head < queue.size) {
            val current = queue[head]
            head += 1
            val base = distances.getValue(current)

            for (neighbor in graph.neighbors(current)) {
                if (neighbor !in distances) {
                    distances[neighbor] = base + 1
                    queue += neighbor
                }
            }
        }

        return distances
    }

    /**
     * One shortest unweighted path from [start] to [target], if reachable.
     */
    fun <V> path(graph: Graph<V>, start: V, target: V): List<V>? {
        require(start in graph.vertices) { "start vertex must belong to graph." }
        require(target in graph.vertices) { "target vertex must belong to graph." }

        val previous = mutableMapOf<V, V?>()
        val queue = mutableListOf<V>()
        previous[start] = null
        queue += start
        var head = 0

        while (head < queue.size) {
            val current = queue[head]
            head += 1
            if (current == target) break

            for (neighbor in graph.neighbors(current)) {
                if (neighbor !in previous) {
                    previous[neighbor] = current
                    queue += neighbor
                }
            }
        }

        if (target !in previous) return null
        return rebuildPath(target, previous)
    }
}

/**
 * Depth-first-search algorithms.
 */
object DFS {
    /**
     * DFS traversal order from [start].
     */
    fun <V> traverse(graph: Graph<V>, start: V): List<V> {
        require(start in graph.vertices) { "start vertex must belong to graph." }

        val visited = mutableSetOf<V>()
        val stack = mutableListOf(start)
        val order = mutableListOf<V>()

        while (stack.isNotEmpty()) {
            val current = stack.removeAt(stack.lastIndex)
            if (current in visited) continue

            visited += current
            order += current

            for (neighbor in graph.neighbors(current)) {
                if (neighbor !in visited) stack += neighbor
            }
        }

        return order
    }

    /**
     * Connected components for an undirected graph.
     */
    fun <V> connectedComponents(graph: UndirectedGraph<V>): List<Set<V>> {
        val unvisited = graph.vertices.toMutableSet()
        val components = mutableListOf<Set<V>>()

        while (unvisited.isNotEmpty()) {
            val start = unvisited.first()
            val component = mutableSetOf<V>()
            val stack = mutableListOf(start)

            while (stack.isNotEmpty()) {
                val current = stack.removeAt(stack.lastIndex)
                if (current !in unvisited) continue
                unvisited.remove(current)
                component += current

                for (neighbor in graph.neighbors(current)) {
                    if (neighbor in unvisited) stack += neighbor
                }
            }

            components += component
        }

        return components
    }
}

/**
 * Result of shortest-path algorithms.
 */
data class ShortestPathResult<V>(
    val source: V,
    val distances: Map<V, Double>,
    val previous: Map<V, V?>
) {
    /**
     * Reconstructs one shortest path from source to [target], if reachable.
     */
    fun pathTo(target: V): List<V>? {
        val distance = distances[target] ?: return null
        if (!distance.isFinite()) return null
        if (target !in previous && target != source) return null
        return rebuildPath(target, previous)
    }
}

/**
 * Bellman-Ford result including negative-cycle detection.
 */
data class BellmanFordResult<V>(
    val source: V,
    val distances: Map<V, Double>,
    val previous: Map<V, V?>,
    val hasNegativeCycle: Boolean
) {
    /**
     * Reconstructs one shortest path from source to [target], if available.
     */
    fun pathTo(target: V): List<V>? {
        if (hasNegativeCycle) return null
        val distance = distances[target] ?: return null
        if (!distance.isFinite()) return null
        if (target !in previous && target != source) return null
        return rebuildPath(target, previous)
    }
}

/**
 * Dijkstra shortest paths (non-negative weights).
 */
object Dijkstra {
    /**
     * Computes shortest paths from [source].
     */
    fun <V> shortestPaths(graph: WeightedGraph<V>, source: V): ShortestPathResult<V> {
        require(source in graph.vertices) { "source vertex must belong to graph." }

        val distances = graph.vertices.associateWith { Double.POSITIVE_INFINITY }.toMutableMap()
        val previous = mutableMapOf<V, V?>()
        val queue = MinDistanceQueue<V>()
        distances[source] = 0.0
        previous[source] = null
        queue.push(source, 0.0)

        while (!queue.isEmpty()) {
            val node = queue.pop() ?: break
            val current = node.vertex
            val currentDistance = node.distance
            if (currentDistance > distances.getValue(current)) continue

            for ((neighbor, weight) in graph.neighbors(current)) {
                require(weight >= 0.0) { "Dijkstra requires non-negative weights." }
                val candidate = currentDistance + weight
                if (candidate < distances.getValue(neighbor)) {
                    distances[neighbor] = candidate
                    previous[neighbor] = current
                    queue.push(neighbor, candidate)
                }
            }
        }

        return ShortestPathResult(source, distances, previous)
    }
}

/**
 * Bellman-Ford shortest paths.
 */
object BellmanFord {
    /**
     * Computes shortest paths from [source], marking negative cycles when detected.
     */
    fun <V> shortestPaths(graph: WeightedGraph<V>, source: V): BellmanFordResult<V> {
        require(source in graph.vertices) { "source vertex must belong to graph." }

        val distances = graph.vertices.associateWith { Double.POSITIVE_INFINITY }.toMutableMap()
        val previous = mutableMapOf<V, V?>()
        distances[source] = 0.0
        previous[source] = null

        val edges = graph.edges().toList()

        repeat(graph.vertices.size - 1) {
            var updated = false

            for (edge in edges) {
                updated = relax(edge.from, edge.to, edge.weight, distances, previous) || updated
                if (!graph.directed) {
                    updated = relax(edge.to, edge.from, edge.weight, distances, previous) || updated
                }
            }

            if (!updated) return@repeat
        }

        var hasNegativeCycle = false
        for (edge in edges) {
            if (canRelax(edge.from, edge.to, edge.weight, distances) ||
                (!graph.directed && canRelax(edge.to, edge.from, edge.weight, distances))
            ) {
                hasNegativeCycle = true
                break
            }
        }

        return BellmanFordResult(source, distances, previous, hasNegativeCycle)
    }

    private fun <V> relax(
        from: V,
        to: V,
        weight: Double,
        distances: MutableMap<V, Double>,
        previous: MutableMap<V, V?>
    ): Boolean {
        val sourceDistance = distances.getValue(from)
        if (!sourceDistance.isFinite()) return false

        val candidate = sourceDistance + weight
        if (candidate < distances.getValue(to)) {
            distances[to] = candidate
            previous[to] = from
            return true
        }
        return false
    }

    private fun <V> canRelax(
        from: V,
        to: V,
        weight: Double,
        distances: Map<V, Double>
    ): Boolean {
        val sourceDistance = distances.getValue(from)
        if (!sourceDistance.isFinite()) return false
        return sourceDistance + weight < distances.getValue(to)
    }
}

/**
 * Minimum spanning tree/forest result.
 */
data class MinimumSpanningTree<V>(
    val edges: Set<WeightedEdge<V>>,
    val totalWeight: Double,
    val connected: Boolean
)

/**
 * Kruskal minimum-spanning-tree algorithm.
 */
object Kruskal {
    /**
     * Computes a minimum spanning tree/forest for a weighted undirected graph.
     */
    fun <V> minimumSpanningTree(graph: WeightedUndirectedGraph<V>): MinimumSpanningTree<V> {
        val unionFind = UnionFind(graph.vertices)
        val selected = mutableSetOf<WeightedEdge<V>>()
        var totalWeight = 0.0

        for (edge in graph.edges().sortedBy { it.weight }) {
            if (unionFind.union(edge.from, edge.to)) {
                selected += edge
                totalWeight += edge.weight
            }
        }

        return MinimumSpanningTree(
            edges = selected,
            totalWeight = totalWeight,
            connected = selected.size == graph.vertices.size - 1
        )
    }
}

/**
 * Prim minimum-spanning-tree algorithm.
 */
object Prim {
    /**
     * Computes a minimum spanning tree/forest for a weighted undirected graph.
     */
    fun <V> minimumSpanningTree(
        graph: WeightedUndirectedGraph<V>,
        start: V = graph.vertices.first()
    ): MinimumSpanningTree<V> {
        require(start in graph.vertices) { "start vertex must belong to graph." }

        val visited = mutableSetOf(start)
        val selected = mutableSetOf<WeightedEdge<V>>()
        var totalWeight = 0.0

        while (visited.size < graph.vertices.size) {
            var best: WeightedEdge<V>? = null

            for (from in visited) {
                for ((to, weight) in graph.neighbors(from)) {
                    if (to in visited) continue
                    if (best == null || weight < best!!.weight) {
                        best = WeightedEdge(from, to, weight)
                    }
                }
            }

            val chosen = best ?: break
            selected += chosen
            visited += chosen.to
            totalWeight += chosen.weight
        }

        return MinimumSpanningTree(
            edges = selected,
            totalWeight = totalWeight,
            connected = visited.size == graph.vertices.size
        )
    }
}

private fun <V> rebuildPath(target: V, previous: Map<V, V?>): List<V> {
    val path = mutableListOf<V>()
    var current: V? = target
    while (current != null) {
        path += current
        current = previous[current]
    }
    path.reverse()
    return path
}

private class UnionFind<V>(elements: Set<V>) {
    private val parent = elements.associateWith { it }.toMutableMap()
    private val rank = elements.associateWith { 0 }.toMutableMap()

    fun find(x: V): V {
        val p = parent.getValue(x)
        if (p == x) return x
        val root = find(p)
        parent[x] = root
        return root
    }

    fun union(a: V, b: V): Boolean {
        var rootA = find(a)
        var rootB = find(b)
        if (rootA == rootB) return false

        val rankA = rank.getValue(rootA)
        val rankB = rank.getValue(rootB)

        if (rankA < rankB) {
            val tmp = rootA
            rootA = rootB
            rootB = tmp
        }

        parent[rootB] = rootA
        if (rankA == rankB) {
            rank[rootA] = rankA + 1
        }

        return true
    }
}

private class MinDistanceQueue<V> {
    private data class Entry<V>(val vertex: V, val distance: Double)

    private val heap = mutableListOf<Entry<V>>()

    fun isEmpty(): Boolean = heap.isEmpty()

    fun push(vertex: V, distance: Double) {
        heap += Entry(vertex, distance)
        siftUp(heap.lastIndex)
    }

    fun pop(): Node<V>? {
        if (heap.isEmpty()) return null
        val first = heap.first()
        val last = heap.removeAt(heap.lastIndex)
        if (heap.isNotEmpty()) {
            heap[0] = last
            siftDown(0)
        }
        return Node(first.vertex, first.distance)
    }

    private fun siftUp(startIndex: Int) {
        var index = startIndex
        while (index > 0) {
            val parent = (index - 1) / 2
            if (heap[parent].distance <= heap[index].distance) break
            swap(parent, index)
            index = parent
        }
    }

    private fun siftDown(startIndex: Int) {
        var index = startIndex
        while (true) {
            val left = index * 2 + 1
            val right = left + 1
            var smallest = index

            if (left < heap.size && heap[left].distance < heap[smallest].distance) {
                smallest = left
            }
            if (right < heap.size && heap[right].distance < heap[smallest].distance) {
                smallest = right
            }
            if (smallest == index) break

            swap(index, smallest)
            index = smallest
        }
    }

    private fun swap(i: Int, j: Int) {
        val tmp = heap[i]
        heap[i] = heap[j]
        heap[j] = tmp
    }

    data class Node<V>(val vertex: V, val distance: Double)
}
