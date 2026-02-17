# Repository Guidelines

## Project Structure & Module Organization
This repository is a Kotlin Multiplatform library (`mathsets-kt`) organized by domain modules. Active modules today are `kernel/`, `set/`, and `relation/`; other modules listed in `settings.gradle.kts` are scaffolds for planned layers (`logic`, `construction`, `ordinal`, etc.).

Code follows the standard KMP layout:
- Main code: `<module>/src/commonMain/kotlin/mathsets/...`
- Shared tests: `<module>/src/commonTest/kotlin/mathsets/...`
- Platform code (when needed): `kernel/src/jvmMain`, `kernel/src/jsMain`, `kernel/src/nativeMain`

Architecture and theory notes live in `docs/` (especially `docs/ARCHITECTURE.md`).

## Build, Test, and Development Commands
Use the Gradle wrapper from repo root.

- `./gradlew build` (`gradlew.bat build` on Windows): compile and run full build lifecycle.
- `./gradlew check`: run verification tasks across modules (tests + static checks).
- `./gradlew test`: run JVM test tasks.
- `./gradlew detekt`: run static analysis.
- `./gradlew dokkaHtml`: generate API docs.
- `./gradlew :set:testJvm`: run tests for one module.
- `./gradlew test --tests "*SetAlgebra*"`: run matching test classes/methods.

## Coding Style & Naming Conventions
- Language: Kotlin 2.x with Kotlin Multiplatform.
- Indentation: 4 spaces; keep files formatted consistently with existing module style.
- Packages: lowercase under `mathsets.*` (example: `mathsets.set`).
- Types: `PascalCase`; functions/properties: `camelCase`; tests end with `Test`.
- Prefer immutable APIs and lazy evaluation (`Sequence<T>`) for set operations.
- Keep cross-module dependencies directional (avoid circular references).

## Testing Guidelines
Tests use Kotlin Test + Kotest (`FunSpec`, matchers, and property testing support).

- Place tests under each module’s `commonTest`.
- Name files `*Test.kt` (example: `SetAlgebraPropertiesTest.kt`).
- Add property-style tests for algebraic laws when adding operations.
- No enforced coverage threshold is configured yet; new behavior should include targeted tests.

## Commit & Pull Request Guidelines
Current history uses short, direct subjects (e.g., `Primeiro Commit`), with mixed Portuguese/English. Keep commits focused and descriptive, ideally one logical change per commit.

For PRs, include:
- What changed and why.
- Impacted modules (example: `kernel`, `set`).
- Commands run locally (for example: `./gradlew check`).
- Linked issue/spec when available.

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
Slash commands: veja `.opencode/command/`
