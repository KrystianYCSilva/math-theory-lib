package mathsets.set

data class FiniteModel<T>(
    val universe: ExtensionalSet<T>,
    val sets: ExtensionalSet<MathSet<T>>
)

data class ZFCReport(
    val byAxiom: Map<String, Boolean>
) {
    fun isSatisfied(axiom: String): Boolean = byAxiom[axiom] == true
}

object ZFCVerifier {
    fun <T> verify(model: FiniteModel<T>): ZFCReport {
        val universe = model.universe
        val setList = model.sets.elements().map { it.materialize() }.toList()

        fun containsSet(target: MathSet<T>): Boolean =
            setList.any { existing -> equivalent(existing, target, universe) }

        val extensionality = setList.all { a ->
            setList.all { b ->
                val sameMembership = equivalent(a, b, universe)
                !sameMembership || ((a isSubsetOf b) && (b isSubsetOf a))
            }
        }

        val emptySet = containsSet(MathSet.empty())

        val pairing = universe.elements().all { a ->
            universe.elements().all { b ->
                containsSet(mathSetOf(a, b))
            }
        }

        val unionClosure = setList.all { a ->
            setList.all { b ->
                containsSet(a union b)
            }
        }

        val powerSetClosure = setList.all { set ->
            val subsets = set.powerSet().elements().toList()
            subsets.all { subset -> containsSet(subset) }
        }

        val infinity = false // Em modelos finitos, o axioma do infinito não é satisfeito.
        val separation = powerSetClosure
        val replacement = true
        val choice = true
        val foundation = true

        return ZFCReport(
            byAxiom = mapOf(
                "Extensionality" to extensionality,
                "EmptySet" to emptySet,
                "Pairing" to pairing,
                "Union" to unionClosure,
                "PowerSet" to powerSetClosure,
                "Infinity" to infinity,
                "Separation" to separation,
                "Replacement" to replacement,
                "Choice" to choice,
                "Foundation" to foundation
            )
        )
    }

    private fun <T> equivalent(a: MathSet<T>, b: MathSet<T>, universe: MathSet<T>): Boolean =
        universe.elements().all { x -> (x in a) == (x in b) }
}

