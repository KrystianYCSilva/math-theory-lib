package mathsets.logic

sealed interface Axiom {
    val name: String
    val formula: Formula

    data object Extensionality : Axiom {
        override val name: String = "Extensionality"
        override val formula: Formula =
            forAll("A") {
                forAll("B") {
                    ("A" eq "B")
                }
            }
    }

    data object EmptySet : Axiom {
        override val name: String = "EmptySet"
        override val formula: Formula = exists("E") { not("x" memberOf "E") }
    }

    data object Pairing : Axiom {
        override val name: String = "Pairing"
        override val formula: Formula = exists("P") { "a" memberOf "P" or ("b" memberOf "P") }
    }

    data object Union : Axiom {
        override val name: String = "Union"
        override val formula: Formula = exists("U") { "x" memberOf "U" }
    }

    data object PowerSet : Axiom {
        override val name: String = "PowerSet"
        override val formula: Formula = exists("P") { "X" memberOf "P" }
    }

    data object Infinity : Axiom {
        override val name: String = "Infinity"
        override val formula: Formula = exists("I") { "x" memberOf "I" }
    }

    data object Separation : Axiom {
        override val name: String = "Separation"
        override val formula: Formula = exists("B") { "x" memberOf "B" }
    }

    data object Replacement : Axiom {
        override val name: String = "Replacement"
        override val formula: Formula = exists("R") { "x" memberOf "R" }
    }

    data object Choice : Axiom {
        override val name: String = "Choice"
        override val formula: Formula = exists("f") { "x" memberOf "f" }
    }

    data object Foundation : Axiom {
        override val name: String = "Foundation"
        override val formula: Formula = forAll("A") { "A" eq "A" }
    }
}

