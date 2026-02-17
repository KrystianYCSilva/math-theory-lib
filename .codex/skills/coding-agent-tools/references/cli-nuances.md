---
name: cli-nuances
description: |
  Documentation file for agent operations and skill usage.
  Use when: you need procedural guidance for agent execution and context management.
---

# CLI & Tool Peculiarities

Each tool has a unique "dialect". Don't confuse them.

## 1. Claude
-   **Interaction**: Native `/slash-commands`.
-   **MCP**: First-class citizen. Connects to local servers/databases easily.
-   **Nuance**: Prefers "thinking" before acting (CoT is native).

## 2. GitHub Copilot
-   **Interaction**: `@workspace` to scope context, `@terminal` for shell.
-   **Nuance**: Tightly coupled to the specific files you have open or referenced. "Context is King" - if you don't reference it, Copilot might not see it.

## 3. Cursor
-   **Interaction**: `Cmd+K` (Inline edit), `Cmd+L` (Chat), `Composer` (Multi-file agent).
-   **Symbols**: Use `@File`, `@Folder`, `@Docs` to explicitly add context.
-   **Nuance**: "Composer" is distinct from "Chat" - use Composer when you want the AI to write/apply code across multiple files.

## 4. Gemini
-   **Interaction**: Multimodal input (can paste images of UI).
-   **Nuance**: Huge context window allows dumping entire library documentation into the prompt for "Grounding".

