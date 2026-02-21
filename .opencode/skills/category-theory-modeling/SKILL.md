---
name: category-theory-modeling
description: >-
  Models categories, functors, and natural transformations using Kotlin's type system.
  Use when: formalizing mathematical structures categorically, defining universal properties, or working with diagram commutativity.
---

# Skill: Category Theory Modeling

This skill provides patterns for implementing category theory concepts in Kotlin. It leverages higher-kinded types (via interfaces), sealed types, and composition to model categories, functors, and natural transformations rigorously.

## How to Model Categories

### Define Category Interface

```kotlin
/**
 * A Category C consists of:
 * - Objects: Ob(C)
 * - Morphisms: Hom(A,B) for each pair A,B ∈ Ob(C)
 * - Composition: ∘ : Hom(B,C) × Hom(A,B) → Hom(A,C)
 * - Identity: id_A ∈ Hom(A,A) for each A
 * 
 * Satisfying:
 * - Associativity: h ∘ (g ∘ f) = (h ∘ g) ∘ f
 * - Left identity: id_B ∘ f = f
 * - Right identity: f ∘ id_A = f
 */
interface Category<Obj, Mor> {
    /** Source/object domain of morphism */
    fun domain(mor: Mor): Obj
    
    /** Target/object codomain of morphism */
    fun codomain(mor: Mor): Obj
    
    /** Identity morphism for object A */
    fun id(A: Obj): Mor
    
    /** Composition: g ∘ f (apply f first, then g) */
    infix fun Mor.compose(g: Mor): Mor
    
    // Must satisfy associativity and identity laws
}
```

### Concrete Category: Set (Finite Sets)

```kotlin
/**
 * Category FinSet: finite sets and functions between them.
 */
object FinSetCategory : Category<MathSet<*>, Function<*, *>> {
    
    override fun domain(mor: Function<*, *>): MathSet<*> = mor.domain
    
    override fun codomain(mor: Function<*, *>): MathSet<*> = mor.codomain
    
    override fun id(A: MathSet<*>): Function<*, *> = 
        MathFunction.identity(A)
    
    override infix fun Function<*, *>.compose(g: Function<*, *>): Function<*, *> {
        require(this.codomain == g.domain) {
            "Codomain of f must match domain of g"
        }
        return this compose g // Function composition
    }
}
```

### Concrete Category: Grp (Groups and Homomorphisms)

```kotlin
/**
 * Category Grp: groups and group homomorphisms.
 */
object GrpCategory : Category<Group<*>, GroupHomomorphism<*, *>> {
    
    override fun domain(mor: GroupHomomorphism<*, *>): Group<*> = mor.domain
    
    override fun codomain(mor: GroupHomomorphism<*, *>): Group<*> = mor.codomain
    
    override fun id(G: Group<*>): GroupHomomorphism<*, *> =
        object : GroupHomomorphism<Any, Any> {
            override val domain = G
            override val codomain = G
            @Suppress("UNCHECKED_CAST")
            override fun apply(g: Any): Any = g as Any
        }
    
    override infix fun GroupHomomorphism<*, *>.compose(
        g: GroupHomomorphism<*, *>
    ): GroupHomomorphism<*, *> {
        require(this.codomain == g.domain)
        
        return object : GroupHomomorphism<Any, Any> {
            override val domain = this@compose.domain
            override val codomain = g.codomain
            
            @Suppress("UNCHECKED_CAST")
            override fun apply(x: Any): Any =
                g.apply(this@compose.apply(x))
        }
    }
}
```

## How to Model Functors

### Functor Definition

```kotlin
/**
 * A functor F: C → D maps:
 * - Objects: Ob(C) → Ob(D)
 * - Morphisms: Hom_C(A,B) → Hom_D(F(A), F(B))
 * 
 * Satisfying:
 * - F(id_A) = id_{F(A)}
 * - F(g ∘ f) = F(g) ∘ F(f)
 */
interface Functor<C: Category<*, *>, D: Category<*, *>> {
    /** Map objects: A ↦ F(A) */
    fun mapObj(obj: C.Obj): D.Obj
    
    /** Map morphisms: f ↦ F(f) */
    fun mapMor(mor: C.Mor): D.Mor
    
    // Must preserve identity and composition
}
```

### Forgetful Functor: Grp → Set

```kotlin
/**
 * Forgetful functor U: Grp → Set.
 * Maps group to underlying set, homomorphism to function.
 */
class ForgetfulFunctorGrpSet : 
    Functor<GrpCategory, FinSetCategory> {
    
    override fun mapObj(obj: Group<*>): MathSet<*> =
        obj.carrier
    
    override fun mapMor(mor: GroupHomomorphism<*, *>): Function<*, *> =
        mor.asFunction()
}
```

