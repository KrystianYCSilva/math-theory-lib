### Sprint 1: Kernel - Tipos Numéricos Primitivos
- **Data:** 2026-02-14
- **Arquivos:** `NaturalNumber.kt`, `IntegerNumber.kt`, `RationalNumber.kt`, `Cardinality.kt`, `BigInteger.kt` (Platform abstractions)
- **Testes:** Testes de propriedade (Kotest) para comutatividade, associatividade, identidade, inversos e normalização de racionais.
- **Notas:** 
    - Implementação de `BigInteger` via `expect/actual` com delegação para `java.math.BigInteger` na JVM e stubs/wrappers para JS/Native.
    - `RationalNumber` usa normalização automática via GCD no construtor.
    - `NaturalNumber` e `IntegerNumber` são `value class`es para performance.
