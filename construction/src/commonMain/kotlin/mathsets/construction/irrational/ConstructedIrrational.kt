package mathsets.construction.irrational

import mathsets.construction.rational.ConstructedRational
import mathsets.construction.real.ConstructedReal
import mathsets.kernel.IrrationalNumber
import mathsets.kernel.MathElement
import mathsets.kernel.RationalNumber

/**
 * Describes how an irrational number was constructed.
 *
 * - [ALGEBRAIC_CONSTRUCTION]: built from an algebraic identity (e.g. square root via bisection).
 * - [AXIOMATIC_SYMBOL]: declared axiomatically with a known decimal expansion (e.g. pi, e).
 */
enum class IrrationalFoundation {
    /** The irrational was constructed algebraically (e.g. sqrt(2) via bisection). */
    ALGEBRAIC_CONSTRUCTION,

    /** The irrational was introduced axiomatically by symbol and decimal expansion. */
    AXIOMATIC_SYMBOL
}

/**
 * Axiomatic construction of an irrational number, equipped with:
 *
 * - A symbolic name (e.g. "sqrt(2)", "pi").
 * - A [foundation] indicating whether it was derived algebraically or axiomatically.
 * - A [ConstructedReal] representative (Cauchy sequence of rationals).
 * - A **lower Dedekind cut predicate**: for a rational `q`, returns `true` if `q` is
 *   strictly less than this irrational.
 * - An **irrationality witness**: for any rational `q`, returns `true` if `q` is provably
 *   not equal to this value (e.g. `q^2 != 2` for sqrt(2)).
 *
 * Arithmetic operations on irrationals produce [ConstructedReal] values, since the
 * sum/product of irrationals is not necessarily irrational.
 *
 * @property symbol the symbolic name of this irrational.
 * @property foundation the construction method (algebraic or axiomatic).
 * @see ConstructedReal
 * @see ConstructedIrrationalIsomorphism
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

    /**
     * Projects this constructed irrational to a kernel [IrrationalNumber].
     */
    fun toKernel(): IrrationalNumber = IrrationalNumber.of(symbol, realRepresentative.toKernel())

    /**
     * Returns the underlying [ConstructedReal] Cauchy-sequence representative.
     */
    fun toConstructedReal(): ConstructedReal = realRepresentative

    /**
     * Tests whether the rational [candidate] belongs to the lower Dedekind cut
     * of this irrational (i.e. whether `candidate < this`).
     *
     * @param candidate the rational to test.
     * @return `true` if [candidate] is in the lower cut.
     */
    fun lowerCutContains(candidate: ConstructedRational): Boolean = lowerCutPredicate(candidate)

    /**
     * Tests whether the irrationality witness refutes [candidate] as an exact
     * rational representation of this value.
     *
     * For algebraic irrationals this is a constructive proof (e.g. `q^2 != 2`);
     * for axiomatic irrationals this always returns `true`.
     *
     * @param candidate the rational to test.
     * @return `true` if [candidate] is provably not equal to this irrational.
     */
    fun refutesAsExactRational(candidate: ConstructedRational): Boolean = irrationalityWitness(candidate)

    /**
     * Adds two irrationals, returning a [ConstructedReal].
     *
     * @param other the addend.
     * @return the real-valued sum.
     */
    operator fun plus(other: ConstructedIrrational): ConstructedReal =
        realRepresentative + other.realRepresentative

    /**
     * Subtracts [other] from this irrational, returning a [ConstructedReal].
     *
     * @param other the subtrahend.
     * @return the real-valued difference.
     */
    operator fun minus(other: ConstructedIrrational): ConstructedReal =
        realRepresentative - other.realRepresentative

    /**
     * Multiplies two irrationals, returning a [ConstructedReal].
     *
     * @param other the multiplier.
     * @return the real-valued product.
     */
    operator fun times(other: ConstructedIrrational): ConstructedReal =
        realRepresentative * other.realRepresentative

    /**
     * Divides this irrational by [other], returning a [ConstructedReal].
     *
     * @param other the divisor.
     * @return the real-valued quotient.
     */
    operator fun div(other: ConstructedIrrational): ConstructedReal =
        realRepresentative / other.realRepresentative

    /**
     * Adds a [ConstructedReal] to this irrational.
     *
     * @param other the real addend.
     * @return the real-valued sum.
     */
    operator fun plus(other: ConstructedReal): ConstructedReal = realRepresentative + other

    /**
     * Subtracts a [ConstructedReal] from this irrational.
     *
     * @param other the real subtrahend.
     * @return the real-valued difference.
     */
    operator fun minus(other: ConstructedReal): ConstructedReal = realRepresentative - other

    /**
     * Multiplies this irrational by a [ConstructedReal].
     *
     * @param other the real multiplier.
     * @return the real-valued product.
     */
    operator fun times(other: ConstructedReal): ConstructedReal = realRepresentative * other

    /**
     * Divides this irrational by a [ConstructedReal].
     *
     * @param other the real divisor.
     * @return the real-valued quotient.
     */
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

        /** The square root of 2, constructed algebraically via bisection. */
        val SQRT2: ConstructedIrrational = algebraicSquareRoot("sqrt(2)", 2)

        /** The square root of 3, constructed algebraically via bisection. */
        val SQRT3: ConstructedIrrational = algebraicSquareRoot("sqrt(3)", 3)

        /** The golden ratio `(1 + sqrt(5)) / 2`, constructed algebraically. */
        val GOLDEN_RATIO: ConstructedIrrational = algebraicGoldenRatio()

        /** Pi, introduced axiomatically with a 30-digit decimal expansion. */
        val PI: ConstructedIrrational = axiomatic(
            symbol = "pi",
            decimalExpansion = "3.141592653589793238462643383279"
        )

        /** Euler's number e, introduced axiomatically with a 30-digit decimal expansion. */
        val E: ConstructedIrrational = axiomatic(
            symbol = "e",
            decimalExpansion = "2.718281828459045235360287471352"
        )

        /**
         * Creates a [ConstructedIrrational] from a symbol and a real approximation.
         *
         * @param symbol the symbolic name.
         * @param approximation the [ConstructedReal] representing this irrational.
         * @param foundation the construction method (defaults to [IrrationalFoundation.AXIOMATIC_SYMBOL]).
         * @return the constructed irrational.
         */
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

        /**
         * Creates a [ConstructedIrrational] from a kernel [IrrationalNumber].
         *
         * @param value the kernel irrational.
         * @return the corresponding constructed irrational.
         */
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

/**
 * Isomorphism between [ConstructedIrrational] and the kernel [IrrationalNumber],
 * with verification of known constants.
 */
object ConstructedIrrationalIsomorphism {
    /**
     * Projects a [ConstructedIrrational] to its kernel [IrrationalNumber].
     */
    fun toKernel(value: ConstructedIrrational): IrrationalNumber = value.toKernel()

    /**
     * Lifts a kernel [IrrationalNumber] to a [ConstructedIrrational].
     */
    fun fromKernel(value: IrrationalNumber): ConstructedIrrational =
        ConstructedIrrational.fromKernel(value)

    /**
     * Verifies the round-trip property for all pre-defined irrational constants
     * (sqrt(2), sqrt(3), golden ratio, pi, e).
     *
     * @return `true` if the round-trip holds for every constant.
     */
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

/**
 * Extension to convert a kernel [IrrationalNumber] to [ConstructedIrrational].
 */
fun IrrationalNumber.toMathIrrational(): ConstructedIrrational =
    ConstructedIrrational.fromKernel(this)
