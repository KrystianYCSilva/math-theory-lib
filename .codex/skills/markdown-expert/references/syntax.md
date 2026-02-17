---
name: syntax
description: |
  Documentation file for agent operations and skill usage.
  Use when: you need procedural guidance for agent execution and context management.
---

# Advanced Markdown Syntax

## Tables (GFM)
Colons define alignment.

```markdown
| Left | Center | Right |
|:-----|:------:|------:|
| Cell | Cell   | Cell  |
```

## Task Lists
Interactive checklists in GitHub/GitLab.

```markdown
- [x] Completed task
- [ ] Pending task
```

## Math (LaTeX)
Supported by GitHub, GitLab, and Obsidian.

```markdown
$$
f(x) = \int_{-\infty}^\infty \hat f(\xi)\,e^{2\pi i \xi x} \,d\xi
$$
```

## Footnotes
Supported by GFM and many parsers.

```markdown
Here is a footnote reference.[^1]

[^1]: Here is the footnote.
```

## Alerts (GitHub)
Special blockquote syntax.

```markdown
> [!NOTE]
> Highlights information that users should take into account.

> [!WARNING]
> Critical content demanding immediate user attention.
```

