package mathsets.construction.natural

/**
 * Ordem em naturais de Von Neumann via definicao:
 * a <= b  <=>  existe c tal que a + c = b
 */
object VonNeumannNaturalOrder {
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

    fun lessThan(a: VonNeumannNatural, b: VonNeumannNatural): Boolean =
        lessOrEqual(a, b) && a != b

    fun greaterOrEqual(a: VonNeumannNatural, b: VonNeumannNatural): Boolean =
        lessOrEqual(b, a)

    fun greaterThan(a: VonNeumannNatural, b: VonNeumannNatural): Boolean =
        lessOrEqual(b, a) && a != b
}

