package mathsets.linalg

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import mathsets.algebra.RationalField
import mathsets.kernel.RationalNumber

class MatrixTest : FunSpec({
    val algebra = MatrixLinearAlgebra(RationalField)

    fun q(n: Int, d: Int = 1): RationalNumber = RationalNumber.of(n, d)

    test("sparse and dense representations roundtrip") {
        val dense = Matrix.ofRows(
            listOf(
                listOf(q(1), q(0), q(2)),
                listOf(q(0), q(0), q(3))
            )
        )

        val sparse = SparseMatrix.fromDense(dense, RationalNumber.ZERO)
        sparse.entries.size shouldBe 3
        sparse.toDense(RationalNumber.ZERO) shouldBe dense
    }

    test("gaussian elimination solves a unique system over rationals") {
        val a = Matrix.ofRows(
            listOf(
                listOf(q(1), q(1), q(1)),
                listOf(q(2), q(3), q(1)),
                listOf(q(1), q(-1), q(2))
            )
        )
        val b = listOf(q(6), q(11), q(5))

        val solution = algebra.solve(a, b)
        (solution is LinearSystemSolution.Unique<RationalNumber>) shouldBe true
        val values = (solution as LinearSystemSolution.Unique<RationalNumber>).values
        values shouldBe listOf(q(1), q(2), q(3))
    }

    test("linear-system classification detects inconsistent and infinite cases") {
        val dependent = Matrix.ofRows(
            listOf(
                listOf(q(1), q(1)),
                listOf(q(2), q(2))
            )
        )

        algebra.solve(dependent, listOf(q(1), q(3))) shouldBe LinearSystemSolution.Inconsistent

        val infinite = algebra.solve(dependent, listOf(q(1), q(2)))
        (infinite is LinearSystemSolution.Infinite) shouldBe true
        val info = infinite as LinearSystemSolution.Infinite
        info.rank shouldBe 1
        info.freeVariables shouldBe 1
    }

    test("determinant and inverse satisfy det(A) * det(A^-1) = 1") {
        val a = Matrix.ofRows(
            listOf(
                listOf(q(2), q(1)),
                listOf(q(1), q(1))
            )
        )

        val det = algebra.determinant(a)
        det shouldBe q(1)

        val inverse = algebra.inverse(a)
        (inverse != null) shouldBe true
        val detInv = algebra.determinant(inverse!!)
        RationalField.multiply(det, detInv) shouldBe RationalNumber.ONE

        algebra.multiply(a, inverse) shouldBe algebra.identity(2)
    }

    test("leibniz and elimination determinants agree on 3x3") {
        val a = Matrix.ofRows(
            listOf(
                listOf(q(1), q(2), q(3)),
                listOf(q(0), q(1), q(4)),
                listOf(q(5), q(6), q(0))
            )
        )

        val detElimination = algebra.determinant(a)
        val detLeibniz = algebra.determinantLeibniz(a)

        detElimination shouldBe q(1)
        detLeibniz shouldBe q(1)
    }

    test("cayley-hamilton holds for 2x2 sample") {
        val a = Matrix.ofRows(
            listOf(
                listOf(q(2), q(1)),
                listOf(q(1), q(1))
            )
        )

        val trace = q(3)
        val determinant = q(1)

        val aSquared = algebra.power(a, 2)
        val term = algebra.add(
            aSquared,
            algebra.add(
                algebra.scalarMultiply(q(-3), a),
                algebra.scalarMultiply(determinant, algebra.identity(2))
            )
        )

        term shouldBe algebra.zero(2, 2)
        trace shouldBe q(3)
    }
})
