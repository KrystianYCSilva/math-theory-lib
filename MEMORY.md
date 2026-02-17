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
