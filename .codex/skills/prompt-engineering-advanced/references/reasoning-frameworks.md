---
name: reasoning-frameworks
description: |
  Documentation file for agent operations and skill usage.
  Use when: you need procedural guidance for agent execution and context management.
---

# Reasoning Frameworks Deep Dive

## Chain-of-Thought (CoT)
Explicitly asking the model to show its work.

### Example
**Prompt**:
"Solve this logic puzzle. Let's think step by step."

**Output**:
1. First, I identify the constraints: A must be before B.
2. Second, I look at the options...
3. Therefore, the answer is C.

## Tree-of-Thought (ToT)
Exploring multiple possibilities before deciding.

### Example
**Prompt**:
"Propose 3 distinct architectural patterns for this problem.
For each, list Pros and Cons.
Then, evaluate which is best for a high-traffic startup."

**Output**:
- Path A: Microservices...
- Path B: Monolith...
- Path C: Serverless...
- Evaluation: Path C is best because...

## ReAct (Reason + Act)
Interleaving thought with tool use.

### Example
**Trace**:
- **Thought**: User wants weather in Tokyo. I need to check the weather tool.
- **Action**: `weather_tool("Tokyo")`
- **Observation**: "18°C, Cloudy"
- **Thought**: I have the data. I will answer the user.
- **Answer**: "It is currently 18°C and cloudy in Tokyo."

