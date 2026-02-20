---
description: "Roadmap de implementação da fundação do mathsets-kt (Sprints 1-20)"
use_when: "When checking the foundational implementation phases, milestones, and dependencies of the core mathematical library"
---

# ROADMAP.md — `mathsets-kt`

## Roadmap de Implementação

**Versão:** 1.0.0
**Última atualização:** 2026-02-20

---

## 1. Visão do Roadmap

O roadmap está organizado em **4 Fases** correspondentes às 4 Camadas da arquitetura,
totalizando **20 sprints** (estimativa de 2 semanas cada). Cada sprint possui:

- **Entregáveis** — código, testes, documentação.
- **Critério de Aceite** — testes que devem passar (property-based quando possível).
- **Dependências** — módulos que devem estar completos antes.
- **Referência Teórica** — seção do documento teórico que fundamenta o sprint.

---

## 2. Fase 0 — Kernel (Sprints 1–2)

> **Objetivo:** Estabelecer os primitivos computacionais que toda a biblioteca usará.
> Sem dependências externas (exceto `BigInteger`). Esses tipos existem para que a
> máquina funcione — não possuem pretensão axiomática.

### Sprint 1: Tipos Numéricos Primitivos

**Módulo:** `kernel/`

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `NaturalNumber` | `@JvmInline value class` sobre `BigInteger`. Operações: `+`, `×`, `^`, `succ()`, `pred()`, `isZero()`, `isEven()`, `isPrime()`, `compareTo()`. |
| `IntegerNumber` | `@JvmInline value class` sobre `BigInteger`. Operações: `+`, `-`, `×`, `unaryMinus()`, `abs()`, `compareTo()`. |
| `RationalNumber` | `value class` sobre par `(numerator: BigInteger, denominator: BigInteger)`. Normalização automática (gcd). Operações: `+`, `-`, `×`, `÷`, `compareTo()`. |
| `Cardinality` | `sealed interface`: `Finite(n)`, `CountablyInfinite`, `Uncountable`, `Unknown`. `Comparable`. |

**Critério de Aceite:**
- `NaturalNumber.of(0)` é o único natural onde `isZero() == true`.
- Aritmética: `∀a,b: a + b == b + a` (comutatividade — property-based).
- `RationalNumber`: `1/2 + 1/3 == 5/6` (normalizado).
- `RationalNumber(2, 4) == RationalNumber(1, 2)` (normalização por gcd).

### Sprint 2: Predicados, Geradores e Infraestrutura

**Módulo:** `kernel/`

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `Predicate<T>` | `typealias` + combinadores: `and`, `or`, `not`, `implies`. |
| `Generator` | Geradores `Sequence<T>` para ℕ, ℤ, ℚ. `naturals()`, `integers()` (zigzag), `rationals()` (Cantor pairing). |
| `Arithmetic<N>` | Interface genérica de aritmética para uso polimórfico. |
| `MathElement` | Interface marcadora para tipos que podem ser elementos de `MathSet`. |
| Setup KMP | `build.gradle.kts` configurado para JVM + Native + JS. `expect`/`actual` para `BigInteger`. |

**Critério de Aceite:**
- `Generators.naturals().take(100)` produz 0..99 sem erro.
- `Generators.integers().take(10)` produz `[0, 1, -1, 2, -2, 3, -3, 4, -4, 5]`.
- `Generators.rationals()` não gera duplicatas (frações já normalizadas).
- Build passa em JVM e pelo menos um target adicional (Native ou JS).

**Referência Teórica:** DOCUMENTATION.md §1 (Sistemas Numéricos Primitivos).

---

## 3. Fase 1 — Lógica e Especificações (Sprints 3–4)

> **Objetivo:** Definir a infraestrutura lógica (fórmulas, axiomas, verificadores)
> e as especificações formais (PeanoAxioms) que os módulos posteriores implementarão.

### Sprint 3: AST de Fórmulas e Lógica de Primeira Ordem

