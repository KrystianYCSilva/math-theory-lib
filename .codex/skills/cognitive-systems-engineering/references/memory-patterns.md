---
name: memory-patterns
description: |
  Documentation file for agent operations and skill usage.
  Use when: you need procedural guidance for agent execution and context management.
---

# Memory Patterns & Implementation

## 1. Working Memory (Short-Term)
The active context window.
- **Pattern**: Sliding Window.
- **Implementation**: Keep last $N$ turns (e.g., 10). Discard older ones.
- **Pros**: Simple, low latency.
- **Cons**: Forgets early instructions.

## 2. Episodic Memory (Long-Term)
Retrieval of past events via RAG.
- **Pattern**: Vector Store.
- **Implementation**:
    1. Embed user query.
    2. Search Vector DB (e.g., Pinecone/Chroma).
    3. Retrieve top-k relevant past interactions.
- **Schema**: `timestamp`, `user_id`, `interaction_embedding`, `summary`.

## 3. Semantic Memory (Knowledge Base)
Static facts and rules.
- **Pattern**: Knowledge Graph or Document Store.
- **Implementation**: `.context/` files loaded Just-In-Time.
- **Use Case**: "What is the refund policy?" -> Load `refund-policy.md`.

## 4. Procedural Memory (Skills)
Tool definitions and "How-to" knowledge.
- **Pattern**: Agent Skills (like this file).
- **Implementation**: `SKILL.md` files defining workflows.

