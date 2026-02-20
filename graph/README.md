# graph

Graph-theory foundations for Wave 3.

## Overview

This module introduces core graph structures and classical algorithms:

- `UndirectedGraph<V>`, `DirectedGraph<V>`
- `WeightedUndirectedGraph<V>`, `WeightedDirectedGraph<V>`
- `BipartiteGraph<L, R>` with explicit partitions
- adjacency list/matrix builders (`Adjacency`)
- traversal algorithms: `BFS`, `DFS`
- shortest paths: `Dijkstra`, `BellmanFord`
- minimum spanning trees: `Kruskal`, `Prim`
- maximum flow: `MaxFlow.edmondsKarp`
- maximum matching: `HopcroftKarp.maximumMatching`
- coloring: greedy and exact chromatic number via backtracking
- isomorphism: backtracking-based check for small graphs
- planarity: heuristic detection of non-planar signatures ($K_5$, $K_{3,3}$)
- spectral helpers: theoretical spectrum of complete graphs

## Notes

- `Dijkstra` assumes non-negative weights.
- `BellmanFord` supports negative edges and reports negative cycles.
- Adjacency-matrix helpers reuse `Matrix` from `linalg`.
- `SpectralGraph.adjacencyMatrix` returns `Matrix<RealNumber>` for integration with linear algebra.
