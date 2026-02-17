package mathsets.set

import mathsets.kernel.Cardinality
import mathsets.kernel.NaturalNumber

/**
 * A compact, bit-vector-backed set of non-negative integers in the range `[0, size)`.
 *
 * Each integer `i` is stored as a single bit in a [LongArray]; membership tests and
 * enumeration operate directly on the bit representation, making this implementation
 * well-suited for dense integer sets where memory efficiency matters.
 *
 * @param size the exclusive upper bound of representable elements (must be non-negative).
 * @param bits the underlying [LongArray] where each bit encodes membership.
 */
class BitMathSet(private val size: Int, private val bits: LongArray) : MathSet<Int> {
    init {
        require(size >= 0) { "size must be non-negative" }
    }

    /**
     * The cardinality of this set, computed by counting the elements that have their
     * corresponding bits set.
     */
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
        /**
         * Constructs a [BitMathSet] from a plain [Set] of integers.
         *
         * Values outside the range `[0, size)` are silently ignored.
         *
         * @param size the exclusive upper bound.
         * @param ints the integers to include.
         * @return a new [BitMathSet].
         */
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
