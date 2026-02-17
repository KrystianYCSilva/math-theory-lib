package mathsets.construction.natural

/**
 * Total order on Von Neumann naturals defined by the existence-of-witness characterization:
 *
 * `a <= b` if and only if there exists a natural `c` such that `a + c = b`.
 *
 * This is the axiomatic definition of order on the naturals, verified
 * by iterating over candidate witnesses starting from zero.
 *
 * @see VonNeumannNaturalArithmetic
 */
object VonNeumannNaturalOrder {
    /**
     * Tests whether `a <= b` by searching for a witness `c` with `a + c = b`.
     *
     * @param a the left operand.
     * @param b the right operand.
     * @return `true` if `a <= b`.
     */
    fun lessOrEqual(a: VonNeumannNatural, b: VonNeumannNatural): Boolean {
        var witness: VonNeumannNatural = VonNeumannNatural.ZERO
        while (true) {
            val sum = VonNeumannNaturalArithmetic.add(a, witness)
            val comparison = VonNeumannNaturalArithmetic.compare(sum, b)
            if (comparison == 0) return true
            if (comparison > 0) return false
            witness = witness.succ()
        }
    }

    /**
     * Tests whether `a < b` (strict inequality).
     *
     * @param a the left operand.
     * @param b the right operand.
     * @return `true` if `a < b`.
     */
    fun lessThan(a: VonNeumannNatural, b: VonNeumannNatural): Boolean =
        lessOrEqual(a, b) && a != b

    /**
     * Tests whether `a >= b`.
     *
     * @param a the left operand.
     * @param b the right operand.
     * @return `true` if `a >= b`.
     */
    fun greaterOrEqual(a: VonNeumannNatural, b: VonNeumannNatural): Boolean =
        lessOrEqual(b, a)

    /**
     * Tests whether `a > b` (strict inequality).
     *
     * @param a the left operand.
     * @param b the right operand.
     * @return `true` if `a > b`.
     */
    fun greaterThan(a: VonNeumannNatural, b: VonNeumannNatural): Boolean =
        lessOrEqual(b, a) && a != b
}
