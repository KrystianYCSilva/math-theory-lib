package mathsets.linalg

import mathsets.algebra.Field
import mathsets.algebra.Ring

/**
 * Immutable dense matrix with row-major storage.
 *
 * @param K Scalar type.
 */
data class Matrix<K>(
    val rows: Int,
    val cols: Int,
    private val values: List<K>
) {
    init {
        require(rows > 0) { "rows must be positive." }
        require(cols > 0) { "cols must be positive." }
        require(values.size == rows * cols) { "values size must be rows * cols." }
    }

    /**
     * Returns matrix entry at [row], [col].
     */
    operator fun get(row: Int, col: Int): K {
        require(row in 0 until rows) { "row out of range: $row" }
        require(col in 0 until cols) { "col out of range: $col" }
        return values[row * cols + col]
    }

    /**
     * Returns the given row as a list.
     */
    fun row(index: Int): List<K> {
        require(index in 0 until rows) { "row out of range: $index" }
        val start = index * cols
        return values.subList(start, start + cols)
    }

    /**
     * Returns the given column as a list.
     */
    fun column(index: Int): List<K> {
        require(index in 0 until cols) { "col out of range: $index" }
        return (0 until rows).map { this[it, index] }
    }

    /**
     * Returns row-wise representation.
     */
    fun toRows(): List<List<K>> = (0 until rows).map(::row)

    /**
     * Maps matrix entries preserving dimensions.
     */
    fun mapIndexed(transform: (row: Int, col: Int, value: K) -> K): Matrix<K> =
        fill(rows, cols) { r, c -> transform(r, c, this[r, c]) }

    companion object {
        /**
         * Builds a matrix from row lists.
         */
        fun <K> ofRows(rows: List<List<K>>): Matrix<K> {
            require(rows.isNotEmpty()) { "rows cannot be empty." }
            val cols = rows.first().size
            require(cols > 0) { "columns cannot be zero." }
            require(rows.all { it.size == cols }) { "all rows must have the same length." }
            return Matrix(rows.size, cols, rows.flatten())
        }

        /**
         * Builds a matrix with generated entries.
         */
        fun <K> fill(rows: Int, cols: Int, init: (row: Int, col: Int) -> K): Matrix<K> {
            require(rows > 0) { "rows must be positive." }
            require(cols > 0) { "cols must be positive." }
            val values = MutableList(rows * cols) { idx ->
                val r = idx / cols
                val c = idx % cols
                init(r, c)
            }
            return Matrix(rows, cols, values)
        }
    }
}

/**
 * Sparse matrix representation as coordinate map.
 */
data class SparseMatrix<K>(
    val rows: Int,
    val cols: Int,
    val entries: Map<Pair<Int, Int>, K>
) {
    init {
        require(rows > 0) { "rows must be positive." }
        require(cols > 0) { "cols must be positive." }
        require(entries.keys.all { (r, c) -> r in 0 until rows && c in 0 until cols }) {
            "sparse entry coordinates out of bounds."
        }
    }

    /**
     * Converts sparse representation to dense matrix.
     */
    fun toDense(zero: K): Matrix<K> =
        Matrix.fill(rows, cols) { r, c -> entries[r to c] ?: zero }

    companion object {
        /**
         * Builds sparse representation from dense matrix.
         */
        fun <K> fromDense(matrix: Matrix<K>, zero: K): SparseMatrix<K> {
            val entries = mutableMapOf<Pair<Int, Int>, K>()
            for (r in 0 until matrix.rows) {
                for (c in 0 until matrix.cols) {
                    val value = matrix[r, c]
                    if (value != zero) entries[r to c] = value
                }
            }
            return SparseMatrix(matrix.rows, matrix.cols, entries)
        }
    }
}

/**
 * Reduced row-echelon form result.
 */
data class RowReducedEchelonForm<K>(
    val matrix: Matrix<K>,
    val pivotColumns: List<Int>
)

/**
 * Classification of linear-system outcomes.
 */
sealed interface LinearSystemSolution<out K> {
    data class Unique<K>(val values: List<K>) : LinearSystemSolution<K>
    data class Infinite(val rank: Int, val freeVariables: Int) : LinearSystemSolution<Nothing>
    data object Inconsistent : LinearSystemSolution<Nothing>
}

/**
 * Field-driven matrix operations.
 */
class MatrixLinearAlgebra<K>(private val field: Field<K>) {
    /**
     * Returns a zero matrix.
     */
    fun zero(rows: Int, cols: Int): Matrix<K> = Matrix.fill(rows, cols) { _, _ -> field.zero }

