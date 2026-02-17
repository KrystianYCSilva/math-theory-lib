---
name: prompt-templates
description: |
  Documentation file for agent operations and skill usage.
  Use when: you need procedural guidance for agent execution and context management.
---

# Prompt Templates

Copy and paste these templates to structure your prompts.

## R.I.C.E. Framework (General Tasks)

```text
*** ROLE ***
You are a Senior [Job Title] specializing in [Domain].
Your tone is [Tone: e.g., Professional, Socratic, Concise].

*** CONTEXT ***
I am working on [Project Description].
Current constraints:
- [Constraint 1]
- [Constraint 2]

*** INSTRUCTIONS ***
Please [Action Verb] the following [Input].
Steps to follow:
1. [Step 1]
2. [Step 2]

*** EXAMPLES ***
Input: [Example Input]
Output: [Example Output]
```

## C.R.I.S.P.E. Framework (Persona-Heavy)

```text
*** CAPACITY ***
Act as an expert in [Field].

*** ROLE ***
You are [Name/Role], known for [Characteristic].

*** INSIGHT ***
Context: [Background Info].
The user needs: [Specific Need].

*** STATEMENT ***
Your task is to [Task].

*** PERSONALITY ***
Be [Adjective 1], [Adjective 2], and [Adjective 3].

*** EXPERIMENT ***
(Optional) Reply with multiple options for me to choose.
```

