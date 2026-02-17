package mathsets.kernel.analysis

import mathsets.kernel.ExtendedReal
import mathsets.kernel.RealNumber
import mathsets.kernel.toExtendedReal

/**
 * Operadores base para derivadas numéricas via quociente de diferenças.
 */
object Derivatives {
    fun forwardDifference(
        f: (RealNumber) -> RealNumber,
        at: RealNumber,
        h: RealNumber
    ): ExtendedReal = Limits.differenceQuotient(f, at, h)

    fun symmetricDifference(
        f: (RealNumber) -> RealNumber,
        at: RealNumber,
        h: RealNumber
    ): ExtendedReal {
        val numerator = f(at + h) - f(at - h)
        val denominator = h + h
        return numerator.toExtendedReal() / denominator.toExtendedReal()
    }
}

