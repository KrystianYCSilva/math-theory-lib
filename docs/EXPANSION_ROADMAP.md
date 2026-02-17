# EXPANSION_ROADMAP.md — `mathsets-kt`

## Roadmap de Expansão: De Teoria dos Conjuntos a Plataforma Matemática Completa

**Versão:** 2.0.0-draft  
**Última atualização:** 2026-02-17  
**Pré-requisito:** Conclusão do ROADMAP.md v1.0 (Sprints 1–20)

---

## 1. Visão Estratégica

### 1.1 De Onde Viemos

O ROADMAP v1.0 (Sprints 1–20) entrega a fundação:

```
v1.0 entrega:
  kernel/          → NaturalNumber, IntegerNumber, RationalNumber
  logic/           → FOL, PeanoAxioms, ModelChecker
  set/             → MathSet<T> (extensional + intensional), ZFCVerifier
  relation/        → OrderedPair, EquivalenceRelation, Partition, Orders
  function/        → MathFunction, Bijection, ChoiceFunction
  construction/    → VonNeumann(ℕ), ℤ=ℕ×ℕ/~, ℚ=ℤ×ℤ*/~, Isomorphisms
  ordinal/         → CNF até ε₀, aritmética transfinita
  cardinal/        → Cantor diagonal, ℵ₀, CH didático
  descriptive/     → Topologia finita, Borel, Gale-Stewart
  combinatorics/   → Ramsey, Bell, partições
  forcing/         → Posets, filtros genéricos (experimental)
```

### 1.2 Para Onde Vamos

A expansão transforma `mathsets-kt` em uma **plataforma matemática computacional
com verificação formal**, crescendo em duas direções simultâneas:

```
                        ┌─────────────────────────────┐
                        │   PROOF ASSISTANTS (Lean,    │
                        │   Isabelle, Z3)              │
                        │   Verificação Formal         │
                        └──────────┬──────────────────┘
                                   │
  ╔═══════════════════════════════════════════════════════════════╗
  ║  EXPANSÃO "PARA CIMA" — Estruturas Aplicadas                 ║
  ║                                                               ║
  ║  analysis/     topology/     measure/     diffgeo/            ║
  ║  (ℝ, limites,  (abertos,    (σ-álgebras, (variedades,        ║
  ║   derivadas,   compacidade,  Lebesgue,    curvatura,          ║
  ║   integrais)   homotopia)    probabil.)   tensores)           ║
  ║       │             │            │             │               ║
  ║  linalg/       polynomial/  ntheory/     graph/               ║
  ║  (espaços      (anéis de    (primos,     (grafos,             ║
  ║   vetoriais,   polinômios,   mod arith,   algoritmos,         ║
  ║   matrizes)    Gröbner)      cripto)      coloração)          ║
  ║       │             │            │             │               ║
  ║  ┌────┴─────────────┴────────────┴─────────────┘              ║
  ║  │          algebra/                                           ║
  ║  │  (Magma → Semigroup → Monoid → Group)                      ║
  ║  │  (Semiring → Ring → IntegralDomain → Field)                 ║
  ║  └────────────────────┬───────────────────────────            ║
  ╠═══════════════════════╪═══════════════════════════════════════╣
  ║  v1.0 FOUNDATION      │   set/ relation/ function/            ║
  ║  (Sprints 1–20)       │   construction/ ordinal/ cardinal/    ║
  ╠═══════════════════════╪═══════════════════════════════════════╣
  ║  EXPANSÃO "PARA BAIXO" — Meta-Fundações                      ║
  ║                        │                                       ║
  ║  category/         typetheory/      computability/             ║
  ║  (categorias,      (MLTT, HoTT,    (Turing, λ-calc,          ║
  ║   funtores,        tipos depend.,    recursão,                ║
  ║   transformações   Curry-Howard)     decidibilidade)          ║
  ║   naturais)                                                    ║
  ║                    modeltheory/      solver/                   ║
  ║                    (estruturas,      (Z3, SAT/SMT,            ║
  ║                     satisfação,      verificação              ║
  ║                     completude)      automática)              ║
  ╚═══════════════════════════════════════════════════════════════╝
```

### 1.3 Princípios da Expansão

1. **Hierarquia de Dependência Estrita** — Nenhum módulo da camada N depende
   de módulos da camada N+1. Dependências só apontam para baixo.

2. **Dual Mode Preservado** — Cada novo módulo mantém modo didático (construção
   axiomática fiel, passo a passo) e modo eficiente (algoritmos otimizados,
   mesma API).

3. **Integração Incremental com Proof Assistants** — Verificação formal cresce
   junto com os módulos: não é uma fase separada, mas um companheiro constante.

4. **Compatibilidade com Ecossistema JVM** — Novos módulos oferecem bridges para
   bibliotecas existentes (KMath, JGraphT, EJML, Z3 Java bindings) quando
   disponíveis, sem dependência obrigatória.

5. **Cada Módulo é Publicável Isoladamente** — Gradle multi-module com
   publicação independente no Maven Central. Usuário pode importar só
   `mathsets-algebra` sem carregar `mathsets-topology`.

---

## 2. Estrutura de Fases

A expansão está organizada em **7 Fases** (Sprints 21–56), cada uma construindo
sobre as anteriores. As fases são independentes entre os dois eixos (cima/baixo)
mas compartilham dependências internas.

```
═══════════════════════════════════════════════════════════════
  TIMELINE GERAL
═══════════════════════════════════════════════════════════════

  v1.0 (Sprints 1–20)    ████████████████████  FUNDAÇÃO
                                │
  ┌─────────────────────────────┤
  │                             │
  ▼                             ▼
  Fase A (21–26)              Fase E (21–24)
  ÁLGEBRA ABSTRATA            META-FUNDAÇÕES
  Grupos, Anéis, Corpos       Categorias, Tipos,
  Hierarquia algébrica        Computabilidade
  │                             │
  ▼                             ▼
  Fase B (27–32)              Fase F (25–30)
  CONSTRUÇÃO DE ℝ E ℂ        SOLVERS E VERIFICAÇÃO
  Cauchy/Dedekind, Análise    Z3, SAT/SMT, Proof Objects
  básica, Completude          Isabelle bridge
  │                             │
  ├──────────┬──────────┐       │
  ▼          ▼          ▼       │
  Fase C     Fase C     Fase C  │
  (33–38)    (33–38)    (33–38) │
  ÁLGEBRA    TEORIA     GRAFOS  │
  LINEAR     DOS NUM.          │
  │          │          │       │
  └──────────┼──────────┘       │
             ▼                  │
  Fase D (39–48)               │
  ESTRUTURAS SUPERIORES        │
  Topologia, Medida,           │
  Análise, Geometria    ◄──────┘
  │
  ▼
  Fase G (49–56)
  INTEGRAÇÃO E SÍNTESE
  Proof assistant pipeline,
  Symbolic computation,
  Release 2.0
```

---

## 3. Fase A — Álgebra Abstrata (Sprints 21–26)

> **Objetivo:** Construir a hierarquia algébrica completa (Magma → Field),
> provar que ℤ é anel, ℚ é corpo, e fornecer instâncias concretas fundamentais.
> Este é o módulo mais crítico da expansão — tudo depende dele.

> **Referências Primárias:**
> - Hungerford, T.W. *Algebra*. Springer, 1974.
> - Lang, S. *Algebra*. 3rd ed. Springer, 2002.
> - Domingues, H.H.; Iezzi, G. *Álgebra Moderna*. 4ª ed. Atual, 2003.
> - Mathlib: `Mathlib.Algebra.Group.Defs`, `Mathlib.Algebra.Ring.Defs`.

### Sprint 21: Hierarquia Algébrica — Parte 1 (Magma → Group)

**Módulo:** `algebra/`

**Design Pattern: Context-Oriented Programming**

