# logic — Layer 1: Logical Specifications

The `logic` module provides a first-order logic framework for expressing, parsing, printing, and model-checking mathematical formulas. It sits at **Layer 1** of the architecture, above the `kernel` (Layer 0) and below the `construction` layer, serving as the formal language in which axioms and theorems are stated.

## Module Overview

| Component | File | Purpose |
|-----------|------|---------|
| `Term` | `Term.kt` | AST for terms (variables, constants, function applications) |
| `Formula` | `Formula.kt` | AST for first-order formulas with set-theoretic predicates |
| `FormulaScope` | `FormulaBuilder.kt` | Kotlin DSL for building formulas |
| `FormulaParser` | `FormulaParser.kt` | Unicode string → `Formula` parser |
| `FormulaPrettyPrinter` | `FormulaPrettyPrinter.kt` | `Formula` → Unicode string renderer |
| `Axiom` | `Axiom.kt` | Named ZFC axioms as `Formula` instances |
| `AxiomSystem` | `AxiomSystem.kt` | Pre-defined axiom collections (ZFC, ZF, NBG, KM) |
| `PeanoSystem` | `PeanoSystem.kt` | Interface encoding the Peano axioms |
| `Interpretation` | `Interpretation.kt` | First-order interpretation (model) |
| `ModelChecker` | `ModelChecker.kt` | Tarski-style formula evaluator |

## Formula AST

### Term Hierarchy

`Term` is a sealed interface with three variants:

```kotlin
sealed interface Term {
    data class Var(val name: String) : Term       // variable: x, A, B
    data class Const(val value: String) : Term     // constant: 0, ∅
    data class App(val function: String, val args: List<Term>) : Term  // f(x, y)
}
```

### Formula Hierarchy

`Formula` is a sealed interface covering atomic predicates, connectives, and quantifiers:

| Variant | Notation | Description |
|---------|----------|-------------|
| `Membership(element, set)` | `x ∈ A` | Set membership |
| `Equals(left, right)` | `x = y` | Equality |
| `Not(inner)` | `¬φ` | Negation |
| `And(left, right)` | `φ ∧ ψ` | Conjunction |
| `Or(left, right)` | `φ ∨ ψ` | Disjunction |
| `Implies(premise, conclusion)` | `φ → ψ` | Implication |
| `Iff(left, right)` | `φ ↔ ψ` | Biconditional |
| `ForAll(variable, body)` | `∀x(φ)` | Universal quantifier |
| `Exists(variable, body)` | `∃x(φ)` | Existential quantifier |

## DSL for Building Formulas

The `FormulaBuilder.kt` file provides a Kotlin DSL that reads almost like mathematical notation:

```kotlin
// ∀x(x ∈ A → x ∈ B)
val subset = forAll("x") {
    ("x" memberOf "A") implies ("x" memberOf "B")
}

// ∃E(¬(x ∈ E))
val emptySet = exists("E") {
    not("x" memberOf "E")
}

// Combining connectives
val formula = forAll("x") {
    ("x" memberOf "A") and ("x" memberOf "B") implies ("x" eq "x")
}
```

**Available operators in `FormulaScope`:**

| Operator | Syntax | Result |
|----------|--------|--------|
| Membership | `"x" memberOf "A"` | `Formula.Membership` |
| Equality | `"x" eq "y"` | `Formula.Equals` |
| Conjunction | `φ and ψ` | `Formula.And` |
| Disjunction | `φ or ψ` | `Formula.Or` |
| Implication | `φ implies ψ` | `Formula.Implies` |
| Biconditional | `φ iff ψ` | `Formula.Iff` |
| Negation | `not(φ)` | `Formula.Not` |

## Parser and Pretty Printer

### FormulaParser

Parses a Unicode string into a `Formula` AST using recursive descent:

```kotlin
val formula = FormulaParser.parse("∀x(x ∈ A → x ∈ B)")
// Formula.ForAll("x", Formula.Implies(
//     Formula.Membership(Var("x"), Var("A")),
//     Formula.Membership(Var("x"), Var("B"))
// ))
```

