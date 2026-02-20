package mathsets.ntheory

/**
 * Classical arithmetic functions over positive integers.
 */
object ArithmeticFunctions {
    /**
     * Prime factorization as map p -> exponent.
     */
    fun primeFactorization(n: Long): Map<Long, Int> {
        require(n > 0L) { "n must be positive." }
        if (n == 1L) return emptyMap()

        val factors = mutableMapOf<Long, Int>()
        for (prime in PollardRho.factorize(n)) {
            factors[prime] = (factors[prime] ?: 0) + 1
        }
        return factors.entries
            .sortedBy { it.key }
            .associate { it.key to it.value }
    }

    /**
     * Euler's totient function phi(n).
     */
    fun eulerPhi(n: Long): Long {
        require(n > 0L) { "n must be positive." }
        if (n == 1L) return 1L

        var result = n
        for (prime in primeFactorization(n).keys) {
            result = (result / prime) * (prime - 1L)
        }
        return result
    }

    /**
     * Mobius function mu(n).
     */
    fun mobius(n: Long): Int {
        require(n > 0L) { "n must be positive." }
        if (n == 1L) return 1

        val exponents = primeFactorization(n).values
        if (exponents.any { it > 1 }) return 0
        return if (exponents.size % 2 == 0) 1 else -1
    }

    /**
     * Divisor-count function tau(n).
     */
    fun tau(n: Long): Long {
        require(n > 0L) { "n must be positive." }
        return primeFactorization(n).values.fold(1L) { acc, exponent -> acc * (exponent + 1L) }
    }

    /**
     * Sum-of-divisors function sigma(n).
     */
    fun sigma(n: Long): Long {
        require(n > 0L) { "n must be positive." }
        if (n == 1L) return 1L

        var result = 1L
        for ((prime, exponent) in primeFactorization(n)) {
            var sum = 1L
            var power = 1L
            repeat(exponent) {
                power *= prime
                sum += power
            }
            result *= sum
        }
        return result
    }
}
