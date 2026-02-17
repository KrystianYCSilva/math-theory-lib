package mathsets.construction.natural

import mathsets.kernel.MathElement
import mathsets.kernel.NaturalNumber
import mathsets.set.MathSet
import mathsets.set.mathSetOf

/**
 * Natural de Von Neumann:
 * - 0 = ∅
 * - S(n) = n U {n}
 */
sealed interface VonNeumannNatural : MathElement {
    fun toSet(): MathSet<VonNeumannNatural>

    fun succ(): VonNeumannNatural = Succ(this)

    fun predOrNull(): VonNeumannNatural? = when (this) {
        Zero -> null
        is Succ -> previous
    }

    data object Zero : VonNeumannNatural {
        override fun toSet(): MathSet<VonNeumannNatural> = MathSet.empty()
        override fun toString(): String = "0"
    }

    data class Succ(val previous: VonNeumannNatural) : VonNeumannNatural {
        override fun toSet(): MathSet<VonNeumannNatural> =
            previous.toSet() union MathSet.singleton(previous)
    }

    companion object {
        val ZERO: VonNeumannNatural = Zero
        val ONE: VonNeumannNatural = Succ(Zero)

        fun of(value: NaturalNumber): VonNeumannNatural {
            var result: VonNeumannNatural = Zero
            var current = NaturalNumber.ZERO
            while (current < value) {
                result = Succ(result)
                current = current.succ()
            }
            return result
        }

        fun of(value: Int): VonNeumannNatural = of(NaturalNumber.of(value))

        fun fromSetRepresentation(elements: Iterable<VonNeumannNatural>): VonNeumannNatural {
            val ordered = elements.sortedBy { NaturalIsomorphism.toKernel(it) }
            return if (ordered.isEmpty()) {
                Zero
            } else {
                ordered.last().succ()
            }
        }

        fun finitePrefix(size: Int): MathSet<VonNeumannNatural> {
            require(size >= 0) { "size must be non-negative" }
            return mathSetOf((0 until size).map { of(it) })
        }
    }
}

