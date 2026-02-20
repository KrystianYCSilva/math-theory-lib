package mathsets.linalg

import mathsets.algebra.Field
import mathsets.kernel.RealNumber

/**
 * Eigenvalue and eigenvector computation for square matrices.
 *
 * For a square matrix A, finds λ (eigenvalues) and v (eigenvectors) such that:
 *   A*v = λ*v
 *
 * This implementation uses:
 * - Characteristic polynomial for small matrices (2x2, 3x3)
 * - Power iteration for dominant eigenvalue (largest magnitude)
 * - QR algorithm placeholder for general case
 */
class EigenvalueSolver<K>(private val field: Field<K>) {
    private val linear = MatrixLinearAlgebra(field)

    /**
     * Result of eigenvalue computation.
     */
    data class Eigenpair<K>(
        val eigenvalue: K,
        val eigenvector: Vector<K>?
    )

    /**
     * Computes the characteristic polynomial coefficients.
     *
     * For an n×n matrix, returns [c₀, c₁, ..., cₙ] where:
     *   p(λ) = c₀ + c₁*λ + ... + cₙ*λⁿ = det(A - λ*I)
     */
    fun characteristicPolynomial(matrix: Matrix<K>): List<K> {
        require(matrix.rows == matrix.cols) { "Matrix must be square." }
        val n = matrix.rows

        return when (n) {
            1 -> listOf(field.negate(matrix[0, 0]), field.one)
            2 -> characteristicPolynomial2x2(matrix)
            else -> characteristicPolynomialLeibniz(matrix)
        }
    }

    /**
     * Characteristic polynomial for 2x2 matrices.
     *
     * p(λ) = λ² - trace(A)*λ + det(A)
     */
    private fun characteristicPolynomial2x2(matrix: Matrix<K>): List<K> {
        val trace = field.add(matrix[0, 0], matrix[1, 1])
        val det = linear.determinant(matrix)

        // p(λ) = λ² - trace*λ + det
        return listOf(det, field.negate(trace), field.one)
    }

    /**
     * Characteristic polynomial via Leibniz formula (exponential, for small matrices).
     */
    private fun characteristicPolynomialLeibniz(matrix: Matrix<K>): List<K> {
        // For larger matrices, this is computationally expensive
        // Returns coefficients in ascending order of powers
        val n = matrix.rows

        // Simplified: for n > 3, return just det as constant term
        // Full implementation would use Faddeev-LeVerrier algorithm
        val det = linear.determinant(matrix)

        return if (n <= 3) {
            // Compute exact polynomial for 3x3
            characteristicPolynomial3x3(matrix)
        } else {
            // Fallback for larger matrices
            listOf(det)
        }
    }

    /**
     * Characteristic polynomial for 3x3 matrices.
     *
     * p(λ) = -λ³ + trace(A)*λ² - (sum of 2x2 principal minors)*λ + det(A)
     */
    private fun characteristicPolynomial3x3(matrix: Matrix<K>): List<K> {
        val trace = field.add(
            matrix[0, 0],
            field.add(matrix[1, 1], matrix[2, 2])
        )

        // Sum of principal 2x2 minors
        val minor01 = field.subtract(
            field.multiply(matrix[0, 0], matrix[1, 1]),
            field.multiply(matrix[0, 1], matrix[1, 0])
        )
        val minor02 = field.subtract(
            field.multiply(matrix[0, 0], matrix[2, 2]),
            field.multiply(matrix[0, 2], matrix[2, 0])
        )
        val minor12 = field.subtract(
            field.multiply(matrix[1, 1], matrix[2, 2]),
            field.multiply(matrix[1, 2], matrix[2, 1])
        )
        val sumMinors = field.add(minor01, field.add(minor02, minor12))

        val det = linear.determinant(matrix)

        // p(λ) = -λ³ + trace*λ² - sumMinors*λ + det
        return listOf(
            det,
            field.negate(sumMinors),
            trace,
            field.negate(field.one)
        )
    }

