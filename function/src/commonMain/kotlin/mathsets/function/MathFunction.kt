package mathsets.function

import mathsets.kernel.Cardinality
import mathsets.relation.OrderedPair
import mathsets.set.ExtensionalSet
import mathsets.set.MathSet

open class MathFunction<A, B>(
    val domain: MathSet<A>,
    val codomain: MathSet<B>,
    private val mapping: (A) -> B
) {
    val graph: MathSet<OrderedPair<A, B>> by lazy {
        ExtensionalSet(domain.elements().map { a -> OrderedPair(a, invoke(a)) }.toSet())
    }

    operator fun invoke(a: A): B {
        require(a in domain) { "Element $a does not belong to function domain." }
        val value = mapping(a)
        require(value in codomain) { "Mapped value $value does not belong to codomain." }
        return value
    }

    fun image(): MathSet<B> = ExtensionalSet(domain.elements().map { invoke(it) }.toSet())

    fun preImage(value: B): MathSet<A> = ExtensionalSet(
        domain.elements().filter { a -> invoke(a) == value }.toSet()
    )

    fun preImage(subset: MathSet<B>): MathSet<A> = ExtensionalSet(
        domain.elements().filter { a -> invoke(a) in subset }.toSet()
    )

    open fun isInjective(): Boolean {
        require(domain.cardinality is Cardinality.Finite) {
            "Injectivity check currently requires a finite domain."
        }
        val values = domain.elements().map { invoke(it) }.toList()
        return values.toSet().size == values.size
    }

    open fun isSurjective(): Boolean {
        require(domain.cardinality is Cardinality.Finite && codomain.cardinality is Cardinality.Finite) {
            "Surjectivity check currently requires finite domain and codomain."
        }
        return codomain.elements().all { b -> domain.elements().any { a -> invoke(a) == b } }
    }

    fun isBijective(): Boolean = isInjective() && isSurjective()

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

class Injection<A, B>(
    domain: MathSet<A>,
    codomain: MathSet<B>,
    mapping: (A) -> B
) : MathFunction<A, B>(domain, codomain, mapping) {
    init {
        require(isInjective()) { "Provided mapping is not injective." }
    }
}

class Surjection<A, B>(
    domain: MathSet<A>,
    codomain: MathSet<B>,
    mapping: (A) -> B
) : MathFunction<A, B>(domain, codomain, mapping) {
    init {
        require(isSurjective()) { "Provided mapping is not surjective." }
    }
}

class Bijection<A, B>(
    domain: MathSet<A>,
    codomain: MathSet<B>,
    mapping: (A) -> B
) : MathFunction<A, B>(domain, codomain, mapping) {
    init {
        require(isBijective()) { "Provided mapping is not bijective." }
    }

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
