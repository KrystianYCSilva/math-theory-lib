package mathsets.function

import mathsets.kernel.Cardinality
import mathsets.relation.OrderedPair
import mathsets.set.ExtensionalSet
import mathsets.set.MathSet

/**
 * Represents a mathematical function f: A -> B.
 *
 * A function is a special relation where every element of the [domain] maps to exactly
 * one element of the [codomain]. The function is defined by a [mapping] lambda and
 * enforces at invocation time that inputs belong to the domain and outputs belong to
 * the codomain.
 *
 * @param A the type of domain elements
 * @param B the type of codomain elements
 * @property domain the source set A
 * @property codomain the target set B
 * @param mapping the rule that assigns to each a in A a unique b in B
 */
open class MathFunction<A, B>(
    val domain: MathSet<A>,
    val codomain: MathSet<B>,
    private val mapping: (A) -> B
) {
    /**
     * The **graph** of the function: { (a, f(a)) | a in A }.
     *
     * Lazily computed on first access by enumerating all domain elements.
     */
    val graph: MathSet<OrderedPair<A, B>> by lazy {
        ExtensionalSet(domain.elements().map { a -> OrderedPair(a, invoke(a)) }.toSet())
    }

    /**
     * Applies this function to an argument [a], returning f(a).
     *
     * @param a the input element
     * @return the output element f(a)
     * @throws IllegalArgumentException if [a] is not in the domain or if f(a) is not in the codomain
     */
    operator fun invoke(a: A): B {
        require(a in domain) { "Element $a does not belong to function domain." }
        val value = mapping(a)
        require(value in codomain) { "Mapped value $value does not belong to codomain." }
        return value
    }

    /**
     * Computes the **image** (range) of the function: f(A) = { f(a) | a in A }.
     *
     * @return the set of all output values
     */
    fun image(): MathSet<B> = ExtensionalSet(domain.elements().map { invoke(it) }.toSet())

    /**
     * Computes the **pre-image** of a single [value]: f⁻¹({b}) = { a in A | f(a) = b }.
     *
     * @param value the codomain element to find pre-images for
     * @return the set of all domain elements that map to [value]
     */
    fun preImage(value: B): MathSet<A> = ExtensionalSet(
        domain.elements().filter { a -> invoke(a) == value }.toSet()
    )

    /**
     * Computes the **pre-image** of a [subset] of the codomain: f⁻¹(S) = { a in A | f(a) in S }.
     *
     * @param subset a subset of the codomain
     * @return the set of all domain elements whose image falls within [subset]
     */
    fun preImage(subset: MathSet<B>): MathSet<A> = ExtensionalSet(
        domain.elements().filter { a -> invoke(a) in subset }.toSet()
    )

    /**
     * Tests whether this function is **injective** (one-to-one).
     *
     * A function is injective if f(a₁) = f(a₂) implies a₁ = a₂ for all a₁, a₂ in A.
     * Currently requires a finite domain.
     *
     * @return `true` if the function is injective
     * @throws IllegalArgumentException if the domain is not finite
     */
    open fun isInjective(): Boolean {
        require(domain.cardinality is Cardinality.Finite) {
            "Injectivity check currently requires a finite domain."
        }
        val values = domain.elements().map { invoke(it) }.toList()
        return values.toSet().size == values.size
    }

    /**
     * Tests whether this function is **surjective** (onto).
     *
     * A function is surjective if for every b in B there exists an a in A with f(a) = b.
     * Currently requires both domain and codomain to be finite.
     *
     * @return `true` if the function is surjective
     * @throws IllegalArgumentException if domain or codomain is not finite
     */
    open fun isSurjective(): Boolean {
        require(domain.cardinality is Cardinality.Finite && codomain.cardinality is Cardinality.Finite) {
            "Surjectivity check currently requires finite domain and codomain."
        }
        return codomain.elements().all { b -> domain.elements().any { a -> invoke(a) == b } }
    }

    /**
     * Tests whether this function is **bijective** (both injective and surjective).
     *
     * @return `true` if the function is a bijection
     */
    fun isBijective(): Boolean = isInjective() && isSurjective()

    /**
     * Computes the **composition** of this function with [before]: (f . g)(x) = f(g(x)).
     *
     * Given g: X -> A and this function f: A -> B, returns the composed function
     * f . g: X -> B. Validates that the image of [before] is a subset of this
     * function's domain.
     *
     * @param X the domain type of the [before] function
     * @param before the function to apply first (g: X -> A)
     * @return the composed function f . g: X -> B
     * @throws IllegalArgumentException if the image of [before] is not contained in this domain,
     *   or if [before]'s domain is not finite
     */
    fun <X> compose(before: MathFunction<X, A>): MathFunction<X, B> {
        require(before.domain.cardinality is Cardinality.Finite) {
            "Composition validation currently requires a finite pre-domain."
        }
        val allOutputsInDomain = before.domain.elements().all { x -> before(x) in domain }
        require(allOutputsInDomain) {
            "Function composition invalid: image(before) must be subset of domain(current)."
        }
        return MathFunction(before.domain, codomain) { x -> invoke(before(x)) }
    }
}

