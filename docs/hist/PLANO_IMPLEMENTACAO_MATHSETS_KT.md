---
description: "Documentação para Plano de Implementação: `mathsets-kt`"
use_when: "When you need information about Plano de Implementação: `mathsets-kt`"
---

# Plano de Implementação: `mathsets-kt`

## Biblioteca Kotlin para Teoria dos Conjuntos e Teoria dos Números

---

## 1. Visão Geral e Filosofia

### 1.1 Objetivo

Construir uma biblioteca Kotlin que traduza os conceitos da Teoria Axiomática dos Conjuntos (ZFC)
e da construção dos conjuntos numéricos (ℕ, ℤ, ℚ, ℝ) em código idiomático, aproveitando o sistema
de tipos de Kotlin como espelho da hierarquia matemática.

### 1.2 Por que Kotlin?

| Recurso Kotlin | Mapeamento Matemático |
|---|---|
| `sealed class` / `sealed interface` | Universo fechado de tipos (análogo a axioma da Extensionalidade) |
| `generics` com variância (`in`/`out`) | Relações de subtipo ↔ subconjunto |
| `operator overloading` | Notação natural: `A union B`, `A intersect B`, `a in S` |
| `inline class` / `value class` | Wrappers zero-cost para ordinais, cardinais |
| `sequence` / `Sequence<T>` | Conjuntos potencialmente infinitos (lazy) |
| Extension functions | Enriquecimento de tipos sem herança |
| `object` / companion | Singletons: conjunto vazio, universo |
| Multiplatform (KMP) | JVM + Native + JS — mesma lib, múltiplos targets |
| Contratos (`contract`) | Pré/pós-condições como "axiomas em runtime" |

### 1.3 Princípios Arquiteturais

1. **Type-Safety como Axioma** — O compilador Kotlin deve rejeitar construções inválidas
   sempre que possível. Ex: uma `Function<A,B>` que não é bijetora não pode ser usada
   onde se espera `Bijection<A,B>`.

2. **Lazy by Default** — Conjuntos potência, produtos cartesianos e enumerações usam
   `Sequence<T>` para evitar explosão de memória.

3. **Immutability** — Conjuntos matemáticos são imutáveis. Toda operação retorna um novo
   `MathSet<T>`, nunca muta o original.

4. **Testável como Teorema** — Cada teste é a verificação computacional de uma propriedade
   algébrica (comutatividade, De Morgan, etc.) via property-based testing (Kotest).

5. **Progressão Didática** — Os módulos seguem a ordem de construção da matemática:
   conjuntos → relações → funções → naturais → inteiros → racionais.

---

## 2. Arquitetura de Módulos

```
mathsets-kt/
├── core/                    ← Módulo 1: MathSet<T> + operações ZFC
│   └── src/
│       ├── commonMain/      ← Código multiplataforma
│       └── jvmMain/         ← Otimizações JVM (BitSet nativo, etc.)
│
├── relation/                ← Módulo 2: Pares, Relações, Ordens
│   └── src/commonMain/
│
├── function/                ← Módulo 3: Funções, Bijeções, Composição
│   └── src/commonMain/
│
├── number-natural/          ← Módulo 4: ℕ (Peano + Aritmética)
│   └── src/commonMain/
│
├── number-integer/          ← Módulo 5: ℤ (Construção via ℕ×ℕ/~)
│   └── src/commonMain/
│
├── number-rational/         ← Módulo 6: ℚ (Construção via ℤ×ℤ/~)
│   └── src/commonMain/
│
├── ordinal/                 ← Módulo 7: Ordinais + Aritmética Ordinal
│   └── src/commonMain/
│
├── cardinal/                ← Módulo 8: Cardinalidade + Diagonal de Cantor
│   └── src/commonMain/
│
├── logic/                   ← Módulo 9: Fórmulas FOL + Verificador de Axiomas
│   └── src/commonMain/
│
├── descriptive/             ← Módulo 10: Topologia Finita + Jogos
│   └── src/commonMain/
│
├── combinatorics/           ← Módulo 11: Ramsey + Partições
│   └── src/commonMain/
│
├── examples/                ← Demonstrações interativas
│   └── src/
│
└── build.gradle.kts         ← Kotlin Multiplatform config
```

---

## 3. Módulo 1 — `core`: MathSet\<T\> e Axiomas ZFC

### 3.1 Hierarquia de Tipos

```
MathSet<T> (sealed interface)
├── EmptySet            → Axioma do Conjunto Vazio
├── FiniteSet<T>        → Backed por HashSet (imutável)
├── BitMathSet          → Backed por BitSet (universo [0,N))
├── LazySet<T>          → Backed por Sequence<T> + predicado
├── SingletonSet<T>     → {x} — otimização
├── PairSet<T>          → {a, b} — Axioma do Par
├── PowerSet<T>         → P(S) — Axioma da Potência (lazy)
├── UnionSet<T>         → A ∪ B — Axioma da União (vista lazy)
├── FilteredSet<T>      → {x ∈ A | P(x)} — Axioma da Separação
└── MappedSet<T,R>      → {f(x) | x ∈ A} — Axioma da Substituição
```

