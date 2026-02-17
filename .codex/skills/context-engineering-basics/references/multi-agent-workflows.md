---
name: multi-agent-workflows
description: |
  Documentation file for agent operations and skill usage.
  Use when: you need procedural guidance for agent execution and context management.
---

# Multi-Agent Context & Workflows

## Single vs. Multi-Agent Context

| Feature | Single Agent | Multi-Agent |
|---------|--------------|-------------|
| **Context Window** | Monolithic (everything in one place) | Fragmented (each agent sees a slice) |
| **State** | Global variables in prompt | Distributed state / Shared Blackboard |
| **Communication** | Internal thought chain | Explicit message passing |

## Multi-Agent Strategies

### 1. The Blackboard Pattern
A shared file (e.g., `state.json` or `memory.md`) that acts as the "Central Truth".
- **Read**: All agents can read.
- **Write**: Only specific "Secretary" agents or the current active agent can write.
- **Benefit**: Keeps agents synchronized without passing massive context strings.

### 2. Context Isolation (The "Need to Know" Principle)
Don't give every agent the full project context.
- **Developer Agent**: Loads `code-quality.md` + `tech-stack.md`.
- **QA Agent**: Loads `testing-strategy.md` + `user-stories.md`.
- **PM Agent**: Loads `project-overview.md` + `roadmap.md`.
- **Benefit**: drastically reduces token costs and confusion (hallucinations).

### 3. The Handoff Protocol
When Agent A passes a task to Agent B, it should pass a **Summary**, not the raw history.
- **Bad**: Passing 50 messages of debugging logs.
- **Good**: Passing "Identified bug in Login.ts, suspected Auth0 config issue."

## Common Workflows

### The "Router" Architecture
1.  **Input**: User query.
2.  **Router Agent**: Analyzes intent. Selects correct agent (Dev, QA, or Writer).
3.  **Context Loading**: Router injects specific `.context/` files relevant to the chosen agent.
4.  **Execution**: Specialist agent runs.
5.  **Output**: Result returned.

### The "Linear Chain" Architecture
1.  **Planner**: Creates a plan (Step 1, 2, 3). Writes to `memory.md`.
2.  **Executor**: Reads Step 1 from `memory.md`, executes, writes result.
3.  **Reviewer**: Reads result, approves/rejects.

