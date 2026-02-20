package mathsets.ntheory

import kotlin.math.ceil
import kotlin.math.sqrt

/**
 * Discrete logarithm algorithms.
 *
 * Given a prime p, generator g, and target h, finds x such that:
 *   g^x ≡ h (mod p)
 */
object DiscreteLogarithm {

    /**
     * Baby-step Giant-step algorithm.
     *
     * Time complexity: O(√p) time and space.
     *
     * @param g The generator (base).
     * @param h The target value.
     * @param p The prime modulus.
     * @return x such that g^x ≡ h (mod p), or null if no solution exists.
     */
    fun babyStepGiantStep(g: Long, h: Long, p: Long): Long? {
        require(p > 2L) { "p must be a prime > 2." }
        require(g in 1L until p) { "g must be in [1, p)." }
        require(h in 0L until p) { "h must be in [0, p)." }

        val n = ceil(sqrt((p - 1).toDouble())).toLong()

        // Baby step: build table of g^j mod p for j = 0, 1, ..., n-1
        val babySteps = mutableMapOf<Long, Long>()
        var power = 1L
        for (j in 0 until n) {
            babySteps[power] = j
            power = mulMod(power, g, p)
        }

        // Compute g^(-n) mod p using Fermat's little theorem
        // g^(-n) ≡ g^(p-1-n) (mod p)
        val gnInv = powMod(g, p - 1 - n, p)

        // Giant step: search for match
        var gamma = h
        for (i in 0 until n) {
            babySteps[gamma]?.let { j ->
                return i * n + j
            }
            gamma = mulMod(gamma, gnInv, p)
        }

        return null // No solution found
    }

    /**
     * Brute-force discrete log for small primes.
     *
     * @param g The generator (base).
     * @param h The target value.
     * @param p The prime modulus.
     * @return x such that g^x ≡ h (mod p), or null if no solution.
     */
    fun bruteForce(g: Long, h: Long, p: Long): Long? {
        require(p > 2L) { "p must be a prime > 2." }

        var power = 1L
        for (x in 0L until p - 1) {
            if (power == h) return x
            power = mulMod(power, g, p)
        }

        return null
    }

    /**
     * Modular exponentiation.
     */
    private fun powMod(base: Long, exponent: Long, modulus: Long): Long {
        var result = 1L
        var current = base % modulus
        var exp = exponent

        while (exp > 0L) {
            if ((exp and 1L) == 1L) {
                result = mulMod(result, current, modulus)
            }
            current = mulMod(current, current, modulus)
            exp = exp ushr 1
        }

        return result
    }

    /**
     * Modular multiplication avoiding overflow.
     */
    private fun mulMod(a: Long, b: Long, modulus: Long): Long {
        // For 64-bit integers within reasonable range
        // Use unsigned arithmetic for larger values
        val aU = a.toULong()
        val bU = b.toULong()
        val mU = modulus.toULong()

        var result = 0uL
        var x = aU
        var y = bU

        while (y > 0uL) {
            if ((y and 1uL) == 1uL) {
                result = (result + x) % mU
            }
            x = (x + x) % mU
            y = y shr 1
        }

        return result.toLong()
    }
}