### 3.2 Interface Principal

```kotlin
// Contrato público do MathSet
sealed interface MathSet<out T> {
    operator fun contains(element: @UnsafeVariance T): Boolean
    val cardinality: Cardinality   // Finita ou tipo de infinito
    fun iterator(): Iterator<T>    // Para conjuntos enumeráveis
    
    // Operações ZFC
    infix fun union(other: MathSet<@UnsafeVariance T>): MathSet<T>
    infix fun intersect(other: MathSet<@UnsafeVariance T>): MathSet<T>
    infix fun minus(other: MathSet<@UnsafeVariance T>): MathSet<T>
    infix fun symmetricDiff(other: MathSet<@UnsafeVariance T>): MathSet<T>
    infix fun isSubsetOf(other: MathSet<@UnsafeVariance T>): Boolean
    infix fun isProperSubsetOf(other: MathSet<@UnsafeVariance T>): Boolean
    
    fun powerSet(): MathSet<MathSet<T>>         // P(S)
    fun filter(predicate: (T) -> Boolean): MathSet<T>  // Separação
    fun <R> map(f: (T) -> R): MathSet<R>        // Substituição
    
    companion object {
        fun <T> empty(): MathSet<T>             // ∅
        fun <T> of(vararg elements: T): MathSet<T>
        fun <T> pair(a: T, b: T): MathSet<T>   // {a, b}
    }
}
```

### 3.3 Estratégias de Backend

| Cenário | Backend | Razão |
|---|---|---|
| Conjunto pequeno (<64 elem. inteiros) | `BitMathSet` | Operações bitwise O(1) |
| Conjunto finito genérico | `FiniteSet` (HashSet) | O(1) amortizado |
| Conjunto potência | `PowerSet` (lazy) | 2^n elementos — materializar mata memória |
| Resultado de union/intersect/filter | Views lazy | Avaliação sob demanda, composição gratuita |
| Universo finito grande | `BitMathSet` | Complemento em O(n/64) |

### 3.4 Axiomas como Testes (Property-Based)

Cada axioma ZFC vira um teste Kotest com `forAll`:

| Axioma | Propriedade testada |
|---|---|
| Extensionalidade | `(A.containsAll(B) && B.containsAll(A)) ↔ (A == B)` |
| Vazio | `MathSet.empty<Int>().none()` |
| Par | `MathSet.pair(a,b).size == if(a==b) 1 else 2` |
| União | `x in (A union B) ↔ (x in A \|\| x in B)` |
| Potência | `powerSet(S).all { it isSubsetOf S }` |
| Separação | `A.filter(P).all { P(it) && it in A }` |
| Substituição | `A.map(f).size <= A.size` (para f não injetora) |

Leis algébricas via Kotest:

| Lei | Asserção |
|---|---|
| Comutatividade | `A union B == B union A` |
| De Morgan | `(A union B).complement(U) == A.complement(U) intersect B.complement(U)` |
| Absorção | `A union (A intersect B) == A` |

---

## 4. Módulo 2 — `relation`: Pares Ordenados e Relações

### 4.1 Hierarquia de Tipos

```
OrderedPair<A, B>          → (a, b) — Kuratowski: {{a}, {a,b}}
│
Relation<A, B>  : MathSet<OrderedPair<A, B>>
├── BinaryRelation<A>      → R ⊆ A × A
├── EquivalenceRelation<A> → reflexiva + simétrica + transitiva
├── PartialOrder<A>        → reflexiva + antissimétrica + transitiva
├── TotalOrder<A>          → ordem parcial + linear (tricotômica)
└── WellOrder<A>           → total + todo subconjunto não-vazio tem mínimo
```

### 4.2 Classes-chave

```kotlin
// Par ordenado com representação de Kuratowski disponível
data class OrderedPair<out A, out B>(val first: A, val second: B) {
    fun toKuratowski(): MathSet<MathSet<Any?>>  // {{a}, {a,b}}
}

// Relação como conjunto de pares + propriedades verificáveis
class Relation<A, B>(
    val domain: MathSet<A>,
    val codomain: MathSet<B>,
    val graph: MathSet<OrderedPair<A, B>>
) {
    // Propriedades verificáveis em runtime
    fun isReflexive(): Boolean        // ∀a∈A: (a,a) ∈ R
    fun isSymmetric(): Boolean        // (a,b) ∈ R → (b,a) ∈ R
    fun isTransitive(): Boolean       // (a,b),(b,c) ∈ R → (a,c) ∈ R
    fun isAntisymmetric(): Boolean
    fun isIrreflexive(): Boolean
    fun isTrichotomous(): Boolean     // ∀a,b: (a,b) ∈ R ∨ a=b ∨ (b,a) ∈ R
    fun isConnex(): Boolean           // linear
    
    // Operações
    fun inverse(): Relation<B, A>
    fun compose(other: Relation<B, *>): Relation<A, *>
    fun image(): MathSet<B>
    fun restrictDomain(subset: MathSet<A>): Relation<A, B>
    fun restrictImage(subset: MathSet<B>): Relation<A, B>
}
```

### 4.3 Relações de Equivalência e Partições

