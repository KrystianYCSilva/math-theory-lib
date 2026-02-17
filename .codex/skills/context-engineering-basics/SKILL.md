---
name: context-engineering-basics
description: |
  Master context engineering: unified structures, metadata-driven discovery, compression, JIT loading, and multi-agent memory management.
  Use when: architecting AI systems, optimizing token usage, designing multi-agent workflows, or managing complex context windows.
---

# Context Engineering Basics

Context Engineering is the discipline of curating the "mental state" of an AI model. It determines whether an agent is a genius or a hallucination engine.

## How to design a Unified Context Structure

Use a tiered file structure (like `.context/`) to organize information by authority.

-   **T0 (Constitution)**: Absolute rules.
-   **T1 (Standards)**: Normative guidelines.
-   **T2 (Context)**: Informative project data.

**Crucial:** Use high-quality **Metadata** (filename + frontmatter description) for every file. The AI uses these as "semantic hooks" to decide what to read.

> See [Context Structure & Metadata](references/context-structure.md) for the full template and metadata guide.

## How to optimize Token Usage

Tokens are money and latency.

-   **Compression**: Minify data formats (JSON -> YAML) and summarize history.
-   **Prompt Caching**: Structure prompts with static prefixes to leverage cache hits.
-   **JIT Loading**: Load detailed context only when triggered by keywords.

> See [Optimization Techniques](references/optimization-techniques.md) for compression algorithms and caching strategies.

## How to manage Memory (Single vs. Multi-Agent)

Memory strategies differ significantly by architecture:

-   **Single Agent**: Monolithic context window with sliding window or summary.
-   **Multi-Agent**: Distributed context. Each agent has private "Working Memory" and accesses a shared "Knowledge Base".

> See [Memory Management](references/memory-management.md) for consolidation patterns and multi-agent workflows.

## Common Pitfalls & Warnings

| Error | Consequence | Fix |
|-------|-------------|-----|
| **Vague Metadata** | Agent fails to find relevant docs. | Ensure `description` contains specific keywords/triggers. |
| **Context Flooding** | "Lost in the Middle" phenomenon. | Use JIT loading; don't dump all files at once. |
| **Shared State Pollution** | In multi-agent, one agent confuses another. | Isolate Working Memory; use explicit message passing. |

## Useful Scripts

-   `scripts/load-context.sh`: Example script to aggregate context based on Tiers.

## References

-   [Context Structure & Metadata](references/context-structure.md)
-   [Optimization Techniques](references/optimization-techniques.md)
-   [Memory Management & Multi-Agent](references/memory-management.md)
-   [Anthropic Prompt Caching](https://docs.anthropic.com/en/docs/build-with-claude/prompt-caching)
