package mathsets.logic

/**
 * A sealed hierarchy representing axioms of set theory as named [Formula] instances.
 *
 * Each variant encodes one axiom of Zermelo-Fraenkel set theory (with Choice).
 * The [formula] property holds a simplified logical representation of the axiom;
 * these are intentionally compact stubs suitable for structural tagging and
 * model-checking rather than full formal statements.
 *
 * @property name A human-readable name for the axiom (e.g., `"Extensionality"`).
 * @property formula The logical formula encoding (or approximating) the axiom.
 * @see AxiomSystem
 */
sealed interface Axiom {
    val name: String
    val formula: Formula

    /**
     * **Axiom of Extensionality**: Two sets are equal if and only if they have the
     * same members.
     *
     * Simplified encoding: `∀A∀B(A = B)`.
     */
    data object Extensionality : Axiom {
        override val name: String = "Extensionality"
        override val formula: Formula =
            forAll("A") {
                forAll("B") {
                    ("A" eq "B")
                }
            }
    }

    /**
     * **Axiom of the Empty Set**: There exists a set with no members.
     *
     * Simplified encoding: `∃E(¬(x ∈ E))`.
     */
    data object EmptySet : Axiom {
        override val name: String = "EmptySet"
        override val formula: Formula = exists("E") { not("x" memberOf "E") }
    }

    /**
     * **Axiom of Pairing**: For any two sets, there exists a set containing both.
     *
     * Simplified encoding: `∃P(a ∈ P ∨ b ∈ P)`.
     */
    data object Pairing : Axiom {
        override val name: String = "Pairing"
        override val formula: Formula = exists("P") { "a" memberOf "P" or ("b" memberOf "P") }
    }

    /**
     * **Axiom of Union**: For any set of sets, there exists a union set.
     *
     * Simplified encoding: `∃U(x ∈ U)`.
     */
    data object Union : Axiom {
        override val name: String = "Union"
        override val formula: Formula = exists("U") { "x" memberOf "U" }
    }

    /**
     * **Axiom of Power Set**: For any set, there exists a set of all its subsets.
     *
     * Simplified encoding: `∃P(X ∈ P)`.
     */
    data object PowerSet : Axiom {
        override val name: String = "PowerSet"
        override val formula: Formula = exists("P") { "X" memberOf "P" }
    }

    /**
     * **Axiom of Infinity**: There exists an infinite set (containing the natural numbers).
     *
     * Simplified encoding: `∃I(x ∈ I)`.
     */
    data object Infinity : Axiom {
        override val name: String = "Infinity"
        override val formula: Formula = exists("I") { "x" memberOf "I" }
    }

    /**
     * **Axiom Schema of Separation** (Comprehension): For any set and predicate,
     * there exists a subset satisfying the predicate.
     *
     * Simplified encoding: `∃B(x ∈ B)`.
     */
    data object Separation : Axiom {
        override val name: String = "Separation"
        override val formula: Formula = exists("B") { "x" memberOf "B" }
    }

    /**
     * **Axiom Schema of Replacement**: The image of a set under a definable function
     * is also a set.
     *
     * Simplified encoding: `∃R(x ∈ R)`.
     */
    data object Replacement : Axiom {
        override val name: String = "Replacement"
        override val formula: Formula = exists("R") { "x" memberOf "R" }
    }

    /**
     * **Axiom of Choice**: Every collection of non-empty sets admits a choice function.
     *
     * Simplified encoding: `∃f(x ∈ f)`.
     */
    data object Choice : Axiom {
        override val name: String = "Choice"
        override val formula: Formula = exists("f") { "x" memberOf "f" }
    }

    /**
     * **Axiom of Foundation** (Regularity): Every non-empty set contains a member
     * disjoint from itself, preventing infinite descending membership chains.
     *
     * Simplified encoding: `∀A(A = A)`.
     */
    data object Foundation : Axiom {
        override val name: String = "Foundation"
        override val formula: Formula = forAll("A") { "A" eq "A" }
    }
}
