---
description: |
  Testing strategy and guidelines.
  Use when: writing tests or deciding what to test.
---

# Testing Strategy

## 1. Property-Based Testing (PBT)
Since mathsets-kt implements mathematical theories, testing edge cases is insufficient. We rely heavily on **Kotest Property Testing** (`checkAll`, `forAll`). 
*   **Law Verification:** If a module implements a Group, tests must generate hundreds of random elements and verify Associativity, Identity, and Inverse laws hold.
*   **Generators:** Custom generators (Arbitrary `Arb<T>`) must be written for all domain types (e.g., `Arb.naturalNumber()`, `Arb.matrix(rows, cols)`).

## 2. Isomorphism "Roundtrip" Testing
For the "Dual Mode" philosophy, every `Construction` type must be tested against its `Kernel` counterpart.
*   Generate a `Kernel` object $x$.
*   Map it to `Construction` object $x'$.
*   Perform operation $O$ on $x$ to get $y$.
*   Perform operation $O'$ on $x'$ to get $y'$.
*   Map $y'$ back to `Kernel` and assert it equals $y$.

## 3. Unit Tests (`FunSpec`)
For explicit boundary conditions (e.g., $0 \times \infty$, dividing by $0$, paradoxes in Set Theory), simple `FunSpec` blocks are sufficient and necessary to quickly assert the correct exceptions are thrown or specific rules are respected.

## 4. Test Independence
Tests must be entirely stateless and deterministic. Random seeds for Property Tests should be reproducible in the event of failure.