package mathsets.kernel.platform

expect class BigInteger : Comparable<BigInteger> {
    // No member operators if mapped to Java class without them
    override fun toString(): String
    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
    
    fun toLong(): Long
    fun toInt(): Int
}

// Operators as extension functions
expect operator fun BigInteger.plus(other: BigInteger): BigInteger
expect operator fun BigInteger.minus(other: BigInteger): BigInteger
expect operator fun BigInteger.times(other: BigInteger): BigInteger
expect operator fun BigInteger.div(other: BigInteger): BigInteger
expect operator fun BigInteger.rem(other: BigInteger): BigInteger
expect operator fun BigInteger.unaryMinus(): BigInteger

// Methods
expect fun BigInteger.abs(): BigInteger
expect fun BigInteger.pow(exponent: Int): BigInteger

// Constants
expect val BI_ZERO: BigInteger
expect val BI_ONE: BigInteger
expect val BI_TEN: BigInteger

// Factories
expect fun String.toBigInteger(): BigInteger
expect fun Long.toBigInteger(): BigInteger
expect fun Int.toBigInteger(): BigInteger

fun bigIntegerOf(value: Int): BigInteger = value.toBigInteger()
fun bigIntegerOf(value: Long): BigInteger = value.toBigInteger()
fun bigIntegerOf(value: String): BigInteger = value.toBigInteger()
