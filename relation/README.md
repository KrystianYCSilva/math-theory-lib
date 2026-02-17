# Relation Module

The `relation` module provides a rigorous, set-theoretic implementation of binary relations and their classical properties. All constructions follow standard ZFC definitions.

## Overview

| Class / Function | Description |
|---|---|
| `OrderedPair<A, B>` | Ordered pair with Kuratowski encoding |
| `cartesianProduct()` | Extension function for A × B |
| `Relation<A, B>` | Binary relation R ⊆ A × B |
| `RelationProperties<A>` | Property checker (reflexive, symmetric, transitive, …) |
| `relationOf()` | Factory for building relations from predicates |
| `EquivalenceRelation<A>` | Reflexive + symmetric + transitive relation |
| `Partition<A>` | Non-empty, disjoint cover of a set |
| `OrderedSet<A>` | Set with an ordering relation |
| `PartialOrder<A>` | Reflexive + antisymmetric + transitive |
| `TotalOrder<A>` | Partial order + connex |
| `WellOrder<A>` | Total order where every non-empty subset has a minimum |

## Ordered Pair and Kuratowski Representation

An ordered pair `(a, b)` is a fundamental construct where order matters: `(a, b) ≠ (b, a)` unless `a = b`.

The **Kuratowski encoding** reduces ordered pairs to pure sets:

```
(a, b) = {{a}, {a, b}}
```

This encoding guarantees the identity property: `(a, b) = (c, d)` if and only if `a = c` and `b = d`.

```kotlin
val pair = OrderedPair(1, "x")
val kuratowski = pair.toKuratowski()  // {{1}, {1, "x"}}
```

## Cartesian Product

The Cartesian product A × B is the set of all ordered pairs with first component from A and second from B:

```
A × B = { (a, b) | a ∈ A ∧ b ∈ B }
```

```kotlin
val A = ExtensionalSet(setOf(1, 2))
val B = ExtensionalSet(setOf("a", "b"))
val product = A.cartesianProduct(B)
// {(1,"a"), (1,"b"), (2,"a"), (2,"b")}
```

## Relation

A `Relation<A, B>` models a binary relation R ⊆ A × B, defined by:

- **domain** — the source set A
- **codomain** — the target set B
- **graph** — the set of ordered pairs { (a, b) | aRb }

### Operations

| Method | Description |
|---|---|
| `contains(a, b)` | Tests whether `(a, b) ∈ R` |
| `inverse()` | Computes R⁻¹ = { (b, a) \| (a, b) ∈ R } |
| `compose(other)` | Computes R ; S = { (a, c) \| ∃b: aRb ∧ bSc } |

```kotlin
val graph = ExtensionalSet(setOf(OrderedPair(1, "a"), OrderedPair(2, "b")))
val R = Relation(graph)
R.contains(1, "a")  // true
R.inverse()         // Relation from {"a","b"} to {1,2}
```

## Relation Properties

`RelationProperties<A>` analyzes a homogeneous relation R ⊆ A × A for classical properties:

| Property | Definition | Method |
|---|---|---|
| Reflexive | ∀a: aRa | `isReflexive()` |
| Symmetric | ∀a,b: aRb → bRa | `isSymmetric()` |
| Transitive | ∀a,b,c: (aRb ∧ bRc) → aRc | `isTransitive()` |
| Antisymmetric | ∀a,b: (aRb ∧ bRa) → a=b | `isAntisymmetric()` |
| Irreflexive | ∀a: ¬(aRa) | `isIrreflexive()` |
| Trichotomous | ∀a,b: aRb ∨ a=b ∨ bRa | `isTrichotomous()` |
| Connex | ∀a,b: a≠b → (aRb ∨ bRa) | `isConnex()` |

The `relationOf` factory builds a relation from a predicate:

```kotlin
val universe = ExtensionalSet(setOf(1, 2, 3))
val leq = relationOf(universe) { a, b -> a <= b }
val props = RelationProperties(leq, universe)
props.isReflexive()     // true
props.isAntisymmetric() // true
props.isTransitive()    // true
```

## Equivalence Relation and Partition

### Equivalence Relation

An `EquivalenceRelation<A>` is a relation that is reflexive, symmetric, and transitive. It partitions the universe into **equivalence classes**: `[a] = { x ∈ A | aRx }`.

```kotlin
val universe = ExtensionalSet(setOf(1, 2, 3, 4))
val modTwo = relationOf(universe) { a, b -> a % 2 == b % 2 }
val equiv = EquivalenceRelation(universe, modTwo)

equiv.equivalenceClass(1) // {1, 3}
equiv.equivalenceClass(2) // {2, 4}
equiv.quotientSet()       // {{1,3}, {2,4}}
```

### Partition

A `Partition<A>` is a collection of non-empty, pairwise disjoint subsets whose union equals the universe. At construction, the class validates:

1. All parts are non-empty
2. Parts are pairwise disjoint
3. The union of all parts equals the universe

### Fundamental Theorem of Equivalence Relations

There is a natural bijection between equivalence relations on A and partitions of A:

```
EquivalenceRelation  ⟷  Partition
```

- `EquivalenceRelation.toPartition()` — extracts equivalence classes as a partition
- `Partition.toEquivalenceRelation()` — defines "same block" as an equivalence relation

```kotlin
val partition = equiv.toPartition()
val recovered = partition.toEquivalenceRelation()
// recovered defines the same relation as equiv
```

## Order Hierarchy

The module provides a class hierarchy that mirrors the mathematical tower of ordered structures:

```
OrderedSet<A>
  └── PartialOrder<A>        (+ reflexive, antisymmetric, transitive)
        └── TotalOrder<A>    (+ connex)
              └── WellOrder<A>  (every non-empty subset has a minimum)
```

### OrderedSet

The base class pairs a universe with an ordering relation and provides operations:

| Method | Description |
|---|---|
| `minimum()` | Least element (≤ all others), or `null` |
| `maximum()` | Greatest element (≥ all others), or `null` |
| `minimals()` | Elements with nothing strictly below them |
| `maximals()` | Elements with nothing strictly above them |
| `supremum(subset)` | Least upper bound of a subset |
| `infimum(subset)` | Greatest lower bound of a subset |
| `successor(element)` | Immediate next element |
| `predecessor(element)` | Immediate previous element |

### PartialOrder

A partial order requires the relation to be **reflexive**, **antisymmetric**, and **transitive**. Incomparable elements are allowed.

```kotlin
val divides = relationOf(ExtensionalSet(setOf(1, 2, 3, 6))) { a, b -> b % a == 0 }
val poset = PartialOrder(ExtensionalSet(setOf(1, 2, 3, 6)), divides)
poset.minimum()   // 1
poset.maximum()   // 6
```

### TotalOrder

A total order adds **connexity**: every pair of distinct elements is comparable.

```kotlin
val leq = relationOf(ExtensionalSet(setOf(1, 2, 3))) { a, b -> a <= b }
val total = TotalOrder(ExtensionalSet(setOf(1, 2, 3)), leq)
total.successor(1) // 2
```

### WellOrder

A well-order is a total order where **every non-empty subset has a minimum**. Verification currently requires a finite universe (all subsets in the power set are checked).

```kotlin
val leq = relationOf(ExtensionalSet(setOf(1, 2, 3))) { a, b -> a <= b }
val well = WellOrder(ExtensionalSet(setOf(1, 2, 3)), leq)
well.minimum() // 1
```
