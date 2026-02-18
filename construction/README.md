# construction — Layer 3: Axiomatic Construction of Number Systems

This module provides rigorous, set-theoretic constructions of the classical number systems, building each layer from the one below:

```
N (Von Neumann ordinals)
 └─► Z (equivalence classes of pairs of N)
      └─► Q (equivalence classes of pairs of Z)
           └─► R (Cauchy sequences of Q)
                ├─► Irrationals (algebraic or axiomatic)
                └─► C (ordered pairs of R)
                     └─► Imaginary (pure imaginary bi)
```

Every constructed type is backed by a **kernel projection** for efficient interoperability, while its internal representation follows the formal mathematical definition.

## The Dual Mode Pattern

The library uses two parallel representations for each number system:

| Layer | Kernel (efficient) | Construction (rigorous) |
|---|---|---|
| **N** | `NaturalNumber` (arbitrary-precision integer) | `VonNeumannNatural` (nested sets: `S(n) = n ∪ {n}`) |
| **Z** | `IntegerNumber` | `ConstructedInteger` (pairs `(a, b) ~ (c, d) ⟺ a+d = b+c`) |
| **Q** | `RationalNumber` | `ConstructedRational` (pairs `(a, b) ~ (c, d) ⟺ a·d = b·c`) |
| **R** | `RealNumber` | `ConstructedReal` (Cauchy sequences of `ConstructedRational`) |
| **C** | `ComplexNumber` | `ConstructedComplex` (ordered pairs of `ConstructedReal`) |

The **kernel** types live in the `kernel` module and are optimized for computation. The **construction** types in this module encode the axiomatic definitions and are optimized for mathematical rigor. Isomorphism objects bridge the two worlds.

## Sub-packages

### `natural` — Von Neumann Naturals

The natural numbers are constructed as Von Neumann ordinals:

- `0 = ∅` (the empty set)
- `S(n) = n ∪ {n}`

| Class / Object | Role |
|---|---|
| `VonNeumannNatural` | Sealed interface with `Zero` and `Succ(previous)` cases |
| `VonNeumannPeanoSystem` | Peano axiom implementation (zero, successor, injectivity, recursion) |
| `VonNeumannNaturalArithmetic` | Recursive addition/multiplication; kernel-delegated subtraction/division |
| `VonNeumannNaturalOrder` | Order via existence of witness: `a ≤ b ⟺ ∃c. a + c = b` |
| `NaturalIsomorphism` | Bijection `VonNeumannNatural ↔ NaturalNumber` with structure verification |

```kotlin
val three = VonNeumannNatural.of(3)           // Succ(Succ(Succ(Zero)))
val five = VonNeumannNaturalArithmetic.add(three, VonNeumannNatural.of(2))
val asSet = three.toSet()                     // {0, 1, 2}
val kernel = NaturalIsomorphism.toKernel(five) // NaturalNumber(5)
```

### `integer` — Constructed Integers

Integers are equivalence classes of pairs of Von Neumann naturals `(a, b)` representing `a - b`:

| Class / Object | Role |
|---|---|
| `ConstructedInteger` | Equivalence class `(a, b) ~ (c, d) ⟺ a + d = b + c` |
| `ConstructedIntegerArithmetic` | Full integer arithmetic via `Arithmetic<ConstructedInteger>` |
| `ConstructedIntegerOrder` | Total order on integers |
| `NaturalIntegerEmbedding` | Canonical embedding `N ↪ Z` as `n ↦ (n, 0)` |
| `ConstructedIntegerIsomorphism` | Bijection `ConstructedInteger ↔ IntegerNumber` |

```kotlin
val negTwo = ConstructedInteger.of(VonNeumannNatural.ZERO, VonNeumannNatural.of(2)) // (0,2) ~ -2
val three = ConstructedInteger.fromKernel(IntegerNumber.of(3))
val sum = negTwo + three  // represents 1
```

### `rational` — Constructed Rationals

Rationals are equivalence classes of pairs of constructed integers `(a, b)` representing `a / b`:

| Class / Object | Role |
|---|---|
| `ConstructedRational` | Equivalence class `(a, b) ~ (c, d) ⟺ a·d = b·c`, `b ≠ 0` |
| `ConstructedRationalArithmetic` | Full rational arithmetic via `Arithmetic<ConstructedRational>` |
| `ConstructedRationalOrder` | Total order on rationals |
| `IntegerRationalEmbedding` | Canonical embedding `Z ↪ Q` as `z ↦ z/1` |
| `ConstructedRationalIsomorphism` | Bijection `ConstructedRational ↔ RationalNumber` |
| `RationalDensity` | Demonstrates density: `between(a, b)` returns `(a + b) / 2` |

```kotlin
val half = ConstructedRational.of(ConstructedInteger.ONE, ConstructedInteger.fromKernel(IntegerNumber.of(2)))
val third = ConstructedRational.fromKernel(RationalNumber.of(1, 3))
val mid = RationalDensity.between(half, third)  // a rational between 1/3 and 1/2
```

### `real` — Constructed Reals (Cauchy Sequences)

