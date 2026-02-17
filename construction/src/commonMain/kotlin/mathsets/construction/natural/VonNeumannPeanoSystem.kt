package mathsets.construction.natural

import mathsets.logic.PeanoSystem

/**
 * Sistema de Peano sobre os naturais de Von Neumann.
 */
object VonNeumannPeanoSystem : PeanoSystem<VonNeumannNatural> {
    override val zero: VonNeumannNatural = VonNeumannNatural.ZERO

    override fun succ(n: VonNeumannNatural): VonNeumannNatural = n.succ()

    override fun pred(n: VonNeumannNatural): VonNeumannNatural? = n.predOrNull()

    override fun isZero(n: VonNeumannNatural): Boolean = n == VonNeumannNatural.ZERO

    override fun verifyInjectivity(sampleSize: Int): Boolean {
        for (a in 0 until sampleSize) {
            for (b in 0 until sampleSize) {
                val na = VonNeumannNatural.of(a)
                val nb = VonNeumannNatural.of(b)
                if (succ(na) == succ(nb) && na != nb) return false
            }
        }
        return true
    }

    override fun verifyZeroNotSuccessor(sampleSize: Int): Boolean {
        for (n in 0 until sampleSize) {
            if (succ(VonNeumannNatural.of(n)) == zero) return false
        }
        return true
    }

    override fun <R> recursion(base: R, step: (VonNeumannNatural, R) -> R): (VonNeumannNatural) -> R {
        return { n ->
            var acc = base
            var current: VonNeumannNatural = zero
            while (current != n) {
                acc = step(current, acc)
                current = succ(current)
            }
            acc
        }
    }
}

