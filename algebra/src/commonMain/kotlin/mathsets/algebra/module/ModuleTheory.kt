package mathsets.algebra.module

import mathsets.algebra.AdditiveAbelianGroup
import mathsets.algebra.Field
import mathsets.algebra.Ring

/**
 * An R-module: an additive abelian group M with scalar multiplication by R.
 *
 * @param R The scalar type.
 * @param M The module element type.
 */
interface Module<R, M> {
    /**
     * The scalar ring R.
     */
    val scalarRing: Ring<R>

    /**
     * The additive abelian group structure on M.
     */
    val additiveGroup: AdditiveAbelianGroup<M>

    /**
     * Scalar action R x M -> M.
     *
     * @param scalar The scalar in R.
     * @param value The module element in M.
     * @return The scaled element.
     */
    fun scalarMultiply(scalar: R, value: M): M

    /**
     * Adds two module elements.
     *
     * @param a First element.
     * @param b Second element.
     * @return a + b.
     */
    fun add(a: M, b: M): M = additiveGroup.add(a, b)

    /**
     * Negates a module element.
     *
     * @param a The element.
     * @return -a.
     */
    fun negate(a: M): M = additiveGroup.negate(a)

    /**
     * The zero element of M.
     */
    val zero: M get() = additiveGroup.zero
}

/**
 * A vector space over a field K.
 *
 * @param K The scalar field type.
 * @param V The vector type.
 */
interface VectorSpace<K, V> : Module<K, V> {
    /**
     * The scalar field K.
     */
    override val scalarRing: Field<K>
}

/**
 * A K-algebra: a vector space over K with an internal multiplication.
 *
 * @param K The scalar field type.
 * @param A The algebra element type.
 */
interface Algebra<K, A> : VectorSpace<K, A> {
    /**
     * Internal multiplication in the algebra.
     *
     * @param a First element.
     * @param b Second element.
     * @return a * b in the algebra.
     */
    fun multiply(a: A, b: A): A
}

/**
 * A finite-dimensional vector represented by its coordinates.
 *
 * @param K The scalar type.
 * @property coordinates Coordinates in a fixed basis order.
 */
data class Vector<K>(val coordinates: List<K>)

/**
 * A basis of a finite-dimensional vector space.
 *
 * @param K The scalar type.
 * @property space The ambient vector space.
 * @property vectors Basis vectors.
 * @throws IllegalArgumentException if vectors are not linearly independent or
 * do not have the space dimension.
 */
class Basis<K>(
    val space: FiniteDimensionalVectorSpace<K>,
    val vectors: List<Vector<K>>
) {
    /**
     * The basis dimension.
     */
    val dimension: Int = vectors.size

    init {
        require(vectors.size == space.dimension) {
            "A basis must contain exactly ${space.dimension} vectors."
        }
        require(vectors.all { it.coordinates.size == space.dimension }) {
            "All basis vectors must have dimension ${space.dimension}."
        }
        require(space.rank(vectors) == vectors.size) {
            "Basis vectors must be linearly independent."
        }
    }
}

/**
 * Finite-dimensional vector space K^n with coordinate-wise operations.
 *
 * @param K The scalar type.
 * @property scalarRing The scalar field K.
 * @property dimension The finite dimension n.
 */