Reals are equivalence classes of Cauchy sequences over constructed rationals:

| Class / Object | Role |
|---|---|
| `ConstructedReal` | Cauchy representative with `term(n)` and `modulus(k)` |
| `ConstructedRealArithmetic` | Full real arithmetic via `Arithmetic<ConstructedReal>` |
| `ConstructedRealOrder` | Total order on reals (at default precision) |
| `RationalRealEmbedding` | Canonical embedding `Q ↪ R` as constant sequences |
| `ConstructedRealIsomorphism` | Bijection `ConstructedReal ↔ RealNumber` |

Arithmetic composes Cauchy representatives lazily — addition builds a `BinaryRepresentative` that evaluates `a_n + b_n` at each index.

```kotlin
val sqrt2 = ConstructedReal.squareRootOf(2)         // 40-step bisection sequence
val pi = ConstructedReal.fromDecimalExpansion("3.141592653589793")
val approx = sqrt2.approximateRational(30)           // 30th rational approximation
val isCauchy = sqrt2.isCauchyOnFinitePrefix(10)      // verify Cauchy property
```

### `irrational` — Constructed Irrationals

Irrationals are constructed reals augmented with a symbolic name, a Dedekind lower-cut predicate, and an irrationality witness:

| Class / Object | Role |
|---|---|
| `ConstructedIrrational` | Irrational with symbol, real representative, cut predicate, witness |
| `IrrationalFoundation` | Enum: `ALGEBRAIC_CONSTRUCTION` or `AXIOMATIC_SYMBOL` |
| `ConstructedIrrationalIsomorphism` | Round-trip with kernel `IrrationalNumber` |

**Algebraic irrationals** (e.g. `sqrt(2)`, `sqrt(3)`, golden ratio) are constructed via bisection with a constructive irrationality witness (`q² ≠ 2`).

**Axiomatic irrationals** (e.g. `pi`, `e`) are declared by symbol and decimal expansion.

```kotlin
val sqrt2 = ConstructedIrrational.SQRT2
val phi = ConstructedIrrational.GOLDEN_RATIO
val pi = ConstructedIrrational.PI

sqrt2.lowerCutContains(ConstructedRational.ONE)          // true: 1 < sqrt(2)
sqrt2.refutesAsExactRational(ConstructedRational.ONE)     // true: 1² ≠ 2
sqrt2.foundation                                          // ALGEBRAIC_CONSTRUCTION
pi.foundation                                             // AXIOMATIC_SYMBOL
```

### `complex` — Constructed Complex Numbers

Complex numbers are ordered pairs of constructed reals `(a, b)` representing `a + bi`:

| Class / Object | Role |
|---|---|
| `ConstructedComplex` | Pair `(real, imaginary)` with full complex arithmetic |
| `ConstructedImaginary` | Pure imaginary `bi` with `(bi)(di) = -bd` |
| `ConstructedComplexArithmetic` | `AlgebraicArithmetic<ConstructedComplex>` (no total order) |
| `ConstructedImaginaryIsomorphism` | Round-trip with kernel `ImaginaryNumber` |
| `ConstructedComplexIsomorphism` | Round-trip with kernel `ComplexNumber` |

```kotlin
val z = ConstructedComplex.of(ConstructedReal.ONE, ConstructedReal.TWO) // 1 + 2i
val w = ConstructedComplex.I                                            // 0 + 1i
val product = z * w                                                     // -2 + 1i
val conj = z.conjugate()                                                // 1 - 2i
val modSq = z.modulusSquared()                                          // 5
```

## Isomorphism and Embedding Patterns

Every number system provides three interoperability mechanisms:

1. **Isomorphism objects** (`NaturalIsomorphism`, `ConstructedIntegerIsomorphism`, etc.) provide `toKernel` / `fromKernel` bijections with `verifyRoundTrip` methods.

2. **Embedding objects** (`NaturalIntegerEmbedding`, `IntegerRationalEmbedding`, `RationalRealEmbedding`) implement the canonical inclusions `N ↪ Z ↪ Q ↪ R ↪ C`.

3. **Extension functions** (`.toMathInteger()`, `.toMathRational()`, `.toMathReal()`, `.toMathComplex()`) provide idiomatic Kotlin conversions along the embedding chain.

## Arithmetic and Order Objects

Each ordered number system provides a singleton `Arithmetic<T>` implementation and a companion `Order` object:

| Number System | Arithmetic Object | Order Object |
|---|---|---|
| N | `VonNeumannNaturalArithmetic` | `VonNeumannNaturalOrder` |
| Z | `ConstructedIntegerArithmetic` | `ConstructedIntegerOrder` |
| Q | `ConstructedRationalArithmetic` | `ConstructedRationalOrder` |
| R | `ConstructedRealArithmetic` | `ConstructedRealOrder` |
| C | `ConstructedComplexArithmetic` | *(none — C is not ordered)* |

The `Arithmetic<T>` interface provides `add`, `subtract`, `multiply`, `divide`, and `compare`. Complex numbers implement `AlgebraicArithmetic<T>` (without `compare`) since they have no total order.
