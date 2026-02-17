package mathsets.kernel.platform

actual class BigInteger(val value: Long) : Comparable<BigInteger> {
    actual override fun toString(): String = value.toString()
    
    actual override fun equals(other: Any?): Boolean {
        if (other !is BigInteger) return false
        return value == other.value
    }
    
    actual override fun hashCode(): Int = value.hashCode()

    override fun compareTo(other: BigInteger): Int = value.compareTo(other.value)
    
    actual fun toLong(): Long = value
    actual fun toInt(): Int = value.toInt()
}

actual operator fun BigInteger.plus(other: BigInteger): BigInteger = BigInteger(value + other.value)
actual operator fun BigInteger.minus(other: BigInteger): BigInteger = BigInteger(value - other.value)
actual operator fun BigInteger.times(other: BigInteger): BigInteger = BigInteger(value * other.value)
actual operator fun BigInteger.div(other: BigInteger): BigInteger = BigInteger(value / other.value)
actual operator fun BigInteger.rem(other: BigInteger): BigInteger = BigInteger(value % other.value)
actual operator fun BigInteger.unaryMinus(): BigInteger = BigInteger(-value)

actual fun BigInteger.abs(): BigInteger = BigInteger(if (value < 0) -value else value)
actual fun BigInteger.pow(exponent: Int): BigInteger {
    var res = 1L
    repeat(exponent) { res *= value }
    return BigInteger(res)
}

actual val BI_ZERO: BigInteger = BigInteger(0L)
actual val BI_ONE: BigInteger = BigInteger(1L)
actual val BI_TEN: BigInteger = BigInteger(10L)

actual fun String.toBigInteger(): BigInteger = BigInteger(this.toLong())
actual fun Long.toBigInteger(): BigInteger = BigInteger(this)
actual fun Int.toBigInteger(): BigInteger = BigInteger(this.toLong())