```kotlin
// EquivalenceRelation garante as 3 propriedades no construtor
class EquivalenceRelation<A>(/*...*/) : Relation<A, A> {
    fun equivalenceClass(element: A): MathSet<A>    // [a] = {x | xRa}
    fun quotientSet(): MathSet<MathSet<A>>          // A/R
    fun toPartition(): Partition<A>                 // bijeção R ↔ Partição
}

// Partição como família de conjuntos disjuntos cuja união é o todo
class Partition<A>(val parts: MathSet<MathSet<A>>, val universe: MathSet<A>) {
    init { /* verifica: disjuntos 2-a-2, ∪parts = universe, nenhum vazio */ }
    fun toEquivalenceRelation(): EquivalenceRelation<A>
}
```

### 4.4 Relações de Ordem

```kotlin
// Elementos especiais de uma ordem
interface OrderedSet<A> {
    fun minimum(): A?
    fun maximum(): A?
    fun minimals(): MathSet<A>      // Pode haver vários em ordem parcial
    fun maximals(): MathSet<A>
    fun supremum(subset: MathSet<A>): A?
    fun infimum(subset: MathSet<A>): A?
    fun successor(element: A): A?   // menor b tal que b > a (se existir)
    fun predecessor(element: A): A?
    
    // Representação visual
    fun hasseDigraph(): DirectedGraph<A>  // DAG para diagrama de Hasse
}
```

### 4.5 Produto Cartesiano

```kotlin
// A × B como MathSet<OrderedPair<A,B>> — lazy
fun <A, B> MathSet<A>.cartesianProduct(other: MathSet<B>): MathSet<OrderedPair<A, B>>

// A × B × C via nested pairs com extensão
fun <A, B, C> cartesianProduct(
    a: MathSet<A>, b: MathSet<B>, c: MathSet<C>
): MathSet<Triple<A, B, C>>
```

**Referência:** Planos de ensino UNESP (§4.1-4.2), UFSC (Produto Cartesiano, Relações Notáveis).

---

## 5. Módulo 3 — `function`: Funções como Relações Especiais

### 5.1 Hierarquia de Tipos

```
MathFunction<A, B>  : Relation<A, B>  (relação funcional)
├── Injection<A, B>       → f(a₁) = f(a₂) → a₁ = a₂
├── Surjection<A, B>      → ∀b ∈ B, ∃a: f(a) = b
├── Bijection<A, B>       → injetora + sobrejetora
│   └── inverse(): Bijection<B, A>
├── Endomorphism<A>       → f: A → A
├── Automorphism<A>       → bijeção A → A
└── ChoiceFunction<S>     → Axioma da Escolha: seleciona um elem. de cada conjunto
```

### 5.2 Operações-chave

```kotlin
interface MathFunction<A, B> {
    val domain: MathSet<A>
    val codomain: MathSet<B>
    
    operator fun invoke(a: A): B              // f(a)
    fun image(): MathSet<B>                   // Im(f) = {f(a) | a ∈ domain}
    fun preImage(b: B): MathSet<A>            // f⁻¹({b})
    fun preImage(subset: MathSet<B>): MathSet<A>
    
    // Classificação
    fun isInjective(): Boolean
    fun isSurjective(): Boolean
    fun isBijective(): Boolean
    
    // Composição: g ∘ f
    infix fun <C> compose(g: MathFunction<B, C>): MathFunction<A, C>
    
    // Restrição
    fun restrictTo(subset: MathSet<A>): MathFunction<A, B>
}
```

### 5.3 Equipolência (Cardinalidade via Bijeção)

```kotlin
// Dois conjuntos são equipolentes se existe bijeção entre eles
infix fun <A, B> MathSet<A>.isEquipotentTo(other: MathSet<B>): Boolean

// Tenta construir bijeção explícita
fun <A, B> MathSet<A>.findBijection(other: MathSet<B>): Bijection<A, B>?
```

**Referência:** UNICAMP (§4 Equipolência), UNESP (§4.3 Funções, §4.4 Conjuntos Infinitos).

---

## 6. Módulo 4 — `number-natural`: ℕ (Construção de Peano)

### 6.1 Filosofia

Este módulo **constrói** ℕ a partir de conjuntos, não usa `Int` de Kotlin como primitivo.
Internamente, claro, delega para `Int`/`Long`/`BigInteger` por performance,
mas a API expõe a construção axiomática.

### 6.2 Construção de Von Neumann

```kotlin
// Cada natural é um conjunto:
// 0 = ∅, 1 = {∅}, 2 = {∅, {∅}}, 3 = {∅, {∅}, {∅,{∅}}}, ...
// n = {0, 1, 2, ..., n-1}

sealed interface Natural : Comparable<Natural> {
    object Zero : Natural                    // 0 = ∅
    data class Succ(val pred: Natural) : Natural  // S(n) = n ∪ {n}
    
    fun toVonNeumannSet(): MathSet<MathSet<*>>  // Representação conjuntista
}
```

### 6.3 Axiomas de Peano (como interface + verificador)

