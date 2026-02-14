---
id: plan-001
title: Roadmap de Implementação - Fase 0 a 3
status: active
created: 2026-02-14
context:
  - docs/ROADMAP.md
  - docs/ARCHITECTURE.md
---

# Implementation Plan

Este plano reflete o `docs/ROADMAP.md` e guia a construção do `mathsets-kt` da fundação (Kernel) até os tópicos avançados.

## Phase 0: Kernel (Bootstrap)

> Primitivos computacionais sem dependências externas.

- [x] **Sprint 1: Tipos Numéricos Primitivos**
  - [x] Implementar `NaturalNumber` (value class sobre BigInteger)
  - [x] Implementar `IntegerNumber` (value class sobre BigInteger)
  - [x] Implementar `RationalNumber` (gcd normalization)
  - [x] Implementar `Cardinality` (sealed interface)
  - [x] Setup de Testes com Kotest Property

- [ ] **Sprint 2: Predicados e Geradores**
  - [ ] Implementar `Predicate<T>` e combinadores
  - [ ] Implementar `Generator` (Sequences para ℕ, ℤ, ℚ)
  - [ ] Definir interface `Arithmetic<N>`
  - [ ] Validar build em JVM, Native e JS

## Phase 1: Logic & Specifications

> Infraestrutura lógica e especificações formais.

- [ ] **Sprint 3: AST e Lógica de Primeira Ordem**
  - [ ] Implementar AST `Formula` (ForAll, Exists, And, Or, etc.)
  - [ ] Implementar `Term` (Var, Const, App)
  - [ ] Criar DSL `FormulaBuilder`
  - [ ] Implementar `FormulaPrettyPrinter`

- [ ] **Sprint 4: Axiomas e Peano**
  - [ ] Definir `Axiom` sealed types (ZFC)
  - [ ] Definir `PeanoSystem<N>` interface
  - [ ] Implementar `NaturalPeanoSystem` (Kernel adapter)
  - [ ] Criar `ModelChecker` para universos finitos

## Phase 2: Set Theory Core

> MathSet<T> com dual mode e relações.

- [ ] **Sprint 5: MathSet Core (Extensional)**
  - [ ] Implementar `MathSet<T>` interface
  - [ ] Implementar `ExtensionalSet<T>` (HashSet backed)
  - [ ] Implementar `BitMathSet`
  - [ ] Implementar operações básicas (union, intersect, minus) com property tests

- [ ] **Sprint 6: Intensional & Universal**
  - [ ] Implementar `IntensionalSet<T>` (lazy filter)
  - [ ] Objetos `Naturals`, `Integers`, `Rationals`
  - [ ] Implementar `map` e `filter` (Axiomas Separação/Substituição)

- [ ] **Sprint 7: PowerSet & Advanced**
  - [ ] Implementar `PowerSet<T>` (lazy)
  - [ ] Implementar `ZFCVerifier`
  - [ ] Demos de Paradoxos

- [ ] **Sprint 8: Relations**
  - [ ] Implementar `OrderedPair` e `CartesianProduct`
  - [ ] Implementar `Relation<A,B>` e verificadores de propriedades

- [ ] **Sprint 9: Order & Partition**
  - [ ] Implementar `EquivalenceRelation` e `Partition`
  - [ ] Implementar `PartialOrder`, `TotalOrder`, `WellOrder`

- [ ] **Sprint 10: Functions**
  - [ ] Implementar `MathFunction`, `Bijection`, `Injection`
  - [ ] Implementar `ChoiceFunction`
  - [ ] Implementar `Equipotence`

## Phase 3: Constructions

> Construção axiomática de ℕ, ℤ, ℚ.

- [ ] **Sprint 11-12: Construção de ℕ** (`VonNeumannNatural`)
- [ ] **Sprint 13-14: Construção de ℤ e ℚ** (Quocientes)
- [ ] **Sprint 15-16: Ordinais Transfinitos** (CNF, Aritmética)
- [ ] **Sprint 17-18: Cardinalidade** (Cantor, Infinitos)
- [ ] **Sprint 19-20: Avançados** (Descritiva, Combinatória, Forcing)
