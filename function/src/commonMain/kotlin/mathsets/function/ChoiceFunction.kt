package mathsets.function

import mathsets.kernel.Cardinality
import mathsets.set.ExtensionalSet
import mathsets.set.MathSet

/**
 * Implements a **choice function** for a family of non-empty sets.
 *
 * Given a family of sets F = {S₁, S₂, …}, a choice function selects exactly one
 * element from each member set. The existence of such a function for arbitrary
 * families is guaranteed by the **Axiom of Choice** (AC).
 *
 * This implementation works with finite families and selects the first available
 * element from each set.
 *
 * @param A the element type of the sets in the family
 * @property family a [MathSet] of non-empty [MathSet]s to choose from
 */
class ChoiceFunction<A>(private val family: MathSet<MathSet<A>>) {
    /**
     * Performs the choice, returning a map from each set in the family to a chosen element.
     *
     * @return a [Map] associating each member set with a selected element
     * @throws IllegalArgumentException if the family is not finite or contains an empty set
     */
    fun choose(): Map<MathSet<A>, A> {
        require(family.cardinality is Cardinality.Finite) {
            "ChoiceFunction currently requires a finite family."
        }
        val result = linkedMapOf<MathSet<A>, A>()
        for (subset in family.elements()) {
            val chosen = subset.elements().firstOrNull()
                ?: throw IllegalArgumentException("Family must contain only non-empty sets.")
            result[subset.materialize()] = chosen
        }
        return result
    }

    /**
     * Wraps the choice as a [MathFunction] from the family of sets to the chosen elements.
     *
     * The resulting function maps each set S in the family to the element chosen
     * from S by [choose].
     *
     * @return a [MathFunction] representing the choice function
     */
    fun asFunction(): MathFunction<MathSet<A>, A> {
        val choices = choose()
        val domain = ExtensionalSet(choices.keys)
        val codomain = ExtensionalSet(choices.values.toSet())
        return MathFunction(domain, codomain) { set ->
            choices[set.materialize()]
                ?: throw IllegalArgumentException("No chosen value for subset $set")
        }
    }
}
