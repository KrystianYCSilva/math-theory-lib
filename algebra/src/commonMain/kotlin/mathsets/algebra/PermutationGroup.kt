package mathsets.algebra

/**
 * Represents a permutation of elements `{0, 1, ..., n-1}` as an array mapping.
 *
 * A permutation sigma maps each index i to sigma(i). Composition is function
 * composition: `(sigma compose tau)(i) = sigma(tau(i))`.
 *
 * @property mapping The array where `mapping[i]` is the image of `i` under this permutation.
 */
data class Permutation(val mapping: IntArray) {
    val degree: Int get() = mapping.size

    operator fun invoke(i: Int): Int = mapping[i]

    fun compose(other: Permutation): Permutation {
        require(degree == other.degree) { "Permutations must have the same degree." }
        return Permutation(IntArray(degree) { mapping[other.mapping[it]] })
    }

    fun invert(): Permutation {
        val inv = IntArray(degree)
        for (i in mapping.indices) {
            inv[mapping[i]] = i
        }
        return Permutation(inv)
    }

    fun isIdentity(): Boolean = mapping.indices.all { mapping[it] == it }

    fun toCycleNotation(): List<List<Int>> {
        val visited = BooleanArray(degree)
        val cycles = mutableListOf<List<Int>>()
        for (i in 0 until degree) {
            if (visited[i]) continue
            val cycle = mutableListOf<Int>()
            var current = i
            while (!visited[current]) {
                visited[current] = true
                cycle.add(current)
                current = mapping[current]
            }
            if (cycle.size > 1) {
                cycles.add(cycle)
            }
        }
        return cycles
    }

    fun order(): Int {
        val cycles = toCycleNotation()
        if (cycles.isEmpty()) return 1
        return cycles.map { it.size }.reduce { acc, len -> lcm(acc, len) }
    }

    fun sign(): Int {
        val transpositionCount = toCycleNotation().sumOf { it.size - 1 }
        return if (transpositionCount % 2 == 0) 1 else -1
    }

    override fun equals(other: Any?): Boolean =
        other is Permutation && mapping.contentEquals(other.mapping)

    override fun hashCode(): Int = mapping.contentHashCode()

    override fun toString(): String {
        val cycles = toCycleNotation()
        if (cycles.isEmpty()) return "id"
        return cycles.joinToString("") { cycle ->
            "(${cycle.joinToString(" ")})"
        }
    }

    companion object {
        fun identity(degree: Int): Permutation =
            Permutation(IntArray(degree) { it })

        fun transposition(degree: Int, i: Int, j: Int): Permutation {
            require(i in 0 until degree && j in 0 until degree) { "Indices out of range." }
            val arr = IntArray(degree) { it }
            arr[i] = j
            arr[j] = i
            return Permutation(arr)
        }

        fun cycle(degree: Int, vararg elements: Int): Permutation {
            require(elements.all { it in 0 until degree }) { "Cycle elements out of range." }
            val arr = IntArray(degree) { it }
            for (k in elements.indices) {
                arr[elements[k]] = elements[(k + 1) % elements.size]
            }
            return Permutation(arr)
        }
    }
}

private fun gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
private fun lcm(a: Int, b: Int): Int = a / gcd(a, b) * b

/**
 * The symmetric group S_n: the group of all permutations of `{0, 1, ..., n-1}`.
 *
 * The group operation is function composition. The group has n! elements.
 *
 * @property degree The number of elements being permuted (n).
 * @throws IllegalArgumentException if [degree] is less than 1.
 */
class PermutationGroup(val degree: Int) : Group<Permutation> {
    init {
        require(degree >= 1) { "Degree must be at least 1." }
    }

    override val identity: Permutation = Permutation.identity(degree)

    override fun op(a: Permutation, b: Permutation): Permutation = a.compose(b)

    override fun inverse(a: Permutation): Permutation = a.invert()

    fun elements(): Set<Permutation> = generatePermutations(degree)

    fun order(): Long = factorial(degree)

    fun alternatingGroupElements(): Set<Permutation> =
        elements().filter { it.sign() == 1 }.toSet()

    private fun generatePermutations(n: Int): Set<Permutation> {
        val result = mutableSetOf<Permutation>()
        val arr = IntArray(n) { it }
        permute(arr, 0, result)
        return result
    }

    private fun permute(arr: IntArray, start: Int, result: MutableSet<Permutation>) {
        if (start == arr.size) {
            result.add(Permutation(arr.copyOf()))
            return
        }
        for (i in start until arr.size) {
            arr[start] = arr[i].also { arr[i] = arr[start] }
            permute(arr, start + 1, result)
            arr[start] = arr[i].also { arr[i] = arr[start] }
        }
    }

    private fun factorial(n: Int): Long {
        var result = 1L
        for (i in 2..n) result *= i
        return result
    }
}
