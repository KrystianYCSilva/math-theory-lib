package mathsets.kernel

import kotlin.ConsistentCopyVisibility

/**
 * Symbolic representation of irrational numbers with a decimal approximation in the kernel.
 *
 * This structure preserves the symbolic name (e.g., "pi", "e", "sqrt(2)") alongside
 * a [RealNumber] approximation for computational use. Arithmetic operations on
 * irrational numbers produce [RealNumber] results based on their approximations.
 *
 * Well-known constants such as [PI], [E], [SQRT2], [SQRT3], and [GOLDEN_RATIO]
 * are provided as pre-defined instances.
 *
 * @property symbol The symbolic identifier for this irrational constant (e.g., "pi").
 * @property approximation The decimal approximation as a [RealNumber].
 * @see RealNumber
 * @see MathElement
 */
@ConsistentCopyVisibility
data class IrrationalNumber private constructor(
    val symbol: String,
    val approximation: RealNumber
) : Comparable<IrrationalNumber>, MathElement {
    init {
        require(symbol.isNotBlank()) { "Irrational symbol cannot be blank." }
    }

    /**
     * Adds two irrational numbers using their approximations.
     *
     * @param other The irrational number to add.
     * @return The approximate sum as a [RealNumber].
     */
    operator fun plus(other: IrrationalNumber): RealNumber = approximation + other.approximation

    /**
     * Subtracts another irrational number using approximations.
     *
     * @param other The irrational number to subtract.
     * @return The approximate difference as a [RealNumber].
     */
    operator fun minus(other: IrrationalNumber): RealNumber = approximation - other.approximation

    /**
     * Multiplies two irrational numbers using their approximations.
     *
     * @param other The irrational number to multiply by.
     * @return The approximate product as a [RealNumber].
     */
    operator fun times(other: IrrationalNumber): RealNumber = approximation * other.approximation

    /**
     * Divides this irrational number by another using approximations.
     *
     * @param other The irrational divisor.
     * @return The approximate quotient as a [RealNumber].
     * @throws IllegalArgumentException if [other]'s approximation is zero.
     */
    operator fun div(other: IrrationalNumber): RealNumber = approximation / other.approximation

    /**
     * Adds a real number to this irrational number's approximation.
     *
     * @param other The real number to add.
     * @return The approximate sum as a [RealNumber].
     */
    operator fun plus(other: RealNumber): RealNumber = approximation + other

    /**
     * Subtracts a real number from this irrational number's approximation.
     *
     * @param other The real number to subtract.
     * @return The approximate difference as a [RealNumber].
     */
    operator fun minus(other: RealNumber): RealNumber = approximation - other

    /**
     * Multiplies this irrational number's approximation by a real number.
     *
     * @param other The real scalar.
     * @return The approximate product as a [RealNumber].
     */
    operator fun times(other: RealNumber): RealNumber = approximation * other

    /**
     * Divides this irrational number's approximation by a real number.
     *
     * @param other The real divisor.
     * @return The approximate quotient as a [RealNumber].
     * @throws IllegalArgumentException if [other] is zero.
     */
    operator fun div(other: RealNumber): RealNumber = approximation / other

    override fun compareTo(other: IrrationalNumber): Int =
        approximation.compareTo(other.approximation)

    /**
     * Converts this irrational number to its [RealNumber] approximation.
     *
     * @return The decimal approximation as a [RealNumber].
     */
    fun toReal(): RealNumber = approximation

    override fun toString(): String = symbol

    companion object {
        /** Archimedes' constant pi (approximately 3.14159265...). */
        val PI: IrrationalNumber = IrrationalNumber(
            symbol = "pi",
            approximation = RealNumber.parse("3.141592653589793238462643383279")
        )

        /** Euler's number e (approximately 2.71828182...). */
        val E: IrrationalNumber = IrrationalNumber(
            symbol = "e",
            approximation = RealNumber.parse("2.718281828459045235360287471352")
        )

        /** The square root of 2 (approximately 1.41421356...). */
        val SQRT2: IrrationalNumber = IrrationalNumber(
            symbol = "sqrt(2)",
            approximation = RealNumber.parse("1.414213562373095048801688724209")
        )

        /** The square root of 3 (approximately 1.73205080...). */
        val SQRT3: IrrationalNumber = IrrationalNumber(
            symbol = "sqrt(3)",
            approximation = RealNumber.parse("1.732050807568877293527446341506")
        )

        /** The golden ratio phi = (1 + sqrt(5)) / 2 (approximately 1.61803398...). */
        val GOLDEN_RATIO: IrrationalNumber = IrrationalNumber(
            symbol = "phi",
            approximation = RealNumber.parse("1.618033988749894848204586834366")
        )

        /**
         * Creates a custom [IrrationalNumber] with the given symbol and approximation.
         *
         * @param symbol A non-blank symbolic name for the constant.
         * @param approximation The decimal approximation as a [RealNumber].
         * @return A new [IrrationalNumber] instance.
         * @throws IllegalArgumentException if [symbol] is blank.
         */
        fun of(symbol: String, approximation: RealNumber): IrrationalNumber =
            IrrationalNumber(symbol.trim(), approximation)
    }
}