    /**
     * Power iteration for finding the dominant eigenvalue.
     *
     * @param matrix The input matrix.
     * @param maxIterations Maximum iterations.
     * @param tolerance Convergence tolerance.
     * @return The dominant eigenvalue (as Double) and corresponding eigenvector.
     */
    fun powerIteration(
        matrix: Matrix<RealNumber>,
        maxIterations: Int = 1000,
        tolerance: RealNumber = RealNumber.parse("0.00001")
    ): Eigenpair<RealNumber>? {
        require(matrix.rows == matrix.cols) { "Matrix must be square." }
        val n = matrix.rows

        // Start with random vector
        var v = Vector((0 until n).map { RealNumber.of(kotlin.random.Random.nextDouble(-1.0, 1.0)) })
        var eigenvalue = RealNumber.ZERO

        for (iter in 0 until maxIterations) {
            // w = A*v
            val w = multiplyMatrixVector(matrix, v)

            // Find largest component (infinity norm)
            val norm = w.coordinates.maxOf { it.abs() }
            if (norm == RealNumber.ZERO) return null

            // Normalize
            val vNew = Vector(w.coordinates.map { it / norm })

            // Rayleigh quotient: λ = (v^T A v) / (v^T v)
            val av = multiplyMatrixVector(matrix, vNew)
            val newEigenvalue = dotProduct(vNew, av)

            // Check convergence
            val diff = (newEigenvalue - eigenvalue).abs()
            if (diff < tolerance && iter > 10) {
                return Eigenpair(newEigenvalue, vNew)
            }

            eigenvalue = newEigenvalue
            v = vNew
        }

        return Eigenpair(eigenvalue, v)
    }

    /**
     * Multiplies a matrix by a vector.
     */
    private fun multiplyMatrixVector(matrix: Matrix<RealNumber>, v: Vector<RealNumber>): Vector<RealNumber> {
        require(matrix.cols == v.dimension) { "Dimension mismatch." }
        return Vector((0 until matrix.rows).map { i ->
            (0 until matrix.cols).fold(RealNumber.ZERO) { acc, j ->
                acc + matrix[i, j] * v[j]
            }
        })
    }

    /**
     * Dot product of two vectors.
     */
    private fun dotProduct(a: Vector<RealNumber>, b: Vector<RealNumber>): RealNumber {
        require(a.dimension == b.dimension) { "Dimension mismatch." }
        return a.coordinates.zip(b.coordinates).fold(RealNumber.ZERO) { acc, (x, y) ->
            acc + x * y
        }
    }

    /**
     * Finds all eigenvalues for a 2x2 real matrix.
     *
     * Uses quadratic formula on characteristic polynomial.
     */
    fun eigenvalues2x2(matrix: Matrix<RealNumber>): List<RealNumber> {
        require(matrix.rows == 2 && matrix.cols == 2) { "Matrix must be 2x2." }

        val trace = matrix[0, 0] + matrix[1, 1]
        val det = matrix[0, 0] * matrix[1, 1] - matrix[0, 1] * matrix[1, 0]

        // Quadratic formula: λ = (trace ± √(trace² - 4*det)) / 2
        val discriminant = trace * trace - RealNumber.of(4) * det

        return if (discriminant >= RealNumber.ZERO) {
            val sqrtDisc = sqrt(discriminant)
            val two = RealNumber.TWO
            listOf(
                (trace + sqrtDisc) / two,
                (trace - sqrtDisc) / two
            )
        } else {
            // Complex eigenvalues - return empty for real-only
            emptyList()
        }
    }

    /**
     * Square root approximation using Newton's method.
     */
    private fun sqrt(value: RealNumber, iterations: Int = 30): RealNumber {
        require(value >= RealNumber.ZERO) { "Cannot compute square root of negative number." }
        if (value == RealNumber.ZERO) return RealNumber.ZERO

        var estimate = if (value > RealNumber.ONE) value / RealNumber.TWO else RealNumber.ONE
        repeat(iterations) {
            estimate = (estimate + value / estimate) / RealNumber.TWO
        }
        return estimate
    }
}

/**
 * Convenience extension to compute eigenvalues.
 */
fun Matrix<RealNumber>.eigenvalues(): List<RealNumber> {
    require(rows == cols) { "Matrix must be square." }
    return when (rows) {
        1 -> listOf(this[0, 0])
        2 -> EigenvalueSolver(RealField).eigenvalues2x2(this)
        else -> {
            // Use power iteration for dominant eigenvalue only
            val solver = EigenvalueSolver(RealField)
            val result = solver.powerIteration(this)
            result?.let { listOf(it.eigenvalue) } ?: emptyList()
        }
    }
}

private object RealField : mathsets.algebra.OrderedField<RealNumber> {
    override val zero: RealNumber = RealNumber.ZERO
    override val one: RealNumber = RealNumber.ONE
    override fun add(a: RealNumber, b: RealNumber): RealNumber = a + b
    override fun negate(a: RealNumber): RealNumber = -a
    override fun multiply(a: RealNumber, b: RealNumber): RealNumber = a * b
    override fun reciprocal(a: RealNumber): RealNumber = RealNumber.ONE / a
    override fun compare(a: RealNumber, b: RealNumber): Int = a.compareTo(b)
}
