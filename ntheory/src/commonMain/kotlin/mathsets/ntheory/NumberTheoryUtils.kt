package mathsets.ntheory

internal fun normalize(value: Long, modulus: Long): Long {
    require(modulus > 0) { "modulus must be positive." }
    val residue = value % modulus
    return if (residue < 0) residue + modulus else residue
}

internal fun gcd(a: Long, b: Long): Long {
    var x = if (a < 0) -a else a
    var y = if (b < 0) -b else b
    while (y != 0L) {
        val temp = x % y
        x = y
        y = temp
    }
    return x
}

internal fun mulMod(a: Long, b: Long, modulus: Long): Long {
    require(modulus > 0) { "modulus must be positive." }

    var x = normalize(a, modulus).toULong()
    var y = normalize(b, modulus).toULong()
    val m = modulus.toULong()
    var result = 0uL

    while (y > 0uL) {
        if ((y and 1uL) == 1uL) {
            result = (result + x) % m
        }
        x = (x + x) % m
        y = y shr 1
    }

    return result.toLong()
}

internal fun powMod(base: Long, exponent: Long, modulus: Long): Long {
    require(exponent >= 0) { "exponent must be non-negative." }
    require(modulus > 0) { "modulus must be positive." }

    var result = 1L
    var current = normalize(base, modulus)
    var exp = exponent

    while (exp > 0L) {
        if ((exp and 1L) == 1L) {
            result = mulMod(result, current, modulus)
        }
        current = mulMod(current, current, modulus)
        exp = exp ushr 1
    }

    return result
}