```kotlin
interface PeanoAxioms<N> {
    val zero: N                          // P1: ∃ zero
    fun succ(n: N): N                    // P2: ∀n, ∃ S(n)
    fun isZero(n: N): Boolean
    fun pred(n: N): N?                   // P3: S é injetora (→ pred é parcial)
    
    // P4: zero ∉ Im(S)
    // P5: Indução — verificável apenas para predicados específicos
    fun <R> induction(
        base: R,                         // P(0) = base
        step: (N, R) -> R                // P(n) → P(S(n))
    ): (N) -> R                          // ∀n, P(n)
}
```

### 6.4 Aritmética de ℕ

```kotlin
interface NaturalArithmetic {
    // Definidas por recursão (§5.3 do plano UNESP)
    operator fun Natural.plus(other: Natural): Natural
    // a + 0 = a;  a + S(b) = S(a + b)
    
    operator fun Natural.times(other: Natural): Natural
    // a × 0 = 0;  a × S(b) = a × b + a
    
    fun Natural.pow(exp: Natural): Natural
    // a^0 = 1;  a^S(b) = a^b × a
    
    // Ordem (§5.5 UNESP): a ≤ b ↔ ∃c: a + c = b
    operator fun Natural.compareTo(other: Natural): Int
}
```

### 6.5 Conjuntos Indutivos

```kotlin
// Axioma do Infinito: existe um conjunto indutivo
// I é indutivo se: ∅ ∈ I ∧ ∀x(x ∈ I → x ∪ {x} ∈ I)
interface InductiveSet {
    fun contains(element: MathSet<*>): Boolean
    fun successor(element: MathSet<*>): MathSet<*>  // x ∪ {x}
}

// ℕ = ⋂{I | I é indutivo} — o menor conjunto indutivo
```

**Referência:** UNESP (§5 inteiro), UNICAMP (§5-6 Conjuntos finitos/infinitos, §7 Ordinais).

---

## 7. Módulo 5 — `number-integer`: ℤ (Construção via Quociente)

### 7.1 Construção

ℤ é construído como quociente de ℕ × ℕ pela relação de equivalência
`(a,b) ~ (c,d) ↔ a + d = b + c`, onde (a,b) representa a − b.

```kotlin
// A construção formal
data class IntegerPair(val a: Natural, val b: Natural)  // representa a - b

val integerEquivalence = EquivalenceRelation<IntegerPair> { p1, p2 ->
    p1.a + p2.b == p1.b + p2.a   // (a,b) ~ (c,d) ↔ a+d = b+c
}

// ℤ = (ℕ × ℕ) / ~
typealias MathInteger = EquivalenceClass<IntegerPair>
```

### 7.2 Aritmética de ℤ

```kotlin
interface IntegerArithmetic {
    // [(a,b)] + [(c,d)] = [(a+c, b+d)]
    operator fun MathInteger.plus(other: MathInteger): MathInteger
    
    // [(a,b)] × [(c,d)] = [(ac+bd, ad+bc)]
    operator fun MathInteger.times(other: MathInteger): MathInteger
    
    // Negação: -[(a,b)] = [(b,a)]
    operator fun MathInteger.unaryMinus(): MathInteger
    
    // Subtração via adição: a - b = a + (-b)
    operator fun MathInteger.minus(other: MathInteger): MathInteger
}
```

### 7.3 Embedding ℕ ↪ ℤ⁺

```kotlin
// Imersão canônica: n ↦ [(n, 0)]
fun Natural.toMathInteger(): MathInteger

// Verificação: esta imersão preserva + e ×
// n.toMathInteger() + m.toMathInteger() == (n + m).toMathInteger()
```

### 7.4 Ordem de ℤ

```kotlin
// [(a,b)] ≤ [(c,d)] ↔ a + d ≤ b + c  (em ℕ)
```

**Referência:** UNESP (§6 completo: aritmética, ordem, identificação ℕ ↔ ℤ⁺).

---

## 8. Módulo 6 — `number-rational`: ℚ (Construção via Quociente)

### 8.1 Construção

ℚ é construído como quociente de ℤ × (ℤ \ {0}) pela relação
`(a,b) ~ (c,d) ↔ a × d = b × c`, onde (a,b) representa a/b.

```kotlin
data class RationalPair(val num: MathInteger, val den: MathInteger) {
    init { require(den != MathInteger.ZERO) { "Denominador não pode ser zero" } }
}

val rationalEquivalence = EquivalenceRelation<RationalPair> { p1, p2 ->
    p1.num * p2.den == p1.den * p2.num
}

typealias MathRational = EquivalenceClass<RationalPair>
```

### 8.2 Aritmética de ℚ

```kotlin
interface RationalArithmetic {
    // [(a,b)] + [(c,d)] = [(ad + bc, bd)]
    operator fun MathRational.plus(other: MathRational): MathRational
    
    // [(a,b)] × [(c,d)] = [(ac, bd)]
    operator fun MathRational.times(other: MathRational): MathRational
    
    // Inverso multiplicativo: [(a,b)]⁻¹ = [(b,a)]  (a ≠ 0)
    fun MathRational.reciprocal(): MathRational
    
    // Divisão: a / b = a × b⁻¹
    operator fun MathRational.div(other: MathRational): MathRational
}
```

### 8.3 Embedding ℤ ↪ ℚ

