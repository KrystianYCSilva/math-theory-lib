---
name: graph-algorithm-implementation
description: >-
  Patterns for implementing graph data structures and algorithms (traversal, shortest paths, connectivity).
  Use when: modeling relations as graphs, finding paths, computing connected components, or analyzing network structure.
---

# Skill: Graph Algorithm Implementation

This skill provides patterns for implementing graph theory algorithms in Kotlin Multiplatform. It covers graph representations, traversal algorithms, shortest path computation, and connectivity analysis using immutable, type-safe structures.

## How to Represent Graphs

### Adjacency List Representation (Recommended)

For most algorithms, use an adjacency map for O(1) neighbor lookup:

```kotlin
/**
 * A directed graph G = (V, E) represented by adjacency lists.
 * @param V the vertex set
 * @param adjacencyMap v -> {neighbors of v}
 */
data class DirectedGraph<V>(
    val vertices: MathSet<V>,
    private val adjacencyMap: Map<V, Set<V>>
) {
    /** Out-neighbors of v: N⁺(v) = {u | (v,u) ∈ E} */
    fun neighbors(v: V): Set<V> = adjacencyMap[v].orEmpty()
    
    /** Edge existence: (u,v) ∈ E? */
    fun hasEdge(u: V, v: V): Boolean = v in neighbors(u)
    
    /** Number of edges: |E| */
    val edgeCount: Int
        get() = adjacencyMap.values.sumOf { it.size }
}

/** Undirected graph: symmetric adjacency */
class UndirectedGraph<V>(vertices: MathSet<V>, adjacencyMap: Map<V, Set<V>>) 
    : DirectedGraph<V>(vertices, adjacencyMap) {
    
    init {
        // Invariant: u ~ v ↔ v ~ u
        require(adjacencyMap.all { (u, neighbors) ->
            neighbors.all { v -> u in (adjacencyMap[v].orEmpty()) }
        }) { "Undirected graph must have symmetric adjacency" }
    }
}
```

### Construct Graphs from Edge Lists

```kotlin
fun <V> graphFromEdges(
    vertices: Collection<V>,
    edges: List<Pair<V, V>>,
    directed: Boolean = false
): DirectedGraph<V> {
    val adjMap = mutableMapOf<V, MutableSet<V>>()
    
    // Initialize all vertices
    vertices.forEach { v -> adjMap[v] = mutableSetOf() }
    
    // Add edges
    edges.forEach { (u, v) ->
        adjMap.getOrPut(u) { mutableSetOf() }.add(v)
        if (!directed) {
            adjMap.getOrPut(v) { mutableSetOf() }.add(u)
        }
    }
    
    return DirectedGraph(MathSet.of(vertices.toSet()), adjMap.mapValues { it.value.toSet() })
}
```

## How to Implement Graph Traversal

### Breadth-First Search (BFS)

Use BFS for shortest paths in unweighted graphs:

```kotlin
/**
 * BFS traversal from source s.
 * Returns: (distances, predecessors) where:
 *   - dist[v] = shortest distance from s to v
 *   - pred[v] = predecessor of v on shortest path
 */
fun <V> DirectedGraph<V>.bfs(s: V): Pair<Map<V, Int>, Map<V, V?>> {
    val dist = mutableMapOf<V, Int>()
    val pred = mutableMapOf<V, V?>()
    
    // Initialize
    vertices.elements().forEach { v ->
        dist[v] = Int.MAX_VALUE
        pred[v] = null
    }
    dist[s] = 0
    
    // Queue for BFS (use simple list as queue)
    val queue = ArrayDeque<V>()
    queue.addLast(s)
    
    while (queue.isNotEmpty()) {
        val u = queue.removeFirst()
        
        for (v in neighbors(u)) {
            if (dist[v] == Int.MAX_VALUE) {
                // First time seeing v
                dist[v] = dist[u]!! + 1
                pred[v] = u
                queue.addLast(v)
            }
        }
    }
    
    return dist to pred
}

/** Reconstruct path from s to t using BFS predecessors */
fun <V> reconstructPath(pred: Map<V, V?>, t: V): List<V> {
    val path = mutableListOf<V>()
    var current: V? = t
    
    while (current != null) {
        path.add(current)
        current = pred[current]
    }
    
    return path.reversed()
}
```

### Depth-First Search (DFS)

Use DFS for cycle detection, topological sort, and strongly connected components:

```kotlin
/**
 * DFS with discovery/finish times for cycle detection.
 * Returns: (discovery, finish, isCyclic)
 */
fun <V> DirectedGraph<V>.dfsWithTimes(): 
    Triple<Map<V, Int>, Map<V, Int>, Boolean> {
    
    val discovery = mutableMapOf<V, Int>()
    val finish = mutableMapOf<V, Int>()
    var time = 0
    var isCyclic = false
    
    fun visit(u: V, parent: V?) {
        discovery[u] = ++time
        
        for (v in neighbors(u)) {
            if (v !in discovery) {
                visit(v, u)
            } else if (v != parent && v in discovery && v !in finish) {
                // Back edge found → cycle
                isCyclic = true
            }
        }
        
        finish[u] = ++time
    }
    
    // Visit all vertices (handles disconnected graphs)
    vertices.elements().filter { it !in discovery }.forEach { visit(it, null) }
    
    return Triple(discovery, finish, isCyclic)
}
```

## How to Compute Shortest Paths

