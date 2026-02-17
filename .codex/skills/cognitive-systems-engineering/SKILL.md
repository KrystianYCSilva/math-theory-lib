---
name: cognitive-systems-engineering
description: |
  Engineering cognitive architectures for AI agents using frameworks like CoALA and ACT-R. Covers memory systems, decision cycles (OODA), and multi-agent patterns.
  Use when: architecting complex AI agents, designing memory systems, or defining multi-agent collaboration patterns.
---

# Cognitive Systems Engineering

This skill provides blueprints for building the "brain" of an AI agent, moving beyond simple prompt-response loops to persistent, stateful, and goal-oriented architectures.

## How to design Memory Systems

Implement memory tiers inspired by CoALA and ACT-R:
> See [Architectures Deep Dive](references/architectures.md) for detailed schemas.

1.  **Working Memory (Short-Term)**:
    -   *Function*: Active context window management.
    -   *Implementation*: Sliding window of last $N$ messages or summary of current session.
2.  **Episodic Memory (Long-Term)**:
    -   *Function*: Recall past events.
    -   *Implementation*: Vector Database (RAG) storing interactions as embeddings.
3.  **Semantic Memory (Knowledge)**:
    -   *Function*: Static facts and rules.
    -   *Implementation*: `.context/` files or Knowledge Graph.
4.  **Procedural Memory (Skills)**:
    -   *Function*: "How-to" knowledge.
    -   *Implementation*: Tool definitions and Agent Skills (like this one).

## How to implement Decision Cycles

Choose a cycle based on agent autonomy:

-   **OODA Loop (Observe-Orient-Decide-Act)**: Best for dynamic environments.
    -   *Observe*: Read user input + environment state.
    -   *Orient*: Retrieve relevant memory + context.
    -   *Decide*: Select tool or response strategy.
    -   *Act*: Execute tool or generate text.
-   **ReAct (Reason + Act)**: Interleave reasoning traces with actions.
    -   *Thought*: "I need to check the file size."
    -   *Action*: `check_file_size("log.txt")`
    -   *Observation*: "10MB"

## How to structure Multi-Agent Systems

Select a pattern based on task complexity:

| Pattern | Description | Best For |
|---------|-------------|----------|
| **Orchestrator-Workers** | Central brain delegates to specialists. | Complex tasks with clear sub-steps. |
| **Blackboard** | Agents read/write to a shared state file. | Collaborative planning, non-linear tasks. |
| **Pipeline** | Output of Agent A becomes Input of Agent B. | Data processing, CI/CD chains. |
| **Debate** | Agents propose and critique solutions. | High-stakes verification. |

## References

-   [CoALA Framework (Sumers et al.)](https://arxiv.org/abs/2303.11366)
-   [ACT-R Theory](http://act-r.psy.cmu.edu/)
-   [Architectures Deep Dive](references/architectures.md)
