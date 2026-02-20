---
description: "Documentação para MEMORY.md"
use_when: "When you need information about MEMORY.md"
---

### Sprint 1: Kernel - Tipos Numéricos Primitivos
- **Data:** 2026-02-14
- **Arquivos:** `NaturalNumber.kt`, `IntegerNumber.kt`, `RationalNumber.kt`, `Cardinality.kt`, `BigInteger.kt` (Platform abstractions)
- **Testes:** Testes de propriedade (Kotest) para comutatividade, associatividade, identidade, inversos e normalização de racionais.
- **Notas:** 
    - Implementação de `BigInteger` via `expect/actual` com delegação para `java.math.BigInteger` na JVM e stubs/wrappers para JS/Native.
    - `RationalNumber` usa normalização automática via GCD no construtor.
    - `NaturalNumber` e `IntegerNumber` são `value class`es para performance.

### Expansão Roadmap (Sprints 2-10) - Kernel, Logic, Set, Relation, Function
- **Data:** 2026-02-17
- **Arquivos-chave:** 
    - `kernel/`: `Predicate.kt`, `Generator.kt`, `Arithmetic.kt`, `MathElement.kt` + expansão de `NaturalNumber.kt`.
    - `logic/`: AST, parser/pretty-printer, axiomas, `NaturalPeanoSystem`, `ModelChecker`.
    - `set/`: `MathSet.kt` expandido, `UniversalSets.kt`, `SetAlgebra.kt`, `ZFCVerifier.kt`, `Paradoxes.kt`.
    - `relation/`: `RelationProperties.kt`, `EquivalenceRelation.kt`, `Order.kt`.
    - `function/`: novo módulo com `MathFunction`, classes de bijeção e função de escolha.
- **Testes:** suites de `jvmTest` para `kernel`, `logic`, `set`, `relation`, `function`; compilação JS validada para os mesmos módulos.
- **Notas:**
    - Estrutura KMP ampliada para módulos `logic` e `function` com `build.gradle.kts` próprios.
    - Materialização de conjuntos infinitos agora falha explicitamente (`InfiniteMaterializationException`).
    - Pendências macro permanecem nos sprints 11-20 (construction/ordinal/cardinal/descriptive/combinatorics/forcing/examples).

### Hotfix KMP - BigInteger sem expect/actual
- **Data:** 2026-02-17
- **Motivação:** eliminar warning de beta feature em `expect/actual` e aderir a prática KMP de preferir abstrações comuns quando possível.
- **Mudanças:**
    - Migração para `com.ionspin.kotlin:bignum` no `kernel`.
    - `kernel/src/commonMain/kotlin/mathsets/kernel/platform/BigInteger.kt` passou a ser implementação comum (sem `expect/actual`).
    - Remoção de implementações por plataforma (`jvmMain/jsMain/nativeMain`) de `BigIntegerImpl.kt`.
- **Validação:** testes `jvmTest` e compilações JS dos módulos ativos (`kernel`, `set`, `relation`, `function`, `logic`) executados com sucesso.

### Expansao Kernel de Analise + Modulo Examples
- **Data:** 2026-02-17
- **Escopo:** `kernel/` e `examples/`
- **Mudancas principais:**
  - Adicao de tipos numericos para analise no kernel:
    - `RealNumber` (base decimal multiplataforma)
    - `ImaginaryNumber`
    - `ComplexNumber`
    - `IrrationalNumber` (simbolico com aproximacao)
    - `ExtendedReal` com `+infinity`, `-infinity` e `indeterminate`
  - Definicao explicita de regras criticas envolvendo zero/infinito em `ExtendedReal`:
    - `(+infinity) + (-infinity) = indeterminate`
    - `infinity * 0 = indeterminate`
    - `0 / 0 = indeterminate`
    - `infinity / infinity = indeterminate`
  - Ativacao do modulo `examples` com casos de uso executaveis para:
    - reciprocidade com infinito,
    - quociente de diferencas em `x=0`,
    - raizes complexas de `x^2 + 1 = 0`,
    - particao por paridade,
    - roundtrip de bijecao finita.

