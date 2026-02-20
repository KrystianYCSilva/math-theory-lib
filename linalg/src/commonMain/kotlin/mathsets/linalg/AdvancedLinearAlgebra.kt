package mathsets.linalg

import mathsets.algebra.Field
import mathsets.kernel.RealNumber

/**
 * LU decomposition with row-permutation matrix.
 */
data class LUDecomposition<K>(
    val lower: Matrix<K>,
    val upper: Matrix<K>,
    val permutation: Matrix<K>,
    val sign: Int
)

/**
 * Characteristic polynomial for a 2x2 matrix: lambda^2 + b*lambda + c.
 */
data class CharacteristicPolynomial2x2<K>(
    val lambdaSquared: K,
    val lambda: K,
    val constant: K
)

/**
 * Finite-dimensional vector over a scalar field.
 */
data class Vector<K>(val coordinates: List<K>) {
    init {
        require(coordinates.isNotEmpty()) { "vector must have at least one coordinate." }
    }

    val dimension: Int get() = coordinates.size

    operator fun get(index: Int): K = coordinates[index]
}

/**
 * Inner-product-based vector operations over a field.
 */
class InnerProductOps<K>(private val field: Field<K>) {
    /**
     * Vector addition.
     */
    fun add(a: Vector<K>, b: Vector<K>): Vector<K> {
        require(a.dimension == b.dimension) { "dimension mismatch for vector addition." }
        return Vector(a.coordinates.zip(b.coordinates).map { (x, y) -> field.add(x, y) })
    }

    /**
     * Vector subtraction.
     */
    fun subtract(a: Vector<K>, b: Vector<K>): Vector<K> {
        require(a.dimension == b.dimension) { "dimension mismatch for vector subtraction." }
        return Vector(a.coordinates.zip(b.coordinates).map { (x, y) -> field.subtract(x, y) })
    }

    /**
     * Scalar-vector product.
     */
    fun scale(scalar: K, vector: Vector<K>): Vector<K> =
        Vector(vector.coordinates.map { field.multiply(scalar, it) })

    /**
     * Canonical dot product sum_i a_i*b_i.
     */
    fun dot(a: Vector<K>, b: Vector<K>): K {
        require(a.dimension == b.dimension) { "dimension mismatch for dot product." }
        return a.coordinates.zip(b.coordinates).fold(field.zero) { acc, (x, y) ->
            field.add(acc, field.multiply(x, y))
        }
    }

    /**
     * Gram-Schmidt orthogonalization.
     *
     * Dependent vectors are skipped when [dropDependent] is true.
     */
    fun gramSchmidt(vectors: List<Vector<K>>, dropDependent: Boolean = true): List<Vector<K>> {
        if (vectors.isEmpty()) return emptyList()
        val dimension = vectors.first().dimension
        require(vectors.all { it.dimension == dimension }) { "all vectors must share the same dimension." }

        val orthogonal = mutableListOf<Vector<K>>()

        for (vector in vectors) {
            var current = vector
            for (basis in orthogonal) {
                val denominator = dot(basis, basis)
                if (denominator == field.zero) continue
                val coefficient = field.divide(dot(current, basis), denominator)
                current = subtract(current, scale(coefficient, basis))
            }

            val isZero = current.coordinates.all { it == field.zero }
            if (isZero) {
                if (!dropDependent) orthogonal += current
            } else {
                orthogonal += current
            }
        }

        return orthogonal
    }
}

/**
 * Extra linear-algebra operations for Wave 3 progression.
 */
class AdvancedMatrixAlgebra<K>(private val field: Field<K>) {
    private val linear = MatrixLinearAlgebra(field)

    /**
     * LU decomposition using partial pivoting by first non-zero pivot.
     *
     * Returns matrices P, L, U such that P*A = L*U.
     */
    fun luDecomposition(matrix: Matrix<K>): LUDecomposition<K> {
        require(matrix.rows == matrix.cols) { "LU decomposition requires a square matrix." }
        val n = matrix.rows

        val upper = matrix.toRows().map { it.toMutableList() }.toMutableList()
        val lower = MutableList(n) { r -> MutableList(n) { c -> if (r == c) field.one else field.zero } }
        val permutation = MutableList(n) { r -> MutableList(n) { c -> if (r == c) field.one else field.zero } }

        var sign = 1

        for (column in 0 until n) {
            val pivotRow = (column until n).firstOrNull { upper[it][column] != field.zero }
            if (pivotRow == null) continue

            if (pivotRow != column) {
                swapRows(upper, column, pivotRow)
                swapRows(permutation, column, pivotRow)

                for (j in 0 until column) {
                    val tmp = lower[column][j]
                    lower[column][j] = lower[pivotRow][j]
                    lower[pivotRow][j] = tmp
                }
                sign = -sign
            }

            val pivot = upper[column][column]
            if (pivot == field.zero) continue

            for (row in column + 1 until n) {
                val value = upper[row][column]
                if (value == field.zero) continue

                val factor = field.divide(value, pivot)
                lower[row][column] = factor

                for (j in column until n) {
                    upper[row][j] = field.subtract(
                        upper[row][j],
                        field.multiply(factor, upper[column][j])
                    )
                }
            }
        }

        return LUDecomposition(
            lower = Matrix.ofRows(lower.map { it.toList() }),
            upper = Matrix.ofRows(upper.map { it.toList() }),
            permutation = Matrix.ofRows(permutation.map { it.toList() }),
            sign = sign
        )
    }