### Hom-Functor: Hom(A, -)

```kotlin
/**
 * Covariant Hom-functor Hom(A, -): C → Set.
 * Maps X ↦ Hom(A,X), f ↦ (g ↦ f ∘ g)
 */
class CovariantHomFunctor<A>(
    private val category: Category<*, *>,
    private val fixedObject: Any
) : Functor<Category<*, *>, FinSetCategory> {
    
    override fun mapObj(obj: Any): MathSet<*> {
        // Hom(A, X) = set of all morphisms from A to X
        TODO("Return set of morphisms")
    }
    
    override fun mapMor(mor: Any): Function<*, *> {
        // Post-composition: g ↦ f ∘ g
        TODO("Return post-composition function")
    }
}
```

## How to Model Natural Transformations

### Natural Transformation Definition

```kotlin
/**
 * A natural transformation η: F ⇒ G between functors F,G: C → D.
 * For each object X in C, gives morphism η_X: F(X) → G(X) in D.
 * 
 * Naturality condition: For f: X → Y in C,
 *   G(f) ∘ η_X = η_Y ∘ F(f)
 * 
 * This makes the naturality square commute.
 */
interface NaturalTransformation<
    C: Category<*, *>,
    D: Category<*, *>,
    F: Functor<C, D>,
    G: Functor<C, D>
> {
    /** Component at object X: η_X: F(X) → G(X) */
    fun component(X: C.Obj): D.Mor
    
    // Must satisfy naturality condition for all morphisms in C
}
```

### Example: Double Dual Natural Transformation

For vector spaces V → V**:

```kotlin
/**
 * Natural transformation from identity functor to double dual functor.
 * η_V: V → V** where η_V(v)(φ) = φ(v) for φ ∈ V*
 */
class DoubleDualNaturalTransformation(
    val field: Field
) : NaturalTransformation<VecCategory, VecCategory, IdFunctor, DoubleDualFunctor> {
    
    override fun component(V: VectorSpace): LinearMap {
        // η_V: V → V**
        return object : LinearMap {
            override val domain = V
            override val codomain = V.doubleDual()
            
            override fun apply(v: Vector): Vector {
                // η_V(v) ∈ V** is evaluation at v
                return object : Vector {
                    override fun apply(phi: Covector): Scalar {
                        // η_V(v)(φ) = φ(v)
                        return phi(v)
                    }
                }
            }
        }
    }
}
```

## How to Verify Category Laws

Use property-based testing:

```kotlin
class CategoryLawsTest<C: Category<Obj, Mor>>(
    val category: C,
    val arbObj: Arb<Obj>,
    val arbMor: Arb<Mor>
) : FunSpec({
    
    test("Associativity of composition") {
        forAll(arbMor, arbMor, arbMor) { f, g, h ->
            // Need compatible domains/codomains
            assume(category.codomain(f) == category.domain(g))
            assume(category.codomain(g) == category.domain(h))
            
            val left = (f compose g) compose h
            val right = f compose (g compose h)
            left shouldBe right
        }
    }
    
    test("Left identity") {
        forAll(arbMor) { f ->
            val A = category.domain(f)
            val idA = category.id(A)
            (idA compose f) shouldBe f
        }
    }
    
    test("Right identity") {
        forAll(arbMor) { f ->
            val B = category.codomain(f)
            val idB = category.id(B)
            (f compose idB) shouldBe f
        }
    }
})
```

## Best Practices

1. **Use type parameters carefully** - `Category<Obj, Mor>` allows flexibility
2. **Enforce composition compatibility** - Check domain/codomain matching
3. **Test laws generically** - Write tests that work for any Category instance
4. **Document naturality squares** - Draw diagrams in KDoc
5. **Leverage sealed types** - For finite categories or specific examples

## Common Constructions Reference

| Construction | Description | Method |
|--------------|-------------|--------|
| Opposite Category | C^op reverses arrows | `category.opposite()` |
| Product Category | C × D has pairs | `C product D` |
| Functor Category | [C, D] has functors | `FunctorCategory(C, D)` |
| Slice Category | C/A has objects over A | `sliceCategory(A)` |
| Comma Category | (F ↓ G) generalizes slice | `commaCategory(F, G)` |

## References

1. **Source:** `category/src/commonMain/kotlin/mathsets/category/` from mathsets-kt
2. **Category Theory:** Mac Lane, S. *Categories for the Working Mathematician*. 2nd ed. Springer, 1998.
3. **Applied Category Theory:** Fong, B.; Spivak, D.I. *Seven Sketches in Compositionality*. Cambridge, 2019.
4. **Kotlin Higher-Kinded Types:** [Workaround via Interfaces](https://kotlinlang.org/docs/generics.html)