**Operator precedence** (lowest to highest):

1. `↔` — Biconditional
2. `→` — Implication
3. `∨` — Disjunction
4. `∧` — Conjunction
5. `¬` — Negation
6. `∀`, `∃`, atoms — Quantifiers and atomic formulas

### FormulaPrettyPrinter

Converts a `Formula` AST back to a Unicode string:

```kotlin
val text = FormulaPrettyPrinter.print(formula)
// "∀x(x ∈ A → x ∈ B)"
```

The parser and printer round-trip correctly: `FormulaParser.parse(FormulaPrettyPrinter.print(f))` reproduces the original AST for well-formed formulas.

## ZFC Axiom Definitions

The `Axiom` sealed interface defines the ten axioms of ZFC as named data objects:

| Axiom | Name | Simplified Formula |
|-------|------|--------------------|
| Extensionality | `Axiom.Extensionality` | `∀A∀B(A = B)` |
| Empty Set | `Axiom.EmptySet` | `∃E(¬(x ∈ E))` |
| Pairing | `Axiom.Pairing` | `∃P(a ∈ P ∨ b ∈ P)` |
| Union | `Axiom.Union` | `∃U(x ∈ U)` |
| Power Set | `Axiom.PowerSet` | `∃P(X ∈ P)` |
| Infinity | `Axiom.Infinity` | `∃I(x ∈ I)` |
| Separation | `Axiom.Separation` | `∃B(x ∈ B)` |
| Replacement | `Axiom.Replacement` | `∃R(x ∈ R)` |
| Choice | `Axiom.Choice` | `∃f(x ∈ f)` |
| Foundation | `Axiom.Foundation` | `∀A(A = A)` |

Pre-assembled systems are available via `AxiomSystem`:

```kotlin
AxiomSystem.ZFC  // all 10 axioms
AxiomSystem.ZF   // ZFC minus Choice
AxiomSystem.NBG  // von Neumann-Bernays-Gödel (currently = ZFC)
AxiomSystem.KM   // Kelley-Morse (currently = ZFC)
```

## Peano System

The `PeanoSystem<N>` interface encodes the Peano axioms generically:

```kotlin
interface PeanoSystem<N> {
    val zero: N
    fun succ(n: N): N
    fun pred(n: N): N?
    fun isZero(n: N): Boolean
    fun verifyInjectivity(sampleSize: Int = 1000): Boolean
    fun verifyZeroNotSuccessor(sampleSize: Int = 1000): Boolean
    fun <R> recursion(base: R, step: (N, R) -> R): (N) -> R
}
```

`NaturalPeanoSystem` is the concrete implementation backed by `mathsets.kernel.NaturalNumber`:

```kotlin
val add = NaturalPeanoSystem.recursion(NaturalNumber.ZERO) { _, acc -> acc.succ() }
val three = add(NaturalNumber.of(3))  // NaturalNumber(3)
```

## Model Checking

### Interpretation

An `Interpretation` defines a finite model for evaluating formulas:

```kotlin
val interp = Interpretation(
    universe = setOf(emptySet<Any>(), setOf(1), setOf(1, 2)),
    membership = { elem, set -> (set as? Set<*>)?.contains(elem) == true },
    constants = mapOf("E" to emptySet<Any>()),
    functions = emptyMap()
)
```

### ModelChecker

`ModelChecker.evaluate` performs Tarski-style evaluation — quantifiers range over `interpretation.universe`, connectives follow standard truth tables:

```kotlin
val formula = exists("E") { not("x" memberOf "E") }
val result = ModelChecker.evaluate(formula, interp)
// true — the empty set has no members
```

You can verify entire axiom systems against an interpretation:

```kotlin
val satisfied = AxiomSystem.ZFC.all { axiom ->
    ModelChecker.evaluate(axiom.formula, interp)
}
```

## Dependencies

- **kernel** — `PeanoSystem` depends on `mathsets.kernel.NaturalNumber`.
- No external libraries beyond Kotlin stdlib.
