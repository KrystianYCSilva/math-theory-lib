# ARCHITECTURE.md — `mathsets-kt`

## Arquitetura da Biblioteca Kotlin para Teoria dos Conjuntos e Teoria dos Números

**Versão:** 1.0.0-draft  
**Última atualização:** 2026-02-14  
**Autores:** [Seu nome]  
**Licença:** [A definir]

---

## 1. Visão Geral

`mathsets-kt` é uma biblioteca Kotlin Multiplatform que implementa computacionalmente
a Teoria Axiomática dos Conjuntos (ZFC) e a construção dos conjuntos numéricos
(ℕ, ℤ, ℚ), combinando rigor matemático com engenharia de software moderna.

A biblioteca traduz conceitos da lógica matemática — axiomas, relações, funções,
ordinalidade, cardinalidade — em estruturas de dados e algoritmos verificáveis,
servindo simultaneamente como ferramenta computacional e como laboratório didático
de fundamentos da matemática.

### 1.1 Objetivos

1. **Fidelidade Axiomática** — Cada estrutura e operação mapeia diretamente para
   um axioma, definição ou teorema da Teoria dos Conjuntos, com referência explícita
   à literatura.

2. **Correção por Construção** — O sistema de tipos de Kotlin (sealed types, generics,
   variance) deve rejeitar em tempo de compilação construções matematicamente inválidas
   sempre que possível.

3. **Dual Mode** — Cada conceito possui uma implementação *computacional eficiente*
   (kernel) e uma *construção axiomática formal* (construction), com isomorfismo
   verificável entre ambas.

4. **Avaliação Lazy** — Conjuntos definidos por compreensão (intensionais) e conjuntos
   infinitos utilizam `Sequence<T>` para geração sob demanda, sem materialização
   desnecessária.

5. **Testabilidade como Teorema** — Testes unitários verificam propriedades algébricas
   (comutatividade, De Morgan, Peano) via property-based testing.

---

## 2. O Problema do Bootstrap Circular

### 2.1 A Circularidade Fundamental

A Teoria dos Conjuntos e a Teoria dos Números possuem uma dependência circular
que é intrínseca à própria matemática:

- **Conjuntos precisam de números** para funcionar computacionalmente: geradores de
  sequências, predicados sobre domínios numéricos, cardinalidade.
- **Números precisam de conjuntos** para serem construídos axiomaticamente: a construção
  de von Neumann define 0 = ∅, S(n) = n ∪ {n}; ℤ e ℚ são quocientes de produtos
  cartesianos por relações de equivalência.

Esta circularidade é a mesma que Zermelo, Fraenkel, Peano e Dedekind enfrentaram
historicamente. A solução matemática é o uso de *níveis de fundamentação*:
primeiro assume-se axiomas primitivos, depois constrói-se em camadas.

> **Referência:** Enderton (1977, §1.1) discute explicitamente essa estratificação.
> Halmos (1960, Prefácio) argumenta que a abordagem "ingênua" é suficiente para
> a maioria das construções, com a axiomática servindo como fundamentação posterior.

### 2.2 A Solução Arquitetural: Quatro Camadas

A circularidade é resolvida por uma separação rigorosa entre *primitivos computacionais*
(necessários para a máquina funcionar) e *construções axiomáticas* (demonstrações de
que os conceitos podem ser derivados de conjuntos):