```kotlin
// z ↦ [(z, 1)]
fun MathInteger.toMathRational(): MathRational

// Cadeia completa: ℕ ↪ ℤ ↪ ℚ
fun Natural.toMathRational(): MathRational = this.toMathInteger().toMathRational()
```

### 8.4 Densidade e Ordem

```kotlin
// ℚ é denso: entre quaisquer dois racionais existe outro
fun between(a: MathRational, b: MathRational): MathRational = (a + b) / TWO

// Enumerabilidade: bijeção ℕ → ℚ (zigzag de Cantor)
fun cantorEnumeration(): Sequence<MathRational>
```

**Referência:** UNESP (§7 completo), UNICAMP (§10-11 Cardinais).

---

## 9. Módulo 7 — `ordinal`: Ordinais e Aritmética Transfinita

### 9.1 Ordinais Finitos + CNF

```kotlin
// Ordinais finitos = Naturais (construção de Von Neumann)
// Ordinais transfinitos via Cantor Normal Form
sealed interface Ordinal : Comparable<Ordinal> {
    data class Finite(val n: Natural) : Ordinal
    
    // ω^α₁·c₁ + ω^α₂·c₂ + ... (α₁ > α₂ > ...)
    data class CNF(val terms: List<CNFTerm>) : Ordinal
}

data class CNFTerm(val exponent: Ordinal, val coefficient: Natural)

// Exemplos:
// ω     = CNF(listOf(CNFTerm(Finite(1), 1)))
// ω²    = CNF(listOf(CNFTerm(Finite(2), 1)))
// ω·3+5 = CNF(listOf(CNFTerm(Finite(1), 3), CNFTerm(Finite(0), 5)))
```

### 9.2 Aritmética Ordinal (Não Comutativa!)

```kotlin
interface OrdinalArithmetic {
    // CUIDADO: 1 + ω ≠ ω + 1
    operator fun Ordinal.plus(other: Ordinal): Ordinal
    operator fun Ordinal.times(other: Ordinal): Ordinal
    fun Ordinal.pow(exp: Ordinal): Ordinal
}
```

### 9.3 Indução e Recursão Transfinita

```kotlin
// Indução transfinita: princípio de prova
// Recursão transfinita: princípio de definição
fun <R> transfiniteRecursion(
    base: R,                                    // F(0)
    successorCase: (Ordinal, R) -> R,           // F(α+1) dado F(α)
    limitCase: (Ordinal, (Ordinal) -> R) -> R   // F(λ) dado F restrito a λ
): (Ordinal) -> R
```

**Referência:** UNICAMP (§7-9: Ordinais, Indução/Recursão Transfinita, Aritmética Ordinal).

---

## 10. Módulo 8 — `cardinal`: Cardinalidade

### 10.1 Tipos de Cardinalidade

```kotlin
sealed interface Cardinality : Comparable<Cardinality> {
    data class Finite(val n: Natural) : Cardinality
    object CountablyInfinite : Cardinality          // ℵ₀
    data class Aleph(val index: Ordinal) : Cardinality  // ℵ_α
    data class Beth(val index: Ordinal) : Cardinality   // ℶ_α = 2^ℶ_(α-1)
    object Continuum : Cardinality                  // 𝔠 = 2^ℵ₀
}
```

### 10.2 Aritmética Cardinal

```kotlin
interface CardinalArithmetic {
    // Para infinitos: ℵ₀ + ℵ₀ = ℵ₀, ℵ₀ × ℵ₀ = ℵ₀
    operator fun Cardinality.plus(other: Cardinality): Cardinality
    operator fun Cardinality.times(other: Cardinality): Cardinality
    fun Cardinality.pow(exp: Cardinality): Cardinality  // 2^ℵ₀ = 𝔠
}
```

### 10.3 Teorema de Cantor (Demonstração Construtiva)

```kotlin
// Dado S e qualquer f: S → P(S), constrói D ∉ Im(f)
fun <T> cantorDiagonal(
    s: MathSet<T>,
    f: MathFunction<T, MathSet<T>>
): MathSet<T> {
    // D = { x ∈ S | x ∉ f(x) }
    return s.filter { x -> x !in f(x) }
    // D ∉ Im(f) — verificável!
}
```

### 10.4 Enumerabilidade

```kotlin
object Enumerations {
    // Bijeção ℕ → ℤ: 0→0, 1→1, 2→-1, 3→2, 4→-2, ...
    fun naturalsToIntegers(): Bijection<Natural, MathInteger>
    
    // Bijeção ℕ → ℚ (pairing function de Cantor)
    fun naturalsToRationals(): Bijection<Natural, MathRational>
    
    // Diagonal: ℝ não é enumerável
    fun cantorDiagonalOnReals(
        supposedEnumeration: (Natural) -> Sequence<Int>  // "dígitos" de r_n
    ): Sequence<Int>  // Constrói real que difere de todos
}
```

### 10.5 Hipótese do Contínuo (Módulo Exploratório)

