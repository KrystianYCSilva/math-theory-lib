---
description: "Documentação para set — Layer 2: Set Theory"
use_when: "When you need information about set — Layer 2: Set Theory"
---

# set — Layer 2: Set Theory

The `set` module provides a computational implementation of axiomatic set theory built on
top of the `kernel` module's number types and cardinality system. It models both finite and
infinite sets through a sealed type hierarchy and supports the standard operations of
classical set algebra, ZFC axiom verification, and paradox demonstration.

## `MathSet<T>` — The Sealed Interface

All sets in the library implement `MathSet<T>`, a sealed interface that declares:

| Member | Description |
|---|---|
| `contains(element)` | Membership test (`x ∈ S`) |
| `elements()` | Lazy `Sequence<T>` enumeration |
| `cardinality` | `Cardinality` (finite, countably infinite, uncountable, unknown) |
| `materialize()` | Eagerly collect into an `ExtensionalSet<T>` |
| `union`, `intersect`, `minus`, `symmetricDiff` | Binary set operations |
| `complement(universe)` | Relative complement |
| `isSubsetOf`, `isProperSubsetOf`, `isDisjointWith` | Set relations |
| `filter(predicate)` | Separation (restricted comprehension) |
| `map(f)` | Replacement (image under a function) |
| `powerSet()` | Power set `P(S)` |

Factory functions:

```kotlin
val a = MathSet.of(1, 2, 3)          // ExtensionalSet
val b = MathSet.empty<Int>()          // EmptySet
val c = MathSet.singleton(42)         // singleton
val d = mathSetOf(1..10)              // from IntRange
val e = mathSetOf(listOf(1, 2, 3))   // from Iterable
```

## Implementations

### Extensional vs Intensional Sets

| Type | Mode | Description |
|---|---|---|
| `ExtensionalSet<T>` | Extensional | Finite set defined by explicitly listing elements (`{a, b, c}`) |
| `IntensionalSet<T>` | Intensional | Set defined by a predicate over a domain (`{ x ∈ D \| P(x) }`) |
| `EmptySet` | Extensional | The empty set (`∅`), singleton object typed to `Nothing` |

**Extensional sets** store members in a Kotlin `Set<T>` and provide O(1) membership via
hashing. They are the default result of factory functions and materialization.

**Intensional sets** evaluate membership lazily — an element belongs to the set only if it
belongs to the domain *and* satisfies the predicate. This directly models the ZFC Axiom of
Separation:

```kotlin
val evens = Naturals.filter { it.value % 2 == 0 }  // { x ∈ N | x is even }
evens.contains(NaturalNumber.of(4))                 // true
evens.contains(NaturalNumber.of(3))                 // false
```

### BitMathSet

A compact bit-vector-backed set for non-negative integers in the range `[0, size)`. Each
integer is stored as a single bit in a `LongArray`, making it memory-efficient for dense
integer sets.

```kotlin
val bits = BitMathSet.fromSet(100, setOf(0, 7, 42, 99))
bits.contains(42)  // true
bits.contains(50)  // false
```

### MappedSet

The image of a set under a function `f`, computed lazily with deduplication. Models the
ZFC Axiom of Replacement:

```kotlin
val squares = mathSetOf(1, 2, 3, 4).map { it * it }  // {1, 4, 9, 16}
```

### LazyPowerSet

The power set `P(S)` — the set of all subsets of `S`. Subsets are generated lazily via
bitmask enumeration (limited to base sets of size ≤ 30):

```kotlin
val p = mathSetOf(1, 2).powerSet()
p.elements().count()  // 4  (∅, {1}, {2}, {1,2})
```

### Set Views (Internal)

`UnionSetView` and `IntersectionSetView` are internal lazy views that defer computation
of union and intersection. They interleave or filter elements from both operands without
eagerly materializing the result, enabling operations on infinite sets.

## Universal Sets

Pre-defined singleton objects for the standard number sets:

