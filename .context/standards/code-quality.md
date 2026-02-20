---
description: |
  Code quality standards and conventions.
  Use when: writing or reviewing code.
---

# Code Quality

## 1. Kotlin Multiplatform (KMP)
Code should be placed in `commonMain` and `commonTest` as much as possible. Only platform-specific wrappers (like random number generation or native array interactions) should use `expect`/`actual`.

## 2. Zero-Overhead Abstractions
Use `@JvmInline value class` extensively for kernel structures. This ensures that memory consumption during high-volume operations (like combinatorial algorithms or matrix math) remains strictly near the underlying primitive values (`Int`, `Double`, `BigInteger`).

## 3. Strong Typing
Do not use `String` or basic numeric types to represent domain concepts if they can be misused. Use strong types (e.g., `value class NaturalNumber(val value: BigInteger)` instead of `BigInteger` everywhere) to prevent accidental negative assignments or cross-domain errors.

## 4. Documentation
Code MUST be documented with KDoc using clear mathematical notations when appropriate. Always describe *what* a function mathematically represents, any preconditions (e.g., "x must not be 0"), and the mathematical laws it upholds.

## 5. Avoid Nulls
Utilize Kotlin's null-safety. If an operation is invalid (like inverse on zero), prefer returning `Result<T>` or explicitly throwing a well-typed domain exception. Avoid returning `null` unless the domain explicitly models "absence" (like a partial function).