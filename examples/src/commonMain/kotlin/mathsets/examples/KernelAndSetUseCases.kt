package mathsets.examples

import mathsets.function.Bijection
import mathsets.kernel.ComplexNumber
import mathsets.kernel.ExtendedReal
import mathsets.kernel.RealNumber
import mathsets.kernel.toExtendedReal
import mathsets.set.MathSet
import mathsets.set.mathSetOf

/**
 * Basic use cases demonstrating the kernel and set modules.
 *
 * Covers extended real arithmetic, complex numbers, set partitioning,
 * and bijection round-trips.
 */
object KernelAndSetUseCases {
    /**
     * Computes the reciprocal 1/x using extended real arithmetic, supporting infinity.
     *
     * @param value The real number to take the reciprocal of.
     * @return The reciprocal as an [ExtendedReal] (may be +infinity or -infinity).
     */
    fun reciprocal(value: RealNumber): ExtendedReal = ExtendedReal.ONE / value.toExtendedReal()

    /**
     * Computes the difference quotient (delta^2 - 0) / delta for f(x) = x^2 at x = 0.
     *
     * For delta = 0, this returns an indeterminate form (0/0).
     *
     * @param delta The step size.
     * @return The difference quotient as an [ExtendedReal].
     */
    fun squareDifferenceQuotient(delta: RealNumber): ExtendedReal {
        val numerator = (delta * delta) - RealNumber.ZERO
        return numerator.toExtendedReal() / delta.toExtendedReal()
    }

    /**
     * Returns the two complex roots of x^2 + 1 = 0, namely i and -i.
     *
     * @return A [Pair] of the two roots.
     */
    fun rootsOfXSquarePlusOne(): Pair<ComplexNumber, ComplexNumber> =
        Pair(ComplexNumber.I, -ComplexNumber.I)

    /**
     * Partitions a collection of integers into even and odd subsets.
     *
     * @param values The integers to partition.
     * @return A [Pair] of (evens, odds) as [MathSet]s.
     */
    fun partitionByParity(values: Iterable<Int>): Pair<MathSet<Int>, MathSet<Int>> {
        val base = mathSetOf(values)
        val evens = base.filter { it % 2 == 0 }
        val odds = base.filter { it % 2 != 0 }
        return Pair(evens, odds)
    }

    /**
     * Demonstrates a bijection round-trip: applies a bijection and then its inverse.
     *
     * Maps {1,2,3} to {"a","b","c"} and verifies that inverse(f(input)) == input.
     *
     * @param input An integer in {1, 2, 3}.
     * @return The original input after the round-trip.
     */
    fun bijectionRoundTrip(input: Int): Int {
        val domain = mathSetOf(1, 2, 3)
        val codomain = mathSetOf("a", "b", "c")

        val bijection = Bijection(domain, codomain) { n ->
            when (n) {
                1 -> "a"
                2 -> "b"
                3 -> "c"
                else -> error("Out of domain")
            }
        }

        return bijection.inverse()(bijection(input))
    }
}