```
╔════════════════════════════════════════════════════════════╗
║  CAMADA 0 — KERNEL                                        ║
║  Primitivos computacionais. Sem dependências.              ║
║  Fornece os tipos numéricos e predicados que a máquina     ║
║  precisa para operar. NÃO tem pretensão axiomática.        ║
╠════════════════════════════════════════════════════════════╣
║  CAMADA 1 — LOGIC + AXIOMS                                ║
║  Lógica de primeira ordem, AST de fórmulas, PeanoAxioms.   ║
║  Define ESPECIFICAÇÕES (o que é um sistema de Peano,        ║
║  o que são axiomas de ZFC). Depende apenas do kernel.       ║
╠════════════════════════════════════════════════════════════╣
║  CAMADA 2 — SET THEORY                                     ║
║  MathSet<T> com dual mode extensional/intensional.          ║
║  Relações, funções, ordens. Usa kernel para tipos           ║
║  numéricos e logic para verificação de axiomas.             ║
╠════════════════════════════════════════════════════════════╣
║  CAMADA 3 — CONSTRUCTIONS + ADVANCED                       ║
║  Construção axiomática de ℕ, ℤ, ℚ a partir de conjuntos.  ║
║  Ordinais, cardinais, teoria descritiva, combinatória.      ║
║  PROVA que kernel ≅ construção (isomorfismo verificável).   ║
╚════════════════════════════════════════════════════════════╝
```

> **Analogia com a matemática:** O kernel é o que um matemático faz no dia-a-dia
> (usa números sem pensar em ∅). A Camada 3 é o que um lógico faz (demonstra que
> esses números podem ser fundamentados em conjuntos puros). A Camada 2 fornece
> a linguagem (teoria dos conjuntos) que conecta ambos.

---

## 3. Estrutura de Módulos

### 3.1 Árvore de Diretórios

