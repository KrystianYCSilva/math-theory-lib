package mathsets.analysis.metric

import mathsets.kernel.RealNumber
import kotlin.math.abs

/**
 * Metric space contract.
 *
 * @param T Point type.
 */
interface MetricSpace<T> {
    /**
     * Distance function d(a, b).
     */
    fun distance(a: T, b: T): RealNumber

    /**
     * Finite-sample verification of metric axioms.
     */
    fun satisfiesAxioms(
        samples: List<T>,
        tolerance: RealNumber = RealNumber.parse("0.000001")
    ): Boolean {
        if (samples.isEmpty()) return true

        val nonNegativeAndIdentity = samples.all { x ->
            val dxx = distance(x, x)
            dxx.abs() <= tolerance
        } && samples.all { x ->
            samples.all { y -> distance(x, y) >= RealNumber.ZERO }
        }
        if (!nonNegativeAndIdentity) return false

        val symmetry = samples.all { x ->
            samples.all { y ->
                (distance(x, y) - distance(y, x)).abs() <= tolerance
            }
        }
        if (!symmetry) return false

        return samples.all { x ->
            samples.all { y ->
                samples.all { z ->
                    distance(x, z) <= distance(x, y) + distance(y, z) + tolerance
                }
            }
        }
    }
}

/**
 * Open metric ball centered at [center] with radius [radius].
 */
data class OpenBall<T>(
    val space: MetricSpace<T>,
    val center: T,
    val radius: RealNumber
) {
    init {
        require(radius > RealNumber.ZERO) { "radius must be positive." }
    }

    fun contains(point: T): Boolean = space.distance(center, point) < radius

    fun from(points: Iterable<T>): Set<T> = points.filterTo(mutableSetOf()) { contains(it) }
}

/**
 * Closed metric ball centered at [center] with radius [radius].
 */
data class ClosedBall<T>(
    val space: MetricSpace<T>,
    val center: T,
    val radius: RealNumber
) {
    init {
        require(radius >= RealNumber.ZERO) { "radius must be non-negative." }
    }

    fun contains(point: T): Boolean = space.distance(center, point) <= radius

    fun from(points: Iterable<T>): Set<T> = points.filterTo(mutableSetOf()) { contains(it) }
}

/**
 * Normed-space contract.
 *
 * @param K Scalar type.
 * @param V Vector type.
 */
interface NormedSpace<K, V> {
    fun add(a: V, b: V): V

    fun scalarMultiply(scalar: K, vector: V): V

    fun norm(vector: V): RealNumber
}

/**
 * Inner-product-space contract.
 */
interface InnerProductSpace<K, V> : NormedSpace<K, V> {
    fun innerProduct(a: V, b: V): K

    fun scalarAbs(value: K): RealNumber

    fun satisfiesCauchySchwarz(
        a: V,
        b: V,
        tolerance: RealNumber = RealNumber.parse("0.000001")
    ): Boolean {
        val left = scalarAbs(innerProduct(a, b))
        val right = norm(a) * norm(b)
        return left <= right + tolerance
    }
}

/**
 * Banach-space marker with sample-based completeness check.
 */
interface BanachSpace<K, V> : NormedSpace<K, V>, MetricSpace<V> {
    fun isSampleComplete(
        sequences: List<(Int) -> V>,
        candidateLimits: List<V>,
        tolerance: RealNumber = RealNumber.parse("0.0001")
    ): Boolean {
        if (sequences.size != candidateLimits.size) return false
        return sequences.indices.all { i ->
            Completeness.hasCauchyConvergence(this, sequences[i], candidateLimits[i], tolerance)
        }
    }
}

/**
 * Hilbert-space marker.
 */
interface HilbertSpace<K, V> : BanachSpace<K, V>, InnerProductSpace<K, V>

/**
 * Finite-dimensional real vector.
 */
data class RealVector(val coordinates: List<RealNumber>) {
    init {
        require(coordinates.isNotEmpty()) { "RealVector must have at least one coordinate." }
    }

    operator fun plus(other: RealVector): RealVector {
        require(coordinates.size == other.coordinates.size) { "Dimension mismatch." }
        return RealVector(coordinates.zip(other.coordinates).map { (a, b) -> a + b })
    }

    operator fun minus(other: RealVector): RealVector {
        require(coordinates.size == other.coordinates.size) { "Dimension mismatch." }
        return RealVector(coordinates.zip(other.coordinates).map { (a, b) -> a - b })
    }

