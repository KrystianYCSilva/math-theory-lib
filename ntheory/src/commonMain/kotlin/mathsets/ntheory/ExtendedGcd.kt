package mathsets.ntheory

/**
 * Result of the extended Euclidean algorithm.
 *
 * @property gcd Greatest common divisor of input values.
 * @property x Bezout coefficient for first input.
 * @property y Bezout coefficient for second input.
 */
data class ExtendedGcdResult(
    val gcd: Long,
    val x: Long,
    val y: Long
) {
    /**
     * Verifies Bezout identity: a*x + b*y == gcd.
     */
    fun verifies(a: Long, b: Long): Boolean = a * x + b * y == gcd
}

/**
 * Extended Euclidean algorithm utilities.
 */
object ExtendedGcd {
    /**
     * Computes gcd(a, b) and Bezout coefficients.
     */
    fun compute(a: Long, b: Long): ExtendedGcdResult {
        var oldR = a
        var r = b

        var oldS = 1L
        var s = 0L

        var oldT = 0L
        var t = 1L

        while (r != 0L) {
            val q = oldR / r

            val tmpR = oldR - q * r
            oldR = r
            r = tmpR

            val tmpS = oldS - q * s
            oldS = s
            s = tmpS

            val tmpT = oldT - q * t
            oldT = t
            t = tmpT
        }

        if (oldR < 0L) {
            return ExtendedGcdResult(
                gcd = -oldR,
                x = -oldS,
                y = -oldT
            )
        }

        return ExtendedGcdResult(
            gcd = oldR,
            x = oldS,
            y = oldT
        )
    }
}