```
mathsets-kt/
├── kernel/                      ← Camada 0: Primitivos computacionais
│   └── src/
│       ├── commonMain/kotlin/mathsets/kernel/
│       │   ├── NaturalNumber.kt        (value class sobre BigInteger)
│       │   ├── IntegerNumber.kt        (value class sobre BigInteger)
│       │   ├── RationalNumber.kt       (value class sobre par BigInteger)
│       │   ├── Arithmetic.kt           (interface: +, ×, compare)
│       │   ├── Cardinality.kt          (sealed: Finite, Countable, ...)
│       │   ├── Predicate.kt            (typealias + combinadores)
│       │   └── Generator.kt            (geradores de Sequence<T>)
│       └── jvmMain/                    (otimizações JVM-específicas)
│
├── logic/                       ← Camada 1: Lógica e especificações
│   └── src/commonMain/kotlin/mathsets/logic/
│       ├── formula/
│       │   ├── Term.kt                 (variáveis, constantes, aplicações)
│       │   ├── Formula.kt              (sealed: ∀, ∃, ∧, ∨, ¬, →, ∈, =)
│       │   └── FormulaBuilder.kt       (DSL para construir fórmulas)
│       ├── axiom/
│       │   ├── Axiom.kt                (sealed: cada axioma ZFC como tipo)
│       │   ├── AxiomSystem.kt          (ZFC, NBG, KM como coleções)
│       │   └── PeanoAxioms.kt          (interface PeanoSystem<N>)
│       └── model/
│           ├── Interpretation.kt       (universo finito + valoração)
│           └── ModelChecker.kt         (avalia Formula sobre modelo)
│
├── set/                         ← Camada 2: Teoria dos Conjuntos
│   └── src/commonMain/kotlin/mathsets/set/
│       ├── MathSet.kt                  (sealed interface principal)
│       ├── ExtensionalSet.kt           (backed por HashSet imutável)
│       ├── IntensionalSet.kt           (domain + predicate, lazy)
│       ├── EmptySet.kt                 (object singleton)
│       ├── BitMathSet.kt              (backed por BitSet, inteiros [0,N))
│       ├── PowerSet.kt                 (lazy: gera subconjuntos sob demanda)
│       ├── UniversalSets.kt            (Naturals, Integers, Rationals)
│       ├── SetOperations.kt            (union, intersect, minus, ...)
│       ├── SetAlgebra.kt               (De Morgan, distributividade, ...)
│       └── ZFCVerifier.kt              (valida axiomas sobre modelos finitos)
│
├── relation/                    ← Camada 2: Relações e ordens
│   └── src/commonMain/kotlin/mathsets/relation/
│       ├── OrderedPair.kt              (Kuratowski: {{a},{a,b}})
│       ├── CartesianProduct.kt         (A × B lazy)
│       ├── Relation.kt                 (MathSet<OrderedPair<A,B>>)
│       ├── RelationProperties.kt       (reflexiva, simétrica, transitiva, ...)
│       ├── EquivalenceRelation.kt      (+ classes de equivalência)
│       ├── Partition.kt                (bijeção com EquivalenceRelation)
│       └── Order.kt                    (PartialOrder, TotalOrder, WellOrder)
│
├── function/                    ← Camada 2: Funções
│   └── src/commonMain/kotlin/mathsets/function/
│       ├── MathFunction.kt             (relação funcional)
│       ├── Injection.kt
│       ├── Surjection.kt
│       ├── Bijection.kt                (+ inversa)
│       ├── Composition.kt              (g ∘ f)
│       ├── ChoiceFunction.kt           (Axioma da Escolha computacional)
│       └── Equipotence.kt              (|A| = |B| via bijeção)
│
├── construction/                ← Camada 3: Construções axiomáticas
│   └── src/commonMain/kotlin/mathsets/construction/
│       ├── natural/
│       │   ├── VonNeumannNatural.kt    (0=∅, S(n)=n∪{n})
│       │   ├── VonNeumannPeano.kt      (prova que satisfaz PeanoAxioms)
│       │   ├── NaturalArithmetic.kt    (recursão: +, ×, ^)
│       │   └── NaturalOrder.kt         (a ≤ b ↔ ∃c: a+c=b)
│       ├── integer/
│       │   ├── IntegerConstruction.kt  (ℤ = ℕ×ℕ/~)
│       │   ├── IntegerArithmetic.kt    ([(a,b)]+[(c,d)]=[(a+c,b+d)])
│       │   ├── IntegerOrder.kt
│       │   └── NaturalEmbedding.kt     (ℕ ↪ ℤ⁺)
│       ├── rational/
│       │   ├── RationalConstruction.kt (ℚ = ℤ×ℤ*/~)
│       │   ├── RationalArithmetic.kt
│       │   ├── RationalOrder.kt
│       │   ├── IntegerEmbedding.kt     (ℤ ↪ ℚ)
│       │   └── Density.kt             (entre quaisquer 2 racionais, ∃ outro)
│       └── isomorphism/
│           ├── NaturalIsomorphism.kt   (kernel ≅ VonNeumann)
│           ├── IntegerIsomorphism.kt   (kernel ≅ construção)
│           └── RationalIsomorphism.kt  (kernel ≅ construção)
│
├── ordinal/                     ← Camada 3: Ordinais transfinitos
│   └── src/commonMain/kotlin/mathsets/ordinal/
│       ├── Ordinal.kt                  (sealed: Finite, CNF)
│       ├── CantorNormalForm.kt         (representação até ε₀)
│       ├── OrdinalArithmetic.kt        (não comutativa!)
│       └── TransfiniteRecursion.kt     (princípio de definição)
│
├── cardinal/                    ← Camada 3: Cardinalidade
│   └── src/commonMain/kotlin/mathsets/cardinal/
│       ├── CardinalArithmetic.kt       (ℵ₀ + ℵ₀ = ℵ₀, etc.)
│       ├── CantorDiagonal.kt           (demonstração construtiva)
│       ├── Countability.kt             (ℕ↔ℤ, ℕ↔ℚ, ℝ não enumerável)
│       └── ContinuumHypothesis.kt      (módulo didático/expositivo)
│
├── descriptive/                 ← Camada 3: Teoria descritiva
│   └── src/commonMain/kotlin/mathsets/descriptive/
│       ├── FiniteTopology.kt           (abertos, fechados, interior, fecho)
│       ├── BorelHierarchy.kt           (classificador de nível)
│       └── GaleStewartGame.kt          (jogos de determinância)
│
├── combinatorics/               ← Camada 3: Combinatória
│   └── src/commonMain/kotlin/mathsets/combinatorics/
│       ├── Ramsey.kt                   (teorema finitário + busca)
│       ├── PartitionCalculus.kt        (Erdős-Rado, Bell numbers)
│       └── Trees.kt                    (Aronszajn, Suslin finitários)
│
├── forcing/                     ← Camada 3: Forcing (experimental)
│   └── src/commonMain/kotlin/mathsets/forcing/
│       ├── Poset.kt                    (ordens parciais + filtros)
│       ├── GenericFilter.kt            (construção de filtros genéricos)
│       └── ForcingExtension.kt         (M → M[G] em domínio finito)
│
├── examples/                    ← Demonstrações interativas
│   └── src/commonMain/kotlin/mathsets/examples/
│       ├── ParadoxDemos.kt             (Russell, Burali-Forti, Cantor)
│       ├── CantorDiagonalDemo.kt       (diagonal interativa)
│       ├── NumberConstructionDemo.kt   (ℕ → ℤ → ℚ passo a passo)
│       └── ForcingDemo.kt             (independência de CH visual)
│
├── build.gradle.kts             ← KMP config
├── settings.gradle.kts
├── ARCHITECTURE.md              ← Este documento
├── ROADMAP.md
└── DOCUMENTATION.md
```

