# mathsets-kt (math-theory-lib)

## Project Overview

**mathsets-kt** is a Kotlin Multiplatform library designed to bridge the gap between computational efficiency and mathematical rigor. It implements Axiomatic Set Theory (ZFC) and constructs number systems ($\mathbb{N}, \mathbb{Z}, \mathbb{Q}$) from first principles.

### Core Philosophy
*   **Dual Mode:** Every concept has two implementations:
    1.  **Kernel:** Efficient, computational primitives (e.g., `BigInteger` wrapper).
    2.  **Construction:** Rigorous axiomatic derivation (e.g., Von Neumann ordinals).
*   **Verification:** The library aims to prove (via tests and logic modules) that the Kernel and Construction layers are isomorphic.
*   **Immutability:** All set structures are immutable, adhering to the mathematical definition of a set.

## Architecture

The project is structured into four distinct layers (see `docs/ARCHITECTURE.md` for details):

1.  **Layer 0: Kernel** (`kernel/`) - Computational primitives without dependencies.
2.  **Layer 1: Logic** (`logic/`) - First-order logic specifications and axioms.
3.  **Layer 2: Set Theory** (`set/`) - `MathSet<T>`, relations, functions.
4.  **Layer 3: Construction** (`construction/`) - Derivation of numbers from sets.

## Technical Stack

*   **Language:** Kotlin (Multiplatform)
*   **Build System:** Gradle KTS
*   **Testing:** Kotest (focus on Property-Based Testing)
*   **Static Analysis:** Detekt

## Development Status

**Current Phase:** Planning & Specification (Inception).

*   Source code (`src`) is currently **NOT** implemented.
*   Focus is on architectural design (`docs/ARCHITECTURE.md`) and implementation planning.
*   Agentic frameworks (`.itzamna`, `.specify`) are configured to guide the implementation.

## Agentic Workflow

This project uses specialized agent configurations:
*   **SpecKit:** For defining specifications and plans.
*   **Itzamna:** For agent personas and skills.

## Getting Started (Future)

*   **Build:** `coming soon` (e.g., `./gradlew build`)
*   **Test:** `coming soon` (e.g., `./gradlew check`)

---

## Itzamna Protocol

Este projeto utiliza o [Itzamna](https://github.com/KrystianYCSilva/itzamna) como orquestrador cognitivo.

**Antes de executar qualquer tarefa nao-trivial:**
1. Leia `.itzamna/kernel.md` (classificacao e roteamento)
2. Leia `.itzamna/memory.md` (estado atual do projeto)
3. Leia `.context/` (project.md, tech.md, rules.md)

**Ao finalizar tarefas significativas:**
- Atualize `.itzamna/memory.md` (estado atual, append-only)
- Para features/releases/hotfixes: proponha update em `MEMORY.md` (root, long-term)

Regras completas: `.itzamna/constitution.md`
Slash commands: veja `commands/` neste diretorio


---

## Itzamna notice added by automation

Detectei que este arquivo foi gerado pelo CLI "Gemini CLI". Adicionando uma referencia curta ao protocolo Itzamna para integrar agentes.

**Antes de executar qualquer tarefa nao-trivial:**
1. Leia `.itzamna/kernel.md` (classificacao e roteamento)
2. Leia `.itzamna/memory.md` (estado atual do projeto)
3. Leia `.context/` (project.md, tech.md, rules.md)

**Ao finalizar tarefas significativas:**
- Atualize `.itzamna/memory.md` (estado atual, append-only)
- Para features/releases/hotfixes: proponha update em `MEMORY.md` (root, long-term)

Regras completas: `.itzamna/constitution.md`
Slash commands: veja `commands/` neste diretorio
