# algebra

Abstract algebra: groups, rings, fields, and their instances.

## Overview

This module implements the algebraic hierarchy using **context-oriented programming**: algebraic operations live on context objects (interfaces), not on the elements themselves. This allows the same type (e.g., `IntegerNumber`) to participate in multiple algebraic structures (additive group, multiplicative monoid) without conflict.

The hierarchy follows the Bourbaki ordering:

```
Magma → Semigroup → Monoid → Group → AbelianGroup
                                  ↓
                    Semiring → Ring → CommutativeRing → IntegralDomain → EuclideanDomain
                                                                     → Field → OrderedField
```

## Key Classes

### Algebraic Hierarchy

| Interface | Description |
|---|---|
| `Magma<T>` | Closed binary operation `op(a, b): T` |
| `Semigroup<T>` | Associative magma |
| `Monoid<T>` | Semigroup with `identity` element |
| `Group<T>` | Monoid with `inverse(a)` |
| `AbelianGroup<T>` | Commutative group |
| `AdditiveGroup<T>` | Group with `+` syntax (`add`, `negate`, `zero`) |
| `MultiplicativeMonoid<T>` | Monoid with `*` syntax (`multiply`, `one`) |
| `Semiring<T>` | Additive commutative monoid + multiplicative monoid + distributivity |
| `Ring<T>` | Semiring with additive abelian group |
| `CommutativeRing<T>` | Ring with commutative multiplication |
| `IntegralDomain<T>` | CommutativeRing with no zero divisors |
| `EuclideanDomain<T>` | IntegralDomain with Euclidean division and `gcd` |
| `Field<T>` | Every non-zero element has a multiplicative inverse |
| `OrderedField<T>` | Field with compatible total order |

### Concrete Instances

| Object | Type | Structure |
|---|---|---|
| `IntAdditiveGroup` | `IntegerNumber` | Abelian group under `+` |
| `RationalAdditiveGroup` | `RationalNumber` | Abelian group under `+` |
| `RealAdditiveGroup` | `RealNumber` | Abelian group under `+` |
| `ComplexAdditiveGroup` | `ComplexNumber` | Abelian group under `+` |
| `NaturalAdditiveMonoid` | `NaturalNumber` | Monoid under `+` |
| `NaturalMultiplicativeMonoid` | `NaturalNumber` | Monoid under `*` |
| `IntegerRing` | `IntegerNumber` | Euclidean domain (ℤ is a ring with GCD) |
| `RationalField` | `RationalNumber` | Ordered field (ℚ) |
| `RealField` | `RealNumber` | Ordered field (ℝ) |
| `ComplexField` | `ComplexNumber` | Field (ℂ, no order) |

### Finite Groups

| Class | Description |
|---|---|
| `CyclicGroup(n)` | ℤ/nℤ under addition. Generator, element order. |
| `PermutationGroup(n)` | Symmetric group S_n. Cycle notation, sign, alternating subgroup. |
| `Permutation` | A single permutation with compose, invert, cycle notation, order, sign. |
| `DihedralGroup(n)` | D_n: symmetries of a regular n-gon. Rotations and reflections. |

### Finite Rings

| Class | Description |
|---|---|
| `ZnRing(n)` | ℤ/nℤ as a commutative ring. |
| `ZpField(p)` | ℤ/pℤ as a field (p must be prime). |

### Structural Components

| Class | Description |
|---|---|
| `Subgroup<T>` | Validated subgroup with coset operations, normality check, Lagrange. |
| `GroupHomomorphism<A,B>` | Map preserving group operation. Kernel, image, injectivity, surjectivity. |
| `RingHomomorphism<A,B>` | Map preserving `+` and `*`. Kernel, image. |
| `QuotientGroup<T>` | G/N via normal subgroup. |
| `Ideal<T>` | Ideal of a ring with principal/prime/maximal checks. |
| `QuotientRing<T>` | R/I via ideal. |
| `AlgebraicLaws` | Brute-force law verification (associativity, commutativity, distributivity, etc.). |

### Module and Linear Structures

| Class/Interface | Description |
|---|---|
| `Module<R, M>` | R-module with scalar action and additive abelian structure. |
| `VectorSpace<K, V>` | Module over a field K. |
| `FiniteDimensionalVectorSpace<K>` | Coordinate space K^n with canonical basis. |
| `Submodule<R, M>` | Finite-validated submodule (closure checks). |
| `Basis<K>` | Basis of a finite-dimensional vector space. |
| `LinearMap<K, V, W>` | Linear map interface. |
| `FiniteDimensionalLinearMap<K>` | Matrix-based linear map with rank/nullity. |
| `ExactSequence<K>` | Exactness checker for A -> B -> C. |
| `ShortExactSequence<K>` | Short exact sequence checker for 0 -> A -> B -> C -> 0. |
| `Algebra<K, A>` | K-algebra (vector space + internal multiplication). |
| `TensorProduct<R, M, N>` | Formal finite tensor sums with scalar arithmetic. |

## Usage Examples

### Context-oriented algebra

```kotlin
val ring = IntegerRing
val a = IntegerNumber.of(7)
val b = IntegerNumber.of(3)
val sum = ring.add(a, b)        // 10
val neg = ring.negate(a)        // -7
val prod = ring.multiply(a, b)  // 21
val gcd = ring.gcd(IntegerNumber.of(12), IntegerNumber.of(8)) // ±4
```

### Finite groups

```kotlin
val z6 = CyclicGroup(6)
val s3 = PermutationGroup(3)
val d4 = DihedralGroup(4)

// Verify group axioms
AlgebraicLaws.verifyGroupAxioms(z6, z6.elements())  // true
AlgebraicLaws.verifyGroupAxioms(s3, s3.elements())  // true

// S_3 is not abelian
AlgebraicLaws.verifyCommutativity(s3, s3.elements()) // false
```

### Subgroups and quotients

```kotlin
val z12 = CyclicGroup(12)
val h = Subgroup(z12, setOf(0, 4, 8))  // subgroup of order 3
h.index(z12.elements())  // 4 (Lagrange: 12/3)

val quotient = QuotientGroup(z12, Subgroup(z12, setOf(0, 3, 6, 9)), z12.elements())
quotient.order()  // 3
```

### Fields

```kotlin
val zp = ZpField(7)
AlgebraicLaws.verifyFieldAxioms(zp, zp.elements()) // true
zp.reciprocal(3)  // 5 (since 3*5 = 15 ≡ 1 mod 7)
```

## Module Dependencies

- `kernel` — `NaturalNumber`, `IntegerNumber`, `RationalNumber`, `RealNumber`, `ComplexNumber`, `MathElement`
