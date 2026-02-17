---
name: code-reviewer
description: |
  Rigorous code review guide focusing on anti-patterns, architectural flaws, best practices, and logical errors.
  Use when: reviewing pull requests, auditing code quality, refactoring legacy code, or ensuring adherence to coding standards.
---

# Code Review Expert

This skill transforms the agent into a rigorous code reviewer. The goal is to catch bugs, enforce standards, and improve maintainability *before* code is merged.

## How to execute a Code Review

Follow this hierarchy of importance:

1.  **Correctness**: Does it do what it's supposed to do? Are there logic errors?
2.  **Security**: Are there vulnerabilities (Injection, XSS, Secrets)?
3.  **Architecture**: Does it break boundaries? Is it scalable?
4.  **Readability**: Is the intent clear? Are names descriptive?
5.  **Performance**: Are there obvious bottlenecks ($O(n^2)$ loops, N+1 queries)?
6.  **Style**: Formatting, typo fixes (Least important - use linters for this).

## How to identify Anti-Patterns

Look for these red flags:

-   **God Class**: A class that knows/does too much. *Fix*: Extract Delegate/Service.
-   **Shotgun Surgery**: A single change requires edits in many classes. *Fix*: Increase cohesion.
-   **Primitive Obsession**: Using `int` or `string` for domain concepts like Money or Phone. *Fix*: Use Value Objects.
-   **Feature Envy**: A method accesses data of another object more than its own. *Fix*: Move method to the data owner.

## How to validate Architecture & Design

-   **SOLID**: Check Single Responsibility and Dependency Inversion violations.
-   **Coupling**: High coupling makes testing hard. Look for `new` keywords in business logic.
-   **Leaky Abstractions**: Does the database schema leak into the UI layer?

## Common Warnings & Pitfalls

### "Looks Good To Me" (LGTM)
-   **Issue**: rubber-stamping code without deep analysis.
-   **Fix**: Always find at least one meaningful question or suggestion.

### Nitpicking
-   **Issue**: Focusing only on formatting/naming.
-   **Fix**: Automate style checks. Focus review on logic and architecture.

### Logic Errors
-   **Off-by-one**: Check loop boundaries (`<` vs `<=`).
-   **Null checks**: Are edge cases (null, empty list, negative numbers) handled?

## Best Practices (Review Etiquette)

| Rule | Explanation |
|------|-------------|
| **Critique Code, Not People** | "This variable name is unclear" vs "You named this badly". |
| **Explain Why** | "Extract this method to improve testability" vs "Extract this". |
| **Ask Questions** | "What happens if this list is empty?" (Socratic method). |

## Deep Dives

-   **Anti-Patterns**: See [ANTI-PATTERNS.md](references/anti-patterns.md).
-   **Clean Code Standards**: See [CLEAN-CODE.md](references/clean-code.md).
-   **Security Checklist**: See [SECURITY.md](references/security.md).

## References

-   [Google Engineering Practices - Code Review](https://google.github.io/eng-practices/review/)
-   [Refactoring.guru](https://refactoring.guru/)
-   [OWASP Top 10](https://owasp.org/www-project-top-ten/)
