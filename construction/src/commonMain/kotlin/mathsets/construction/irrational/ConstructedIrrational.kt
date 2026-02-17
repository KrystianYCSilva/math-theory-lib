package mathsets.construction.irrational

import mathsets.construction.real.ConstructedReal
import mathsets.construction.real.toMathReal
import mathsets.kernel.IrrationalNumber
import mathsets.kernel.MathElement

/**
 * Irracional construído com símbolo e aproximação real.
 */
class ConstructedIrrational private constructor(
    val symbol: String,
    val approximation: ConstructedReal,
    private val kernelValue: IrrationalNumber
) : Comparable<ConstructedIrrational>, MathElement {
    fun toKernel(): IrrationalNumber = kernelValue

    fun toConstructedReal(): ConstructedReal = approximation

    operator fun plus(other: ConstructedIrrational): ConstructedReal =
        approximation + other.approximation

    operator fun minus(other: ConstructedIrrational): ConstructedReal =
        approximation - other.approximation

    operator fun times(other: ConstructedIrrational): ConstructedReal =
        approximation * other.approximation

    operator fun div(other: ConstructedIrrational): ConstructedReal =
        approximation / other.approximation

    operator fun plus(other: ConstructedReal): ConstructedReal = approximation + other

    operator fun minus(other: ConstructedReal): ConstructedReal = approximation - other

    operator fun times(other: ConstructedReal): ConstructedReal = approximation * other

    operator fun div(other: ConstructedReal): ConstructedReal = approximation / other

    override fun compareTo(other: ConstructedIrrational): Int =
        approximation.compareTo(other.approximation)

    override fun equals(other: Any?): Boolean =
        other is ConstructedIrrational &&
            symbol == other.symbol &&
            approximation == other.approximation

    override fun hashCode(): Int = 31 * symbol.hashCode() + approximation.hashCode()

    override fun toString(): String = symbol

    companion object {
        val PI: ConstructedIrrational = fromKernel(IrrationalNumber.PI)
        val E: ConstructedIrrational = fromKernel(IrrationalNumber.E)
        val SQRT2: ConstructedIrrational = fromKernel(IrrationalNumber.SQRT2)
        val SQRT3: ConstructedIrrational = fromKernel(IrrationalNumber.SQRT3)
        val GOLDEN_RATIO: ConstructedIrrational = fromKernel(IrrationalNumber.GOLDEN_RATIO)

        fun of(symbol: String, approximation: ConstructedReal): ConstructedIrrational {
            val kernel = IrrationalNumber.of(symbol, approximation.toKernel())
            return ConstructedIrrational(kernel.symbol, approximation, kernel)
        }

        fun fromKernel(value: IrrationalNumber): ConstructedIrrational =
            ConstructedIrrational(
                symbol = value.symbol,
                approximation = value.approximation.toMathReal(),
                kernelValue = value
            )
    }
}

object ConstructedIrrationalIsomorphism {
    fun toKernel(value: ConstructedIrrational): IrrationalNumber = value.toKernel()

    fun fromKernel(value: IrrationalNumber): ConstructedIrrational =
        ConstructedIrrational.fromKernel(value)

    fun verifyKnownConstants(): Boolean {
        val constants = listOf(
            IrrationalNumber.PI,
            IrrationalNumber.E,
            IrrationalNumber.SQRT2,
            IrrationalNumber.SQRT3,
            IrrationalNumber.GOLDEN_RATIO
        )
        return constants.all { toKernel(fromKernel(it)) == it }
    }
}

fun IrrationalNumber.toMathIrrational(): ConstructedIrrational =
    ConstructedIrrational.fromKernel(this)

