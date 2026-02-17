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
