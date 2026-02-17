---
name: optimization-techniques
description: |
  Documentation file for agent operations and skill usage.
  Use when: you need procedural guidance for agent execution and context management.
---

# Optimization Techniques

## 1. Context Compression

Techniques to fit more signal into fewer tokens.

-   **Format Conversion**: Convert verbose JSON to YAML or Markdown tables.
    -   *JSON*: `{"id": 1, "name": "Alice"}` (9 tokens)
    -   *YAML*: `id: 1, name: Alice` (6 tokens)
-   **Entity Extraction**: Instead of keeping full chat logs, extract state:
    -   *Raw*: "I want to buy a ticket to Paris tomorrow."
    -   *Compressed*: `destination: Paris, date: tomorrow`

## 2. Dynamic Discovery & JIT Loading

Don't load everything. Wait for a trigger.

-   **Keyword Trigger**: `if "database" in user_query: load("schema.md")`
-   **Semantic Router**: Use a small model (e.g., GPT-3.5-Turbo) to classify the query and pick files.
-   **Agentic Retrieval**: Give the agent a `read_file` tool. It will "Google" your filesystem.

## 3. Prompt Caching

Place static content at the top.

```text
[STATIC PREFIX - Cached]
- System Persona
- Constitution (T0)
- Coding Standards (T1)

[DYNAMIC SUFFIX - Uncached]
- User Query
- Retrieved Documents (JIT)
```

