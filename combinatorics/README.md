# combinatorics

Set partitions, Ramsey theory, and Gale-Stewart games.

## Overview

This module implements foundational results from **infinitary combinatorics**, adapted for finite verification:

- **Partition calculus** — enumerate all partitions of a finite set, compute Bell numbers, and verify the Erdos-Rado arrow relation `n → (m)^k_c` by brute force.
- **Ramsey theory** — search for monochromatic cliques in edge-colored complete graphs and compute Ramsey number bounds.
- **Gale-Stewart games** — model finite-horizon two-player games with boolean payoff and solve them via minimax.

## Key Classes

| Class / Object | Description |
|---|---|
| `PartitionCalculus` | All-partitions enumeration, Bell numbers, Erdos-Rado arrow verification |
| `Ramsey` | Monochromatic clique search and Ramsey number bound computation |
| `GaleStewartGame<M>` | Finite-horizon two-player game with minimax evaluation |

## Usage Examples

### Set partitions and Bell numbers

```kotlin
val partitions = PartitionCalculus.allPartitions(setOf(1, 2, 3))
// 5 partitions (B(3) = 5)

val b5 = PartitionCalculus.bellNumber(5)  // 52
```

### Ramsey numbers

```kotlin
// Find R(3,3): smallest n where any 2-coloring of K_n has a monochromatic triangle
val r33 = Ramsey.searchBounds(cliqueSize = 3, colors = 2, maxVertices = 8)
// r33 == 6
```

### Erdos-Rado arrow relation

```kotlin
// Verify 6 → (3)^2_2 (i.e., R(3,3) ≤ 6)
val holds = PartitionCalculus.erdosRadoArrow(n = 6, monochromaticSize = 3)
// holds == true
```

### Gale-Stewart game (simplified Nim)

```kotlin
val game = GaleStewartGame(
    legalMoves = listOf(1, 2),
    horizon = 3,
    payoffForFirstPlayer = { history -> history.sum() >= 4 }
)
val p1Wins = game.firstPlayerHasWinningStrategy()
val bestMove = game.bestMoveFor(emptyList())
```

## Module Dependencies

- `kernel` — `NaturalNumber`
