---
name: anti-patterns
description: |
  Documentation file for agent operations and skill usage.
  Use when: you need procedural guidance for agent execution and context management.
---

# Common Anti-Patterns

## Object-Oriented
-   **Anemic Domain Model**: Entities with data but no behavior (setters only).
-   **Circular Dependency**: Module A -> B -> A. *Fix*: Dependency Inversion or Mediator.
-   **Yo-Yo Problem**: Deep inheritance hierarchy where you bounce up and down to understand flow. *Fix*: Prefer Composition.

## Error Handling
-   **Swallowing Exceptions**: `catch (Exception e) { // do nothing }`. *Fix*: Log or rethrow.
-   **Return Null**: Returning null forces caller to check. *Fix*: Return `Optional`, Result type, or Empty Object.

## Concurrency
-   **Double-Checked Locking**: Often implemented incorrectly. *Fix*: Use standard singleton patterns or `volatile` correctly.
-   **Busy Waiting**: `while(!condition) {}`. *Fix*: Use `wait/notify` or latches.

