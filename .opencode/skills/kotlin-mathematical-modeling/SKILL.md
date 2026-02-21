---
name: kotlin-mathematical-modeling
description: |
  A guide to using advanced Kotlin features for rigorous mathematical and
  logical modeling. Covers sealed types for closed universes, value classes for
  zero-cost type safety, immutability for correctness, and sequences for lazy
  evaluation of infinite structures. Use when: you need to model mathematical
  domains with type safety, immutability, and zero-overhead primitives.
tags: [kotlin, dsl, architecture, type-safety, functional-programming]
---

# Skill: Kotlin for Rigorous Mathematical Modeling

This skill provides a set of patterns for leveraging advanced Kotlin features to create
domain models that are not only efficient but also mathematically and logically correct
by construction. These techniques are particularly useful when translating formal
specifications (from mathematics, logic, physics, etc.) into code.

## 1. Principle: Model Closed Universes with Sealed Types

When a concept can only be one of a specific, limited set of variations, `sealed
interface` or `sealed class` is the ideal tool. This enforces exhaustiveness in `when`
expressions, ensuring that no case is forgotten, which maps directly to the concept of
a "closed universe" or a disjoint union in mathematics.

### Use Case: Modeling Cardinality

In set theory, cardinality can be a finite number, countably infinite (like natural
numbers), or a higher-order infinity (like real numbers). This is a closed set of
possibilities.

```kotlin
import java.math.BigInteger

/**
 * Represents the size (cardinality) of a set.
 * Using a sealed interface ensures that all possible types of cardinality
 * are known at compile time.
 */
sealed interface Cardinality : Comparable<Cardinality> {
    /** A set with a specific, non-negative number of elements. */
    data class Finite(val value: BigInteger) : Cardinality {
        init {
            require(value >= BigInteger.ZERO) { "Finite cardinality must be non-negative." }
        }
    }

    /** A set with a countably infinite number of elements (ℵ₀). */
    object CountablyInfinite : Cardinality

    /** Represents higher-order infinities (e.g., ℵ₁, ℵ₂, ...). */
    data class Aleph(val index: Ordinal) : Cardinality

    /** The cardinality is unknown or cannot be determined computationally. */
    object Unknown : Cardinality

    // Comparison logic would be implemented here...
    override fun compareTo(other: Cardinality): Int = TODO()
}

// Dummy Ordinal for the example
sealed interface Ordinal
```

### Benefits:

-   **Compile-Time Correctness:** The compiler forces you to handle all cases when using `when`, preventing runtime errors from unhandled states.
-   **Clarity:** The code explicitly defines the entire universe of possibilities.
-   **No "Invalid" States:** It's impossible to create a `Cardinality` that isn't one of the defined subtypes.

---

## 2. Principle: Use Value Classes for Zero-Cost Type Safety

Often, you need to distinguish between types that have the same underlying representation (e.g., a `NaturalNumber` is just an `Int`, but a non-negative one). Using `value class` creates a new, distinct type at compile time without incurring the overhead of object allocation at runtime.

### Use Case: Type-Safe Kernel Numbers

In `mathsets-kt`, the `kernel` needs efficient number types that are distinct from standard primitives.

```kotlin
@JvmInline
value class NaturalNumber(val value: ULong) : Comparable<NaturalNumber> {
    init {
        // The underlying type ULong already enforces non-negativity.
    }

    // Operator overloads for type-safe arithmetic
    operator fun plus(other: NaturalNumber) = NaturalNumber(this.value + other.value)
    
    override fun compareTo(other: NaturalNumber): Int = this.value.compareTo(other.value)
}

fun calculateSum(a: NaturalNumber, b: NaturalNumber): NaturalNumber {
    return a + b
}

// val result = calculateSum(NaturalNumber(5u), 2) // COMPILE ERROR!
// The compiler prevents mixing types, even though the underlying representation is similar.
```

### Benefits:

-   **Zero Overhead:** At runtime (on JVM), this is treated as the underlying primitive (`ULong`), avoiding object allocation and pointer indirection.
-   **Type Safety:** You cannot accidentally mix a `NaturalNumber` with a plain `ULong` or `Int`.
-   **Readability:** The type name `NaturalNumber` communicates intent far better than `ULong`.

---

## 3. Principle: Enforce Immutability by Design

Mathematical objects, like sets, are immutable. Their identity is defined by their contents. Any "change" results in a new object. Your data structures should reflect this. Design classes with `val` properties and ensure all "modification" operations return a *new* instance.

### Use Case: An Immutable Mathematical Set

```kotlin
/**
 * An immutable set backed by a persistent data structure.
 * Operations like 'add' return a new instance of the set.
 */
class ExtensionalSet<T>(private val elements: Set<T>) {

    fun contains(element: T): Boolean = element in elements

    fun add(element: T): ExtensionalSet<T> {
        if (contains(element)) {
            return this // Optimization: if element is already there, return same instance
        }
        // Returns a NEW set with the element added. The original is untouched.
        return ExtensionalSet(elements + element)
    }

    fun union(other: ExtensionalSet<T>): ExtensionalSet<T> {
        // Returns a NEW set representing the union.
        return ExtensionalSet(this.elements + other.elements)
    }
}
```

### Benefits:

-   **Thread-Safety:** Immutable objects are inherently safe to share across threads.
-   **Predictability:** State cannot change unexpectedly, making reasoning about code flow simpler.
-   **Correctness:** Aligns with the formal definition of the objects being modeled.

---

## 4. Principle: Represent Infinite Structures with Sequences

To model infinite sets (like the set of all natural numbers) or perform operations that could result in very large collections (like a power set), direct materialization is impossible or inefficient. Use Kotlin's `Sequence<T>` to represent these structures lazily.

A `Sequence` computes its elements on demand, one at a time, only when requested.

### Use Case: The Infinite Set of Natural Numbers

```kotlin
/**
 * A lazy, infinite sequence representing the set of all natural numbers.
 */
val NaturalNumbers: Sequence<ULong> = sequence {
    var current: ULong = 0u
    while (true) {
        yield(current)
        current++
    }
}

fun main() {
    // Operations are lazy and chain together without computation.
    val firstTenEvenSquares = NaturalNumbers
        .filter { it % 2u == 0u }
        .map { it * it }
        
    // Computation happens here, only when we consume the sequence.
    val result = firstTenEvenSquares.take(10).toList() 
    
    println(result) // [0, 4, 16, 36, 64, 100, 144, 196, 256, 324]
}
```

### Benefits:

-   **Infinite Structures:** Makes it possible to represent and operate on infinite data sets.
-   **Efficiency:** Avoids creating large intermediate collections in memory. Only the data that is actually consumed gets computed.
-   **Composability:** Lazy operations can be chained together elegantly.

---

## References:

1.  **Source of this Skill:** [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) from the `mathsets-kt` project.
2.  **Sealed Types:** [Official Kotlin Documentation on Sealed Classes and Interfaces](https://kotlinlang.org/docs/sealed-classes.html)
3.  **Value Classes:** [Official Kotlin Documentation on Value Classes](https://kotlinlang.org/docs/value-classes.html)
4.  **Sequences:** [Official Kotlin Documentation on Sequences](https://kotlinlang.org/docs/sequences.html)
5.  **Immutability:** [Kotlin's `val` vs `var`](https://kotlinlang.org/docs/basic-syntax.html#variables)
