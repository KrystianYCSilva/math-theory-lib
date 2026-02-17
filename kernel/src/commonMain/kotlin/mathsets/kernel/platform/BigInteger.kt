package mathsets.kernel.platform

/**
 * Multiplatform type alias for arbitrary-precision integers.
 *
 * Delegates to `com.ionspin.kotlin.bignum.integer.BigInteger` from the
 * kotlin-multiplatform-bignum library, providing consistent big integer
 * arithmetic across JVM, JS, and Native targets.
 */
typealias BigInteger = com.ionspin.kotlin.bignum.integer.BigInteger

/** The [BigInteger] constant zero (0). */
val BI_ZERO: BigInteger = BigInteger.ZERO

/** The [BigInteger] constant one (1). */
val BI_ONE: BigInteger = BigInteger.ONE

/** The [BigInteger] constant ten (10). */
val BI_TEN: BigInteger = BigInteger.TEN

/**
 * Parses this string as a [BigInteger] in base 10.
 *
 * @return The parsed [BigInteger].
 * @throws NumberFormatException if the string is not a valid integer.
 */
fun String.toBigInteger(): BigInteger = BigInteger.parseString(this, 10)

/**
 * Converts this [Long] to a [BigInteger].
 *
 * @return The corresponding [BigInteger].
 */
fun Long.toBigInteger(): BigInteger = BigInteger.fromLong(this)

/**
 * Converts this [Int] to a [BigInteger].
 *
 * @return The corresponding [BigInteger].
 */
fun Int.toBigInteger(): BigInteger = BigInteger.fromInt(this)

/**
 * Converts this [BigInteger] to a [Long].
 *
 * @return The [Long] value.
 * @throws ArithmeticException if the value overflows [Long] range.
 */
fun BigInteger.toLong(): Long = this.longValue(false)

/**
 * Converts this [BigInteger] to an [Int].
 *
 * @return The [Int] value.
 * @throws ArithmeticException if the value overflows [Int] range.
 */
fun BigInteger.toInt(): Int = this.intValue(false)

/**
 * Creates a [BigInteger] from an [Int] value.
 *
 * @param value The integer value.
 * @return The corresponding [BigInteger].
 */
fun bigIntegerOf(value: Int): BigInteger = value.toBigInteger()

/**
 * Creates a [BigInteger] from a [Long] value.
 *
 * @param value The long value.
 * @return The corresponding [BigInteger].
 */
fun bigIntegerOf(value: Long): BigInteger = value.toBigInteger()

/**
 * Creates a [BigInteger] by parsing a string in base 10.
 *
 * @param value The string representation of the integer.
 * @return The corresponding [BigInteger].
 * @throws NumberFormatException if [value] is not a valid integer.
 */
fun bigIntegerOf(value: String): BigInteger = value.toBigInteger()
