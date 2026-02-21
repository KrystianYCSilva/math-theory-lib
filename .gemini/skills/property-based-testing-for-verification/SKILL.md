---
name: property-based-testing-for-verification
description: |
  A guide to using property-based testing (PBT) to verify universal truths and
  algebraic properties in your code, effectively treating tests as theorem
  provers. It covers writing property tests with Kotest. Use when: verifying
  algebraic laws, mathematical theorems, or system invariants across all inputs.
tags: [testing, quality, verification, kotlin, kotest, property-based-testing]
---

# Skill: Property-Based Testing for Verification

This skill teaches you how to move beyond example-based testing and use Property-Based Testing (PBT) to verify universal properties and algebraic laws within your code. Instead of writing tests for specific inputs and expected outputs, you define a general property and let a testing framework generate hundreds of random inputs to try and falsify it.

This is particularly powerful for verifying mathematical theorems, laws of algebra, or any system invariant that must hold true for *all* possible inputs.

## 1. The Limitation of Example-Based Testing

Traditional unit testing relies on examples. To test a custom `add` function, you might write:

```kotlin
// Example-based test
test("addition works for 2 and 3") {
    add(2, 3) shouldBe 5
}
test("addition works with zero") {
    add(5, 0) shouldBe 5
}
```

This is good, but it only proves your function works for the specific examples you thought of. What about negative numbers? Large numbers? Edge cases you missed?

## 2. Property-Based Testing: Stating Universal Truths

With PBT, you state a property that should always be true. For addition, a fundamental property is **commutativity**: `a + b` should always equal `b + a`.

A PBT framework, like Kotest, will then:
1.  Generate hundreds of random pairs of `(a, b)`.
2.  Check if the property `add(a, b) == add(b, a)` holds for each pair.
3.  If it finds a pair for which the property is false (a "counter-example"), the test fails and the framework reports the exact inputs that caused the failure.

This is a much stronger guarantee of correctness.

---
## 3. How to Write Property Tests with Kotest

Kotest provides a powerful PBT implementation through its `forAll` function.

### Step 1: Define Your Property

First, identify a property, law, or invariant that must be true.
- **Commutativity:** `op(a, b) == op(b, a)`
- **Associativity:** `op(op(a, b), c) == op(a, op(b, c))`
- **Identity Element:** `op(a, identity) == a`
- **Idempotence:** `op(a) == op(op(a))`
- **Round-trip:** `decode(encode(a)) == a`

### Step 2: Implement the Test with `forAll`

Let's test the commutativity of our `add` function.

```kotlin
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.forAll

// Assume this is our custom addition function
fun add(a: Int, b: Int): Int = a + b

class AdditionLawsTest : FunSpec({

    test("Addition should be commutative") {
        // forAll will generate random Ints for 'a' and 'b'
        forAll<Int, Int> { a, b ->
            // The property we are testing:
            add(a, b) == add(b, a)
        }
    }
})
```
Kotest handles the generation of `a` and `b` automatically. If `add(10, 20)` was not equal to `add(20, 10)`, the test would fail and tell you `a=10, b=20` was a counter-example.

### Step 3: Use Arbitraries for Custom Types

For your own data types, you need to tell Kotest how to generate random instances. You do this by creating an `Arb` (arbitrary).

**Case Study: Verifying De Morgan's Laws from `mathsets-kt`**

De Morgan's laws for sets state that `(A ∪ B)ᶜ = Aᶜ ∩ Bᶜ`. This is a perfect candidate for a property test.

```kotlin
import io.kotest.core.spec.style.FunSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.set
import io.kotest.property.forAll

// Assume MathSet is our custom, immutable set implementation
// from the 'kotlin-mathematical-modeling' skill.

// 1. Create an Arbitrary for our custom MathSet type
fun <T> mathSetArb(arbT: Arb<T>): Arb<MathSet<T>> =
    Arb.set(arbT).map { MathSet.of(it) } // Simplified for example

class SetAlgebraLawsTest : FunSpec({

    test("De Morgan's Union Law: (A ∪ B)ᶜ = Aᶜ ∩ Bᶜ") {
        // 2. Use the custom arbitrary in forAll
        forAll(
            mathSetArb(Arb.int()), // Arb for MathSet<Int>
            mathSetArb(Arb.int()), // Arb for MathSet<Int>
            mathSetArb(Arb.int())  // Universe 'U' to compute complement against
        ) { a, b, u ->
            val universe = a union b union u // Ensure a and b are subsets of universe
            
            val leftSide = (a union b).complement(universe)
            val rightSide = a.complement(universe) intersect b.complement(universe)
            
            leftSide == rightSide
        }
    }
})

// Dummy implementations for the example
object MathSet { fun <T> of(s: Set<T>): MathSet<T> = TODO() }
interface MathSet<T> {
    infix fun union(other: MathSet<T>): MathSet<T> = TODO()
    infix fun intersect(other: MathSet<T>): MathSet<T> = TODO()
    fun complement(universe: MathSet<T>): MathSet<T> = TODO()
}
```

This test doesn't just check one or two examples of sets; it verifies De Morgan's law for hundreds of randomly generated sets, providing very strong evidence that the `union`, `intersect`, and `complement` implementations are correct.

---

## 4. Best Practices for PBT

-   **Start Simple:** Begin with simple properties of simple functions before tackling complex ones.
-   **Think About Invariants:** What is always true about your objects, before and after an operation?
-   **Shrinking:** Kotest will automatically "shrink" a failing input to the simplest possible counter-example. For `a=513, b=-289`, it will try to report failure for smaller numbers like `a=1, b=-1` if they also fail.
-   **Don't just test the implementation:** Test against a simpler, "dumber" model. For example, a complex, optimized function can be tested against a slow, simple, obviously correct version.

## References

1.  **Source of this Skill:** [`docs/ARCHITECTURE.md` (Section DA-05)](docs/ARCHITECTURE.md) from the `mathsets-kt` project.
2.  **Kotest Documentation:** [Property Based Testing with Kotest](https://kotest.io/docs/proptest/property-based-testing.html)
3.  **Conceptual Overview:** [What is Property Based Testing?](https://www.hillelwayne.com/post/what-is-property-based-testing/)