class FiniteDimensionalVectorSpace<K>(
    override val scalarRing: Field<K>,
    val dimension: Int
) : VectorSpace<K, Vector<K>> {

    init {
        require(dimension >= 0) { "Dimension must be non-negative." }
    }

    override val additiveGroup: AdditiveAbelianGroup<Vector<K>> = object : AdditiveAbelianGroup<Vector<K>> {
        override val identity: Vector<K> get() = zeroVector()
        override val zero: Vector<K> get() = zeroVector()

        override fun add(a: Vector<K>, b: Vector<K>): Vector<K> {
            requireDimension(a)
            requireDimension(b)
            return Vector(List(dimension) { i -> scalarRing.add(a.coordinates[i], b.coordinates[i]) })
        }

        override fun negate(a: Vector<K>): Vector<K> {
            requireDimension(a)
            return Vector(a.coordinates.map { scalarRing.negate(it) })
        }

        override fun subtract(a: Vector<K>, b: Vector<K>): Vector<K> = add(a, negate(b))

        override fun op(a: Vector<K>, b: Vector<K>): Vector<K> = add(a, b)

        override fun inverse(a: Vector<K>): Vector<K> = negate(a)
    }

    override fun scalarMultiply(scalar: K, value: Vector<K>): Vector<K> {
        requireDimension(value)
        return Vector(value.coordinates.map { scalarRing.multiply(scalar, it) })
    }

    /**
     * Creates a vector in this space.
     *
     * @param coordinates Coordinates of size [dimension].
     * @return A vector in this space.
     * @throws IllegalArgumentException if coordinate size is invalid.
     */
    fun vector(coordinates: List<K>): Vector<K> {
        require(coordinates.size == dimension) {
            "Expected $dimension coordinates, got ${coordinates.size}."
        }
        return Vector(coordinates)
    }

    /**
     * The zero vector in this space.
     *
     * @return The zero vector.
     */
    fun zeroVector(): Vector<K> = Vector(List(dimension) { scalarRing.zero })

    /**
     * Returns the i-th canonical basis vector.
     *
     * @param index Basis index in [0, dimension).
     * @return e_index.
     * @throws IllegalArgumentException if index is out of bounds.
     */
    fun basisVector(index: Int): Vector<K> {
        require(index in 0 until dimension) {
            "Basis index $index is out of bounds for dimension $dimension."
        }
        val coordinates = MutableList(dimension) { scalarRing.zero }
        coordinates[index] = scalarRing.one
        return Vector(coordinates)
    }

    /**
     * Returns the canonical basis {e_1, ..., e_n}.
     *
     * @return The canonical basis for this space.
     */
    fun canonicalBasis(): Basis<K> = Basis(this, List(dimension) { basisVector(it) })

    /**
     * Computes the rank of a vector family.
     *
     * @param vectors The vector family.
     * @return The rank.
     */
    fun rank(vectors: List<Vector<K>>): Int {
        if (vectors.isEmpty()) return 0
        require(vectors.all { it.coordinates.size == dimension }) {
            "All vectors must have dimension $dimension."
        }

        val matrix = MutableList(dimension) { row ->
            MutableList(vectors.size) { col -> vectors[col].coordinates[row] }
        }
        return FieldLinearAlgebra.rank(scalarRing, matrix)
    }

    /**
     * Enumerates all vectors when the scalar field is finite and its elements
     * are explicitly provided.
     *
     * @param fieldElements Explicit finite field elements.
     * @return The finite set of all vectors in this space.
     */
    fun elements(fieldElements: Set<K>): Set<Vector<K>> {
        val elements = fieldElements.toList()
        var tuples = listOf(emptyList<K>())
        repeat(dimension) {
            tuples = tuples.flatMap { prefix -> elements.map { e -> prefix + e } }
        }
        return tuples.map { Vector(it) }.toSet()
    }

    private fun requireDimension(v: Vector<K>) {
        require(v.coordinates.size == dimension) {
            "Expected vector dimension $dimension, got ${v.coordinates.size}."
        }
    }
}

/**
 * A validated submodule of a given module.
 *
 * Validation is finite: closure checks are performed against the provided
 * finite subsets [elements] and [validationScalars].
 *
 * @param R The scalar type.
 * @param M The module element type.
 * @property parent The parent module.
 * @property elements Candidate submodule elements.
 * @property validationScalars Scalars used for finite closure checks.
 * @throws IllegalArgumentException if closure checks fail.
 */
class Submodule<R, M>(
    val parent: Module<R, M>,
    val elements: Set<M>,
    val validationScalars: Set<R>
) {
    init {
        require(parent.zero in elements) { "Submodule must contain the zero element." }
        require(isClosedUnderAddition()) { "Submodule is not closed under addition." }
        require(isClosedUnderInverse()) { "Submodule is not closed under additive inverse." }
        require(isClosedUnderScalarMultiplication()) { "Submodule is not closed under scalar multiplication." }
    }

    /**
     * Membership predicate.
     *
     * @param value Element to test.
     * @return True when value belongs to this submodule.
     */
    fun contains(value: M): Boolean = value in elements

    /**
     * Checks closure under addition.
     *
     * @return True when closed under addition.
     */
    fun isClosedUnderAddition(): Boolean =
        elements.all { a -> elements.all { b -> parent.add(a, b) in elements } }

    /**
     * Checks closure under additive inverse.
     *
     * @return True when closed under additive inverse.
     */
    fun isClosedUnderInverse(): Boolean =
        elements.all { a -> parent.negate(a) in elements }

    /**
     * Checks closure under scalar multiplication for [validationScalars].
     *
     * @return True when closed under tested scalars.
     */
    fun isClosedUnderScalarMultiplication(): Boolean =
        elements.all { v -> validationScalars.all { r -> parent.scalarMultiply(r, v) in elements } }
}

/**
 * A linear map between two vector spaces over the same field.
 *
 * @param K Scalar field type.
 * @param V Domain vector type.
 * @param W Codomain vector type.
 */
