package mathsets.kernel

/**
 * Reta real estendida para operações com infinito no kernel.
 *
 * Regras importantes:
 * - +inf + (-inf) = indeterminate
 * - inf * 0 = indeterminate
 * - 0 / 0 = indeterminate
 * - inf / inf = indeterminate
 */
sealed interface ExtendedReal : Comparable<ExtendedReal>, MathElement {
    data class Finite(val value: RealNumber) : ExtendedReal

    data object PositiveInfinity : ExtendedReal
    data object NegativeInfinity : ExtendedReal

    /**
     * Resultado indefinido para formas indeterminadas.
     */
    data object Indeterminate : ExtendedReal

    fun isFinite(): Boolean = this is Finite

    fun isInfinite(): Boolean = this is PositiveInfinity || this is NegativeInfinity

    fun toRealOrNull(): RealNumber? = (this as? Finite)?.value

    operator fun unaryMinus(): ExtendedReal = when (this) {
        is Finite -> Finite(-value)
        PositiveInfinity -> NegativeInfinity
        NegativeInfinity -> PositiveInfinity
        Indeterminate -> Indeterminate
    }

    operator fun plus(other: ExtendedReal): ExtendedReal = when {
        this is Indeterminate || other is Indeterminate -> Indeterminate
        this is PositiveInfinity && other is NegativeInfinity -> Indeterminate
        this is NegativeInfinity && other is PositiveInfinity -> Indeterminate
        this is PositiveInfinity || other is PositiveInfinity -> PositiveInfinity
        this is NegativeInfinity || other is NegativeInfinity -> NegativeInfinity
        this is Finite && other is Finite -> Finite(this.value + other.value)
        else -> Indeterminate
    }

    operator fun minus(other: ExtendedReal): ExtendedReal = this + (-other)

    operator fun times(other: ExtendedReal): ExtendedReal {
        if (this is Indeterminate || other is Indeterminate) return Indeterminate

        if ((this is Finite && this.value.isZero() && other.isInfinite()) ||
            (other is Finite && other.value.isZero() && this.isInfinite())
        ) {
            return Indeterminate
        }

        if (this is Finite && other is Finite) {
            return Finite(this.value * other.value)
        }

        val leftSign = this.signumForInfinityArithmetic()
        val rightSign = other.signumForInfinityArithmetic()

        if (leftSign == 0 || rightSign == 0) return Indeterminate

        return if (leftSign * rightSign > 0) PositiveInfinity else NegativeInfinity
    }

    operator fun div(other: ExtendedReal): ExtendedReal {
        if (this is Indeterminate || other is Indeterminate) return Indeterminate

        if (this is Finite && other is Finite) {
            if (other.value.isZero()) {
                if (this.value.isZero()) return Indeterminate
                return if (this.value.signum() > 0) PositiveInfinity else NegativeInfinity
            }
            return Finite(this.value / other.value)
        }

        if (this.isInfinite() && other.isInfinite()) return Indeterminate

        if (this.isInfinite() && other is Finite) {
            if (other.value.isZero()) return Indeterminate
            val sign = this.signumForInfinityArithmetic() * other.value.signum()
            return if (sign > 0) PositiveInfinity else NegativeInfinity
        }

        if (this is Finite && other.isInfinite()) {
            return Finite(RealNumber.ZERO)
        }

        return Indeterminate
    }

    override fun compareTo(other: ExtendedReal): Int {
        require(this !is Indeterminate && other !is Indeterminate) {
            "Indeterminate values are not ordered."
        }

        return when {
            this is PositiveInfinity && other is PositiveInfinity -> 0
            this is NegativeInfinity && other is NegativeInfinity -> 0
            this is PositiveInfinity -> 1
            other is PositiveInfinity -> -1
            this is NegativeInfinity -> -1
            other is NegativeInfinity -> 1
            this is Finite && other is Finite -> this.value.compareTo(other.value)
            else -> 0
        }
    }

    companion object {
        val ZERO: ExtendedReal = Finite(RealNumber.ZERO)
        val ONE: ExtendedReal = Finite(RealNumber.ONE)
        val POSITIVE_INFINITY: ExtendedReal = PositiveInfinity
        val NEGATIVE_INFINITY: ExtendedReal = NegativeInfinity
        val INDETERMINATE: ExtendedReal = Indeterminate

        fun of(value: RealNumber): ExtendedReal = Finite(value)
        fun of(value: RationalNumber): ExtendedReal = Finite(RealNumber.of(value))
        fun of(value: Int): ExtendedReal = Finite(RealNumber.of(value))
        fun of(value: Long): ExtendedReal = Finite(RealNumber.of(value))
        fun of(value: Double): ExtendedReal = Finite(RealNumber.of(value))
        fun parse(value: String): ExtendedReal = Finite(RealNumber.parse(value))
    }
}

fun RealNumber.toExtendedReal(): ExtendedReal = ExtendedReal.of(this)
fun RationalNumber.toExtendedReal(): ExtendedReal = ExtendedReal.of(this)

private fun ExtendedReal.signumForInfinityArithmetic(): Int = when (this) {
    is ExtendedReal.Finite -> value.signum()
    ExtendedReal.PositiveInfinity -> 1
    ExtendedReal.NegativeInfinity -> -1
    ExtendedReal.Indeterminate -> 0
}

