---
description: |
  Project-specific coding rules and standards beyond CONSTITUTION.md.
  Use when: writing or reviewing code for this specific project.
---

# Project Rules & Standards

> Regras especificas DESTE projeto. Regras gerais estao em CONSTITUTION.md.

## Regras do projeto

1. **Dual Mode:** Todo conceito deve ter implementação eficiente (Kernel) e axiomática (Construction), com isomorfismo verificável (roundtrip tests).
2. **Imutabilidade:** `MathSet<T>` e derivados devem ser imutáveis.
3. **Lazy Evaluation:** Usar `Sequence<T>` para conjuntos infinitos ou muito grandes.
4. **Dependência Estrita:** Camadas superiores dependem apenas de camadas inferiores (nunca o contrário). Construction -> Set/Relation/Function -> Logic -> Kernel.
5. **Isomorfismo obrigatório:** Cada tipo construído (ex: `ConstructedInteger`) deve ter um objeto `*Isomorphism` com `toKernel`/`fromKernel` e `verifyRoundTrip`.
6. **Embedding chain:** Cada camada numérica embute a anterior (ℕ↪ℤ↪ℚ↪ℝ↪ℂ) via objetos `*Embedding` e extension functions `.toMath*()`.

## Padroes de codigo

- **Nomenclatura:** `MathSet` (não `Set`), `NaturalNumber` (kernel), `VonNeumannNatural` (construção).
- **Types:** Usar `sealed interfaces` para domínios fechados (Cardinality, Formula, Ordinal, ExtendedReal, BorelSet).
- **Value Classes:** Usar para primitivos do kernel (`@JvmInline` sobre BigInteger/BigDecimal).
- **Arithmetic objects:** Cada tipo numérico expõe um object `*Arithmetic` implementando `Arithmetic<N>` (ou `AlgebraicArithmetic<N>` para tipos sem ordem total como ComplexNumber).
- **Testes:** Kotest FunSpec + property-based testing para leis algébricas. Nomear arquivos `*Test.kt`.
- **Platform:** BigInteger/BigDecimal via typealiases em `kernel/platform/` sobre `com.ionspin.kotlin.bignum`.

## Decisoes arquiteturais (ADRs)

| # | Data | Decisao | Status |
|---|------|---------|--------|
| 1 | 2026-02-14 | Separação Kernel/Construction para resolver bootstrap circular | Aceita |
| 2 | 2026-02-14 | MathSet como sealed interface (Extensional vs Intensional) | Aceita |
| 3 | 2026-02-14 | Imutabilidade total das estruturas de conjunto | Aceita |
| 4 | 2026-02-15 | BigInteger/BigDecimal via com.ionspin.kotlin:bignum para KMP | Aceita |
| 5 | 2026-02-15 | ConstructedReal como sequência de Cauchy com módulo de convergência explícito | Aceita |
| 6 | 2026-02-16 | Ordinais via Cantor Normal Form (CNF) com expoentes finitos | Aceita |
| 7 | 2026-02-16 | Conjuntos universais (Reals, Complexes, etc.) como objetos não-enumeráveis (elements() lança exceção) | Aceita |
| 8 | 2026-02-17 | ComplexArithmetic implementa AlgebraicArithmetic (sem Comparable) em vez de Arithmetic | Aceita |
