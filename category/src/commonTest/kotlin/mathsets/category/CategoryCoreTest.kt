package mathsets.category

import mathsets.algebra.CyclicGroup
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CategoryCoreTest {

    @Test
    fun finSetSatisfiesIdentityAndAssociativity() {
        val x = FinSetObject(setOf(0, 1))

        val id = FinSetCategory.id(x)
        val swap = FinSetMorphism.fromFunction(x, x) { if (it == 0) 1 else 0 }
        val const0 = FinSetMorphism.fromFunction(x, x) { 0 }
        val const1 = FinSetMorphism.fromFunction(x, x) { 1 }

        val morphisms = setOf(id, swap, const0, const1)

        assertTrue(CategoryLaws.verifyIdentity(FinSetCategory, morphisms))
        assertTrue(CategoryLaws.verifyAssociativity(FinSetCategory, morphisms))
    }

    @Test
    fun functorCompositionIsAssociativeOnSamples() {
        val singleton = FinSetObject(setOf(0))

        val identity = IdentityFunctor(FinSetCategory)
        val constant = object : Functor<FinSetObject, FinSetMorphism, FinSetObject, FinSetMorphism> {
            override val domain: Category<FinSetObject, FinSetMorphism> = FinSetCategory
            override val codomain: Category<FinSetObject, FinSetMorphism> = FinSetCategory

            override fun mapObject(obj: FinSetObject): FinSetObject = singleton

            override fun mapMorphism(morphism: FinSetMorphism): FinSetMorphism = FinSetCategory.id(singleton)
        }

        val left = ComposedFunctor(ComposedFunctor(identity, constant), identity)
        val right = ComposedFunctor(identity, ComposedFunctor(constant, identity))

        val a = FinSetObject(setOf(0, 1, 2))
        val b = FinSetObject(setOf(0, 1))
        val f = FinSetMorphism.fromFunction(a, b) { it % 2 }

        assertEquals(left.mapObject(a), right.mapObject(a))
        assertEquals(left.mapMorphism(f), right.mapMorphism(f))
    }

    @Test
    fun naturalTransformationNaturalityHolds() {
        val singleton = FinSetObject(setOf(0))

        val identity = IdentityFunctor(FinSetCategory)
        val constant = object : Functor<FinSetObject, FinSetMorphism, FinSetObject, FinSetMorphism> {
            override val domain: Category<FinSetObject, FinSetMorphism> = FinSetCategory
            override val codomain: Category<FinSetObject, FinSetMorphism> = FinSetCategory

            override fun mapObject(obj: FinSetObject): FinSetObject = singleton

            override fun mapMorphism(morphism: FinSetMorphism): FinSetMorphism = FinSetCategory.id(singleton)
        }

        val eta = NaturalTransformation(
            sourceFunctor = identity,
            targetFunctor = constant,
            component = { obj -> FinSetMorphism.fromFunction(obj, singleton) { 0 } }
        )

        val a = FinSetObject(setOf(0, 1))
        val b = FinSetObject(setOf(0, 1, 2))
        val c = FinSetObject(setOf(0, 1, 2, 3))

        val f = FinSetMorphism.fromFunction(a, b) { it }
        val g = FinSetMorphism.fromFunction(b, c) { it }

        assertTrue(eta.verifyNaturality(setOf(f, g)))
    }

    @Test
    fun forgetfulFunctorPreservesIdentitiesAndComposition() {
        val z3 = CyclicGroup(3)
        val g = FinGroupObject(z3, z3.elements())

        val double = FinGroupMorphism.fromFunction(g, g) { (2 * it) % 3 }

        val left = ForgetfulFinGroupToFinSet.mapMorphism(FinGroupCategory.compose(double, double))
        val right = FinSetCategory.compose(
            ForgetfulFinGroupToFinSet.mapMorphism(double),
            ForgetfulFinGroupToFinSet.mapMorphism(double)
        )

        assertEquals(left, right)

        val idMapped = ForgetfulFinGroupToFinSet.mapMorphism(FinGroupCategory.id(g))
        val idOfMappedObject = FinSetCategory.id(ForgetfulFinGroupToFinSet.mapObject(g))
        assertEquals(idOfMappedObject, idMapped)
    }

    @Test
    fun oppositeCategoryReversesCompositionDirection() {
        val x = FinSetObject(setOf(0, 1))
        val swap = FinSetMorphism.fromFunction(x, x) { if (it == 0) 1 else 0 }
        val const0 = FinSetMorphism.fromFunction(x, x) { 0 }

        val op = OppositeCategory(FinSetCategory)
        val left = op.compose(OppositeMorphism(swap), OppositeMorphism(const0))
        val right = OppositeMorphism(FinSetCategory.compose(swap, const0))

        assertEquals(right, left)
    }
}
