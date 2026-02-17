package mathsets.kernel.analysis

import mathsets.kernel.ExtendedReal
import mathsets.kernel.RealNumber
import mathsets.kernel.toExtendedReal

/**
 * Specifies the direction of approach for one-sided limits.
 */
enum class Side {
    /** Approach from the left (from below). */
    LEFT,

    /** Approach from the right (from above). */
    RIGHT
}

/**
 * Deterministic limit primitives based on [ExtendedReal] arithmetic.
 *
 * This module does not attempt to solve general symbolic limits. It provides
 * deterministic rules for fundamental cases used in computational calculus,
 * leveraging the extended real number line's built-in handling of infinity
 * and indeterminate forms.
 *
 * @see ExtendedReal
 * @see Derivatives
 */
object Limits {

    /**
     * Computes the limit of a quotient using extended real division rules.
     *
     * Handles cases such as finite/finite, finite/0, 0/0, and infinity/infinity
     * according to [ExtendedReal] arithmetic conventions.
     *
     * @param numerator The numerator as an [ExtendedReal].
     * @param denominator The denominator as an [ExtendedReal].
     * @return The quotient as an [ExtendedReal], possibly [ExtendedReal.Indeterminate].
     */
    fun quotient(numerator: ExtendedReal, denominator: ExtendedReal): ExtendedReal =
        numerator / denominator

    /**
     * Computes the reciprocal 1/x for a finite real value.
     *
     * @param value The real number to invert.
     * @return The reciprocal as an [ExtendedReal] (may be infinite if [value] is zero).
     */
    fun reciprocal(value: RealNumber): ExtendedReal =
        ExtendedReal.ONE / value.toExtendedReal()

    /**
     * Returns the one-sided limit of 1/x as x approaches 0.
     *
     * @param side The direction of approach ([Side.LEFT] or [Side.RIGHT]).
     * @return [ExtendedReal.NegativeInfinity] for left approach, [ExtendedReal.PositiveInfinity] for right.
     */
    fun reciprocalAtZero(side: Side): ExtendedReal = when (side) {
        Side.LEFT -> ExtendedReal.NEGATIVE_INFINITY
        Side.RIGHT -> ExtendedReal.POSITIVE_INFINITY
    }

    /**
     * Returns the two-sided limit of 1/x as x approaches 0.
     *
     * Since the left and right limits disagree, this is [ExtendedReal.Indeterminate].
     *
     * @return [ExtendedReal.Indeterminate].
     */
    fun twoSidedReciprocalAtZero(): ExtendedReal = ExtendedReal.INDETERMINATE

    /**
     * Computes the difference quotient (f(a + delta) - f(a)) / delta.
     *
     * This is the fundamental building block for numerical differentiation.
     *
     * @param f The real-valued function to evaluate.
     * @param at The point at which to compute the difference quotient.
     * @param delta The step size (should be small for derivative approximation).
     * @return The difference quotient as an [ExtendedReal].
     */
    fun differenceQuotient(
        f: (RealNumber) -> RealNumber,
        at: RealNumber,
        delta: RealNumber
    ): ExtendedReal {
        val numerator = f(at + delta) - f(at)
        return numerator.toExtendedReal() / delta.toExtendedReal()
    }
}