    /**
     * Determinant via LU decomposition.
     */
    fun determinantByLu(matrix: Matrix<K>): K {
        val lu = luDecomposition(matrix)
        var determinant = field.one
        for (i in 0 until matrix.rows) {
            determinant = field.multiply(determinant, lu.upper[i, i])
        }
        return if (lu.sign >= 0) determinant else field.negate(determinant)
    }

    /**
     * Kronecker product of matrices.
     */
    fun kroneckerProduct(a: Matrix<K>, b: Matrix<K>): Matrix<K> {
        val rows = a.rows * b.rows
        val cols = a.cols * b.cols
        return Matrix.fill(rows, cols) { r, c ->
            val aRow = r / b.rows
            val bRow = r % b.rows
            val aCol = c / b.cols
            val bCol = c % b.cols
            field.multiply(a[aRow, aCol], b[bRow, bCol])
        }
    }

    /**
     * Characteristic polynomial of a 2x2 matrix.
     *
     * For A = [[a,b],[c,d]], returns lambda^2 - (a+d)lambda + (ad-bc).
     */
    fun characteristicPolynomial2x2(matrix: Matrix<K>): CharacteristicPolynomial2x2<K> {
        require(matrix.rows == 2 && matrix.cols == 2) { "characteristicPolynomial2x2 requires a 2x2 matrix." }

        val a = matrix[0, 0]
        val b = matrix[0, 1]
        val c = matrix[1, 0]
        val d = matrix[1, 1]

        val trace = field.add(a, d)
        val determinant = field.subtract(
            field.multiply(a, d),
            field.multiply(b, c)
        )

        return CharacteristicPolynomial2x2(
            lambdaSquared = field.one,
            lambda = field.negate(trace),
            constant = determinant
        )
    }

    /**
     * Convenience check for LU reconstruction: P*A == L*U.
     */
    fun verifiesLuDecomposition(matrix: Matrix<K>): Boolean {
        val lu = luDecomposition(matrix)
        val left = linear.multiply(lu.permutation, matrix)
        val right = linear.multiply(lu.lower, lu.upper)
        return left == right
    }

    private fun swapRows(data: MutableList<MutableList<K>>, i: Int, j: Int) {
        val tmp = data[i]
        data[i] = data[j]
        data[j] = tmp
    }
}

/**
 * Real 2x2 eigenvalue helpers.
 */
object RealEigenvalues {
    /**
     * Real eigenvalues for a 2x2 real matrix.
     *
     * Returns an empty list when eigenvalues are non-real.
     */
    fun eigenvalues2x2(matrix: Matrix<RealNumber>): List<RealNumber> {
        require(matrix.rows == 2 && matrix.cols == 2) { "eigenvalues2x2 requires a 2x2 matrix." }

        val a = matrix[0, 0]
        val b = matrix[0, 1]
        val c = matrix[1, 0]
        val d = matrix[1, 1]

        val trace = a + d
        val determinant = a * d - b * c
        val discriminant = trace * trace - RealNumber.of(4) * determinant

        if (discriminant < RealNumber.ZERO) return emptyList()

        val sqrtDiscriminant = sqrt(discriminant)
        val two = RealNumber.TWO
        val lambda1 = (trace + sqrtDiscriminant) / two
        val lambda2 = (trace - sqrtDiscriminant) / two
        return listOf(lambda1, lambda2)
    }

    private fun sqrt(value: RealNumber, iterations: Int = 30): RealNumber {
        require(value >= RealNumber.ZERO) { "sqrt argument must be non-negative." }
        if (value == RealNumber.ZERO) return RealNumber.ZERO

        var estimate = if (value > RealNumber.ONE) value / RealNumber.TWO else RealNumber.ONE
        repeat(iterations) {
            estimate = (estimate + value / estimate) / RealNumber.TWO
        }
        return estimate
    }
}
