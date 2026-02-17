package mathsets.kernel

/**
 * Abstração de operações aritméticas para uso polimórfico entre estruturas numéricas.
 */
interface Arithmetic<N : MathElement> {
    val zero: N
    val one: N

    fun add(a: N, b: N): N
    fun subtract(a: N, b: N): N
    fun multiply(a: N, b: N): N
    fun divide(a: N, b: N): N
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
