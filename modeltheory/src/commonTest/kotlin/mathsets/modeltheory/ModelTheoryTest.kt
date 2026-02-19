package mathsets.modeltheory

import mathsets.logic.Formula
import mathsets.logic.Term
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ModelTheoryTest {

    @Test
    fun satisfactionEvaluatesUniversalIdentitySentence() {
        val signature = Signature(
            functions = setOf(FunctionSymbol("id", 1))
        )

        val structure = Structure(
            signature = signature,
            universe = setOf(0, 1, 2),
            constantInterpretation = emptyMap(),
            functionInterpretation = mapOf(FunctionSymbol("id", 1) to { args: List<Int> -> args[0] }),
            relationInterpretation = emptyMap()
        )

        val sentence = Formula.ForAll(
            "x",
            Formula.Equals(Term.App("id", listOf(Term.Var("x"))), Term.Var("x"))
        )

        assertTrue(Satisfaction(structure).satisfies(sentence))
    }

    @Test
    fun satisfactionUsesInterpretedRelationForMembership() {
        val inSymbol = RelationSymbol("in", 2)
        val signature = Signature(
            constants = setOf(ConstantSymbol("S")),
            relations = setOf(inSymbol)
        )

        val structure = Structure(
            signature = signature,
            universe = setOf(0, 1, 2),
            constantInterpretation = mapOf(ConstantSymbol("S") to 2),
            functionInterpretation = emptyMap(),
            relationInterpretation = mapOf(inSymbol to { args: List<Int> -> args[0] <= args[1] })
        )

        val sentence = Formula.ForAll(
            "x",
            Formula.Implies(
                Formula.Membership(Term.Var("x"), Term.Const("S")),
                Formula.Equals(Term.Var("x"), Term.Var("x"))
            )
        )

        assertTrue(Satisfaction(structure).satisfies(sentence))
    }

    @Test
    fun elementaryEquivalenceOnIsomorphicFiniteStructures() {
        val signature = Signature(
            constants = setOf(ConstantSymbol("c")),
            functions = setOf(FunctionSymbol("id", 1))
        )

        val first = Structure(
            signature = signature,
            universe = setOf(0, 1),
            constantInterpretation = mapOf(ConstantSymbol("c") to 0),
            functionInterpretation = mapOf(FunctionSymbol("id", 1) to { args: List<Int> -> args[0] }),
            relationInterpretation = emptyMap()
        )

        val second = Structure(
            signature = signature,
            universe = setOf(10, 11),
            constantInterpretation = mapOf(ConstantSymbol("c") to 10),
            functionInterpretation = mapOf(FunctionSymbol("id", 1) to { args: List<Int> -> args[0] }),
            relationInterpretation = emptyMap()
        )

        val sentences = setOf(
            Formula.Equals(Term.Const("c"), Term.Const("c")),
            Formula.ForAll("x", Formula.Equals(Term.App("id", listOf(Term.Var("x"))), Term.Var("x")))
        )

        assertTrue(ElementaryEquivalence.equivalent(first, second, sentences))
    }

    @Test
    fun embeddingDetectsInjectiveSignaturePreservation() {
        val succ = FunctionSymbol("succ", 1)
        val signature = Signature(functions = setOf(succ))

        val source = Structure(
            signature = signature,
            universe = setOf(0, 1),
            constantInterpretation = emptyMap(),
            functionInterpretation = mapOf(succ to { args: List<Int> -> if (args[0] == 0) 1 else 1 }),
            relationInterpretation = emptyMap()
        )

        val target = Structure(
            signature = signature,
            universe = setOf(10, 11, 12),
            constantInterpretation = emptyMap(),
            functionInterpretation = mapOf(succ to { args: List<Int> -> if (args[0] == 10) 11 else 11 }),
            relationInterpretation = emptyMap()
        )

        val embedding = Embedding(source, target) { x -> if (x == 0) 10 else 11 }

        assertTrue(embedding.isInjective())
        assertTrue(embedding.preservesSignature())

        val nonInjective = Embedding(source, target) { 10 }
        assertFalse(nonInjective.isInjective())
    }
}
