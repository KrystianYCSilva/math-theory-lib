package mathsets.kernel.analysis

import mathsets.kernel.ExtendedReal
import mathsets.kernel.RealNumber
import mathsets.kernel.toExtendedReal

/**
 * Numerical derivative operators based on finite difference quotients.
 *
 * Provides forward and symmetric (central) difference methods for approximating
 * derivatives of real-valued functions. These are building blocks for numerical
 * analysis, not symbolic differentiation.
 *
 * @see Limits
 * @see ExtendedReal
 */
object Derivatives {

    /**
     * Computes the forward difference approximation of the derivative:
     * f'(x) ~ (f(x + h) - f(x)) / h.
     *
     * This is a first-order approximation with error O(h).
     *
     * @param f The real-valued function to differentiate.
     * @param at The point at which to approximate the derivative.
     * @param h The step size (should be small).
     * @return The forward difference quotient as an [ExtendedReal].
     */
    fun forwardDifference(
        f: (RealNumber) -> RealNumber,
        at: RealNumber,
        h: RealNumber
    ): ExtendedReal = Limits.differenceQuotient(f, at, h)

    /**
     * Computes the symmetric (central) difference approximation of the derivative:
     * f'(x) ~ (f(x + h) - f(x - h)) / (2h).
     *
     * This is a second-order approximation with error O(h^2), generally more
     * accurate than the forward difference for smooth functions.
     *
     * @param f The real-valued function to differentiate.
     * @param at The point at which to approximate the derivative.
     * @param h The half-step size (should be small).
     * @return The symmetric difference quotient as an [ExtendedReal].
     */
    fun symmetricDifference(
        f: (RealNumber) -> RealNumber,
        at: RealNumber,
        h: RealNumber
    ): ExtendedReal {
        val numerator = f(at + h) - f(at - h)
        val denominator = h + h
        return numerator.toExtendedReal() / denominator.toExtendedReal()
    }
}