```kotlin
// Não decidível em ZFC — módulo didático
object ContinuumHypothesis {
    // CH: ℵ₁ = 2^ℵ₀  (não existe cardinal entre ℵ₀ e 2^ℵ₀)
    // GCH: 2^ℵ_α = ℵ_(α+1)
    
    fun explainIndependence(): String  // Texto didático
    fun godelModel(): String           // L (universo construtível): CH vale
    fun cohenModel(): String           // Forcing genérico: ¬CH consistente
}
```

**Referência:** UNICAMP (§10-12: Cardinais, Aritmética Cardinal, CH + AC).

---

## 11. Módulo 9 — `logic`: Fórmulas e Verificação de Axiomas

### 11.1 AST de Fórmulas de Primeira Ordem

```kotlin
sealed interface Formula {
    data class Var(val name: String) : Formula
    data class Membership(val element: Term, val set: Term) : Formula  // x ∈ A
    data class Equals(val left: Term, val right: Term) : Formula
    data class Not(val inner: Formula) : Formula
    data class And(val left: Formula, val right: Formula) : Formula
    data class Or(val left: Formula, val right: Formula) : Formula
    data class Implies(val premise: Formula, val conclusion: Formula) : Formula
    data class ForAll(val variable: String, val body: Formula) : Formula
    data class Exists(val variable: String, val body: Formula) : Formula
}
```

### 11.2 Model Checking sobre Universos Finitos

```kotlin
class FiniteModel(
    val universe: MathSet<Any>,
    val membershipRelation: Relation<Any, Any>  // interpreta ∈
) {
    fun evaluate(formula: Formula, assignment: Map<String, Any>): Boolean
}
```

### 11.3 Verificador de Axiomas ZFC

```kotlin
class ZFCVerifier(val model: FiniteModel) {
    fun checkExtensionality(): Boolean
    fun checkEmptySet(): Boolean
    fun checkPairing(): Boolean
    fun checkUnion(): Boolean
    fun checkPowerSet(): Boolean
    fun checkSeparation(predicate: Formula): Boolean
    fun checkReplacement(function: Formula): Boolean
    fun checkFoundation(): Boolean
    // Axioma do Infinito: não verificável em modelo finito (reporta)
    // Axioma da Escolha: verificável trivialmente em finito
    
    fun fullReport(): ZFCReport  // Quais axiomas satisfeitos/violados
}
```

### 11.4 Paradoxos (Demonstrações Construtivas)

```kotlin
object Paradoxes {
    // Russell: tenta construir S = {x | x ∉ x} e mostra contradição
    fun russellParadox(): ParadoxDemo
    
    // Burali-Forti: "conjunto de todos os ordinais" → contradição
    fun buraliForti(): ParadoxDemo
    
    // Cantor: |S| < |P(S)| para todo S — aplicação da diagonal
    fun cantorParadox(): ParadoxDemo
}
```

**Referência:** UNESP (§1-3), UNICAMP (§1, §13-14), UFSC (Revisão de Lógica Elementar).

---

## 12. Módulo 10 — `descriptive`: Topologia Finita e Jogos

### 12.1 Topologia sobre Conjuntos Finitos

```kotlin
class FiniteTopology<T>(
    val space: MathSet<T>,
    val openSets: MathSet<MathSet<T>>  // τ ⊆ P(X)
) {
    init { /* verifica: ∅,X ∈ τ; fechado sob ∪ finita e ∩ finita */ }
    
    fun closedSets(): MathSet<MathSet<T>>
    fun interior(subset: MathSet<T>): MathSet<T>
    fun closure(subset: MathSet<T>): MathSet<T>
    fun boundary(subset: MathSet<T>): MathSet<T>
    fun isContinuous(f: MathFunction<T, T>, other: FiniteTopology<T>): Boolean
}
```

### 12.2 Hierarquia de Borel (Finita)

```kotlin
// Classificador de nível na hierarquia
sealed interface BorelLevel {
    object Open : BorelLevel           // Σ⁰₁
    object Closed : BorelLevel         // Π⁰₁
    object FSigma : BorelLevel         // Σ⁰₂ (união contável de fechados)
    object GDelta : BorelLevel         // Π⁰₂ (interseção contável de abertos)
    // ...
}

fun classifyBorelLevel(
    set: MathSet<*>,
    topology: FiniteTopology<*>
): BorelLevel
```

### 12.3 Jogos de Gale-Stewart

```kotlin
// Jogo de determinância: dois jogadores, sequência de movimentos
class GaleStewartGame<Move>(
    val moveSet: MathSet<Move>,
    val winCondition: (List<Move>) -> Boolean,  // Player I wins if true
    val maxRounds: Int
) {
    fun hasWinningStrategy(player: Player): Boolean  // Minimax
    fun findWinningStrategy(player: Player): Strategy<Move>?
    
    sealed interface Player { object I : Player; object II : Player }
}
```

---

## 13. Módulo 11 — `combinatorics`: Ramsey e Partições

### 13.1 Teorema de Ramsey Finitário