interface LinearMap<K, V, W> {
    /**
     * Domain space.
     */
    val domain: VectorSpace<K, V>

    /**
     * Codomain space.
     */
    val codomain: VectorSpace<K, W>

    /**
     * Applies the map.
     *
     * @param value Domain element.
     * @return Image in codomain.
     */
    fun apply(value: V): W
}

/**
 * Matrix-based linear map between finite-dimensional spaces.
 *
 * Matrix convention: [matrix] has shape (codomain.dimension x domain.dimension).
 *
 * @param K Scalar type.
 * @property domain Domain vector space.
 * @property codomain Codomain vector space.
 * @property matrix Map matrix.
 */
class FiniteDimensionalLinearMap<K>(
    override val domain: FiniteDimensionalVectorSpace<K>,
    override val codomain: FiniteDimensionalVectorSpace<K>,
    val matrix: List<List<K>>
) : LinearMap<K, Vector<K>, Vector<K>> {

    init {
        require(matrix.size == codomain.dimension) {
            "Matrix row count must be ${codomain.dimension}, got ${matrix.size}."
        }
        require(matrix.all { it.size == domain.dimension }) {
            "Matrix column count must be ${domain.dimension}."
        }
    }

    override fun apply(value: Vector<K>): Vector<K> {
        require(value.coordinates.size == domain.dimension) {
            "Expected domain dimension ${domain.dimension}, got ${value.coordinates.size}."
        }
        val field = domain.scalarRing
        val result = matrix.map { row ->
            row.indices.fold(field.zero) { acc, j ->
                field.add(acc, field.multiply(row[j], value.coordinates[j]))
            }
        }
        return codomain.vector(result)
    }

    /**
     * The rank of this linear map.
     *
     * @return Matrix rank over the scalar field.
     */
    fun rank(): Int =
        FieldLinearAlgebra.rank(domain.scalarRing, matrix.map { it.toMutableList() }.toMutableList())

    /**
     * Dimension of the image (equals rank).
     *
     * @return Image dimension.
     */
    fun imageDimension(): Int = rank()

    /**
     * Dimension of the kernel.
     *
     * @return Kernel dimension.
     */
    fun kernelDimension(): Int = domain.dimension - rank()

    /**
     * Alias for kernel dimension.
     *
     * @return Nullity.
     */
    fun nullity(): Int = kernelDimension()

    /**
     * Checks injectivity.
     *
     * @return True when rank equals domain dimension.
     */
    fun isInjective(): Boolean = rank() == domain.dimension

    /**
     * Checks surjectivity.
     *
     * @return True when rank equals codomain dimension.
     */
    fun isSurjective(): Boolean = rank() == codomain.dimension

    /**
     * Composes this map with another map (this o before).
     *
     * @param before The map applied first.
     * @return The composed linear map.
     * @throws IllegalArgumentException if dimensions are incompatible.
     */
    fun compose(before: FiniteDimensionalLinearMap<K>): FiniteDimensionalLinearMap<K> {
        require(before.codomain.dimension == domain.dimension) {
            "Cannot compose maps with incompatible dimensions."
        }
        val field = domain.scalarRing
        val composed = MutableList(codomain.dimension) {
            MutableList(before.domain.dimension) { field.zero }
        }

        for (i in 0 until codomain.dimension) {
            for (j in 0 until before.domain.dimension) {
                var sum = field.zero
                for (k in 0 until domain.dimension) {
                    sum = field.add(sum, field.multiply(matrix[i][k], before.matrix[k][j]))
                }
                composed[i][j] = sum
            }
        }

        return FiniteDimensionalLinearMap(before.domain, codomain, composed)
    }
}

/**
 * Exactness checker at the middle object for A -> B -> C.
 *
 * @param K Scalar type.
 * @property left The map A -> B.
 * @property right The map B -> C.
 */
class ExactSequence<K>(
    val left: FiniteDimensionalLinearMap<K>,
    val right: FiniteDimensionalLinearMap<K>
) {
    init {
        require(left.codomain.dimension == right.domain.dimension) {
            "Middle dimensions must match for exactness checks."
        }
    }

    /**
     * Checks if right o left is the zero map.
     *
     * @return True when composition is zero on a basis of A.
     */
    fun compositionIsZero(): Boolean =
        left.domain.canonicalBasis().vectors.all {
            right.apply(left.apply(it)) == right.codomain.zero
        }

    /**
     * Checks exactness at B: im(left) = ker(right).
     *
     * Uses finite-dimensional dimension equality and inclusion via zero
     * composition: `right o left = 0` and `rank(left) = kernelDimension(right)`.
     *
     * @return True when exact at the middle object.
     */
    fun isExactAtMiddle(): Boolean =
        compositionIsZero() && left.imageDimension() == right.kernelDimension()
}

