package mathsets.ntheory

import kotlin.math.sqrt

/**
 * Prime generation utilities.
 */
object PrimeGenerator {
    /**
     * Classic sieve of Eratosthenes up to [limit].
     */
    fun sieve(limit: Int): List<Int> {
        require(limit >= 2) { "limit must be >= 2." }

        val composite = BooleanArray(limit + 1)
        val root = sqrt(limit.toDouble()).toInt()

        for (p in 2..root) {
            if (composite[p]) continue
            var multiple = p * p
            while (multiple <= limit) {
                composite[multiple] = true
                multiple += p
            }
        }

        return (2..limit).filter { !composite[it] }
    }

    /**
     * Segmented sieve up to [limit].
     */
    fun segmentedSieve(limit: Int, blockSize: Int = 32_768): List<Int> {
        require(limit >= 2) { "limit must be >= 2." }
        require(blockSize > 0) { "blockSize must be positive." }

        val root = sqrt(limit.toDouble()).toInt()
        val basePrimes = sieve(root)
        val result = mutableListOf<Int>()

        var low = 2
        while (low <= limit) {
            val high = minOf(limit, low + blockSize - 1)
            val mark = BooleanArray(high - low + 1)

            for (p in basePrimes) {
                var start = maxOf(p * p, ((low + p - 1) / p) * p)
                while (start <= high) {
                    mark[start - low] = true
                    start += p
                }
            }

            for (i in mark.indices) {
                if (!mark[i]) result += low + i
            }

            low = high + 1
        }

        return result
    }
}
