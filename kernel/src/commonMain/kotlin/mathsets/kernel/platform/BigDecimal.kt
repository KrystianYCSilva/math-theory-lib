package mathsets.kernel.platform

typealias BigDecimal = com.ionspin.kotlin.bignum.decimal.BigDecimal

val BD_ZERO: BigDecimal = BigDecimal.ZERO
val BD_ONE: BigDecimal = BigDecimal.ONE
val BD_TWO: BigDecimal = BigDecimal.TWO
val BD_TEN: BigDecimal = BigDecimal.TEN

fun String.toBigDecimal(): BigDecimal = BigDecimal.parseString(this)
fun Int.toBigDecimal(): BigDecimal = BigDecimal.fromInt(this)
fun Long.toBigDecimal(): BigDecimal = BigDecimal.fromLong(this)
fun Float.toBigDecimal(): BigDecimal = BigDecimal.fromFloat(this)
fun Double.toBigDecimal(): BigDecimal = BigDecimal.fromDouble(this)

fun bigDecimalOf(value: String): BigDecimal = value.toBigDecimal()
fun bigDecimalOf(value: Int): BigDecimal = value.toBigDecimal()
fun bigDecimalOf(value: Long): BigDecimal = value.toBigDecimal()
fun bigDecimalOf(value: Float): BigDecimal = value.toBigDecimal()
fun bigDecimalOf(value: Double): BigDecimal = value.toBigDecimal()

