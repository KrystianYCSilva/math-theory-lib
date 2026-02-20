package mathsets.ntheory

/**
 * Continued fractions for rational numbers.
 */
object ContinuedFraction {
    /**
     * Finite continued-fraction expansion of numerator/denominator.
     */
    fun fromRational(numerator: Long, denominator: Long): List<Long> {
        require(denominator != 0L) { "denominator cannot be zero." }

        var a = numerator
        var b = denominator
        val terms = mutableListOf<Long>()

        while (b != 0L) {
            val q = floorDiv(a, b)
            terms += q
            val r = a - q * b
            a = b
            b = r
        }

        return terms
    }

    /**
     * Convergents p_k / q_k for a continued fraction.
     */
    fun convergents(terms: List<Long>): List<Pair<Long, Long>> {
        require(terms.isNotEmpty()) { "terms cannot be empty." }

        var pPrev2 = 0L
        var pPrev1 = 1L
        var qPrev2 = 1L
        var qPrev1 = 0L

        val result = mutableListOf<Pair<Long, Long>>()
        for (term in terms) {
            val p = term * pPrev1 + pPrev2
            val q = term * qPrev1 + qPrev2
            result += p to q

            pPrev2 = pPrev1
            pPrev1 = p
            qPrev2 = qPrev1
            qPrev1 = q
        }

        return result
    }

    private fun floorDiv(a: Long, b: Long): Long {
        var q = a / b
        val r = a % b
        if (r != 0L && ((r > 0L) != (b > 0L))) {
            q -= 1L
        }
        return q
    }
}
