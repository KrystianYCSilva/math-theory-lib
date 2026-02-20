---
description: "Documentação para forcing"
use_when: "When you need information about forcing"
---

# forcing

Posets, generic filters, forcing extensions, and CH independence demonstration.

## Overview

This module implements a **finite simulation** of Paul Cohen's forcing technique, which was used to prove the independence of the Continuum Hypothesis (CH) from ZFC. It covers:

- **Posets** — finite partially ordered sets with validation of the partial-order axioms and operations for density, antichains, and filters.
- **Generic filters** — construction and verification of filters that meet every dense set in a given family.
- **Forcing extensions** — simulation of the ground-model extension M → M[G] using forced names.
- **Independence demonstration** — finite analogue models showing that the CH analogue holds in one model and fails in another.

## Key Classes

| Class / Object | Description |
|---|---|
| `Poset<T>` | Finite poset with density, antichain, and filter operations |
| `GenericFilter<T>` | A filter over a poset (set of conditions) |
| `GenericFilterBuilder` | Constructs generic filters meeting specified dense families |
| `ForcedName<M, C>` | A ground-model element paired with supporting conditions |
| `ForcingExtension<M, C>` | Simulates the forcing extension M → M[G] |
| `ChAnalogueModel` | Finite model for CH analogue testing |
| `IndependenceDemo` | Builds models where CH holds and fails |

## Usage Examples

### Building a poset and finding filters

```kotlin
val poset = Poset(setOf("a", "b", "c")) { x, y ->
    x == y || (x == "a" && y != "a") || (x == "b" && y == "c")
}
val filters = poset.filters()
val antichains = poset.antichains()
val denseSubsets = poset.denseSubsets()
```

### Constructing a generic filter

```kotlin
val dense = poset.denseSubsets()
val generic = GenericFilterBuilder.build(poset, dense)
```

### Forcing extension

```kotlin
val ground = setOf("x", "y", "z")
val extension = ForcingExtension(ground, generic!!)
val universe = extension.extensionUniverse()
val name = extension.interpret("x")
```

### CH independence

```kotlin
val (chHolds, chFails) = IndependenceDemo.summary()
// chHolds == true, chFails == false
// Demonstrates independence: same "axioms", different truth values
```

## Module Dependencies

- None (self-contained finite simulation)
