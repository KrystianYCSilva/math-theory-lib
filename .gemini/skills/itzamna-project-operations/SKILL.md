---
name: itzamna-project-operations
description: |
  Procedures and safe automation patterns for Itzamna in this repository.
  Use when: an automated agent needs to inspect, append, or propose changes to
  project-level agent artifacts (`.itzamna/`, `.context/`, CLI-generated files).
tags: [itzamna, operations, automation, governance]
---

# Skill: Itzamna Project Operations

Purpose: provide an agent-safe, human-gated workflow for initializing, validating,
and minimally updating Itzamna-related project metadata and CLI files.

Who benefits: automation engineers, Copilot/Cli agents, maintainers coordinating
agent workflows in this repository.

Use when: an agent needs to verify Itzamna installation, sync CLI files with
Itzamna protocol, or propose initial content for `.context/` and `.itzamna/memory.md`.

## Core ideas (distilled)

- Treat `.itzamna/` and `.context/` as authoritative agent configuration and short-term
  memory; they must not be modified without explicit human approval (Human Gate).
- Do not overwrite CLI-generated files (AGENTS.md, CLAUDE.md, GEMINI.md, `.cursorrules`).
  Non-destructive appends referencing Itzamna are allowed when the human approves.
- When proposing content for `.context/*` or `.itzamna/memory.md`, always present a
  draft and wait for approval before writing.

---

## Instructions (step-by-step)

1. Verify installation (read-only):
   - Confirm presence of these files under `.itzamna/`:
     - `kernel.md`, `constitution.md`, `memory.md`, and `templates/` with `skill-template.md`,
       `agent-template.md`, `quality-checklist.md`, `cli-compatibility.md`.
   - Confirm `.context/` contains `project.md`, `tech.md`, `rules.md`.
   - Confirm `.specify/` and `./.gemini/skills/` if present (spec-kit and gemini skills).

2. Report findings to the human with concise status (OK | MISSING | NEEDS ACTION).

3. CLI file sync policy:
   - If a CLI file exists (e.g., `GEMINI.md`, `AGENTS.md`, `CLAUDE.md`, `.cursorrules`) and
     it does not reference Itzamna, ASK the human whether to append a short Itzamna block.
   - If human approves, append the Itzamna reference at the END of the file (do NOT overwrite).
   - If human rejects or does not respond, do nothing.

4. Proposing `.context/` content:
   - If `.context/*` contains placeholders or templates, produce a proposed draft by analyzing
     README, docs, build files, and module layout. Present the draft to the human and await approval.
   - On approval, write each file separately and show the diff/preview before committing.

5. Proposing `.itzamna/memory.md` content:
   - If `.itzamna/memory.md` appears empty or templated, build a short-term memory proposal
     (project name, stack, current status, next steps). Present for approval.
   - On approval, append or replace only the templated sections (do not erase unrelated notes).

6. `MEMORY.md` (root) checks:
   - If `MEMORY.md` is missing, propose creating it (long-term memory); present template and request approval.
   - If present, do not modify without explicit instructions.

7. Final report:
   - Produce a single concise report summarizing the status of `.itzamna/`, `.context/`, `MEMORY.md`,
     and CLI files and listing the actions taken and edits proposed.

---

## Examples (agent dialog style)

- Detect CLI file without Itzamna reference:
  - Agent: "Detectei GEMINI.md sem referencia ao Itzamna. Posso adicionar um bloco de integracao Itzamna ao final do arquivo?"
  - Human: "Sim"
  - Agent: Append Itzamna block; report file path and diff.

- Propose `.context/project.md` content:
  - Agent: ".context/project.md parece conter placeholders. Posso analisar o repo e propor um preenchimento?"
  - Human: "Sim"
  - Agent: Presents draft (shows diffs); Human approves; Agent writes file and reports.

---

## Quality checklist (auto-crítica guidance)

- [ ] Read-only checks first: always report findings before proposing writes.
- [ ] Human approval recorded in the action log before any write occurs.
- [ ] Never overwrite CLI-generated files; only append with consent.
- [ ] Proposals include diffs and clear rationale.
- [ ] All edits are minimal and localized.
- [ ] References to `.itzamna/` docs and templates are included in proposals.

---

## References

- Project Itzamna templates: `.itzamna/templates/skill-template.md`, `.itzamna/templates/quality-checklist.md`
- Itzamna memory: `.itzamna/memory.md`
- Project agent docs and skills: `.gemini/skills/`
- Project architecture: `docs/ARCHITECTURE.md`


---

End of skill: present this SKILL.md to the human and await approval to persist into other CLI-specific directories as needed.