### 3.2 Grafo de Dependências entre Módulos

```
                        kernel/
                       (Camada 0)
                      ╱         ╲
                     ╱           ╲
               logic/             │
              (Camada 1)          │
                 │  ╲             │
                 │   ╲            │
                 │    set/ ───────┘
                 │   (Camada 2)
                 │   ╱    ╲
                 │  ╱      ╲
            relation/    function/
           (Camada 2)   (Camada 2)
                ╲          ╱
                 ╲        ╱
              construction/
               (Camada 3)
                    │
            ┌───────┼───────┐
            │       │       │
        ordinal/ cardinal/  │
            │       │       │
            └───┬───┘       │
                │           │
          ┌─────┼─────┐     │
          │     │     │     │
    descriptive/│ forcing/  │
          combinatorics/    │
                            │
                      examples/
```

**Regra de ouro:** dependências só apontam para baixo (em direção ao kernel).
Nenhum módulo da Camada N depende de módulos da Camada N+1.

---

## 4. Decisões Arquiteturais

### DA-01: Dual Mode — Extensional vs. Intensional

**Contexto:** Um conjunto matemático pode ser definido por extensão ({1,2,3}) ou por
compreensão/intensão ({x ∈ ℕ | P(x)}). A implementação deve suportar ambos.

**Decisão:** `MathSet<T>` é uma `sealed interface` com duas famílias de implementação:

- `ExtensionalSet<T>` — backed por `Set<T>` imutável. Materializado em memória.
  Pertinência via lookup O(1).
- `IntensionalSet<T>` — definido por `domain: MathSet<T>` + `predicate: (T) -> Boolean`.
  Não materializado. Pertinência via avaliação do predicado. Iteração lazy via
  `domain.elements().filter(predicate)`.

**Consequências:**
- Conjuntos intensionais sobre domínios infinitos são possíveis (ex: pares naturais).
- `powerSet()` retorna `LazyPowerSet` — never materializado de uma vez.
- Operações entre extensionais e intensionais retornam o tipo mais eficiente possível
  (ex: extensional ∩ intensional = extensional filtrado).
- Materialização (`materialize()`) é explícita e lança exceção para domínios infinitos.

**Referência Matemática:** A distinção extensional/intensional corresponde diretamente
ao Axioma da Extensionalidade (identidade por elementos) vs. Axioma da Separação
(formação por predicado). Ver Enderton (1977, §1.3) e Halmos (1960, §1).

### DA-02: Kernel Separado da Construção Axiomática

