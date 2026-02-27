# mathsets-kt

Kotlin Multiplatform library providing mathematically-founded abstractions rooted in ZFC set theory, formal logic, abstract algebra, and category theory.

## Status

> **Alpha** — Core modules (`kernel`, `set`, `logic`, `relation`, `function`, `algebra`, `category`) are functional and under active development. API is not yet stable.

## Modules

| Module | Description | Maturity |
|--------|-------------|----------|
| `kernel` | ZFC axioms, `Element`, `MathSet`, `Pair`, `PowerSet`, universe | **Stable** |
| `set` | Set operations, builders, comprehension, extensional/intensional sets | **Stable** |
| `logic` | Propositional and first-order logic, connectives, quantifiers | **Stable** |
| `relation` | Binary relations, equivalence, partial/total orders | **Stable** |
| `function` | Mathematical functions, injection, surjection, bijection | **Stable** |
| `algebra` | Groups, rings, fields, monoids, homomorphisms | **Beta** |
| `category` | Categories, functors, natural transformations | **Beta** |
| `construction` | Ordinal/cardinal arithmetic, transfinite constructions | **Alpha** |
| `ordinal` | Ordinal numbers and arithmetic | **Alpha** |
| `cardinal` | Cardinal numbers and arithmetic | **Alpha** |
| `linalg` | Linear algebra (vectors, matrices, spaces) | **Alpha** |
| `ntheory` | Number theory | **Alpha** |
| `combinatorics` | Combinatorial structures | **Alpha** |
| `polynomial` | Polynomial rings | **Alpha** |
| `galois` | Galois theory | **Experimental** |
| `typetheory` | Type theory foundations | **Experimental** |
| `forcing` | Forcing and independence proofs | **Experimental** |
| `computability` | Computability theory | **Experimental** |
| `modeltheory` | Model theory | **Experimental** |
| `analysis` | Real analysis foundations | **Experimental** |
| `graph` | Graph theory | **Experimental** |
| `solver` | Constraint solvers | **Experimental** |
| `proof` | Proof assistants | **Experimental** |
| `symbolic` | Symbolic computation | **Experimental** |
| `ode` | Ordinary differential equations | **Experimental** |
| `descriptive` | Descriptive set theory | **Experimental** |

## Quick Start

```kotlin
// Gradle (Kotlin DSL)
dependencies {
    implementation("io.github.KrystianYCSilva:kernel:0.1.0-alpha.1")
    implementation("io.github.KrystianYCSilva:set:0.1.0-alpha.1")
    implementation("io.github.KrystianYCSilva:logic:0.1.0-alpha.1")
}
```

```kotlin
import com.mathsets.kernel.*
import com.mathsets.set.*

// Create a finite set
val primes = extensionalSetOf(2, 3, 5, 7, 11)

// Set operations
val evens = intensionalSet<Int> { it % 2 == 0 }
val evenPrimes = primes intersect evens  // {2}
```

## Building

```sh
# Build all modules
./gradlew build

# Run all tests
./gradlew check

# Run detekt static analysis
./gradlew detekt

# Generate documentation (multi-module)
./gradlew dokkaHtmlMultiModule
```

## Design Principles

- **Immutable by default** — All core types are immutable, aligned with ZFC foundations
- **Lazy evaluation** — `Sequence<T>` for potentially infinite structures
- **Property-based testing** — Algebraic laws verified via Kotest property testing
- **Zero-overhead wrappers** — `@JvmInline value class` for kernel primitives

## Used By

- [Kura Language](https://github.com/KrystianYCSilva/fundamentos-linguagens-compiladores) — Provides the mathematical type system foundation

## License

[MIT](LICENSE)
