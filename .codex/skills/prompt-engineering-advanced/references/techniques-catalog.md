---
name: techniques-catalog
description: |
  Documentation file for agent operations and skill usage.
  Use when: you need procedural guidance for agent execution and context management.
---

# Prompt Engineering Techniques Catalog

## Technique Library

1.  **Zero-Shot**: Direct instruction without examples.
    *   *Prompt*: "Translate this to French."
2.  **Few-Shot**: Providing 1-5 examples of input->output.
    *   *Prompt*: "Convert to JSON. Example: A->{A}... Now do B."
3.  **Chain-of-Thought (CoT)**: Asking for intermediate reasoning steps.
    *   *Prompt*: "Let's think step by step."
4.  **Tree-of-Thought (ToT)**: Exploring multiple reasoning branches.
    *   *Prompt*: "Propose 3 solutions, evaluate each, pick the best."
5.  **Self-Consistency**: Generating multiple answers and voting.
    *   *Prompt*: (Run prompt 5 times, aggregate results).
6.  **ReAct (Reason + Act)**: Interleaving thought with external tool use.
7.  **Least-to-Most**: Breaking a complex problem into sub-problems.
    *   *Prompt*: "First, list the sub-questions. Then answer each."
8.  **Generated Knowledge**: Asking model to generate facts before answering.
    *   *Prompt*: "List facts about X. Now use those facts to answer Y."
9.  **Self-Refine**: Iterative improvement.
    *   *Prompt*: "Critique your answer. Now rewrite it."
10. **Role Prompting**: Assigning a persona.
    *   *Prompt*: "Act as a Senior Physicist."

## Strategy Matrix: Reasoning vs. Standard Models

Reasoning Models (e.g., OpenAI o1, DeepSeek R1) operate differently from Standard Models (GPT-4o, Claude 3.5, Gemini 1.5).

| Technique | Standard Models (GPT-4o, Claude 3.5) | Reasoning Models (o1, R1) | Why? |
|-----------|--------------------------------------|---------------------------|------|
| **Zero-Shot** | ⭐ Good for simple tasks | ⭐ Excellent | R-models handle complexity well zero-shot. |
| **Few-Shot** | ⭐⭐⭐ **Essential** | ⭐ Optional | R-models generalize better without examples. |
| **Chain-of-Thought** | ⭐⭐⭐ **Critical** ("Step by step") | ❌ **Avoid** | R-models *already* do CoT internally. Adding it confuses them or wastes tokens. |
| **Tree-of-Thought** | ⭐⭐ Powerful but expensive | ❌ Redundant | Internal search makes external ToT redundant. |
| **Delimiters** | ⭐⭐ Helpful | ⭐⭐⭐ **Critical** | R-models need very clear constraint boundaries. |
| **Role Prompting** | ⭐⭐ Helpful | ⭐ Neutral | R-models focus more on logic than persona. |
| **Meta-Prompting** | ⭐ Neutral | ⭐⭐ Effective | "Analyze the prompt requirements first" works well. |

### Key Takeaway
-   **Standard Models**: You must **guide the process** (How to think).
-   **Reasoning Models**: You must **define the goal** (What to achieve). They figure out the "How".

