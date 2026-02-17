---
name: warnings
description: |
  Documentation file for agent operations and skill usage.
  Use when: you need procedural guidance for agent execution and context management.
---

# Warnings & Common Pitfalls

## 1. Context Blindness
-   **Error**: Assuming the AI sees files you haven't explicitly added.
-   **Fix**: Use `@file` (Cursor), `open file` (Claude), or `@workspace` (Copilot) explicitly.

## 2. Version Hallucination
-   **Error**: Asking for "Next.js 15 features" from a model with a 2023 cutoff.
-   **Fix**: Always check the model's knowledge cutoff or provide documentation via RAG/MCP.

## 3. Destructive Edits
-   **Error**: Allowing an agent to `rm -rf` or overwrite files without git tracking.
-   **Fix**: Always commit *before* letting an agent refactor. Use tools like `git diff` to review.

## 4. Infinite Loops
-   **Error**: Agents getting stuck in a "Fix error -> Run test -> Fix error" loop.
-   **Fix**: Set a maximum step count (e.g., 5 turns) and force a human handoff if unresolved.

