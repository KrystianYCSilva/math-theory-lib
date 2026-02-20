---
description: |
  Tech stack and architecture decisions.
  Use when: you need to understand the technologies, patterns, and architectural choices.
---

# Tech Stack & Architecture

## Stack

| Camada | Tecnologia | Versão |
|--------|------------|--------|
| Linguagem | Kotlin Multiplatform | 2.1.0 |
| Framework | KMP Standard Lib | - |
| Build | Gradle KTS + Version Catalog | 8.x+ |
| Testes | Kotest (FunSpec + Property) | 5.8.0 |
| BigNum | com.ionspin.kotlin:bignum | 0.3.10 |
| Coroutines | kotlinx-coroutines-core | 1.8.0 |
| Analise | Detekt | 1.23.5 |
| Docs | Dokka | 1.9.10 |

## Arquitetura

Arquitetura em 4 camadas rigorosas (Layered Architecture):
1. **Kernel** (Primitivos computacionais — value classes sobre BigInteger/BigDecimal)
2. **Logic** (FOL, axiomas ZFC, PeanoSystem, ModelChecker)
3. **Set Theory** (MathSet, Relation, Function, Orders)
4. **Construction & Advanced** (Derivação axiomática ℕ→ℤ→ℚ→ℝ→ℂ, Ordinais, Cardinais, Descriptive, Combinatorics, Forcing)

Dependências apenas para baixo (Construction -> Set/Relation/Function -> Logic -> Kernel).

## Estrutura de diretorios

```
math-theory-lib/
├── kernel/          # NaturalNumber, IntegerNumber, RationalNumber, RealNumber,
│                    # ComplexNumber, ImaginaryNumber, IrrationalNumber, ExtendedReal,
│                    # Cardinality, Predicate, Generator, Arithmetic, analysis/
├── logic/           # Formula, Term, Axiom, PeanoSystem, ModelChecker, FormulaParser
├── set/             # MathSet<T>, ExtensionalSet, IntensionalSet, UniversalSets,
│                    # SetAlgebra, ZFCVerifier, Paradoxes, LazyPowerSet, BitMathSet
├── relation/        # OrderedPair, Relation, EquivalenceRelation, Partition,
│                    # PartialOrder, TotalOrder, WellOrder
├── function/        # MathFunction, Injection, Surjection, Bijection, ChoiceFunction
├── construction/    # VonNeumannNatural, ConstructedInteger, ConstructedRational,
│                    # ConstructedReal (Cauchy), ConstructedIrrational, ConstructedComplex
├── ordinal/         # Ordinal (Finite/CNF), CantorNormalForm, OrdinalArithmetic,
│                    # TransfiniteRecursion
├── cardinal/        # Countability (N↔Z, N↔Q), CantorDiagonal, CardinalArithmetic
├── descriptive/     # FiniteTopology, BorelHierarchy
├── combinatorics/   # PartitionCalculus, Ramsey, GaleStewartGame
├── forcing/         # Poset, GenericFilter, ForcingExtension, IndependenceDemo
├── algebra/         # Hierarquia algébrica (Group, Ring, Field), módulos, Galois
├── polynomial/      # Anéis de polinômios, divisão euclidiana, fatoração
├── galois/          # Teoria de Galois, grupos de automorfismos
├── category/        # Category, Functor, NaturalTransformation, Yoneda
├── typetheory/      # MLTT, TypeChecker, Evaluator, CurryHoward
├── computability/   # TuringMachine, LambdaCalculus, Church encoding
├── modeltheory/     # Signature, Structure, Satisfaction, Compactness
├── linalg/          # Matrix, GaussianElimination, Eigenvalue, Smith/Jordan Normal Form, SVD
├── ntheory/         # ModularArithmetic, CRT, MillerRabin, PollardRho, EllipticCurve
├── graph/           # Graph structures, BFS/DFS, Dijkstra, MST, MaxFlow, Matching
├── analysis/        # Limit, Continuity, Differentiation, Integration, Series, Metric Spaces
├── solver/          # SatSolver (DPLL), CnfFormula
├── proof/           # Proof objects, ProofChecker, TheoremRegistry
├── symbolic/        # Expression tree, Simplifier, Differentiator
├── ode/             # EulerMethod, RungeKutta4, ODE systems
└── examples/        # NumberConstructionDemo, ParadoxDemos, KernelAndSetUseCases
```

## Dependencias criticas

- Kotlin Standard Library
- com.ionspin.kotlin:bignum (BigInteger/BigDecimal multiplataforma)
- Kotest (FunSpec, assertions, property-based testing)
- kotlinx-coroutines-core
- kotlinx-atomicfu