    /**
     * Returns the identity matrix of size n.
     */
    fun identity(n: Int): Matrix<K> =
        Matrix.fill(n, n) { r, c -> if (r == c) field.one else field.zero }

    /**
     * Matrix addition.
     */
    fun add(a: Matrix<K>, b: Matrix<K>): Matrix<K> {
        require(a.rows == b.rows && a.cols == b.cols) { "dimension mismatch for addition." }
        return Matrix.fill(a.rows, a.cols) { r, c -> field.add(a[r, c], b[r, c]) }
    }

    /**
     * Matrix subtraction.
     */
    fun subtract(a: Matrix<K>, b: Matrix<K>): Matrix<K> {
        require(a.rows == b.rows && a.cols == b.cols) { "dimension mismatch for subtraction." }
        return Matrix.fill(a.rows, a.cols) { r, c -> field.subtract(a[r, c], b[r, c]) }
    }

    /**
     * Scalar multiplication.
     */
    fun scalarMultiply(scalar: K, matrix: Matrix<K>): Matrix<K> =
        Matrix.fill(matrix.rows, matrix.cols) { r, c -> field.multiply(scalar, matrix[r, c]) }

    /**
     * Matrix multiplication.
     */
    fun multiply(a: Matrix<K>, b: Matrix<K>): Matrix<K> {
        require(a.cols == b.rows) { "dimension mismatch for multiplication." }
        return Matrix.fill(a.rows, b.cols) { r, c ->
            var acc = field.zero
            for (k in 0 until a.cols) {
                acc = field.add(acc, field.multiply(a[r, k], b[k, c]))
            }
            acc
        }
    }

    /**
     * Matrix transpose.
     */
    fun transpose(matrix: Matrix<K>): Matrix<K> =
        Matrix.fill(matrix.cols, matrix.rows) { r, c -> matrix[c, r] }

    /**
     * Matrix power with non-negative exponent.
     */
    fun power(matrix: Matrix<K>, exponent: Int): Matrix<K> {
        require(matrix.rows == matrix.cols) { "power requires a square matrix." }
        require(exponent >= 0) { "exponent must be non-negative." }
        var result = identity(matrix.rows)
        var base = matrix
        var e = exponent
        while (e > 0) {
            if (e % 2 == 1) result = multiply(result, base)
            base = multiply(base, base)
            e /= 2
        }
        return result
    }

    /**
     * Determinant via elimination.
     */
    fun determinant(matrix: Matrix<K>): K {
        require(matrix.rows == matrix.cols) { "determinant requires a square matrix." }
        val n = matrix.rows
        val data = matrix.toRows().map { it.toMutableList() }.toMutableList()

        var det = field.one
        var signFlips = 0

        for (col in 0 until n) {
            val pivotRow = (col until n).firstOrNull { data[it][col] != field.zero } ?: return field.zero

            if (pivotRow != col) {
                val tmp = data[col]
                data[col] = data[pivotRow]
                data[pivotRow] = tmp
                signFlips += 1
            }

            val pivot = data[col][col]
            det = field.multiply(det, pivot)

            for (row in col + 1 until n) {
                val below = data[row][col]
                if (below == field.zero) continue
                val factor = field.divide(below, pivot)
                for (j in col until n) {
                    data[row][j] = field.subtract(data[row][j], field.multiply(factor, data[col][j]))
                }
            }
        }

        return if (signFlips % 2 == 0) det else field.negate(det)
    }

    /**
     * Determinant via Leibniz formula (didactic; exponential complexity).
     */
    fun determinantLeibniz(matrix: Matrix<K>): K {
        require(matrix.rows == matrix.cols) { "determinant requires a square matrix." }
        val n = matrix.rows
        if (n == 1) return matrix[0, 0]

        var total = field.zero
        val current = IntArray(n) { it }

        fun inversionParity(permutation: IntArray): Boolean {
            var inversions = 0
            for (i in permutation.indices) {
                for (j in i + 1 until permutation.size) {
                    if (permutation[i] > permutation[j]) inversions += 1
                }
            }
            return inversions % 2 == 0
        }

        fun evaluateCurrent() {
            var product = field.one
            for (row in 0 until n) {
                product = field.multiply(product, matrix[row, current[row]])
            }
            total = if (inversionParity(current)) {
                field.add(total, product)
            } else {
                field.subtract(total, product)
            }
        }

        fun permute(index: Int) {
            if (index == n) {
                evaluateCurrent()
                return
            }
            for (i in index until n) {
                val tmp = current[index]
                current[index] = current[i]
                current[i] = tmp

                permute(index + 1)

                val back = current[index]
                current[index] = current[i]
                current[i] = back
            }
        }

        permute(0)
        return total
    }

