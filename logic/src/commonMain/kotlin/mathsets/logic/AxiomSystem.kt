package mathsets.logic

/**
 * Pre-defined collections of axioms representing standard axiomatic set theories.
 *
 * Each property provides an ordered list of [Axiom] instances that together
 * constitute a named axiomatic system. These lists can be used with
 * [ModelChecker] to verify that an [Interpretation] satisfies (or violates)
 * each axiom.
 *
 * @see Axiom
 * @see ModelChecker
 */
object AxiomSystem {
    /**
     * The axioms of **Zermelo-Fraenkel set theory with the Axiom of Choice** (ZFC).
     *
     * Contains all ten standard ZFC axioms:
     * Extensionality, Empty Set, Pairing, Union, Power Set, Infinity,
     * Separation, Replacement, Choice, and Foundation.
     */
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

    /**
     * The axioms of **Zermelo-Fraenkel set theory** (ZF) — ZFC without the
     * Axiom of Choice.
     */
    val ZF: List<Axiom> = ZFC.filterNot { it == Axiom.Choice }

    /**
     * The axioms of **von Neumann-Bernays-Gödel** (NBG) set theory.
     *
     * Currently mapped to the same axiom list as [ZFC]; class-theoretic
     * extensions are planned for a future layer.
     */
    val NBG: List<Axiom> = ZFC

    /**
     * The axioms of **Kelley-Morse** (KM) set theory.
     *
     * Currently mapped to the same axiom list as [ZFC]; class-comprehension
     * extensions are planned for a future layer.
     */
    val KM: List<Axiom> = ZFC
}
