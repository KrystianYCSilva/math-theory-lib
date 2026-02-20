package mathsets.ntheory

/**
 * Miller-Rabin primality testing for 64-bit integers.
 */
object MillerRabin {
    private val smallPrimes = longArrayOf(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37)

    // Deterministic base set for signed 64-bit integers.
    private val deterministicBases = longArrayOf(2, 325, 9_375, 28_178, 450_775, 9_780_504, 1_795_265_022)

    /**
     * Returns true when [n] is prime under deterministic 64-bit bases.
     */
    fun isProbablePrime(n: Long): Boolean {
        if (n < 2L) return false

        for (prime in smallPrimes) {
            if (n == prime) return true
            if (n % prime == 0L) return false
        }

        val decomposition = decompose(n - 1)
        val s = decomposition.first
        val d = decomposition.second

        for (base in deterministicBases) {
            if (base % n == 0L) continue

            var x = powMod(base, d, n)
            if (x == 1L || x == n - 1L) continue

            var witnessComposite = true
            for (i in 1 until s) {
                x = mulMod(x, x, n)
                if (x == n - 1L) {
                    witnessComposite = false
                    break
                }
            }

            if (witnessComposite) return false
        }

        return true
    }

    private fun decompose(value: Long): Pair<Int, Long> {
        var d = value
        var s = 0
        while ((d and 1L) == 0L) {
            d = d ushr 1
            s += 1
        }
        return s to d
    }
}
