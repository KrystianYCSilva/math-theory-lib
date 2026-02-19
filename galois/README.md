# galois

Galois theory and field extensions for `mathsets-kt`.

## Overview

This module provides computational tools for:

- **Field Extensions** — Simple algebraic extensions K[x]/(p(x)) with full arithmetic
- **Finite Fields** — GF(p^n) via irreducible polynomials, Frobenius endomorphism, primitive elements
- **Algebraic Elements** — Elements defined by minimal polynomials, irreducibility testing
- **Splitting Fields** — Root finding, factorization verification
- **Galois Groups** — Aut(L/K) as a group, automorphism composition, fixed fields
- **Galois Correspondence** — Fundamental theorem verification: subgroups <-> intermediate fields
- **Cyclotomic Polynomials** — Phi_n(x), roots of unity, Euler's totient

## Dependencies

- `:kernel` — Number types
- `:algebra` — Algebraic hierarchy (Field, Group interfaces, ZpField)
- `:polynomial` — Polynomial arithmetic and Euclidean division

## Key Types

| Type | Description |
|---|---|
| `FieldExtension<K>` | K[x]/(p(x)) as a field over `ExtensionElement<K>` |
| `ExtensionElement<K>` | Element of a field extension (polynomial representative) |
| `FiniteField` | GF(p^n) with Frobenius, primitive elements |
| `AlgebraicElement<K>` | Element defined by its minimal polynomial |
| `GaloisGroup<K>` | Gal(L/K) implementing `Group<FieldAutomorphism<K>>` |
| `GaloisCorrespondence<K>` | Fundamental theorem verifier |
| `SplittingField<K>` | Splitting field of a polynomial |
| `CyclotomicPolynomials` | Cyclotomic polynomial utilities |
| `CyclotomicFieldExtension` | Cyclotomic extensions over finite fields |

## Usage Examples

```kotlin
// GF(8) = GF(2^3)
val gf8 = FiniteField(2, 3)
gf8.elements()          // 8 elements
gf8.frobenius(gf8.alpha) // x -> x^2
gf8.isPrimitiveElement(gf8.alpha)

// Galois group of GF(4)/GF(2)
val gf2 = ZpField(2)
val ext = FieldExtension(gf2, Polynomial(listOf(1, 1, 1)))
val elems = ext.elements(gf2.elements())
val galois = GaloisGroup.of(ext, elems)
// galois.order == 2, galois.isGalois == true

// Galois correspondence
val correspondence = GaloisCorrespondence(galois, elems)
val result = correspondence.verify()
// result.isValid == true
```
