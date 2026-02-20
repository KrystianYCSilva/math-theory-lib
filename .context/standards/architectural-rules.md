---
description: |
  Core architectural rules and guidelines for the project.
  Use when: making structural decisions or creating new modules.
---

# Architectural Rules

## 1. Zero Mutable State
Immutable objects are fundamental. Nothing must ever mutate its internal state after creation. Variables must use `val`, never `var`. Collections must be standard Kotlin read-only structures or sequences, not `MutableList`.

## 2. Sealed Hierarchies for Domain Closure
When representing a closed domain of variations (such as types of numbers, cardinality limits, or logic formulas), `sealed class` or `sealed interface` must be used. This allows the compiler to guarantee exhaustiveness in `when` expressions.

## 3. Strict Layering Isolation
A module (e.g., `kernel`) cannot have dependencies on a higher-order module (e.g., `set`). The dependency arrow always points "down" the mathematical stack. Any abstraction that needs to span across must be defined in the lower layer.

## 4. No Implicit Assumptions (Mathematical Rigor)
Operations like dividing by zero or taking the square root of a negative real number must explicitly throw `MathArithmeticException` or return a clearly defined alternative structure (e.g., lifting into `ComplexField`), rather than relying on implicit behaviors.

## 5. Roundtrip Isomorphism Tests
Any newly added axiomatic `Construction` must be paired with an `Isomorphism` to the `Kernel`. A test must then randomly generate elements in the Kernel, map to Construction, apply operations, map back to Kernel, and verify the result is unchanged.