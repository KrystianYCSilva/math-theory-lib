package mathsets.kernel

import kotlin.ConsistentCopyVisibility

/**
 * Representação simbólica de irracionais com aproximação decimal no kernel.
 *
 * Esta estrutura preserva o símbolo (ex.: pi, e, sqrt(2)) e uma aproximação
 * para uso computacional.
 */
@ConsistentCopyVisibility
data class IrrationalNumber private constructor(
    val symbol: String,
    val approximation: RealNumber
) : Comparable<IrrationalNumber>, MathElement {
    init {
        require(symbol.isNotBlank()) { "Irrational symbol cannot be blank." }
    }

    operator fun plus(other: IrrationalNumber): RealNumber = approximation + other.approximation
    operator fun minus(other: IrrationalNumber): RealNumber = approximation - other.approximation
    operator fun times(other: IrrationalNumber): RealNumber = approximation * other.approximation
    operator fun div(other: IrrationalNumber): RealNumber = approximation / other.approximation

    operator fun plus(other: RealNumber): RealNumber = approximation + other
    operator fun minus(other: RealNumber): RealNumber = approximation - other
    operator fun times(other: RealNumber): RealNumber = approximation * other
    operator fun div(other: RealNumber): RealNumber = approximation / other

    override fun compareTo(other: IrrationalNumber): Int =
        approximation.compareTo(other.approximation)

    fun toReal(): RealNumber = approximation

    override fun toString(): String = symbol

    companion object {
        val PI: IrrationalNumber = IrrationalNumber(
            symbol = "pi",
            approximation = RealNumber.parse("3.141592653589793238462643383279")
        )

        val E: IrrationalNumber = IrrationalNumber(
            symbol = "e",
            approximation = RealNumber.parse("2.718281828459045235360287471352")
        )

        val SQRT2: IrrationalNumber = IrrationalNumber(
            symbol = "sqrt(2)",
            approximation = RealNumber.parse("1.414213562373095048801688724209")
        )

        val SQRT3: IrrationalNumber = IrrationalNumber(
            symbol = "sqrt(3)",
            approximation = RealNumber.parse("1.732050807568877293527446341506")
        )

        val GOLDEN_RATIO: IrrationalNumber = IrrationalNumber(
            symbol = "phi",
            approximation = RealNumber.parse("1.618033988749894848204586834366")
        )

        fun of(symbol: String, approximation: RealNumber): IrrationalNumber =
            IrrationalNumber(symbol.trim(), approximation)
    }
}
