# Tech Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| Language | Kotlin Multiplatform | 2.1.0 |
| Build | Gradle KTS + Version Catalog | 8.x+ |
| Tests | Kotest (FunSpec + Property) | 5.8.0 |
| BigNum | com.ionspin.kotlin:bignum | 0.3.10 |
| Coroutines | kotlinx-coroutines-core | 1.8.0 |
| Analysis | Detekt | 1.23.5 |
| Docs | Dokka | 1.9.10 |

## Architecture (4 Layers)
1. **Kernel** — Primitives (value classes over BigInteger/BigDecimal)
2. **Logic** — FOL, ZFC axioms, PeanoSystem, ModelChecker
3. **Set Theory** — MathSet, Relation, Function, Orders
4. **Construction & Advanced** — Number tower, ordinals, cardinals, algebra, analysis, solvers

Dependencies flow downward only: Construction → Set → Logic → Kernel.

## Key Modules
kernel · logic · set · relation · function · construction · ordinal · cardinal · descriptive · combinatorics · forcing · algebra · polynomial · galois · category · typetheory · computability · modeltheory · linalg · ntheory · graph · analysis · solver · proof · symbolic · ode · examples
