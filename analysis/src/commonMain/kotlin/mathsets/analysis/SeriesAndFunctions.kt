package mathsets.analysis

import mathsets.kernel.RealNumber

/**
 * Finite power-series representation `sum c_n x^n`.
 *
 * @property coefficients Coefficients in ascending degree order.
 */
data class PowerSeries(val coefficients: List<RealNumber>) {
    /**
     * Evaluates the series truncation at x.
     *
     * @param x Evaluation point.
     * @param terms Number of terms to use.
     * @return Truncated sum value.
     * @throws IllegalArgumentException if [terms] is negative.
     */
    fun evaluate(x: RealNumber, terms: Int = coefficients.size): RealNumber {
        require(terms >= 0) { "terms must be non-negative." }
        var sum = RealNumber.ZERO
        var power = RealNumber.ONE
        for (i in 0 until minOf(terms, coefficients.size)) {
            sum += coefficients[i] * power
            power *= x
        }
        return sum
    }

    /**
     * Returns the formal derivative series.
     *
     * @return Derivative power series.
     */
    fun derivative(): PowerSeries {
        if (coefficients.size <= 1) return PowerSeries(listOf(RealNumber.ZERO))
        val derived = (1 until coefficients.size).map { n -> coefficients[n] * RealNumber.of(n) }
        return PowerSeries(derived)
    }
}

/**
 * Series helpers.
 */
object Series {
    /**
     * Returns the sequence of partial sums up to n.
     *
     * @param sequence Source sequence.
     * @param n Last partial-sum index.
     * @return List `[S_1, ..., S_n]`.
     * @throws IllegalArgumentException if [n] is not positive.
     */
    fun partialSums(sequence: RealSequence, n: Int): List<RealNumber> {
        require(n > 0) { "n must be positive." }
        val sums = mutableListOf<RealNumber>()
        var acc = RealNumber.ZERO
        for (k in 1..n) {
            acc += sequence.term(k)
            sums += acc
        }
        return sums
    }

    /**
     * Approximates infinite-series convergence using partial sums.
     *
     * @param sequence Source sequence.
     * @param tolerance Tail closeness tolerance.
     * @param maxN Maximum partial sum index.
     * @param window Tail window size.
     * @return Convergence classification.
     */
    fun approximateInfiniteSum(
        sequence: RealSequence,
        tolerance: RealNumber,
        maxN: Int = 20_000,
        window: Int = 120
    ): Limit<RealNumber> {
        require(maxN >= window && window >= 2) { "Require maxN >= window >= 2." }

        val partial = partialSums(sequence, maxN)
        val tail = partial.takeLast(window)
        val stable = tail.all { a -> tail.all { b -> (a - b).abs() <= tolerance } }
        if (!stable) return Limit.Diverges

        val average = tail.fold(RealNumber.ZERO) { acc, v -> acc + v } / RealNumber.of(tail.size)
        return Limit.Converges(average)
    }

    /**
     * Builds a geometric sequence `a*r^(n-1)`.
     *
     * @param first First term `a`.
     * @param ratio Ratio `r`.
     * @return Generated sequence.
     */
    fun geometric(first: RealNumber, ratio: RealNumber): RealSequence =
        RealSequence { n -> first * (ratio pow (n - 1)) }
}

/**
 * Elementary real functions via power-series / iterative approximations.
 */
object ElementaryFunctions {
    /**
     * Exponential function approximation `exp(x)` via Taylor series.
     *
     * @param x Input value.
     * @param terms Number of Taylor terms.
     * @return Approximate exponential value.
     * @throws IllegalArgumentException if [terms] is not positive.
     */
    fun exp(x: RealNumber, terms: Int = 40): RealNumber {
        require(terms > 0) { "terms must be positive." }
        var sum = RealNumber.ONE
        var term = RealNumber.ONE
        for (n in 1 until terms) {
            term = term * x / RealNumber.of(n)
            sum += term
        }
        return sum
    }

    /**
     * Sine approximation `sin(x)` via Taylor series.
     *
     * @param x Input value.
     * @param terms Number of non-zero terms.
     * @return Approximate sine value.
     * @throws IllegalArgumentException if [terms] is not positive.
     */
    fun sin(x: RealNumber, terms: Int = 24): RealNumber {
        require(terms > 0) { "terms must be positive." }
        var sum = RealNumber.ZERO
        for (k in 0 until terms) {
            val sign = if (k % 2 == 0) RealNumber.ONE else RealNumber.of(-1)
            val exponent = 2 * k + 1
            val numerator = x pow exponent
            val denominator = factorial(exponent)
            sum += sign * numerator / denominator
        }
        return sum
    }

    /**
     * Cosine approximation `cos(x)` via Taylor series.
     *
     * @param x Input value.
     * @param terms Number of non-zero terms.
     * @return Approximate cosine value.
     * @throws IllegalArgumentException if [terms] is not positive.
     */
    fun cos(x: RealNumber, terms: Int = 24): RealNumber {
        require(terms > 0) { "terms must be positive." }
        var sum = RealNumber.ZERO
        for (k in 0 until terms) {
            val sign = if (k % 2 == 0) RealNumber.ONE else RealNumber.of(-1)
            val exponent = 2 * k
            val numerator = x pow exponent
            val denominator = factorial(exponent)
            sum += sign * numerator / denominator
        }
        return sum
    }

    /**
     * Natural logarithm approximation `ln(x)` via Newton iteration on `exp(y)=x`.
     *
     * @param x Positive input.
     * @param iterations Newton iterations.
     * @return Approximate natural logarithm.
     * @throws IllegalArgumentException if `x <= 0`.
     */
    fun naturalLog(x: RealNumber, iterations: Int = 25): RealNumber {
        require(x > RealNumber.ZERO) { "naturalLog is defined only for x > 0." }
        var y = x - RealNumber.ONE
        repeat(iterations) {
            val e = exp(y)
            y += (x - e) / e
        }
        return y
    }

    private fun factorial(n: Int): RealNumber {
        if (n <= 1) return RealNumber.ONE
        var acc = RealNumber.ONE
        for (k in 2..n) {
            acc *= RealNumber.of(k)
        }
        return acc
    }
}

/**
 * Numerical checker for the Fundamental Theorem of Calculus.
 */
object FundamentalTheoremOfCalculus {
    /**
     * Verifies `integral_a^b F'(x) dx ~= F(b) - F(a)` numerically.
     *
     * @param antiderivative Candidate antiderivative `F`.
     * @param derivative Candidate derivative `F'`.
     * @param a Lower bound.
     * @param b Upper bound.
     * @param tolerance Absolute tolerance.
     * @param partitions Integral partitions.
     * @return True when the numeric identity holds within tolerance.
     */
    fun verify(
        antiderivative: (RealNumber) -> RealNumber,
        derivative: (RealNumber) -> RealNumber,
        a: RealNumber,
        b: RealNumber,
        tolerance: RealNumber = RealNumber.parse("0.001"),
        partitions: Int = 12_000
    ): Boolean {
        val left = RiemannIntegral.integrate(derivative, a, b, partitions)
        val right = antiderivative(b) - antiderivative(a)
        return (left - right).abs() <= tolerance
    }
}
