package mathsets.kernel.platform

typealias BigInteger = com.ionspin.kotlin.bignum.integer.BigInteger

val BI_ZERO: BigInteger = BigInteger.ZERO
val BI_ONE: BigInteger = BigInteger.ONE
val BI_TEN: BigInteger = BigInteger.TEN

fun String.toBigInteger(): BigInteger = BigInteger.parseString(this, 10)
fun Long.toBigInteger(): BigInteger = BigInteger.fromLong(this)
fun Int.toBigInteger(): BigInteger = BigInteger.fromInt(this)

fun BigInteger.toLong(): Long = this.longValue(false)
fun BigInteger.toInt(): Int = this.intValue(false)

fun bigIntegerOf(value: Int): BigInteger = value.toBigInteger()
fun bigIntegerOf(value: Long): BigInteger = value.toBigInteger()
fun bigIntegerOf(value: String): BigInteger = value.toBigInteger()
