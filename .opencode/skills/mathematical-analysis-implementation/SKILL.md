---
name: mathematical-analysis-implementation
description: >-
  Implements real analysis concepts (limits, continuity, derivatives, integrals) with rigorous foundations.
  Use when: computing limits, proving continuity, differentiating functions, or evaluating Riemann integrals.
---

# Skill: Mathematical Analysis Implementation

This skill provides patterns for implementing real analysis in Kotlin with mathematical rigor. It covers Cauchy sequences, Dedekind cuts, limits, continuity, differentiation, and integration using constructive methods.

## How to Construct Real Numbers

### Via Cauchy Sequences

```kotlin
/**
 * A Cauchy sequence of rationals: ∀ε>0, ∃N s.t. ∀m,n>N: |aₘ - aₙ| < ε.
 * Represents a real number as the limit of the sequence.
 */
class CauchySequence(
    private val terms: Sequence<RationalNumber>,
    private val convergenceModulus: (RationalNumber) -> NaturalNumber
) {
    /** Get n-th term of sequence */
    fun term(n: NaturalNumber): RationalNumber =
        terms.take(n.value.toInt() + 1).last()
    
    /** Verify Cauchy property: |aₘ - aₙ| < ε for m,n ≥ N(ε) */
    fun verifyCauchy(epsilon: RationalNumber): Boolean {
        require(epsilon > RationalNumber.zero)
        val N = convergenceModulus(epsilon)
        
        // Check finite sample (practical verification)
        for (m in N.value.toInt()..(N.value.toInt() + 100)) {
            for (n in N.value.toInt()..(N.value.toInt() + 100)) {
                val diff = abs(term(NaturalNumber(m.toULong())) - 
                              term(NaturalNumber(n.toULong())))
                if (diff >= epsilon) return false
            }
        }
        return true
    }
}

/** Cauchy sequence converging to √2 via Newton's method */
fun cauchySqrt2(): CauchySequence {
    var current = RationalNumber(1)
    
    val sequence = generateSequence {
        // Newton iteration: x_{n+1} = (x_n + 2/x_n) / 2
        val next = (current + RationalNumber(2) / current) / RationalNumber(2)
        current = next
        current
    }
    
    return CauchySequence(
        sequence,
        convergenceModulus = { epsilon ->
            // Estimate N such that |aₘ - aₙ| < ε for m,n ≥ N
            // For Newton on √2, convergence is quadratic
            NaturalNumber((kotlin.math.log2(epsilon.toDouble().absoluteValue) / 2).toULong())
        }
    )
}
```

### Via Dedekind Cuts

```kotlin
/**
 * A Dedekind cut: partition (L, R) of ℚ where:
 * - L is non-empty, closed downward, has no maximum
 * - R is non-empty, closed upward, has no minimum
 * - ∀l∈L, ∀r∈R: l < r
 */
class DedekindCut(
    val leftSet: MathSet<RationalNumber>,
    val rightSet: MathSet<RationalNumber>
) {
    init {
        // Verify cut properties
        require(leftSet.isNotEmpty()) { "Left set must be non-empty" }
        require(rightSet.isNotEmpty()) { "Right set must be non-empty" }
        
        // Partition: L ∪ R = ℚ, L ∩ R = ∅
        require(leftSet union rightSet == UniversalSets.Rationals)
        require(leftSet intersect rightSet == MathSet.empty())
        
        // Downward closure of L
        require(leftSet.all { q ->
            UniversalSets.Rationals.filter { it < q }.all { it in leftSet }
        }) { "Left set must be closed downward" }
        
        // No maximum in L
        require(leftSet.all { q ->
            leftSet.any { it > q }
        }) { "Left set must have no maximum" }
    }
    
    /** Cut representing √2 */
    companion object {
        fun sqrt2(): DedekindCut {
            val left = IntensionalSet(UniversalSets.Rationals) { q ->
                q < RationalNumber.zero || (q >= RationalNumber.zero && q * q < RationalNumber(2))
            }
            val right = IntensionalSet(UniversalSets.Rationals) { q ->
                q >= RationalNumber.zero && q * q > RationalNumber(2)
            }
            return DedekindCut(left, right)
        }
    }
}
```