**Módulo:** `logic/`

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `Term` | `sealed interface`: `Var(name)`, `Const(value)`, `App(function, args)`. |
| `Formula` | `sealed interface`: `Membership(∈)`, `Equals(=)`, `Not(¬)`, `And(∧)`, `Or(∨)`, `Implies(→)`, `Iff(↔)`, `ForAll(∀)`, `Exists(∃)`. |
| `FormulaBuilder` | DSL Kotlin para construir fórmulas legíveis: `forAll("x") { "x" memberOf "A" implies "x" memberOf "B" }`. |
| `FormulaPrettyPrinter` | Serializa fórmulas em notação matemática UTF-8. |

**Critério de Aceite:**
- Parse/pretty-print roundtrip: `print(parse("∀x(x ∈ A → x ∈ B)"))` == input.
- DSL produz AST idêntica à construção manual.

### Sprint 4: Axiomas e PeanoSystem

**Módulo:** `logic/`

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `Axiom` | `sealed interface` com cada axioma de ZFC como tipo: `Extensionality`, `EmptySet`, `Pairing`, `Union`, `PowerSet`, `Infinity`, `Separation`, `Replacement`, `Choice`, `Foundation`. Cada um carrega sua fórmula. |
| `AxiomSystem` | Coleções nomeadas: `ZFC`, `ZF` (sem Choice), `NBG`, `KM`. |
| `PeanoSystem<N>` | Interface com `zero`, `succ()`, `pred()`, `isZero()`, `verifyInjectivity()`, `verifyZeroNotSuccessor()`, `recursion()`. |
| `NaturalPeanoSystem` | Implementação de `PeanoSystem<NaturalNumber>` usando o kernel. |
| `Interpretation` | Modelo: universo finito + relação de pertinência + valoração. |
| `ModelChecker` | Avalia `Formula` sobre `Interpretation`. Suporta quantificadores finitos. |

**Critério de Aceite:**
- `NaturalPeanoSystem.verifyInjectivity()` passa para 1000 pares aleatórios.
- `NaturalPeanoSystem.verifyZeroNotSuccessor()` retorna `true`.
- `ModelChecker` avalia corretamente `∀x(x ∈ A → x ∈ B)` sobre universo {1,2,3} com A={1,2}, B={1,2,3}.

**Referência Teórica:** DOCUMENTATION.md §2 (Lógica de Primeira Ordem), §3 (Axiomas de Peano).

---

## 4. Fase 2 — Teoria dos Conjuntos (Sprints 5–10)

> **Objetivo:** Implementar `MathSet<T>` com dual mode extensional/intensional,
> relações, funções e todas as operações fundamentais.

### Sprint 5: MathSet Core — Extensional

**Módulo:** `set/`

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `MathSet<T>` | `sealed interface` com contrato completo (conforme ARCHITECTURE.md §5). |
| `EmptySet` | `object` singleton. `contains() = false`, `elements() = emptySequence()`. |
| `ExtensionalSet<T>` | Backed por `Set<T>` imutável (Kotlin stdlib). |
| `BitMathSet` | Backed por `BitSet` para universo `[0, N)` de inteiros. |
| `mathSetOf()` | Factory functions: `mathSetOf(1,2,3)`, `mathSetOf(range)`. |
| Operações básicas | `union`, `intersect`, `minus`, `symmetricDiff`, `complement`, `isSubsetOf`, `isProperSubsetOf`, `isDisjointWith`, `equals`. |

**Critério de Aceite (property-based):**
- Comutatividade: `A union B == B union A` ∧ `A intersect B == B intersect A`.
- Associatividade: `(A union B) union C == A union (B union C)`.
- De Morgan: `(A union B).complement(U) == A.complement(U) intersect B.complement(U)`.
- Idempotência: `A union A == A`.
- Identidade: `A union EmptySet == A` ∧ `A intersect U == A`.
- Absorção: `A union (A intersect B) == A`.
- Involução: `A.complement(U).complement(U) == A`.
- Extensionalidade: `(∀x: x in A ↔ x in B) → A == B`.

### Sprint 6: MathSet Core — Intensional + UniversalSets

