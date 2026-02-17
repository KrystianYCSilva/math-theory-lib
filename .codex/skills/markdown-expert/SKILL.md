---
name: markdown-expert
description: |
  Comprehensive guide for creating documentation, technical reports, and diagrams using Markdown.
  Use when: writing documentation, creating READMEs, generating diagrams (Mermaid), or formatting technical text.
---

# Markdown Expert

Markdown is a lightweight markup language for creating formatted text using a plain-text editor. This skill covers standard syntax, GitHub Flavored Markdown (GFM), and advanced diagramming tools.

## How to write Effective Documentation

Structure matters more than syntax.

- **Headers**: Use H1 (`#`) for titles, H2 (`##`) for sections. Avoid skipping levels (e.g., H1 -> H3).
- **Lists**: Use `-` for bullets and `1.` for ordered lists. Nest using indentation (2 or 4 spaces).
- **Code Blocks**: Fenced code blocks (` ```lang `) with syntax highlighting are essential for technical docs.
- **Links**: `[Text](url)` for standard links. Reference links `[Text][ref]` for repeated URLs.

## How to create Diagrams (Mermaid)

Modern Markdown supports diagrams-as-code via Mermaid.js.

- **Flowcharts**: `graph TD; A-->B;`
- **Sequence**: `sequenceDiagram; A->>B: Hello;`
- **Gantt**: `gantt; title Project Plan;`
- **Class**: `classDiagram; class A { +method() }`

## How to format Technical Reports

For high-quality output (PDF/HTML):

- **Tables**: Use GFM tables. Align columns with `:`.
- **Footnotes**: Use `[^1]` for academic/technical references.
- **Frontmatter**: YAML metadata between `---` at the start of the file (for static site generators like Jekyll/Hugo).
- **Alerts**: Use blockquotes `> [!NOTE]` (GitHub specific) for callouts.

## Common Warnings & Pitfalls

### Trailing Spaces
- **Issue**: Standard Markdown requires 2 spaces at the end of a line for a hard break.
- **Fix**: Use a backslash `` at end of line or explicit paragraph breaks (blank lines).

### Indentation Consistency
- **Issue**: Mixing tabs and spaces breaks list nesting.
- **Fix**: Always use spaces (2 or 4). Configure editor to "Insert spaces for tabs".

### HTML Mixing
- **Issue**: Raw HTML is valid but reduces portability (e.g., won't render in PDF or some viewers).
- **Fix**: Stick to Markdown syntax unless absolutely necessary for layout.

## Best Practices (Documentation)

| Component | Recommendation |
|-----------|----------------|
| **README** | Title, Description, Installation, Usage, License. |
| **Changelog** | Keep a chronological log of changes (KeepAChangelog format). |
| **Comments** | Use `<!-- comment -->` to hide text from renderer. |

## Deep Dives

- **Advanced Syntax**: See [SYNTAX.md](references/syntax.md) (Tables, Footnotes, Math).
- **Diagrams Guide**: See [DIAGRAMS.md](references/diagrams.md) (Mermaid, PlantUML).
- **Tools & Converters**: See [TOOLS.md](references/tools.md) (Pandoc, Hugo, Obsidian).

## References

- [CommonMark Spec](https://commonmark.org/)
- [GitHub Flavored Markdown Spec](https://github.github.com/gfm/)
- [Mermaid.js Documentation](https://mermaid.js.org/)