## How to Compute Limits

### Limit of Sequence

```kotlin
/**
 * lim_{n→∞} aₙ = L means: ∀ε>0, ∃N s.t. ∀n>N: |aₙ - L| < ε.
 * 
 * @param tolerance ε for convergence test
 * @return Approximate limit if convergent, null otherwise
 */
fun Sequence<RationalNumber>.computeLimit(
    tolerance: RationalNumber = RationalNumber(1, 1000),
    maxIterations: Int = 10000
): RationalNumber? {
    val terms = this.take(maxIterations).toList()
    
    // Check if sequence stabilizes within tolerance
    for (i in terms.size - 1 downTo terms.size / 2) {
        val recentTerms = terms.subList(i, terms.size)
        val avg = recentTerms.average()
        val maxDeviation = recentTerms.maxOf { abs(it - avg) }
        
        if (maxDeviation < tolerance.toDouble()) {
            return avg
        }
    }
    
    return null // Did not converge
}
```

### Limit of Function (ε-δ Definition)

```kotlin
/**
 * lim_{x→a} f(x) = L means: ∀ε>0, ∃δ>0 s.t. ∀x: 0<|x-a|<δ → |f(x)-L|<ε.
 * 
 * Numerically approximates limit by sampling points near a.
 */
fun computeFunctionLimit(
    f: (RationalNumber) -> RationalNumber,
    a: RationalNumber,
    targetTolerance: RationalNumber = RationalNumber(1, 1000)
): RationalNumber? {
    var delta = RationalNumber(1)
    
    for (iteration in 0..20) {
        // Sample points at distance δ/2, δ/4, δ/8, ... from a
        val samples = listOf(1, 2, 3, 4).map { k ->
            val offset = delta / RationalNumber(2.pow(k))
            listOf(f(a - offset), f(a + offset))
        }.flatten()
        
        // Check if all samples are within tolerance of each other
        val minVal = samples.minOrNull()!!
        val maxVal = samples.maxOrNull()!!
        
        if (maxVal - minVal < targetTolerance) {
            return samples.average()
        }
        
        delta /= RationalNumber(2)
    }
    
    return null // Limit may not exist
}
```

## How to Test Continuity

### ε-δ Continuity at Point

```kotlin
/**
 * f is continuous at a if: ∀ε>0, ∃δ>0 s.t. ∀x: |x-a|<δ → |f(x)-f(a)|<ε.
 * 
 * Tests continuity numerically with specified precision.
 */
fun isContinuousAt(
    f: (RationalNumber) -> RationalNumber,
    a: RationalNumber,
    epsilon: RationalNumber = RationalNumber(1, 10000)
): Boolean {
    val fa = f(a)
    
    // Binary search for δ
    var delta = RationalNumber(1)
    
    for (iteration in 0..30) {
        // Test interval (a-δ, a+δ)
        val testPoints = listOf(
            a - delta, a - delta / RationalNumber(2),
            a - delta / RationalNumber(10),
            a + delta / RationalNumber(10),
            a + delta / RationalNumber(2),
            a + delta
        )
        
        val violations = testPoints.count { x ->
            try {
                val fx = f(x)
                abs(fx - fa) >= epsilon
            } catch (e: Exception) {
                true // Undefined point counts as violation
            }
        }
        
        if (violations == 0) {
            return true // Found valid δ
        }
        
        delta /= RationalNumber(2)
    }
    
    return false // Could not find δ
}
```

## How to Compute Derivatives

### Definition-Based Derivative