    fun scale(scalar: RealNumber): RealVector = RealVector(coordinates.map { scalar * it })

    fun dot(other: RealVector): RealNumber {
        require(coordinates.size == other.coordinates.size) { "Dimension mismatch." }
        return coordinates.zip(other.coordinates)
            .fold(RealNumber.ZERO) { acc, (a, b) -> acc + a * b }
    }
}

/**
 * Euclidean space R^n with standard norm/metric.
 */
class EuclideanSpace(private val dimension: Int) : HilbertSpace<RealNumber, RealVector> {
    init {
        require(dimension > 0) { "dimension must be positive." }
    }

    private fun checkDimension(vector: RealVector) {
        require(vector.coordinates.size == dimension) {
            "Vector dimension ${vector.coordinates.size} does not match space dimension $dimension."
        }
    }

    override fun add(a: RealVector, b: RealVector): RealVector {
        checkDimension(a)
        checkDimension(b)
        return a + b
    }

    override fun scalarMultiply(scalar: RealNumber, vector: RealVector): RealVector {
        checkDimension(vector)
        return vector.scale(scalar)
    }

    override fun norm(vector: RealVector): RealNumber {
        checkDimension(vector)
        val squared = vector.dot(vector)
        return realSqrt(squared)
    }

    override fun distance(a: RealVector, b: RealVector): RealNumber {
        checkDimension(a)
        checkDimension(b)
        return norm(a - b)
    }

    override fun innerProduct(a: RealVector, b: RealVector): RealNumber {
        checkDimension(a)
        checkDimension(b)
        return a.dot(b)
    }

    override fun scalarAbs(value: RealNumber): RealNumber = value.abs()
}

/**
 * Discrete metric on any set.
 */
class DiscreteMetricSpace<T> : MetricSpace<T> {
    override fun distance(a: T, b: T): RealNumber = if (a == b) RealNumber.ZERO else RealNumber.ONE
}

/**
 * Simplified p-adic metric on integers.
 *
 * d_p(a,b) = p^{-v_p(a-b)} for a != b and 0 otherwise.
 */
class PAdicMetricSpace(private val prime: Int) : MetricSpace<Long> {
    init {
        require(prime > 1) { "prime must be > 1." }
    }

    override fun distance(a: Long, b: Long): RealNumber {
        if (a == b) return RealNumber.ZERO
        val v = pAdicValuation(abs(a - b), prime)
        return RealNumber.ONE / (RealNumber.of(prime) pow v)
    }

    private fun pAdicValuation(value: Long, p: Int): Int {
        var n = value
        var count = 0
        while (n % p.toLong() == 0L && n > 0) {
            n /= p.toLong()
            count += 1
        }
        return count
    }
}

/**
 * Completeness helpers for sampled sequences.
 */
object Completeness {
    /**
     * Checks whether a sampled tail is Cauchy.
     */
    fun <T> isCauchy(
        space: MetricSpace<T>,
        sequence: (Int) -> T,
        tolerance: RealNumber,
        maxN: Int = 4_000,
        window: Int = 80
    ): Boolean {
        require(maxN >= window && window >= 2) { "Require maxN >= window >= 2." }
        val tail = (maxN - window + 1..maxN).map(sequence)
        return tail.all { a -> tail.all { b -> space.distance(a, b) <= tolerance } }
    }

    /**
     * Checks whether a Cauchy sequence converges to a sampled candidate limit.
     */
    fun <T> hasCauchyConvergence(
        space: MetricSpace<T>,
        sequence: (Int) -> T,
        candidateLimit: T,
        tolerance: RealNumber,
        maxN: Int = 4_000,
        window: Int = 80
    ): Boolean {
        if (!isCauchy(space, sequence, tolerance, maxN, window)) return false
        val tail = (maxN - window + 1..maxN).map(sequence)
        return tail.all { point -> space.distance(point, candidateLimit) <= tolerance }
    }
}

private fun realSqrt(
    value: RealNumber,
    iterations: Int = 24
): RealNumber {
    require(value >= RealNumber.ZERO) { "sqrt input must be non-negative." }
    if (value == RealNumber.ZERO) return RealNumber.ZERO

    var estimate = if (value > RealNumber.ONE) value / RealNumber.TWO else RealNumber.ONE
    repeat(iterations) {
        estimate = (estimate + value / estimate) / RealNumber.TWO
    }
    return estimate
}
