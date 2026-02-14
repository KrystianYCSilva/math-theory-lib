package mathsets.kernel.platform

import java.math.BigInteger as JBigInteger

actual typealias BigInteger = JBigInteger

// Implementing expect extensions using Kotlin stdlib extensions for BigInteger or Java methods
actual operator fun BigInteger.plus(other: BigInteger): BigInteger = this.add(other)
actual operator fun BigInteger.minus(other: BigInteger): BigInteger = this.subtract(other)
actual operator fun BigInteger.times(other: BigInteger): BigInteger = this.multiply(other)
actual operator fun BigInteger.div(other: BigInteger): BigInteger = this.divide(other)
actual operator fun BigInteger.rem(other: BigInteger): BigInteger = this.remainder(other)
actual operator fun BigInteger.unaryMinus(): BigInteger = this.negate()

actual fun BigInteger.abs(): BigInteger = this.abs()
actual fun BigInteger.pow(exponent: Int): BigInteger = this.pow(exponent)

actual val BI_ZERO: BigInteger = JBigInteger.ZERO
actual val BI_ONE: BigInteger = JBigInteger.ONE
actual val BI_TEN: BigInteger = JBigInteger.TEN

actual fun String.toBigInteger(): BigInteger = JBigInteger(this)
actual fun Long.toBigInteger(): BigInteger = JBigInteger.valueOf(this)
actual fun Int.toBigInteger(): BigInteger = JBigInteger.valueOf(this.toLong())