/**
 * Short exact sequence checker for 0 -> A -> B -> C -> 0.
 *
 * @param K Scalar type.
 * @property left The map A -> B.
 * @property right The map B -> C.
 */
class ShortExactSequence<K>(
    val left: FiniteDimensionalLinearMap<K>,
    val right: FiniteDimensionalLinearMap<K>
) {
    private val exact = ExactSequence(left, right)

    /**
     * Checks short exactness: left injective, right surjective, and exact at B.
     *
     * @return True when sequence is short exact.
     */
    fun isShortExact(): Boolean =
        left.isInjective() && right.isSurjective() && exact.isExactAtMiddle()
}

/**
 * A formal finite tensor element.
 *
 * It is represented as a finite sum of simple tensors with scalar coefficients.
 *
 * @param R Scalar type.
 * @param M Left module element type.
 * @param N Right module element type.
 * @property terms Formal sum terms keyed by pairs (m, n).
 */
data class Tensor<R, M, N>(val terms: Map<Pair<M, N>, R>)

/**
 * A computational model of tensor products through formal finite sums of
 * elementary tensors.
 *
 * @param R Scalar type.
 * @param M Left module element type.
 * @param N Right module element type.
 * @property scalarRing Scalar ring.
 */
class TensorProduct<R, M, N>(val scalarRing: Ring<R>) {
    /**
     * The zero tensor.
     *
     * @return Zero tensor.
     */
    fun zero(): Tensor<R, M, N> = Tensor(emptyMap())

    /**
     * Creates a simple tensor m ⊗ n with a coefficient.
     *
     * @param left Left element.
     * @param right Right element.
     * @param coefficient Scalar coefficient.
     * @return The resulting tensor.
     */
    fun of(left: M, right: N, coefficient: R = scalarRing.one): Tensor<R, M, N> =
        if (coefficient == scalarRing.zero) zero()
        else Tensor(mapOf((left to right) to coefficient))

    /**
     * Adds tensors.
     *
     * @param a First tensor.
     * @param b Second tensor.
     * @return Sum tensor.
     */
    fun add(a: Tensor<R, M, N>, b: Tensor<R, M, N>): Tensor<R, M, N> {
        val keys = a.terms.keys + b.terms.keys
        val merged = mutableMapOf<Pair<M, N>, R>()
        for (k in keys) {
            val av = a.terms[k] ?: scalarRing.zero
            val bv = b.terms[k] ?: scalarRing.zero
            val sum = scalarRing.add(av, bv)
            if (sum != scalarRing.zero) {
                merged[k] = sum
            }
        }
        return Tensor(merged)
    }

    /**
     * Scalar multiplication on tensors.
     *
     * @param scalar The scalar.
     * @param tensor The tensor.
     * @return Scaled tensor.
     */
    fun scalarMultiply(scalar: R, tensor: Tensor<R, M, N>): Tensor<R, M, N> {
        if (scalar == scalarRing.zero) return zero()
        val scaled = tensor.terms.mapValues { (_, value) -> scalarRing.multiply(scalar, value) }
            .filterValues { it != scalarRing.zero }
        return Tensor(scaled)
    }
}

private object FieldLinearAlgebra {
    fun <K> rank(field: Field<K>, matrix: MutableList<MutableList<K>>): Int {
        if (matrix.isEmpty()) return 0
        val rowCount = matrix.size
        val colCount = matrix.first().size
        if (colCount == 0) return 0

        var rank = 0
        var pivotCol = 0

        while (rank < rowCount && pivotCol < colCount) {
            var pivotRow = rank
            while (pivotRow < rowCount && matrix[pivotRow][pivotCol] == field.zero) {
                pivotRow++
            }

            if (pivotRow == rowCount) {
                pivotCol++
                continue
            }

            if (pivotRow != rank) {
                val tmp = matrix[rank]
                matrix[rank] = matrix[pivotRow]
                matrix[pivotRow] = tmp
            }

            val pivot = matrix[rank][pivotCol]
            val pivotInv = field.reciprocal(pivot)
            for (j in pivotCol until colCount) {
                matrix[rank][j] = field.multiply(matrix[rank][j], pivotInv)
            }

            for (i in 0 until rowCount) {
                if (i == rank) continue
                val factor = matrix[i][pivotCol]
                if (factor == field.zero) continue
                for (j in pivotCol until colCount) {
                    matrix[i][j] = field.subtract(
                        matrix[i][j],
                        field.multiply(factor, matrix[rank][j])
                    )
                }
            }

            rank++
            pivotCol++
        }

        return rank
    }
}