/**
 * A function guaranteed to be **injective** (one-to-one).
 *
 * Injectivity is verified at construction time: f(a₁) = f(a₂) implies a₁ = a₂.
 *
 * @param A the domain type
 * @param B the codomain type
 * @throws IllegalArgumentException if the provided mapping is not injective
 */
class Injection<A, B>(
    domain: MathSet<A>,
    codomain: MathSet<B>,
    mapping: (A) -> B
) : MathFunction<A, B>(domain, codomain, mapping) {
    init {
        require(isInjective()) { "Provided mapping is not injective." }
    }
}

/**
 * A function guaranteed to be **surjective** (onto).
 *
 * Surjectivity is verified at construction time: for every b in B, there exists a in A
 * with f(a) = b.
 *
 * @param A the domain type
 * @param B the codomain type
 * @throws IllegalArgumentException if the provided mapping is not surjective
 */
class Surjection<A, B>(
    domain: MathSet<A>,
    codomain: MathSet<B>,
    mapping: (A) -> B
) : MathFunction<A, B>(domain, codomain, mapping) {
    init {
        require(isSurjective()) { "Provided mapping is not surjective." }
    }
}

/**
 * A function guaranteed to be **bijective** (both injective and surjective).
 *
 * Bijectivity is verified at construction time. A bijection establishes a one-to-one
 * correspondence between its domain and codomain, and admits an [inverse].
 *
 * @param A the domain type
 * @param B the codomain type
 * @throws IllegalArgumentException if the provided mapping is not bijective
 */
class Bijection<A, B>(
    domain: MathSet<A>,
    codomain: MathSet<B>,
    mapping: (A) -> B
) : MathFunction<A, B>(domain, codomain, mapping) {
    init {
        require(isBijective()) { "Provided mapping is not bijective." }
    }

    /**
     * Computes the **inverse bijection** f⁻¹: B -> A.
     *
     * Since every bijection is invertible, f⁻¹(b) is the unique a such that f(a) = b.
     * Currently requires both domain and codomain to be finite.
     *
     * @return the inverse bijection from codomain to domain
     * @throws IllegalArgumentException if domain or codomain is not finite
     */
    fun inverse(): Bijection<B, A> {
        require(domain.cardinality is Cardinality.Finite && codomain.cardinality is Cardinality.Finite) {
            "Inverse currently requires finite domain and codomain."
        }
        val inverseMap = domain.elements().associate { a -> invoke(a) to a }
        return Bijection(codomain, domain) { b ->
            inverseMap[b] ?: throw IllegalArgumentException("No inverse image for $b")
        }
    }
}
