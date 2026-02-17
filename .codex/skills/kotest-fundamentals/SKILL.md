---
name: kotest-fundamentals
description: |
  Guidelines and quick references for writing unit and property tests with Kotest in Kotlin multiplatform projects.
---

# kotest-fundamentals

Overview
Concise guidance for using Kotest: test styles (StringSpec, FunSpec, BehaviorSpec), matchers, property testing, and coroutine testing patterns.

Key recommendations
- Prefer FunSpec or StringSpec for clear, imperative tests; use BehaviorSpec for BDD-style flows.
- Use Kotest matchers (shouldBe, shouldContain) for readable assertions.
- For coroutine testing, use runTest or Kotest's co-routines support and proper dispatchers.

References: see references/kotest-fundamentals.md for examples and snippets.

(End of SKILL.md)
