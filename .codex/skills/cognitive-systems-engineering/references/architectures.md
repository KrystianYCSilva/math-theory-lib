---
name: architectures
description: |
  Documentation file for agent operations and skill usage.
  Use when: you need procedural guidance for agent execution and context management.
---

# Cognitive Architectures Deep Dive

## CoALA (Cognitive Architectures for Language Agents)

The **CoALA framework** standardizes language agents around four key modules.

### 1. Memory Module
-   **Working Memory**: The current context window.
-   **Long-term Memory**: External storage (Vector DBs, Files).

### 2. Action Space
-   **Internal Actions**: Thinking, updating memory.
-   **External Actions**: Calling tools, replying to user.

### 3. Decision Making
-   **Planning**: Decomposing goals.
-   **Execution**: Choosing the next action.

## ACT-R (Adaptive Control of Thought-Rational)

A cognitive theory of learning and memory.

### Key Concepts
-   **Declarative Memory**: "Knowing that" (Facts).
-   **Procedural Memory**: "Knowing how" (Rules).
-   **Production Rules**: IF-THEN logic blocks that drive behavior.

### Application to AI Agents
-   Use **Procedural Memory** to store tools (Agent Skills).
-   Use **Declarative Memory** to store RAG documents.
-   Use **Production Rules** as the System Prompt instructions.

