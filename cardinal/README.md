# cardinal

Countability proofs, Cantor's diagonal argument, cardinal arithmetic, and the Continuum Hypothesis.

## Overview

This module provides tools for reasoning about the **sizes of infinite sets** (cardinalities). It includes:

- **Constructive bijections** proving that the integers and rationals are countable (same size as the naturals).
- **Cantor's diagonal argument** demonstrating that no surjection from a set to its power set exists.
- **Cardinal arithmetic** for finite, countably infinite (ℵ₀), and uncountable cardinals.
- A representation of the **Continuum Hypothesis** and its independence from ZFC.

## Key Classes

| Class / Object | Description |
|---|---|
| `Countability` | Bijections ℕ ↔ ℤ and ℕ ↔ ℚ with round-trip verification |
| `CantorDiagonal` | Diagonal set construction proving |P(S)| > |S| |
| `CantorDiagonalResult` | Result holder for the diagonal argument |
| `CardinalArithmetic` | Addition, multiplication, and exponentiation of cardinals |
| `CardinalNumber` | Type alias for `Cardinality` |
| `ContinuumHypothesis` | Statement and ZFC-independence status of CH |

## Usage Examples

### Proving ℤ is countable

```kotlin
val z = Countability.naturalToInteger(NaturalNumber.of(5))  // 3
val n = Countability.integerToNatural(IntegerNumber.of(-2))  // 4
val valid = Countability.verifyIntegerRoundTrip(100)  // true
```

### Cantor's diagonal argument

```kotlin
val domain = mathSetOf(1, 2, 3)
val mapping: (Int) -> MathSet<Int> = { x ->
    when (x) {
        1 -> mathSetOf(1, 2)
        2 -> mathSetOf(2, 3)
        3 -> mathSetOf(1)
        else -> MathSet.empty()
    }
}
val notSurjective = CantorDiagonal.verifyNotSurjective(domain, mapping)  // true
```

### Cardinal arithmetic

```kotlin
val sum = CardinalArithmetic.add(CardinalArithmetic.aleph0, Cardinality.Finite(NaturalNumber.of(5)))
// CountablyInfinite (ℵ₀ + 5 = ℵ₀)

val power = CardinalArithmetic.power(
    Cardinality.Finite(NaturalNumber.of(2)),
    CardinalArithmetic.aleph0
)
// Uncountable (2^ℵ₀ = continuum)
```

## Module Dependencies

- `kernel` — `NaturalNumber`, `IntegerNumber`, `RationalNumber`, `Cardinality`, `Generators`
- `set` — `MathSet`, `ExtensionalSet`
- `function` — `Bijection`
