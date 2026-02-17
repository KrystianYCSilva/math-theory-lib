package mathsets.function

import mathsets.kernel.Cardinality
import mathsets.set.ExtensionalSet
import mathsets.set.MathSet

class ChoiceFunction<A>(private val family: MathSet<MathSet<A>>) {
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

