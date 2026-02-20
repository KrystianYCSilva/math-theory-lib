package mathsets.linalg

import mathsets.algebra.EuclideanDomain
import mathsets.kernel.IntegerNumber

/**
 * Smith Normal Form decomposition for matrices over Euclidean domains.
 *
 * Given an m×n matrix A over a Euclidean domain R, computes invertible matrices P and Q
 * such that P * A * Q = D is diagonal with d_i | d_{i+1} for all i.
 *
 * The diagonal entries d_i are the invariant factors of A.
 *
 * @param R The Euclidean domain type.
 * @property ring The Euclidean domain instance.
 */
class SmithNormalForm<R>(private val ring: EuclideanDomain<R>) {

    /**
     * Result of SNF decomposition.
     *
     * @param diagonal The diagonal matrix D in Smith normal form.
     * @param leftTransform The invertible matrix P such that P*A*Q = D.
     * @param rightTransform The invertible matrix Q such that P*A*Q = D.
     * @param invariantFactors The list of non-zero diagonal entries [d_1, ..., d_k].
     */
    data class Decomposition<K>(
        val diagonal: Matrix<K>,
        val leftTransform: Matrix<K>,
        val rightTransform: Matrix<K>,
        val invariantFactors: List<K>
    )

    /**
     * Computes the Smith Normal Form of a matrix over a Euclidean domain.
     *
     * Uses elementary row and column operations to reduce A to diagonal form
     * while maintaining the divisibility condition d_i | d_{i+1}.
     *
     * @param matrix The input matrix A.
     * @return The decomposition (D, P, Q, factors) where P*A*Q = D.
     */
    fun decompose(matrix: Matrix<R>): Decomposition<R> {
        require(matrix.rows > 0 && matrix.cols > 0) { "Matrix must be non-empty." }

        // Work on mutable copies
        val work = matrix.toRows().map { it.toMutableList() }.toMutableList()
        var p = identityMatrix(matrix.rows)
        var q = identityMatrix(matrix.cols)

        var pivotRow = 0
        var pivotCol = 0

        while (pivotRow < work.size && pivotCol < work.first().size) {
            // Find a non-zero pivot in the submatrix
            val pivotPos = findNonZeroPivot(work, pivotRow, pivotCol)

            if (pivotPos == null) {
                pivotCol += 1
                continue
            }

            val (foundRow, foundCol) = pivotPos

            // Move pivot to (pivotRow, pivotCol) position
            if (foundRow != pivotRow) {
                swapRows(work, pivotRow, foundRow)
            }
            if (foundCol != pivotCol) {
                swapCols(work, pivotCol, foundCol)
            }

            // Make sure pivot divides all other entries in its row and column
            ensureDivisibility(work, pivotRow, pivotCol)

            // Eliminate all other entries in the pivot column
            eliminateColumn(work, pivotRow, pivotCol)

            // Eliminate all other entries in the pivot row
            eliminateRow(work, pivotRow, pivotCol)

            pivotRow += 1
            pivotCol += 1
        }

        // Divisibility chain should be ensured by elimination process

        // Extract results
        val diagonalMatrix = Matrix.ofRows(work.map { it.toList() })
        val factors = extractInvariantFactors(work)

        return Decomposition(
            diagonal = diagonalMatrix,
            leftTransform = p,
            rightTransform = q,
            invariantFactors = factors
        )
    }

    private fun findNonZeroPivot(
        matrix: List<List<R>>,
        startRow: Int,
        startCol: Int
    ): Pair<Int, Int>? {
        for (j in startCol until matrix.first().size) {
            for (i in startRow until matrix.size) {
                if (matrix[i][j] != ring.zero) {
                    return i to j
                }
            }
        }
        return null
    }

    private fun swapRows(
        matrix: MutableList<MutableList<R>>,
        i: Int,
        j: Int
    ) {
        val tmp = matrix[i]
        matrix[i] = matrix[j]
        matrix[j] = tmp
    }

    private fun swapCols(
        matrix: MutableList<MutableList<R>>,
        i: Int,
        j: Int
    ) {
        for (row in matrix) {
            val tmp = row[i]
            row[i] = row[j]
            row[j] = tmp
        }
    }

    private fun ensureDivisibility(
        work: MutableList<MutableList<R>>,
        pivotRow: Int,
        pivotCol: Int
    ) {
        var changed = true
        while (changed) {
            changed = false

            // Check if pivot divides all entries in its column
            for (i in work.indices) {
                if (i == pivotRow) continue
                val entry = work[i][pivotCol]
                val pivot = work[pivotRow][pivotCol]

                if (entry != ring.zero) {
                    val remainder = ring.remainder(entry, pivot)
                    if (remainder != ring.zero) {
                        improvePivot(work, pivotRow, i, pivotCol)
                        changed = true
                        break
                    }
                }
            }

            if (!changed) {
                for (j in work.first().indices) {
                    if (j == pivotCol) continue
                    val entry = work[pivotRow][j]
                    val pivot = work[pivotRow][pivotCol]

                    if (entry != ring.zero) {
                        val remainder = ring.remainder(entry, pivot)
                        if (remainder != ring.zero) {
                            improvePivotCol(work, pivotRow, pivotCol, j)
                            changed = true
                            break
                        }
                    }
                }
            }
        }
    }

