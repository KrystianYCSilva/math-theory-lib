---
name: prompt-engineering-advanced
description: |
  Advanced prompt engineering strategies including Chain-of-Thought, Tree-of-Thought, and structural frameworks (RICE, CRISPE) for optimal LLM performance.
  Use when: crafting complex prompts, optimizing token usage, or implementing advanced reasoning chains.
---

# Advanced Prompt Engineering

Move beyond "chatting" to engineering deterministic outputs using structured frameworks and reasoning strategies.

## How to apply Reasoning Frameworks

Force the model to "think" before answering.
> See [Techniques Catalog](references/techniques-catalog.md) for a full list.

-   **Chain-of-Thought (CoT)**: Append "Let's think step by step" or provide few-shot examples of reasoning traces.
-   **Tree-of-Thought (ToT)**: Ask the model to generate 3 possible paths, evaluate them, and pick the best.
-   **Self-Consistency**: Generate $N$ reasoning paths and take the majority vote answer.

## How to structure Complex Prompts

Use established acronyms to ensure completeness.
> See [Prompt Templates](references/prompt-templates.md) for copy-paste blocks.

### RICE (General Purpose)
-   **R**ole: "You are a Senior Java Architect."
-   **I**nstructions: "Refactor this class to use the Builder pattern."
-   **C**ontext: "This is a legacy banking application."
-   **E**xamples: "Here is the expected output format..."

### CRISPE (Detailed Personas)
-   **C**apacity, **R**ole, **I**nsight, **S**tatement, **P**ersonality, **E**xperiment.

## How to optimize Context (Compression)

-   **Delimiters**: Use XML tags (`<code_block>`, `<context>`) to segment input. Models pay more attention to structured data.
-   **Reference Anchoring**: Instead of pasting a 50-page doc, paste the Table of Contents and ask the model which section it needs.
-   **Format shifting**: Convert verbose JSON to minimal YAML or Markdown tables to save tokens.

## References

-   [Prompt Engineering Guide](https://www.promptingguide.ai/)
-   [OpenAI Cookbook](https://github.com/openai/openai-cookbook)
-   [Techniques Catalog](references/techniques-catalog.md)
-   [Prompt Templates](references/prompt-templates.md)
