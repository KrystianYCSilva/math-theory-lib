---
description: |
  Project-specific coding rules and standards beyond CONSTITUTION.md.
  Use when: writing or reviewing code for this specific project.
---

# Project Rules & Standards

> Regras especificas DESTE projeto. Regras gerais estao em CONSTITUTION.md.

## Regras do projeto

1. **Dual Mode:** Todo conceito deve ter implementação eficiente (Kernel) e axiomática (Construction).
2. **Imutabilidade:** `MathSet<T>` e derivados devem ser imutáveis.
3. **Lazy Evaluation:** Usar `Sequence<T>` para conjuntos infinitos ou muito grandes.
4. **Dependência Estrita:** Camadas superiores não podem depender de inferiores.

## Padroes de codigo

- **Nomenclatura:** `MathSet` (não `Set`), `NaturalNumber` (kernel), `VonNeumannNatural` (construção).
- **Types:** Usar `sealed interfaces` para domínios fechados (Cardinality, Formula).
- **Value Classes:** Usar para primitivos do kernel (`@JvmInline`).

## Decisoes arquiteturais (ADRs)

| # | Data | Decisao | Status |
|---|------|---------|--------|
| 1 | 2026-02-14 | Separação Kernel/Construction para resolver bootstrap circular | Aceita |
| 2 | 2026-02-14 | MathSet como sealed interface (Extensional vs Intensional) | Aceita |
| 3 | 2026-02-14 | Imutabilidade total das estruturas de conjunto | Aceita |
