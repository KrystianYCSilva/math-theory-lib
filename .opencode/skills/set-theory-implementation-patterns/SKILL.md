---
name: set-theory-implementation-patterns
description: |
  Provides patterns for the computational implementation of axiomatic set
  theory. Covers the "Dual Mode" pattern (efficient kernel vs. rigorous
  construction), lazy evaluation for infinite sets, and distinguishing
  intensional from extensional sets. Use when: implementing ZFC set theory,
  infinite structures, or formal mathematical foundations in code.
tags: [set-theory, computer-science, architecture, formal-methods, mathematics]
---

# Skill: Set Theory Implementation Patterns

This skill describes a series of architectural patterns for implementing axiomatic set theory (like ZFC) in a computational environment. It addresses the challenges of representing abstract, often infinite, mathematical objects in a concrete, finite machine, while balancing formal correctness with practical performance.

These patterns are essential for anyone building systems based on formal mathematical or logical foundations, such as theorem provers, symbolic algebra systems, or educational tools.

## 1. Pattern: Distinguish Extensional and Intensional Sets

In mathematics, a set can be defined in two ways:
- **By Extension:** Listing all its members (e.g., A = {1, 2, 3}). This is finite and explicit.
- **By Intension (Comprehension):** Describing a property its members must satisfy (e.g., P = {x ∈ ℕ | x is prime}). This can be infinite.

Your implementation must support both. A `sealed interface` is a perfect way to model this.

```kotlin
/**
 * A sealed interface representing a mathematical set.
 */
sealed interface MathSet<T> {
    operator fun contains(element: T): Boolean
}

/**
 * An EXTENSIONAL set, defined by explicitly listing its elements.
 * Backed by a standard, immutable collection.
 * 'contains' is a fast O(1) lookup.
 */
class ExtensionalSet<T>(private val elements: Set<T>) : MathSet<T> {
    override fun contains(element: T): Boolean = element in elements
}

/**
 * An INTENSIONAL set, defined by a property (predicate) over a domain.
 * 'contains' involves computation: evaluating the predicate.
 */
class IntensionalSet<T>(
    private val domain: MathSet<T>,
    private val predicate: (T) -> Boolean
) : MathSet<T> {
    override fun contains(element: T): Boolean {
        // To be a member, it must belong to the domain AND satisfy the predicate.
        return domain.contains(element) && predicate(element)
    }
}

// Example usage:
val A = ExtensionalSet(setOf(1, 2, 3, 4, 5))
val B = IntensionalSet(AllNaturalNumbers) { it % 2 == 0 } // The set of even numbers

println(3 in A) // true (fast lookup)
println(10 in B) // true (evaluates predicate)
println(9 in B)  // false (evaluates predicate)
```
*(`AllNaturalNumbers` would itself be an `IntensionalSet` or a lazy `Sequence`)*

---
## 2. Pattern: Dual Mode - Kernel vs. Construction

There is a fundamental circularity when building mathematics from the ground up: numbers are built from sets, but our computers need numbers to even begin to work with collections.

The "Dual Mode" architecture resolves this by separating the system into two distinct modes of operation:
1.  **Kernel Mode:** Provides highly efficient, primitive implementations of core concepts (like numbers). These are `value class` wrappers around machine-native types (`BigInteger`, etc.). This layer is for **performance**. It does not claim to be axiomatically pure; it's what the computer uses to get work done.
2.  **Construction Mode:** Provides a rigorous, formal construction of those same concepts, built purely from set-theoretic axioms. This layer is for **correctness and verification**. It proves that the efficient Kernel objects have a sound mathematical foundation.

Finally, an **Isomorphism** layer uses property-based tests to prove that the Kernel and Construction versions are structurally identical and behave the same way.

### Example: The Two Faces of Natural Numbers

```kotlin
// === KERNEL MODE (in kernel/ module) ===
@JvmInline
value class KernelNatural(val value: ULong) { // Fast, zero-overhead
    operator fun plus(other: KernelNatural) = KernelNatural(this.value + other.value)
}

// === CONSTRUCTION MODE (in construction/ module) ===
/**
 * A Von Neumann Ordinal, where a number is the set of all preceding numbers.
 * 0 = ∅
 * 1 = {0} = {∅}
 * 2 = {0, 1} = {∅, {∅}}
 */
data class VonNeumannNatural(val predecessors: MathSet<VonNeumannNatural>) {
    // '+' is a complex recursive function defined on sets.
    operator fun plus(other: VonNeumannNatural): VonNeumannNatural = TODO("Recursive set union logic")
}

// === ISOMORPHISM (in test/ module) ===
test("Kernel and Construction addition are isomorphic") {
    forAll<ULong, ULong> { a, b ->
        val kernelA = KernelNatural(a)
        val kernelB = KernelNatural(b)
        
        val constructorA = toVonNeumann(a) // Conversion function
        val constructorB = toVonNeumann(b)

        // Check if the results correspond after conversion
        toVonNeumann((kernelA + kernelB).value) == (constructorA + constructorB)
    }
}
```
This separation allows you to use fast, practical types for everyday computation while still being able to formally prove that your system is grounded in first principles.

---

## 3. Pattern: Lazy Evaluation for Infinite & Large Sets

Operations like `powerSet` (the set of all subsets) or `cartesianProduct` produce results that are exponentially larger than their inputs. For infinite sets, the results are also infinite. Attempting to materialize these in memory is impossible.

The solution is to use **lazy evaluation**, where the elements of the resulting set are only computed when they are actually requested. Kotlin's `Sequence<T>` is the perfect tool for this.

### Example: A Lazy Power Set

Instead of calculating all 2ⁿ subsets at once, we create an object that can generate them one by one on demand.

```kotlin
fun <T> MathSet<T>.powerSet(): MathSet<MathSet<T>> {
    // If the set is finite and small, we can materialize it.
    if (this is ExtensionalSet && this.size() < 30) {
        return ExtensionalSet(generateSubsets(this.elements))
    }
    // Otherwise, return a lazy representation.
    return LazyPowerSet(this)
}

/**
 * Represents the power set without storing its 2^N elements.
 */
class LazyPowerSet<T>(private val originalSet: MathSet<T>) : MathSet<MathSet<T>> {
    
    override fun contains(element: MathSet<T>): Boolean {
        // A set 'S' is in the power set of 'U' if 'S' is a subset of 'U'.
        return element.isSubsetOf(originalSet)
    }

    /**
     * This would return a Sequence that generates the subsets one by one,
     * often by mapping integers from 0 to 2^N-1 to their bitmask representation.
     */
    fun elements(): Sequence<MathSet<T>> = sequence {
        TODO("Logic to yield subsets lazily")
    }
}
```
This pattern allows you to represent and perform calculations on transfinitely large objects in a finite computational environment.

## References

1.  **Source of this Skill:** [`docs/ARCHITECTURE.md` (DA-01, DA-02, DA-06)](docs/ARCHITECTURE.md) from the `mathsets-kt` project.
2.  **Set Theory:** [Zermelo-Fraenkel Set Theory (Wikipedia)](https://en.wikipedia.org/wiki/Zermelo%E2%80%93Fraenkel_set_theory)
3.  **Axiomatic vs. Naive Set Theory:** [Halmos, *Naive Set Theory*](https://en.wikipedia.org/wiki/Naive_Set_Theory_(book)) (A classic text explaining the motivation for these patterns).
