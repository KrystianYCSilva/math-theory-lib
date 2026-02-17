package mathsets.logic

import mathsets.kernel.NaturalNumber

interface PeanoSystem<N> {
    val zero: N
    fun succ(n: N): N
    fun pred(n: N): N?
    fun isZero(n: N): Boolean
    fun verifyInjectivity(sampleSize: Int = 1000): Boolean
    fun verifyZeroNotSuccessor(sampleSize: Int = 1000): Boolean
    fun <R> recursion(base: R, step: (N, R) -> R): (N) -> R
}

object NaturalPeanoSystem : PeanoSystem<NaturalNumber> {
    override val zero: NaturalNumber = NaturalNumber.ZERO

    override fun succ(n: NaturalNumber): NaturalNumber = n.succ()

    override fun pred(n: NaturalNumber): NaturalNumber? = if (n.isZero()) null else n.pred()

    override fun isZero(n: NaturalNumber): Boolean = n.isZero()

    override fun verifyInjectivity(sampleSize: Int): Boolean {
        for (a in 0 until sampleSize) {
            for (b in 0 until sampleSize) {
                val na = NaturalNumber.of(a)
                val nb = NaturalNumber.of(b)
                if (succ(na) == succ(nb) && na != nb) return false
            }
        }
        return true
    }

    override fun verifyZeroNotSuccessor(sampleSize: Int): Boolean {
        for (n in 0 until sampleSize) {
            if (succ(NaturalNumber.of(n)) == zero) return false
        }
        return true
    }

    override fun <R> recursion(base: R, step: (NaturalNumber, R) -> R): (NaturalNumber) -> R {
        return { n ->
            var acc = base
            var current = NaturalNumber.ZERO
            while (current < n) {
                acc = step(current, acc)
                current = current.succ()
            }
            acc
        }
    }
}