**Contexto:** Números são necessários para que conjuntos funcionem (geradores, predicados),
mas axiomaticamente os números são construídos a partir de conjuntos.

**Decisão:** Separação em duas camadas:
- `kernel/` — tipos numéricos primitivos (`value class` sobre `BigInteger`).
  Sem pretensão axiomática. Existem para que a máquina funcione.
- `construction/` — construção axiomática de ℕ, ℤ, ℚ a partir de `MathSet`.
  Demonstra que os primitivos podem ser fundamentados em conjuntos.
- `isomorphism/` — verifica computacionalmente que `kernel ≅ construction`.

**Consequências:**
- O `set/` module pode usar `NaturalNumber` do kernel como tipo de elemento sem
  circularidade.
- A construção axiomática vem depois, provando a equivalência.
- Usuários podem escolher: usar o kernel (eficiente) ou a construção (didática).

**Referência Matemática:** Esta separação espelha a prática matemática padrão:
Enderton (1977, §4) constrói ℕ via conjuntos mas observa que "o matemático praticante
pode usar os naturais sem referência à construção conjuntista."

### DA-03: Sealed Types para Universos Fechados

**Contexto:** Vários conceitos da teoria dos conjuntos formam hierarquias fechadas:
tipos de cardinalidade, tipos de fórmulas lógicas, tipos de ordinais.

**Decisão:** Usar `sealed interface`/`sealed class` de Kotlin para modelar essas hierarquias.

**Consequências:**
- O compilador garante exaustividade em expressões `when` — nenhum caso esquecido.
- Novos tipos só podem ser adicionados no mesmo pacote — universo controlado.
- Pattern matching é seguro e eficiente.

**Exemplo:**
```kotlin
sealed interface Cardinality : Comparable<Cardinality> {
    data class Finite(val n: BigInteger) : Cardinality
    object CountablyInfinite : Cardinality     // ℵ₀
    data class Aleph(val index: Ordinal) : Cardinality
    object Unknown : Cardinality                // Para intensionais não decididos
}
```

### DA-04: Imutabilidade Total

**Contexto:** Conjuntos matemáticos são imutáveis por definição.

**Decisão:** Todas as implementações de `MathSet<T>` são imutáveis. Operações retornam
novos conjuntos. Nenhum método muta estado interno.

**Consequências:**
- Thread-safe por construção.
- Composição funcional sem efeitos colaterais.
- `val` everywhere — alinhamento com estilo Kotlin idiomático.

**Referência Matemática:** A imutabilidade é uma propriedade fundamental de conjuntos
em ZFC. O Axioma da Extensionalidade (dois conjuntos com os mesmos elementos são
iguais) implica que a identidade de um conjunto é determinada por seus elementos — logo,
alterá-los mudaria a identidade. Ver Kunen (1980, §I.2).

### DA-05: Property-Based Testing como Verificação de Teoremas

**Contexto:** Propriedades algébricas (comutatividade, associatividade, De Morgan, Peano)
devem ser verificadas sistematicamente.

**Decisão:** Usar Kotest Property-Based Testing para gerar centenas de instâncias
aleatórias e verificar propriedades universais.

**Consequências:**
- Cada teste é a verificação computacional de um teorema.
- Geração automática de contraexemplos se uma propriedade falhar.
- Cobertura muito maior que testes manuais pontuais.

**Exemplo:**
```kotlin
class SetAlgebraProperties : FunSpec({
    test("De Morgan: (A ∪ B)ᶜ = Aᶜ ∩ Bᶜ") {
        forAll(mathSetArb<Int>(), mathSetArb<Int>(), mathSetArb<Int>()) { A, B, U ->
            (A union B).complement(U) == A.complement(U) intersect B.complement(U)
        }
    }
})
```

### DA-06: Lazy Evaluation via Sequence\<T\>

**Contexto:** Conjuntos potência (2ⁿ elementos), produtos cartesianos (|A|×|B|) e
domínios infinitos (ℕ, ℤ, ℚ) podem explodir memória se materializados.

