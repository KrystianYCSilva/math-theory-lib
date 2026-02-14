---
description: |
  Tech stack and architecture decisions.
  Use when: you need to understand the technologies, patterns, and architectural choices.
---

# Tech Stack & Architecture

## Stack

| Camada | Tecnologia | Versao |
|--------|------------|--------|
| Linguagem | Kotlin Multiplatform | 2.x |
| Framework | KMP Standard Lib | - |
| Build | Gradle KTS | 8.x+ |
| Testes | Kotest | 5.x |
| Analise | Detekt | - |

## Arquitetura

Arquitetura em 4 camadas rigorosas (Layered Architecture):
1. **Kernel** (Primitivos computacionais)
2. **Logic** (Especificações lógicas)
3. **Set Theory** (Implementação ZFC)
4. **Construction** (Derivação matemática)

Dependências apenas para baixo (Construction -> Set -> Logic -> Kernel).

## Estrutura de diretorios

```
math-theory-lib/
├── kernel/
├── logic/
├── set/
├── relation/
├── function/
├── construction/
├── ordinal/
├── cardinal/
└── examples/
```

## Dependencias criticas

- Kotlin Standard Library
- Kotest (Property-based testing)