```kotlin
object Ramsey {
    // R(r, s): menor n tal que toda 2-coloração de K_n contém K_r mono ou K_s mono
    fun ramseyNumber(r: Int, s: Int): Int?  // null se desconhecido/impraticável
    
    // Dado n e uma coloração, encontra subconjunto monocromático
    fun findMonochromaticClique(
        n: Int,
        coloring: (Int, Int) -> Color,  // cor da aresta (i,j)
        targetSize: Int
    ): MathSet<Int>?
    
    // Busca computacional por limites de R(k,k)
    fun searchBounds(k: Int): IntRange
}
```

### 13.2 Cálculo de Partições

```kotlin
object PartitionCalculus {
    // Relação de partição de Erdős-Rado: κ → (λ)^n_r
    fun partitionRelation(
        kappa: Int, lambda: Int, n: Int, r: Int
    ): Boolean
    
    // Enumerar todas as partições de um conjunto em k partes
    fun <T> allPartitions(set: MathSet<T>, k: Int): Sequence<Partition<T>>
    
    // Número de Bell: total de partições de um conjunto de n elementos
    fun bellNumber(n: Int): BigInteger
}
```

---

## 14. Roadmap de Sprints

```
═══════════════════════════════════════════════════════════════
              ROADMAP DE IMPLEMENTAÇÃO — mathsets-kt
═══════════════════════════════════════════════════════════════

Sprint 1-2 ── core/MathSet<T>                    [Fundação]
│  sealed interface MathSet<T>
│  FiniteSet, BitMathSet, EmptySet, PairSet
│  Operações: union, intersect, minus, powerSet, filter, map
│  Property-based tests: todas as leis algébricas
│  ✓ Entregável: Set ADT completo com testes de axiomas ZFC
│
Sprint 3-4 ── relation/                           [Estrutura]
│  OrderedPair, Relation, CartesianProduct
│  Propriedades: reflexiva, simétrica, transitiva, ...
│  EquivalenceRelation ↔ Partition (bijeção)
│  PartialOrder, TotalOrder, HasseDigraph
│  ⬑ depende de: core/
│
Sprint 5-6 ── function/                           [Morfismo]
│  MathFunction, Injection, Surjection, Bijection
│  Composição, inversão, imagem, pré-imagem
│  ChoiceFunction (AC computacional)
│  Equipolência
│  ⬑ depende de: core/, relation/
│
Sprint 7-8 ── number-natural/                     [ℕ]
│  Natural (Von Neumann), PeanoAxioms
│  Recursão, aritmética (+, ×, ^), ordem
│  Conjuntos indutivos, princípio de indução
│  ⬑ depende de: core/, relation/, function/
│
Sprint 9-10 ── number-integer/                    [ℤ]
│  Construção ℕ×ℕ/~
│  Aritmética, ordem, embedding ℕ ↪ ℤ
│  ⬑ depende de: number-natural/, relation/
│
Sprint 11-12 ── number-rational/                  [ℚ]
│  Construção ℤ×ℤ*/~
│  Aritmética, ordem, densidade
│  Cadeia ℕ ↪ ℤ ↪ ℚ completa
│  Enumeração de Cantor (ℕ ↔ ℚ)
│  ⬑ depende de: number-integer/, function/
│
Sprint 13-14 ── ordinal/                          [Transfinito]
│  Ordinal (Finite + CNF)
│  Aritmética ordinal (não comutativa)
│  Indução/recursão transfinita
│  ⬑ depende de: core/, number-natural/
│
Sprint 15-16 ── cardinal/                         [Infinitude]
│  Cardinality, Aleph, Beth, Continuum
│  Aritmética cardinal
│  Diagonal de Cantor (construtiva)
│  Enumerabilidade (ℤ, ℚ) + não-enumerabilidade (ℝ)
│  CH/GCH (módulo didático)
│  ⬑ depende de: core/, function/, ordinal/
│
Sprint 17-18 ── logic/                            [Meta]
│  AST de fórmulas FOL
│  Model checking em universos finitos
│  Verificador de axiomas ZFC
│  Paradoxos (Russell, Burali-Forti, Cantor)
│  ⬑ depende de: core/, relation/
│
Sprint 19-20 ── descriptive/ + combinatorics/     [Avançado]
    Topologia finita, hierarquia de Borel
    Jogos de Gale-Stewart
    Ramsey finitário + busca
    Cálculo de partições
    ⬑ depende de: core/, relation/, logic/
```

---

## 15. Stack Tecnológico

| Camada | Tecnologia | Justificativa |
|---|---|---|
| Linguagem | Kotlin 2.x (Multiplatform) | Type safety, operator overloading, sealed types |
| Build | Gradle KTS + KMP plugin | Multi-target: JVM, Native, JS |
| Testes | Kotest 5.x + Kotest Property | Property-based testing para leis algébricas |
| Benchmark | kotlinx-benchmark | Comparação entre backends de MathSet |
| Docs | Dokka | KDoc → HTML/Markdown |
| CI/CD | GitHub Actions | Build + test em JVM/Native/JS |
| Publicação | Maven Central | Lib consumível por qualquer projeto Kotlin/Java |

---

## 16. Convenções e Padrões

### 16.1 Nomenclatura

