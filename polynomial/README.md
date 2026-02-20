---
description: "Documentação para polynomial"
use_when: "When you need information about polynomial"
---

# polynomial

Univariate polynomial rings with arithmetic, Euclidean division, and GCD.

## Overview

This module implements univariate polynomials `R[x]` over any commutative ring R, with specialized operations (division, GCD) over fields. Polynomials are represented in ascending degree order with automatic normalization (trailing zeros are stripped).

The `PolynomialRing<R>` itself implements `CommutativeRing<Polynomial<R>>`, making it composable with the algebra module's hierarchy.

## Key Classes

| Class | Description |
|---|---|
| `Polynomial<R>` | Univariate polynomial with coefficients in R. Degree, leading coefficient, indexing. |
| `PolynomialRing<R>` | R[x] as a `CommutativeRing`. Addition, subtraction, multiplication, scalar multiply, evaluation. |
| `FieldPolynomialOps<R>` | Euclidean division and GCD for polynomials over a field. |

## Usage Examples

### Polynomial arithmetic over Q

```kotlin
val ring = PolynomialRing(RationalField)
val a = Polynomial(listOf(q(1), q(1)))        // 1 + x
val b = Polynomial(listOf(q(-1), q(1)))       // -1 + x
val prod = ring.multiply(a, b)                 // -1 + x^2
ring.evaluate(prod, q(3))                      // 8
```

### Euclidean division and GCD

```kotlin
val ops = FieldPolynomialOps(RationalField)
val x4m1 = Polynomial(listOf(q(-1), q(0), q(0), q(0), q(1)))  // x^4 - 1
val x3m1 = Polynomial(listOf(q(-1), q(0), q(0), q(1)))         // x^3 - 1
val g = ops.gcd(x4m1, x3m1)                                     // x - 1
```

### Over finite fields

```kotlin
val f5 = ZpField(5)
val ring = PolynomialRing(f5)
val p = Polynomial(listOf(1, 2, 3))  // 1 + 2x + 3x^2 in F_5[x]
ring.evaluate(p, 2)                   // (1 + 4 + 12) mod 5 = 2
```

## Module Dependencies

- `kernel` — Number types
- `algebra` — `CommutativeRing`, `Field`, `ZpField`, `RationalField`