- **Testes adicionados:**
  - `kernel/src/commonTest/...`: `RealNumberTest`, `ImaginaryNumberTest`, `ComplexNumberTest`, `IrrationalNumberTest`, `ExtendedRealTest`.
  - `examples/src/commonTest/...`: `KernelAndSetUseCasesTest`.

- **Validacao:**
  - `:kernel:jvmTest :examples:jvmTest`
  - `:kernel:compileKotlinJs :examples:compileKotlinJs`
  - `:kernel:jvmTest :set:jvmTest :relation:jvmTest :function:jvmTest :examples:jvmTest`

- **Nota de qualidade:**
  - Warning de visibilidade de `copy()` em data class com construtor privado foi mitigado com `@ConsistentCopyVisibility`.

### UniversalSets Analiticos + Base de Limites/Derivadas
- **Data:** 2026-02-17
- **Escopo:** `set/` e `kernel/`
- **Mudancas:**
  - `UniversalSets` ampliado com dominios analiticos intensionais nao-enumeraveis:
    - `Reals`, `Irrationals`, `Imaginaries`, `Complexes`, `ExtendedReals`.
  - Semantica de nao-enumerabilidade explicitada:
    - `elements()` nao suportado;
    - `materialize()` proibido;
    - uniao/intersecao com identidade de conjunto universal.
  - Mini camada de calculo no kernel:
    - `analysis/Limits.kt` (quociente em reta estendida, reciproco, limites laterais de 1/x em 0, quociente de diferencas)
    - `analysis/Derivatives.kt` (forward/symmetric difference como base de derivacao)
  - `Arithmetic.kt` complementado para novos tipos:
    - `AlgebraicArithmetic` (sem ordem),
    - `Arithmetic` (com ordem),
    - `RealArithmetic`, `ExtendedRealArithmetic`, `ComplexArithmetic`.

- **Teste e validacao:**
  - Novos testes em `set` e `kernel` cobrindo semantica analitica e operadores base de calculo.
  - Execucoes:
    - `:kernel:jvmTest :set:jvmTest :examples:jvmTest`
    - `:kernel:compileKotlinJs :set:compileKotlinJs :examples:compileKotlinJs`

- **Decisao de modelagem:**
  - `ComplexArithmetic` permanece sem comparacao total por consistencia matematica (complexos nao tem ordem total canonica).

### Finalizacao Roadmap (Sprints 11-20)
- **Data:** 2026-02-17
- **Escopo:** `construction`, `ordinal`, `cardinal`, `descriptive`, `combinatorics`, `forcing`, `examples`
- **Mudancas principais:**
  - **Sprints 11-14 (Construcao da torre numerica):**
    - Implementados `VonNeumannNatural`, `VonNeumannPeanoSystem`, aritmetica/ordem/isomorfismo de naturais construidos.
    - Implementados `ConstructedInteger` e `ConstructedRational` com classes de equivalencia, embeddings e isomorfismos com kernel.
    - Cadeia operacional `NaturalNumber -> MathInteger -> MathRational`.
  - **Sprints 15-16 (Ordinais):**
    - Novo modulo `ordinal/` com `CantorNormalForm`, `Ordinal` (`Finite`, `CNF`), `OrdinalArithmetic`, `TransfiniteRecursion`.
    - Casos classicos cobertos: `omega + 1 != 1 + omega`, `omega * 2 = omega + omega`, `omega^2 > omega * n`.
  - **Sprints 17-18 (Cardinais):**
    - Novo modulo `cardinal/` com `CardinalArithmetic`, `CantorDiagonal`, `Countability` (bijecoes construtivas `N<->Z` e `N<->Q`) e modulo expositivo de CH.
  - **Sprint 19 (Descritiva + Combinatoria):**
    - Novo modulo `descriptive/`: `FiniteTopology` e `BorelHierarchy`.
    - Novo modulo `combinatorics/`: `GaleStewartGame`, `Ramsey`, `PartitionCalculus` (`allPartitions`, `bellNumber`, `erdosRadoArrow`).
  - **Sprint 20 (Forcing + Demos):**
    - Novo modulo `forcing/`: `Poset`, `GenericFilterBuilder`, `ForcingExtension`, `IndependenceDemo`.
    - `examples/` expandido com `ParadoxDemos` (Russell/Burali-Forti/Cantor) e `NumberConstructionDemo`.
  - **Correcao de integracao:**
    - Ajustado `NaturalNumber` para validar corretamente apenas valores negativos no `init` (bug de condicao invertida identificado na suite de `set`).

