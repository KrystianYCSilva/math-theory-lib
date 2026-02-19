package mathsets.modeltheory

import mathsets.logic.Formula
import mathsets.logic.Term

/**
 * Function symbol with fixed arity.
 *
 * @property name Symbol name.
 * @property arity Arity.
 */
data class FunctionSymbol(val name: String, val arity: Int)

/**
 * Relation symbol with fixed arity.
 *
 * @property name Symbol name.
 * @property arity Arity.
 */
data class RelationSymbol(val name: String, val arity: Int)

/**
 * Constant symbol.
 *
 * @property name Symbol name.
 */
data class ConstantSymbol(val name: String)

/**
 * First-order signature.
 *
 * @property functions Function symbols.
 * @property relations Relation symbols.
 * @property constants Constant symbols.
 */
data class Signature(
    val functions: Set<FunctionSymbol> = emptySet(),
    val relations: Set<RelationSymbol> = emptySet(),
    val constants: Set<ConstantSymbol> = emptySet()
)

/**
 * First-order structure over a finite universe.
 *
 * @param T Universe element type.
 * @property signature Language signature.
 * @property universe Carrier set.
 * @property constantInterpretation Constant interpretations.
 * @property functionInterpretation Function interpretations.
 * @property relationInterpretation Relation interpretations.
 * @throws IllegalArgumentException when signature symbols are missing interpretations.
 */
class Structure<T>(
    val signature: Signature,
    val universe: Set<T>,
    val constantInterpretation: Map<ConstantSymbol, T>,
    val functionInterpretation: Map<FunctionSymbol, (List<T>) -> T>,
    val relationInterpretation: Map<RelationSymbol, (List<T>) -> Boolean>
) {
    init {
        require(signature.constants.all { it in constantInterpretation }) {
            "All constant symbols must be interpreted."
        }
        require(signature.functions.all { it in functionInterpretation }) {
            "All function symbols must be interpreted."
        }
        require(signature.relations.all { it in relationInterpretation }) {
            "All relation symbols must be interpreted."
        }
    }

    /**
     * Resolves a function symbol by name/arity.
     *
     * @param name Symbol name.
     * @param arity Symbol arity.
     * @return Matching symbol or null.
     */
    fun functionSymbol(name: String, arity: Int): FunctionSymbol? =
        signature.functions.firstOrNull { it.name == name && it.arity == arity }

    /**
     * Resolves a relation symbol by name/arity.
     *
     * @param name Symbol name.
     * @param arity Symbol arity.
     * @return Matching symbol or null.
     */
    fun relationSymbol(name: String, arity: Int): RelationSymbol? =
        signature.relations.firstOrNull { it.name == name && it.arity == arity }
}

/**
 * Satisfaction relation evaluator M |= phi for finite structures.
 *
 * @param T Universe element type.
 * @property structure Evaluated structure.
 */
class Satisfaction<T>(val structure: Structure<T>) {
    /**
     * Evaluates a formula under an assignment.
     *
     * @param formula Formula to evaluate.
     * @param assignment Variable assignment.
     * @return True when formula is satisfied.
     * @throws IllegalStateException when a symbol/variable is unbound.
     */
    fun satisfies(formula: Formula, assignment: Map<String, T> = emptyMap()): Boolean = when (formula) {
        is Formula.Membership -> {
            val relation = structure.relationSymbol("in", 2)
                ?: error("Relation symbol 'in/2' is not interpreted.")
            val left = evaluateTerm(formula.element, assignment)
            val right = evaluateTerm(formula.set, assignment)
            structure.relationInterpretation.getValue(relation)(listOf(left, right))
        }
        is Formula.Equals -> evaluateTerm(formula.left, assignment) == evaluateTerm(formula.right, assignment)
        is Formula.Not -> !satisfies(formula.inner, assignment)
        is Formula.And -> satisfies(formula.left, assignment) && satisfies(formula.right, assignment)
        is Formula.Or -> satisfies(formula.left, assignment) || satisfies(formula.right, assignment)
        is Formula.Implies -> !satisfies(formula.premise, assignment) || satisfies(formula.conclusion, assignment)
        is Formula.Iff -> satisfies(formula.left, assignment) == satisfies(formula.right, assignment)
        is Formula.ForAll -> structure.universe.all { v -> satisfies(formula.body, assignment + (formula.variable to v)) }
        is Formula.Exists -> structure.universe.any { v -> satisfies(formula.body, assignment + (formula.variable to v)) }
    }

    private fun evaluateTerm(term: Term, assignment: Map<String, T>): T = when (term) {
        is Term.Var -> assignment[term.name] ?: error("Unbound variable '${term.name}'.")
        is Term.Const -> {
            val symbol = ConstantSymbol(term.value)
            structure.constantInterpretation[symbol] ?: error("Unknown constant '${term.value}'.")
        }
        is Term.App -> {
            val args = term.args.map { evaluateTerm(it, assignment) }
            val symbol = structure.functionSymbol(term.function, args.size)
                ?: error("Unknown function '${term.function}/${args.size}'.")
            structure.functionInterpretation.getValue(symbol)(args)
        }
    }
}

/**
 * Elementary-equivalence checker over a sampled sentence set.
 */
object ElementaryEquivalence {
    /**
     * Checks if two structures satisfy the same sampled sentences.
     *
     * @param T Universe element type.
     * @param first First structure.
     * @param second Second structure.
     * @param sentences Sentence sample (closed formulas).
     * @return True when both structures agree on all sampled sentences.
     */
    fun <T> equivalent(
        first: Structure<T>,
        second: Structure<T>,
        sentences: Set<Formula>
    ): Boolean {
        val satFirst = Satisfaction(first)
        val satSecond = Satisfaction(second)
        return sentences.all { sentence -> satFirst.satisfies(sentence) == satSecond.satisfies(sentence) }
    }
}

/**
 * Finite embedding checker between structures of the same signature.
 *
 * @param S Source element type.
 * @param T Target element type.
 * @property source Source structure.
 * @property target Target structure.
 * @property map Embedding candidate map.
 */
class Embedding<S, T>(
    val source: Structure<S>,
    val target: Structure<T>,
    val map: (S) -> T
) {
    /**
     * Checks injectivity on source universe.
     *
     * @return True when map is injective on finite source carrier.
     */
    fun isInjective(): Boolean =
        source.universe.map(map).toSet().size == source.universe.size

    /**
     * Checks function and relation preservation on finite carriers.
     *
     * @return True when the map is a homomorphism of structures.
     */
    fun preservesSignature(): Boolean {
        val functionOk = source.signature.functions.all { symbol ->
            val sourceFn = source.functionInterpretation.getValue(symbol)
            val targetFn = target.functionInterpretation.getValue(symbol)
            tuples(source.universe.toList(), symbol.arity).all { tuple ->
                val left = map(sourceFn(tuple))
                val right = targetFn(tuple.map(map))
                left == right
            }
        }

        val relationOk = source.signature.relations.all { symbol ->
            val sourceRel = source.relationInterpretation.getValue(symbol)
            val targetRel = target.relationInterpretation.getValue(symbol)
            tuples(source.universe.toList(), symbol.arity).all { tuple ->
                sourceRel(tuple) == targetRel(tuple.map(map))
            }
        }

        return functionOk && relationOk
    }

    private fun <X> tuples(elements: List<X>, arity: Int): List<List<X>> {
        if (arity == 0) return listOf(emptyList())
        var result = listOf(emptyList<X>())
        repeat(arity) {
            result = result.flatMap { prefix -> elements.map { prefix + it } }
        }
        return result
    }
}
