package mathsets.kernel.analysis

import mathsets.kernel.ExtendedReal
import mathsets.kernel.RealNumber
import mathsets.kernel.toExtendedReal

enum class Side {
    LEFT,
    RIGHT
}

/**
 * Primitivos de limite baseados em `ExtendedReal`.
 *
 * Este módulo não tenta resolver limites simbólicos gerais. Ele fornece regras
 * determinísticas para casos fundamentais usados em cálculo computacional.
 */
object Limits {
    fun quotient(numerator: ExtendedReal, denominator: ExtendedReal): ExtendedReal =
        numerator / denominator

    fun reciprocal(value: RealNumber): ExtendedReal =
        ExtendedReal.ONE / value.toExtendedReal()

    fun reciprocalAtZero(side: Side): ExtendedReal = when (side) {
        Side.LEFT -> ExtendedReal.NEGATIVE_INFINITY
        Side.RIGHT -> ExtendedReal.POSITIVE_INFINITY
    }

    fun twoSidedReciprocalAtZero(): ExtendedReal = ExtendedReal.INDETERMINATE

    fun differenceQuotient(
        f: (RealNumber) -> RealNumber,
        at: RealNumber,
        delta: RealNumber
    ): ExtendedReal {
        val numerator = f(at + delta) - f(at)
        return numerator.toExtendedReal() / delta.toExtendedReal()
    }
}

