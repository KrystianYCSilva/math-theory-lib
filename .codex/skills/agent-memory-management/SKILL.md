---
name: agent-memory-management
description: |
  Architect and optimize memory systems for AI agents, from basic context windows to advanced RAG and multi-agent state.
  Use when: designing agent architectures, solving context limit issues, or implementing stateful multi-agent systems.
---

# Agent Memory Management

Memory turns a stateless LLM into a coherent agent. It ranges from simple buffers to complex cognitive architectures.

## How to implement Basic Memory

For simple conversational agents or single-task bots.

-   **Sliding Window**: Keep last N messages or T tokens. Simple, but loses distant history.
-   **FIFO Buffer**: Queue-based approach. Oldest out, newest in.
-   **Token Budgeting**: Truncate middle or head to fit context window (e.g., `HEAD + ... + TAIL`).

## How to implement Advanced Memory

For autonomous agents and complex workflows.

-   **Summarization**: Recursively summarize history into a "System Note". Preserves semantic gist, loses detail.
-   **Vector/RAG**: Store chunks in vector DB. Retrieve top-k relevant chunks per query. "Infinite" capacity, non-linear.
-   **Entity Memory**: Extract and update key-value pairs (e.g., `User.name = "Alice"`). Specific for personalization.

## How to manage Multi-Agent State

-   **Shared Blackboard**: Single `state.json` accessible by all agents. Good for synchronization, bad for contention.
-   **Message Passing**: Agents exchange explicit JSON messages. No shared state. Better for distributed/modular systems.
-   **Role-Based Views**: Filter context based on agent role (e.g., "Coder" sees code, "Reviewer" sees diffs).

## Common Patterns & Anti-Patterns

| Pattern | Verdict | Why |
|---------|---------|-----|
| **Infinite Context** | Anti-Pattern | "Lost in the Middle" syndrome; high latency/cost. |
| **Context Compression** | Best Practice | Remove stop words, standard headers, or whitespace to save tokens. |
| **Episodic RAG** | Best Practice | Store "episodes" (goal-action-result) to learn from past mistakes. |
| **Global Mutable State** | Risk | In multi-agent, causes race conditions or hallucinations if not locked/validated. |

## Troubleshooting Memory

-   **Hallucination**: Often caused by stale context or conflicting memories. *Fix*: Add timestamps to memory chunks; decay old memories.
-   **Repetition**: Caused by "circular context" (loops in history). *Fix*: Deduplicate history before feeding to model.
-   **Context Overflow**: *Fix*: Implement strict token counting (e.g., `tiktoken`) before request.

## Examples

### Example: Shared Blackboard (Multi-Agent)

**Context**: A generic shared state object.

```json
{
  "project_status": "active",
  "current_task": "fix-login-bug",
  "agents": {
    "coder": "writing-test",
    "reviewer": "idle"
  },
  "artifacts": ["src/auth.ts", "tests/auth.test.ts"]
}
```

## References

-   [CoALA: Cognitive Architectures for Language Agents](https://arxiv.org/abs/2309.02427)
-   [Generative Agents: Interactive Simulacra](https://arxiv.org/abs/2304.03442)
