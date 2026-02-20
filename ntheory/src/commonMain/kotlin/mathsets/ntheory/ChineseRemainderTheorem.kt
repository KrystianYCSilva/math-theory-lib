package mathsets.ntheory

/**
 * Single congruence x == residue (mod modulus).
 */
data class Congruence(
    val residue: Long,
    val modulus: Long
) {
    init {
        require(modulus > 0) { "modulus must be positive." }
    }
}

/**
 * Solution for a congruence system.
 */
data class CongruenceSolution(
    val residue: Long,
    val modulus: Long
) {
    init {
        require(modulus > 0) { "modulus must be positive." }
    }

    /**
     * Verifies whether this solution satisfies all equations.
     */
    fun satisfies(system: List<Congruence>): Boolean =
        system.all { equation ->
            normalize(residue, equation.modulus) == normalize(equation.residue, equation.modulus)
        }
}

/**
 * Chinese remainder theorem reconstruction.
 */
object ChineseRemainderTheorem {
    /**
     * Solves a finite congruence system.
     *
     * Returns null when no solution exists.
     */
    fun solve(system: List<Congruence>): CongruenceSolution? {
        if (system.isEmpty()) return CongruenceSolution(0L, 1L)

        var residue = normalize(system.first().residue, system.first().modulus)
        var modulus = system.first().modulus

        for (equation in system.drop(1)) {
            val targetResidue = normalize(equation.residue, equation.modulus)
            val targetModulus = equation.modulus

            val g = gcd(modulus, targetModulus)
            val difference = targetResidue - residue
            if (difference % g != 0L) return null

            val left = modulus / g
            val right = targetModulus / g

            val scaledDifference = difference / g
            val inverse = ModularArithmetic.inverse(left, right) ?: return null
            val k = ModularArithmetic.multiply(scaledDifference, inverse, right)

            val lcm = modulus * right
            residue = normalize(residue + modulus * k, lcm)
            modulus = lcm
        }

        return CongruenceSolution(residue, modulus)
    }
}
