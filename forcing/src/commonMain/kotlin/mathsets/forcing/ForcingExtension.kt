package mathsets.forcing

/**
 * Represents a forcing name: an element of the ground model paired with supporting conditions
 * from the generic filter.
 *
 * In forcing, names are used to refer to elements of the extension model M[G].
 * Each name pairs a ground-model element with the set of conditions that "support" it.
 *
 * @param M The type of ground model elements.
 * @param C The type of forcing conditions.
 * @property groundElement The element from the ground model.
 * @property support The set of conditions supporting this name.
 */
data class ForcedName<M, C>(
    val groundElement: M,
    val support: Set<C>
)

/**
 * Simulates a finite forcing extension M -> M[G].
 *
 * Given a ground model (a finite set of elements) and a generic filter, this class
 * constructs the extension universe by pairing each ground-model element with the
 * filter's conditions as support.
 *
 * @param M The type of ground model elements.
 * @param C The type of forcing conditions.
 * @property groundModel The finite set of elements in the ground model.
 * @property genericFilter The generic filter used to build the extension.
 */
class ForcingExtension<M, C>(
    private val groundModel: Set<M>,
    private val genericFilter: GenericFilter<C>
) {
    /**
     * Constructs the extension universe M[G] by interpreting each ground-model element
     * as a forced name supported by the entire generic filter.
     *
     * @return The set of [ForcedName]s forming the extension model.
     */
    fun extensionUniverse(): Set<ForcedName<M, C>> =
        groundModel.mapTo(linkedSetOf()) { element ->
            ForcedName(element, genericFilter.conditions)
        }

    /**
     * Interprets a single ground-model element as a forced name in the extension.
     *
     * @param element The ground-model element to interpret.
     * @return The corresponding [ForcedName] in M[G].
     * @throws IllegalArgumentException if [element] is not in the ground model.
     */
    fun interpret(element: M): ForcedName<M, C> {
        require(element in groundModel) { "Element must belong to the ground model." }
        return ForcedName(element, genericFilter.conditions)
    }
}
