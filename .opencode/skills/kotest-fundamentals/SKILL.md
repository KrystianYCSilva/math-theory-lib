---
name: kotest-fundamentals
description: |
  Guidelines for writing unit and property tests with Kotest in Kotlin multiplatform projects.
  Use when: you need to create FunSpec, StringSpec, or property-based tests with Kotest matchers.
---

# Skill: Kotest Fundamentals

This skill provides essential patterns for writing effective tests using the Kotest framework in Kotlin Multiplatform projects. It covers test styles, matchers, property-based testing, and coroutine testing patterns.

## How to Write Unit Tests with Kotest

### Choose Your Test Style

Kotest supports multiple test styles. Select based on your team's preference:

**FunSpec** - Functional, nested style (recommended for mathsets-kt):
```kotlin
class SetAlgebraTest : FunSpec({
    test("union is commutative") {
        val a = MathSet.of(1, 2)
        val b = MathSet.of(3, 4)
        a union b shouldBe b union a
    }
    
    context("empty sets") {
        test("union with empty is identity") {
            val a = MathSet.of(1, 2)
            a union MathSet.empty<Int>() shouldBe a
        }
    }
})
```

**StringSpec** - Concise, string-based:
```kotlin
class NaturalNumberTest : StringSpec({
    "addition should be associative" {
        (a + b) + c shouldBe a + (b + c)
    }
})
```

### Use Kotest Matchers

Prefer Kotest's expressive matchers over `assertEquals`:
```kotlin
// Instead of: assertEquals(expected, actual)
actual shouldBe expected
actual shouldNotBe expected

// Collection matchers
list shouldContain 5
list shouldContainAll listOf(1, 2, 3)
list shouldBeEmpty()

// Boolean matchers
condition shouldBe true
condition shouldNotBe true
```

## How to Write Property-Based Tests

Property-based testing verifies universal laws across hundreds of random inputs.

### Basic Property Test Structure

```kotlin
import io.kotest.property.forAll
import io.kotest.property.arbitrary.int

test("addition is commutative") {
    forAll<Int, Int> { a, b ->
        add(a, b) == add(b, a)
    }
}
```

### Create Custom Arbitraries for Domain Types

For custom types like `MathSet`, define how to generate random instances:

```kotlin
fun <T> mathSetArb(arbT: Arb<T>): Arb<MathSet<T>> =
    Arb.set(arbT).map { MathSet.of(it) }

test("De Morgan's law: (A ∪ B)ᶜ = Aᶜ ∩ Bᶜ") {
    forAll(
        mathSetArb(Arb.int()),
        mathSetArb(Arb.int()),
        mathSetArb(Arb.int()) // Universe
    ) { a, b, u ->
        val universe = a union b union u
        val leftSide = (a union b).complement(universe)
        val rightSide = a.complement(universe) intersect b.complement(universe)
        leftSide == rightSide
    }
}
```

### Verify Algebraic Laws

Common properties to verify in mathsets-kt:

| Property | Pattern | Example |
|----------|---------|---------|
| Commutativity | `op(a, b) == op(b, a)` | Union, intersection, addition |
| Associativity | `op(op(a,b),c) == op(a,op(b,c))` | All algebraic operations |
| Identity | `op(a, identity) == a` | Empty set union, zero addition |
| Idempotence | `op(a) == op(op(a))` | Closure operations |
| Distributivity | `a * (b + c) == (a*b) + (a*c)` | Multiplication over addition |

## How to Test Coroutines

Use Kotest's coroutine support for testing suspending functions:

```kotlin
class AsyncComputationTest : FunSpec({
    test("async computation completes") {
        withTestDispatcher {
            val result = asyncComputation()
            result shouldBe ExpectedValue
        }
    }
})
```

## Best Practices

1. **Prefer FunSpec** for complex nested tests
2. **Use descriptive test names** that explain the property being tested
3. **Group related tests** with `context()` blocks
4. **Leverage shrinking** - Kotest automatically simplifies counter-examples
5. **Test against models** - Compare optimized implementations against simple reference versions

## References

1. **Source:** `.codex/skills/kotest-fundamentals/SKILL.md` from mathsets-kt
2. **Official Docs:** [Kotest Property Testing](https://kotest.io/docs/proptest/property-based-testing.html)
3. **Matchers:** [Kotest Matchers](https://kotest.io/docs/assertions/core-matchers.html)
