package mathsets.category

/**
 * A morphism between two objects in a category.
 *
 * @param Obj The object type.
 */
interface Morphism<Obj> {
    /**
     * Source object.
     */
    val source: Obj

    /**
     * Target object.
     */
    val target: Obj
}

/**
 * A category with identity and composition.
 *
 * Composition convention: `compose(f, g)` means `f o g`.
 *
 * @param Obj The object type.
 * @param Mor The morphism type.
 */
interface Category<Obj, Mor : Morphism<Obj>> {
    /**
     * Identity morphism on an object.
     *
     * @param obj The object.
     * @return id_obj.
     */
    fun id(obj: Obj): Mor

    /**
     * Composes morphisms.
     *
     * @param f Morphism B -> C.
     * @param g Morphism A -> B.
     * @return Morphism A -> C.
     * @throws IllegalArgumentException if domains are incompatible.
     */
    fun compose(f: Mor, g: Mor): Mor
}

/**
 * An isomorphism in a category.
 *
 * @param Obj The object type.
 * @param Mor The morphism type.
 * @property category The ambient category.
 * @property forward Forward morphism.
 * @property backward Inverse morphism.
 */
data class Isomorphism<Obj, Mor : Morphism<Obj>>(
    val category: Category<Obj, Mor>,
    val forward: Mor,
    val backward: Mor
) {
    /**
     * Checks the inverse laws.
     *
     * @return True when backward is inverse of forward.
     */
    fun isValid(): Boolean {
        val left = category.compose(forward, backward)
        val right = category.compose(backward, forward)
        return left == category.id(forward.target) && right == category.id(forward.source)
    }
}

/**
 * Utilities to verify category laws on finite samples.
 */
object CategoryLaws {
    /**
     * Verifies identity laws for sampled morphisms.
     *
     * @param Obj The object type.
     * @param Mor The morphism type.
     * @param category The category.
     * @param morphisms Sampled morphisms.
     * @return True when left and right identities hold.
     */
    fun <Obj, Mor : Morphism<Obj>> verifyIdentity(
        category: Category<Obj, Mor>,
        morphisms: Set<Mor>
    ): Boolean = morphisms.all { m ->
        category.compose(category.id(m.target), m) == m &&
            category.compose(m, category.id(m.source)) == m
    }

    /**
     * Verifies associativity for sampled composable triples.
     *
     * @param Obj The object type.
     * @param Mor The morphism type.
     * @param category The category.
     * @param morphisms Sampled morphisms.
     * @return True when associativity holds for all composable triples.
     */
    fun <Obj, Mor : Morphism<Obj>> verifyAssociativity(
        category: Category<Obj, Mor>,
        morphisms: Set<Mor>
    ): Boolean {
        val triples = morphisms.flatMap { f ->
            morphisms.flatMap { g ->
                morphisms.mapNotNull { h ->
                    if (g.target == f.source && h.target == g.source) Triple(f, g, h) else null
                }
            }
        }
        return triples.all { (f, g, h) ->
            category.compose(category.compose(f, g), h) == category.compose(f, category.compose(g, h))
        }
    }
}

/**
 * A morphism in an opposite category.
 *
 * @param Obj The object type.
 * @param Mor The original morphism type.
 * @property original The wrapped original morphism.
 */
data class OppositeMorphism<Obj, Mor : Morphism<Obj>>(val original: Mor) : Morphism<Obj> {
    override val source: Obj = original.target
    override val target: Obj = original.source
}

/**
 * Opposite category C^op.
 *
 * @param Obj The object type.
 * @param Mor The original morphism type.
 * @property original The original category C.
 */
class OppositeCategory<Obj, Mor : Morphism<Obj>>(
    val original: Category<Obj, Mor>
) : Category<Obj, OppositeMorphism<Obj, Mor>> {
    override fun id(obj: Obj): OppositeMorphism<Obj, Mor> = OppositeMorphism(original.id(obj))

    override fun compose(
        f: OppositeMorphism<Obj, Mor>,
        g: OppositeMorphism<Obj, Mor>
    ): OppositeMorphism<Obj, Mor> {
        require(g.target == f.source) { "Morphisms are not composable in C^op." }
        return OppositeMorphism(original.compose(f.original, g.original))
    }
}

/**
 * Product object in C x D.
 *
 * @param O1 First object type.
 * @param O2 Second object type.
 */
data class ProductObject<O1, O2>(val left: O1, val right: O2)

/**
 * Product morphism in C x D.
 *
 * @param O1 First object type.
 * @param M1 First morphism type.
 * @param O2 Second object type.
 * @param M2 Second morphism type.
 * @property left Left component morphism.
 * @property right Right component morphism.
 */
data class ProductMorphism<O1, M1 : Morphism<O1>, O2, M2 : Morphism<O2>>(
    val left: M1,
    val right: M2
) : Morphism<ProductObject<O1, O2>> {
    override val source: ProductObject<O1, O2> = ProductObject(left.source, right.source)
    override val target: ProductObject<O1, O2> = ProductObject(left.target, right.target)
}

/**
 * Product category C x D.
 *
 * @param O1 First object type.
 * @param M1 First morphism type.
 * @param O2 Second object type.
 * @param M2 Second morphism type.
 * @property first First category C.
 * @property second Second category D.
 */
class ProductCategory<O1, M1 : Morphism<O1>, O2, M2 : Morphism<O2>>(
    val first: Category<O1, M1>,
    val second: Category<O2, M2>
) : Category<ProductObject<O1, O2>, ProductMorphism<O1, M1, O2, M2>> {
    override fun id(obj: ProductObject<O1, O2>): ProductMorphism<O1, M1, O2, M2> =
        ProductMorphism(first.id(obj.left), second.id(obj.right))

    override fun compose(
        f: ProductMorphism<O1, M1, O2, M2>,
        g: ProductMorphism<O1, M1, O2, M2>
    ): ProductMorphism<O1, M1, O2, M2> {
        require(g.target == f.source) { "Morphisms are not composable in product category." }
        return ProductMorphism(
            first.compose(f.left, g.left),
            second.compose(f.right, g.right)
        )
    }
}
