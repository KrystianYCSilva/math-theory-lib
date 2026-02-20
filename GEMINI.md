---
description: "Guia de integração e informações para o Agente Gemini"
use_when: "You need to understand the project's overall context, phases, and agent integrations"
---

# GEMINI.md — mathsets-kt

## Project Overview

**mathsets-kt** is a Kotlin Multiplatform library designed to bridge the gap between computational efficiency and mathematical rigor. It implements Axiomatic Set Theory (ZFC), constructs number systems ($\mathbb{N}, \mathbb{Z}, \mathbb{Q}, \mathbb{R}, \mathbb{C}$) from first principles, and provides a comprehensive mathematical platform (algebra, analysis, category theory, formal verification).

### Core Philosophy
*   **Dual Mode:** Every concept has two implementations:
    1.  **Kernel:** Efficient, computational primitives (e.g., `BigInteger` wrapper).
    2.  **Construction:** Rigorous axiomatic derivation (e.g., Von Neumann ordinals).
*   **Verification:** The library aims to prove (via tests and logic modules) that the Kernel and Construction layers are isomorphic.
*   **Immutability:** All set structures are immutable, adhering to the mathematical definition of a set.

## Architecture

The project is structured into multiple distinct layers (see `docs/ARCHITECTURE.md` and `docs/EXPANSION_ROADMAP.md` for details):

1.  **Layer 0: Kernel** (`kernel/`) - Computational primitives without dependencies.
2.  **Layer 1: Logic** (`logic/`) - First-order logic specifications and axioms.
3.  **Layer 2: Set Theory** (`set/`) - `MathSet<T>`, relations, functions.
4.  **Layer 3: Construction** (`construction/`) - Derivation of numbers from sets.
5.  **Layers 4+: Expansions** (`algebra/`, `analysis/`, `category/`, `solver/`, etc.) - Higher-level mathematical structures, solvers, and bridges to proof assistants (Z3, Lean, Isabelle).

## Technical Stack

*   **Language:** Kotlin (Multiplatform: JVM, JS, Native)
*   **Build System:** Gradle KTS
*   **Testing:** Kotest (focus on Property-Based Testing)
*   **Static Analysis:** Detekt

## Development Status

**Current Phase:** Implementation Ready (1.0 & 2.0 specs complete).

*   Source code (`src`) directories are structured, but major implementations are pending.
*   The `docs/` folder contains comprehensive execution plans, roadmaps, and architectural guidelines.
*   Agentic frameworks (`.itzamna`, `.specify`, `.codex`) are fully configured to guide the multi-phase implementation (Fases A-G).

## Agentic Workflow

This project uses specialized agent configurations:
*   **SpecKit / Codex:** For defining specifications and plans.
*   **Itzamna:** For agent personas, memory, and skills.
*   **Opencode:** Interactive CLI agents.

See `AGENTS.md` for details on how agents should interact with this repository.

## Getting Started (Future)

*   **Build:** `coming soon` (e.g., `./gradlew build`)
*   **Test:** `coming soon` (e.g., `./gradlew check`)

---

## Itzamna Protocol

Este projeto utiliza o [Itzamna](https://github.com/KrystianYCSilva/itzamna) como orquestrador cognitivo.

**Antes de executar qualquer tarefa não-trivial:**
1. Leia `.itzamna/kernel.md` (classificacao e roteamento)
2. Leia `.itzamna/memory.md` (estado atual do projeto)
3. Leia `.context/` (project.md, tech.md, rules.md)

**Ao finalizar tarefas significativas:**
- Atualize `.itzamna/memory.md` (estado atual, append-only)
- Para features/releases/hotfixes: proponha update em `MEMORY.md` (root, long-term)

Regras completas: `.itzamna/constitution.md`
Slash commands: veja `commands/` neste diretorio
