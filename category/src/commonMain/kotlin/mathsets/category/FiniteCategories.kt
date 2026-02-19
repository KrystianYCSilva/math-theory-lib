package mathsets.category

import mathsets.algebra.Group

/**
 * Object of the finite-set category.
 *
 * @property elements Finite set elements.
 */
data class FinSetObject(val elements: Set<Int>)

/**
 * Morphism in FinSet.
 *
 * @property source Source set.
 * @property target Target set.
 * @property mapping Total map from source elements to target elements.
 */
data class FinSetMorphism(
    override val source: FinSetObject,
    override val target: FinSetObject,
    val mapping: Map<Int, Int>
) : Morphism<FinSetObject> {
    init {
        require(mapping.keys == source.elements) {
            "FinSet morphism must map every source element exactly once."
        }
        require(mapping.values.all { it in target.elements }) {
            "FinSet morphism must land in the target set."
        }
    }

    /**
     * Applies this morphism.
     *
     * @param x Source element.
     * @return Image element.
     */
    fun apply(x: Int): Int = mapping.getValue(x)

    companion object {
        /**
         * Builds a finite-set morphism from a function.
         *
         * @param source Source object.
         * @param target Target object.
         * @param f Function from source elements to target elements.
         * @return Constructed morphism.
         */
        fun fromFunction(source: FinSetObject, target: FinSetObject, f: (Int) -> Int): FinSetMorphism =
            FinSetMorphism(source, target, source.elements.associateWith { f(it) })
    }
}

/**
 * Category FinSet of finite sets and functions.
 */
object FinSetCategory : Category<FinSetObject, FinSetMorphism> {
    override fun id(obj: FinSetObject): FinSetMorphism =
        FinSetMorphism(obj, obj, obj.elements.associateWith { it })

    override fun compose(f: FinSetMorphism, g: FinSetMorphism): FinSetMorphism {
        require(g.target == f.source) { "FinSet morphisms are not composable." }
        val composed = g.source.elements.associateWith { x ->
            f.mapping.getValue(g.mapping.getValue(x))
        }
        return FinSetMorphism(g.source, f.target, composed)
    }

    /**
     * Enumerates all morphisms between two finite sets.
     *
     * @param source Source object.
     * @param target Target object.
     * @return All set-theoretic maps source -> target.
     */
    fun hom(source: FinSetObject, target: FinSetObject): Set<FinSetMorphism> {
        if (source.elements.isEmpty()) return setOf(id(FinSetObject(emptySet())).copy(target = target))

        val sourceList = source.elements.toList()
        val targetList = target.elements.toList()
        if (targetList.isEmpty()) return emptySet()

        var assignments = listOf(emptyList<Int>())
        repeat(sourceList.size) {
            assignments = assignments.flatMap { prefix -> targetList.map { v -> prefix + v } }
        }

        return assignments.map { values ->
            val map = sourceList.indices.associate { i -> sourceList[i] to values[i] }
            FinSetMorphism(source, target, map)
        }.toSet()
    }
}

/**
 * Object of a finite-group category (implemented for Int-based groups).
 *
 * @property group Group structure.
 * @property elements Finite carrier set.
 */
data class FinGroupObject(
    val group: Group<Int>,
    val elements: Set<Int>
)

/**
 * Morphism in FinGroup.
 *
 * @property source Source finite group.
 * @property target Target finite group.
 * @property mapping Group-homomorphism table.
 */
data class FinGroupMorphism(
    override val source: FinGroupObject,
    override val target: FinGroupObject,
    val mapping: Map<Int, Int>
) : Morphism<FinGroupObject> {
    init {
        require(mapping.keys == source.elements) {
            "FinGroup morphism must map every source element exactly once."
        }
        require(mapping.values.all { it in target.elements }) {
            "FinGroup morphism must land in the target group."
        }
        require(verifyHomomorphism()) {
            "Mapping is not a group homomorphism."
        }
    }

    /**
     * Applies this morphism.
     *
     * @param x Source group element.
     * @return Image group element.
     */
    fun apply(x: Int): Int = mapping.getValue(x)

    /**
     * Verifies homomorphism law on the finite carrier.
     *
     * @return True when f(a * b) = f(a) * f(b) for all a,b.
     */
    fun verifyHomomorphism(): Boolean =
        source.elements.all { a ->
            source.elements.all { b ->
                apply(source.group.op(a, b)) == target.group.op(apply(a), apply(b))
            }
        }

    companion object {
        /**
         * Builds a finite-group morphism from a function.
         *
         * @param source Source finite group.
         * @param target Target finite group.
         * @param f Mapping function.
         * @return Constructed group morphism.
         */
        fun fromFunction(source: FinGroupObject, target: FinGroupObject, f: (Int) -> Int): FinGroupMorphism =
            FinGroupMorphism(source, target, source.elements.associateWith { f(it) })
    }
}

/**
 * Category FinGroup of finite Int-based groups and homomorphisms.
 */
object FinGroupCategory : Category<FinGroupObject, FinGroupMorphism> {
    override fun id(obj: FinGroupObject): FinGroupMorphism =
        FinGroupMorphism(obj, obj, obj.elements.associateWith { it })

    override fun compose(f: FinGroupMorphism, g: FinGroupMorphism): FinGroupMorphism {
        require(g.target == f.source) { "FinGroup morphisms are not composable." }
        val composed = g.source.elements.associateWith { x ->
            f.mapping.getValue(g.mapping.getValue(x))
        }
        return FinGroupMorphism(g.source, f.target, composed)
    }
}

/**
 * Forgetful functor U: FinGroup -> FinSet.
 */
object ForgetfulFinGroupToFinSet : Functor<FinGroupObject, FinGroupMorphism, FinSetObject, FinSetMorphism> {
    override val domain: Category<FinGroupObject, FinGroupMorphism> = FinGroupCategory
    override val codomain: Category<FinSetObject, FinSetMorphism> = FinSetCategory

    override fun mapObject(obj: FinGroupObject): FinSetObject = FinSetObject(obj.elements)

    override fun mapMorphism(morphism: FinGroupMorphism): FinSetMorphism = FinSetMorphism(
        source = mapObject(morphism.source),
        target = mapObject(morphism.target),
        mapping = morphism.mapping
    )
}
