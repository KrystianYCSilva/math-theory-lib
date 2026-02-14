# DOCUMENTATION.md — `mathsets-kt`

## Documentação Técnica e Teórica

**Versão:** 1.0.0-draft  
**Última atualização:** 2026-02-14

---

## Sumário

1. [Sistemas Numéricos Primitivos (Kernel)](#1-sistemas-numéricos-primitivos-kernel)
2. [Lógica de Primeira Ordem](#2-lógica-de-primeira-ordem)
3. [Axiomas de Peano e os Números Naturais](#3-axiomas-de-peano-e-os-números-naturais)
4. [Teoria Axiomática dos Conjuntos (ZFC)](#4-teoria-axiomática-dos-conjuntos-zfc)
5. [Relações, Funções e Ordens](#5-relações-funções-e-ordens)
6. [Equipolência e Cardinalidade](#6-equipolência-e-cardinalidade)
7. [Construção dos Conjuntos Numéricos](#7-construção-dos-conjuntos-numéricos)
8. [Ordinais e Aritmética Transfinita](#8-ordinais-e-aritmética-transfinita)
9. [Hipótese do Contínuo e Axioma da Escolha](#9-hipótese-do-contínuo-e-axioma-da-escolha)
10. [Tópicos Avançados](#10-tópicos-avançados)
11. [Referências Bibliográficas](#11-referências-bibliográficas)

---

## 1. Sistemas Numéricos Primitivos (Kernel)

### 1.1 Motivação e Papel Arquitetural

O módulo `kernel/` fornece tipos numéricos computacionais que servem como base
operacional para toda a biblioteca. Esses tipos **não são** a construção axiomática
dos números (que será feita no módulo `construction/`), mas sim **primitivos
computacionais** — ferramentas para que a máquina funcione.

Esta separação reflete a prática matemática padrão: o matemático usa ℕ, ℤ, ℚ
no dia-a-dia sem referência à construção conjuntista, e separadamente demonstra
que esses objetos podem ser fundamentados em conjuntos. Como observa Enderton:

> "We will construct, within set theory, structures that 'are' the natural numbers
> [...] But you need not think that mathematicians walk around with the construction
> in their heads."
>
> — Enderton, H.B. *Elements of Set Theory*, Academic Press, 1977, p. 66.

### 1.2 Números Naturais — `NaturalNumber`

Wrapper sobre `BigInteger` (sem limite de tamanho) com aritmética básica.

**Definição computacional:** ℕ = {0, 1, 2, 3, ...} como tipo com aritmética
fechada sob adição e multiplicação.

**Propriedades garantidas pelo tipo:**
- Não-negatividade (enforcement no construtor).
- Aritmética total: `a + b` e `a × b` sempre definidas em ℕ.
- Ordem total: `compareTo` implementa ≤ usual.
- Subtração parcial: `a - b` definida apenas quando `a ≥ b`.

### 1.3 Números Inteiros — `IntegerNumber`

Wrapper sobre `BigInteger` com aritmética de anel comutativo.

**Propriedades algébricas:** (ℤ, +, ×) forma um **domínio de integridade**:
anel comutativo com unidade onde `a × b = 0 → a = 0 ∨ b = 0`.

> Referência: Hungerford, T.W. *Algebra*. Springer, 1974, Ch. III, §1.
> Domingues, H.H.; Iezzi, G. *Álgebra Moderna*. 4ª ed. Atual, 2003, Cap. 4.

### 1.4 Números Racionais — `RationalNumber`

Par `(numerador, denominador)` com normalização automática via `gcd`.

**Propriedades algébricas:** (ℚ, +, ×) forma um **corpo ordenado**:
corpo comutativo com ordem total compatível com as operações.

**Normalização canônica:** Cada racional é armazenado na forma `p/q` com
`gcd(|p|, |q|) = 1` e `q > 0`. Isso garante representação única, essencial
para que a igualdade por valor funcione corretamente.

> Referência: Enderton (1977, Ch. 5): "The rationals as equivalence classes".

### 1.5 Geradores de Sequências Infinitas

Os geradores produzem `Sequence<T>` infinitos que enumeram elementos sob demanda,
sem materialização. São a base para que conjuntos intensionais (`IntensionalSet`)
possam iterar sobre domínios infinitos.

**Enumeração de ℤ (zigzag):** A bijeção canônica ℕ → ℤ é:
`0 ↦ 0, 1 ↦ 1, 2 ↦ -1, 3 ↦ 2, 4 ↦ -2, ...`

Formalmente: `f(2k) = k` e `f(2k+1) = -(k+1)` para `k ≥ 0`.

**Enumeração de ℚ (Cantor pairing):** Utiliza a função de emparelhamento de Cantor
para percorrer pares `(p, q)` com `gcd(p, q) = 1`, `q > 0`, em uma espiral que
cobre todos os racionais sem repetição.

> Referência: Cantor, G. "Über eine Eigenschaft des Inbegriffes aller reellen
> algebraischen Zahlen." *Journal für die reine und angewandte Mathematik*, 77,
> 258–262, 1874. A demonstração construtiva da enumerabilidade de ℚ aparece
> em forma moderna em Enderton (1977, §6.4, Theorem 6B).

### 1.6 Predicados e Combinadores

`Predicate<T>` é um `typealias` para `(T) -> Boolean` com extension functions
para composição lógica, correspondendo à álgebra booleana de propriedades.

Combinadores disponíveis: `and`, `or`, `not`, `implies`, `iff`.

---

## 2. Lógica de Primeira Ordem

### 2.1 Linguagem Formal

A Teoria dos Conjuntos é formulada na linguagem da **lógica de primeira ordem com
igualdade** (FOL=), estendida com um único símbolo de relação binária: ∈ (pertinência).

**Alfabeto:**
- Variáveis: `x, y, z, A, B, C, ...`
- Conectivos: `¬, ∧, ∨, →, ↔`
- Quantificadores: `∀, ∃`
- Relações: `∈, =`
- Pontuação: `(, ), ,`

**Fórmulas bem-formadas (wff):** definidas indutivamente:
- **Atômicas:** `t₁ ∈ t₂` e `t₁ = t₂` são fórmulas.
- **Compostas:** Se φ e ψ são fórmulas, então `¬φ`, `φ ∧ ψ`, `φ ∨ ψ`, `φ → ψ`, `φ ↔ ψ` são fórmulas.
- **Quantificadas:** Se φ é fórmula e x é variável, então `∀x φ` e `∃x φ` são fórmulas.

> Referência: Enderton, H.B. *A Mathematical Introduction to Logic*. 2nd ed.
> Academic Press, 2001, Ch. 2. Para a linguagem específica da teoria dos conjuntos:
> Kunen (1980, §I.1) e Jech (2003, §1.1).

### 2.2 Implementação: AST de Fórmulas

A representação computacional utiliza uma **Árvore Sintática Abstrata** (AST)
modelada como `sealed interface` em Kotlin. Cada nó da AST corresponde a uma
regra de formação da gramática de FOL.

O `sealed interface Formula` garante que o universo de fórmulas é fechado (não
pode ser estendido externamente), refletindo a definição indutiva formal.

### 2.3 Model Checking

Dado um **universo finito** U e uma **interpretação** I (que atribui significado
aos símbolos da linguagem), o `ModelChecker` avalia se uma fórmula é verdadeira
no modelo (U, I).

**Complexidade:** Para fórmulas com k quantificadores aninhados sobre universo de
tamanho n, a avaliação é O(nᵏ) no pior caso — cada quantificador universal itera
sobre todo o universo.

> Referência: A semântica de Tarski para FOL é descrita em Enderton (2001, §2.2).
> O problema de model checking para FOL sobre estruturas finitas é tratado em
> Libkin, L. *Elements of Finite Model Theory*. Springer, 2004, Ch. 1.

---

## 3. Axiomas de Peano e os Números Naturais

### 3.1 Os Axiomas de Peano

Os axiomas de Peano (na forma de Dedekind-Peano) definem a estrutura dos números
naturais. Um **sistema de Peano** é uma tripla (N, 0, S) onde:

- **P1 (Existência do zero):** 0 ∈ N.
- **P2 (Sucessor):** Para todo n ∈ N, S(n) ∈ N.
- **P3 (Injetividade):** S(m) = S(n) → m = n.
- **P4 (Zero não é sucessor):** Para todo n ∈ N, S(n) ≠ 0.
- **P5 (Indução):** Se X ⊆ N, 0 ∈ X e (n ∈ X → S(n) ∈ X), então X = N.

> Referência primária: Dedekind, R. *Was sind und was sollen die Zahlen?*
> Braunschweig, 1888. Tradução inglesa: "The Nature and Meaning of Numbers",
> in *Essays on the Theory of Numbers*, Dover, 1963.
>
> Referência moderna: Enderton (1977, Ch. 4, §4.1, Peano's Postulates).
> Também: Feitosa, H.A.; Nascimento, M.C.; Alfonso, A.B. *Teoria dos Conjuntos:
> sobre a fundamentação matemática e a construção dos conjuntos numéricos*.
> Ciência Moderna, 2011, Cap. 5.

### 3.2 Definição por Recursão em ℕ

O **Teorema da Recursão** garante que as operações aritméticas podem ser definidas
unicamente por recursão sobre os naturais:

**Adição:**
- a + 0 = a
- a + S(b) = S(a + b)

**Multiplicação:**
- a × 0 = 0
- a × S(b) = (a × b) + a

**Exponenciação:**
- a⁰ = 1 (para a ≠ 0)
- a^S(b) = a^b × a

> Referência: Enderton (1977, §4.3, Theorem 4C — Recursion Theorem).
> A demonstração de que estas definições recursivas são as únicas possíveis
> compatíveis com os axiomas é dada em Halmos (1960, §12).

### 3.3 A Interface `PeanoSystem<N>`

A interface `PeanoSystem<N>` codifica os axiomas como métodos verificáveis:

- `zero: N` — P1
- `succ(n: N): N` — P2
- `verifyInjectivity(a, b)` — P3 (testável para pares específicos)
- `verifyZeroNotSuccessor()` — P4
- `recursion(base, step)` — P5 (como higher-order function)

O princípio de indução (P5) é codificado como **recursão**: dada uma base e um
passo, produz uma função definida sobre todos os naturais. Isso corresponde à
equivalência entre indução (princípio de prova) e recursão (princípio de definição).

> Referência: Feitosa, Nascimento e Alfonso (2011, §5.3: "Recursão em ℕ").

---

## 4. Teoria Axiomática dos Conjuntos (ZFC)

### 4.1 Histórico

A Teoria dos Conjuntos foi iniciada por Georg Cantor em 1874 com seu artigo
demonstrando que o conjunto dos números algébricos é enumerável mas o conjunto
dos números reais não é. A teoria "ingênua" de Cantor foi formalizada
axiomaticamente após a descoberta de paradoxos (Russell, 1901; Burali-Forti, 1897).

O sistema axiomático padrão foi desenvolvido por Ernst Zermelo (1908) e
aperfeiçoado por Abraham Fraenkel (1921) e Thoralf Skolem (1922), resultando
no sistema **ZFC** (Zermelo-Fraenkel com Axioma da Escolha).

> Referências históricas:
> - Cantor, G. "Über eine Eigenschaft des Inbegriffes aller reellen algebraischen
>   Zahlen." *J. reine angew. Math.*, 77, 258–262, 1874.
> - Zermelo, E. "Untersuchungen über die Grundlagen der Mengenlehre I."
>   *Math. Annalen*, 65, 261–281, 1908.
> - Fraenkel, A. "Zu den Grundlagen der Cantor-Zermeloschen Mengenlehre."
>   *Math. Annalen*, 86, 230–237, 1922.
> - Russell, B. "Letter to Frege" (1902), publicada em van Heijenoort, J.
>   *From Frege to Gödel*. Harvard University Press, 1967.

### 4.2 Os Axiomas de ZFC

O sistema ZFC consiste em 9 axiomas (incluindo dois esquemas axiomáticos) na
linguagem da lógica de primeira ordem com ∈ como único símbolo de relação
(além de =).

#### Axioma 1: Extensionalidade

`∀A ∀B [∀x (x ∈ A ↔ x ∈ B) → A = B]`

Dois conjuntos são iguais se e somente se possuem os mesmos elementos. Este axioma
é a base da identidade conjuntista e fundamenta a distinção extensional/intensional.

**Implementação:** `MathSet.equals()` e `ExtensionalSet` (identidade por elementos).

> Referência: Halmos (1960, §1: "The Axiom of Extension").

#### Axioma 2: Conjunto Vazio

`∃∅ ∀x (x ∉ ∅)`

Existe um conjunto que não contém nenhum elemento. Pela Extensionalidade, esse
conjunto é único.

**Implementação:** `EmptySet` — `object` singleton.

> Referência: Halmos (1960, §2: "The Axiom of Specification") deduz ∅ a partir
> da Separação. Enderton (1977, §2.1) postula ∅ separadamente.

#### Axioma 3: Par (Pairing)

`∀a ∀b ∃C ∀x (x ∈ C ↔ x = a ∨ x = b)`

Para quaisquer a e b, existe o conjunto {a, b}.

**Implementação:** `MathSet.pair(a, b)`.

#### Axioma 4: União (Union)

`∀S ∃U ∀x (x ∈ U ↔ ∃A (A ∈ S ∧ x ∈ A))`

Para qualquer coleção S de conjuntos, existe a união de todos os seus membros.

**Implementação:** `MathSet.union()`.

> Referência: Halmos (1960, §4: "Unions and Intersections").

#### Axioma 5: Conjunto Potência (Power Set)

`∀S ∃P ∀X (X ∈ P ↔ X ⊆ S)`

Para qualquer conjunto S, existe o conjunto de todos os subconjuntos de S.

**Implementação:** `MathSet.powerSet()` — lazy, via `PowerSet<T>`.

**Nota computacional:** |P(S)| = 2^|S|. Para |S| = 20, P(S) tem mais de um
milhão de elementos. A implementação lazy é essencial.

> Referência: Halmos (1960, §5: "Powers"). Enderton (1977, §2.3).

#### Axioma 6: Infinito (Infinity)

`∃I (∅ ∈ I ∧ ∀x (x ∈ I → x ∪ {x} ∈ I))`

Existe um conjunto **indutivo** que contém ∅ e é fechado sob a operação
sucessor `x ↦ x ∪ {x}`. O menor tal conjunto é ω (os naturais).

**Implementação:** `Naturals` — `object` que gera `Sequence<NaturalNumber>` infinita.

> Referência: Enderton (1977, §4.1). Kunen (1980, §I.6).

#### Axioma 7: Esquema da Separação (Comprehension / Specification)

`∀A ∃B ∀x (x ∈ B ↔ x ∈ A ∧ φ(x))`

Para qualquer conjunto A e fórmula φ, existe {x ∈ A | φ(x)}.

**Implementação:** `MathSet.filter(predicate)` → `IntensionalSet<T>`.

Este é o axioma computacionalmente mais significativo: é exatamente a construção
intensional de conjuntos. O predicado φ corresponde ao parâmetro `predicate: (T) -> Boolean`.

**Nota:** É um *esquema* axiomático — um axioma para cada fórmula φ da linguagem.
Computacionalmente, isso corresponde a aceitar qualquer `(T) -> Boolean` como predicado.

> Referência: Halmos (1960, §2). A restrição a um conjunto base A (em vez de
> {x | φ(x)} irrestrito) é o que evita o Paradoxo de Russell. Ver Enderton
> (1977, §2.1) para a discussão histórica.

#### Axioma 8: Esquema da Substituição (Replacement)

Se F é uma operação definível (classe funcional), então para todo conjunto A,
{F(x) | x ∈ A} é um conjunto.

**Implementação:** `MathSet.map(f)`.

> Referência: Kunen (1980, §I.4). Jech (2003, §1.4).

#### Axioma 9: Escolha (Choice)

Para toda família de conjuntos não-vazios, existe uma função que seleciona um
elemento de cada conjunto.

**Implementação:** `ChoiceFunction` — trivial computacionalmente para conjuntos
finitos (qualquer seleção serve), mas profundamente não-trivial na teoria.

> Referência: A história do Axioma da Escolha e suas equivalências (Lema de Zorn,
> Princípio da Boa-Ordem) é narrada em Moore, G.H. *Zermelo's Axiom of Choice:
> Its Origins, Development, and Influence*. Springer, 1982.
> Ver também: Bell, J.L. "The Axiom of Choice." *Stanford Encyclopedia of
> Philosophy*, 2008 (atualizado 2021).

#### Axioma 10: Fundação / Regularidade

Todo conjunto não-vazio A contém um elemento disjunto de A.

`∀A (A ≠ ∅ → ∃x (x ∈ A ∧ x ∩ A = ∅))`

Impede cadeias infinitas descendentes de pertinência (... ∈ c ∈ b ∈ a) e
garante que a relação ∈ é bem-fundada.

**Implementação:** Garantido pela estrutura de dados — Kotlin não permite
referências circulares em `data class`es imutáveis.

> Referência: Kunen (1980, §I.5). Jech (2003, §1.7).

### 4.3 Outros Sistemas Axiomáticos

A biblioteca reconhece (e o módulo `logic/AxiomSystem` cataloga) sistemas
alternativos:

**NBG (Von Neumann–Bernays–Gödel):** Extensão conservativa de ZFC que permite
falar de **classes próprias** (coleções "grandes demais" para serem conjuntos,
como a classe de todos os conjuntos). É finitamente axiomatizável (diferente
de ZFC, que tem esquemas infinitos).

> Referência: Mendelson, E. *Introduction to Mathematical Logic*. 6th ed.
> CRC Press, 2015, Ch. 4.

**KM (Kelley–Morse):** Mais forte que NBG — permite compreensão sobre classes
com quantificação sobre classes. Usada como base no plano de ensino da UFSC
(Prof. Arthur Buchsbaum, FIL5705).

> Referência: Morse, A.P. *A Theory of Sets*. 2nd ed. Academic Press, 1986.
> Kelley, J.L. *General Topology*. Van Nostrand, 1955, Appendix (axiomas de KM).

**NF (New Foundations de Quine):** Evita paradoxos via *estratificação* de
fórmulas, sem o axioma da Fundação. Permite um conjunto universal.

> Referência: Quine, W.V. "New Foundations for Mathematical Logic."
> *American Mathematical Monthly*, 44, 70–80, 1937.
> Holmes, M.R. *Elementary Set Theory with a Universal Set*. Bruylant-Academia, 1998.

### 4.4 Paradoxos e sua Resolução

Os paradoxos da teoria ingênua motivaram a axiomatização:

**Paradoxo de Russell (1901):** Considere S = {x | x ∉ x}. Então S ∈ S ↔ S ∉ S.
Em ZFC, o Axioma da Separação impede essa construção: precisamos de um conjunto
base A para formar {x ∈ A | x ∉ x}, que é simplesmente A \ A = ∅.

**Paradoxo de Burali-Forti (1897):** O "conjunto de todos os ordinais" Ω seria
um ordinal maior que todos os ordinais — contradição. Em ZFC, Ω é uma **classe
própria**, não um conjunto.

**Paradoxo de Cantor:** O "conjunto de todos os conjuntos" V levaria a |V| < |P(V)|,
mas P(V) ⊆ V, contradição. Novamente, V é classe própria.

> Referência: van Heijenoort, J. *From Frege to Gödel: A Source Book in
> Mathematical Logic, 1879–1931*. Harvard University Press, 1967.
> Para tratamento pedagógico: Halmos (1960, §1, "Naive Set Theory and its problems").

### 4.5 Mapeamento Axiomas → Implementação

| Axioma | Implementação | Módulo |
|---|---|---|
| Extensionalidade | `MathSet.equals()`, `ExtensionalSet` | `set/` |
| Conjunto Vazio | `EmptySet` (object singleton) | `set/` |
| Par | `MathSet.pair(a, b)` | `set/` |
| União | `MathSet.union()` | `set/` |
| Potência | `MathSet.powerSet()` → `PowerSet<T>` (lazy) | `set/` |
| Infinito | `Naturals`, `Integers`, `Rationals` (geradores infinitos) | `set/` |
| Separação | `MathSet.filter(predicate)` → `IntensionalSet<T>` | `set/` |
| Substituição | `MathSet.map(f)` → `MappedSet<T,R>` | `set/` |
| Escolha | `ChoiceFunction` | `function/` |
| Fundação | Imutabilidade estrutural (sem ciclos ∈) | (design) |

---

## 5. Relações, Funções e Ordens

### 5.1 Par Ordenado

O par ordenado (a, b) é definido via a construção de Kuratowski:

`(a, b) = {{a}, {a, b}}`

Esta definição garante a propriedade fundamental: `(a, b) = (c, d) ↔ a = c ∧ b = d`.

> Referência: Kuratowski, C. "Sur la notion de l'ordre dans la théorie des
> ensembles." *Fundamenta Mathematicae*, 2, 161–171, 1921.
> Apresentação moderna: Enderton (1977, §3.1, Theorem 3A).

### 5.2 Produto Cartesiano

`A × B = {(a, b) | a ∈ A ∧ b ∈ B}`

Implementado como `MathSet<OrderedPair<A, B>>` com iteração lazy.

**Cardinalidade:** |A × B| = |A| · |B|.

### 5.3 Relações Binárias

Uma relação binária R de A em B é um subconjunto de A × B:

`R ⊆ A × B`

**Propriedades verificáveis** (para R ⊆ A × A):

| Propriedade | Definição Formal | Verificação |
|---|---|---|
| Reflexiva | ∀a ∈ A: (a,a) ∈ R | O(n) |
| Simétrica | (a,b) ∈ R → (b,a) ∈ R | O(|R|) |
| Transitiva | (a,b),(b,c) ∈ R → (a,c) ∈ R | O(|R|²) |
| Antissimétrica | (a,b),(b,a) ∈ R → a = b | O(|R|) |
| Irreflexiva | ∀a ∈ A: (a,a) ∉ R | O(n) |
| Tricotômica | ∀a,b: (a,b) ∈ R ∨ a = b ∨ (b,a) ∈ R | O(n²) |
| Conexa | ∀a≠b: (a,b) ∈ R ∨ (b,a) ∈ R | O(n²) |

> Referência: Enderton (1977, §3.2). Para a classificação exaustiva de propriedades
> de relações: Halmos (1960, §7: "Relations").
> Plano de ensino UFSC (FIL5705, Prof. Buchsbaum): relações notáveis com tratamento
> detalhado de cada propriedade e suas interações.

### 5.4 Relações de Equivalência e Partições

Uma **relação de equivalência** é reflexiva, simétrica e transitiva.

Dada R sobre A, a **classe de equivalência** de a é [a] = {x ∈ A | xRa}.

O **conjunto quociente** é A/R = {[a] | a ∈ A}.

**Teorema Fundamental:** Existe uma bijeção canônica entre relações de equivalência
sobre A e partições de A.

> Referência: Enderton (1977, §3.4, Theorem 3Q). Halmos (1960, §7).
> Plano de ensino UFSC: "correspondência biunívoca entre relações de equivalência
> e partições."

### 5.5 Relações de Ordem

Uma **ordem parcial** é reflexiva, antissimétrica e transitiva.

Uma **ordem total** (linear) é uma ordem parcial conexa.

Uma **boa-ordem** é uma ordem total onde todo subconjunto não-vazio tem mínimo.

**Elementos especiais em (A, ≤):**

| Elemento | Definição |
|---|---|
| Mínimo | a ∈ A: ∀x ∈ A, a ≤ x |
| Máximo | a ∈ A: ∀x ∈ A, x ≤ a |
| Minimal | a ∈ A: ¬∃x ∈ A, x < a |
| Maximal | a ∈ A: ¬∃x ∈ A, a < x |
| Supremo de S | menor cota superior de S |
| Ínfimo de S | maior cota inferior de S |
| Sucessor de a | menor b tal que b > a (se existir) |

> Referência: Hrbacek, K.; Jech, T. *Introduction to Set Theory*. 3rd ed.
> Marcel Dekker, 1999, Ch. 2.
> Plano de ensino UFSC: tratamento detalhado de todos os elementos especiais.

### 5.6 Funções

Uma **função** f: A → B é uma relação funcional: para cada a ∈ A, existe
exatamente um b ∈ B tal que (a, b) ∈ f.

| Tipo de Função | Definição |
|---|---|
| Injetora | f(a₁) = f(a₂) → a₁ = a₂ |
| Sobrejetora | ∀b ∈ B, ∃a ∈ A: f(a) = b |
| Bijetora | Injetora e sobrejetora |

**Composição:** (g ∘ f)(x) = g(f(x)). Associativa mas não comutativa.

**Inversa:** Se f é bijetora, existe f⁻¹: B → A com f⁻¹ ∘ f = id_A e f ∘ f⁻¹ = id_B.

> Referência: Enderton (1977, §3.3–3.5).
> Plano de ensino UNESP: §4.3 (Funções), ementa UNICAMP: §4 (Funções em ZF).

---

## 6. Equipolência e Cardinalidade

### 6.1 Equipolência

Dois conjuntos A e B são **equipolentes** (ou equinumerosos) se existe uma
bijeção f: A → B. Escrevemos A ≈ B ou |A| = |B|.

A equipolência é uma relação de equivalência sobre a classe de todos os conjuntos.

> Referência: Enderton (1977, §6.1). Plano de ensino UNICAMP: §4 (Equipolência).

### 6.2 Conjuntos Finitos e Infinitos

Um conjunto é **finito** se é equipolente a algum {0, 1, ..., n-1} (para n ∈ ω).
É **infinito** caso contrário.

**Definição de Dedekind:** Um conjunto é *Dedekind-infinito* se é equipolente a
um subconjunto próprio de si mesmo. Com o Axioma da Escolha, Dedekind-infinito ↔
infinito.

> Referência: Enderton (1977, §6.2). Plano de ensino UNICAMP: §5 (Conjuntos
> finitos e infinitos). Plano de ensino UNESP: §4.4 (Conjuntos infinitos).

### 6.3 Cardinalidade

A **cardinalidade** |A| de um conjunto A é a classe de equivalência de A sob ≈.
Para conjuntos finitos, |A| = n. Para infinitos, utilizamos os **números cardinais**
de Cantor: ℵ₀, ℵ₁, ℵ₂, ...

**Teorema de Cantor:** |A| < |P(A)| para todo conjunto A.

A demonstração é construtiva (diagonal): dada qualquer f: A → P(A), o conjunto
D = {x ∈ A | x ∉ f(x)} não está em Im(f). Na implementação, `CantorDiagonal`
constrói D explicitamente.

> Referência: Cantor, G. "Über eine elementare Frage der Mannigfaltigkeitslehre."
> *Jahresbericht der Deutschen Mathematiker-Vereinigung*, 1, 75–78, 1891.
> Enderton (1977, §6.3, Theorem 6D — Cantor's Theorem).

### 6.4 Aritmética Cardinal

Para cardinais infinitos:

| Operação | Resultado |
|---|---|
| ℵ₀ + ℵ₀ | ℵ₀ |
| ℵ₀ × ℵ₀ | ℵ₀ |
| 2^ℵ₀ | 𝔠 (cardinalidade do contínuo) |
| ℵ₀^ℵ₀ | 𝔠 |

> Referência: Jech (2003, Ch. 5: "Cardinal Arithmetic"). Enderton (1977, §6.4).
> Plano de ensino UNICAMP: §11 (Aritmética Cardinal).

### 6.5 Enumerabilidade

Um conjunto é **enumerável** (ou contável) se é finito ou equipolente a ℕ.

**Resultados implementados:**
- ℤ é enumerável (bijeção construtiva via zigzag).
- ℚ é enumerável (bijeção construtiva via Cantor pairing).
- ℝ **não é** enumerável (argumento diagonal de Cantor).
- Q(√2), ℝ algébricos são enumeráveis (Cantor, 1874).

> Referência: Enderton (1977, §6.4, Theorems 6A–6C).

---

## 7. Construção dos Conjuntos Numéricos

### 7.1 Construção de ℕ (Von Neumann)

Os números naturais são construídos como conjuntos:

```
0 = ∅
1 = {∅} = {0}
2 = {∅, {∅}} = {0, 1}
3 = {∅, {∅}, {∅, {∅}}} = {0, 1, 2}
n = {0, 1, 2, ..., n-1}
```

O **sucessor** é S(n) = n ∪ {n}. O conjunto ω = {0, 1, 2, ...} é o menor
conjunto indutivo (Axioma do Infinito).

**Teorema:** (ω, ∅, S) satisfaz os Axiomas de Peano.

> Referência: Von Neumann, J. "Zur Einführung der transfiniten Zahlen."
> *Acta Scientiarum Mathematicarum (Szeged)*, 1, 199–208, 1923.
> Enderton (1977, §4.2). Halmos (1960, §11–12).
> Feitosa, Nascimento e Alfonso (2011, §5.1–5.2).

### 7.2 Construção de ℤ

ℤ é construído como quociente:

`ℤ = (ℕ × ℕ) / ~`

onde `(a, b) ~ (c, d) ↔ a + d = c + b`. O par (a, b) representa a − b.

**Aritmética:**
- [(a,b)] + [(c,d)] = [(a+c, b+d)]
- [(a,b)] × [(c,d)] = [(ac+bd, ad+bc)]
- −[(a,b)] = [(b,a)]

**Embedding canônico:** ℕ ↪ ℤ⁺ via n ↦ [(n, 0)].

> Referência: Enderton (1977, §5.1). Feitosa, Nascimento e Alfonso (2011, §6).
> Mendelson, E. *Number Systems and the Foundations of Analysis*. Academic Press,
> 1973, Ch. 2.

### 7.3 Construção de ℚ

ℚ é construído como quociente:

`ℚ = (ℤ × ℤ*) / ~`

onde ℤ* = ℤ \ {0} e `(a, b) ~ (c, d) ↔ a × d = c × b`. O par (a, b) representa a/b.

**Aritmética:**
- [(a,b)] + [(c,d)] = [(ad + bc, bd)]
- [(a,b)] × [(c,d)] = [(ac, bd)]
- [(a,b)]⁻¹ = [(b,a)] (quando a ≠ 0)

**Embedding canônico:** ℤ ↪ ℚ via z ↦ [(z, 1)].

**Propriedades de ℚ:**
- Corpo ordenado: (ℚ, +, ×, ≤) com todas as propriedades de corpo + ordem total.
- Densidade: entre quaisquer dois racionais existe outro.
- Enumerabilidade: |ℚ| = ℵ₀.
- Arquimedianidade: ∀q ∈ ℚ, ∃n ∈ ℕ: n > q.

> Referência: Enderton (1977, §5.2). Feitosa, Nascimento e Alfonso (2011, §7).
> Plano de ensino UNESP: §7 (Números Racionais), cobrindo aritmética, ordem
> usual e a inclusão de ℤ em ℚ.

### 7.4 A Torre de Embeddings

A cadeia de embeddings preserva as operações aritméticas:

```
ℕ ↪ ℤ ↪ ℚ

n ↦ [(n, 0)] ↦ [([(n, 0)], [(1, 0)])]
```

Cada embedding é:
- **Injetor** — preserva distinção.
- **Homomorfismo de anel** — preserva + e ×.
- **Compatível com a ordem** — preserva ≤.

O módulo `isomorphism/` verifica computacionalmente estas propriedades,
demonstrando que os primitivos do `kernel` e as construções axiomáticas
produzem estruturas isomorfas.

---

## 8. Ordinais e Aritmética Transfinita

### 8.1 Números Ordinais

Um **ordinal** é um conjunto transitivo bem-ordenado por ∈. Na construção de
von Neumann, os ordinais são exatamente os conjuntos transitivos de conjuntos
transitivos.

Ordinais finitos coincidem com naturais. Os ordinais transfinitos começam com:

`ω, ω+1, ω+2, ..., ω·2, ..., ω², ..., ωω, ..., ε₀, ...`

> Referência: Kunen (1980, Ch. I, §6–7). Jech (2003, Ch. 2: "Ordinal Numbers").
> Hrbacek e Jech (1999, Ch. 6–7).
> Plano de ensino UNICAMP: §7–9 (Ordinais, Indução/Recursão Transfinita,
> Aritmética Ordinal).

### 8.2 Cantor Normal Form (CNF)

Todo ordinal α > 0 menor que ε₀ pode ser escrito unicamente na forma:

`α = ω^β₁ · c₁ + ω^β₂ · c₂ + ... + ω^βₖ · cₖ`

onde `β₁ > β₂ > ... > βₖ ≥ 0` e `0 < cᵢ < ω`.

**Implementação:** `CNF(terms: List<CNFTerm>)` com `CNFTerm(exponent: Ordinal, coefficient: NaturalNumber)`.

> Referência: Cantor, G. "Beiträge zur Begründung der transfiniten Mengenlehre."
> *Mathematische Annalen*, 49, 207–246, 1897.
> Apresentação moderna: Pohlers, W. *Proof Theory*. Springer, 2009, §1.5.

### 8.3 Aritmética Ordinal

A aritmética ordinal **não é comutativa**:

- `1 + ω = ω ≠ ω + 1`
- `2 × ω = ω ≠ ω × 2`

**Adição:** Concatenação de boas-ordens. Formalmente, `α + β` é o tipo de ordem
de `α ⊔ β` (soma disjunta com β "à direita" de α).

**Multiplicação:** Produto lexicográfico reverso. `α · β` é o tipo de ordem de
`β × α` com ordem lexicográfica da direita.

> Referência: Kunen (1980, §I.7). Jech (2003, §2.4).
> Plano de ensino UNICAMP: §9 (Aritmética Ordinal).

### 8.4 Indução e Recursão Transfinita

**Princípio de Indução Transfinita:** Se uma propriedade P vale para 0, e se
P(β) para todo β < α implica P(α), então P(α) vale para todo ordinal α.

**Princípio de Recursão Transfinita:** Permite definir funções sobre todos os
ordinais especificando: (1) o valor em 0, (2) o passo sucessor, (3) o passo limite.

> Referência: Enderton (1977, §4.4). Kunen (1980, §I.6, Theorem 6.4).
> Plano de ensino UNICAMP: §8 (Indução e Recursão Transfinita. Aplicações).

---

## 9. Hipótese do Contínuo e Axioma da Escolha

### 9.1 Hipótese do Contínuo (CH)

**CH:** Não existe cardinal entre ℵ₀ e 2^ℵ₀. Equivalentemente, ℵ₁ = 2^ℵ₀.

**GCH (Generalizada):** Para todo ordinal α, 2^ℵα = ℵα+1.

### 9.2 Independência

A CH é **independente de ZFC**: não pode ser provada nem refutada a partir dos
axiomas de ZFC.

- **Gödel (1940):** Construiu o **universo construtível** L e mostrou que
  ZFC + CH é consistente (se ZFC é consistente).
- **Cohen (1963):** Inventou a técnica de **forcing** e mostrou que
  ZFC + ¬CH é consistente (se ZFC é consistente).

> Referência:
> - Gödel, K. *The Consistency of the Axiom of Choice and of the Generalized
>   Continuum Hypothesis with the Axioms of Set Theory*. Princeton University
>   Press, 1940 (Annals of Mathematics Studies, no. 3).
> - Cohen, P.J. "The independence of the continuum hypothesis." *Proceedings
>   of the National Academy of Sciences*, 50(6), 1143–1148, 1963.
> - Cohen, P.J. *Set Theory and the Continuum Hypothesis*. W.A. Benjamin, 1966.
> - Kunen (1980, Ch. VII: "Forcing"). Jech (2003, Ch. 14–15).
> - Plano de ensino UNICAMP: §12 (CH e AC) e §14 (Consistência e Independência).

### 9.3 O Axioma da Escolha e suas Equivalências

O Axioma da Escolha (AC) é equivalente a:

- **Lema de Zorn:** Toda ordem parcial onde toda cadeia tem cota superior possui
  elemento maximal.
- **Teorema da Boa-Ordem (Zermelo):** Todo conjunto pode ser bem-ordenado.
- **Teorema de Tychonoff:** O produto de espaços topológicos compactos é compacto.
- **Todo espaço vetorial possui uma base.**

> Referência: Jech, T. *The Axiom of Choice*. North-Holland, 1973 (Dover reprint, 2008).
> Para equivalências: Herrlich, H. *Axiom of Choice*. Springer, 2006, Lecture Notes
> in Mathematics, v. 1876.

### 9.4 Axioma da Fundacionalidade e Construtibilidade

O **Axioma da Fundacionalidade** garante que a relação ∈ é bem-fundada: não
existem sequências infinitas descendentes ... ∈ a₂ ∈ a₁ ∈ a₀.

O **Axioma da Construtibilidade** (V = L) afirma que todo conjunto é construtível.
É mais forte que AC e CH (ambos seguem de V = L), mas a maioria dos teóricos de
conjuntos o considera restritivo demais.

> Referência: Gödel (1940). Kunen (1980, §VI: "Constructibility").
> Plano de ensino UNICAMP: §13 (Fundacionalidade e Construtibilidade).

---

## 10. Tópicos Avançados

### 10.1 Grandes Cardinais

A **hierarquia de grandes cardinais** estende a força de consistência de ZFC.
Cada nível da hierarquia adiciona axiomas que implicam a consistência de todos
os níveis inferiores.

Alguns níveis relevantes (em ordem crescente de força):
- **Inacessíveis:** κ regular e limite forte.
- **Mahlo:** Estacionariamente muitos inacessíveis abaixo.
- **Mensuráveis:** Possuem ultrafiltro κ-completo não-principal.
- **Woodin:** Essenciais para a teoria descritiva.
- **Supercompactos:** Implicam muitas propriedades combinatórias.

> Referência: Kanamori, A. *The Higher Infinite: Large Cardinals in Set Theory
> from Their Beginnings*. 2nd ed. Springer, 2003. É a referência padrão.
> Drake, F.R. *Set Theory: An Introduction to Large Cardinals*. North-Holland, 1974.

### 10.2 Teoria Descritiva de Conjuntos

Estuda as propriedades de complexidade de subconjuntos dos reais usando
ferramentas topológicas.

**Hierarquia de Borel:** Conjuntos construídos a partir de abertos por
complementação e união contável. Classificação em níveis Σ⁰ₙ e Π⁰ₙ.

**Determinância:** O Axioma da Determinância (AD) afirma que em jogos de
Gale-Stewart com payoff Borel, um dos jogadores tem estratégia vencedora.
AD contradiz AC mas é consistente com ZF.

> Referência: Kechris, A.S. *Classical Descriptive Set Theory*. Springer, 1995.
> Moschovakis, Y.N. *Descriptive Set Theory*. 2nd ed. AMS, 2009.

### 10.3 Forcing

A técnica de **forcing** de Cohen (1963) permite construir extensões de modelos
de ZFC adicionando "conjuntos genéricos." É o principal método para demonstrar
independência em teoria dos conjuntos.

**Intuição:** Dado um modelo M ⊨ ZFC e uma ordem parcial (P, ≤) em M, um
**filtro genérico** G sobre P (que intersecta todos os conjuntos densos) gera
uma extensão M[G] que também satisfaz ZFC, mas pode satisfazer ou não sentenças
adicionais (como CH) dependendo da escolha de P.

O módulo `forcing/` simula essa técnica sobre modelos finitos, onde:
- `Poset` representa a ordem parcial.
- `GenericFilter` constrói o filtro genérico.
- `ForcingExtension` constrói M[G].

> Referência: Cohen (1963, 1966). Kunen (1980, Ch. VII).
> Para exposição acessível: Chow, T.Y. "A beginner's guide to forcing."
> *Contemporary Mathematics*, 479, 25–40, 2009.
> Smullyan, R.; Fitting, M. *Set Theory and the Continuum Problem*.
> Clarendon Press, 1996, Parte III.

### 10.4 Combinatória Infinitária

**Teorema de Ramsey (versão finita):** Para todos r, k ≥ 2, existe N tal que
toda r-coloração das arestas de K_N contém um K_k monocromático.

O **número de Ramsey** R(s, t) é o menor N tal que toda 2-coloração de K_N
contém K_s vermelho ou K_t azul. Valores conhecidos são escassos:
R(3,3) = 6, R(3,4) = 9, R(3,5) = 14, R(4,4) = 18.

> Referência: Graham, R.L.; Rothschild, B.L.; Spencer, J.H. *Ramsey Theory*.
> 2nd ed. Wiley, 1990.
> Diestel, R. *Graph Theory*. 5th ed. Springer, 2017, Ch. 9.

---

## 11. Referências Bibliográficas

### 11.1 Referências Primárias (Teoria dos Conjuntos)

[1] **Halmos, P.R.** *Naive Set Theory*. Springer, 1960 (reprint 1974).
Introdução clássica à teoria "ingênua" dos conjuntos. Referência para
operações básicas, relações, funções e cardinalidade finita.

[2] **Enderton, H.B.** *Elements of Set Theory*. Academic Press, 1977.
Texto principal para a construção axiomática ZFC e a construção dos
conjuntos numéricos ℕ, ℤ, ℚ, ℝ. Referência primária das ementas UNESP e UNICAMP.

[3] **Kunen, K.** *Set Theory: An Introduction to Independence Proofs*.
North-Holland, 1980 (Studies in Logic, v. 102).
Referência padrão para forcing, independência e teoria avançada.

[4] **Jech, T.** *Set Theory: The Third Millennium Edition, Revised and Expanded*.
Springer, 2003 (Springer Monographs in Mathematics).
Tratado enciclopédico. Referência para grandes cardinais, forcing avançado,
teoria descritiva.

[5] **Hrbacek, K.; Jech, T.** *Introduction to Set Theory*. 3rd ed.
Marcel Dekker, 1999. Texto intermediário entre Halmos e Kunen.

### 11.2 Referências Primárias (Fundamentos e Lógica)

[6] **Enderton, H.B.** *A Mathematical Introduction to Logic*. 2nd ed.
Academic Press, 2001. Fundamentos de lógica de primeira ordem.

[7] **Mendelson, E.** *Introduction to Mathematical Logic*. 6th ed.
CRC Press, 2015. Inclui axiomática de NBG.

[8] **Feitosa, H.A.; Nascimento, M.C.; Alfonso, A.B.** *Teoria dos Conjuntos:
sobre a fundamentação matemática e a construção dos conjuntos numéricos*.
Rio de Janeiro: Ciência Moderna, 2011. Texto-base do curso de Teoria dos
Conjuntos da UNESP/Bauru.

### 11.3 Referências de Construção de Números

[9] **Dedekind, R.** *Was sind und was sollen die Zahlen?* Braunschweig, 1888.
Tradução: "The Nature and Meaning of Numbers" in *Essays on the Theory
of Numbers*, Dover, 1963.

[10] **Mendelson, E.** *Number Systems and the Foundations of Analysis*.
Academic Press, 1973. Construção axiomática de ℕ, ℤ, ℚ, ℝ.

[11] **Von Neumann, J.** "Zur Einführung der transfiniten Zahlen."
*Acta Sci. Math. (Szeged)*, 1, 199–208, 1923.

### 11.4 Referências de Tópicos Avançados

[12] **Kanamori, A.** *The Higher Infinite: Large Cardinals in Set Theory
from Their Beginnings*. 2nd ed. Springer, 2003.

[13] **Cohen, P.J.** *Set Theory and the Continuum Hypothesis*.
W.A. Benjamin, 1966.

[14] **Kechris, A.S.** *Classical Descriptive Set Theory*. Springer, 1995.

[15] **Graham, R.L.; Rothschild, B.L.; Spencer, J.H.** *Ramsey Theory*.
2nd ed. Wiley, 1990.

[16] **Moore, G.H.** *Zermelo's Axiom of Choice: Its Origins, Development,
and Influence*. Springer, 1982.

[17] **Jech, T.** *The Axiom of Choice*. North-Holland, 1973 (Dover, 2008).

### 11.5 Referências de Sistemas Alternativos

[18] **Morse, A.P.** *A Theory of Sets*. 2nd ed. Academic Press, 1986.

[19] **Kelley, J.L.** *General Topology*. Van Nostrand, 1955 (Appendix: KM axioms).

[20] **Quine, W.V.** "New Foundations for Mathematical Logic."
*American Mathematical Monthly*, 44, 70–80, 1937.

[21] **Holmes, M.R.** *Elementary Set Theory with a Universal Set*.
Bruylant-Academia, 1998.

### 11.6 Referências de Algoritmos e Computação

[22] **Cormen, T.H.; Leiserson, C.E.; Rivest, R.L.; Stein, C.** *Introduction
to Algorithms*. 4th ed. MIT Press, 2022. Estruturas de dados (hash tables,
BSTs, etc.) que fundamentam os backends de `MathSet`.

[23] **Knuth, D.E.** *The Art of Computer Programming, Volume 4A:
Combinatorial Algorithms, Part 1*. Addison-Wesley, 2011. Operações bitwise
e algoritmos combinatórios.

[24] **Libkin, L.** *Elements of Finite Model Theory*. Springer, 2004.
Fundamenta o model checking sobre estruturas finitas.

### 11.7 Referências Históricas

[25] **van Heijenoort, J.** *From Frege to Gödel: A Source Book in
Mathematical Logic, 1879–1931*. Harvard University Press, 1967.
Textos originais de Cantor, Zermelo, Russell, Fraenkel, Gödel.

[26] **Tiles, M.** *The Philosophy of Set Theory: An Historical Introduction
to Cantor's Paradise*. Dover, 2004.

[27] **Suppes, P.** *Axiomatic Set Theory*. Dover, 1972.

### 11.8 Referências Complementares Brasileiras

[28] **Miraglia, F.** *Teoria dos Conjuntos: um mínimo*. EDUSP, 1992.

[29] **Halmos, P.R.** *Teoria Ingênua dos Conjuntos*. Tradução brasileira.
Rio de Janeiro: Ciência Moderna, 2001.

[30] **Krause, D.** *Introdução aos Fundamentos Axiomáticos da Ciência*.
São Paulo: E.P.U., 2002.

[31] **Di Prisco, C.A.** *Una Introducción a la Teoría de Conjuntos y los
Fundamentos de las Matemáticas*. Campinas: UNICAMP, 1997 (Coleção CLE, v. 10).

[32] **Feitosa, H.A.; Paulovich, L.** *Um Prelúdio à Lógica*.
São Paulo: Editora da UNESP, 2005.

[33] **Domingues, H.H.; Iezzi, G.** *Álgebra Moderna*. 4ª ed. reform.
São Paulo: Atual, 2003.

### 11.9 Referências dos Planos de Ensino Utilizados

[34] **UNESP — Faculdade de Ciências/Bauru.** Plano de Ensino: Teoria dos Conjuntos.
Curso 1504 — Licenciatura em Matemática. Departamento de Matemática.
Disciplina: 60h, 4 créditos. Ementa: ZFC, Relações, Aplicações, Operações,
Construção de ℕ, ℤ, ℚ.

[35] **UNICAMP — Instituto de Filosofia e Ciências Humanas.** HF005-I: Teoria
dos Conjuntos I. Profs. Walter A. Carnielli e Gabriele Pulcini. 1º sem/2015.
Programa: teoria ingênua, ZF, ordinais, cardinais, indução/recursão transfinita,
CH, AC, fundacionalidade, construtibilidade, independência.

[36] **UFSC — Centro de Filosofia e Ciências Humanas.** FIL5705: Teoria dos
Conjuntos. Prof. Arthur Buchsbaum. Semestre 2020/1. 72h. Baseada na teoria KM.
Ementa: lógica elementar, KM, relações notáveis, equivalências, partições, ordens.