    /**
     * Reduced row-echelon form with pivot-column tracking.
     */
    fun rref(matrix: Matrix<K>): RowReducedEchelonForm<K> {
        val data = matrix.toRows().map { it.toMutableList() }.toMutableList()
        val pivotColumns = mutableListOf<Int>()
        var pivotRow = 0

        for (col in 0 until matrix.cols) {
            if (pivotRow >= matrix.rows) break

            val found = (pivotRow until matrix.rows).firstOrNull { data[it][col] != field.zero } ?: continue

            if (found != pivotRow) {
                val tmp = data[pivotRow]
                data[pivotRow] = data[found]
                data[found] = tmp
            }

            val pivot = data[pivotRow][col]
            val pivotInverse = field.reciprocal(pivot)

            for (j in col until matrix.cols) {
                data[pivotRow][j] = field.multiply(data[pivotRow][j], pivotInverse)
            }

            for (row in 0 until matrix.rows) {
                if (row == pivotRow) continue
                val factor = data[row][col]
                if (factor == field.zero) continue
                for (j in col until matrix.cols) {
                    data[row][j] = field.subtract(data[row][j], field.multiply(factor, data[pivotRow][j]))
                }
            }

            pivotColumns += col
            pivotRow += 1
        }

        return RowReducedEchelonForm(
            matrix = Matrix.ofRows(data.map { it.toList() }),
            pivotColumns = pivotColumns
        )
    }

    /**
     * Inverse matrix, or null when singular.
     */
    fun inverse(matrix: Matrix<K>): Matrix<K>? {
        require(matrix.rows == matrix.cols) { "inverse requires a square matrix." }
        val n = matrix.rows

        val augmented = Matrix.fill(n, 2 * n) { r, c ->
            when {
                c < n -> matrix[r, c]
                c - n == r -> field.one
                else -> field.zero
            }
        }

        val reduced = rref(augmented).matrix

        for (r in 0 until n) {
            for (c in 0 until n) {
                val expected = if (r == c) field.one else field.zero
                if (reduced[r, c] != expected) return null
            }
        }

        return Matrix.fill(n, n) { r, c -> reduced[r, c + n] }
    }

    /**
     * Solves A x = b via row reduction.
     */
    fun solve(a: Matrix<K>, b: List<K>): LinearSystemSolution<K> {
        require(a.rows == b.size) { "right-hand side size must match matrix rows." }

        val augmented = Matrix.fill(a.rows, a.cols + 1) { r, c ->
            if (c < a.cols) a[r, c] else b[r]
        }

        val reducedForm = rref(augmented)
        val reduced = reducedForm.matrix

        for (r in 0 until a.rows) {
            val allZero = (0 until a.cols).all { c -> reduced[r, c] == field.zero }
            if (allZero && reduced[r, a.cols] != field.zero) {
                return LinearSystemSolution.Inconsistent
            }
        }

        val rank = (0 until a.rows).count { r ->
            (0 until a.cols).any { c -> reduced[r, c] != field.zero }
        }

        if (rank < a.cols) {
            return LinearSystemSolution.Infinite(rank = rank, freeVariables = a.cols - rank)
        }

        val solution = MutableList(a.cols) { field.zero }
        for (pivotRow in reducedForm.pivotColumns.indices) {
            val pivotCol = reducedForm.pivotColumns[pivotRow]
            if (pivotCol < a.cols) {
                solution[pivotCol] = reduced[pivotRow, a.cols]
            }
        }
        return LinearSystemSolution.Unique(solution)
    }
}

/**
 * Ring of n x n matrices over a field.
 */
class MatrixRing<K>(
    private val field: Field<K>,
    private val dimension: Int
) : Ring<Matrix<K>> {
    init {
        require(dimension > 0) { "dimension must be positive." }
    }

    private val linear = MatrixLinearAlgebra(field)
    private val minusOne = field.negate(field.one)

    override val zero: Matrix<K> = linear.zero(dimension, dimension)
    override val one: Matrix<K> = linear.identity(dimension)

    override fun add(a: Matrix<K>, b: Matrix<K>): Matrix<K> = linear.add(a, b)

    override fun negate(a: Matrix<K>): Matrix<K> = linear.scalarMultiply(minusOne, a)

    override fun multiply(a: Matrix<K>, b: Matrix<K>): Matrix<K> = linear.multiply(a, b)
}
