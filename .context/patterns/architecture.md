---
description: |
  Common architectural patterns used in the project.
  Use when: implementing new features or refactoring.
---

# Architecture Patterns

## 1. Layered Architecture (Strict Acyclic Dependencies)

The system is built on a strict, monotonic layer dependency model:

```text
Layer 4: Algebra, Analysis, Combinatorics, etc.
   ↓
Layer 3: Construction (Axiomatic N, Z, Q, R, C)
   ↓
Layer 2: Set Theory & Relations (MathSet, PartialOrders, Functions)
   ↓
Layer 1: Logic (First-Order Logic, Proofs)
   ↓
Layer 0: Kernel (Platform Primitives, Value Classes)
```

**Rule:** Higher layers may import lower layers. Lower layers must *never* know about higher layers. The Kernel cannot depend on Set Theory.

## 2. Dual Mode (Kernel vs Construction)

Every mathematical concept is implemented in two ways:
*   **Kernel:** Uses zero-overhead `value classes` around `BigInteger`/`BigDecimal` for maximum efficiency.
*   **Construction:** Uses axiomatic models (e.g., sequences, nested sets).

**Pattern:** We enforce an `Isomorphism` interface between both representations. They must evaluate identically under mathematical laws.

## 3. Context-Oriented Algebra

Instead of objects containing operations (`a.add(b)`), operations belong to an explicit context/structure object. This allows elements to be reused across different algebraic structures without violating the Open-Closed Principle.

*   **Anti-pattern:** `class Integer { fun add(other: Integer): Integer }`
*   **Pattern:** `object IntegerRing : CommutativeRing<Integer> { fun add(a: Integer, b: Integer): Integer }`

## 4. Lazy Evaluation (Infinite Sets)

Sets in Mathematics are frequently infinite. To represent this computationally without `OutOfMemory` errors:
*   Sets expose `.elements(): Sequence<T>`, never `.toList()`.
*   Operations like `intersection` or `union` over intensional sets return new rule-based objects.