**Módulo:** `set/`

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `IntensionalSet<T>` | `domain: MathSet<T>` + `predicate: (T) -> Boolean`. `contains()` avalia predicado. `elements()` filtra lazy. `materialize()` falha em domínios infinitos. |
| `Naturals` | `object : MathSet<NaturalNumber>`. `contains() = true`. `elements() = Generators.naturals()`. |
| `Integers` | `object : MathSet<IntegerNumber>`. Zigzag enumeration. |
| `Rationals` | `object : MathSet<RationalNumber>`. Cantor pairing. |
| `filter()` | `MathSet<T>.filter(predicate)` retorna `IntensionalSet` (Axioma da Separação). |
| `map()` | `MathSet<T>.map(f)` retorna `MappedSet` (Axioma da Substituição). |
| Materialização inteligente | Cache lazy: se domínio finito e muitas consultas, materializa transparentemente. |

**Critério de Aceite:**
- `4 in Naturals.filter { it.isEven() }` → `true` (sem materializar ℕ).
- `7 in Naturals.filter { it.isPrime() }` → `true`.
- `Naturals.filter { it.isEven() }.elements().take(5)` → `[0, 2, 4, 6, 8]`.
- `Naturals.materialize()` → `InfiniteMaterializationException`.
- `mathSetOf(1..100).filter { it % 2 == 0 }.materialize()` → OK, retorna extensional.

### Sprint 7: PowerSet + Operações Avançadas

**Módulo:** `set/`

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `PowerSet<T>` | Implementação lazy: gera subconjuntos via bitmask incremental. `contains(S)` verifica `S isSubsetOf original`. |
| `ZFCVerifier` | Dado um `FiniteModel`, verifica quais axiomas de ZFC são satisfeitos. Reporta resultado por axioma. |
| `SetAlgebra` | Verificadores de leis algébricas parametrizados. |
| `Paradoxes` | Demonstrações construtivas de Russell e Cantor. |

**Critério de Aceite:**
- `mathSetOf(1,2,3).powerSet().elements().count()` == 8 (2³).
- `mathSetOf(1,2).powerSet().contains(mathSetOf(1))` → `true`.
- `ZFCVerifier` identifica corretamente violações em modelos construídos para falhar.

### Sprint 8: Relações

**Módulo:** `relation/`

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `OrderedPair<A,B>` | `data class`. `toKuratowski()` retorna `MathSet<MathSet<Any?>>`. |
| `CartesianProduct` | `A.cartesianProduct(B)` → `MathSet<OrderedPair<A,B>>` lazy. |
| `Relation<A,B>` | `domain`, `codomain`, `graph: MathSet<OrderedPair<A,B>>`. |
| `RelationProperties` | Verificadores: `isReflexive()`, `isSymmetric()`, `isTransitive()`, `isAntisymmetric()`, `isIrreflexive()`, `isTrichotomous()`, `isConnex()`. |
| `inverse()` | Relação inversa: `R⁻¹ = {(b,a) \| (a,b) ∈ R}`. |
| `compose()` | Composição: `R ∘ S`. |

**Critério de Aceite:**
- `OrderedPair(1,2) != OrderedPair(2,1)` (ordenação importa).
- Relação "≤" sobre {1,2,3}: `isReflexive()`, `isTransitive()`, `isAntisymmetric()` → `true`.
- Relação "=" sobre {1,2,3}: `isReflexive()`, `isSymmetric()`, `isTransitive()` → `true`.

**Referência Teórica:** DOCUMENTATION.md §5 (Relações).

### Sprint 9: Equivalência, Partições e Ordens

**Módulo:** `relation/`

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `EquivalenceRelation<A>` | Verifica 3 propriedades no construtor. `equivalenceClass(a)`, `quotientSet()`. |
| `Partition<A>` | Família de conjuntos disjuntos cuja união é o todo. Verificação no construtor. |
| Bijeção Equivalência ↔ Partição | `EquivalenceRelation.toPartition()` e `Partition.toEquivalenceRelation()`. |
| `PartialOrder<A>` | Reflexiva + antissimétrica + transitiva. |
| `TotalOrder<A>` | Ordem parcial + linear. |
| `WellOrder<A>` | Total + todo subconjunto não-vazio tem mínimo. |
| `OrderedSet<A>` | `minimum()`, `maximum()`, `minimals()`, `maximals()`, `supremum()`, `infimum()`, `successor()`, `predecessor()`. |