**Decisão:** Operações que geram conjuntos grandes ou infinitos retornam views lazy
baseados em `Sequence<T>` de Kotlin.

**Consequências:**
- `powerSet()` gera subconjuntos sob demanda.
- `Naturals.elements()` é uma sequência infinita.
- `take(n)` permite explorar parcialmente conjuntos grandes.
- Composição de `filter`/`map` sobre lazy sets é zero-cost até consumo.

### DA-07: Value Classes para Tipos Numéricos do Kernel

**Contexto:** `NaturalNumber`, `IntegerNumber`, `RationalNumber` são wrappers sobre
`BigInteger` que precisam ser eficientes.

**Decisão:** Usar `@JvmInline value class` para evitar alocação de objetos no JVM.

**Consequências:**
- Zero overhead em runtime — o JVM trata como o tipo primitivo encapsulado.
- Type safety plena em compile-time.
- Operadores sobrecarregados (`+`, `*`, `in`, `compareTo`) funcionam naturalmente.

---

## 5. Interface Pública Principal — MathSet\<T\>

### 5.1 Contrato

```kotlin
sealed interface MathSet<out T> {
    // ═══════ Pertinência (operação fundamental) ═══════
    operator fun contains(element: @UnsafeVariance T): Boolean
    
    // ═══════ Iteração (lazy, potencialmente infinita) ═══════
    fun elements(): Sequence<T>
    
    // ═══════ Cardinalidade ═══════
    val cardinality: Cardinality
    
    // ═══════ Materialização explícita ═══════
    fun materialize(): ExtensionalSet<T>
    
    // ═══════ Operações ZFC ═══════
    infix fun union(other: MathSet<@UnsafeVariance T>): MathSet<T>
    infix fun intersect(other: MathSet<@UnsafeVariance T>): MathSet<T>
    infix fun minus(other: MathSet<@UnsafeVariance T>): MathSet<T>
    infix fun symmetricDiff(other: MathSet<@UnsafeVariance T>): MathSet<T>
    fun complement(universe: MathSet<@UnsafeVariance T>): MathSet<T>
    
    // ═══════ Relações entre conjuntos ═══════
    infix fun isSubsetOf(other: MathSet<@UnsafeVariance T>): Boolean
    infix fun isProperSubsetOf(other: MathSet<@UnsafeVariance T>): Boolean
    infix fun isDisjointWith(other: MathSet<@UnsafeVariance T>): Boolean
    
    // ═══════ Construções ZFC ═══════
    fun powerSet(): MathSet<MathSet<T>>
    fun filter(predicate: (T) -> Boolean): MathSet<T>        // Separação
    fun <R> map(f: (T) -> R): MathSet<R>                     // Substituição
    fun <B> cartesianProduct(other: MathSet<B>): MathSet<OrderedPair<T, B>>
    
    // ═══════ Factory methods ═══════
    companion object {
        fun <T> empty(): MathSet<T>
        fun <T> of(vararg elements: T): MathSet<T>
        fun <T> pair(a: T, b: T): MathSet<T>
        fun <T> singleton(element: T): MathSet<T>
    }
}
```

### 5.2 Estratégia de Dispatch em Operações Binárias

Quando dois `MathSet<T>` interagem (ex: `A union B`), o tipo de retorno é decidido
pela combinação dos tipos de entrada:

```
              │ ExtensionalSet │ IntensionalSet │ BitMathSet
──────────────┼────────────────┼────────────────┼───────────
ExtensionalSet│  Extensional   │   Extensional* │ BitMathSet
IntensionalSet│  Extensional*  │   Intensional  │ Intensional
BitMathSet    │  BitMathSet    │   Intensional  │ BitMathSet

* Extensional materializado a partir do intensional filtrado
```

Para `union`: se ambos extensionais, merge dos HashSets. Se algum é intensional,
retorna view lazy com `contains` delegando a ambos.

