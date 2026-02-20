package mathsets.ntheory

/**
 * Pollard-Rho factor search and recursive factorization.
 */
object PollardRho {
    /**
     * Finds a non-trivial factor of [n], or [n] when search fails.
     */
    fun findFactor(n: Long): Long {
        require(n > 1L) { "n must be > 1." }

        if ((n and 1L) == 0L) return 2L
        if (MillerRabin.isProbablePrime(n)) return n

        var start = 2L
        var constant = 1L
        repeat(30) {
            val factor = rhoAttempt(n, start, constant)
            if (factor != 1L && factor != n) return factor
            start += 1L
            constant += 2L
        }

        return n
    }

    /**
     * Fully factors [n] into prime factors (multiset as sorted list).
     */
    fun factorize(n: Long): List<Long> {
        require(n >= 1L) { "n must be >= 1." }
        if (n == 1L) return emptyList()
        if (MillerRabin.isProbablePrime(n)) return listOf(n)

        val factor = findFactor(n)
        if (factor == n) return listOf(n)

        return (factorize(factor) + factorize(n / factor)).sorted()
    }

    private fun rhoAttempt(n: Long, start: Long, constant: Long): Long {
        var x = normalize(start, n)
        var y = x
        var d = 1L

        fun next(value: Long): Long = normalize(mulMod(value, value, n) + constant, n)

        var iterations = 0
        while (d == 1L && iterations < 250_000) {
            x = next(x)
            y = next(next(y))
            d = gcd(x - y, n)
            iterations += 1
        }

        return if (d == 0L) n else d
    }
}