**Critério de Aceite:**
- Partição de {1,2,3,4,5,6} por paridade → `{{1,3,5}, {2,4,6}}`.
- `toEquivalenceRelation().toPartition()` roundtrip == identidade.
- Ordem parcial de divisibilidade sobre {1,2,3,4,6,12}: `minimals() == {1}`, `maximals() == {12}`.

### Sprint 10: Funções

**Módulo:** `function/`

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `MathFunction<A,B>` | Relação funcional: `invoke(a)`, `image()`, `preImage(b)`, `preImage(subset)`. |
| `Injection<A,B>` | Verificação: `f(a₁) = f(a₂) → a₁ = a₂`. |
| `Surjection<A,B>` | Verificação: `∀b ∈ B, ∃a: f(a) = b`. |
| `Bijection<A,B>` | Injeção + surjeção. `inverse(): Bijection<B,A>`. |
| `Composition` | `g.compose(f)` com verificação de domínio/contradomínio. |
| `ChoiceFunction` | Dada família de conjuntos não-vazios, seleciona um elemento de cada. |
| `Equipotence` | `A isEquipotentTo B` via tentativa de bijeção. |

**Critério de Aceite:**
- `f: {1,2,3} → {a,b,c}` bijetora: `f.inverse().invoke(f(1)) == 1`.
- Composição: `(g ∘ f)(x) == g(f(x))` para 100 entradas aleatórias.
- `ChoiceFunction` sobre `{{1,2}, {3,4}, {5,6}}` retorna um elemento de cada.

**Referência Teórica:** DOCUMENTATION.md §5 (Funções), §6 (Equipolência).

---

## 5. Fase 3 — Construções e Módulos Avançados (Sprints 11–20)

### Sprint 11–12: Construção Axiomática de ℕ

**Módulo:** `construction/natural/`

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `VonNeumannNatural` | `sealed interface`: `Zero` (= ∅), `Succ(pred)` (= n ∪ {n}). `toSet()` retorna representação conjuntista. |
| `VonNeumannPeanoSystem` | Implementa `PeanoSystem<VonNeumannNatural>`. |
| `NaturalArithmetic` | Adição, multiplicação, exponenciação definidas por recursão. |
| `NaturalOrder` | `a ≤ b ↔ ∃c: a + c = b`. |
| `NaturalIsomorphism` | Bijeção `NaturalNumber ↔ VonNeumannNatural` preservando aritmética. |

**Critério de Aceite:**
- `VonNeumannNatural.Succ(Succ(Zero)).toSet()` == `{∅, {∅}}`.
- `VonNeumannPeanoSystem` passa todos os verificadores de `PeanoSystem`.
- Isomorfismo: `toKernel(toVonNeumann(n)) == n` ∧ `toVonNeumann(toKernel(vn)) == vn` para n ∈ [0,100].
- Preservação: `toKernel(a_vn + b_vn) == toKernel(a_vn) + toKernel(b_vn)`.

### Sprint 13–14: Construção Axiomática de ℤ e ℚ

**Módulo:** `construction/integer/`, `construction/rational/`

**Entregáveis (ℤ):**

| Componente | Descrição |
|---|---|
| `IntegerConstruction` | ℤ = (ℕ×ℕ)/~ onde `(a,b) ~ (c,d) ↔ a+d = b+c`. Usa `EquivalenceRelation` + `Partition`. |
| `IntegerArithmetic` | `[(a,b)] + [(c,d)] = [(a+c, b+d)]` etc. |
| `IntegerOrder` | `[(a,b)] ≤ [(c,d)] ↔ a+d ≤ b+c`. |
| `NaturalEmbedding` | `n ↦ [(n, 0)]`. Prova que preserva `+` e `×`. |
| `IntegerIsomorphism` | Bijeção `IntegerNumber ↔ ℤ-construído`. |

