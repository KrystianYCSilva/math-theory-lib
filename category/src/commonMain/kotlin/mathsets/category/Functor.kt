package mathsets.category

/**
 * A functor F: C -> D.
 *
 * @param CObj Object type of C.
 * @param CMor Morphism type of C.
 * @param DObj Object type of D.
 * @param DMor Morphism type of D.
 */
interface Functor<CObj, CMor : Morphism<CObj>, DObj, DMor : Morphism<DObj>> {
    /**
     * Domain category C.
     */
    val domain: Category<CObj, CMor>

    /**
     * Codomain category D.
     */
    val codomain: Category<DObj, DMor>

    /**
     * Maps an object.
     *
     * @param obj Object in C.
     * @return Object in D.
     */
    fun mapObject(obj: CObj): DObj

    /**
     * Maps a morphism.
     *
     * @param morphism Morphism in C.
     * @return Morphism in D.
     */
    fun mapMorphism(morphism: CMor): DMor
}

/**
 * Identity functor Id_C.
 *
 * @param Obj Object type.
 * @param Mor Morphism type.
 * @property category The category C.
 */
class IdentityFunctor<Obj, Mor : Morphism<Obj>>(
    val category: Category<Obj, Mor>
) : Functor<Obj, Mor, Obj, Mor> {
    override val domain: Category<Obj, Mor> = category
    override val codomain: Category<Obj, Mor> = category

    override fun mapObject(obj: Obj): Obj = obj

    override fun mapMorphism(morphism: Mor): Mor = morphism
}

/**
 * Composition of functors F: C->D and G: D->E yielding G o F: C->E.
 *
 * @param CObj Object type of C.
 * @param CMor Morphism type of C.
 * @param DObj Object type of D.
 * @param DMor Morphism type of D.
 * @param EObj Object type of E.
 * @param EMor Morphism type of E.
 * @property first F: C -> D.
 * @property second G: D -> E.
 */
class ComposedFunctor<CObj, CMor : Morphism<CObj>, DObj, DMor : Morphism<DObj>, EObj, EMor : Morphism<EObj>>(
    val first: Functor<CObj, CMor, DObj, DMor>,
    val second: Functor<DObj, DMor, EObj, EMor>
) : Functor<CObj, CMor, EObj, EMor> {
    init {
        require(first.codomain == second.domain) {
            "Functor composition requires first.codomain == second.domain."
        }
    }

    override val domain: Category<CObj, CMor> = first.domain
    override val codomain: Category<EObj, EMor> = second.codomain

    override fun mapObject(obj: CObj): EObj = second.mapObject(first.mapObject(obj))

    override fun mapMorphism(morphism: CMor): EMor = second.mapMorphism(first.mapMorphism(morphism))
}

/**
 * A natural transformation eta: F => G.
 *
 * @param CObj Object type of C.
 * @param CMor Morphism type of C.
 * @param DObj Object type of D.
 * @param DMor Morphism type of D.
 * @property sourceFunctor F.
 * @property targetFunctor G.
 * @property component Component eta_A for each object A in C.
 */
class NaturalTransformation<CObj, CMor : Morphism<CObj>, DObj, DMor : Morphism<DObj>>(
    val sourceFunctor: Functor<CObj, CMor, DObj, DMor>,
    val targetFunctor: Functor<CObj, CMor, DObj, DMor>,
    val component: (CObj) -> DMor
) {
    init {
        require(sourceFunctor.domain == targetFunctor.domain) {
            "Natural transformation requires same source category."
        }
        require(sourceFunctor.codomain == targetFunctor.codomain) {
            "Natural transformation requires same target category."
        }
    }

    /**
     * Checks the naturality condition for sampled morphisms:
     * eta_B o F(f) = G(f) o eta_A.
     *
     * @param morphisms Sample morphisms f: A -> B in C.
     * @return True when naturality holds for all samples.
     */
    fun verifyNaturality(morphisms: Set<CMor>): Boolean {
        val d = sourceFunctor.codomain
        return morphisms.all { f ->
            val left = d.compose(component(f.target), sourceFunctor.mapMorphism(f))
            val right = d.compose(targetFunctor.mapMorphism(f), component(f.source))
            left == right
        }
    }
}

/**
 * Utilities to verify functor laws on finite samples.
 */
object FunctorLaws {
    /**
     * Verifies identity preservation F(id_X) = id_{F(X)} on sampled objects.
     *
     * @param CObj Object type of C.
     * @param CMor Morphism type of C.
     * @param DObj Object type of D.
     * @param DMor Morphism type of D.
     * @param functor The functor to test.
     * @param objects Sample objects.
     * @return True when identity is preserved for all samples.
     */
    fun <CObj, CMor : Morphism<CObj>, DObj, DMor : Morphism<DObj>> verifyIdentityPreservation(
        functor: Functor<CObj, CMor, DObj, DMor>,
        objects: Set<CObj>
    ): Boolean = objects.all { obj ->
        functor.mapMorphism(functor.domain.id(obj)) == functor.codomain.id(functor.mapObject(obj))
    }

    /**
     * Verifies composition preservation F(f o g) = F(f) o F(g).
     *
     * @param CObj Object type of C.
     * @param CMor Morphism type of C.
     * @param DObj Object type of D.
     * @param DMor Morphism type of D.
     * @param functor The functor to test.
     * @param morphisms Sample morphisms.
     * @return True when composition is preserved for all sampled composable pairs.
     */
    fun <CObj, CMor : Morphism<CObj>, DObj, DMor : Morphism<DObj>> verifyCompositionPreservation(
        functor: Functor<CObj, CMor, DObj, DMor>,
        morphisms: Set<CMor>
    ): Boolean {
        val pairs = morphisms.flatMap { f ->
            morphisms.mapNotNull { g -> if (g.target == f.source) f to g else null }
        }
        return pairs.all { (f, g) ->
            functor.mapMorphism(functor.domain.compose(f, g)) ==
                functor.codomain.compose(functor.mapMorphism(f), functor.mapMorphism(g))
        }
    }
}
