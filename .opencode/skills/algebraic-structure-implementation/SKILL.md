---
name: algebraic-structure-implementation
description: >-
  Guides implementation of algebraic structures (groups, rings, fields) using Kotlin's type system.
  Use when: implementing hierarchical algebraic structures with laws, homomorphisms, or quotient structures.
---

# Skill: Algebraic Structure Implementation

This skill provides patterns for implementing algebraic structures in Kotlin, from basic magmas to groups, rings, and fields. It leverages sealed types, interfaces, and property-based testing to ensure algebraic laws are satisfied.

## How to Model Algebraic Hierarchies

### Define the Base Interface

Start with the minimal structure (Magma: set + binary operation):

```kotlin
/**
 * A Magma: a set G with a closed binary operation.
 * @param G the carrier type (elements of the structure)
 */
interface Magma<G> {
    val carrier: MathSet<G>
    operator fun G.times(other: G): G
    
    // Must satisfy: closure is enforced by return type
}
```

### Build Upward with Extension

Extend base interfaces to add axioms. For Semigroup (associative magma):

```kotlin
interface Semigroup<G> : Magma<G> {
    // Associativity law (verified via property-based tests):
    // (a * b) * c == a * (b * c)
}

interface Monoid<G> : Semigroup<G> {
    val identity: G
    // Identity law: a * e == a && e * a == a
}

interface Group<G> : Monoid<G> {
    fun G.inverse(): G
    // Inverse law: a * a⁻¹ == e && a⁻¹ * a == e
}
```

### Implement Concrete Instances

For specific groups like ℤₙ (integers modulo n):

```kotlin
/**
 * The cyclic group Z_n = {0, 1, ..., n-1} under addition mod n.
 */
class CyclicGroup(private val n: NaturalNumber) : Group<NaturalNumber> {
    override val carrier = IntensionalSet(NaturalNumbers) { it < n.value }
    override val identity = NaturalNumber(0u)
    
    override fun NaturalNumber.times(other: NaturalNumber): NaturalNumber =
        NaturalNumber((this.value + other.value) % n.value)
    
    override fun NaturalNumber.inverse(): NaturalNumber =
        NaturalNumber((n.value - this.value) % n.value)
}
```

## How to Verify Algebraic Laws

Use property-based testing to verify laws across thousands of inputs:

```kotlin
class GroupLawsTest<G>(val group: Group<G>, val arbG: Arb<G>) : FunSpec({
    
    test("Associativity") {
        forAll(arbG, arbG, arbG) { a, b, c ->
            (a times b) times c == a times (b times c)
        }
    }
    
    test("Identity") {
        forAll(arbG) { a ->
            (a times group.identity == a) && 
            (group.identity times a == a)
        }
    }
    
    test("Inverse") {
        forAll(arbG) { a ->
            (a times a.inverse() == group.identity) &&
            (a.inverse() times a == group.identity)
        }
    }
})

// Usage for CyclicGroup:
test("CyclicGroup satisfies group laws") {
    val group = CyclicGroup(NaturalNumber(7u))
    val arbElement = Arb.int(0..6).map { NaturalNumber(it.toULong()) }
    GroupLawsTest(group, arbElement).execute()
}
```

## How to Implement Homomorphisms

Model structure-preserving maps between algebraic structures:

```kotlin
/**
 * A group homomorphism: φ(a * b) = φ(a) * φ(b)
 */
interface GroupHomomorphism<G, H> {
    val domain: Group<G>
    val codomain: Group<H>
    fun apply(g: G): H
    
    // Must preserve operation:
    // apply(a * b) == apply(a) * apply(b)
}

/**
 * Kernel of homomorphism: ker(φ) = {g ∈ G | φ(g) = e_H}
 */
fun <G, H> GroupHomomorphism<G, H>.kernel(): MathSet<G> =
    IntensionalSet(domain.carrier) { g ->
        apply(g) == codomain.identity
    }

/**
 * Image of homomorphism: im(φ) = {φ(g) | g ∈ G}
 */
fun <G, H> GroupHomomorphism<G, H>.image(): MathSet<H> =
    IntensionalSet(codomain.carrier) { h ->
        domain.carrier.any { g -> apply(g) == h }
    }
```

## How to Implement Quotient Structures

For quotient groups G/N where N is a normal subgroup:

```kotlin
/**
 * Quotient group G/N where N is normal in G.
 * Elements are cosets [g] = gN = {g*n | n ∈ N}.
 */
class QuotientGroup<G>(
    val group: Group<G>,
    val normalSubgroup: MathSet<G>
) {
    /** Coset representative: gN */
    fun cosetOf(g: G): MathSet<G> =
        IntensionalSet(group.carrier) { x ->
            normalSubgroup.any { n -> x == g times n }
        }
    
    /** Operation on cosets: [a] * [b] = [a * b] */
    operator fun times(cosetA: MathSet<G>, cosetB: MathSet<G>): MathSet<G> {
        // Well-definedness requires N to be normal
        TODO("Implement coset multiplication")
    }
}
```

## Best Practices

1. **Separate interface from implementation** - Define laws in interfaces, verify in tests
2. **Use value classes for carriers** - `@JvmInline value class Element(val value: Int)`
3. **Test laws generically** - Write `GroupLawsTest<G>` that works for any Group
4. **Document which laws are enforced** - Use KDoc to specify axioms
5. **Provide canonical instances** - `IntegerRing`, `RationalField`, `MatrixRing(n)`

## Common Pitfalls

| Pitfall | Solution |
|---------|----------|
| Forgetting to verify well-definedness in quotients | Prove operation doesn't depend on representative choice |
| Mixing additive/multiplicative notation | Use consistent naming: `plus/zero` vs `times/one` |
| Not handling edge cases (n=0 in Z_n) | Add `require(n > 0)` in constructor |
| Forgetting commutativity for abelian groups | Add `CommGroup<G>` interface extending `Group<G>` |

## References

1. **Source:** `algebra/src/commonMain/kotlin/mathsets/algebra/` from mathsets-kt
2. **Abstract Algebra:** Dummit, D.S.; Foote, R.M. *Abstract Algebra*. 3rd ed. Wiley, 2004.
3. **Kotlin Modeling:** [Official Kotlin Documentation on Interfaces](https://kotlinlang.org/docs/interfaces.html)
4. **Property-Based Testing:** `property-based-testing-for-verification` skill
