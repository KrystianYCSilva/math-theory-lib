package mathsets.ntheory

/**
 * Pell's equation solver: x² - D*y² = 1
 *
 * Uses continued fractions to find the fundamental solution.
 *
 * @param D The non-square positive integer D.
 */
class PellEquation(private val D: Long) {
    init {
        require(D > 0) { "D must be positive." }
        require(!isPerfectSquare(D)) { "D must not be a perfect square." }
    }

    /**
     * Fundamental solution (x₁, y₁) to x² - D*y² = 1.
     */
    data class Solution(val x: Long, val y: Long)

    /**
     * Finds the fundamental solution using continued fractions.
     */
    fun solve(): Solution {
        // Continued fraction expansion of √D
        val m0 = 0L
        val d0 = 1L
        val a0 = floorSqrt(D)

        var m = m0
        var d = d0
        var a = a0

        // Convergents p_k / q_k
        var pPrev2 = 0L
        var pPrev1 = 1L
        var qPrev2 = 1L
        var qPrev1 = 0L

        var k = 0
        while (true) {
            val p = a * pPrev1 + pPrev2
            val q = a * qPrev1 + qPrev2

            // Check if this convergent gives a solution
            val x = p
            val y = q
            val value = x * x - D * y * y

            if (value == 1L) {
                return Solution(x, y)
            }

            // Next term in continued fraction
            m = d * a - m
            d = (D - m * m) / d
            a = (a0 + m) / d

            pPrev2 = pPrev1
            pPrev1 = p
            qPrev2 = qPrev1
            qPrev1 = q

            k += 1

            // Safety limit to avoid infinite loops
            if (k > 10_000) break
        }

        throw RuntimeException("No solution found within iteration limit.")
    }

    /**
     * Generates the first n solutions from the fundamental solution.
     */
    fun generateSolutions(n: Int): List<Solution> {
        val fundamental = solve()
        val solutions = mutableListOf(fundamental)

        var xPrev = fundamental.x
        var yPrev = fundamental.y

        repeat(n - 1) {
            // (x_k + y_k*√D) = (x₁ + y₁*√D)^k
            // Use recurrence: x_{k+1} = x₁*x_k + D*y₁*y_k
            //                 y_{k+1} = x₁*y_k + y₁*x_k
            val xNext = fundamental.x * xPrev + D * fundamental.y * yPrev
            val yNext = fundamental.x * yPrev + fundamental.y * xPrev

            solutions += Solution(xNext, yNext)
            xPrev = xNext
            yPrev = yNext
        }

        return solutions
    }

    private fun isPerfectSquare(n: Long): Boolean {
        if (n < 0) return false
        if (n == 0L || n == 1L) return true
        val root = floorSqrt(n)
        return root * root == n
    }

    private fun floorSqrt(n: Long): Long {
        if (n < 0) throw IllegalArgumentException("Cannot compute sqrt of negative number.")
        if (n == 0L) return 0L

        var estimate = n / 2
        var prev: Long

        do {
            prev = estimate
            estimate = (estimate + n / estimate) / 2
        } while (kotlin.math.abs(estimate - prev) > 1)

        return estimate
    }

    companion object {
        /**
         * Solves Pell's equation for a given D.
         */
        fun solve(D: Long): Solution = PellEquation(D).solve()

        /**
         * Generates n solutions for Pell's equation with given D.
         */
        fun generateSolutions(D: Long, n: Int): List<Solution> = PellEquation(D).generateSolutions(n)
    }
}
