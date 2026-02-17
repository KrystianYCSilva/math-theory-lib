---
name: warnings
description: |
  Documentation file for agent operations and skill usage.
  Use when: you need procedural guidance for agent execution and context management.
---

# Warnings & Common Pitfalls (Cognitive Systems)

## 1. Infinite Loops
- **Error**: Agent getting stuck retrying the same failed action (e.g., "File not found" loop).
- **Fix**: Implement a "Retry Counter" in the Decision module. Max 3 retries, then escalate to human.

## 2. Context Pollution
- **Error**: Flooding Working Memory with too much Episodic recall (RAG top-k=50).
- **Fix**: Use strict similarity thresholds and re-ranking. Keep top-k small (3-5).

## 3. Hallucination of Capability
- **Error**: Agent claims it can do something it has no tool for (e.g., "I will email the user").
- **Fix**: Strict "Tool Grounding". If no tool exists in Action Space, the agent MUST say "I cannot do that".

## 4. Memory Drift
- **Error**: Long-term memory accumulating conflicting information over time.
- **Fix**: Implement a "Memory Consolidation" process that periodically summarizes and cleans the Vector DB.

