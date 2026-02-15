# Copilot instructions for math-theory-lib (mathsets-kt)

Purpose: concise, actionable guidance so Copilot sessions and AGI agents quickly understand build/test/lint tasks, high-level architecture, and repository-specific conventions.

---

1) Build, test, and lint commands

- Use the Gradle wrapper from the repo root. On Unix/macOS: `./gradlew <task>`; on Windows use the wrapper batch: `gradlew.bat <task>` (or `./gradlew.bat`).

- Full build + checks: `./gradlew build` or `./gradlew check` (Windows: `gradlew.bat build`).

- Run detekt (static analysis): `./gradlew detekt` (available for subprojects via plugin).

- Dokka docs (if configured): `./gradlew dokkaHtml`.

- Run all tests: `./gradlew test` (for JVM target) or `./gradlew check` to run all verification tasks across modules.

- Run a single test class or method (examples):
  - Single test class (JVM): `./gradlew test --tests "com.mathsets.set.SetAlgebraProperties"`
  - Single test method (JVM): `./gradlew test --tests "com.mathsets.set.SetAlgebraProperties.De Morgan: (A ∪ B)ᶜ = Aᶜ ∩ Bᶜ"` (use quoting/escaping as needed).
  - Pattern match: `./gradlew test --tests "*SetAlgebra*"`.

Notes: Kotlin Multiplatform tests may expose separate targets (e.g., `:set:testJvm`, `:kernel:testJvm`). If a task fails to find tests, list tasks with `./gradlew tasks --all` and run the specific platform test task.

---

2) High-level architecture (summary)

- The project is a Kotlin Multiplatform library split into four conceptual layers:
  - Layer 0 — kernel/: primitive computational types (efficient, value classes over BigInteger). No axiomatics.
  - Layer 1 — logic/: first-order logic, formula ASTs, axioms and model checking.
  - Layer 2 — set/, relation/, function/: MathSet<T> (dual-mode extensional/intensional), relations, functions, and core set operations.
  - Layer 3 — construction/, ordinal/, cardinal/, combinatorics/, descriptive/, forcing/: axiom-based constructions (Von Neumann naturals, integers, rationals), ordinals, cardinal arithmetic, and advanced modules.

- Dependency rule: dependencies only point downward towards kernel; no upward references from construction to kernel that would create circular build dependencies.

- Key entry points and docs to consult:
  - docs/ARCHITECTURE.md — authoritative architecture and design decisions.
  - README.md (project root) and MEMORY.md for project context and agent workflow.
  - `.itzamna/` and `.context/` — project agent/Itzamna protocol and memory; read before major automated changes.

---

3) Key conventions and patterns Copilot must respect

- Dual Mode: Every MathSet supports two implementations:
  - ExtensionalSet<T> — materialized immutable backing set.
  - IntensionalSet<T> — domain + predicate, lazy, and not materialized by default.
  Copilot should prefer returning lazy/intensional views for operations that may be infinite or expensive; only materialize explicitly via `materialize()`.

- Naming and packages:
  - Root package prefix: `mathsets.*` (examples: `mathsets.kernel`, `mathsets.set`, `mathsets.construction`).
  - Kernel types use explicit names to avoid primitive confusion (e.g., `NaturalNumber`, `IntegerNumber`, `RationalNumber`, not `Int`/`Long`).
  - Construction implementations are explicitly named (e.g., `VonNeumannNatural`).

- Immutability: All MathSet implementations are immutable; operations return new MathSet instances and must not mutate inputs.

- Sealed types: Use `sealed interface`/`sealed class` for closed hierarchies (Cardinality, Formula, Ordinal). Ensure exhaustive `when` branches or add `else` only when necessary.

- Value classes: Kernel numeric wrappers are `@JvmInline value class`-style (avoid introducing boxing/unnecessary allocations on JVM).

- Lazy evaluation: Prefer `Sequence<T>` for elements() and for powerSet/cartesianProduct implementations — avoid materializing potentially infinite collections.

- Dispatch rules for binary set ops: When combining extensionals and intensionals prefer returning the more efficient representation (see docs/ARCHITECTURE.md "Strategy of dispatch in binary operations"). For union/intersect/minus follow the documented table (extensional+extensional => extensional, extensional+intensional => extensional filtered or intensional view depending on sizes).