### Dijkstra's Algorithm (Weighted Graphs)

For graphs with non-negative edge weights:

```kotlin
/**
 * Dijkstra's algorithm for single-source shortest paths.
 * @param weight Function returning edge weight w(u,v)
 * @return Map of shortest distances from source
 */
fun <V> DirectedGraph<V>.dijkstra(
    source: V,
    weight: (V, V) -> NaturalNumber
): Map<V, NaturalNumber> {
    
    val dist = mutableMapOf<V, NaturalNumber>()
    val visited = mutableSetOf<V>()
    
    // Initialize distances
    vertices.elements().forEach { v ->
        dist[v] = if (v == source) NaturalNumber(0u) 
                  else NaturalNumber(ULong.MAX_VALUE)
    }
    
    while (visited.size < vertices.cardinality().finiteValue()?.toInt()) {
        // Select unvisited vertex with minimum distance
        val u = vertices.elements()
            .filter { it !in visited }
            .minByOrNull { dist[it]!! } ?: break
        
        if (dist[u]!!.value == ULong.MAX_VALUE) break
        
        visited.add(u)
        
        // Relax edges
        for (v in neighbors(u)) {
            val newDist = dist[u]!!.value + weight(u, v).value
            if (newDist < dist[v]!!.value) {
                dist[v] = NaturalNumber(newDist)
            }
        }
    }
    
    return dist.toMap()
}
```

## How to Analyze Connectivity

### Connected Components (Undirected Graphs)

```kotlin
/**
 * Find all connected components in an undirected graph.
 * Returns partition of V into maximal connected subgraphs.
 */
fun <V> UndirectedGraph<V>.connectedComponents(): List<MathSet<V>> {
    val visited = mutableSetOf<V>()
    val components = mutableListOf<MathSet<V>>()
    
    fun dfsComponent(start: V): Set<V> {
        val component = mutableSetOf<V>()
        val stack = ArrayDeque<V>()
        stack.addLast(start)
        
        while (stack.isNotEmpty()) {
            val u = stack.removeLast()
            if (u in visited || u in component) continue
            
            component.add(u)
            visited.add(u)
            
            for (v in neighbors(u)) {
                if (v !in visited && v !in component) {
                    stack.addLast(v)
                }
            }
        }
        
        return component
    }
    
    for (v in vertices.elements()) {
        if (v !in visited) {
            val componentSet = dfsComponent(v)
            components.add(MathSet.of(componentSet))
        }
    }
    
    return components
}
```

### Topological Sort (DAGs Only)

```kotlin
/**
 * Topological sort using Kahn's algorithm.
 * Returns linear ordering where u → v implies u comes before v.
 * Returns null if graph has a cycle.
 */
fun <V> DirectedGraph<V>.topologicalSort(): List<V>? {
    // Compute in-degrees
    val inDegree = mutableMapOf<V, Int>()
    vertices.elements().forEach { v -> inDegree[v] = 0 }
    
    for ((u, neighbors) in adjacencyMap) {
        for (v in neighbors) {
            inDegree[v] = inDegree[v]!! + 1
        }
    }
    
    // Queue of vertices with no incoming edges
    val queue = ArrayDeque<V>()
    inDegree.filter { it.value == 0 }.keys.forEach { queue.addLast(it) }
    
    val result = mutableListOf<V>()
    
    while (queue.isNotEmpty()) {
        val u = queue.removeFirst()
        result.add(u)
        
        for (v in neighbors(u)) {
            inDegree[v] = inDegree[v]!! - 1
            if (inDegree[v] == 0) {
                queue.addLast(v)
            }
        }
    }
    
    // If not all vertices processed, graph has a cycle
    return if (result.size == vertices.cardinality().finiteValue()?.toInt()) 
        result else null
}
```

## Best Practices

1. **Choose representation wisely:**
   - Adjacency list: sparse graphs, most algorithms
   - Adjacency matrix: dense graphs, O(1) edge queries

2. **Handle disconnected graphs** - Always iterate over all vertices, not just from source

3. **Use appropriate data structures:**
   - `ArrayDeque` for BFS/DFS stacks/queues
   - `MutableMap` for distance/predecessor tracking

4. **Verify invariants:**
   - Undirected graphs must have symmetric adjacency
   - DAGs required for topological sort

5. **Test with known graphs:**
   - Complete graphs Kₙ
   - Cycle graphs Cₙ
   - Path graphs Pₙ
   - Trees (acyclic connected)

## Common Algorithms Reference

| Algorithm | Complexity | Use Case |
|-----------|------------|----------|
| BFS | O(V + E) | Shortest paths (unweighted), level-order |
| DFS | O(V + E) | Cycle detection, topological sort |
| Dijkstra | O((V+E) log V) | Shortest paths (weighted, non-negative) |
| Topological Sort | O(V + E) | Dependency resolution, scheduling |
| Connected Components | O(V + E) | Network analysis, clustering |

## References

1. **Source:** `graph/src/commonMain/kotlin/mathsets/graph/` from mathsets-kt
2. **Graph Theory:** West, D.B. *Introduction to Graph Theory*. 2nd ed. Prentice Hall, 2001.
3. **Algorithms:** Cormen, T.H. et al. *Introduction to Algorithms*. 3rd ed. MIT Press, 2009. Ch. 22-24.
4. **Kotlin Collections:** [Official Kotlin Documentation](https://kotlinlang.org/docs/collections-overview.html)
