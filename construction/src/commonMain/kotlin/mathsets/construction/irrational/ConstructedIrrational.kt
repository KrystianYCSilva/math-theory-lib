package mathsets.construction.irrational

import mathsets.construction.rational.ConstructedRational
import mathsets.construction.real.ConstructedReal
import mathsets.kernel.IrrationalNumber
import mathsets.kernel.MathElement
import mathsets.kernel.RationalNumber

enum class IrrationalFoundation {
    ALGEBRAIC_CONSTRUCTION,
    AXIOMATIC_SYMBOL
}

/**
 * Irracional construído com:
 * - símbolo
 * - representante real (sequência de Cauchy)
 * - predicado de corte inferior em Q
 * - testemunha de não-racionalidade (quando disponível)
 */
class ConstructedIrrational private constructor(
    val symbol: String,
    val foundation: IrrationalFoundation,
    private val realRepresentative: ConstructedReal,
    private val lowerCutPredicate: (ConstructedRational) -> Boolean,
    private val irrationalityWitness: (ConstructedRational) -> Boolean
) : Comparable<ConstructedIrrational>, MathElement {
    init {
        require(symbol.isNotBlank()) { "Irrational symbol cannot be blank." }
    }

    fun toKernel(): IrrationalNumber = IrrationalNumber.of(symbol, realRepresentative.toKernel())

    fun toConstructedReal(): ConstructedReal = realRepresentative

    fun lowerCutContains(candidate: ConstructedRational): Boolean = lowerCutPredicate(candidate)

    fun refutesAsExactRational(candidate: ConstructedRational): Boolean = irrationalityWitness(candidate)

    operator fun plus(other: ConstructedIrrational): ConstructedReal =
        realRepresentative + other.realRepresentative

    operator fun minus(other: ConstructedIrrational): ConstructedReal =
        realRepresentative - other.realRepresentative

    operator fun times(other: ConstructedIrrational): ConstructedReal =
        realRepresentative * other.realRepresentative

    operator fun div(other: ConstructedIrrational): ConstructedReal =
        realRepresentative / other.realRepresentative

    operator fun plus(other: ConstructedReal): ConstructedReal = realRepresentative + other

    operator fun minus(other: ConstructedReal): ConstructedReal = realRepresentative - other

    operator fun times(other: ConstructedReal): ConstructedReal = realRepresentative * other

    operator fun div(other: ConstructedReal): ConstructedReal = realRepresentative / other

    override fun compareTo(other: ConstructedIrrational): Int =
        realRepresentative.compareTo(other.realRepresentative)

    override fun equals(other: Any?): Boolean =
        other is ConstructedIrrational &&
            symbol == other.symbol &&
            realRepresentative == other.realRepresentative

    override fun hashCode(): Int = 31 * symbol.hashCode() + realRepresentative.hashCode()

    override fun toString(): String = symbol

    companion object {
        private val ONE_R: ConstructedRational = ConstructedRational.fromKernel(RationalNumber.of(1, 1))

        val SQRT2: ConstructedIrrational = algebraicSquareRoot("sqrt(2)", 2)
        val SQRT3: ConstructedIrrational = algebraicSquareRoot("sqrt(3)", 3)
        val GOLDEN_RATIO: ConstructedIrrational = algebraicGoldenRatio()

        val PI: ConstructedIrrational = axiomatic(
            symbol = "pi",
            decimalExpansion = "3.141592653589793238462643383279"
        )

        val E: ConstructedIrrational = axiomatic(
            symbol = "e",
            decimalExpansion = "2.718281828459045235360287471352"
        )

        fun of(
            symbol: String,
            approximation: ConstructedReal,
            foundation: IrrationalFoundation = IrrationalFoundation.AXIOMATIC_SYMBOL
        ): ConstructedIrrational = ConstructedIrrational(
            symbol = symbol.trim(),
            foundation = foundation,
            realRepresentative = approximation,
            lowerCutPredicate = { q -> q < approximation.approximateRational(28) },
            irrationalityWitness = { true }
        )

        fun fromKernel(value: IrrationalNumber): ConstructedIrrational = axiomatic(
            symbol = value.symbol,
            decimalExpansion = value.approximation.toString()
        )

        private fun algebraicSquareRoot(symbol: String, radicand: Int): ConstructedIrrational {
            val real = ConstructedReal.squareRootOf(radicand)
            val target = ConstructedRational.fromKernel(RationalNumber.of(radicand, 1))
            return ConstructedIrrational(
                symbol = symbol,
                foundation = IrrationalFoundation.ALGEBRAIC_CONSTRUCTION,
                realRepresentative = real,
                lowerCutPredicate = { q -> (q * q) < target },
                irrationalityWitness = { q -> (q * q) != target }
            )
        }

        private fun algebraicGoldenRatio(): ConstructedIrrational {
            val sqrt5 = ConstructedReal.squareRootOf(5)
            val real = (ConstructedReal.ONE + sqrt5) / ConstructedReal.TWO
            return ConstructedIrrational(
                symbol = "phi",
                foundation = IrrationalFoundation.ALGEBRAIC_CONSTRUCTION,
                realRepresentative = real,
                lowerCutPredicate = { q -> (q * q) < (q + ONE_R) },
                irrationalityWitness = { q -> (q * q) != (q + ONE_R) }
            )
        }

        private fun axiomatic(symbol: String, decimalExpansion: String): ConstructedIrrational {
            val real = ConstructedReal.fromDecimalExpansion(decimalExpansion)
            return ConstructedIrrational(
                symbol = symbol,
                foundation = IrrationalFoundation.AXIOMATIC_SYMBOL,
                realRepresentative = real,
                lowerCutPredicate = { q -> q < real.approximateRational(28) },
                irrationalityWitness = { true }
            )
        }
    }
}

object ConstructedIrrationalIsomorphism {
    fun toKernel(value: ConstructedIrrational): IrrationalNumber = value.toKernel()

    fun fromKernel(value: IrrationalNumber): ConstructedIrrational =
        ConstructedIrrational.fromKernel(value)

    fun verifyKnownConstants(): Boolean {
        val constants = listOf(
            ConstructedIrrational.SQRT2,
            ConstructedIrrational.SQRT3,
            ConstructedIrrational.GOLDEN_RATIO,
            ConstructedIrrational.PI,
            ConstructedIrrational.E
        )
        return constants.all { constant -> toKernel(fromKernel(constant.toKernel())) == constant.toKernel() }
    }
}

fun IrrationalNumber.toMathIrrational(): ConstructedIrrational =
    ConstructedIrrational.fromKernel(this)
