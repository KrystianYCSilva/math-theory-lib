package mathsets.typetheory

/**
 * Types in a small Martin-Lof type theory subset.
 */
sealed interface Type {
    /**
     * Universe type U_level.
     *
     * @property level Universe level.
     */
    data class Universe(val level: Int) : Type

    /**
     * Dependent function type Pi(x : domain). codomain.
     *
     * In this minimal implementation, codomain is represented directly as a [Type].
     *
     * @property domain Domain type.
     * @property codomain Codomain type.
     */
    data class Pi(val domain: Type, val codomain: Type) : Type

    /**
     * Dependent pair type Sigma(x : base). fiber.
     *
     * In this minimal implementation, fiber is represented directly as a [Type].
     *
     * @property base Base type.
     * @property fiber Fiber type.
     */
    data class Sigma(val base: Type, val fiber: Type) : Type

    /**
     * Identity type Id(type, left, right).
     *
     * @property type Carrier type.
     * @property left Left term.
     * @property right Right term.
     */
    data class Id(val type: Type, val left: Term, val right: Term) : Type

    /**
     * Natural number type.
     */
    data object Nat : Type

    /**
     * Boolean type.
     */
    data object Bool : Type

    /**
     * Empty type.
     */
    data object Empty : Type

    /**
     * Unit type.
     */
    data object Unit : Type
}

/**
 * Terms in a small Martin-Lof type theory subset.
 */
sealed interface Term {
    /**
     * Variable term.
     *
     * @property name Variable identifier.
     */
    data class Var(val name: String) : Term

    /**
     * Lambda abstraction.
     *
     * @property parameter Bound parameter name.
     * @property parameterType Parameter type.
     * @property body Lambda body.
     */
    data class Lambda(val parameter: String, val parameterType: Type, val body: Term) : Term

    /**
     * Application term.
     *
     * @property function Function term.
     * @property argument Argument term.
     */
    data class App(val function: Term, val argument: Term) : Term

    /**
     * Pair constructor.
     *
     * @property first First component.
     * @property second Second component.
     */
    data class Pair(val first: Term, val second: Term) : Term

    /**
     * First projection.
     *
     * @property value Pair term.
     */
    data class Proj1(val value: Term) : Term

    /**
     * Second projection.
     *
     * @property value Pair term.
     */
    data class Proj2(val value: Term) : Term

    /**
     * Reflexivity witness.
     *
     * @property value Term proving value = value.
     */
    data class Refl(val value: Term) : Term

    /**
     * Natural number zero.
     */
    data object Zero : Term

    /**
     * Natural number successor.
     *
     * @property predecessor Previous natural number.
     */
    data class Succ(val predecessor: Term) : Term

    /**
     * Primitive recursion on naturals.
     *
     * @property target Target natural number.
     * @property zeroCase Value for zero.
     * @property succCase Step function of type Pi(Nat, Pi(T, T)).
     */
    data class NatRec(val target: Term, val zeroCase: Term, val succCase: Term) : Term

    /**
     * Unit inhabitant.
     */
    data object UnitValue : Term

    /**
     * Boolean true.
     */
    data object True : Term

    /**
     * Boolean false.
     */
    data object False : Term
}

/**
 * Typing context for variables.
 *
 * @property bindings Variable-to-type map.
 */
data class Context(val bindings: Map<String, Type> = emptyMap()) {
    /**
     * Looks up the type of a variable.
     *
     * @param name Variable name.
     * @return Variable type when bound, or null.
     */
    fun lookup(name: String): Type? = bindings[name]

    /**
     * Extends this context with a fresh binding.
     *
     * @param name Variable name.
     * @param type Bound type.
     * @return Extended context.
     */
    fun withBinding(name: String, type: Type): Context = Context(bindings + (name to type))
}
