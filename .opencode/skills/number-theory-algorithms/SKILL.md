---
name: number-theory-algorithms
description: >-
  Implements number-theoretic algorithms (primality, factorization, modular arithmetic).
  Use when: working with primes, computing GCD/LCM, solving congruences, or implementing cryptographic primitives.
---

# Skill: Number Theory Algorithms

This skill provides patterns for implementing computational number theory in Kotlin. It covers primality testing, integer factorization, modular arithmetic, and Diophantine equations using exact arithmetic and efficient algorithms.

## How to Implement Primality Testing

### Trial Division (Small Numbers)

```kotlin
/**
 * Trial division primality test.
 * Returns true if n is prime, false otherwise.
 * Complexity: O(√n)
 */
fun NaturalNumber.isPrimeTrialDivision(): Boolean {
    if (this < NaturalNumber(2u)) return false
    if (this == NaturalNumber(2u)) return true
    if (this.value % 2u == 0u) return false
    
    var i = NaturalNumber(3u)
    while (i.value * i.value <= this.value) {
        if (this.value % i.value == 0u) return false
        i += NaturalNumber(2u)
    }
    
    return true
}
```

### Miller-Rabin Probabilistic Test (Large Numbers)

```kotlin
/**
 * Miller-Rabin primality test.
 * @param k number of rounds (higher = more certain)
 * @return true if probably prime, false if definitely composite
 * Complexity: O(k log³ n)
 */
fun NaturalNumber.millerRabin(k: Int = 40): Boolean {
    if (this < NaturalNumber(2u)) return false
    if (this == NaturalNumber(2u) || this == NaturalNumber(3u)) return true
    if (this.value % 2u == 0u) return false
    
    // Write n-1 as 2^r * d where d is odd
    val nMinus1 = this - NaturalNumber(1u)
    var r = 0
    var d = nMinus1
    
    while (d.value % 2u == 0u) {
        d /= NaturalNumber(2u)
        r++
    }
    
    // Witness loop
    for (i in 0 until k) {
        val a = randomWitness(this)
        var x = modPow(a, d, this)
        
        if (x == NaturalNumber(1u) || x == nMinus1) continue
        
        var composite = true
        for (j in 0 until r - 1) {
            x = modPow(x, NaturalNumber(2u), this)
            if (x == nMinus1) {
                composite = false
                break
            }
        }
        
        if (composite) return false // Definitely composite
    }
    
    return true // Probably prime with high confidence
}

/** Modular exponentiation: a^b mod n */
fun modPow(base: NaturalNumber, exp: NaturalNumber, modulus: NaturalNumber): NaturalNumber {
    var result = NaturalNumber(1u)
    var b = base % modulus
    var e = exp
    
    while (e > NaturalNumber(0u)) {
        if (e.value % 2u == 1u) {
            result = (result * b) % modulus
        }
        e /= NaturalNumber(2u)
        b = (b * b) % modulus
    }
    
    return result
}
```

## How to Compute GCD and LCM

### Euclidean Algorithm

```kotlin
/**
 * Greatest common divisor via Euclidean algorithm.
 * gcd(a, b) = largest d such that d|a and d|b
 * Complexity: O(log(min(a,b)))
 */
fun gcd(a: NaturalNumber, b: NaturalNumber): NaturalNumber {
    var x = a
    var y = b
    
    while (y > NaturalNumber(0u)) {
        val temp = y
        y = x % y
        x = temp
    }
    
    return x
}

/**
 * Extended Euclidean algorithm.
 * Returns (g, s, t) where g = gcd(a,b) and a*s + b*t = g
 * Useful for modular inverse computation.
 */
fun extendedGcd(a: NaturalNumber, b: NaturalNumber): Triple<NaturalNumber, IntegerNumber, IntegerNumber> {
    if (b == NaturalNumber(0u)) {
        return Triple(a, IntegerNumber(1), IntegerNumber(0))
    }
    
    val (g, s1, t1) = extendedGcd(b, a % b)
    val q = a / b
    
    val s = t1
    val t = s1 - (q.toInteger() * t1)
    
    return Triple(g, s, t)
}

/** Least common multiple: lcm(a,b) = (a*b)/gcd(a,b) */
fun lcm(a: NaturalNumber, b: NaturalNumber): NaturalNumber {
    if (a == NaturalNumber(0u) || b == NaturalNumber(0u)) {
        return NaturalNumber(0u)
    }
    return (a * b) / gcd(a, b)
}
```

## How to Work with Modular Arithmetic

### Modular Inverse

```kotlin
/**
 * Modular multiplicative inverse: a⁻¹ mod n.
 * Returns x such that a*x ≡ 1 (mod n).
 * Exists iff gcd(a,n) = 1.
 */
fun modInverse(a: NaturalNumber, n: NaturalNumber): NaturalNumber? {
    val (g, s, _) = extendedGcd(a, n)
    
    if (g != NaturalNumber(1u)) {
        return null // Inverse doesn't exist
    }
    
    // Ensure positive result
    val result = s.toNaturalOrNull() ?: (n - (-s).toNatural())
    return result % n
}
```

### Chinese Remainder Theorem

