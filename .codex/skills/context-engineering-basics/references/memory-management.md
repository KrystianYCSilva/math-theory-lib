---
name: memory-management
description: |
  Documentation file for agent operations and skill usage.
  Use when: you need procedural guidance for agent execution and context management.
---

# Memory Management & Multi-Agent Workflows

## Memory Consolidation Tiers

1.  **Working Memory (Hot)**: Last 5-10 messages. High fidelity.
2.  **Session Memory (Warm)**: Summary of the current conversation + extracted entities.
3.  **Episodic Memory (Cold)**: Vector database of past conversations.

## Multi-Agent Context Strategy

Managing context for 1 agent is easy. Managing 5 agents requires architecture.

### Single vs. Multi-Agent

| Feature | Single Agent | Multi-Agent |
|---------|--------------|-------------|
| **Context Window** | Monolithic (everything in one place) | Fragmented (each agent sees a slice) |
| **State** | Global variables in prompt | Distributed state / Shared Blackboard |
| **Communication** | Internal thought chain | Explicit message passing |

### Strategies for Multi-Agent

1.  **The Blackboard Pattern**: A shared `state.json` that all agents can read, but only specific agents can write to.
2.  **Context Isolation**:
    -   *Developer Agent* sees: Code + Tech Specs.
    -   *QA Agent* sees: Code + Test Specs.
    -   *PM Agent* sees: User Stories + Roadmap.
    -   *Benefit*: Reduces noise and token cost for each agent.
3.  **Handoff Protocol**: When Agent A passes task to Agent B, it passes a **Compressed Summary**, not the full chat history.

## Common Workflows

### The "Handoff"
1.  **Router**: Analyzes input. Decides "This is a DB task".
2.  **Context Builder**: Loads `database-schema.md`.
3.  **Handoff**: Calls `DB_Specialist_Agent` with the task + schema.
4.  **Execution**: Specialist solves it.
5.  **Return**: Returns result to Router/User.

