---
description: "Guidelines and directory for agentic interactions within the project"
use_when: "When an agent needs to understand its role, workflows, and tools available in this repository"
---

# AGENTS.md — mathsets-kt

## Introduction

This file outlines the rules, directory structures, and conventions for AI Agents interacting with the `mathsets-kt` repository. This project is heavily driven by Agentic Workflows, relying on rigorous specifications, tests, and cognitive orchestration.

## Agent Personas & Ecosystem

The repository utilizes multiple agent ecosystems that coordinate work, handle context, and execute implementations:

*   **Opencode:** Interactive CLI agents for coding, refactoring, and exploring the codebase.
*   **Itzamna:** Cognitive orchestrator providing memory, skill extraction, and project status tracking.
*   **SpecKit / Codex:** Specification-driven development guidelines to ensure formal correctness.

### Key Directories for Agents

1.  **`.context/`**: Global project context, coding standards, domain concepts, and architectural rules. Read this first!
2.  **`.codex/` / `.gemini/` / `.opencode/`**: Specific skills and configurations for the respective AI agents. 
3.  **`.itzamna/`**: Contains the active memory (`memory.md`), agent templates, and the kernel router for tasks.
4.  **`docs/`**: Holds the mathematical theory (`DOCUMENTATION.md`), the architecture (`ARCHITECTURE.md`), and the phased implementation plans (`ROADMAP.md`, `EXPANSION_ROADMAP.md`, `EXPANSION_EXECUTION_PLAN.md`).

## Agentic Development Workflow

When implementing new features or fixing bugs in `mathsets-kt`, agents MUST adhere to the following strict loop:

1.  **Understand Phase**
    *   Read the relevant mathematical theory in `docs/DOCUMENTATION.md` or `docs/ARCHITECTURE.md`.
    *   Consult `.itzamna/memory.md` to see the current sprint/phase status.
    *   Check `docs/EXPANSION_EXECUTION_PLAN.md` to understand the dependencies of the target module.
2.  **Plan Phase**
    *   Draft a checklist or technical plan locally or via the `TodoWrite` tool.
    *   Do NOT bypass the "Dual Mode" principle (Kernel vs. Construction).
3.  **Implement Phase**
    *   Write the interfaces first.
    *   Apply rigorous Kotlin types (sealed classes, value classes) to forbid invalid states.
    *   Implement "Property-Based Tests" using Kotest BEFORE or ALONGSIDE the main logic.
4.  **Verify & Update**
    *   Run tests (`./gradlew check` or equivalent).
    *   Update `.itzamna/memory.md` to record the completed steps.
    *   Update `MEMORY.md` if a milestone is achieved.

## Principles for AI Agents

*   **Immutable Everything:** Do not introduce mutable state (`var`, `MutableList`, etc.) without extreme justification. Everything must be immutable to align with ZFC.
*   **Lazy Evaluation:** Favor `Sequence<T>` for potentially infinite structures.
*   **Absolute Paths:** When reading or writing files, construct the absolute paths relative to the project root.
*   **Zero-Overhead:** Use `@JvmInline value class` for kernel primitive wrappers.
*   **Property-Based Thinking:** Write tests that verify universal algebraic properties (e.g., associativity, De Morgan's laws) over hundreds of randomized inputs rather than single edge-cases.

## Checking Project Status

Agents should periodically review the milestones inside `ROADMAP.md` and `EXPANSION_ROADMAP.md` to know what has been delivered. If a sprint is finished, mark it as such in the cognitive memory.