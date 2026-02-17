---
name: cognitive-architectures
description: |
  Design, optimize, and manage cognitive architectures for LLM agents, from historical roots to modern CoALA implementations.
  Use when: building autonomous agents, designing memory/planning systems, researching agentic behaviors, or optimizing agent performance.
---

# Cognitive Architectures

Cognitive architectures provide the structural design for AI agents, moving beyond simple "prompt-response" loops to stateful, goal-oriented entities.

## How to Apply CoALA Framework

The CoALA (Cognitive Architectures for Language Agents) framework organizes agent design into four modules:

1.  **Memory**:
    -   *Working Memory*: The active context window (current interaction).
    -   *Episodic Memory*: Past experiences (Vector DB/RAG).
    -   *Semantic Memory*: Facts and knowledge (Knowledge Graphs/Docs).
    -   *Procedural Memory*: Code/Tools/Skills (Implicit or Explicit).
2.  **Action Space**: The set of executable external actions (API calls, tool use) and internal actions (memory retrieval, reasoning).
3.  **Decision Making**: The "loop" (e.g., ReAct, OODA, Plan-and-Solve) that selects actions based on memory and perception.
4.  **Perception**: How the agent inputs information (Text, Vision, Audio).

## How to Optimize Performance

-   **Memory Retrieval**: Use *Hybrid Search* (Keyword + Vector) to reduce retrieval latency and increase relevance. Implement *Memory Decay* to prioritize recent or frequently accessed memories.
-   **Reflection**: Periodically summarize episodic memory into high-level insights (Semantic Memory) to prevent context saturation.
-   **Tool Use**: Minimize token usage by optimizing tool descriptions and using structured outputs (JSON) for tool arguments.

## Common Pitfalls & Solutions

| Pitfall | Impact | Solution |
|---------|--------|----------|
| **Context Overflow** | Model hallucination or truncation. | Implement summarization hierarchies and strict token budgeting. |
| **Action Loops** | Agent gets stuck repeating the same tool call. | Add a "max_retries" counter and a "give_up" or "ask_human" fallback. |
| **Memory Pollution** | Irrelevant info clogs retrieval. | Use strict filtering/ranking during RAG retrieval; periodically prune vector DB. |

## Evolution & History

Understanding the lineage helps choose the right pattern:
> See [History of Cognitive Architectures](references/history.md) for the evolution from Symbolic AI to LLMs.

## Examples

### Example: Implementing a Reflection Step

**Input:** 10 recent user interactions (Episodic Memory).
**Goal:** Update User Profile (Semantic Memory).

**Prompt Pattern:**
"Review the last 10 interactions. Extract 3 key facts about the user's preferences. Update the 'User Profile' JSON object. discard the raw logs."

## References

-   [CoALA Paper (Sumers et al.)](https://arxiv.org/abs/2309.02427)
-   [Generative Agents (Park et al.)](https://arxiv.org/abs/2304.03442)
-   [History of Cognitive Architectures](references/history.md)
