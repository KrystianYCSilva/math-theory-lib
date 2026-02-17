package mathsets.kernel

/**
 * Abstração de operações aritméticas algébricas para uso polimórfico entre estruturas numéricas.
 */
interface AlgebraicArithmetic<N : MathElement> {
    val zero: N
    val one: N

    fun add(a: N, b: N): N
    fun subtract(a: N, b: N): N
    fun multiply(a: N, b: N): N
    fun divide(a: N, b: N): N
}

/**
 * Extensão de aritmética algébrica para domínios ordenados.
 */
interface Arithmetic<N : MathElement> : AlgebraicArithmetic<N> {
    fun compare(a: N, b: N): Int
}

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

object IntegerArithmetic : Arithmetic<IntegerNumber> {
    override val zero: IntegerNumber = IntegerNumber.ZERO
    override val one: IntegerNumber = IntegerNumber.ONE

    override fun add(a: IntegerNumber, b: IntegerNumber): IntegerNumber = a + b
    override fun subtract(a: IntegerNumber, b: IntegerNumber): IntegerNumber = a - b
    override fun multiply(a: IntegerNumber, b: IntegerNumber): IntegerNumber = a * b
    override fun divide(a: IntegerNumber, b: IntegerNumber): IntegerNumber = a / b
    override fun compare(a: IntegerNumber, b: IntegerNumber): Int = a.compareTo(b)
}

object RationalArithmetic : Arithmetic<RationalNumber> {
    override val zero: RationalNumber = RationalNumber.ZERO
    override val one: RationalNumber = RationalNumber.ONE

    override fun add(a: RationalNumber, b: RationalNumber): RationalNumber = a + b
    override fun subtract(a: RationalNumber, b: RationalNumber): RationalNumber = a - b
    override fun multiply(a: RationalNumber, b: RationalNumber): RationalNumber = a * b
    override fun divide(a: RationalNumber, b: RationalNumber): RationalNumber = a / b
    override fun compare(a: RationalNumber, b: RationalNumber): Int = a.compareTo(b)
}

object RealArithmetic : Arithmetic<RealNumber> {
    override val zero: RealNumber = RealNumber.ZERO
    override val one: RealNumber = RealNumber.ONE

    override fun add(a: RealNumber, b: RealNumber): RealNumber = a + b
    override fun subtract(a: RealNumber, b: RealNumber): RealNumber = a - b
    override fun multiply(a: RealNumber, b: RealNumber): RealNumber = a * b
    override fun divide(a: RealNumber, b: RealNumber): RealNumber = a / b
    override fun compare(a: RealNumber, b: RealNumber): Int = a.compareTo(b)
}

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
 * Complexos não possuem ordem total canônica; por isso expõem apenas aritmética algébrica.
 */
object ComplexArithmetic : AlgebraicArithmetic<ComplexNumber> {
    override val zero: ComplexNumber = ComplexNumber.ZERO
    override val one: ComplexNumber = ComplexNumber.ONE

    override fun add(a: ComplexNumber, b: ComplexNumber): ComplexNumber = a + b
    override fun subtract(a: ComplexNumber, b: ComplexNumber): ComplexNumber = a - b
    override fun multiply(a: ComplexNumber, b: ComplexNumber): ComplexNumber = a * b
    override fun divide(a: ComplexNumber, b: ComplexNumber): ComplexNumber = a / b
}
