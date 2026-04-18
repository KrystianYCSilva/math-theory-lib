# mathsets-kt

## What This Project Is
Kotlin Multiplatform library implementing ZFC set theory and the complete number tower (ℕ→ℤ→ℚ→ℝ→ℂ), with 27 modules covering algebra, analysis, category theory, computability, and more.

## Tech Stack
Kotlin 2.1.0 KMP · Gradle KTS · Kotest 5.8 (FunSpec + Property) · Detekt · Dokka · bignum 0.3.10

## Conventions
- Zero mutable state: `val` only, read-only collections, `Sequence<T>` for infinite structures
- Sealed hierarchies for closed domains (numbers, formulas, cardinality)
- Strict 4-layer architecture: Kernel → Logic → Set Theory → Construction
- Roundtrip isomorphism tests for every Construction ↔ Kernel pair
- Property-based tests for algebraic laws over randomized inputs

## How to Build and Test
```bash
gradlew.bat build                    # full build
gradlew.bat check                    # all tests + detekt
gradlew.bat :set:jvmTest             # single module
gradlew.bat test --tests "*SetAlgebra*"  # pattern match
gradlew.bat detekt                   # static analysis
gradlew.bat dokkaHtml                # API docs
```

## Key Directories
- `kernel/` — Primitive number types (value classes over BigInteger/BigDecimal)
- `logic/` — First-order logic, ZFC axioms, Peano system
- `set/` — MathSet, SetAlgebra, ZFCVerifier
- `relation/` — OrderedPair, equivalence, partial/total/well orders
- `function/` — Injection, surjection, bijection, choice
- `construction/` — Axiomatic number tower (Von Neumann → Cauchy reals → ℂ)
- `algebra/` — Group → Ring → Field hierarchy
- `docs/` — DOCUMENTATION.md, ARCHITECTURE.md, ROADMAP.md