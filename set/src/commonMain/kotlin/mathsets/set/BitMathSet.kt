package mathsets.set

import mathsets.kernel.Cardinality
import mathsets.kernel.NaturalNumber

class BitMathSet(private val size: Int, private val bits: LongArray) : MathSet<Int> {
    init {
        require(size >= 0) { "size must be non-negative" }
    }

    override val cardinality: Cardinality
        get() = Cardinality.Finite(NaturalNumber.of(elements().count()))

    override fun contains(element: Int): Boolean {
        if (element < 0 || element >= size) return false
        val idx = element / 64
        val bit = element % 64
        return ((bits[idx] ushr bit) and 1L) == 1L
    }

    override fun elements(): Sequence<Int> = sequence {
        for (i in 0 until size) {
            if (contains(i)) yield(i)
        }
    }

    override fun materialize(): ExtensionalSet<Int> = ExtensionalSet(elements().toSet())

    override fun union(other: MathSet<Int>): MathSet<Int> = when {
        other.cardinality.isFinite() -> ExtensionalSet(this.elements().toSet() + other.elements().toSet())
        else -> UnionSetView(this, other)
    }

    override fun intersect(other: MathSet<Int>): MathSet<Int> = ExtensionalSet(
        this.elements().filter { it in other }.toSet()
    )

    companion object {
        fun fromSet(size: Int, ints: Set<Int>): BitMathSet {
            val words = (size + 63) / 64
            val bits = LongArray(words)
            for (i in ints) {
                if (i in 0 until size) {
                    val idx = i / 64
                    val b = i % 64
                    bits[idx] = bits[idx] or (1L shl b)
                }
            }
            return BitMathSet(size, bits)
        }
    }
}