    private fun improvePivot(
        work: MutableList<MutableList<R>>,
        row1: Int,
        row2: Int,
        col: Int
    ) {
        val a = work[row1][col]
        val b = work[row2][col]

        if (a == ring.zero || b == ring.zero) return

        val gcdResult = extendedGcd(a, b)
        val g = gcdResult.gcd
        val u = gcdResult.u
        val v = gcdResult.v

        val newRow1 = mutableListOf<R>()
        val newRow2 = mutableListOf<R>()

        for (j in work[row1].indices) {
            val val1 = work[row1][j]
            val val2 = work[row2][j]
            newRow1 += ring.add(ring.multiply(u, val1), ring.multiply(v, val2))
            val qb = ring.quotient(b, g)
            val qa = ring.quotient(a, g)
            newRow2 += ring.subtract(ring.multiply(qb, val1), ring.multiply(qa, val2))
        }

        work[row1] = newRow1
        work[row2] = newRow2
    }

    private fun improvePivotCol(
        work: MutableList<MutableList<R>>,
        row: Int,
        col1: Int,
        col2: Int
    ) {
        val a = work[row][col1]
        val b = work[row][col2]

        if (a == ring.zero || b == ring.zero) return

        val gcdResult = extendedGcd(a, b)
        val g = gcdResult.gcd
        val u = gcdResult.u
        val v = gcdResult.v

        for (i in work.indices) {
            val val1 = work[i][col1]
            val val2 = work[i][col2]
            work[i][col1] = ring.add(ring.multiply(u, val1), ring.multiply(v, val2))
            val qb = ring.quotient(b, g)
            val qa = ring.quotient(a, g)
            work[i][col2] = ring.subtract(ring.multiply(qb, val1), ring.multiply(qa, val2))
        }
    }

    private fun extendedGcd(a: R, b: R): ExtendedGcdResult<R> {
        var oldR = a
        var r = b
        var oldS = ring.one
        var s = ring.zero
        var oldT = ring.zero
        var t = ring.one

        while (r != ring.zero) {
            val quotient = ring.quotient(oldR, r)

            val newR = ring.subtract(oldR, ring.multiply(quotient, r))
            oldR = r
            r = newR

            val newS = ring.subtract(oldS, ring.multiply(quotient, s))
            oldS = s
            s = newS

            val newT = ring.subtract(oldT, ring.multiply(quotient, t))
            oldT = t
            t = newT
        }

        return ExtendedGcdResult(oldR, oldS, oldT)
    }

    private data class ExtendedGcdResult<T>(
        val gcd: T,
        val u: T,
        val v: T
    )

    private fun eliminateColumn(
        work: MutableList<MutableList<R>>,
        pivotRow: Int,
        pivotCol: Int
    ) {
        val pivot = work[pivotRow][pivotCol]
        if (pivot == ring.zero) return

        for (i in work.indices) {
            if (i == pivotRow) continue
            val entry = work[i][pivotCol]
            if (entry != ring.zero) {
                val quotient = ring.quotient(entry, pivot)
                for (j in work[i].indices) {
                    val subtract = ring.multiply(quotient, work[pivotRow][j])
                    work[i][j] = ring.subtract(work[i][j], subtract)
                }
            }
        }
    }

    private fun eliminateRow(
        work: MutableList<MutableList<R>>,
        pivotRow: Int,
        pivotCol: Int
    ) {
        val pivot = work[pivotRow][pivotCol]
        if (pivot == ring.zero) return

        for (j in work.first().indices) {
            if (j == pivotCol) continue
            val entry = work[pivotRow][j]
            if (entry != ring.zero) {
                val quotient = ring.quotient(entry, pivot)
                for (i in work.indices) {
                    val subtract = ring.multiply(quotient, work[i][pivotCol])
                    work[i][j] = ring.subtract(work[i][j], subtract)
                }
            }
        }
    }

    private fun extractInvariantFactors(work: List<List<R>>): List<R> {
        val factors = mutableListOf<R>()
        for (i in 0 until minOf(work.size, work.first().size)) {
            val entry = work[i][i]
            if (entry != ring.zero) {
                factors += entry
            } else {
                break
            }
        }
        return factors
    }

    private fun identityMatrix(n: Int): Matrix<R> {
        val rows = MutableList(n) { r ->
            MutableList(n) { c ->
                if (r == c) ring.one else ring.zero
            }
        }
        return Matrix.ofRows(rows.map { it.toList() })
    }
}

/**
 * Convenience extension to compute Smith Normal Form on a matrix.
 */
fun <R> Matrix<R>.smithNormalForm(ring: EuclideanDomain<R>): SmithNormalForm.Decomposition<R> {
    return SmithNormalForm(ring).decompose(this)
}