**Entregáveis (ℚ):**

| Componente | Descrição |
|---|---|
| `RationalConstruction` | ℚ = (ℤ×ℤ*)/~ onde `(a,b) ~ (c,d) ↔ a×d = b×c`. |
| `RationalArithmetic` | `[(a,b)] + [(c,d)] = [(ad+bc, bd)]` etc. |
| `RationalOrder` | Ordem total. |
| `IntegerEmbedding` | `z ↦ [(z, 1)]`. |
| `Density` | `between(a, b) = (a+b)/2`. |
| `RationalIsomorphism` | Bijeção `RationalNumber ↔ ℚ-construído`. |

**Critério de Aceite:**
- Cadeia completa: `NaturalNumber(3).toMathInteger().toMathRational()` funciona.
- Isomorfismos roundtrip para 1000 valores aleatórios.
- `Density.between(1/3, 1/2)` → `5/12`.

### Sprint 15–16: Ordinais Transfinitos

**Módulo:** `ordinal/`

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `Ordinal` | `sealed interface`: `Finite(n: NaturalNumber)`, `CNF(terms: List<CNFTerm>)`. |
| `CantorNormalForm` | `CNFTerm(exponent: Ordinal, coefficient: NaturalNumber)`. Representação até ε₀. Normalização automática. |
| `OrdinalArithmetic` | Adição, multiplicação, exponenciação. **Não comutativa!** |
| `OrdinalComparison` | Comparação e boa-ordem sobre CNF. |
| `TransfiniteRecursion` | `transfiniteRecursion(base, successorCase, limitCase)`. |

**Critério de Aceite:**
- `ω + 1 ≠ 1 + ω` (não comutatividade verificada).
- `ω × 2 == ω + ω`.
- `ω² > ω × n` para todo n finito.

### Sprint 17–18: Cardinalidade

**Módulo:** `cardinal/`

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `CardinalArithmetic` | `ℵ₀ + ℵ₀ = ℵ₀`, `ℵ₀ × ℵ₀ = ℵ₀`, `2^ℵ₀ = 𝔠`. |
| `CantorDiagonal` | Dado S e f: S → P(S), constrói D = {x ∈ S \| x ∉ f(x)} e verifica D ∉ Im(f). |
| `Countability` | Bijeções ℕ↔ℤ, ℕ↔ℚ (construtivas). Diagonal sobre ℝ (construtiva). |
| `ContinuumHypothesis` | Módulo expositivo: explica CH, GCH, independência de ZFC. |

**Critério de Aceite:**
- `CantorDiagonal` produz conjunto D que realmente não está na imagem de f, para 100 funções aleatórias.
- Bijeção ℕ↔ℚ: `toNatural(toRational(n)) == n` para n ∈ [0,10000].

### Sprint 19: Teoria Descritiva + Combinatória

**Módulos:** `descriptive/`, `combinatorics/`

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `FiniteTopology<T>` | Verificação de axiomas topológicos. `interior()`, `closure()`, `boundary()`. |
| `BorelHierarchy` | Classificador de nível (Σ⁰₁, Π⁰₁, Σ⁰₂, ...). |
| `GaleStewartGame` | Jogos de determinância com minimax. |
| `Ramsey` | Teorema finitário: `findMonochromaticClique()`, `searchBounds()`. |
| `PartitionCalculus` | `allPartitions()`, `bellNumber()`, relação de Erdős-Rado. |

### Sprint 20: Forcing (Experimental) + Exemplos

**Módulos:** `forcing/`, `examples/`

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `Poset` | Ordens parciais + densos, antichains, filtros. |
| `GenericFilter` | Construção de filtro genérico sobre posets finitos. |
| `ForcingExtension` | Simulador M → M[G] em domínio finito. |
| `IndependenceDemo` | Modelos finitos onde análogo de CH vale e não vale. |
| `ParadoxDemos` | Russell, Burali-Forti, Cantor — interativos. |
| `NumberConstructionDemo` | ℕ → ℤ → ℚ passo a passo visual. |