```kotlin
/**
 * f'(a) = lim_{h→0} [f(a+h) - f(a)] / h
 * 
 * Computes derivative numerically using symmetric difference quotient.
 */
fun derivative(
    f: (RationalNumber) -> RationalNumber,
    a: RationalNumber,
    h: RationalNumber = RationalNumber(1, 1000000)
): RationalNumber {
    // Symmetric difference: [f(a+h) - f(a-h)] / (2h)
    // More accurate than forward difference
    val fp = f(a + h)
    val fm = f(a - h)
    
    return (fp - fm) / (RationalNumber(2) * h)
}

/** Higher-order derivatives: f''(a), f'''(a), etc. */
fun nthDerivative(
    f: (RationalNumber) -> RationalNumber,
    a: RationalNumber,
    n: NaturalNumber,
    h: RationalNumber = RationalNumber(1, 10000)
): RationalNumber {
    if (n == NaturalNumber(0u)) return f(a)
    
    // Recursive definition: f^{(n)}(a) = (f^{(n-1)})'(a)
    val g = { x: RationalNumber -> derivative(f, x, h) }
    return nthDerivative(g, a, n - NaturalNumber(1u), h)
}
```

## How to Compute Integrals

### Riemann Integral

```kotlin
/**
 * Riemann integral via midpoint rule.
 * ∫_a^b f(x) dx ≈ Σᵢ f((xᵢ+xᵢ₊₁)/2) · Δx
 * 
 * @param n number of subintervals (higher = more accurate)
 */
fun riemannIntegrate(
    f: (RationalNumber) -> RationalNumber,
    a: RationalNumber,
    b: RationalNumber,
    n: NaturalNumber = NaturalNumber(1000u)
): RationalNumber {
    require(a <= b) { "Integration bounds must satisfy a ≤ b" }
    
    val deltaX = (b - a) / RationalNumber(n.value.toInt())
    var sum = RationalNumber.zero
    
    for (i in 0 until n.value.toInt()) {
        val xLeft = a + RationalNumber(i) * deltaX
        val xRight = xLeft + deltaX
        val midpoint = (xLeft + xRight) / RationalNumber(2)
        
        sum += f(midpoint) * deltaX
    }
    
    return sum
}

/** Trapezoidal rule: often more accurate than midpoint */
fun trapezoidalIntegrate(
    f: (RationalNumber) -> RationalNumber,
    a: RationalNumber,
    b: RationalNumber,
    n: NaturalNumber = NaturalNumber(1000u)
): RationalNumber {
    val deltaX = (b - a) / RationalNumber(n.value.toInt())
    
    var sum = (f(a) + f(b)) / RationalNumber(2)
    
    for (i in 1 until n.value.toInt()) {
        val xi = a + RationalNumber(i) * deltaX
        sum += f(xi)
    }
    
    return sum * deltaX
}
```

## Best Practices

1. **Use exact arithmetic** - Prefer `RationalNumber` over `Double` for correctness
2. **Verify convergence** - Always check Cauchy property or provide convergence modulus
3. **Handle singularities** - Detect undefined points, asymptotes, discontinuities
4. **Adaptive precision** - Increase sampling density near problematic regions
5. **Document error bounds** - Specify approximation accuracy for numerical methods

## Common Constructions Reference

| Construction | Method | Complexity |
|--------------|--------|------------|
| √2 via Cauchy | Newton iteration | O(log n) digits |
| √2 via Dedekind | Cut {q ∈ ℚ : q² < 2} | N/A (exact) |
| e via Cauchy | Σ 1/n! | O(n) terms |
| π via Cauchy | Leibniz/Machin series | O(n) terms |
| Derivative | Symmetric difference | O(1) evaluations |
| Integral | Midpoint/Trapezoidal | O(n) evaluations |

## References

1. **Source:** `analysis/src/commonMain/kotlin/mathsets/analysis/` and `construction/real/` from mathsets-kt
2. **Real Analysis:** Rudin, W. *Principles of Mathematical Analysis*. 3rd ed. McGraw-Hill, 1976.
3. **Constructive Analysis:** Bishop, E.; Bridges, D. *Constructive Analysis*. Springer, 1985.
4. **Dedekind Cuts:** Dedekind, R. *Continuity and Irrational Numbers*. 1872. Reprinted in *Essays on the Theory of Numbers*, Dover, 1963.
