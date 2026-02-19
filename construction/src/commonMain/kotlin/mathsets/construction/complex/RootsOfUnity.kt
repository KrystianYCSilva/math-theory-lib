package mathsets.construction.complex

import mathsets.construction.real.ConstructedReal
import mathsets.kernel.RealNumber
import mathsets.construction.real.toMathReal
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Explicit embedding R -> C.
 */
object RealComplexEmbedding {
    /**
     * Embeds a constructed real into the complex plane as `r + 0i`.
     *
     * @param value Constructed real value.
     * @return Embedded complex value.
     */
    fun embed(value: ConstructedReal): ConstructedComplex = ConstructedComplex.fromReal(value)
}

/**
 * Utilities for roots of unity in constructed complex numbers.
 */
object RootsOfUnity {
    /**
     * Returns `exp(2*pi*i*k/n)` as a constructed complex approximation.
     *
     * @param n Positive order.
     * @param k Integer exponent.
     * @return The k-th n-th root of unity.
     */
    fun zeta(n: Int, k: Int = 1): ConstructedComplex {
        require(n > 0) { "n must be positive." }
        val angle = 2.0 * PI * k / n
        val real = RealNumber.of(cos(angle)).toMathReal()
        val imag = RealNumber.of(sin(angle)).toMathReal()
        return ConstructedComplex.of(real, imag)
    }

    /**
     * Returns all n-th roots of unity.
     *
     * @param n Positive order.
     * @return List `[zeta(n,0), ..., zeta(n,n-1)]`.
     */
    fun all(n: Int): List<ConstructedComplex> {
        require(n > 0) { "n must be positive." }
        return (0 until n).map { k -> zeta(n, k) }
    }

    /**
     * Checks that `z^n = 1` for each sampled n-th root.
     *
     * @param n Positive order.
     * @param tolerance Numeric tolerance on both real and imaginary parts.
     * @return True when all roots satisfy the equation within tolerance.
     */
    fun verifyEquation(n: Int, tolerance: Double = 1e-6): Boolean {
        val roots = all(n)
        val toleranceReal = RealNumber.of(tolerance)
        return roots.all { root ->
            val power = pow(root, n)
            val delta = power - ConstructedComplex.ONE
            delta.real.toKernel().abs() <= toleranceReal &&
                delta.imaginary.toKernel().abs() <= toleranceReal
        }
    }

    private fun pow(base: ConstructedComplex, exponent: Int): ConstructedComplex {
        require(exponent >= 0) { "Exponent must be non-negative." }
        var result = ConstructedComplex.ONE
        var b = base
        var e = exponent
        while (e > 0) {
            if (e % 2 == 1) result = result * b
            b = b * b
            e /= 2
        }
        return result
    }
}
