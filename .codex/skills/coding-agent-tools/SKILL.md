---
name: coding-agent-tools
description: |
  Guide to AI-assisted development ("Coding Vibe"): tool selection, IDE integration, and workflows for maximizing developer productivity with AI agents.
  Use when: setting up AI-assisted development environments, selecting coding tools, or integrating AI into CI/CD workflows.
---

# Coding Agent Tools & Workflows

This skill guides the setup and usage of the modern "AI Pair Programmer" stack.

## How to select AI Coding Tools

Choose tools based on interaction depth.
> See [CLI Guide](references/cli-guide.md) for detailed comparisons.

| Tool | Type | Best Use Case |
|------|------|---------------|
| **Claude Code / Aider** | CLI Agent | Full-file refactoring, multi-file edits, "Apply this feature". |
| **GitHub Copilot** | IDE Ghostwriter | Autocomplete, boilerplate generation, quick unit tests. |
| **Cursor** | AI-Native IDE | "Chat with codebase", semantic search, diff review. |
| **OpenCode** | Open Interpreter | Local LLM execution, privacy-focused tasks. |

## How to workflow with AI Agents (The "Coding Vibe")

1.  **Spec-First**: Don't write code. Write a Markdown spec (`spec.md`) and ask the agent to "Implement this spec".
2.  **TDD Loop**:
    -   Ask agent: "Generate tests for feature X based on spec."
    -   Run tests (Fail).
    -   Ask agent: "Write implementation to pass tests."
    -   Run tests (Pass).
3.  **Reviewer Mode**: Before committing, pipe `git diff` to the agent: "Review this diff for security issues and potential bugs."

## How to integrate AI into CI/CD

-   **PR Reviews**: Use actions (like Codium or custom scripts) to auto-review PRs.
-   **Commit Messages**: Use `git diff | llm "generate conventional commit"` for standardized history.
-   **Doc Generation**: Auto-generate API docs from code signatures on merge.

## References

-   [CLI Guide & Documentation](references/cli-guide.md)
-   [Modern AI Coding Stack](references/modern-stack.md)
-   [CLI & Tool Peculiarities](references/cli-nuances.md)
-   [Warnings & Pitfalls](references/warnings.md)