Para `intersect`: se algum é extensional, itera sobre o menor e testa no maior
(mesmo que maior seja intensional — avalia predicado). Se ambos intensionais,
retorna novo intensional composto.

---

## 6. Convenções de Projeto

### 6.1 Nomenclatura

| Conceito Matemático | Nome Kotlin          | Razão da escolha                         |
|---------------------|----------------------|------------------------------------------|
| Conjunto            | `MathSet<T>`         | Evita conflito com `kotlin.collections.Set` |
| Função              | `MathFunction<A,B>`  | Evita conflito com `kotlin.Function`     |
| Natural (kernel)    | `NaturalNumber`      | Distingue do `Natural` construído        |
| Natural (construção)| `VonNeumannNatural`  | Explicita a construção utilizada         |
| Inteiro (kernel)    | `IntegerNumber`      | Evita conflito com `Int`/`Integer`       |
| Racional (kernel)   | `RationalNumber`     | Evita conflito com libs existentes       |
| Par Ordenado        | `OrderedPair<A,B>`   | Explicita a ordenação (vs. `Pair`)       |

### 6.2 Packages

Todas as classes residem sob o package root `mathsets.*`:
- `mathsets.kernel` — primitivos computacionais
- `mathsets.logic` — lógica de primeira ordem
- `mathsets.set` — teoria dos conjuntos
- `mathsets.relation` — relações e ordens
- `mathsets.function` — funções e bijeções
- `mathsets.construction` — construções axiomáticas
- `mathsets.ordinal` — ordinais transfinitos
- `mathsets.cardinal` — cardinalidade
- `mathsets.descriptive` — teoria descritiva
- `mathsets.combinatorics` — combinatória
- `mathsets.forcing` — forcing (experimental)

### 6.3 DSL Fluente

```kotlin
// Construção extensional
val A = mathSetOf(1, 2, 3, 4, 5)

// Construção intensional
val pares = Naturals.filter { it.isEven() }
val primos = Naturals.filter { it.isPrime() }

// Operações com notação infixada
val C = A union B
val D = A intersect B
val E = A minus B

// Pertinência com operator overloading
3 in A          // true
7 in primos     // true (avalia predicado)

// Subconjunto
A isSubsetOf (A union B)   // true

// Produto cartesiano
val pares_ordenados = A cartesianProduct B
```

---

## 7. Stack Tecnológico

| Camada         | Tecnologia              | Justificativa                                |
|----------------|-------------------------|----------------------------------------------|
| Linguagem      | Kotlin 2.x (KMP)       | Type safety, sealed types, operator overload |
| Build          | Gradle KTS + KMP plugin | Multi-target: JVM, Native, JS                |
| Testes         | Kotest 5.x              | Property-based testing + assertions DSL      |
| Benchmark      | kotlinx-benchmark       | Comparação de backends                       |
| Documentação   | Dokka                   | KDoc → HTML/Markdown automático              |
| CI/CD          | GitHub Actions          | Build + test em JVM/Native/JS                |
| Publicação     | Maven Central           | Distribuição padrão para Kotlin/Java         |
| Análise Estática| detekt                 | Regras de estilo e qualidade                 |

---

## 8. Targets de Plataforma

| Target     | Uso principal                           | Limitações                      |
|------------|-----------------------------------------|---------------------------------|
| JVM        | Uso geral, integração com Java/Android  | Nenhuma significativa           |
| Native     | CLI tools, performance-critical modules | Sem `BigInteger` nativo (polyfill) |
| JS/WASM    | Visualizações web, demos interativas    | Sem `BigInteger` nativo (polyfill) |

Código `expect`/`actual` para tipos que diferem entre plataformas:
- `BigInteger`: JVM usa `java.math.BigInteger`; Native/JS usa lib KMP.
- `BitSet`: JVM usa `java.util.BitSet`; demais usam implementação própria.
