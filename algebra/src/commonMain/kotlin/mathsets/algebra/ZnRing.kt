package mathsets.algebra

/**
 * The ring Z/nZ of integers modulo n.
 *
 * Elements are represented as [Int] values in `{0, 1, ..., n-1}`.
 * This is a commutative ring for all n >= 1, and a field when n is prime.
 *
 * @property modulus The modulus n (must be at least 1).
 * @throws IllegalArgumentException if [modulus] is less than 1.
 */
class ZnRing(val modulus: Int) : CommutativeRing<Int> {
    init {
        require(modulus >= 1) { "Modulus must be at least 1." }
    }

    override val zero: Int = 0
    override val one: Int = if (modulus == 1) 0 else 1

    override fun add(a: Int, b: Int): Int = ((a % modulus + b % modulus) % modulus + modulus) % modulus
    override fun negate(a: Int): Int = if (a == 0) 0 else modulus - (a.mod(modulus))
    override fun multiply(a: Int, b: Int): Int = ((a.toLong() % modulus * (b.toLong() % modulus)) % modulus + modulus).toInt() % modulus

    fun elements(): Set<Int> = (0 until modulus).toSet()

    fun isField(): Boolean = isPrime(modulus)

    fun inversesExist(): Map<Int, Int?> =
        (0 until modulus).associateWith { a ->
            if (a == 0) null
            else (1 until modulus).firstOrNull { b -> multiply(a, b) == one }
        }

    private fun isPrime(n: Int): Boolean {
        if (n < 2) return false
        if (n < 4) return true
        if (n % 2 == 0 || n % 3 == 0) return false
        var i = 5
        while (i * i <= n) {
            if (n % i == 0 || n % (i + 2) == 0) return false
            i += 6
        }
        return true
    }
}

/**
 * The field Z/pZ of integers modulo a prime p.
 *
 * Every non-zero element has a multiplicative inverse, computed via
 * the extended Euclidean algorithm.
 *
 * @property prime The prime modulus p (must be prime and at least 2).
 * @throws IllegalArgumentException if [prime] is not prime.
 */
class ZpField(val prime: Int) : Field<Int> {
    init {
        require(isPrime(prime)) { "$prime is not prime." }
    }

    override val zero: Int = 0
    override val one: Int = 1

    override fun add(a: Int, b: Int): Int = ((a % prime + b % prime) % prime + prime) % prime
    override fun negate(a: Int): Int = if (a == 0) 0 else prime - (a.mod(prime))
    override fun multiply(a: Int, b: Int): Int = ((a.toLong() % prime * (b.toLong() % prime)) % prime + prime).toInt() % prime

    override fun reciprocal(a: Int): Int {
        require(a.mod(prime) != 0) { "Cannot invert zero." }
        return modPow(a.mod(prime), prime - 2, prime)
    }

    fun elements(): Set<Int> = (0 until prime).toSet()

    private fun modPow(base: Int, exp: Int, mod: Int): Int {
        var result = 1L
        var b = base.toLong() % mod
        var e = exp
        while (e > 0) {
            if (e % 2 == 1) result = result * b % mod
            b = b * b % mod
            e /= 2
        }
        return result.toInt()
    }

    private fun isPrime(n: Int): Boolean {
        if (n < 2) return false
        if (n < 4) return true
        if (n % 2 == 0 || n % 3 == 0) return false
        var i = 5
        while (i * i <= n) {
            if (n % i == 0 || n % (i + 2) == 0) return false
            i += 6
        }
        return true
    }
}
