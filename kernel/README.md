# kernel — Layer 0: Computational Primitives

The `kernel` module is the foundational layer of **mathsets-kt**. It provides arbitrary-precision number types, arithmetic abstractions, predicate combinators, lazy generators, and analysis primitives that all higher layers (sets, relations, ordinals, etc.) build upon.

Everything in this module is **pure Kotlin Multiplatform** — no platform-specific logic leaks into the public API.

## Number Hierarchy

The kernel models the classical tower of number systems:

```
ℕ ⊂ ℤ ⊂ ℚ ⊂ ℝ ⊂ ℂ
                ℝ ⊂ ℝ* (extended reals)
```

| Class | Set | Description |
|---|---|---|
| `NaturalNumber` | ℕ = {0, 1, 2, …} | Non-negative integers with Peano successor/predecessor, primality testing, and modular arithmetic. Value class over `BigInteger`. |
| `IntegerNumber` | ℤ = {…, -1, 0, 1, …} | Signed integers with full arithmetic and conversion to/from ℕ. Value class over `BigInteger`. |
| `RationalNumber` | ℚ = { p/q } | Exact fractions, always stored in reduced form with positive denominator. Value class over `Pair<BigInteger, BigInteger>`. |
| `RealNumber` | ℝ (computational) | Finite-precision decimals backed by `BigDecimal`. Covers all terminating decimals. Value class. |
| `IrrationalNumber` | ℝ \ ℚ (symbolic) | Symbolic irrationals (π, e, √2, φ, …) with a decimal approximation for computation. Arithmetic falls back to the approximation. |
| `ImaginaryNumber` | Im(ℂ) | Pure imaginary numbers of the form *bi*. Value class over `RealNumber`. |
| `ComplexNumber` | ℂ = { a + bi } | Full complex numbers with conjugate, modulus, and field operations. Data class. |
| `ExtendedReal` | ℝ* = ℝ ∪ {-∞, +∞} | Extended real line with `Finite`, `PositiveInfinity`, `NegativeInfinity`, and `Indeterminate` cases. Sealed interface. |

### Common Interface

All number types implement `MathElement`, the marker interface that allows them to participate in set-theoretic structures.

### Cardinality

`Cardinality` is a sealed interface representing the size of a set:

| Variant | Meaning |
|---|---|
| `Cardinality.Finite(n)` | Finite cardinality equal to the natural number *n*. |
| `Cardinality.CountablyInfinite` | ℵ₀ — the cardinality of ℕ, ℤ, ℚ. |
| `Cardinality.Uncountable` | Uncountable cardinality (e.g., |ℝ|). |
| `Cardinality.Unknown` | Placeholder for unevaluated intensional sets. |

## Arithmetic Abstractions

Polymorphic arithmetic is provided through two interfaces and concrete singleton implementations:

```kotlin
interface AlgebraicArithmetic<N : MathElement> {
    val zero: N
    val one: N
    fun add(a: N, b: N): N
    fun subtract(a: N, b: N): N
    fun multiply(a: N, b: N): N
    fun divide(a: N, b: N): N
}

interface Arithmetic<N : MathElement> : AlgebraicArithmetic<N> {
    fun compare(a: N, b: N): Int
}
```

| Object | Type Parameter | Notes |
|---|---|---|
| `NaturalArithmetic` | `NaturalNumber` | Subtraction is partial (throws if result < 0). |
| `IntegerArithmetic` | `IntegerNumber` | Full ring arithmetic. |
| `RationalArithmetic` | `RationalNumber` | Field arithmetic. |
| `RealArithmetic` | `RealNumber` | Decimal field arithmetic. |
| `ExtendedRealArithmetic` | `ExtendedReal` | Handles infinity and indeterminate forms. |
| `ComplexArithmetic` | `ComplexNumber` | Algebraic only (no total order on ℂ). |

## Predicate Combinators

The `Predicate<T>` type alias (`(T) -> Boolean`) comes with logical combinators:

```kotlin
val isEven: Predicate<NaturalNumber> = { it.isEven() }
val isSmall: Predicate<NaturalNumber> = { it < NaturalNumber.of(100) }

val smallEven = isEven and isSmall       // conjunction
val either    = isEven or isSmall        // disjunction
val notEven   = isEven.not()             // negation
val implies   = isEven implies isSmall   // material implication
val iff       = isEven iff isSmall       // biconditional
```

## Lazy Generators

The `Generators` (and its alias `Generator`) object provides infinite `Sequence<T>` enumerations:

```kotlin
// First 10 natural numbers: 0, 1, 2, ..., 9
Generators.naturals().take(10).toList()

// Integers in zigzag order: 0, 1, -1, 2, -2, ...
Generators.integers().take(10).toList()

// Rationals by diagonal enumeration: 0, 1/1, -1/1, 1/2, -1/2, ...
Generators.rationals().take(20).toList()
```

All generators are lazily evaluated and compose naturally with Kotlin's `Sequence` API.

## Analysis Primitives

The `mathsets.kernel.analysis` package provides numerical building blocks for calculus:

### Limits

`Limits` offers deterministic limit rules for fundamental cases:

```kotlin
Limits.quotient(numerator, denominator)        // Extended real division
Limits.reciprocal(value)                       // 1/x
Limits.reciprocalAtZero(Side.RIGHT)            // lim x→0⁺ 1/x = +∞
Limits.reciprocalAtZero(Side.LEFT)             // lim x→0⁻ 1/x = -∞
Limits.twoSidedReciprocalAtZero()              // Indeterminate
Limits.differenceQuotient(f, at, delta)        // (f(a+δ) - f(a)) / δ
```

### Derivatives

`Derivatives` provides numerical differentiation via finite differences:

```kotlin
Derivatives.forwardDifference(f, at, h)        // (f(x+h) - f(x)) / h         — O(h)
Derivatives.symmetricDifference(f, at, h)      // (f(x+h) - f(x-h)) / 2h      — O(h²)
```

## Platform Abstractions

The `mathsets.kernel.platform` package contains multiplatform type aliases and factory functions for arbitrary-precision arithmetic:

| Type Alias | Delegates To | Purpose |
|---|---|---|
| `BigInteger` | `com.ionspin.kotlin.bignum.integer.BigInteger` | Arbitrary-precision integers. |
| `BigDecimal` | `com.ionspin.kotlin.bignum.decimal.BigDecimal` | Arbitrary-precision decimals. |

Convenience constants (`BI_ZERO`, `BI_ONE`, `BD_ZERO`, `BD_ONE`, etc.) and factory functions (`bigIntegerOf(...)`, `bigDecimalOf(...)`) provide idiomatic Kotlin construction across all targets.

## Module Dependencies

```
kernel (this module)
  └── com.ionspin.kotlin.bignum (multiplatform big number library)
```

The kernel has **no dependencies** on other modules in this project. All higher layers depend on it.
