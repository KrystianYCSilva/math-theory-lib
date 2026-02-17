---
name: clean-code
description: |
  Documentation file for agent operations and skill usage.
  Use when: you need procedural guidance for agent execution and context management.
---

# Clean Code Checklist

## Naming
-   **Intent-Revealing**: `daysSinceCreation` vs `d`.
-   **No Encodings**: Avoid Hungarian notation (`strName`, `iCount`).
-   **Pronounceable**: Names should be easy to say in conversation.

## Functions
-   **Small**: Functions should do one thing.
-   **Low Arity**: Max 3 arguments. If more, introduce a parameter object.
-   **No Side Effects**: Functions should not secretly change system state.

## Comments
-   **Explain "Why", not "What"**: Code explains *what*, comments explain *intent*.
-   **Delete commented-out code**: Version control keeps history. Don't leave zombies.

## Testing
-   **F.I.R.S.T**: Fast, Independent, Repeatable, Self-Validating, Timely.
-   **Coverage**: High coverage != high quality, but low coverage is a risk.

