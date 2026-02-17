---
name: kotlin-functional-fundamental
description: |
  Promote functional programming practices in Kotlin to produce predictable, testable and composable code. Use when: the agent must design, review or refactor Kotlin code to apply functional paradigms in production.
---

# kotlin-functional-fundamental

Overview
Functional programming in Kotlin emphasizes pure functions, immutability and composition. This skill gives pragmatic, incremental guidance to apply FP in Kotlin codebases while remaining interoperable with Java.

When to Use
- Data pipelines, transformations, ETL, and business logic composed of pure transformations.
- Concurrency and asynchronous processing where immutability simplifies reasoning and testing.
- Modules where determinism, testability and composability are primary goals.

Best Practices
- Guarantee immutability using val, read-only collections and explicit state copies for changes.
- Prefer pure functions: inputs -> outputs; push side effects to the edges (I/O adapters, effect handlers).
- Compose small functions (map/filter/fold) and favor expression-oriented constructs (if/when returning values).
- Use Sequences for lazy pipelines on large datasets; use Collections for simplicity when appropriate.
- Apply inline/noinline/crossinline where performance of higher-order functions matters, but measure impact on binary size.

Pitfalls and Mitigations
- Capturing mutable state in closures: ensure captured values are immutable or pass state explicitly.
- Overusing scope functions (let/apply/run/also/with): prefer named small functions to improve readability.
- Mixing effects and pure logic: isolate effects behind adapters and keep core logic pure for testability.
- Ignoring allocation costs in pipelines: switch to Sequences or stream-aware APIs when profiling reveals issues.

Reference Routing
- Read references/core_fp_concepts.md for HOFs, inline optimizations, collection pipelines and Sequence vs Iterable trade-offs.
- Read references/error_handling_and_arrow.md for functional error handling patterns (Result, Option, Either) and practical Arrow usage.
- Rule: follow Best Practices for most PRs; consult references when making performance, interoperability, or error-model decisions.

How to introduce FP incrementally
1. Identify stateless modules and refactor utilities to pure functions.
2. Extract side effects to adapter boundaries and expose pure interfaces for core logic.
3. Replace nullable returns progressively with Option/nullable contracts or Result/Either where appropriate.

How to evaluate performance trade-offs
1. Measure real workloads; prefer clarity until profiling justifies optimization.
2. Use Sequences for multi-stage pipelines over large datasets to reduce intermediate allocations.
3. Inline critical small functions and measure impact on binary size and performance.

How to apply functional error handling
1. Choose the contract: Result for simple success/failure, Either/Option for typed failures and absence.
2. Compose errors with map/flatMap instead of throwing; document failure semantics at API boundaries.
3. Convert thrown exceptions from legacy Java code to functional results at the adapter layer.
