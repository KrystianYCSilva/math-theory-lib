package mathsets.linalg

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import mathsets.algebra.RationalField
import mathsets.kernel.RationalNumber
import mathsets.kernel.RealNumber

class AdvancedLinearAlgebraTest : FunSpec({
    val linear = MatrixLinearAlgebra(RationalField)
    val advanced = AdvancedMatrixAlgebra(RationalField)
    val inner = InnerProductOps(RationalField)

    fun q(n: Int, d: Int = 1): RationalNumber = RationalNumber.of(n, d)

    test("lu decomposition reconstructs matrix with permutation") {
        val matrix = Matrix.ofRows(
            listOf(
                listOf(q(0), q(2), q(1)),
                listOf(q(1), q(1), q(0)),
                listOf(q(2), q(3), q(4))
            )
        )

        advanced.verifiesLuDecomposition(matrix) shouldBe true
    }

    test("determinant via lu matches elimination") {
        val matrix = Matrix.ofRows(
            listOf(
                listOf(q(3), q(1), q(2)),
                listOf(q(0), q(4), q(5)),
                listOf(q(1), q(2), q(6))
            )
        )

        advanced.determinantByLu(matrix) shouldBe linear.determinant(matrix)
    }

    test("kronecker product builds block matrix") {
        val a = Matrix.ofRows(
            listOf(
                listOf(q(1), q(2)),
                listOf(q(3), q(4))
            )
        )

        val b = Matrix.ofRows(
            listOf(
                listOf(q(0), q(5)),
                listOf(q(6), q(7))
            )
        )

        val product = advanced.kroneckerProduct(a, b)

        product shouldBe Matrix.ofRows(
            listOf(
                listOf(q(0), q(5), q(0), q(10)),
                listOf(q(6), q(7), q(12), q(14)),
                listOf(q(0), q(15), q(0), q(20)),
                listOf(q(18), q(21), q(24), q(28))
            )
        )
    }

    test("characteristic polynomial for 2x2 is computed") {
        val matrix = Matrix.ofRows(
            listOf(
                listOf(q(2), q(1)),
                listOf(q(1), q(1))
            )
        )

        val polynomial = advanced.characteristicPolynomial2x2(matrix)
        polynomial.lambdaSquared shouldBe q(1)
        polynomial.lambda shouldBe q(-3)
        polynomial.constant shouldBe q(1)
    }

    test("gram schmidt returns orthogonal vectors") {
        val vectors = listOf(
            Vector(listOf(q(1), q(1))),
            Vector(listOf(q(1), q(0)))
        )

        val orthogonal = inner.gramSchmidt(vectors)
        orthogonal.size shouldBe 2
        inner.dot(orthogonal[0], orthogonal[1]) shouldBe RationalNumber.ZERO
    }

    test("real eigenvalues for symmetric 2x2 matrix") {
        val matrix = Matrix.ofRows(
            listOf(
                listOf(RealNumber.of(2), RealNumber.of(1)),
                listOf(RealNumber.of(1), RealNumber.of(2))
            )
        )

        val eigenvalues = RealEigenvalues.eigenvalues2x2(matrix).sorted()

        ((eigenvalues[0] - RealNumber.ONE).abs() < RealNumber.parse("0.0001")) shouldBe true
        ((eigenvalues[1] - RealNumber.of(3)).abs() < RealNumber.parse("0.0001")) shouldBe true
    }

    test("real eigenvalue helper returns empty for non-real pair") {
        val matrix = Matrix.ofRows(
            listOf(
                listOf(RealNumber.ZERO, RealNumber.of(-1)),
                listOf(RealNumber.ONE, RealNumber.ZERO)
            )
        )

        RealEigenvalues.eigenvalues2x2(matrix).isEmpty() shouldBe true
    }
})
