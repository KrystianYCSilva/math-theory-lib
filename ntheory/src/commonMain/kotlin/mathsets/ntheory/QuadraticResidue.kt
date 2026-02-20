package mathsets.ntheory

/**
 * Quadratic residue symbols.
 */
object QuadraticResidue {
    /**
     * Legendre symbol (a/p) for odd prime p.
     */
    fun legendreSymbol(a: Long, prime: Long): Int {
        require(prime > 2L && (prime and 1L) == 1L) { "prime must be an odd prime." }
        require(MillerRabin.isProbablePrime(prime)) { "prime must be prime." }

        val normalized = normalize(a, prime)
        if (normalized == 0L) return 0

        val value = powMod(normalized, (prime - 1L) / 2L, prime)
        return when (value) {
            1L -> 1
            prime - 1L -> -1
            else -> 0
        }
    }

    /**
     * Jacobi symbol (a/n) for odd n > 0.
     */
    fun jacobiSymbol(a: Long, n: Long): Int {
        require(n > 0L && (n and 1L) == 1L) { "n must be positive odd." }

        var aa = normalize(a, n)
        var nn = n
        var result = 1

        while (aa != 0L) {
            while ((aa and 1L) == 0L) {
                aa = aa ushr 1
                val r = nn % 8L
                if (r == 3L || r == 5L) result = -result
            }

            val temp = aa
            aa = nn
            nn = temp

            if ((aa % 4L == 3L) && (nn % 4L == 3L)) result = -result
            aa %= nn
        }

        return if (nn == 1L) result else 0
    }
}
