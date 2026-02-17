package mathsets.kernel

/**
 * Abstraction for algebraic arithmetic operations over a [MathElement] type.
 *
 * Provides the four fundamental field operations (add, subtract, multiply, divide)
 * along with identity elements ([zero] and [one]). This interface supports
 * polymorphic arithmetic across different number types without requiring a
 * total order.
 *
 * @param N The number type, which must implement [MathElement].
 * @see Arithmetic
 */
interface AlgebraicArithmetic<N : MathElement> {
    /** The additive identity element (0). */
    val zero: N

    /** The multiplicative identity element (1). */
    val one: N

    /**
     * Adds two elements.
     *
     * @param a The first operand.
     * @param b The second operand.
     * @return The sum a + b.
     */
    fun add(a: N, b: N): N

    /**
     * Subtracts one element from another.
     *
     * @param a The minuend.
     * @param b The subtrahend.
     * @return The difference a - b.
     */
    fun subtract(a: N, b: N): N

    /**
     * Multiplies two elements.
     *
     * @param a The first factor.
     * @param b The second factor.
     * @return The product a * b.
     */
    fun multiply(a: N, b: N): N

    /**
     * Divides one element by another.
     *
     * @param a The dividend.
     * @param b The divisor.
     * @return The quotient a / b.
     * @throws ArithmeticException if division is undefined (e.g., division by zero).
     */
    fun divide(a: N, b: N): N
}

/**
 * Extension of [AlgebraicArithmetic] for ordered domains that support comparison.
 *
 * @param N The number type, which must implement [MathElement].
 * @see AlgebraicArithmetic
 */
interface Arithmetic<N : MathElement> : AlgebraicArithmetic<N> {
    /**
     * Compares two elements for ordering.
     *
     * @param a The first element.
     * @param b The second element.
     * @return A negative integer, zero, or positive integer as a is less than, equal to,
     *         or greater than b.
     */
    fun compare(a: N, b: N): Int
}

/**
 * Arithmetic operations for [NaturalNumber] (the natural numbers).
 *
 * Note: subtraction is a partial operation in the naturals and will throw
 * if the result would be negative.
 *
 * @see NaturalNumber
 */
object NaturalArithmetic : Arithmetic<NaturalNumber> {
    override val zero: NaturalNumber = NaturalNumber.ZERO
    override val one: NaturalNumber = NaturalNumber.ONE

    override fun add(a: NaturalNumber, b: NaturalNumber): NaturalNumber = a + b

    override fun subtract(a: NaturalNumber, b: NaturalNumber): NaturalNumber {
        return a - b
    }

    override fun multiply(a: NaturalNumber, b: NaturalNumber): NaturalNumber = a * b

    override fun divide(a: NaturalNumber, b: NaturalNumber): NaturalNumber = a / b

    override fun compare(a: NaturalNumber, b: NaturalNumber): Int = a.compareTo(b)
}

/**
 * Arithmetic operations for [IntegerNumber] (the integers).
 *
 * @see IntegerNumber
 */
object IntegerArithmetic : Arithmetic<IntegerNumber> {
    override val zero: IntegerNumber = IntegerNumber.ZERO
    override val one: IntegerNumber = IntegerNumber.ONE

    override fun add(a: IntegerNumber, b: IntegerNumber): IntegerNumber = a + b
    override fun subtract(a: IntegerNumber, b: IntegerNumber): IntegerNumber = a - b
    override fun multiply(a: IntegerNumber, b: IntegerNumber): IntegerNumber = a * b
    override fun divide(a: IntegerNumber, b: IntegerNumber): IntegerNumber = a / b
    override fun compare(a: IntegerNumber, b: IntegerNumber): Int = a.compareTo(b)
}

/**
 * Arithmetic operations for [RationalNumber] (the rationals).
 *
 * @see RationalNumber
 */
object RationalArithmetic : Arithmetic<RationalNumber> {
    override val zero: RationalNumber = RationalNumber.ZERO
    override val one: RationalNumber = RationalNumber.ONE

    override fun add(a: RationalNumber, b: RationalNumber): RationalNumber = a + b
    override fun subtract(a: RationalNumber, b: RationalNumber): RationalNumber = a - b
    override fun multiply(a: RationalNumber, b: RationalNumber): RationalNumber = a * b
    override fun divide(a: RationalNumber, b: RationalNumber): RationalNumber = a / b
    override fun compare(a: RationalNumber, b: RationalNumber): Int = a.compareTo(b)
}

/**
 * Arithmetic operations for [RealNumber] (computational reals).
 *
 * @see RealNumber
 */
object RealArithmetic : Arithmetic<RealNumber> {
    override val zero: RealNumber = RealNumber.ZERO
    override val one: RealNumber = RealNumber.ONE

    override fun add(a: RealNumber, b: RealNumber): RealNumber = a + b
    override fun subtract(a: RealNumber, b: RealNumber): RealNumber = a - b
    override fun multiply(a: RealNumber, b: RealNumber): RealNumber = a * b
    override fun divide(a: RealNumber, b: RealNumber): RealNumber = a / b
    override fun compare(a: RealNumber, b: RealNumber): Int = a.compareTo(b)
}

/**
 * Arithmetic operations for [ExtendedReal] (the extended real line with infinities).
 *
 * @see ExtendedReal
 */
object ExtendedRealArithmetic : Arithmetic<ExtendedReal> {
    override val zero: ExtendedReal = ExtendedReal.ZERO
    override val one: ExtendedReal = ExtendedReal.ONE

    override fun add(a: ExtendedReal, b: ExtendedReal): ExtendedReal = a + b
    override fun subtract(a: ExtendedReal, b: ExtendedReal): ExtendedReal = a - b
    override fun multiply(a: ExtendedReal, b: ExtendedReal): ExtendedReal = a * b
    override fun divide(a: ExtendedReal, b: ExtendedReal): ExtendedReal = a / b
    override fun compare(a: ExtendedReal, b: ExtendedReal): Int = a.compareTo(b)
}

/**
 * Algebraic arithmetic for [ComplexNumber] (the complex numbers).
 *
 * Complex numbers do not have a canonical total order, so this implements
 * [AlgebraicArithmetic] rather than [Arithmetic].
 *
 * @see ComplexNumber
 */
object ComplexArithmetic : AlgebraicArithmetic<ComplexNumber> {
    override val zero: ComplexNumber = ComplexNumber.ZERO
    override val one: ComplexNumber = ComplexNumber.ONE

    override fun add(a: ComplexNumber, b: ComplexNumber): ComplexNumber = a + b
    override fun subtract(a: ComplexNumber, b: ComplexNumber): ComplexNumber = a - b
    override fun multiply(a: ComplexNumber, b: ComplexNumber): ComplexNumber = a * b
    override fun divide(a: ComplexNumber, b: ComplexNumber): ComplexNumber = a / b
}
