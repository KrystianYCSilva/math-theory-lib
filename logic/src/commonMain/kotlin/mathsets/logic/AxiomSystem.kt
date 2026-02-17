package mathsets.logic

object AxiomSystem {
    val ZFC: List<Axiom> = listOf(
        Axiom.Extensionality,
        Axiom.EmptySet,
        Axiom.Pairing,
        Axiom.Union,
        Axiom.PowerSet,
        Axiom.Infinity,
        Axiom.Separation,
        Axiom.Replacement,
        Axiom.Choice,
        Axiom.Foundation
    )

    val ZF: List<Axiom> = ZFC.filterNot { it == Axiom.Choice }
    val NBG: List<Axiom> = ZFC
    val KM: List<Axiom> = ZFC
}