---

## 6. Milestones

```
═══════════════════════════════════════════════════════════════
                    MILESTONES — mathsets-kt
═══════════════════════════════════════════════════════════════

M0 "Bootstrap"              Sprint 1–2      Kernel funcional
│   NaturalNumber, IntegerNumber, RationalNumber operacionais.
│   Generators produzem sequências infinitas corretas.
│   Build KMP verde em JVM.
│
M1 "Logic Foundation"       Sprint 3–4      Lógica e Peano
│   AST de fórmulas parseável e pretty-printável.
│   PeanoSystem<NaturalNumber> verificado.
│   ModelChecker funcional sobre universos finitos.
│
M2 "Set Theory Core"        Sprint 5–7      MathSet operacional
│   Extensional + Intensional + BitMathSet.
│   Todas as 9 leis algébricas verificadas via property-based.
│   PowerSet lazy funcional.
│   {x ∈ ℕ | P(x)} funciona sem materializar ℕ.
│
M3 "Relations & Functions"  Sprint 8–10     Relações e Funções
│   Relações com verificação automática de propriedades.
│   Equivalência ↔ Partição bijeção verificada.
│   Bijeções com inversas corretas.
│   ChoiceFunction operacional.
│
M4 "Number Tower"           Sprint 11–14    ℕ → ℤ → ℚ construídos
│   Von Neumann satisfaz Peano (verificado).
│   ℤ como quociente de ℕ×ℕ.
│   ℚ como quociente de ℤ×ℤ*.
│   Isomorfismos kernel ≅ construção verificados.
│   Cadeia ℕ ↪ ℤ ↪ ℚ com embeddings preservando aritmética.
│
M5 "Transfinite"            Sprint 15–18    Ordinais + Cardinais
│   Aritmética ordinal (não comutativa) até ε₀.
│   Diagonal de Cantor construtiva.
│   Enumerabilidade de ℤ, ℚ demonstrada.
│
M6 "Advanced"               Sprint 19–20    Descritiva + Combinatória + Forcing
│   Topologia finita, Ramsey, jogos.
│   Forcing sobre posets finitos.
│   Exemplos interativos completos.
│
M7 "Release 1.0"            Pós-Sprint 20   Publicação
    Documentação Dokka completa.
    Publicação no Maven Central.
    README com quick-start guide.
```

---

## 7. Diagrama de Dependências dos Sprints

```
Sprint 1 ─── Sprint 2                      Fase 0: Kernel
              │
        ┌─────┴─────┐
    Sprint 3    Sprint 4                    Fase 1: Logic
        │           │
        └─────┬─────┘
              │
    Sprint 5 ── Sprint 6 ── Sprint 7       Fase 2: Set Theory
              │
        ┌─────┴─────┐
    Sprint 8    Sprint 9                    Fase 2: Relations
        │           │
        └─────┬─────┘
              │
          Sprint 10                         Fase 2: Functions
              │
        ┌─────┴─────┐
  Sprint 11-12  Sprint 13-14               Fase 3: ℕ, ℤ, ℚ
        │           │
        └─────┬─────┘
              │
        ┌─────┴─────┐
  Sprint 15-16  Sprint 17-18               Fase 3: Ordinal, Cardinal
        │           │
        └─────┬─────┘
              │
        ┌─────┴─────┐
    Sprint 19    Sprint 20                  Fase 3: Avançados
```

---

## 8. Critérios de Qualidade Transversais

Aplicáveis a todos os sprints:

| Critério | Métrica | Ferramenta |
|---|---|---|
| Cobertura de testes | ≥ 90% de branches | Kover (Kotlin coverage) |
| Property-based tests | Toda lei algébrica verificada com ≥ 200 instâncias | Kotest Property |
| Documentação | Toda classe/função pública com KDoc | Dokka + detekt |
| Estilo | Zero warnings do detekt | detekt |
| Performance | Benchmarks para operações O(1) do kernel | kotlinx-benchmark |
| Compatibilidade | Build verde em JVM + pelo menos 1 target adicional | GitHub Actions |
