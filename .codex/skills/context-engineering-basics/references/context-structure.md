---
name: context-structure
description: |
  Documentation file for agent operations and skill usage.
  Use when: you need procedural guidance for agent execution and context management.
---

# Unified Context Structure & Metadata

This guide defines the standard for organizing project context, based on the **Unified Context Structure (RFC)**. Choosing the right structure impacts token usage and agent performance.

---

## Option A: The "Enterprise" (Complete) Structure

Ideal for: Large projects, teams, strict governance (T0-T3), multi-agent systems.

### Directory Layout

```text
.context/
├── README.md                    # Navigation Hub (The "Map")
├── ai-assistant-guide.md        # System Prompt / Protocol
│
├── _meta/                       # T2: Project Identity
│   ├── project-overview.md
│   ├── tech-stack.md
│   └── key-decisions.md         # ADRs
│
├── standards/                   # T1: Normative Rules
│   ├── architectural-rules.md   # T0/T1 Rules
│   ├── code-quality.md
│   └── testing-strategy.md
│
├── patterns/                    # T1: Design Blueprints
│   └── architecture.md
│
├── knowledge/                   # T3: Deep Dives
│   └── domain-concepts.md
│
└── workflows/                   # T2: Operational Guides
    └── deployment.md
```

### Pros & Cons

| Feature | Impact |
|---------|--------|
| **Pros** | **Granularity**: Agents load only what they need (e.g., just "testing").<br>**governance**: Clear separation of Rules (T0) vs. Suggestions (T3).<br>**Scalability**: Supports hundreds of context files organized by domain. |
| **Cons** | **Complexity**: Overkill for simple scripts.<br>**Maintenance**: Requires discipline to keep files updated.<br>**Discovery**: Agents need good routing logic to traverse folders. |

---

## Option B: The "Lean" (Basic) Structure

Ideal for: Prototypes, solo developers, small scripts, rapid iteration.

### Directory Layout

```text
.context/
├── rules.md           # Combined T0/T1 (Rules + Standards)
├── tech.md            # Stack + Architecture
├── project.md         # Overview + Goals
└── memory.md          # Session notes / Decisions
```

### Pros & Cons

| Feature | Impact |
|---------|--------|
| **Pros** | **Simplicity**: Zero friction to setup.<br>**Speed**: Agent reads everything in one pass (if tokens allow).<br>**Low Maintenance**: Fewer files to manage. |
| **Cons** | **Token Waste**: Agent loads "Testing Rules" even when just doing "CSS fixes".<br>**Confusion**: Hard to distinguish "Must do" (T0) from "Nice to have".<br>**bloat**: Files grow too large quickly, degrading retrieval. |

---

## The Critical Role of Metadata

Regardless of structure (Complete or Basic), **Metadata is the API for your Context**.

AI Agents act like search engines. They scan filenames and descriptions to decide what to load (Semantic Routing).

### 1. Filename Strategy
- **Bad**: `doc1.md`, `notes.txt`, `misc.md`
- **Good**: `api-auth-flow.md`, `testing-standards.md`, `database-schema.md`

### 2. Frontmatter Description
Every context file MUST have a description. This is the "snippet" the agent reads before loading the file.

```yaml
---
description: Defines the T0 (Absolute) architectural rules for the project. MUST be followed by all agents.
---
# Architectural Rules
...
```

**Rule of Thumb:** If an agent cannot determine the file's utility from its `filename` + `description`, the file is effectively invisible.

