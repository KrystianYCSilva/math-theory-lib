package mathsets.kernel.platform

external fun BigInt(value: Any): dynamic

actual class BigInteger(val value: dynamic) : Comparable<BigInteger> {
    override fun toString(): String = value.toString()
    
    override fun equals(other: Any?): Boolean {
        if (other !is BigInteger) return false
        return value == other.value
    }
    
    override fun hashCode(): Int = value.toString().hashCode()

    override fun compareTo(other: BigInteger): Int {
        return if (value < other.value) -1 else if (value > other.value) 1 else 0
    }
    
    actual fun toLong(): Long = value.toString().toLong()
    actual fun toInt(): Int = value.toString().toInt()
}

actual operator fun BigInteger.plus(other: BigInteger): BigInteger = BigInteger(value + other.value)
actual operator fun BigInteger.minus(other: BigInteger): BigInteger = BigInteger(value - other.value)
actual operator fun BigInteger.times(other: BigInteger): BigInteger = BigInteger(value * other.value)
actual operator fun BigInteger.div(other: BigInteger): BigInteger = BigInteger(value / other.value)
actual operator fun BigInteger.rem(other: BigInteger): BigInteger = BigInteger(value % other.value)
actual operator fun BigInteger.unaryMinus(): BigInteger = BigInteger(-value)

actual fun BigInteger.abs(): BigInteger = if (this < BI_ZERO) -this else this
actual fun BigInteger.pow(exponent: Int): BigInteger {
    var res = BI_ONE
    var base = this
    var exp = exponent
    while (exp > 0) {
        if (exp % 2 == 1) res = res * base
        base = base * base
        exp /= 2
    }
    return res
}

actual val BI_ZERO: BigInteger = BigInteger(BigInt(0))
actual val BI_ONE: BigInteger = BigInteger(BigInt(1))
actual val BI_TEN: BigInteger = BigInteger(BigInt(10))

actual fun String.toBigInteger(): BigInteger = BigInteger(BigInt(this))
actual fun Long.toBigInteger(): BigInteger = BigInteger(BigInt(this.toString()))
actual fun Int.toBigInteger(): BigInteger = BigInteger(BigInt(this))