| Conceito Matemático | Nome Kotlin | Razão |
|---|---|---|
| Conjunto | `MathSet<T>` | Evita conflito com `java.util.Set` e `kotlin.collections.Set` |
| Função | `MathFunction<A,B>` | Evita conflito com `kotlin.Function` |
| Natural | `Natural` | Sem conflito |
| Inteiro (construído) | `MathInteger` | Evita conflito com `Int`/`Integer` |
| Racional (construído) | `MathRational` | Evita conflito com bibliotecas existentes |

### 16.2 DSL para Construção

```kotlin
// Objetivo: tornar a construção tão próxima da notação matemática quanto possível
val A = mathSetOf(1, 2, 3)
val B = mathSetOf(3, 4, 5)

val C = A union B                // {1,2,3,4,5}
val D = A intersect B            // {3}
val E = A minus B                // {1,2}
val F = A symmetricDiff B        // {1,2,4,5}
val P = A.powerSet()             // P(A), 2³ = 8 subconjuntos

val R = relation(A, A) { a, b -> a < b }  // Ordem estrita
val f = mathFunction(A, B) { it + 2 }     // f(x) = x + 2

3 in A                           // true (operator contains)
A isSubsetOf (A union B)         // true
```

### 16.3 Dual Mode: Didático vs. Eficiente

Cada módulo oferece duas "personalidades":

```kotlin
// Modo Didático: mostra cada passo, fiel à construção matemática
val n3 = Natural.Succ(Natural.Succ(Natural.Succ(Natural.Zero)))  // 3
val vonNeumann3 = n3.toVonNeumannSet()  // {∅, {∅}, {∅,{∅}}}

// Modo Eficiente: delega para primitivos, mesma semântica
val n3fast = Natural.of(3)  // Internamente usa Int, API idêntica
```

---

## 17. Mapeamento Ementas → Módulos

### UNESP (Licenciatura em Matemática)

| Tópico da Ementa | Módulo |
|---|---|
| §1 Cantor e paradoxos | `logic/Paradoxes` |
| §2 Axiomas de ZFC | `core/MathSet` + `logic/ZFCVerifier` |
| §3 Primeiros conjuntos, operações | `core/` |
| §4.1 Pares ordenados | `relation/OrderedPair` |
| §4.2 Relações | `relation/Relation` |
| §4.3 Funções | `function/MathFunction` |
| §4.4 Conjuntos infinitos | `cardinal/` |
| §4.5 Operações | `core/` operações avançadas |
| §4.6 Estruturas matemáticas | `relation/` (grupos, anéis como extensão futura) |
| §5 Naturais (Peano, recursão, aritmética, ordem) | `number-natural/` |
| §6 Inteiros (aritmética, ordem, ℕ ↪ ℤ⁺) | `number-integer/` |
| §7 Racionais (aritmética, ordem, ℤ ↪ ℚ) | `number-rational/` |

### UNICAMP (Pós-Graduação)

| Tópico do Programa | Módulo |
|---|---|
| §1 Teoria ingênua e problemas | `logic/Paradoxes` |
| §2 Axiomas de ZF, produtos, funções | `core/` + `relation/` + `function/` |
| §3 Relações de ordem/equivalência | `relation/` |
| §4 Equipolência | `function/Bijection` + `cardinal/` |
| §5-6 Conjuntos finitos/infinitos + Axiomas | `core/` + `cardinal/` |
| §7-9 Ordinais, indução/recursão transfinita, aritmética | `ordinal/` |
| §10-11 Cardinais + aritmética cardinal | `cardinal/` |
| §12 CH + AC | `cardinal/ContinuumHypothesis` |
| §13 Fundacionalidade + Construtibilidade | `logic/ZFCVerifier` |
| §14 Consistência e independência | `logic/` (expositivo) |

### UFSC (Teoria KM)

| Tópico da Ementa | Módulo |
|---|---|
| Entidades de KM: classes, elementos, conjuntos | `core/MathSet` (classes próprias como extensão) |
| Pertinência e Inclusão | `core/contains`, `isSubsetOf` |
| Operações elementares | `core/union`, `intersect`, `minus` |
| Listas finitas, pares | `relation/OrderedPair` |
| Relações notáveis | `relation/Relation` (todas as propriedades) |
| Equivalência e Partições | `relation/EquivalenceRelation`, `Partition` |
| Ordens (min, max, sup, inf, sucessor) | `relation/OrderedSet` |
| Classe Potência | `core/powerSet()` |
| Produto Cartesiano | `relation/cartesianProduct()` |

---

## 18. Riscos e Mitigações

| Risco | Impacto | Mitigação |
|---|---|---|
| Explosão de memória em PowerSet | Alto | Backend lazy (`Sequence`), materializa sob demanda |
| Aritmética ordinal incorreta (não é comutativa) | Médio | Testes exaustivos + fuzzing com ordinais pequenos |
| Conflito de nomes com stdlib Kotlin | Médio | Prefixo `Math` em tipos (MathSet, MathFunction, etc.) |
| Circularidade nas dependências de módulos | Médio | Dependência estritamente acíclica (ver grafo de sprints) |
| Performance do modo didático (Von Neumann) | Baixo | Dual mode: didático para demo, eficiente para produção |
| KMP JS target com BigInteger | Baixo | Expect/actual para BigInteger (JVM nativo, JS polyfill) |
