# examples

Comprehensive examples demonstrating the math-theory-lib across basic, advanced, and commercial use cases.

## Overview

This module contains runnable demonstrations that show how the library's mathematical abstractions apply to both theoretical and practical problems. Examples are organized into three categories:

1. **Basic** — Number construction (N → Z → Q), set-theoretic paradoxes, kernel/set operations.
2. **Advanced** — Ordinal arithmetic, transfinite recursion, Cantor diagonal, Ramsey theory, topology, game theory, ZFC verification, forcing, and constructive reals.
3. **Commercial** — Enterprise patterns: RBAC, credit scoring, dependency resolution, deduplication, A/B testing, financial math, feature flags, load balancing, pricing games, schema mapping, data validation, workflow engines, and set-based queries.

## Key Classes

### Basic Examples

| Class / Object | Description |
|---|---|
| `NumberConstructionDemo` | Walks through the N → Z → Q embedding chain |
| `ParadoxDemos` | Narratives for Russell's, Burali-Forti, and Cantor's paradoxes |
| `KernelAndSetUseCases` | Extended reals, complex roots, parity partitions, bijection round-trips |

### Advanced Examples

| Class / Object | Description |
|---|---|
| `AdvancedUseCases` | 15 advanced demos covering the full library |

Key demonstrations:
- Equivalence-based student classification
- First-order logic database constraint verification
- Task scheduling with total orders
- Axiom of Choice for delegate election
- Cantor diagonal non-surjectivity proof
- Gale-Stewart game analysis (Nim variant)
- Ramsey party problem R(3,3) = 6
- Finite topology interior/closure/boundary
- Ordinal non-commutativity
- CH independence via forcing analogue
- ZFC axiom verification on mini-models
- Constructive sqrt(2) as Cauchy sequence
- Rational enumeration (ℕ ↔ ℚ)
- Bell numbers and partition counting
- Poset filter enumeration

### Commercial Examples

| Class / Object | Description |
|---|---|
| `CommercialUseCases.RBACEngine` | Role-based access control with segregation-of-duties |
| `CommercialUseCases.CreditRuleEngine` | Predicate-composed credit approval rules |
| `CommercialUseCases.DependencyResolver` | Topological sort via partial orders |
| `CommercialUseCases.DataDeduplicator` | Duplicate detection via equivalence relations |
| `CommercialUseCases.ABTestAllocator` | Deterministic A/B test assignment via bijections |
| `CommercialUseCases.FinancialCalculator` | Exact rational arithmetic for money |
| `CommercialUseCases.FeatureFlagEngine` | Composed predicates for feature toggles |
| `CommercialUseCases.LoadBalancer` | Server selection via Axiom of Choice |
| `CommercialUseCases.BacklogPrioritizer` | Sprint planning via total orders |
| `CommercialUseCases.PricingGame` | Competitive pricing via Gale-Stewart games |
| `CommercialUseCases.SchemaMapper` | Reversible schema transforms via bijections |
| `CommercialUseCases.DataValidator` | Referential integrity and uniqueness checks |
| `CommercialUseCases.WorkflowEngine` | State machines as partial orders |
| `CommercialUseCases.SetQueryEngine` | Set algebra for data filtering |
| `CommercialUseCases.PermissionAuditor` | Cartesian product for permission review |

## Usage Example

```kotlin
// Number construction chain
val walkthrough = NumberConstructionDemo.walkthrough(5)
// ["Natural: 5", "Embed em Z: 5", "Embed em Q: 5/1"]

// Ordinal non-commutativity
val (addCommutes, mulCommutes) = AdvancedUseCases.ordinalNonCommutativity()
// (false, false) — ordinal arithmetic is NOT commutative

// Credit rule engine
val app = CommercialUseCases.CreditApplication("Alice", 90000, 750, false, 0.3)
val tier = CommercialUseCases.CreditRuleEngine.evaluate(app)
// "STANDARD"
```

## Module Dependencies

This module depends on nearly all other modules:
- `kernel`, `set`, `function`, `relation`, `logic`
- `construction` (integer, rational, real)
- `ordinal`, `cardinal`, `descriptive`, `combinatorics`, `forcing`