- Tests:
  - Property-based testing (Kotest) is used broadly — tests express algebraic theorems. When adding tests follow existing property-based patterns and reuse the provided arbitraries (e.g., `mathSetArb<T>()`).

- Platform specifics:
  - Use `expect/actual` for platform-specific implementations (BigInteger, BitSet, etc.). Avoid assuming JVM-only APIs without adding an `actual` shim or KMP abstraction.

---

4) Files & AI assistant integrations to merge with

- Existing project docs to incorporate when generating responses: `docs/ARCHITECTURE.md`, `README.md`, `MEMORY.md`, `GEMINI.md`.
- Agent/automation notes: `.itzamna/` and `.context/` — these often contain workflow constraints and must be read before sweeping changes.
- If you (Copilot) search for other AI assistant config files, consider these filenames and incorporate their rules if present: `CLAUDE.md`, `AGENTS.md`, `.cursorrules`, `.windsurfrules`, `CONVENTIONS.md`, `AIDER_CONVENTIONS.md`, `.clinerules`.

(Repository currently includes docs/ARCHITECTURE.md, MEMORY.md, GEMINI.md and references `.itzamna` and `.context`.)

---

5) Quick heuristics for automated edits

- Prefer minimal, local edits scoped to a single module; update `.itzamna/memory.md` after significant changes per project protocol.
- Avoid adding platform-specific code that lacks `expect/actual` counterparts; if a JVM-only helper is necessary, add it under `jvmMain` and document the need.
- When adding public API types, update package exports and module-level docs and add a minimal property-based test demonstrating the algebraic law (Kotest).

---

References and reading order for Copilot sessions

1. docs/ARCHITECTURE.md — read fully (authoritative).
2. README.md + MEMORY.md — project status and agent workflow.
3. `.itzamna/*` and `.context/*` — agent policies and memory before performing large-scale automated changes.
4. build.gradle.kts and settings.gradle.kts — to understand module names and tasks.

---

If adjustments are desired (language, more examples of single-test invocations per module, or CI specifics), say which area to expand and Copilot will update this file accordingly.

---

Itzamna initialization checklist (automated agent guidance)

This project uses Itzamna. Follow the checklist below before performing large-scale automated edits.

Phase 1 — Verify .itzamna and .context
- Confirm these paths exist and contain required files:
  - `.itzamna/kernel.md`, `.itzamna/constitution.md`, `.itzamna/memory.md`
  - `.itzamna/templates/` contains: skill-template.md, agent-template.md, quality-checklist.md, cli-compatibility.md
  - `.context/project.md`, `.context/tech.md`, `.context/rules.md`
- If anything is missing, prompt the user to run: `itzamna init`.

Phase 2 — Sync with CLI files
- Detect which CLI file exists (AGENTS.md, CLAUDE.md, GEMINI.md, .cursorrules, etc.).
- If the CLI file exists and does NOT reference Itzamna, ask the user for approval to append a small Itzamna protocol block to the end of that file (do NOT overwrite).
- Do NOT create the CLI file if missing; only the CLI should create that file.

Phase 3 — MEMORY.md checks
- Ensure `MEMORY.md` exists in repo root (long-term memory). If missing, propose creating it (ask user permission first).
- Ensure `.itzamna/memory.md` exists (short-term memory). Keep both: root MEMORY.md = long-term, `.itzamna/memory.md` = short-term.

Phase 4 — Fill `.context/` only with Human Gate
- If `.context/` contains unfilled templates (placeholders), offer to analyze the codebase and propose content for `.context/project.md`, `.context/tech.md`, `.context/rules.md`.
- Show proposed content and ask for approval before writing.

Phase 5 — Fill `.itzamna/memory.md` only with Human Gate
- If `.itzamna/memory.md` is empty or template-like, propose an initial short-term memory draft based on repository analysis and ask for approval before writing.

Phase 6 — Final status report
- Produce a concise report indicating which items are OK, MISSING, or NEEDS SYNC (do not auto-fix beyond adding non-destructive Itzamna references to existing CLI files when approved).

Rules (MANDATORY):
- NEVER overwrite CLI-generated files; only append an Itzamna reference block if user approves.
- NEVER create the CLI's primary file (AGENTS.md, CLAUDE.md, etc.) — the native CLI must create it.
- NEVER modify `.context/` or `.itzamna/memory.md` contents without explicit human approval.
- HUMAN GATE required for all writes related to Itzamna/context/memory.

(End of Itzamna checklist)
