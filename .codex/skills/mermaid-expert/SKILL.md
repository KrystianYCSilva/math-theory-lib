---
name: mermaid-expert
description: |
  Comprehensive guide for creating complex diagrams, charts, and visualizations using Mermaid.js syntax.
  Use when: generating technical diagrams, flowcharts, sequence diagrams, ER diagrams, or Gantt charts in Markdown.
---

# Mermaid Expert

Mermaid.js allows creating diagrams and visualizations using text and code. This skill covers the syntax for Flowcharts, Sequence Diagrams, Class Diagrams, State Diagrams, Entity Relationship Diagrams (ER), and Gantt charts.

## How to create Flowcharts

Flowcharts are composed of nodes (geometric shapes) and edges (arrows or lines).

- **Orientation**: `graph TD` (Top-Down), `graph LR` (Left-Right).
- **Shapes**: `[Rectangle]`, `(Round)`, `([Stadium])`, `[[Subroutine]]`, `[(Database)]`, `{{Hexagon}}`.
- **Links**: `-->` (Arrow), `---` (Line), `-.->` (Dotted).
- **Labels**: `A-->|Text|B` or `A-- Text -->B`.

## How to create Sequence Diagrams

Sequence diagrams show interaction between participants over time.

- **Participants**: `participant A` or `actor B`.
- **Messages**: `->>` (Solid arrow), `-->>` (Dotted arrow).
- **Activations**: `activate A` / `deactivate A` or `A->>+B: Call` / `B-->>-A: Return`.
- **Notes**: `Note right of A: Text`.
- **Loops/Alt**: `loop LoopName ... end`, `alt Success ... else Failure ... end`.

## How to create Class Diagrams

Model object-oriented structure.

- **Class**: `class BankAccount`.
- **Members**: `+publicAttribute`, `-privateMethod()`.
- **Relationships**: `<|--` (Inheritance), `*--` (Composition), `o--` (Aggregation).

## Common Warnings & Pitfalls

### Syntax Errors
- **Issue**: Missing semicolons or incorrect arrow syntax often breaks rendering silently.
- **Fix**: Use the [Mermaid Live Editor](https://mermaid.live/) to validate syntax before committing.

### Text Escaping
- **Issue**: Special characters in labels (e.g., `[]`, `()`) break parsing.
- **Fix**: Wrap label text in double quotes: `id["Node with (special) chars"]`.

### Direction Conflicts
- **Issue**: Mixing subgraph directions with main graph directions can cause layout issues.
- **Fix**: Keep subgraphs consistent with the main graph orientation (`TB` vs `LR`).

## Best Practices

| Aspect | Recommendation |
|--------|----------------|
| **Ids vs Labels** | Use short IDs (`A`, `B`) and descriptive labels (`A[Start Process]`). |
| **Styling** | Use `classDef` to style multiple nodes consistently (e.g., error nodes red). |
| **Complexity** | Break huge diagrams into smaller subgraphs or separate diagrams. |

## Deep Dives

- **Flowchart Mastery**: See [FLOWCHARTS.md](references/flowcharts.md).
- **Sequence Deep Dive**: See [SEQUENCE.md](references/sequence.md).
- **Advanced Types**: See [ADVANCED.md](references/advanced-diagrams.md) (ER, State, Gantt, GitGraph).

## References

- [Mermaid Official Documentation](https://mermaid.js.org/intro/)
- [Mermaid Live Editor](https://mermaid.live/)
