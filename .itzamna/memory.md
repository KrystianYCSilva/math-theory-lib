---
description: |
  Project episodic memory: current state, decisions, next steps, session history.
  Use when: starting any Deliberado+ task (MANDATORY read), ending significant sessions (MANDATORY update).
---

# Memory - Estado do Projeto

> Lido pelo agente no inicio de sessoes Deliberado+.
> Atualizado ao final de sessoes significativas.
> Append-only: nunca apague entradas.

---

## Projeto

- **Nome:** mathsets-kt (math-theory-lib)
- **Stack:** Kotlin Multiplatform, Gradle, Kotest
- **Repositorio:** Local

---

## Estado atual

Início da Implementação (Sprint 1: Kernel). Setup do projeto (scaffolding de diretórios, configuração Gradle multi-módulo, Version Catalog) concluído. Plano de implementação SpecKit criado.

---

## Decisoes

| Data | Decisao | Justificativa |
|------|---------|---------------|
| 2026-02-14 | Inicialização Itzamna | Configuração do ambiente de agente |
| 2026-02-14 | Estrutura Multi-módulo | Criação física dos módulos kernel, logic, set, etc. conforme arquitetura |
| 2026-02-14 | Plano SpecKit | Tradução do Roadmap para .specify/plans/001-implementation.md |

---

## Proximos passos

- [ ] **Sprint 1 (Kernel):** Implementar `NaturalNumber` e `IntegerNumber`
- [ ] Implementar testes de propriedades (Kotest) para os tipos numéricos
- [ ] Configurar detekt rules básicas

---

## Sessoes

| # | Data | Nivel | Resumo |
|---|------|-------|--------|
| 1 | 2026-02-14 | Init | Setup inicial do contexto e estrutura do projeto |
| 2 | 2026-02-14 | Setup | Scaffolding de diretórios, Gradle setup, SpecKit Plan creation |

---

*Ultima atualizacao: 2026-02-14.*

---

## Atualizacao de Sessao (2026-02-17)

- **Nivel:** Deliberado
- **Resumo:** Continuidade de implementação orientada ao `docs/ROADMAP.md`, com foco em fechar lacunas dos sprints 1-10 e ampliar cobertura de testes.

### Entregas Realizadas

- **Kernel (Sprint 1-2):**
  - Adicionados `MathElement`, `Predicate` (+ combinadores), `Arithmetic` (+ implementações), `Generator`/`Generators`.
  - `NaturalNumber` ampliado com `minus`, `pow`, `isPrime`.
  - Novos testes: `ArithmeticTest`, `CardinalityTest`, `GeneratorTest`, `PredicateTest` + expansão de `NaturalNumberTest`.
- **Set (Sprint 5-7):**
  - `MathSet` expandido com operações de álgebra (`minus`, `symmetricDiff`, `complement`, subset/disjoint checks), factories `mathSetOf`, propriedade `cardinality`.
  - Materialização segura para infinitos com `InfiniteMaterializationException`.
  - Implementações de suporte: `SetViews`, `UniversalSets` (`Naturals`, `Integers`, `Rationals`), `SetAlgebra`, `ZFCVerifier`, `Paradoxes`.
  - Testes atualizados/novos: `MathSetBasicsTest`, `SetAlgebraPropertiesTest`, `UniversalSetsTest`, `ZFCVerifierTest`.
- **Relation (Sprint 8-9):**
  - `Relation` reestruturada (domain/codomain/graph), `RelationProperties`, `EquivalenceRelation`, `Partition`, `OrderedSet`, `PartialOrder`, `TotalOrder`, `WellOrder`.
  - Testes de propriedades, partições e ordem parcial adicionados em `RelationBasicsTest`.
- **Function (Sprint 10):**
  - Novo módulo `function/` com build KMP e implementação de `MathFunction`, `Injection`, `Surjection`, `Bijection` (com inversa), `ChoiceFunction`, `Equipotence`.
  - Testes de bijeção/inversa, composição, função de escolha e equipotência adicionados.
- **Logic (Sprint 3-4):**
  - Novo módulo `logic/` com AST (`Term`, `Formula`), DSL (`FormulaBuilder`), parser/pretty-printer, `Axiom`/`AxiomSystem`, `PeanoSystem`/`NaturalPeanoSystem`, `Interpretation`/`ModelChecker`.
  - Testes de roundtrip parser/pretty-printer, equivalência DSL/AST, verificações de Peano e avaliação de modelo.

### Validacao Executada

- `:kernel:jvmTest`
- `:set:jvmTest`
- `:relation:jvmTest`
- `:function:jvmTest`
- `:logic:jvmTest`
- `:kernel:compileKotlinJs`
- `:set:compileKotlinJs`
- `:relation:compileKotlinJs`
- `:function:compileKotlinJs`
- `:logic:compileKotlinJs`

### Pendencias Atuais

- Sprints 11-20 (construction, ordinal avançado, cardinal, descriptive, combinatorics, forcing, examples) continuam pendentes.
- `./gradlew check` completo ainda bloqueia por compilação native no ambiente atual (path/toolchain de Kotlin/Native).
