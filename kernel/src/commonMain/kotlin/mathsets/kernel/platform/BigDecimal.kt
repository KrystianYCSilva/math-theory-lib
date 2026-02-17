package mathsets.kernel.platform

/**
 * Multiplatform type alias for arbitrary-precision decimal numbers.
 *
 * Delegates to `com.ionspin.kotlin.bignum.decimal.BigDecimal` from the
 * kotlin-multiplatform-bignum library, providing consistent decimal
 * arithmetic across JVM, JS, and Native targets.
 */
typealias BigDecimal = com.ionspin.kotlin.bignum.decimal.BigDecimal

/** The [BigDecimal] constant zero (0). */
val BD_ZERO: BigDecimal = BigDecimal.ZERO

/** The [BigDecimal] constant one (1). */
val BD_ONE: BigDecimal = BigDecimal.ONE

/** The [BigDecimal] constant two (2). */
val BD_TWO: BigDecimal = BigDecimal.TWO

/** The [BigDecimal] constant ten (10). */
val BD_TEN: BigDecimal = BigDecimal.TEN

/**
 * Parses this string as a [BigDecimal].
 *
 * @return The parsed [BigDecimal].
 * @throws NumberFormatException if the string is not a valid decimal.
 */
fun String.toBigDecimal(): BigDecimal = BigDecimal.parseString(this)

/**
 * Converts this [Int] to a [BigDecimal].
 *
 * @return The corresponding [BigDecimal].
 */
fun Int.toBigDecimal(): BigDecimal = BigDecimal.fromInt(this)

/**
 * Converts this [Long] to a [BigDecimal].
 *
 * @return The corresponding [BigDecimal].
 */
fun Long.toBigDecimal(): BigDecimal = BigDecimal.fromLong(this)

/**
 * Converts this [Float] to a [BigDecimal].
 *
 * @return The corresponding [BigDecimal].
 */
fun Float.toBigDecimal(): BigDecimal = BigDecimal.fromFloat(this)

/**
 * Converts this [Double] to a [BigDecimal].
 *
 * @return The corresponding [BigDecimal].
 */
fun Double.toBigDecimal(): BigDecimal = BigDecimal.fromDouble(this)

/**
 * Creates a [BigDecimal] by parsing a string.
 *
 * @param value The string representation of the decimal number.
 * @return The corresponding [BigDecimal].
 * @throws NumberFormatException if [value] is not a valid decimal.
 */
fun bigDecimalOf(value: String): BigDecimal = value.toBigDecimal()

/**
 * Creates a [BigDecimal] from an [Int] value.
 *
 * @param value The integer value.
 * @return The corresponding [BigDecimal].
 */
fun bigDecimalOf(value: Int): BigDecimal = value.toBigDecimal()

/**
 * Creates a [BigDecimal] from a [Long] value.
 *
 * @param value The long value.
 * @return The corresponding [BigDecimal].
 */
fun bigDecimalOf(value: Long): BigDecimal = value.toBigDecimal()

/**
 * Creates a [BigDecimal] from a [Float] value.
 *
 * @param value The float value.
 * @return The corresponding [BigDecimal].
 */
fun bigDecimalOf(value: Float): BigDecimal = value.toBigDecimal()

/**
 * Creates a [BigDecimal] from a [Double] value.
 *
 * @param value The double value.
 * @return The corresponding [BigDecimal].
 */
fun bigDecimalOf(value: Double): BigDecimal = value.toBigDecimal()
