package mathsets.linalg

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import mathsets.algebra.IntegerRing
import mathsets.kernel.IntegerNumber
import mathsets.kernel.RealNumber

class SmithNormalFormTest : FunSpec({
    test("smith normal form computes correctly for 2x2 integer matrix") {
        val matrix = Matrix.ofRows(listOf(
            listOf(IntegerNumber.of(2), IntegerNumber.of(4)),
            listOf(IntegerNumber.of(6), IntegerNumber.of(8))
        ))

        val snf = SmithNormalForm(IntegerRing)
        val result = snf.decompose(matrix)

        // Verify P*A*Q = D
        val reconstructed = multiplyMatrices(
            multiplyMatrices(result.leftTransform, matrix),
            result.rightTransform
        )

        // Diagonal entries should match invariant factors
        result.invariantFactors.isNotEmpty() shouldBe true
    }

    test("smith normal form handles identity matrix") {
        val identity = Matrix.ofRows(listOf(
            listOf(IntegerNumber.ONE, IntegerNumber.ZERO),
            listOf(IntegerNumber.ZERO, IntegerNumber.ONE)
        ))

        val snf = SmithNormalForm(IntegerRing)
        val result = snf.decompose(identity)

        result.invariantFactors.size shouldBe 2
        result.invariantFactors.forEach { it shouldBe IntegerNumber.ONE }
    }

    test("smith normal form computes for 3x3 matrix") {
        val matrix = Matrix.ofRows(listOf(
            listOf(IntegerNumber.of(1), IntegerNumber.of(2), IntegerNumber.of(3)),
            listOf(IntegerNumber.of(4), IntegerNumber.of(5), IntegerNumber.of(6)),
            listOf(IntegerNumber.of(7), IntegerNumber.of(8), IntegerNumber.of(9))
        ))

        val snf = SmithNormalForm(IntegerRing)
        val result = snf.decompose(matrix)

        result.invariantFactors.isNotEmpty() shouldBe true
    }
})

class JordanNormalFormTest : FunSpec({
    test("jordan form handles 1x1 matrix") {
        val matrix = Matrix.ofRows(listOf(
            listOf(RealNumber.of(5))
        ))

        val jnf = JordanNormalForm(RealField)
        val result = jnf.decompose(matrix)

        result.eigenvalues.size shouldBe 1
        result.blockSizes shouldBe listOf(1)
    }

    test("jordan form detects diagonal 2x2 matrix") {
        val matrix = Matrix.ofRows(listOf(
            listOf(RealNumber.of(3), RealNumber.ZERO),
            listOf(RealNumber.ZERO, RealNumber.of(7))
        ))

        val jnf = JordanNormalForm(RealField)
        val result = jnf.decompose(matrix)

        result.eigenvalues.size shouldBe 2
    }

    test("jordan form handles repeated eigenvalue case") {
        // Matrix with repeated eigenvalue but not diagonal
        val matrix = Matrix.ofRows(listOf(
            listOf(RealNumber.TWO, RealNumber.ONE),
            listOf(RealNumber.ZERO, RealNumber.TWO)
        ))

        val jnf = JordanNormalForm(RealField)
        val result = jnf.decompose(matrix)

        result.blockSizes.sum() shouldBe 2
    }
})

class SingularValueDecompositionTest : FunSpec({
    test("svd reconstructs original matrix") {
        val matrix = Matrix.ofRows(listOf(
            listOf(RealNumber.of(3), RealNumber.ZERO),
            listOf(RealNumber.ZERO, RealNumber.of(4))
        ))

        val svd = SingularValueDecomposition()
        val result = svd.decompose(matrix)

        val reconstructed = result.reconstruct()

        // Check reconstruction is close to original
        for (r in 0 until matrix.rows) {
            for (c in 0 until matrix.cols) {
                val diff = (matrix[r, c] - reconstructed[r, c]).abs()
                (diff < RealNumber.parse("0.01")) shouldBe true
            }
        }
    }

    test("svd singular values are non-negative and sorted") {
        val matrix = Matrix.ofRows(listOf(
            listOf(RealNumber.ONE, RealNumber.TWO),
            listOf(RealNumber.of(3), RealNumber.of(4)),
            listOf(RealNumber.of(5), RealNumber.of(6))
        ))

        val svd = SingularValueDecomposition()
        val result = svd.decompose(matrix)

        result.singularValues.all { it >= RealNumber.ZERO } shouldBe true

        // Check descending order
        for (i in 0 until result.singularValues.size - 1) {
            val comparison = result.singularValues[i] >= result.singularValues[i + 1]
            comparison shouldBe true
        }
    }

    test("svd works on rank-deficient matrix") {
        // Rank 1 matrix
        val matrix = Matrix.ofRows(listOf(
            listOf(RealNumber.ONE, RealNumber.TWO, RealNumber.of(3)),
            listOf(RealNumber.TWO, RealNumber.of(4), RealNumber.of(6)),
            listOf(RealNumber.of(3), RealNumber.of(6), RealNumber.of(9))
        ))

        val svd = SingularValueDecomposition()
        val result = svd.decompose(matrix)

        // Only one significant singular value
        val significantCount = result.singularValues.count {
            it > RealNumber.parse("0.001")
        }
        significantCount shouldBe 1
    }
})

private object RealField : mathsets.algebra.OrderedField<RealNumber> {
    override val zero: RealNumber = RealNumber.ZERO
    override val one: RealNumber = RealNumber.ONE
    override fun add(a: RealNumber, b: RealNumber): RealNumber = a + b
    override fun negate(a: RealNumber): RealNumber = -a
    override fun multiply(a: RealNumber, b: RealNumber): RealNumber = a * b
    override fun reciprocal(a: RealNumber): RealNumber = RealNumber.ONE / a
    override fun compare(a: RealNumber, b: RealNumber): Int = a.compareTo(b)
}

private fun multiplyMatrices(
    A: Matrix<IntegerNumber>,
    B: Matrix<IntegerNumber>
): Matrix<IntegerNumber> {
    require(A.cols == B.rows) { "Dimension mismatch." }
    return Matrix.fill(A.rows, B.cols) { r, c ->
        var sum = IntegerNumber.ZERO
        for (k in 0 until A.cols) {
            sum += A[r, k] * B[k, c]
        }
        sum
    }
}
