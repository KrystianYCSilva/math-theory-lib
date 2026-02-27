# Contributing to mathsets-kt

Thank you for your interest in contributing!

## Getting Started

1. Fork the repository and clone your fork.
2. Ensure JDK 21+ is installed.
3. Run `./gradlew check` to verify the build.

## Guidelines

- Follow Kotlin official code style (`kotlin.code.style=official`).
- All core types must be **immutable** — no `var` or mutable collections without justification.
- Write **property-based tests** using Kotest for algebraic laws (associativity, commutativity, identity, etc.).
- Add KDoc to all public types and functions.
- Keep modules focused — one mathematical concept per module.

## Pull Requests

1. Create a feature branch from `main`.
2. Write tests before or alongside implementation.
3. Ensure `./gradlew check` passes.
4. Open a PR with a clear description of the change.

## Code of Conduct

Be respectful and constructive in all interactions.