- **Validacao executada:**
  - Suites por modulo (JVM + JS) para `ordinal`, `cardinal`, `descriptive`, `combinatorics`, `forcing`, `examples`.
  - Validacao integrada completa:
    - JVM: `kernel`, `logic`, `set`, `relation`, `function`, `construction`, `ordinal`, `cardinal`, `descriptive`, `combinatorics`, `forcing`, `examples`
    - JS compile: mesmos modulos.

- **Resultado:**
  - Roadmap `docs/ROADMAP.md` implementado fim-a-fim (Sprint 1 ao 20) no codigo.
  - Ambiente local continua sem build Native completo por restricoes de toolchain, mas validacao JVM + JS verde.

### Expansion Construction para Tipos Analiticos (R, Irracionais, Imaginarios, Complexos)
- **Data:** 2026-02-17
- **Escopo:** `construction/` (novos subpacotes `real`, `irrational`, `complex`)
- **Mudancas principais:**
  - `ConstructedReal` com aproximantes racionais + interoperabilidade com `RealNumber`.
  - `ConstructedIrrational` com simbolo e aproximacao construida, incluindo constantes canonicas.
  - `ConstructedImaginary` e `ConstructedComplex` com operacoes algébricas e embeddings.
  - Objetos de isomorfismo/embedding para roundtrip e verificacao com kernel.
- **Testes adicionados:**
  - `ConstructedRealTest`, `ConstructedIrrationalTest`, `ConstructedImaginaryTest`, `ConstructedComplexTest`.
- **Validacao:**
  - `:construction:jvmTest :construction:compileKotlinJs`
  - `:examples:jvmTest :examples:compileKotlinJs`

### Refatoracao Rigorosa dos Constructed Analiticos
- **Data:** 2026-02-17
- **Escopo:** `construction/real`, `construction/irrational`, `construction/complex`
- **Mudancas principais:**
  - `ConstructedReal` migrado para representante explicito de sequencias de Cauchy (termos + modulo), com operacoes definidas no nivel da construcao.
  - `ConstructedIrrational` com fundamento explicito:
    - `ALGEBRAIC_CONSTRUCTION` para `sqrt(2)`, `sqrt(3)`, `phi`,
    - `AXIOMATIC_SYMBOL` para `pi` e `e` (com aproximacao construtiva para interoperabilidade).
  - `ConstructedImaginary` e `ConstructedComplex` reescritos para aritmetica por componentes `ConstructedReal`, removendo dependencia de `kernelValue` como fonte primária.
  - Novos mecanismos de verificacao no real construído: aproximacao racional por indice, modulo de Cauchy e checagem finita de Cauchy.
- **Testes atualizados:**
  - `ConstructedRealTest` com cobertura de `squareRootOf` e criterio Cauchy.
  - `ConstructedIrrationalTest` com cortes inferiores e testemunha de nao-racionalidade para irracionais algébricos.
- **Validacao:**
  - `:construction:jvmTest :construction:compileKotlinJs`
  - `:examples:jvmTest :examples:compileKotlinJs`
