---
description: "Documentação para descriptive"
use_when: "When you need information about descriptive"
---

# descriptive

Finite topology and Borel hierarchy classification.

## Overview

This module provides tools from **descriptive set theory**, focused on:

- **Finite topologies** — construct and validate topological spaces on finite sets, then compute interior, closure, and boundary of subsets.
- **Borel hierarchy** — classify sets built from open/closed sets via complements, countable unions, and countable intersections into the initial levels of the Borel hierarchy (Σ⁰₁, Π⁰₁, Σ⁰₂, Π⁰₂).

## Key Classes

| Class / Object | Description |
|---|---|
| `FiniteTopology<T>` | A validated finite topological space with interior/closure/boundary operations |
| `BorelSet<T>` | Sealed hierarchy: `Open`, `Closed`, `CountableUnion`, `CountableIntersection`, `Complement` |
| `BorelLevel` | Enum of Borel levels: `SIGMA_0_1`, `PI_0_1`, `SIGMA_0_2`, `PI_0_2`, `HIGHER` |
| `BorelHierarchy` | Classifier that determines the Borel level of a `BorelSet` |

## Usage Examples

### Finite topology

```kotlin
val universe = mathSetOf(1, 2, 3, 4)
val openSets: MathSet<MathSet<Int>> = mathSetOf(listOf(
    MathSet.empty<Int>(),
    universe,
    mathSetOf(1),
    mathSetOf(1, 2),
    mathSetOf(1, 2, 3)
))
val topology = FiniteTopology(universe, openSets)

val target = mathSetOf(2, 3)
val interior = topology.interior(target)   // largest open subset of {2,3}
val closure = topology.closure(target)     // smallest closed superset of {2,3}
val boundary = topology.boundary(target)   // closure ∩ closure(complement)
```

### Borel classification

```kotlin
val open = BorelSet.Open(mathSetOf(1, 2))
val closed = BorelSet.Closed(mathSetOf(3))
val fSigma = BorelSet.CountableUnion(listOf(open, closed))

BorelHierarchy.classify(open)    // SIGMA_0_1
BorelHierarchy.classify(closed)  // PI_0_1
BorelHierarchy.classify(fSigma)  // SIGMA_0_2
```

## Module Dependencies

- `kernel` — `Cardinality`
- `set` — `MathSet`, `ExtensionalSet`