| Object | Type | Cardinality | Enumerable? |
|---|---|---|---|
| `Naturals` | `MathSet<NaturalNumber>` | Countably infinite | Yes (lazy) |
| `Integers` | `MathSet<IntegerNumber>` | Countably infinite | Yes (lazy) |
| `Rationals` | `MathSet<RationalNumber>` | Countably infinite | Yes (lazy) |
| `Reals` | `MathSet<RealNumber>` | Uncountable | No |
| `Irrationals` | `MathSet<IrrationalNumber>` | Uncountable | No |
| `Imaginaries` | `MathSet<ImaginaryNumber>` | Uncountable | No |
| `Complexes` | `MathSet<ComplexNumber>` | Uncountable | No |
| `ExtendedReals` | `MathSet<ExtendedReal>` | Uncountable | No |

Countably infinite sets support lazy enumeration via `elements()`. Uncountable sets
support membership testing but throw `UnsupportedOperationException` on `elements()` and
`InfiniteMaterializationException` on `materialize()`.

## Set Algebra Verification

The `SetAlgebra` object provides element-by-element verifiers for classical identities
over a finite universe:

| Function | Identity |
|---|---|
| `isUnionCommutative` | `A ∪ B = B ∪ A` |
| `isIntersectionCommutative` | `A ∩ B = B ∩ A` |
| `isUnionAssociative` | `(A ∪ B) ∪ C = A ∪ (B ∪ C)` |
| `isDeMorganForUnion` | `(A ∪ B)' = A' ∩ B'` |
| `isIdempotentUnion` | `A ∪ A = A` |
| `hasIdentityUnion` | `A ∪ ∅ = A` |
| `hasAbsorption` | `A ∪ (A ∩ B) = A` |
| `hasInvolution` | `(A')' = A` |
| `extensionalityHolds` | Same members ⟹ equal sets |

```kotlin
val u = mathSetOf(1, 2, 3, 4, 5)
val a = mathSetOf(1, 2, 3)
val b = mathSetOf(2, 3, 4)
SetAlgebra.isUnionCommutative(a, b, u)  // true
SetAlgebra.isDeMorganForUnion(a, b, u)  // true
```

## ZFC Axiom Verification

`ZFCVerifier` checks a `FiniteModel<T>` (a universe of elements plus a collection of sets)
against the ten standard ZFC axioms:

```kotlin
val universe = ExtensionalSet(setOf(1, 2))
val sets = ExtensionalSet(setOf(
    MathSet.empty<Int>(),
    mathSetOf(1),
    mathSetOf(2),
    mathSetOf(1, 2)
))
val model = FiniteModel(universe, sets)
val report = ZFCVerifier.verify(model)

report.isSatisfied("Extensionality")  // true
report.isSatisfied("EmptySet")        // true
report.isSatisfied("Infinity")        // false (always false for finite models)
```

The report maps each axiom name to a boolean:

| Axiom | Finite-model behavior |
|---|---|
| Extensionality | Verified element-by-element |
| EmptySet | Checks `∅` exists in model |
| Pairing | Checks `{a, b}` exists for all element pairs |
| Union | Checks closure under pairwise union |
| PowerSet | Checks all subsets are present |
| Infinity | Always `false` (finite models cannot satisfy it) |
| Separation | Approximated by power-set closure |
| Replacement | Trivially `true` |
| Choice | Trivially `true` |
| Foundation | Trivially `true` |

## Paradox Demonstrations

The `Paradoxes` object demonstrates classical paradoxes and shows how ZFC prevents
contradictions:

**Russell's Paradox** — constructs `R = { x ∈ base | x ∉ x }` and shows that restricted
comprehension prevents the contradiction:

```kotlin
val base = ExtensionalSet(setOf<MathSet<Any?>>(MathSet.empty(), mathSetOf(1)))
val result = Paradoxes.russell(base)
result.contradictionDetected  // false
```

**Cantor's Theorem** — verifies `|P(S)| > |S|` for a finite set:

```kotlin
val result = Paradoxes.cantor(ExtensionalSet(setOf(1, 2, 3)))
result.artifact               // 8  (= 2^3)
result.contradictionDetected  // false
```

## Exception

`InfiniteMaterializationException` (extends `IllegalStateException`) is thrown whenever
code attempts to eagerly collect an infinite or uncountable set into an `ExtensionalSet`.