Seguindo o padrão do KMath e a lição do Mathlib ("separate algebraic operations
from objects"), as estruturas algébricas são definidas como **interfaces de contexto**,
não como traits dos elementos.

```kotlin
// A operação algébrica vive no CONTEXTO, não no elemento.
// Isso permite que o mesmo tipo (Int) participe de múltiplas
// estruturas (grupo aditivo, monoide multiplicativo) sem conflito.

interface Magma<T> {
    fun op(a: T, b: T): T
}

interface Semigroup<T> : Magma<T>
// Lei: associatividade — op(op(a,b), c) == op(a, op(b,c))

interface Monoid<T> : Semigroup<T> {
    val identity: T
    // Lei: identity é neutro — op(a, identity) == a == op(identity, a)
}

interface Group<T> : Monoid<T> {
    fun inverse(a: T): T
    // Lei: op(a, inverse(a)) == identity
}

interface AbelianGroup<T> : Group<T>
// Lei: comutatividade — op(a, b) == op(b, a)
```

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `Magma<T>` | Interface base: operação binária fechada. |
| `Semigroup<T>` | + verificador de associatividade (property-based). |
| `Monoid<T>` | + `identity: T` com verificador de neutralidade. |
| `Group<T>` | + `inverse(a: T)` com verificador de invertibilidade. |
| `AbelianGroup<T>` | + verificador de comutatividade. |
| `AdditiveGroup<T>` | Alias com sintaxe `+`, `-`, `zero`. |
| `MultiplicativeMonoid<T>` | Alias com sintaxe `*`, `one`. |
| `Subgroup<T>` | Subgrupo: verificação de fechamento, identidade, inverso. |
| `GroupHomomorphism<A,B>` | Mapa que preserva a operação: `f(a·b) = f(a)·f(b)`. |
| `Kernel<A,B>` | ker(f) = {a ∈ A \| f(a) = identity_B}. |
| `QuotientGroup<T>` | G/N via subgrupo normal N. |
| Instâncias concretas | `IntAdditiveGroup`, `RationalAdditiveGroup`. |
| `PermutationGroup<T>` | Grupo simétrico S_n. Algoritmo de Schreier-Sims para ordem. |
| `CyclicGroup` | ℤ/nℤ aditivo. Gerador, ordem. |
| `DihedralGroup` | D_n com geradores e relações. |

**Critério de Aceite (property-based, ≥500 instâncias cada):**
- Associatividade: `∀a,b,c: op(op(a,b),c) == op(a,op(b,c))`
- Identidade: `∀a: op(a, identity) == a ∧ op(identity, a) == a`
- Inverso: `∀a: op(a, inverse(a)) == identity`
- Homomorfismo: `∀a,b: f(op(a,b)) == op'(f(a), f(b))`
- Lagrange: `|H|` divide `|G|` para todo subgrupo H de G finito.
- Primeiro Teorema do Isomorfismo: `G/ker(f) ≅ Im(f)`.

### Sprint 22: Hierarquia Algébrica — Parte 2 (Semiring → Field)

**Módulo:** `algebra/`

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `Semiring<T>` | Dois monoide (aditivo + multiplicativo) com distributividade. |
| `Ring<T>` | Semiring com grupo aditivo abeliano. |
| `CommutativeRing<T>` | Ring com multiplicação comutativa. |
| `IntegralDomain<T>` | CommutativeRing sem divisores de zero. |
| `EuclideanDomain<T>` | IntegralDomain com divisão euclidiana + gcd via Euclides. |
| `Field<T>` | CommutativeRing onde todo não-zero é inversível. |
| `OrderedField<T>` | Field com ordem total compatível. |
| `RingHomomorphism<A,B>` | Preserva + e ×. Kernel, imagem. |
| `Ideal<T>` | Ideal de anel: verificação de absorção. Principal, primo, maximal. |
| `QuotientRing<T>` | R/I via ideal I. |
| Instâncias-prova | `IntegerRing : CommutativeRing<IntegerNumber>` — prova que ℤ é anel. |
| | `RationalField : OrderedField<RationalNumber>` — prova que ℚ é corpo. |
| | `ZnRing(n)` — ℤ/nℤ como anel. `ZpField(p)` — ℤ/pℤ como corpo (p primo). |

**Critério de Aceite:**
- Distributividade: `∀a,b,c: a*(b+c) == a*b + a*c`
- ℤ é domínio de integridade: `∀a,b: a*b == 0 → a == 0 ∨ b == 0`
- ℚ é corpo: `∀a≠0: a * a⁻¹ == 1`
- ℤ/pℤ é corpo para p primo; ℤ/nℤ não é corpo para n composto.
- Euclides em ℤ: `∀a,b≠0: a == q*b + r ∧ 0 ≤ r < |b|`

### Sprint 23: Anéis de Polinômios

**Módulo:** `polynomial/`

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `Polynomial<R>` | Polinômio sobre anel R. Representação densa (List<R>) e esparsa (Map<Int,R>). |
| `PolynomialRing<R>` | R[x] como instância de `CommutativeRing<Polynomial<R>>`. |
| Aritmética | Adição, multiplicação, divisão euclidiana (sobre corpos). |
| `gcd()` | Algoritmo de Euclides para polinômios sobre corpos. |
| `factor()` | Fatoração livre de quadrados. Berlekamp/Cantor-Zassenhaus sobre corpos finitos. |
| `MultivariatePolynomial<R>` | R[x₁,...,xₙ]. Representação esparsa (Map<Monomial, R>). |
| `MonomialOrder` | Lex, GrLex, GRevLex. |
| `groebnerBasis()` | Algoritmo de Buchberger. Base para álgebra computacional. |
| `idealMembership()` | Redução por base de Gröbner. |

**Critério de Aceite:**
- `(x² - 1) == (x-1)(x+1)` sobre ℚ[x].
- `gcd(x⁴ - 1, x³ - 1) == x - 1` sobre ℚ[x].
- Buchberger: ideal ⟨f₁,...,fₖ⟩ tem base de Gröbner verificável.

> **Referências:**
> - Cox, D.; Little, J.; O'Shea, D. *Ideals, Varieties, and Algorithms*. 4th ed. Springer, 2015.
> - Von zur Gathen, J.; Gerhard, J. *Modern Computer Algebra*. 3rd ed. Cambridge, 2013.

### Sprint 24: Galois e Extensões de Corpos

**Módulo:** `algebra/galois/`

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `FieldExtension<K,L>` | L como extensão de K. Grau [L:K]. |
| `AlgebraicElement<K>` | Elemento algébrico: polinômio minimal sobre K. |
| `SplittingField<K>` | Corpo de decomposição de um polinômio. |
| `GaloisGroup<K,L>` | Aut(L/K) como grupo de automorfismos. |
| `GaloisCorrespondence` | Bijeção subgrupos de Gal(L/K) ↔ corpos intermediários K ⊆ M ⊆ L. |
| `FiniteField` | GF(p^n) via polinômio irredutível. Aritmética completa. |
| `CyclotomicField` | ℚ(ζₙ) e suas propriedades. |

**Critério de Aceite:**
- GF(2³) tem 8 elementos e é corpo.
- Gal(ℚ(√2)/ℚ) ≅ ℤ/2ℤ.
- Correspondência de Galois: subgrupos ↔ corpos intermediários para extensões pequenas.

> **Referências:**
> - Stewart, I. *Galois Theory*. 4th ed. CRC Press, 2015.
> - Artin, E. *Galois Theory*. Dover, 1998 (orig. 1942).

### Sprint 25–26: Módulos, Espaços Vetoriais Abstratos e Álgebras

**Módulo:** `algebra/module/`

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `Module<R,M>` | R-módulo: grupo abeliano M com ação escalar de R. |
| `VectorSpace<K,V>` | K-espaço vetorial: Module onde K é corpo. |
| `Submodule<R,M>` | Sub-R-módulo com verificação de fechamento. |
| `Basis<K,V>` | Base de espaço vetorial. Dimensão finita/infinita. |
| `LinearMap<K,V,W>` | Transformação linear. Kernel, imagem, posto, nulidade. |
| `Algebra<K,A>` | K-álgebra: espaço vetorial com multiplicação interna. |
| `TensorProduct<R,M,N>` | M ⊗_R N com propriedade universal. |
| `ExactSequence` | Sequências exatas curtas. Lema da Cobra (didático). |

**Critério de Aceite:**
- Teorema do Posto-Nulidade: `dim(ker(f)) + dim(im(f)) == dim(V)`.
- Sequência exata curta 0 → A → B → C → 0 verificada.
- ℚ³ como espaço vetorial com base canônica {e₁, e₂, e₃}.

---

## 4. Fase B — Construção de ℝ, ℂ e Análise Básica (Sprints 27–32)

> **Objetivo:** Completar a torre numérica ℕ ↪ ℤ ↪ ℚ ↪ ℝ ↪ ℂ e
> estabelecer os fundamentos da análise real.

> **Referências Primárias:**
> - Rudin, W. *Principles of Mathematical Analysis*. 3rd ed. McGraw-Hill, 1976.
> - Spivak, M. *Calculus*. 4th ed. Publish or Perish, 2008.
> - Enderton, H.B. *Elements of Set Theory*. Academic Press, 1977, Ch. 5 (Reals).
> - Lima, E.L. *Curso de Análise Vol. 1*. 14ª ed. IMPA, 2012.
> - Mathlib: `Mathlib.Topology.Order.Basic`, `Mathlib.Analysis.SpecificLimits`.

### Sprint 27–28: Construção de ℝ

**Módulo:** `construction/real/`

**Duas construções, mesma interface:**

| Construção | Abordagem | Vantagem |
|---|---|---|
| `CauchyReal` | ℝ = sequências de Cauchy em ℚ / ~ | Aritmética simples: lift de ℚ. Escolha do Mathlib. |
| `DedekindReal` | ℝ = cortes de Dedekind em ℚ | Conceitualmente transparente. Ordem imediata. |

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `CauchySequence<Q>` | Sequência em ℚ com módulo de convergência explícito: `∀ε>0 ∃N ∀m,n>N: \|aₘ - aₙ\| < ε`. |
| `CauchyReal` | Classe de equivalência de CauchySequences. |
| `CauchyRealField` | Prova que ℝ_Cauchy é corpo ordenado completo. |
| `DedekindCut` | Par (L, R) com L ∪ R = ℚ, L < R, L sem máximo. |
| `DedekindReal` | Implementação via cortes. |
| `DedekindRealField` | Prova que ℝ_Dedekind é corpo ordenado completo. |
| `RealIsomorphism` | Bijeção Cauchy ≅ Dedekind preservando aritmética e ordem. |
| `RealNumber` (kernel) | `value class` sobre `Double`/`BigDecimal` para modo eficiente. |
| `RealKernelIsomorphism` | Ligação kernel ↔ construção axiomática. |
| `approximate(precision)` | Avaliação lazy: dado ε, retorna q ∈ ℚ com |r - q| < ε. |
| `Completude` | Toda sequência de Cauchy em ℝ converge. Toda cota superior tem supremo. |
| `RationalEmbedding` | ℚ ↪ ℝ com preservação de operações e ordem. |
| `sqrt()`, `exp()`, `pi()` | Constantes e funções via sequências convergentes. |

**Critério de Aceite:**
- `√2 * √2 ≈ 2` com precisão arbitrária.
- `√2` não é racional (prova construtiva).
- Completude: sequência de Cauchy construída explicitamente converge.
- Isomorfismo Cauchy ≅ Dedekind roundtrip para 100 reais aleatórios.

### Sprint 29: Construção de ℂ e Quaternions

**Módulo:** `construction/complex/`

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `ComplexNumber` | ℂ = ℝ × ℝ com (a,b) + (c,d) = (a+c, b+d), (a,b)·(c,d) = (ac-bd, ad+bc). |
| `ComplexField` | Prova que ℂ é corpo algebricamente fechado. |
| `RealEmbedding` | ℝ ↪ ℂ via r ↦ (r, 0). |
| `Conjugate` | z̄ = (a, -b). Propriedades: |z|² = z·z̄. |
| `Modulus`, `Argument` | Forma polar. |
| `RootsOfUnity` | ζₙ = e^{2πi/n}. Grupo cíclico de ordem n. |
| `Quaternion` | ℍ = ℝ⁴ com multiplicação de Hamilton. Corpo não comutativo. |
| `Octonion` | 𝕆 = ℝ⁸. Álgebra não associativa (menção didática). |

**Critério de Aceite:**
- Teorema Fundamental da Álgebra: todo p(z) grau n tem n raízes em ℂ (verificado computacionalmente para graus pequenos).
- i² = -1.
- Em ℍ: i·j = k, j·i = -k (não comutatividade).

### Sprint 30–31: Análise Real Básica

**Módulo:** `analysis/`

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `RealSequence` | Sequência de reais com operações (limite, limsup, liminf). |
| `Limit<T>` | `sealed interface`: `Converges(value)`, `Diverges`, `Unknown`. |
| `convergence()` | Verificação numérica de convergência (com módulo). |
| `Series` | Séries: soma parcial, convergência absoluta, testes (razão, raiz, comparação). |
| `Continuity` | f: ℝ → ℝ contínua em ponto / intervalo. Definição ε-δ computacional. |
| `Differentiable` | Derivada como limite. Derivadas de funções elementares. |
| `RiemannIntegral` | Integral de Riemann: somas de Darboux, integrabilidade. |
| `PowerSeries` | Séries de potências. Raio de convergência. Taylor/Maclaurin. |
| `FundamentalTheoremOfCalculus` | ∫ₐᵇ f'(x)dx = f(b) - f(a) verificado numericamente. |
| Funções elementares | `exp`, `log`, `sin`, `cos`, `sinh`, `cosh` via séries de potências. |

**Critério de Aceite:**
- `lim(1/n) = 0`, `lim((1+1/n)^n) ≈ e`.
- Σ(1/n²) = π²/6 (verificado com precisão 10⁻¹⁰).
- d/dx(x³) = 3x² (simbólico + numérico).
- ∫₀¹ x² dx = 1/3 (Darboux converge).

### Sprint 32: Espaços Métricos e Normados

**Módulo:** `analysis/metric/`

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `MetricSpace<T>` | `distance(a: T, b: T): RealNumber` com axiomas de métrica. |
| `NormedSpace<K,V>` | Espaço vetorial normado. `norm(v): RealNumber`. |
| `InnerProductSpace<K,V>` | Produto interno. Desigualdade de Cauchy-Schwarz. |
| `BanachSpace<K,V>` | Espaço normado completo. |
| `HilbertSpace<K,V>` | Espaço com produto interno completo. |
| `OpenBall`, `ClosedBall` | Bolas métricas. |
| `Completeness` | Completude como propriedade verificável de espaço métrico. |
| Instâncias | ℝⁿ com norma euclidiana, métrica discreta, métricas p-ádicas. |

---

## 5. Fase C — Álgebra Linear, Teoria dos Números e Grafos (Sprints 33–38)

> **Objetivo:** Três frentes paralelas que dependem de algebra/ e construction/real/
> mas não entre si. Podem ser desenvolvidas por equipes independentes.

### Sprint 33–35: Álgebra Linear

**Módulo:** `linalg/`

> **Referências:**
> - Hoffman, K.; Kunze, R. *Linear Algebra*. 2nd ed. Prentice-Hall, 1971.
> - Strang, G. *Linear Algebra and Its Applications*. 4th ed. Thomson, 2006.
> - Lima, E.L. *Álgebra Linear*. 9ª ed. IMPA, 2016.
> - Axler, S. *Linear Algebra Done Right*. 4th ed. Springer, 2024.

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `Matrix<K>` | Matriz m×n sobre corpo K. Representação densa e esparsa (CSR). |
| `MatrixRing<K>` | M_n(K) como anel. |
| `GaussianElimination<K>` | Eliminação sobre corpo exato (ℚ). Escalonamento, posto, inversa. |
| `Determinant<K>` | Via Leibniz (didático), cofatores, LU (eficiente). |
| `LinearSystem<K>` | Ax = b. Existência, unicidade, espaço de soluções. |
| `Eigenvalue<K>` | Polinômio característico, autovalores, autovetores. |
| `JordanNormalForm<K>` | Forma canônica de Jordan (sobre corpo algebricamente fechado). |
| `SmithNormalForm<R>` | Para matrizes sobre domínios euclidianos. Base de homologia. |
| `SingularValueDecomposition` | SVD (numérico sobre ℝ). |
| `InnerProductOps<K,V>` | Gram-Schmidt, projeções ortogonais. |
| `TensorOps` | Produto tensorial de matrizes. Produto de Kronecker. |
| Bridge: KMath | Adapter `KMathMatrix ↔ Matrix<K>`. |
| Bridge: EJML | Adapter para operações numéricas otimizadas (Double). |

**Critério de Aceite:**
- Gauss sobre ℚ: sistema 100×100 resolvido exatamente.
- det(A) × det(A⁻¹) = 1 para matrizes invertíveis.
- Cayley-Hamilton: p(A) = 0 onde p é o polinômio característico.
- Smith Normal Form: SNF(A) é diagonal com d_i | d_{i+1}.

### Sprint 35–36: Teoria dos Números Avançada

**Módulo:** `ntheory/`

> **Referências:**
> - Hardy, G.H.; Wright, E.M. *An Introduction to the Theory of Numbers*. 6th ed. Oxford, 2008.
> - Ireland, K.; Rosen, M. *A Classical Introduction to Modern Number Theory*. 2nd ed. Springer, 1990.
> - Shoup, V. *A Computational Introduction to Number Theory and Algebra*. Cambridge, 2009.
> - Santos, J.P.O. *Introdução à Teoria dos Números*. IMPA, 2006.

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `ModularArithmetic` | ℤ/nℤ com aritmética completa. |
| `ExtendedGcd` | Algoritmo de Euclides estendido. ax + by = gcd(a,b). |
| `ChineseRemainderTheorem` | Sistema de congruências. Reconstrução. |
| `MillerRabin` | Teste de primalidade probabilístico. |
| `PollardRho` | Fatoração. |
| `QuadraticResidue` | Símbolo de Legendre, Jacobi. Lei de Reciprocidade Quadrática. |
| `ContinuedFraction` | Representação em frações contínuas. Convergentes. |
| `PellEquation` | x² - Dy² = 1. |
| `EllipticCurve<K>` | Curva elíptica sobre corpo K. Grupo de pontos. Lei de adição. |
| `DiscreteLogarithm` | Baby-step/Giant-step, Pohlig-Hellman. |
| Funções aritméticas | φ(n), μ(n), σ(n), τ(n), Λ(n). Convolução de Dirichlet. |
| `PrimeGenerator` | Crivo de Eratóstenes segmentado. Crivo de Atkin. |

**Critério de Aceite:**
- CRT: reconstrução correta para sistemas com até 10 congruências.
- Miller-Rabin: 0 falsos positivos em 10⁶ compostos testados.
- Curva elíptica: lei de grupo verificada (associatividade) para 1000 pontos.
- φ(p) = p-1 para primos, φ(p·q) = (p-1)(q-1).

### Sprint 37–38: Teoria dos Grafos

**Módulo:** `graph/`

> **Referências:**
> - Diestel, R. *Graph Theory*. 5th ed. Springer, 2017.
> - Bondy, J.A.; Murty, U.S.R. *Graph Theory*. Springer, 2008.
> - Cormen, T.H. et al. *Introduction to Algorithms*. 4th ed. MIT Press, 2022.

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `Graph<V>` | Grafo como (V, E) onde E ⊆ V × V (usa MathSet + Relation do v1.0). |
| `DirectedGraph<V>` | Grafo dirigido. |
| `WeightedGraph<V,W>` | Arestas com pesos. |
| `Adjacency` | Matriz de adjacência (via `Matrix<K>`) e lista de adjacência. |
| `BFS`, `DFS` | Busca em largura e profundidade. |
| `Dijkstra`, `BellmanFord` | Caminhos mínimos. |
| `Kruskal`, `Prim` | Árvore geradora mínima. |
| `MaxFlow` | Ford-Fulkerson / Edmonds-Karp. |
| `Matching` | Emparelhamento máximo (Hopcroft-Karp). |
| `Coloring` | Coloração gulosa, número cromático (backtracking). |
| `Isomorphism` | Teste de isomorfismo (VF2 ou WL). |
| `SpectralGraph` | Espectro do grafo via autovalores da matriz de adjacência (liga com linalg/). |
| `PlanarityTest` | Teste de planaridade (Boyer-Myrvold). |
| Bridge: JGraphT | Adapter bidirecional `Graph<V> ↔ JGraphT.Graph`. |

**Critério de Aceite:**
- K₅ e K₃,₃ não são planares.
- χ(K_n) = n (número cromático de grafo completo).
- Dijkstra: caminho mínimo correto para grafos com 10⁴ vértices.
- Espectro de K_n: autovalor n-1 com multiplicidade 1, autovalor -1 com multiplicidade n-1.

---

## 6. Fase D — Estruturas Superiores (Sprints 39–48)

> **Objetivo:** Topologia geral, teoria da medida, análise avançada e
> geometria diferencial — as grandes estruturas que unificam os módulos anteriores.

### Sprint 39–40: Topologia Geral

**Módulo:** `topology/`

> **Referências:**
> - Munkres, J.R. *Topology*. 2nd ed. Prentice Hall, 2000.
> - Lima, E.L. *Elementos de Topologia Geral*. SBM, 2009.
> - Kelley, J.L. *General Topology*. Springer, 1955.

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `TopologicalSpace<T>` | Interface: `openSets(): MathSet<MathSet<T>>` com axiomas (∅, X abertos; união arbitrária; interseção finita). |
| `MetricTopology<T>` | Topologia induzida por métrica. |
| `SubspaceTopology` | Topologia induzida. |
| `ProductTopology` | Produto de espaços topológicos. |
| `QuotientTopology` | Topologia quociente via relação de equivalência. |
| `Continuity<X,Y>` | f: X → Y contínua ↔ pré-imagem de aberto é aberto. |
| `Homeomorphism<X,Y>` | Bijeção bicontínua. |
| `Compactness` | Cobertura finita. Heine-Borel para ℝⁿ (didático). |
| `Connectedness` | Conexidade. Componentes conexas. Conexidade por caminhos. |
| `Hausdorff` | Axioma de separação T₂. |
| `SimplicialComplex` | Simplexos, complexos simpliciais, homologia simplicial. |
| `BettiNumbers` | Via Smith Normal Form das matrizes de bordo. |
| Bridge: JavaPlex | Adapter para homologia persistente. |

**Critério de Aceite:**
- Toro T² ≅ S¹ × S¹ tem β₀=1, β₁=2, β₂=1.
- [0,1] é compacto; (0,1) não é.
- f: ℝ → ℝ contínua na topologia métrica ↔ contínua na definição ε-δ.

### Sprint 41–42: Teoria da Medida e Probabilidade

**Módulo:** `measure/`

> **Referências:**
> - Bartle, R.G. *The Elements of Integration and Lebesgue Measure*. Wiley, 1995.
> - Billingsley, P. *Probability and Measure*. 3rd ed. Wiley, 1995.
> - Folland, G.B. *Real Analysis*. 2nd ed. Wiley, 1999.

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `SigmaAlgebra<T>` | σ-álgebra como `MathSet<MathSet<T>>` fechada sob complemento e união contável. |
| `MeasurableSpace<T>` | Par (X, Σ) com Σ σ-álgebra sobre X. |
| `Measure<T>` | μ: Σ → [0, ∞]. Aditiva contável. |
| `ProbabilityMeasure<T>` | Medida com μ(X) = 1. |
| `LebesgueMeasure` | Medida de Lebesgue em ℝⁿ (construção didática). |
| `MeasurableFunction<A,B>` | Pré-imagem de mensurável é mensurável. |
| `LebesgueIntegral` | Integral de Lebesgue: funções simples → funções mensuráveis. |
| `FiniteProbabilitySpace` | Ω finito com distribuição discreta. |
| `RandomVariable<T>` | Função mensurável de Ω para T. |
| `Expectation` | E[X] = ∫ X dP. Linearidade. |
| `Variance` | Var(X) = E[(X - E[X])²]. |
| `ConditionalExpectation` | E[X\|Y]. |
| `LawOfLargeNumbers` | Verificação computacional (simulação). |
| Distribuições | Bernoulli, Binomial, Poisson, Normal (via séries). |

**Critério de Aceite:**
- σ-álgebra sobre {1,2,3}: verificação dos axiomas para 100 exemplos.
- E[X+Y] == E[X] + E[Y] para 1000 pares aleatórios.
- Lei dos Grandes Números: média amostral converge para E[X].

### Sprint 43–44: Análise Funcional e Espaços L^p

**Módulo:** `analysis/functional/`

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `LpSpace<T>` | Espaço L^p com norma ‖f‖_p = (∫\|f\|^p)^{1/p}. |
| `DualSpace<V>` | V* = espaço de funcionais lineares contínuos. |
| `LinearOperator<V,W>` | Operadores lineares contínuos entre espaços normados. |
| `BoundedOperator` | ‖T‖ = sup{‖Tx‖/‖x‖}. |
| `Spectrum<T>` | Espectro de operador: σ(T) = {λ \| T - λI não inversível}. |
| Teoremas fundamentais | Hahn-Banach, Banach-Steinhaus, Open Mapping (enunciados + exemplos finitos). |

### Sprint 45–46: Geometria Diferencial Básica

**Módulo:** `diffgeo/`

> **Referências:**
> - do Carmo, M.P. *Differential Geometry of Curves and Surfaces*. 2nd ed. Dover, 2016.
> - Lee, J.M. *Introduction to Smooth Manifolds*. 2nd ed. Springer, 2012.
> - do Carmo, M.P. *Geometria Riemanniana*. 5ª ed. IMPA, 2011.

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `SmoothManifold` | Variedade suave: cartas, atlas, transições suaves. |
| `TangentVector` | Vetores tangentes como derivações. |
| `TangentBundle` | TM como variedade. |
| `DifferentialForm` | k-formas: wedge product, derivada exterior. |
| `RiemannianMetric` | Tensor métrico g. Comprimento de curvas. |
| `Curvature` | Curvatura gaussiana, curvatura média, tensor de Riemann. |
| `ChristoffelSymbols` | Γᵢⱼₖ — conexão de Levi-Civita. |
| `Geodesic` | Equações geodésicas. Integração numérica. |
| Superfícies clássicas | Esfera, toro, superfície de revolução, hiperboloide. |

### Sprint 47–48: Álgebra Homológica e Topologia Algébrica

**Módulo:** `topology/algebraic/`

> **Referências:**
> - Hatcher, A. *Algebraic Topology*. Cambridge, 2001 (disponível gratuitamente).
> - Rotman, J.J. *An Introduction to Homological Algebra*. 2nd ed. Springer, 2009.

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `ChainComplex` | Sequência de módulos com operadores de bordo ∂ₙ: Cₙ → Cₙ₋₁ com ∂² = 0. |
| `HomologyGroup` | Hₙ = ker(∂ₙ) / im(∂ₙ₊₁). Calculado via Smith Normal Form. |
| `CohomologyGroup` | Dual: Hⁿ. |
| `FundamentalGroup` | π₁(X, x₀) para complexos simpliciais. |
| `CoveringSpace` | Espaço de recobrimento. |
| `ExactSequenceLong` | Sequência exata longa de par (X, A). |
| `EulerCharacteristic` | χ = Σ(-1)ⁱ βᵢ = Σ(-1)ⁱ #(i-simplexos). |
| `MayerVietoris` | Sequência de Mayer-Vietoris para cálculo de homologia. |

---

## 7. Fase E — Meta-Fundações (Sprints 21–24, paralelo à Fase A)

> **Objetivo:** Expandir "para baixo" — Teoria das Categorias como meta-framework,
> Teoria dos Tipos como fundação alternativa, Teoria da Computabilidade.
> Executada em paralelo com a Fase A (mesmo período, equipe/esforço separado).

### Sprint 21–22 (paralelo): Teoria das Categorias

**Módulo:** `category/`

> **Referências:**
> - Mac Lane, S. *Categories for the Working Mathematician*. 2nd ed. Springer, 1998.
> - Awodey, S. *Category Theory*. 2nd ed. Oxford, 2010.
> - Riehl, E. *Category Theory in Context*. Dover, 2016 (disponível gratuitamente).
> - Mathlib: `Mathlib.CategoryTheory.Category.Basic`.

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `Category<Obj, Hom>` | Interface: `id(a)`, `compose(f, g)`, `source(f)`, `target(f)`. Leis: associatividade e identidade. |
| `Functor<C, D>` | Mapeia objetos e morfismos preservando composição e identidade. |
| `NaturalTransformation<F, G>` | Família de morfismos η_A: F(A) → G(A) com naturalidade. |
| `Isomorphism<C>` | Morfismo com inversa. |
| Categorias concretas | `FinSet` (conjuntos finitos), `FinGroup` (grupos finitos), `FinVect_K` (espaços vetoriais finitos sobre K). |
| `OppositeCategory<C>` | C^op: inverte direção dos morfismos. |
| `ProductCategory<C,D>` | C × D. |
| `Yoneda<C>` | Funtor Hom(A, -). Lema de Yoneda (verificado para categorias finitas). |
| `Adjunction<F, G>` | Par adjunto F ⊣ G com bijection natural Hom(FA, B) ≅ Hom(A, GB). |
| `Limit`, `Colimit` | Produtos, coprodutos, equalizadores, coequalizadores. |
| `MonoidalCategory` | (C, ⊗, I) com associadores e unificadores. |

**Critério de Aceite:**
- Lema de Yoneda verificado sobre FinSet com |Obj| ≤ 10.
- Free ⊣ Forgetful adjunção para FinGroup → FinSet.
- Composição de funtores é associativa.

### Sprint 23 (paralelo): Teoria dos Tipos e Computabilidade

**Módulo:** `typetheory/`, `computability/`

> **Referências:**
> - Martin-Löf, P. *Intuitionistic Type Theory*. Bibliopolis, 1984.
> - Univalent Foundations Program. *Homotopy Type Theory*. IAS, 2013 (HoTT Book).
> - Cutland, N.J. *Computability: An Introduction to Recursive Function Theory*. Cambridge, 1980.
> - Sipser, M. *Introduction to the Theory of Computation*. 3rd ed. Cengage, 2012.

**Entregáveis (typetheory/):**

| Componente | Descrição |
|---|---|
| `Type` | `sealed interface`: `Universe(level)`, `Pi(domain, codomain)`, `Sigma(base, fiber)`, `Id(type, left, right)`, `Nat`, `Bool`, `Empty`, `Unit`. |
| `Term` | `sealed interface`: `Var`, `Lambda`, `App`, `Pair`, `Proj1`, `Proj2`, `Refl`, `Zero`, `Succ`, `NatRec`. |
| `TypeChecker` | Bidirecional: `infer(ctx, term): Type` e `check(ctx, term, type): Boolean`. |
| `CurryHoward` | Módulo didático: proposição ↔ tipo, prova ↔ programa. |
| `Evaluator` | Redução β + regras de computação para Nat, Bool, etc. |

**Entregáveis (computability/):**

| Componente | Descrição |
|---|---|
| `TuringMachine<S, A>` | Estados S, alfabeto A, função de transição, fita infinita. |
| `TuringSimulator` | Execução bounded (máximo de passos). Visualização de fita. |
| `LambdaCalculus` | Termos λ, redução β, avaliação normal-order e applicative-order. |
| `PartialRecursiveFunction` | Composição, recursão primitiva, μ-recursão. |
| `ChurchEncoding` | Naturais de Church, booleanos, pares. |
| `HaltingProblem` | Demonstração construtiva da indecidibilidade (diagonal). |
| `Kolmogorov` | Complexidade de Kolmogorov: definição + incomputabilidade. |

### Sprint 24 (paralelo): Teoria dos Modelos

**Módulo:** `modeltheory/`

> **Referências:**
> - Marker, D. *Model Theory: An Introduction*. Springer, 2002.
> - Hodges, W. *A Shorter Model Theory*. Cambridge, 1997.
> - Chang, C.C.; Keisler, H.J. *Model Theory*. 3rd ed. North-Holland, 1990.

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `Signature` | Símbolos de função (aridade), relação (aridade), constantes. |
| `Structure<S>` | Domínio + interpretação dos símbolos de S. |
| `Satisfaction` | `M ⊨ φ` — avaliação de fórmula FOL em estrutura. Estende logic/ModelChecker. |
| `ElementaryEquivalence` | M ≡ N ↔ mesmas sentenças verdadeiras. |
| `Embedding` | Mergulho elementar. |
| `Ultraproduct` | Construção de ultraproduto sobre famílias finitas (didático). |
| `Compactness` | Teorema da Compacidade: verificação sobre conjuntos finitos de sentenças. |
| `LöwenheimSkolem` | Módulo expositivo: enunciado + consequências. |
| `QuantifierElimination` | QE para DLO (ordens lineares densas sem extremos). |

---

## 8. Fase F — Solvers e Verificação Formal (Sprints 25–30, paralelo)

> **Objetivo:** Integrar ferramentas de verificação automática (Z3, SAT solvers)
> e estabelecer a ponte com proof assistants (Isabelle, Lean 4).

### Sprint 25–26 (paralelo): Integração Z3 e SAT/SMT

**Módulo:** `solver/`

> **Referências:**
> - De Moura, L.; Bjørner, N. "Z3: An Efficient SMT Solver." TACAS 2008.
> - Biere, A. et al. *Handbook of Satisfiability*. 2nd ed. IOS Press, 2021.

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `Z3Bridge` | Wrapper Kotlin sobre Z3 Java bindings. Traduz `Formula` (logic/) para Z3 AST e vice-versa. |
| `SmtSolver` | Interface: `checkSat(formulas): SatResult`. `sealed interface SatResult`: `Sat(model)`, `Unsat(proof)`, `Unknown`. |
| `SetConstraintSolver` | Traduz restrições sobre MathSet para SMT-LIB teoria de conjuntos. |
| `ArithmeticVerifier` | Verifica propriedades aritméticas via Z3 (quantifier-free LIA/LRA). |
| `SatSolver` | Interface para SAT puro. Backends: kotlin-satlib (MiniSat, CaDiCaL) ou KoSAT puro. |
| `PropositionalLogic` | Tradução de `Formula` proposicional para CNF. |
| `AllSatEnumerator` | Enumera todos os modelos satisfazíveis. |

**Critério de Aceite:**
- Z3 verifica `∀x,y ∈ ℤ: x + y == y + x` como válido.
- Z3 encontra contraexemplo para `∀x ∈ ℤ: x² > 0` (x = 0).
- SAT solver resolve instâncias com 10³ variáveis.

### Sprint 27–28 (paralelo): Proof Objects e Verificação

**Módulo:** `proof/`

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `Proof` | `sealed interface`: `Axiom(formula)`, `ModusPonens(p, pImpliesQ)`, `UniversalGeneralization(p, var)`, `Assumption(formula)`, `Contradiction(p, notP)`. |
| `ProofChecker` | `verify(proof: Proof): VerificationResult`. Verifica passo a passo. |
| `TheoremRegistry` | Registro de teoremas provados. Rastreia dependências axiomáticas. |
| `ProofBuilder` | DSL para construir provas: `proof { assume(φ); apply(modusPonens, ...); qed() }`. |
| `ProofSearch` | Busca automática de prova para fórmulas simples (Davis-Putnam, tableau). |
| `CertifiedComputation` | Wrapper que associa resultado computacional + proof object. |

### Sprint 29–30 (paralelo): Bridge com Isabelle e Lean

**Módulo:** `verified/`

> **Referências:**
> - Hupel, L.; Kuncak, V. "Translating Scala Programs to Isabelle/HOL." IJCAR 2016.
> - Haftmann, F.; Nipkow, T. "Code Generation via Higher-Order Rewrite Systems." FLOPS 2010.
> - Mathlib documentation: leanprover-community.github.io

**Entregáveis:**

| Componente | Descrição |
|---|---|
| **Isabelle Pipeline** | |
| `isabelle-export/` | Teorias Isabelle/HOL formalizando: aritmética de ℤ/ℚ, propriedades de grupo/anel, Smith Normal Form. |
| `isabelle-codegen/` | `export_code` → Scala → `.jar`. |
| `IsabelleVerifiedOps` | API Kotlin que delega para código Isabelle-generated. |
| **Lean Pipeline** | |
| `lean-proofs/` | Provas Lean 4 das propriedades-chave (teoremas de álgebra, análise). |
| `lean-export/` | Lean 4 → C → shared library (.so/.dll). |
| `LeanVerifiedOps` | API Kotlin via JNI/JNA para funções Lean exportadas. |
| **Interop** | |
| `ProofTranslator` | Traduz `Proof` objects (módulo proof/) para Lean/Isabelle syntax para verificação externa. |
| `VerificationReport` | Relatório: quais teoremas têm prova formal, quais são apenas property-tested. |

**Critério de Aceite:**
- Código Isabelle-generated para `gcd(a, b)` produz resultado idêntico ao kernel Kotlin.
- Pelo menos 5 teoremas de algebra/ com prova formal em Lean 4 exportada.
- `VerificationReport` cataloga cobertura formal vs. property-based.

---

## 9. Fase G — Integração, Symbolic Computation e Release 2.0 (Sprints 49–56)

### Sprint 49–50: Motor de Computação Simbólica

**Módulo:** `symbolic/`

> **Referências:**
> - Geddes, K.O.; Czapor, S.R.; Labahn, G. *Algorithms for Computer Algebra*. Springer, 1992.
> - Von zur Gathen, J.; Gerhard, J. *Modern Computer Algebra*. 3rd ed. Cambridge, 2013.

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `Expression` | `sealed interface`: `Const`, `Var`, `Add`, `Mul`, `Pow`, `Func(name, args)`. Imutável. Hashconsing para dedup. |
| `Simplifier` | Regras de simplificação: comutatividade, distribuição, cancelamento, identidades. |
| `Differentiator` | Derivação simbólica: chain rule, product rule, quotient rule. |
| `Integrator` | Integração simbólica: Risch semi-algorithm para funções elementares. |
| `EquationSolver` | Solver simbólico: lineares, quadráticas, sistemas. |
| `PatternMatcher` | Pattern matching sobre árvores de expressão. |
| `LaTeXRenderer` | Expressão → LaTeX string. |
| `ExpressionParser` | String → Expression (parser de notação matemática). |

### Sprint 51–52: Equações Diferenciais

**Módulo:** `ode/`

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `ODE` | Equação diferencial ordinária: y' = f(t, y). |
| `EulerMethod` | Integrador numérico simples (didático). |
| `RungeKutta4` | RK4 clássico. |
| `AdaptiveRK` | Dormand-Prince com passo adaptativo. |
| `SymbolicODE` | Solver simbólico para EDOs separáveis, lineares, Bernoulli, exatas. |
| `SystemOfODEs` | Sistemas: representação matricial, autovalores para sistemas lineares. |
| `PhasePortrait` | Geração de dados para retrato de fase (pontos de equilíbrio, classificação). |

### Sprint 53–54: Unificação e Cross-Cutting

**Entregáveis:**

| Componente | Descrição |
|---|---|
| `MathExplorer` | REPL/notebook interativo: avalia expressões, plota, explora estruturas. |
| `ProofDashboard` | Visualização do status de verificação formal de toda a biblioteca. |
| `BenchmarkSuite` | Comparação de performance: kernel vs. construção axiomática vs. bibliotecas externas. |
| `InteropMatrix` | Testes de integração cross-module: algebra + linalg + topology. |
| Documentação unificada | Dokka para todos os módulos. Site com tutoriais interativos. |

### Sprint 55–56: Polimento e Release 2.0

**Entregáveis:**

| Componente | Descrição |
|---|---|
| API Review | Revisão de toda a API pública. Deprecation de inconsistências. |
| Performance | Profiling e otimização dos hot paths (polynomial arithmetic, matrix ops, Z3 bridge). |
| Publicação | Maven Central: cada módulo como artefato independente. |
| Paper acadêmico | Draft de artigo descrevendo a arquitetura e a ponte computation ↔ verification. |
| Release 2.0 | Tag, changelog, migration guide de v1.0 → v2.0. |

---

## 10. Milestones da Expansão

```
═══════════════════════════════════════════════════════════════
                MILESTONES — mathsets-kt v2.0
═══════════════════════════════════════════════════════════════

M7  "Release v1.0"         Sprint 20       (ROADMAP v1.0 completo)
│
├── Eixo "Para Cima" ──────────────────────────────────────────
│
M8  "Algebra Core"          Sprint 23       Hierarquia algébrica completa
│   Group + Ring + Field + instâncias ℤ, ℚ.
│   Polynomial rings + Gröbner bases.
│
M9  "Number Tower ℝ ℂ"     Sprint 29       Torre numérica completa
│   ℝ (Cauchy + Dedekind) + ℂ + ℍ.
│   Análise real básica: limites, derivadas, integrais.
│
M10 "Applied Math"          Sprint 38       Álgebra linear + NTheory + Grafos
│   Matrizes sobre ℚ/ℝ. Smith/Jordan Normal Forms.
│   Primalidade, fatoração, curvas elípticas.
│   Grafos com algoritmos clássicos.
│
M11 "Higher Structures"     Sprint 48       Topologia + Medida + Geometria
│   Topologia geral + simplicial. Homologia.
│   σ-álgebras + Lebesgue + probabilidade.
│   Variedades + curvatura + geodésicas.
│
├── Eixo "Para Baixo" ─────────────────────────────────────────
│
M12 "Meta-Foundations"      Sprint 24       Categorias + Tipos + Modelos
│   Categorias finitas + funtores + Yoneda.
│   Type checker MLTT. Turing machine simulator.
│   Model theory: Signature + Structure + Satisfaction.
│
M13 "Verified Math"         Sprint 30       Z3 + Proofs + Isabelle/Lean
│   Z3 bridge funcional.
│   Proof objects verificáveis.
│   ≥5 teoremas com prova formal em Lean/Isabelle.
│
├── Síntese ────────────────────────────────────────────────────
│
M14 "Symbolic Engine"       Sprint 52       Computação simbólica
│   Expression trees + simplificação + derivação + integração.
│   Solver de EDOs simbólico + numérico.
│
M15 "Release v2.0"         Sprint 56       Plataforma completa
    API unificada, documentation site, Maven Central.
    Paper acadêmico submetido.
    Proof dashboard: status de verificação formal.
```

---

## 11. Grafo de Dependências Completo

```
                            kernel/  logic/  set/  relation/  function/
                            construction/  ordinal/  cardinal/
                                    (v1.0 — Sprints 1–20)
                                           │
                 ┌─────────────────────────┼─────────────────────────┐
                 │                         │                         │
          ┌──────┴──────┐           ┌──────┴──────┐          ┌──────┴──────┐
          │  category/  │           │  algebra/   │          │  solver/    │
          │  typetheory/│           │  (Fase A)   │          │  proof/     │
          │  computab./ │           │ Sprint 21-26│          │  (Fase F)   │
          │  modelthy./ │           └──────┬──────┘          │ Sprint 25-30│
          │  (Fase E)   │                  │                 └──────┬──────┘
          │ Sprint 21-24│           ┌──────┴──────┐                │
          └──────┬──────┘           │  real/ ℂ    │                │
                 │                  │  analysis/  │                │
                 │                  │  (Fase B)   │                │
                 │                  │ Sprint 27-32│                │
                 │                  └──────┬──────┘                │
                 │                         │                       │
                 │            ┌────────────┼────────────┐          │
                 │            │            │            │          │
                 │      ┌─────┴─────┐ ┌───┴───┐ ┌─────┴─────┐    │
                 │      │  linalg/  │ │ntheory│ │  graph/    │    │
                 │      │Sprint33-35│ │Spr35-6│ │ Sprint37-38│    │
                 │      └─────┬─────┘ └───┬───┘ └─────┬─────┘    │
                 │            └────────────┼───────────┘          │
                 │                         │                      │
                 │            ┌────────────┼────────────┐         │
                 │            │            │            │         │
                 │      ┌─────┴─────┐ ┌───┴────┐ ┌────┴────┐    │
                 │      │ topology/ │ │measure/│ │ diffgeo/ │    │
                 │      │Sprint39-40│ │Spr41-42│ │Spr45-46  │    │
                 │      └─────┬─────┘ └───┬────┘ └────┬────┘    │
                 │            │            │           │         │
                 │      ┌─────┴─────┐ ┌───┴────┐      │         │
                 │      │func.anal. │ │alg.top.│      │         │
                 │      │Sprint43-44│ │Spr47-48│      │         │
                 │      └───────────┘ └────────┘      │         │
                 │                                     │         │
                 └─────────────────┬───────────────────┘         │
                                   │                             │
                            ┌──────┴──────┐               ┌─────┴──────┐
                            │  symbolic/  │               │  verified/ │
                            │  ode/       │               │ (Isabelle, │
                            │ Sprint49-52 │               │  Lean)     │
                            └──────┬──────┘               │ Sprint29-30│
                                   │                      └─────┬──────┘
                                   └────────────┬───────────────┘
                                                │
                                         ┌──────┴──────┐
                                         │  Release    │
                                         │  v2.0       │
                                         │ Sprint53-56 │
                                         └─────────────┘
```

---

## 12. Novos Módulos — Árvore de Diretórios

```
mathsets-kt/
├── [v1.0 modules]          (kernel, logic, set, relation, function,
│                            construction, ordinal, cardinal,
│                            descriptive, combinatorics, forcing)
│
├── algebra/                        ← Fase A
│   ├── core/                       (Magma → Group → Ring → Field)
│   ├── galois/                     (Extensões de corpo, Galois)
│   └── module/                     (Módulos, espaços vetoriais abstratos)
│
├── polynomial/                     ← Fase A
│   ├── univariate/                 (R[x])
│   └── multivariate/              (R[x₁,...,xₙ], Gröbner)
│
├── construction/
│   ├── [v1.0: natural, integer, rational, isomorphism]
│   ├── real/                       ← Fase B (Cauchy + Dedekind)
│   └── complex/                   ← Fase B (ℂ, ℍ, 𝕆)
│
├── analysis/                       ← Fase B + D
│   ├── sequences/                  (Limites, séries)
│   ├── calculus/                   (Derivadas, integrais)
│   ├── metric/                     (Espaços métricos, normados)
│   └── functional/                (L^p, operadores, espectro)
│
├── linalg/                         ← Fase C
│   ├── matrix/                     (Densa, esparsa, operações)
│   ├── decomposition/             (LU, QR, SVD, Jordan, Smith)
│   └── bridge/                    (KMath, EJML adapters)
│
├── ntheory/                        ← Fase C
│   ├── modular/                   (ℤ/nℤ, CRT, Euler)
│   ├── primality/                 (Miller-Rabin, Pollard)
│   ├── elliptic/                  (Curvas elípticas)
│   └── arithmetic/               (Funções aritméticas)
│
├── graph/                          ← Fase C
│   ├── core/                      (Graph<V>, DirectedGraph<V>)
│   ├── algorithms/                (BFS, Dijkstra, MaxFlow, ...)
│   ├── spectral/                  (Espectro de grafos)
│   └── bridge/                    (JGraphT adapter)
│
├── topology/                       ← Fase D
│   ├── general/                   (TopologicalSpace, continuidade)
│   ├── simplicial/                (Complexos simpliciais)
│   ├── algebraic/                 (Homologia, π₁, Euler)
│   └── bridge/                    (JavaPlex adapter)
│
├── measure/                        ← Fase D
│   ├── sigma/                     (σ-álgebras, medidas)
│   ├── lebesgue/                  (Medida e integral de Lebesgue)
│   └── probability/              (Prob. spaces, distribuições, E[X])
│
├── diffgeo/                        ← Fase D
│   ├── manifold/                  (Variedades, cartas, atlas)
│   ├── forms/                     (Formas diferenciais)
│   └── riemannian/               (Métrica, curvatura, geodésicas)
│
├── category/                       ← Fase E
│   ├── core/                      (Category, Functor, NatTrans)
│   ├── constructions/            (Product, Coproduct, Limit)
│   └── concrete/                 (FinSet, FinGroup, FinVect)
│
├── typetheory/                     ← Fase E
│   ├── mltt/                      (Martin-Löf: Pi, Sigma, Id, Nat)
│   ├── checker/                   (Type checker bidirecional)
│   └── curryhoward/              (Proposições ↔ Tipos didático)
│
├── computability/                  ← Fase E
│   ├── turing/                    (TuringMachine, simulador)
│   ├── lambda/                    (λ-cálculo, redução β)
│   └── recursive/                (Funções recursivas parciais)
│
├── modeltheory/                    ← Fase E
│   ├── signature/                 (Linguagens de primeira ordem)
│   ├── structure/                 (Modelos, satisfação)
│   └── theorems/                 (Compacidade, Löwenheim-Skolem)
│
├── solver/                         ← Fase F
│   ├── z3/                        (Z3 bridge, SMT-LIB)
│   ├── sat/                       (SAT solver interface + backends)
│   └── arithmetic/               (Verificação aritmética automática)
│
├── proof/                          ← Fase F
│   ├── objects/                   (Proof sealed class hierarchy)
│   ├── checker/                   (Verificador de provas)
│   ├── builder/                   (DSL para construção de provas)
│   └── registry/                 (TheoremRegistry, dependências)
│
├── verified/                       ← Fase F
│   ├── isabelle/                  (Teorias .thy + codegen Scala)
│   ├── lean/                      (Provas .lean + FFI C)
│   └── interop/                  (ProofTranslator, VerificationReport)
│
├── symbolic/                       ← Fase G
│   ├── expression/                (AST, simplificação, pattern match)
│   ├── calculus/                  (Derivação, integração simbólica)
│   └── render/                   (LaTeX, pretty-print, parser)
│
├── ode/                            ← Fase G
│   ├── numerical/                 (Euler, RK4, adaptativo)
│   └── symbolic/                 (Separáveis, lineares, Bernoulli)
│
└── explorer/                       ← Fase G
    ├── repl/                      (REPL interativo)
    └── dashboard/                (Proof dashboard, benchmarks)
```

---

## 13. Dependências Externas (Opcionais)

Cada bridge é **opcional** — o módulo funciona sem a dependência externa,
mas ganha performance/funcionalidade com ela.

| Dependência | Módulo | Uso | Obrigatória? |
|---|---|---|---|
| Z3 Java Bindings | solver/z3/ | SMT solving | Não — fallback para SAT puro |
| kotlin-satlib | solver/sat/ | SAT solver JNI | Não — fallback para KoSAT puro |
| EJML | linalg/bridge/ | Matrizes numéricas Double | Não — fallback para implementação pura |
| KMath | linalg/bridge/ | Interop Kotlin math | Não |
| JGraphT | graph/bridge/ | Algoritmos de grafos otimizados | Não — fallback para implementação pura |
| JavaPlex | topology/bridge/ | Homologia persistente | Não |
| Isabelle | verified/isabelle/ | Código verificado formalmente | Não |
| Lean 4 | verified/lean/ | Provas formais | Não |

---

## 14. Critérios de Qualidade (Expansão)

Além dos critérios do ROADMAP v1.0, a expansão adiciona:

| Critério | Métrica | Ferramenta |
|---|---|---|
| Verificação formal | ≥ 30% dos teoremas-chave com prova Lean/Isabelle | verified/ pipeline |
| Cobertura algébrica | Toda lei de estrutura (grupo, anel, corpo) com property-based | Kotest Property |
| Performance numérica | linalg/ dentro de 10x da performance EJML para matrizes ≤ 1000×1000 | kotlinx-benchmark |
| Interop | Todo bridge com teste de roundtrip (mathsets ↔ lib externa) | Integration tests |
| Didático | Todo módulo com ≥ 3 exemplos interativos em examples/ | Manual review |
| Paper | Arquitetura descrita em paper submetível a SBMF ou SBC | LaTeX draft |

---

## 15. Estimativa de Esforço

| Fase | Sprints | Duração | Complexidade | Paralelizável? |
|---|---|---|---|---|
| A — Álgebra Abstrata | 21–26 | 12 semanas | Alta | Não (dependência linear) |
| B — ℝ, ℂ, Análise | 27–32 | 12 semanas | Muito Alta | Parcial (ℝ sequencial, análise paralela) |
| C — LinAlg + NTheory + Grafos | 33–38 | 12 semanas | Média-Alta | **Sim** (3 frentes independentes) |
| D — Topologia + Medida + DiffGeo | 39–48 | 20 semanas | Muito Alta | **Sim** (4 frentes independentes) |
| E — Meta-Fundações | 21–24 | 8 semanas | Alta | **Sim** (paralelo à Fase A) |
| F — Solvers + Verificação | 25–30 | 12 semanas | Alta | **Sim** (paralelo às Fases A-B) |
| G — Symbolic + Release | 49–56 | 16 semanas | Média | Parcial |

**Total:** 56 sprints × 2 semanas = ~112 semanas ≈ **26 meses** (sequencial)
**Com paralelização (Fases E/F em paralelo, Fase C em 3 frentes):** ~18 meses

---

## 16. Referências Bibliográficas da Expansão

### Álgebra Abstrata
[A1] Hungerford, T.W. *Algebra*. Springer, 1974.
[A2] Lang, S. *Algebra*. 3rd ed. Springer, 2002.
[A3] Artin, M. *Algebra*. 2nd ed. Pearson, 2011.
[A4] Domingues, H.H.; Iezzi, G. *Álgebra Moderna*. 4ª ed. Atual, 2003.
[A5] Gonçalves, A. *Introdução à Álgebra*. 5ª ed. IMPA, 2013.

### Análise Real e Complexa
[B1] Rudin, W. *Principles of Mathematical Analysis*. 3rd ed. McGraw-Hill, 1976.
[B2] Lima, E.L. *Curso de Análise Vol. 1*. 14ª ed. IMPA, 2012.
[B3] Spivak, M. *Calculus*. 4th ed. Publish or Perish, 2008.
[B4] Ahlfors, L.V. *Complex Analysis*. 3rd ed. McGraw-Hill, 1979.

### Álgebra Linear
[C1] Hoffman, K.; Kunze, R. *Linear Algebra*. 2nd ed. Prentice-Hall, 1971.
[C2] Axler, S. *Linear Algebra Done Right*. 4th ed. Springer, 2024.
[C3] Lima, E.L. *Álgebra Linear*. 9ª ed. IMPA, 2016.
[C4] Strang, G. *Linear Algebra and Its Applications*. 4th ed. Thomson, 2006.

### Teoria dos Números
[D1] Hardy, G.H.; Wright, E.M. *An Introduction to the Theory of Numbers*. 6th ed. Oxford, 2008.
[D2] Ireland, K.; Rosen, M. *A Classical Introduction to Modern Number Theory*. 2nd ed. Springer, 1990.
[D3] Shoup, V. *A Computational Introduction to Number Theory and Algebra*. Cambridge, 2009.
[D4] Silverman, J.H. *The Arithmetic of Elliptic Curves*. 2nd ed. Springer, 2009.
[D5] Santos, J.P.O. *Introdução à Teoria dos Números*. IMPA, 2006.

### Topologia
[E1] Munkres, J.R. *Topology*. 2nd ed. Prentice Hall, 2000.
[E2] Hatcher, A. *Algebraic Topology*. Cambridge, 2001.
[E3] Lima, E.L. *Elementos de Topologia Geral*. SBM, 2009.

### Teoria da Medida e Probabilidade
[F1] Folland, G.B. *Real Analysis*. 2nd ed. Wiley, 1999.
[F2] Billingsley, P. *Probability and Measure*. 3rd ed. Wiley, 1995.
[F3] Bartle, R.G. *The Elements of Integration and Lebesgue Measure*. Wiley, 1995.

### Geometria Diferencial
[G1] do Carmo, M.P. *Differential Geometry of Curves and Surfaces*. 2nd ed. Dover, 2016.
[G2] Lee, J.M. *Introduction to Smooth Manifolds*. 2nd ed. Springer, 2012.
[G3] do Carmo, M.P. *Geometria Riemanniana*. 5ª ed. IMPA, 2011.

### Teoria das Categorias
[H1] Mac Lane, S. *Categories for the Working Mathematician*. 2nd ed. Springer, 1998.
[H2] Awodey, S. *Category Theory*. 2nd ed. Oxford, 2010.
[H3] Riehl, E. *Category Theory in Context*. Dover, 2016.

### Teoria dos Tipos
[I1] Martin-Löf, P. *Intuitionistic Type Theory*. Bibliopolis, 1984.
[I2] Univalent Foundations Program. *Homotopy Type Theory*. IAS, 2013.

### Computabilidade
[J1] Sipser, M. *Introduction to the Theory of Computation*. 3rd ed. Cengage, 2012.
[J2] Cutland, N.J. *Computability*. Cambridge, 1980.

### Teoria dos Modelos
[K1] Marker, D. *Model Theory: An Introduction*. Springer, 2002.
[K2] Hodges, W. *A Shorter Model Theory*. Cambridge, 1997.

### Álgebra Computacional
[L1] Cox, D.; Little, J.; O'Shea, D. *Ideals, Varieties, and Algorithms*. 4th ed. Springer, 2015.
[L2] Von zur Gathen, J.; Gerhard, J. *Modern Computer Algebra*. 3rd ed. Cambridge, 2013.
[L3] Geddes, K.O.; Czapor, S.R.; Labahn, G. *Algorithms for Computer Algebra*. Springer, 1992.

### Grafos
[M1] Diestel, R. *Graph Theory*. 5th ed. Springer, 2017.
[M2] Bondy, J.A.; Murty, U.S.R. *Graph Theory*. Springer, 2008.

### Verificação Formal e Proof Assistants
[N1] Hupel, L.; Kuncak, V. "Translating Scala Programs to Isabelle/HOL." IJCAR 2016.
[N2] De Moura, L.; Bjørner, N. "Z3: An Efficient SMT Solver." TACAS 2008.
[N3] Mathlib Community. *Mathlib4*. github.com/leanprover-community/mathlib4.
[N4] Nipkow, T.; Klein, G. *Concrete Semantics with Isabelle/HOL*. Springer, 2014.

### Álgebra Homológica
[O1] Rotman, J.J. *An Introduction to Homological Algebra*. 2nd ed. Springer, 2009.
[O2] Weibel, C.A. *An Introduction to Homological Algebra*. Cambridge, 1994.

### Análise Funcional
[P1] Kreyszig, E. *Introductory Functional Analysis with Applications*. Wiley, 1978.
[P2] Brezis, H. *Functional Analysis, Sobolev Spaces and PDEs*. Springer, 2011.

### Ecossistema e Arquitetura de Software Matemático
[Q1] Postovalov, I. et al. "KMath: Compilation of mathematical expressions in Kotlin." arXiv:2102.07924, 2021.
[Q2] Meurer, A. et al. "SymPy: symbolic computing in Python." PeerJ Computer Science 3:e103, 2017.
[Q3] The GAP Group. *GAP — Groups, Algorithms, Programming*. www.gap-system.org.
[Q4] Grayson, D.; Stillman, M. *Macaulay2*. macaulay2.com.
