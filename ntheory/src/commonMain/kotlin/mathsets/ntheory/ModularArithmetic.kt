package mathsets.ntheory

/**
 * Arithmetic in Z/nZ.
 */
object ModularArithmetic {
    /**
     * Canonical representative in [0, modulus).
     */
    fun normalize(value: Long, modulus: Long): Long = mathsets.ntheory.normalize(value, modulus)

    /**
     * Modular addition.
     */
    fun add(a: Long, b: Long, modulus: Long): Long = normalize(normalize(a, modulus) + normalize(b, modulus), modulus)

    /**
     * Modular subtraction.
     */
    fun subtract(a: Long, b: Long, modulus: Long): Long = normalize(normalize(a, modulus) - normalize(b, modulus), modulus)

    /**
     * Modular multiplication.
     */
    fun multiply(a: Long, b: Long, modulus: Long): Long = mulMod(a, b, modulus)

    /**
     * Modular exponentiation.
     */
    fun power(base: Long, exponent: Long, modulus: Long): Long = powMod(base, exponent, modulus)

    /**
     * Multiplicative inverse modulo n, when gcd(a, n) = 1.
     */
    fun inverse(value: Long, modulus: Long): Long? {
        val result = ExtendedGcd.compute(value, modulus)
        if (result.gcd != 1L) return null
        return normalize(result.x, modulus)
    }

    /**
     * Modular division, when divisor is invertible.
     */
    fun divide(a: Long, b: Long, modulus: Long): Long? =
        inverse(b, modulus)?.let { inv -> multiply(a, inv, modulus) }
}