```kotlin
/**
 * Chinese Remainder Theorem solver.
 * Given:
 *   x ≡ a₁ (mod n₁)
 *   x ≡ a₂ (mod n₂)
 *   ...
 *   x ≡ aₖ (mod nₖ)
 * where all nᵢ are pairwise coprime.
 * 
 * Returns unique solution x mod N where N = n₁*n₂*...*nₖ.
 */
fun chineseRemainderTheorem(
    remainders: List<NaturalNumber>,
    moduli: List<NaturalNumber>
): NaturalNumber? {
    require(remainders.size == moduli.size)
    require(moduli.size > 0)
    
    // Verify pairwise coprimality
    for (i in 0 until moduli.size - 1) {
        for (j in i + 1 until moduli.size) {
            if (gcd(moduli[i], moduli[j]) != NaturalNumber(1u)) {
                return null // Moduli must be coprime
            }
        }
    }
    
    // N = product of all moduli
    val N = moduli.reduce { acc, n -> acc * n }
    
    var x = NaturalNumber(0u)
    
    for (i in remainders.indices) {
        val ni = moduli[i]
        val ai = remainders[i]
        val Ni = N / ni
        val yi = modInverse(Ni, ni) ?: return null
        
        x += ai * Ni * yi
    }
    
    return x % N
}
```

## How to Factor Integers

### Pollard's Rho Algorithm

For factoring composite numbers:

```kotlin
/**
 * Pollard's rho algorithm for integer factorization.
 * Returns a non-trivial factor of n (not necessarily prime).
 * Complexity: O(n^(1/4)) expected
 */
fun pollardRho(n: NaturalNumber): NaturalNumber? {
    if (n <= NaturalNumber(1u)) return null
    if (n.value % 2u == 0u) return NaturalNumber(2u)
    
    var x = NaturalNumber(2u)
    var y = NaturalNumber(2u)
    var d = NaturalNumber(1u)
    
    val f = { num: NaturalNumber -> (num * num + NaturalNumber(1u)) % n }
    
    while (d == NaturalNumber(1u)) {
        x = f(x)
        y = f(f(y))
        d = gcd(if (x > y) x - y else y - x, n)
    }
    
    return if (d != n) d else null // Failed if d == n
}

/**
 * Complete factorization into prime powers.
 * Returns list of (prime, exponent) pairs.
 */
fun factorize(n: NaturalNumber): List<Pair<NaturalNumber, Int>> {
    if (n <= NaturalNumber(1u)) return emptyList()
    
    val factors = mutableMapOf<NaturalNumber, Int>()
    var remaining = n
    
    // Remove factors of 2
    while (remaining.value % 2u == 0u) {
        factors[NaturalNumber(2u)] = (factors[NaturalNumber(2u)] ?: 0) + 1
        remaining /= NaturalNumber(2u)
    }
    
    // Use Pollard's rho for odd factors
    while (remaining > NaturalNumber(1u)) {
        if (remaining.millerRabin()) {
            // Remaining is prime
            factors[remaining] = (factors[remaining] ?: 0) + 1
            break
        }
        
        val factor = pollardRho(remaining) ?: break
        var count = 0
        while (remaining % factor == NaturalNumber(0u)) {
            remaining /= factor
            count++
        }
        factors[factor] = (factors[factor] ?: 0) + count
    }
    
    return factors.toList().sortedBy { it.first }
}
```

## How to Solve Linear Congruences

### ax ≡ b (mod n)

```kotlin
/**
 * Solve linear congruence: a*x ≡ b (mod n).
 * Returns all solutions modulo n, or empty if no solution exists.
 */
fun solveLinearCongruence(
    a: NaturalNumber,
    b: NaturalNumber,
    n: NaturalNumber
): List<NaturalNumber> {
    val g = gcd(a, n)
    
    // Solution exists iff g divides b
    if (b % g != NaturalNumber(0u)) {
        return emptyList()
    }
    
    // Reduce to (a/g)*x ≡ (b/g) (mod n/g)
    val aPrime = a / g
    val bPrime = b / g
    val nPrime = n / g
    
    // Find inverse of a' mod n'
    val invA = modInverse(aPrime, nPrime) ?: return emptyList()
    
    // Particular solution
    val x0 = (invA * bPrime) % nPrime
    
    // All solutions: x₀ + k*(n/g) for k = 0, 1, ..., g-1
    return (0 until g.value.toInt()).map { k ->
        x0 + NaturalNumber(k.toULong()) * nPrime
    }
}
```

## Best Practices

1. **Use exact arithmetic** - Always use `NaturalNumber`/`IntegerNumber`, never `Double`
2. **Choose algorithm by size** - Trial division for n < 10⁶, Miller-Rabin for larger
3. **Verify coprimality** - Check gcd(a,n) = 1 before assuming inverses exist
4. **Handle edge cases** - n=0, n=1, negative inputs appropriately
5. **Cache small primes** - Precompute primes up to 10⁶ for faster trial division

## Algorithm Reference

| Algorithm | Purpose | Complexity |
|-----------|---------|------------|
| Trial Division | Primality (small n) | O(√n) |
| Miller-Rabin | Primality (large n) | O(k log³ n) |
| Euclidean | GCD | O(log n) |
| Extended Euclidean | GCD + Bézout coefficients | O(log n) |
| Modular Exponentiation | a^b mod n | O(log b) |
| Pollard's Rho | Factorization | O(n^(1/4)) |
| CRT | Solve system of congruences | O(k² log² N) |

## References

1. **Source:** `ntheory/src/commonMain/kotlin/mathsets/ntheory/` from mathsets-kt
2. **Number Theory:** Rosen, K.H. *Elementary Number Theory and Its Applications*. 6th ed. Pearson, 2011.
3. **Algorithms:** Cormen, T.H. et al. *Introduction to Algorithms*. 3rd ed. MIT Press, 2009. Ch. 31 (Number-Theoretic Algorithms).
4. **Cryptography Applications:** Stinson, D.R. *Cryptography: Theory and Practice*. 3rd ed. Chapman & Hall/CRC, 2005.
