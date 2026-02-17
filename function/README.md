# Function Module

The `function` module models mathematical functions as first-class objects with domain and codomain validation, classification by injectivity/surjectivity, and support for the Axiom of Choice.

## Overview

| Class / Function | Description |
|---|---|
| `MathFunction<A, B>` | A function f: A → B with domain/codomain enforcement |
| `Injection<A, B>` | Injective (one-to-one) function |
| `Surjection<A, B>` | Surjective (onto) function |
| `Bijection<A, B>` | Bijective function with `inverse()` |
| `ChoiceFunction<A>` | Choice function over a family of non-empty sets |
| `isEquipotentTo()` | Equipotence test via bijection existence |
| `findBijectionTo()` | Explicit bijection construction |

## MathFunction

A `MathFunction<A, B>` models f: A → B rigorously:

- **domain** — the source set A
- **codomain** — the target set B
- **mapping** — the rule assigning each a ∈ A to a unique f(a) ∈ B
- **graph** — lazily computed set { (a, f(a)) | a ∈ A }

Every invocation validates that the input belongs to the domain and the output belongs to the codomain.

```kotlin
val A = ExtensionalSet(setOf(1, 2, 3))
val B = ExtensionalSet(setOf(2, 4, 6))
val f = MathFunction(A, B) { it * 2 }

f(1)        // 2
f(4)        // throws: 4 not in domain
f.image()   // {2, 4, 6}
```

### Operations

| Method | Description |
|---|---|
| `invoke(a)` | Apply the function: f(a) |
| `image()` | Range of f: { f(a) \| a ∈ A } |
| `preImage(value)` | f⁻¹({b}) = { a \| f(a) = b } |
| `preImage(subset)` | f⁻¹(S) = { a \| f(a) ∈ S } |
| `isInjective()` | f(a₁) = f(a₂) → a₁ = a₂ |
| `isSurjective()` | ∀b ∈ B, ∃a ∈ A: f(a) = b |
| `isBijective()` | Injective ∧ Surjective |
| `compose(before)` | (f ∘ g)(x) = f(g(x)) |

## Injection, Surjection, Bijection

These are type-safe subclasses that **validate their respective property at construction time**. Using them communicates the function's classification through the type system.

### Injection (One-to-One)

An `Injection<A, B>` guarantees that distinct inputs map to distinct outputs.

```kotlin
val A = ExtensionalSet(setOf(1, 2, 3))
val B = ExtensionalSet(setOf(10, 20, 30, 40))
val inj = Injection(A, B) { it * 10 }
// OK: 1→10, 2→20, 3→30 — all distinct
```

### Surjection (Onto)

A `Surjection<A, B>` guarantees that every element of the codomain is hit.

```kotlin
val A = ExtensionalSet(setOf(1, 2, 3))
val B = ExtensionalSet(setOf(1, 2))
val surj = Surjection(A, B) { if (it <= 2) it else 1 }
// OK: image covers all of B
```

### Bijection (One-to-One Correspondence)

A `Bijection<A, B>` is both injective and surjective, establishing a perfect pairing. It supports computing the inverse:

```kotlin
val A = ExtensionalSet(setOf(1, 2, 3))
val B = ExtensionalSet(setOf("a", "b", "c"))
val bij = Bijection(A, B) { when(it) { 1 -> "a"; 2 -> "b"; else -> "c" } }

val inv = bij.inverse()  // Bijection<String, Int>
inv("a")                 // 1
```

### Classification Summary

| Class | Injective | Surjective | Invertible |
|---|---|---|---|
| `MathFunction` | ? | ? | No |
| `Injection` | Yes | ? | No |
| `Surjection` | ? | Yes | No |
| `Bijection` | Yes | Yes | Yes |

## ChoiceFunction (Axiom of Choice)

The **Axiom of Choice** (AC) states: for every family of non-empty sets, there exists a function that selects one element from each set.

`ChoiceFunction<A>` implements this for finite families:

```kotlin
val family = ExtensionalSet(setOf(
    ExtensionalSet(setOf(1, 2)),
    ExtensionalSet(setOf(3, 4)),
    ExtensionalSet(setOf(5))
))

val choice = ChoiceFunction(family)
val selected = choice.choose()  // e.g., {1,2}→1, {3,4}→3, {5}→5
val f = choice.asFunction()     // MathFunction<MathSet<Int>, Int>
```

| Method | Description |
|---|---|
| `choose()` | Returns a `Map<MathSet<A>, A>` of choices |
| `asFunction()` | Wraps the choice as a `MathFunction` |

## Equipotence

Two sets are **equipotent** (have the same cardinality) if there exists a bijection between them. This is the foundational definition of "same size" in set theory and extends naturally to infinite sets.

```kotlin
val A = ExtensionalSet(setOf(1, 2, 3))
val B = ExtensionalSet(setOf("x", "y", "z"))

A isEquipotentTo B              // true (|A| = |B| = 3)
A.findBijectionTo(B)            // Bijection<Int, String>

val C = ExtensionalSet(setOf(10, 20))
A isEquipotentTo C              // false (|A| ≠ |C|)
```

| Function | Description |
|---|---|
| `isEquipotentTo(other)` | Returns `true` if a bijection exists |
| `findBijectionTo(other)` | Returns a `Bijection` or `null` |

Both functions currently require finite sets.
