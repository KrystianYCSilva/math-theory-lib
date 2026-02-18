# ordinal

Ordinal numbers, Cantor Normal Form, arithmetic, and transfinite recursion.

## Overview

This module implements ordinal numbers up to finite powers of omega (ω). Ordinals are represented either as finite natural numbers or in **Cantor Normal Form** (CNF), which expresses every ordinal as a sum of terms `ω^a · c` with strictly decreasing exponents.

Key concepts:
- **Ordinal arithmetic** is non-commutative: `1 + ω = ω` but `ω + 1 ≠ ω`.
- **Transfinite recursion** generalizes ordinary recursion to ordinal-indexed sequences, handling zero, successor, and limit cases.

## Key Classes

| Class / Object | Description |
|---|---|
| `Ordinal` | Sealed interface with `Finite` and `CNF` variants |
| `Ordinal.Finite` | A finite ordinal backed by `NaturalNumber` |
| `Ordinal.CNF` | An ordinal in Cantor Normal Form |
| `CNFTerm` | A single term `coefficient · ω^exponent` in CNF |
| `CantorNormalForm` | Validated CNF representation with strictly descending exponents |
| `OrdinalArithmetic` | Addition, multiplication, exponentiation, and comparison |
| `TransfiniteRecursion` | Transfinite recursion with zero, successor, and limit cases |

## Usage Examples

### Creating ordinals

```kotlin
val zero = Ordinal.ZERO          // 0
val three = Ordinal.finite(3)    // 3
val omega = Ordinal.OMEGA        // ω
val omegaSq = Ordinal.omegaPower(2) // ω^2
```

### Arithmetic (non-commutative!)

```kotlin
import mathsets.ordinal.plus
import mathsets.ordinal.times
import mathsets.ordinal.pow

val a = omega + Ordinal.finite(3)   // ω + 3
val b = Ordinal.finite(3) + omega   // ω  (3 is absorbed!)
val c = omega * Ordinal.finite(2)   // ω · 2
val d = omega pow 3                 // ω^3
```

### Transfinite recursion

```kotlin
val sum = TransfiniteRecursion.transfiniteRecursion(
    ordinal = Ordinal.finite(10),
    base = 0,
    successorCase = { _, prev -> prev + 1 },
    limitCase = { _, approx -> approx.last() }
)
// sum == 10
```

## Module Dependencies

- `kernel` — `NaturalNumber`, `MathElement`
