package mathsets.typetheory

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TypeTheoryTest {

    private val checker = TypeChecker()

    @Test
    fun inferIdentityOnNat() {
        val idNat = Term.Lambda("x", Type.Nat, Term.Var("x"))
        val inferred = checker.infer(Context(), idNat)

        assertEquals(Type.Pi(Type.Nat, Type.Nat), inferred)
    }

    @Test
    fun betaReductionWorks() {
        val idNat = Term.Lambda("x", Type.Nat, Term.Var("x"))
        val application = Term.App(idNat, Term.Succ(Term.Zero))

        val reduced = Evaluator.normalize(application)
        assertEquals(Term.Succ(Term.Zero), reduced)
    }

    @Test
    fun natRecComputesSuccessorChain() {
        val succCase = Term.Lambda(
            "n",
            Type.Nat,
            Term.Lambda("acc", Type.Nat, Term.Succ(Term.Var("acc")))
        )

        val one = Term.Succ(Term.Zero)
        val two = Term.Succ(one)

        val rec = Term.NatRec(two, Term.Zero, succCase)
        val normalized = Evaluator.normalize(rec)

        assertEquals(two, normalized)
    }

    @Test
    fun curryHowardIdentityProofChecks() {
        val proof = CurryHoward.identityProof(Type.Bool)
        val proposition = CurryHoward.implicationType(Type.Bool, Type.Bool)

        assertTrue(CurryHoward.isProof(checker, Context(), proof, proposition))
    }
}
