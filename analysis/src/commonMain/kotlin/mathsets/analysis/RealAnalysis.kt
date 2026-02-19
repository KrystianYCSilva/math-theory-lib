package mathsets.analysis

import mathsets.kernel.RealNumber

/**
 * Result of a limit computation.
 *
 * @param T Limit value type.
 */
sealed interface Limit<out T> {
    /**
     * Sequence/function converges to [value].
     *
     * @param T Value type.
     * @property value Limit value.
     */
    data class Converges<T>(val value: T) : Limit<T>

    /**
     * Sequence/function diverges under current criteria.
     */
    data object Diverges : Limit<Nothing>

    /**
     * Inconclusive under current criteria.
     */
    data object Unknown : Limit<Nothing>
}

/**
 * Real-valued sequence a_n over positive indices.
 *
 * @property generator Index-to-value generator with n >= 1.
 */
class RealSequence(private val generator: (Int) -> RealNumber) {
    /**
     * Returns term a_n.
     *
     * @param n Positive index.
     * @return Sequence value at n.
     */
    fun term(n: Int): RealNumber {
        require(n >= 1) { "Sequence index must be >= 1." }
        return generator(n)
    }

    /**
     * Computes partial sum S_n = a_1 + ... + a_n.
     *
     * @param n Positive index.
     * @return Partial sum.
     */
    fun partialSum(n: Int): RealNumber {
        require(n >= 1) { "Partial sum index must be >= 1." }
        var sum = RealNumber.ZERO
        for (i in 1..n) {
            sum += term(i)
        }
        return sum
    }

    /**
     * Attempts to numerically classify sequence convergence.
     *
     * The method inspects the last [window] terms up to [maxN], checking
     * pairwise closeness under [tolerance].
     *
     * @param tolerance Pairwise tolerance.
     * @param maxN Maximum sampled index.
     * @param window Number of tail terms to inspect.
     * @return Convergence classification.
     */
    fun convergence(
        tolerance: RealNumber,
        maxN: Int = 10_000,
        window: Int = 40
    ): Limit<RealNumber> {
        require(maxN >= window && window >= 2) {
            "Require maxN >= window >= 2."
        }

        val tail = (maxN - window + 1..maxN).map { term(it) }
        val pairwiseClose = tail.all { a -> tail.all { b -> (a - b).abs() <= tolerance } }
        if (!pairwiseClose) return Limit.Diverges

        val average = tail.fold(RealNumber.ZERO) { acc, v -> acc + v } / RealNumber.of(tail.size)
        return Limit.Converges(average)
    }
}

/**
 * Numerical limit helpers.
 */
object Limits {
    /**
     * Estimates lim_{x->point} f(x) using symmetric approach values.
     *
     * @param f Real function.
     * @param point Approach point.
     * @param scales Number of shrinking scales.
     * @param tolerance Stability tolerance between left and right values.
     * @return Limit classification.
     */
    fun atPoint(
        f: (RealNumber) -> RealNumber,
        point: RealNumber,
        scales: Int = 20,
        tolerance: RealNumber = RealNumber.parse("0.000001")
    ): Limit<RealNumber> {
        require(scales >= 2) { "scales must be >= 2." }

        var h = RealNumber.ONE
        var previous: RealNumber? = null
        repeat(scales) {
            h /= RealNumber.of(2)
            val left = f(point - h)
            val right = f(point + h)
            if ((left - right).abs() > tolerance) return Limit.Diverges

            val current = (left + right) / RealNumber.of(2)
            if (previous != null && (current - previous!!).abs() > tolerance * RealNumber.of(8)) {
                previous = current
            } else {
                previous = current
            }
        }

        return previous?.let { Limit.Converges(it) } ?: Limit.Unknown
    }
}

/**
 * Continuity checks via finite epsilon-delta sampling.
 */
object Continuity {
    /**
     * Checks continuity of f at x via sampled epsilon-delta conditions.
     *
     * @param f Function to test.
     * @param x Test point.
     * @param epsilons Sample epsilon values.
     * @return True when each sampled epsilon admits a tested delta.
     */
    fun isContinuousAt(
        f: (RealNumber) -> RealNumber,
        x: RealNumber,
        epsilons: List<RealNumber> = listOf(
            RealNumber.parse("0.1"),
            RealNumber.parse("0.01"),
            RealNumber.parse("0.001")
        )
    ): Boolean {
        val fX = f(x)
        val deltas = listOf(
            RealNumber.parse("0.1"),
            RealNumber.parse("0.05"),
            RealNumber.parse("0.01"),
            RealNumber.parse("0.005"),
            RealNumber.parse("0.001")
        )

        return epsilons.all { epsilon ->
            deltas.any { delta ->
                val samples = listOf(
                    x - delta,
                    x + delta,
                    x - delta / RealNumber.of(2),
                    x + delta / RealNumber.of(2)
                )
                samples.all { s -> (f(s) - fX).abs() < epsilon }
            }
        }
    }
}

/**
 * Numerical differentiation helpers.
 */
object Differentiation {
    /**
     * Symmetric finite-difference derivative estimate at point x.
     *
     * @param f Differentiable candidate function.
     * @param x Evaluation point.
     * @param h Small step.
     * @return Numerical derivative estimate.
     */
    fun derivativeAt(
        f: (RealNumber) -> RealNumber,
        x: RealNumber,
        h: RealNumber = RealNumber.parse("0.00001")
    ): RealNumber = (f(x + h) - f(x - h)) / (RealNumber.of(2) * h)
}

/**
 * Numerical Riemann integration helpers.
 */
object RiemannIntegral {
    /**
     * Midpoint-rule Riemann integral approximation.
     *
     * @param f Integrand.
     * @param a Lower bound.
     * @param b Upper bound.
     * @param partitions Number of partitions.
     * @return Integral approximation.
     */
    fun integrate(
        f: (RealNumber) -> RealNumber,
        a: RealNumber,
        b: RealNumber,
        partitions: Int = 10_000
    ): RealNumber {
        require(partitions > 0) { "partitions must be > 0." }
        val width = (b - a) / RealNumber.of(partitions)
        var sum = RealNumber.ZERO
        for (i in 0 until partitions) {
            val midpoint = a + width * (RealNumber.of(i) + RealNumber.parse("0.5"))
            sum += f(midpoint)
        }
        return sum * width
    }
}
